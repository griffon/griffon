/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.ast;

import griffon.core.Event;
import griffon.core.EventPublisher;
import griffon.core.EventRouter;
import griffon.util.RunnableWithArgs;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;

/**
 * Handles generation of code for the {@code @EventPublisher} annotation.
 * <p/>
 * Generally, it adds (if needed) an EventRouter field and
 * the needed add/removeEventListener methods to support the
 * listeners.
 * <p/>
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class EventPublisherASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(EventPublisherASTTransformation.class);
    private static final ClassNode RUNNABLE_WITH_ARGS_TYPE = makeClassSafe(RunnableWithArgs.class);
    private static final ClassNode EVENT_HANDLER_TYPE = makeClassSafe(EventPublisher.class);
    private static final ClassNode EVENT_PUBLISHER_TYPE = makeClassSafe(griffon.transform.EventPublisher.class);
    private static final ClassNode EVENT_ROUTER_TYPE = makeClassSafe(EventRouter.class);
    private static final ClassNode EVENT_TYPE = makeClassSafe(Event.class);
    private static final ClassNode EVENT_CLASS_TYPE = makeClassSafe(Class.class);/*makeClassSafe0(
        makeClassSafe(Class.class),
        makeGenericsType(
            makeClassSafe(ClassHelper.OBJECT_TYPE),
            new ClassNode[]{makeClassSafe(Event.class)},
            null, true));*/
    private static final ClassNode GAH_TYPE = makeClassSafe(GriffonApplicationHelper.class);

    private static final String LISTENER = "listener";
    private static final String NAME = "name";
    private static final String ARGS = "args";
    private static final String ENABLED = "enabled";
    private static final String EVENT = "event";
    private static final String EVENT_CLASS = "eventClass";

    private static final String METHOD_ADD_EVENT_LISTENER = "addEventListener";
    private static final String METHOD_REMOVE_EVENT_LISTENER = "removeEventListener";
    private static final String METHOD_PUBLISH = "publish";
    private static final String METHOD_PUBLISH_OUTSIDE_UI = "publishOutsideUI";
    private static final String METHOD_PUBLISH_ASYNC = "publishAsync";
    private static final String METHOD_PUBLISH_EVENT = "publishEvent";
    private static final String METHOD_PUBLISH_EVENT_OUTSIDE_UI = "publishEventOutsideUI";
    private static final String METHOD_PUBLISH_EVENT_ASYNC = "publishEventAsync";
    private static final String METHOD_IS_EVENT_PUBLISHING_ENABLED = "isEventPublishingEnabled";
    private static final String METHOD_SET_EVENT_PUBLISHING_ENABLED = "setEventPublishingEnabled";

    /**
     * Convenience method to see if an annotated node is {@code @EventPublisher}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasEventPublisherAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (EVENT_PUBLISHER_TYPE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addEventRouterToClass(source, (ClassNode) nodes[1]);
    }

    public static void addEventRouterToClass(SourceUnit source, ClassNode classNode) {
        if (needsEventRouter(classNode, source)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + EventPublisher.class.getName() + " into " + classNode.getName());
            }
            addEventRouter(classNode);
        }
    }

    /**
     * Snoops through the declaring class and all parents looking for methods<ul>
     * <li>void addEventListener(Object)</li>
     * <li>void addEventListener(String, Closure)</li>
     * <li>void addEventListener(String, RunnableWithArgs)</li>
     * <li>void addEventListener(Event, Closure)</li>
     * <li>void addEventListener(Event, RunnableWithArgs)</li>
     * <li>void removeEventListener(Object)</li>
     * <li>void removeEventListener(String, Closure)</li>
     * <li>void removeEventListener(String, RunnableWithArgs)</li>
     * <li>void removeEventListener(Event, Closure)</li>
     * <li>void removeEventListener(Event, RunnableWithArgs)</li>
     * <li>void publishEvent(String, List = [])</li>
     * <li>void publishEventOutsideUI(String, List = [])</li>
     * <li>void publishEventAsync(String, List = [])</li>
     * <li>void publishEvent(Event)</li>
     * <li>void publishEventOutsideUI(Event)</li>
     * <li>void publishEventAsync(Event)</li>
     * <li>boolean isEventPublishingEnabled()</li>
     * <li>void setEventPublishingEnabled(boolean)</li>
     * </ul>If any are defined all
     * must be defined or a compilation error results.
     *
     * @param declaringClass the class to search
     * @param sourceUnit     the source unit, for error reporting. {@code @NotNull}.
     * @return true if property change support should be added
     */
    protected static boolean needsEventRouter(ClassNode declaringClass, SourceUnit sourceUnit) {
        boolean foundAdd = false, foundRemove = false, foundPublish = false, foundEnabled = false;
        ClassNode consideredClass = declaringClass;
        while (consideredClass != null) {
            for (MethodNode method : consideredClass.getMethods()) {
                // just check length, MOP will match it up
                foundAdd = foundAdd || method.getName().equals(METHOD_ADD_EVENT_LISTENER) && method.getParameters().length == 1;
                foundAdd = foundAdd || method.getName().equals(METHOD_ADD_EVENT_LISTENER) && method.getParameters().length == 2;
                foundRemove = foundRemove || method.getName().equals(METHOD_REMOVE_EVENT_LISTENER) && method.getParameters().length == 1;
                foundRemove = foundRemove || method.getName().equals(METHOD_REMOVE_EVENT_LISTENER) && method.getParameters().length == 2;
                foundPublish = foundPublish || method.getName().equals(METHOD_PUBLISH_EVENT) && method.getParameters().length == 1;
                foundPublish = foundPublish || method.getName().equals(METHOD_PUBLISH_EVENT) && method.getParameters().length == 2;
                foundPublish = foundPublish || method.getName().equals(METHOD_PUBLISH_EVENT_OUTSIDE_UI) && method.getParameters().length == 1;
                foundPublish = foundPublish || method.getName().equals(METHOD_PUBLISH_EVENT_OUTSIDE_UI) && method.getParameters().length == 2;
                foundPublish = foundPublish || method.getName().equals(METHOD_PUBLISH_EVENT_ASYNC) && method.getParameters().length == 1;
                foundPublish = foundPublish || method.getName().equals(METHOD_PUBLISH_EVENT_ASYNC) && method.getParameters().length == 2;
                foundEnabled = foundEnabled || method.getName().equals(METHOD_IS_EVENT_PUBLISHING_ENABLED) && method.getParameters().length == 0;
                foundEnabled = foundEnabled || method.getName().equals(METHOD_SET_EVENT_PUBLISHING_ENABLED) && method.getParameters().length == 1;
                if (foundAdd && foundRemove && foundPublish && foundEnabled) {
                    return false;
                }
            }
            consideredClass = consideredClass.getSuperClass();
        }
        if (foundAdd || foundRemove || foundPublish || foundEnabled) {
            sourceUnit.getErrorCollector().addErrorAndContinue(
                new SimpleMessage("@EventPublisher cannot be processed on "
                    + declaringClass.getName()
                    + " because some but not all of addEventListener, removeEventListener, publishEvent, publishEventAsync and publishEventOutsideUI were declared in the current class or super classes.",
                    sourceUnit)
            );
            return false;
        }
        return true;
    }

    /**
     * Adds the necessary field and methods to support event firing.
     * <p/>
     * Adds a new field:
     * <code>protected final org.codehaus.griffon.runtime.core.EventRouter this$eventRouter = new org.codehaus.griffon.runtime.core.EventRouter()</code>
     * <p/>
     * Also adds support methods:
     * <code>public void addEventListener(Object)</code><br/>
     * <code>public void addEventListener(String, Closure)</code><br/>
     * <code>public void addEventListener(String, RunnableWithArgs)</code><br/>
     * <code>public void addEventListener(Event, Closure)</code><br/>
     * <code>public void addEventListener(Event, RunnableWithArgs)</code><br/>
     * <code>public void removeEventListener(Object)</code><br/>
     * <code>public void removeEventListener(String, Closure)</code><br/>
     * <code>public void removeEventListener(String, RunnableWithArgs)</code><br/>
     * <code>public void removeEventListener(Event, Closure)</code><br/>
     * <code>public void removeEventListener(Event, RunnableWithArgs)</code><br/>
     * <code>public void publishEvent(String,List = [])</code><br/>
     * <code>public void publishEventOutsideUI(String,List = [])</code><br/>
     * <code>public void publishEventAsync(String,List = [])</code><br/>
     * <code>public void publishEvent(Event)</code><br/>
     * <code>public void publishEventOutsideUI(Event)</code><br/>
     * <code>public void publishEventAsync(Event)</code><br/>
     * <code>public boolean isEventPublishingEnabled()</code><br/>
     * <code>public void setEventPublishingEnabled(boolean)</code><br/>
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    protected static void addEventRouter(ClassNode declaringClass) {
        injectInterface(declaringClass, EVENT_HANDLER_TYPE);

        // add field:
        // protected final EventRouter this$eventRouter = GriffonApplicationHelper
        //                                                    .createEventRouter(ApplicationHolder.getApplication())
        FieldNode erField = declaringClass.addField(
            "this$eventRouter",
            ACC_FINAL | ACC_PRIVATE | ACC_SYNTHETIC,
            EVENT_ROUTER_TYPE,
            eventRouterInstance());

        // add method:
        // void addEventListener(listener) {
        //     this$eventRouter.addEventListener(listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_ADD_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(param(DYNAMIC_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_ADD_EVENT_LISTENER,
                vars(LISTENER)))
        ));

        // add method:
        // void addEventListener(String name, Closure listener) {
        //     this$eventRouter.addEventListener(name, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_ADD_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, NAME),
                param(makeClassSafe(CLOSURE_TYPE), LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_ADD_EVENT_LISTENER,
                vars(NAME, LISTENER)))
        ));

        // add method:
        // void addEventListener(String name, RunnableWithArgs listener) {
        //     this$eventRouter.addEventListener(name, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_ADD_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, NAME),
                param(RUNNABLE_WITH_ARGS_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_ADD_EVENT_LISTENER,
                vars(NAME, LISTENER)))
        ));

        // add method:
        // void addEventListener(Class<? extends Event> eventClass, Closure listener) {
        //     this$eventRouter.addEventListener(eventClass, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_ADD_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(makeClassSafe(CLOSURE_TYPE), LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_ADD_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // add method:
        // void addEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
        //     this$eventRouter.addEventListener(eventClass, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_ADD_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(RUNNABLE_WITH_ARGS_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_ADD_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // add method:
        // void removeEventListener(listener) {
        //    return this$eventRouter.removeEventListener(listener);
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_REMOVE_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(param(DYNAMIC_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_REMOVE_EVENT_LISTENER,
                vars(LISTENER)))
        ));

        // add method:
        // void removeEventListener(String name, Closure listener) {
        //    return this$eventRouter.removeEventListener(name, listener);
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_REMOVE_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, NAME),
                param(makeClassSafe(CLOSURE_TYPE), LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_REMOVE_EVENT_LISTENER,
                vars(NAME, LISTENER)))
        ));

        // add method:
        // void removeEventListener(String name, RunnableWithArgs listener) {
        //    return this$eventRouter.removeEventListener(name, listener);
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_REMOVE_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, NAME),
                param(RUNNABLE_WITH_ARGS_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_REMOVE_EVENT_LISTENER,
                vars(NAME, LISTENER)))
        ));

        // add method:
        // void removeEventListener(Class<? extends Event> eventClass, Closure listener) {
        //    return this$eventRouter.removeEventListener(eventClass, listener);
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_REMOVE_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(makeClassSafe(CLOSURE_TYPE), LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_REMOVE_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // add method:
        // void removeEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
        //    return this$eventRouter.removeEventListener(eventClass, listener);
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_REMOVE_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(RUNNABLE_WITH_ARGS_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_REMOVE_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // add method:
        // void publishEvent(String name, List args = []) {
        //     this$eventRouter.publishEvent(name, args)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_PUBLISH_EVENT,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, NAME),
                param(makeClassSafe(LIST_TYPE), ARGS, new ListExpression())),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_PUBLISH,
                vars(NAME, ARGS)))
        ));

        // add method:
        // void publishEventOutsideUI(String name, List args = []) {
        //     this$eventRouter.publishEventOutsideUI(name, args)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, NAME),
                param(makeClassSafe(LIST_TYPE), ARGS, new ListExpression())),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_PUBLISH_OUTSIDE_UI,
                vars(NAME, ARGS)))
        ));

        // add method:
        // void publishEventAsync(String name, List args = []) {
        //     this$eventRouter.publishEventAsync(name, args)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_PUBLISH_EVENT_ASYNC,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, NAME),
                param(makeClassSafe(LIST_TYPE), ARGS, new ListExpression())),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_PUBLISH_ASYNC,
                vars(NAME, ARGS)))
        ));

        // add method:
        // boolean isEventPublishingEnabled() {
        //      $this.eventRouter.isEnabled()
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_IS_EVENT_PUBLISHING_ENABLED,
            ACC_PUBLIC,
            boolean_TYPE,
            params(),
            NO_EXCEPTIONS,
            returns(call(
                field(erField),
                "isEnabled",
                NO_ARGS
            ))
        ));

        // add method:
        // void setEventPublishingEnabled(boolean enabled) {
        //      $this.eventRouter.setEnabled(enabled)
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_SET_EVENT_PUBLISHING_ENABLED,
            ACC_PUBLIC,
            VOID_TYPE,
            params(param(boolean_TYPE, ENABLED)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                "setEnabled",
                vars(ENABLED)
            ))
        ));

        // add method:
        // void addEventListener(Class<? extends Event> eventClass, Closure listener) {
        //     this$eventRouter.addEventListener(event, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_ADD_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(makeClassSafe(CLOSURE_TYPE), LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_ADD_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // add method:
        // void addEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
        //     this$eventRouter.addEventListener(event, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_ADD_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(RUNNABLE_WITH_ARGS_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_ADD_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // remove method:
        // void removeEventListener(Class<? extends Event> eventClass, Closure listener) {
        //     this$eventRouter.removeEventListener(event, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_REMOVE_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(makeClassSafe(CLOSURE_TYPE), LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_REMOVE_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // remove method:
        // void removeEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
        //     this$eventRouter.removeEventListener(event, listener)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_REMOVE_EVENT_LISTENER,
            ACC_PUBLIC,
            VOID_TYPE,
            params(
                param(EVENT_CLASS_TYPE, EVENT_CLASS),
                param(RUNNABLE_WITH_ARGS_TYPE, LISTENER)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_REMOVE_EVENT_LISTENER,
                vars(EVENT_CLASS, LISTENER)))
        ));

        // add method:
        // void publishEvent(Event event) {
        //     this$eventRouter.publishEvent(event)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_PUBLISH_EVENT,
            ACC_PUBLIC,
            VOID_TYPE,
            params(param(EVENT_TYPE, EVENT)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_PUBLISH,
                vars(EVENT)))
        ));

        // add method:
        // void publishEventOutsideUI(Event event) {
        //     this$eventRouter.publishEventOutsideUI(event)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            ACC_PUBLIC,
            VOID_TYPE,
            params(param(EVENT_TYPE, EVENT)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_PUBLISH_OUTSIDE_UI,
                vars(EVENT)))
        ));

        // add method:
        // void publishEventAsync(Event event) {
        //     this$eventRouter.publishEventAsync(event)
        //  }
        injectMethod(declaringClass, new MethodNode(
            METHOD_PUBLISH_EVENT_ASYNC,
            ACC_PUBLIC,
            VOID_TYPE,
            params(param(EVENT_TYPE, EVENT)),
            NO_EXCEPTIONS,
            stmnt(call(
                field(erField),
                METHOD_PUBLISH_ASYNC,
                vars(EVENT)))
        ));
    }

    public static Expression eventRouterInstance() {
        return call(GAH_TYPE, "createEventRouter", args(applicationInstance()));
    }
}
