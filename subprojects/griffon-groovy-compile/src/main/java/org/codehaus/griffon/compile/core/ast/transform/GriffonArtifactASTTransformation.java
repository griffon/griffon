/*
 * Copyright 2008-2016 the original author or authors.
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
package org.codehaus.griffon.compile.core.ast.transform;

import griffon.core.GriffonApplication;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.compile.core.BaseConstants;
import org.codehaus.griffon.compile.core.ast.SourceUnitCollector;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.SourceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for Griffon artifacts.
 * <p>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class GriffonArtifactASTTransformation extends AbstractASTTransformation implements BaseConstants {
    protected static final String ERROR_CLASS_NODE_NULL = "Argument 'classNode' must not be null";
    protected static final String ERROR_SOURCE_NULL = "Argument 'source' must not be null";
    private static final Logger LOG = LoggerFactory.getLogger(GriffonArtifactASTTransformation.class);
    private static final ClassNode GRIFFON_APPLICATION_TYPE = makeClassSafe(GriffonApplication.class);
    private static final ClassNode INJECT_TYPE = makeClassSafe(Inject.class);

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
        } else if (superClassNode != null && ClassHelper.OBJECT_TYPE.equals(superClass)) {
            LOG.debug("Setting {} as the superclass of {}", superClassNode.getName(), classNode.getName());
            classNode.setSuperClass(superClassNode);
        } else if (!classNode.implementsInterface(getInterfaceNode())) {
            inject(classNode, superClass);
        }
    }

    protected void inject(ClassNode classNode, ClassNode superClass) {
        SourceUnit superSource = SourceUnitCollector.getInstance().getSourceUnit(superClass);

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
        } else if (!matches(superClass, superSource)) {
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
}
