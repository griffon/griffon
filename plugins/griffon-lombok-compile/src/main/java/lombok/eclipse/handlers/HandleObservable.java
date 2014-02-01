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
 */
package lombok.eclipse.handlers;

import griffon.transform.Observable;
import lombok.ast.TypeRef;
import lombok.core.AnnotationValues;
import lombok.core.handlers.ObservableHandler;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseField;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.List;

import static lombok.eclipse.handlers.Eclipse.ensureAllClassScopeMethodWereBuild;

/**
 * Contains code copied from HandlerBoundSetter.java, original from Philipp Eichhorn (lombok-pg)
 *
 * @author Andres Almiray
 */
@ServiceProviderFor(EclipseAnnotationHandler.class)
public class HandleObservable extends EclipseAnnotationHandler<Observable> {
    @Override
    public void handle(final AnnotationValues<Observable> annotation, final Annotation source, final EclipseNode annotationNode) {
        new ObservableHandler<EclipseType, EclipseField, EclipseNode, ASTNode>(annotationNode, source) {
            @Override
            protected void addInterface(EclipseType type, String interfaceClassName) {
                EclipseUtil.addInterface(type.get(), interfaceClassName, source);
            }

            @Override
            protected EclipseType typeOf(EclipseNode node, ASTNode ast) {
                return EclipseType.typeOf(node, ast);
            }

            @Override
            protected EclipseField fieldOf(EclipseNode node, ASTNode ast) {
                return EclipseField.fieldOf(node, ast);
            }

            @Override
            protected boolean hasMethodIncludingSupertypes(EclipseType type, String methodName, TypeRef... argumentTypes) {
                return hasMethod(type.get().binding, methodName, type.editor().build(As.list(argumentTypes)));
            }

            private boolean hasMethod(final TypeBinding binding, final String methodName, List<ASTNode> argumentTypes) {
                if (binding instanceof ReferenceBinding) {
                    ReferenceBinding rb = (ReferenceBinding) binding;
                    MethodBinding[] availableMethods = rb.availableMethods();
                    for (MethodBinding method : Each.elementIn(availableMethods)) {
                        if (method.isAbstract()) continue;
                        /*if (!method.isPublic()) continue;*/
                        if (!methodName.equals(As.string(method.selector)))
                            continue;
                        if (argumentTypes.size() != As.list(method.parameters).size())
                            continue;
                        // TODO check actual types..
                        return true;
                    }
                    ReferenceBinding superclass = rb.superclass();
                    ensureAllClassScopeMethodWereBuild(superclass);
                    return hasMethod(superclass, methodName, argumentTypes);
                }
                return false;
            }

            @Override
            protected boolean isAnnotatedWith(EclipseType type, Class<? extends java.lang.annotation.Annotation> annotationClass) {
                Annotation[] annotations = type.get().annotations;
                if (annotations == null) return false;
                for (Annotation annotation : annotations) {
                    if (annotation == null || annotation.type == null) continue;
                    String annotationName = resolveAnnotationName(annotation.type.getTypeName());
                    if (annotationName.equals(annotationClass.getName()))
                        return true;
                }
                return false;
            }

            @Override
            protected boolean isAnnotatedWith(final EclipseNode eclipseNode, final Class<? extends java.lang.annotation.Annotation> annotationClass) {
                final boolean[] result = new boolean[1];
                result[0] = false;
                eclipseNode.traverse(new EclipseASTAdapter() {
                    @Override
                    public void visitAnnotationOnType(TypeDeclaration type, EclipseNode annotationNode, Annotation annotation) {
                        Annotation[] annotations = type.annotations;
                        if (annotations == null) {
                            result[0] = false;
                        } else {
                            for (Annotation a : annotations) {
                                if (a == null || a.type == null) continue;
                                String annotationName = resolveAnnotationName(a.type.getTypeName());
                                if (annotationName.equals(annotationClass.getName())) {
                                    result[0] = true;
                                    break;
                                }
                            }
                        }
                    }

                });

                return result[0];
            }

            private String resolveAnnotationName(char[][] typeName) {
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < typeName.length; i++) {
                    b.append(new String(typeName[i]));
                    if (i < typeName.length - 1) b.append(".");
                }
                return b.toString();
            }
        }.handle();
    }
}
