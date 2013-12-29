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

import griffon.core.ResourceHandler;
import griffon.transform.ResourcesAware;
import org.codehaus.griffon.runtime.core.ResourceLocator;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.LIST_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE;

/**
 * Handles generation of code for the {@code @ResourcesAware} annotation.
 * <p/>
 * Generally, it adds (if needed) a ResourceLocator field and
 * the required methods from {@code ResourceHandler}.
 * <p/>
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ResourcesAwareASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesAwareASTTransformation.class);
    private static final ClassNode RESOURCE_HANDLER_TYPE = makeClassSafe(ResourceHandler.class);
    private static final ClassNode RESOURCES_AWARE_TYPE = makeClassSafe(ResourcesAware.class);
    private static final ClassNode RESOURCE_LOCATOR_TYPE = makeClassSafe(ResourceLocator.class);
    private static final ClassNode URL_TYPE = makeClassSafe(URL.class);
    private static final ClassNode INPUT_STREAM_TYPE = makeClassSafe(InputStream.class);

    private static final String NAME = "name";
    private static final String METHOD_GET_RESOURCE_AS_URL = "getResourceAsURL";
    private static final String METHOD_GET_RESOURCE_AS_STREAM = "getResourceAsStream";
    private static final String METHOD_GET_RESOURCES = "getResources";

    /**
     * Convenience method to see if an annotated node is {@code @ResourcesAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasResourcesAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (RESOURCES_AWARE_TYPE.equals(annotation.getClassNode())) {
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
        if (needsResourceLocator(classNode, source)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + ResourceLocator.class.getName() + " into " + classNode.getName());
            }
            apply(classNode);
        }
    }

    /**
     * Snoops through the declaring class and all parents looking for methods<ul>
     * <li>URL getResourceAsURL(String)</li>
     * <li>InputStream getResourceAsStream(String)</li>
     * <li>List&gt;URL&lt; getResources(String)</li>
     * </ul>If any are defined all
     * must be defined or a compilation error results.
     *
     * @param declaringClass the class to search
     * @param sourceUnit     the source unit, for error reporting. {@code @NotNull}.
     * @return true if property change support should be added
     */
    protected static boolean needsResourceLocator(ClassNode declaringClass, SourceUnit sourceUnit) {
        boolean found1 = false, found2 = false, found3 = false;
        ClassNode consideredClass = declaringClass;
        while (consideredClass != null) {
            for (MethodNode method : consideredClass.getMethods()) {
                // just check length, MOP will match it up
                found1 = method.getName().equals(METHOD_GET_RESOURCE_AS_URL) && method.getParameters().length == 1;
                found2 = method.getName().equals(METHOD_GET_RESOURCE_AS_STREAM) && method.getParameters().length == 1;
                found3 = method.getName().equals(METHOD_GET_RESOURCES) && method.getParameters().length == 1;
                if (found1 && found2 && found3) {
                    return false;
                }
            }
            consideredClass = consideredClass.getSuperClass();
        }
        if (found1 || found2 || found3) {
            sourceUnit.getErrorCollector().addErrorAndContinue(
                    new SimpleMessage("@ResourcesAware cannot be processed on "
                            + declaringClass.getName()
                            + " because some but not all of getResourceAsURL, getResourceAsStream, and getResources were declared in the current class or super classes.",
                            sourceUnit)
            );
            return false;
        }
        return true;
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     * <p/>
     * Adds a new field:
     * <code>protected final org.codehaus.griffon.runtime.core.ResourceLocator this$resourceLocator = new org.codehaus.griffon.runtime.core.ResourceLocator()</code>
     * <p/>
     * Also adds support methods:
     * <code>public URL getResourceAsURL(String)</code><br/>
     * <code>public InputStream getResourceAsStream(String)</code><br/>
     * <code>public List&gt;URL&lt; getResources(String)</code><br/>
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(ClassNode declaringClass) {
        injectInterface(declaringClass, RESOURCE_HANDLER_TYPE);

        // add field:
        // protected final ResourceLocator this$resourceLocator = new org.codehaus.griffon.runtime.core.ResourceLocator()
        FieldNode rlField = declaringClass.addField(
                "this$resourceLocator",
                ACC_FINAL | ACC_PRIVATE | ACC_SYNTHETIC,
                RESOURCE_LOCATOR_TYPE,
                ctor(RESOURCE_LOCATOR_TYPE, NO_ARGS));

        // add method:
        // URL getResourceAsURL(String name) {
        //     return this$resourceLocator.getResourceAsURL(name)
        // }
        injectMethod(declaringClass, new MethodNode(
                METHOD_GET_RESOURCE_AS_URL,
                ACC_PUBLIC,
                makeClassSafe(URL_TYPE),
                params(param(STRING_TYPE, NAME)),
                NO_EXCEPTIONS,
                returns(call(
                        field(rlField),
                        METHOD_GET_RESOURCE_AS_URL,
                        vars(NAME)))
        ));

        // add method:
        // InputStream getResourceAsStream(String name) {
        //     return this$resourceLocator.getResourceAsStream(name)
        // }
        injectMethod(declaringClass, new MethodNode(
                METHOD_GET_RESOURCE_AS_STREAM,
                ACC_PUBLIC,
                makeClassSafe(INPUT_STREAM_TYPE),
                params(param(STRING_TYPE, NAME)),
                NO_EXCEPTIONS,
                returns(call(
                        field(rlField),
                        METHOD_GET_RESOURCE_AS_STREAM,
                        vars(NAME)))
        ));

        // add method:
        // List<URL> getResources(String name) {
        //     return this$resourceLocator.getResources(name)
        // }
        injectMethod(declaringClass, new MethodNode(
                METHOD_GET_RESOURCES,
                ACC_PUBLIC,
                makeClassSafe(LIST_TYPE),
                params(param(STRING_TYPE, NAME)),
                NO_EXCEPTIONS,
                returns(call(
                        field(rlField),
                        METHOD_GET_RESOURCES,
                        vars(NAME)))
        ));
    }
}
