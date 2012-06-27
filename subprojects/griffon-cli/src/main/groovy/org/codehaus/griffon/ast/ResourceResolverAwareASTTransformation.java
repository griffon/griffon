/*
 * Copyright 2009-2012 the original author or authors.
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

import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;
import griffon.transform.ResourceResolverAware;
import org.codehaus.griffon.runtime.core.ResourceLocator;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;

/**
 * Handles generation of code for the {@code @ResourceResolverAware} annotation.
 * <p/>
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ResourceResolverAwareASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceResolverAwareASTTransformation.class);
    private static final ClassNode RESOURCE_RESOLVER_TYPE = makeClassSafe(ResourceResolver.class);
    private static final ClassNode RESOURCE_RESOLVER_AWARE_TYPE = makeClassSafe(ResourceResolverAware.class);
    private static final ClassNode NO_SUCH_RESOURCE_EXCEPTION_TYPE = makeClassSafe(NoSuchResourceException.class);
    private static final ClassNode OBJECT_ARRAY_TYPE = makeClassSafe(Object[].class);
    private static final ClassNode LOCALE_TYPE = makeClassSafe(Locale.class);

    private static final String KEY = "key";
    private static final String ARGS = "args";
    private static final String LOCALE = "locale";
    private static final String DEFAULT_VALUE = "defaultValue";
    private static final String METHOD_RESOLVE_RESOURCE = "resolveResource";

    /**
     * Convenience method to see if an annotated node is {@code @ResourceResolverAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasResourceResolverAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (RESOURCE_RESOLVER_AWARE_TYPE.equals(annotation.getClassNode())) {
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
        addResourceLocatorIfNeeded(source, (ClassNode) nodes[1]);
    }

    public static void addResourceLocatorIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsResourceResolver(classNode, source)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + ResourceLocator.class.getName() + " into " + classNode.getName());
            }
            apply(classNode);
        }
    }

    /**
     * Snoops through the declaring class and all parents looking for methods<ul>
     * <li><code>public Object resolveResource(java.lang.String)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.Locale)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.lang.Object[])</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.lang.Object[], java.util.Locale)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.List)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.List, java.util.Locale)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.Map)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.Map, java.util.Locale)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.lang.Object)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.lang.Object, java.util.Locale)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.lang.Object[], java.lang.Object)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.lang.Object[], java.lang.Object, java.util.Locale)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.List, java.lang.Object)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.List, java.lang.Object, java.util.Locale)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.Map, java.lang.Object)</code></li>
     * <li><code>public Object resolveResource(java.lang.String, java.util.Map, java.lang.Object, java.util.Locale)</code></li>
     * </ul>If any are defined all
     * must be defined or a compilation error results.
     *
     * @param declaringClass the class to search
     * @param sourceUnit     the source unit, for error reporting. {@code @NotNull}.
     * @return true if property change support should be added
     */
    protected static boolean needsResourceResolver(ClassNode declaringClass, SourceUnit sourceUnit) {
        boolean found = false;
        ClassNode consideredClass = declaringClass;
        while (consideredClass != null) {
            for (MethodNode method : consideredClass.getMethods()) {
                // just check length, MOP will match it up
                found = method.getName().equals(METHOD_RESOLVE_RESOURCE) && method.getParameters().length == 1;
                found |= method.getName().equals(METHOD_RESOLVE_RESOURCE) && method.getParameters().length == 2;
                found |= method.getName().equals(METHOD_RESOLVE_RESOURCE) && method.getParameters().length == 3;
                found |= method.getName().equals(METHOD_RESOLVE_RESOURCE) && method.getParameters().length == 4;
                if (found) return false;
            }
            consideredClass = consideredClass.getSuperClass();
        }
        if (found) {
            sourceUnit.getErrorCollector().addErrorAndContinue(
                    new SimpleMessage("@ResourceResolverAware cannot be processed on "
                            + declaringClass.getName()
                            + " because some but not all of variants of getMessage() were declared in the current class or super classes.",
                            sourceUnit)
            );
            return false;
        }
        return true;
    }

    /**
     * Adds the necessary field and methods to support message resolution.
     * <p/>
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(ClassNode declaringClass) {
        injectInterface(declaringClass, RESOURCE_RESOLVER_TYPE);

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(param(STRING_TYPE, KEY)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(OBJECT_ARRAY_TYPE, ARGS)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(LOCALE_TYPE, LOCALE)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, LOCALE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(OBJECT_ARRAY_TYPE, ARGS),
                        param(LOCALE_TYPE, LOCALE)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, LOCALE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(LIST_TYPE), ARGS)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(LIST_TYPE), ARGS),
                        param(LOCALE_TYPE, LOCALE)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, LOCALE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(MAP_TYPE), ARGS)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(LOCALE_TYPE, LOCALE)),
                throwing(NO_SUCH_RESOURCE_EXCEPTION_TYPE),
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, LOCALE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(OBJECT_TYPE, DEFAULT_VALUE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, DEFAULT_VALUE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(OBJECT_ARRAY_TYPE, ARGS),
                        param(OBJECT_TYPE, DEFAULT_VALUE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, DEFAULT_VALUE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(OBJECT_TYPE, DEFAULT_VALUE),
                        param(LOCALE_TYPE, LOCALE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, DEFAULT_VALUE, LOCALE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(OBJECT_ARRAY_TYPE, ARGS),
                        param(OBJECT_TYPE, DEFAULT_VALUE),
                        param(LOCALE_TYPE, LOCALE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, DEFAULT_VALUE, LOCALE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(LIST_TYPE), ARGS),
                        param(OBJECT_TYPE, DEFAULT_VALUE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, DEFAULT_VALUE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(LIST_TYPE), ARGS),
                        param(OBJECT_TYPE, DEFAULT_VALUE),
                        param(LOCALE_TYPE, LOCALE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, DEFAULT_VALUE, LOCALE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(OBJECT_TYPE, DEFAULT_VALUE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, DEFAULT_VALUE)))
        ));

        injectMethod(declaringClass, new MethodNode(
                METHOD_RESOLVE_RESOURCE,
                ACC_PUBLIC,
                OBJECT_TYPE,
                params(
                        param(STRING_TYPE, KEY),
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(OBJECT_TYPE, DEFAULT_VALUE),
                        param(LOCALE_TYPE, LOCALE)),
                NO_EXCEPTIONS,
                returns(call(
                        applicationInstance(),
                        METHOD_RESOLVE_RESOURCE,
                        vars(KEY, ARGS, DEFAULT_VALUE, LOCALE)))
        ));
    }
}
