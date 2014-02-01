/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.core.compile.ast.transform;

import griffon.core.GriffonApplication;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.core.compile.BaseConstants;
import org.codehaus.griffon.core.compile.ast.SourceUnitCollector;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.SourceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static java.lang.reflect.Modifier.PUBLIC;
import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.*;

/**
 * Handles generation of code for Griffon artifacts.
 * <p/>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class GriffonArtifactASTTransformation extends AbstractASTTransformation implements BaseConstants {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonArtifactASTTransformation.class);
    protected static final String ERROR_CLASS_NODE_NULL = "Argument 'classNode' cannot be null";
    protected static final String ERROR_SOURCE_NULL = "Argument 'source' cannot be null";
    private static final ClassNode GRIFFON_APPLICATION_TYPE = makeClassSafe(GriffonApplication.class);
    private static final ClassNode INJECT_TYPE = makeClassSafe(Inject.class);

    public void visit(ASTNode[] nodes, SourceUnit source) {
        ModuleNode moduleNode = (ModuleNode) nodes[0];
        ClassNode classNode = moduleNode.getClasses().get(0);
        if (classNode.isDerivedFrom(ClassHelper.SCRIPT_TYPE) && !allowsScriptAsArtifact() ||
            !matches(classNode, source)) {
            return;
        }
        transform(classNode);
    }

    protected boolean allowsScriptAsArtifact() {
        return false;
    }

    protected void transform(ClassNode classNode) {
        ClassNode superClass = classNode.getSuperClass();
        ClassNode superScriptClassNode = getSuperScriptClassNode(classNode);
        ClassNode superClassNode = getSuperClassNode(classNode);
        if (superScriptClassNode != null && allowsScriptAsArtifact() && classNode.isDerivedFrom(ClassHelper.SCRIPT_TYPE)) {
            LOG.debug("Setting {} as the superclass of {}", superScriptClassNode.getName(), classNode.getName());
            classNode.setSuperClass(superScriptClassNode);
            injectConstructor(classNode);
        } else if (superClassNode != null && ClassHelper.OBJECT_TYPE.equals(superClass)) {
            LOG.debug("Setting {} as the superclass of {}", superClassNode.getName(), classNode.getName());
            classNode.setSuperClass(superClassNode);
            injectConstructor(classNode);
        } else if (!classNode.implementsInterface(getInterfaceNode())) {
            inject(classNode, superClass);
        }
    }

    protected void injectConstructor(ClassNode classNode) {
        ConstructorNode constructor = classNode.addConstructor(
            PUBLIC,
            params(param(GRIFFON_APPLICATION_TYPE, APPLICATION)),
            NO_EXCEPTIONS,
            stmnt(new ConstructorCallExpression(ClassNode.SUPER, vars(APPLICATION)))
        );
        constructor.addAnnotation(new AnnotationNode(INJECT_TYPE));
    }

    protected void inject(ClassNode classNode, ClassNode superClass) {
        SourceUnit superSource = SourceUnitCollector.getInstance().getSourceUnit(superClass);
        if (matches(superClass, superSource)) return;

        if (superSource == null) {
            ClassNode interfaceNode = getInterfaceNode();
            LOG.debug("Injecting {} behavior to {}", interfaceNode.getName(), classNode.getName());
            // 1. add interface
            injectInterface(classNode, interfaceNode);
            // 2. add methods
            for (ASTInjector injector : getASTInjectors()) {
                injector.inject(classNode, getArtifactType());
            }
            postInject(classNode);
        } else {
            transform(superClass);
        }
    }

    protected abstract String getArtifactType();

    protected ClassNode getSuperScriptClassNode(ClassNode classNode) {
        return null;
    }

    protected ClassNode getSuperClassNode(ClassNode classNode) {
        return null;
    }

    protected abstract ClassNode getInterfaceNode();

    protected abstract boolean matches(ClassNode classNode, SourceUnit source);

    protected abstract ASTInjector[] getASTInjectors();

    protected void postInject(ClassNode classNode) {

    }

    public static boolean isOrImplements(ClassNode fieldType, ClassNode interfaceType) {
        return fieldType.equals(interfaceType) || fieldType.implementsInterface(interfaceType);
    }

    protected static boolean isArtifact(@Nonnull ClassNode classNode, @Nonnull SourceUnit source, @Nonnull ClassNode artifactType) {
        requireNonNull(classNode, ERROR_CLASS_NODE_NULL);
        requireNonNull(source, ERROR_SOURCE_NULL);
        List<AnnotationNode> annotations = classNode.getAnnotations(makeClassSafe(ArtifactProviderFor.class));
        if (annotations == null || annotations.isEmpty() || annotations.size() != 1) {
            return false;
        }
        AnnotationNode artifact = annotations.get(0);
        Expression value = artifact.getMember("value");
        return value instanceof ClassExpression && value.getType().equals(artifactType);
    }
}
