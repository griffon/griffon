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
package lombok.javac.handlers;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import griffon.transform.Vetoable;
import lombok.ast.TypeRef;
import lombok.core.AnnotationValues;
import lombok.core.handlers.VetoableHandler;
import lombok.core.util.As;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacField;
import lombok.javac.handlers.ast.JavacType;
import org.kordamp.jipsy.ServiceProviderFor;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Contains code copied from HandlerBoundSetter.java, original from Philipp Eichhorn (lombok-pg)
 *
 * @author Andres Almiray
 */
@ServiceProviderFor(JavacAnnotationHandler.class)
public class HandleVetoable extends JavacAnnotationHandler<Vetoable> {
    @Override
    public void handle(AnnotationValues<Vetoable> annotation, JCAnnotation source, JavacNode annotationNode) {
        new VetoableHandler<JavacType, JavacField, JavacNode, JCTree>(annotationNode, source) {
            @Override
            protected void addInterface(JavacType type, String interfaceClassName) {
                JavacUtil.addInterface(type.node(), interfaceClassName);
            }

            @Override
            protected JavacType typeOf(JavacNode node, JCTree ast) {
                return JavacType.typeOf(node, ast);
            }

            @Override
            protected JavacField fieldOf(JavacNode node, JCTree ast) {
                return JavacField.fieldOf(node, ast);
            }

            @Override
            protected boolean hasMethodIncludingSupertypes(JavacType type, String methodName, TypeRef... argumentTypes) {
                return hasMethod(type.get().sym, methodName, type.editor().build(As.list(argumentTypes)));
            }

            @Override
            protected boolean isAnnotatedWith(final JavacType type, final Class<? extends Annotation> annotationClass) {
                final Symbol.TypeSymbol typeSymbol = type.get().sym;
                if (typeSymbol == null) return false;
                Type supertype = ((ClassSymbol) typeSymbol).getSuperclass();
                return isAnnotatedWith(supertype.tsym, annotationClass);
            }

            @Override
            protected boolean isAnnotatedWith(final JavacNode javacNode, final Class<? extends Annotation> annotationClass) {
                final Symbol.TypeSymbol typeSymbol = javacNode.get().type.tsym;
                if (typeSymbol == null) return false;
                Type supertype = ((ClassSymbol) typeSymbol).getSuperclass();
                return isAnnotatedWith(supertype.tsym, annotationClass);
            }

            private boolean isAnnotatedWith(final Symbol type, final Class<? extends Annotation> annotationClass) {
                return JavacElements.getAnnotation(type, annotationClass) != null;
            }

            private boolean hasMethod(final Symbol.TypeSymbol type, final String methodName, final List<JCTree> argumentTypes) {
                if (type == null) return false;
                for (Symbol enclosedElement : type.getEnclosedElements()) {
                    if (enclosedElement instanceof Symbol.MethodSymbol) {
                        if ((enclosedElement.flags() & (Flags.ABSTRACT)) != 0)
                            continue;
                        /*if ((enclosedElement.flags() & (Flags.PUBLIC)) == 0)
                            continue;*/
                        Symbol.MethodSymbol method = (Symbol.MethodSymbol) enclosedElement;
                        if (!methodName.equals(As.string(method.name)))
                            continue;
                        MethodType methodType = (MethodType) method.type;
                        if (argumentTypes.size() != methodType.argtypes.size())
                            continue;
                        // TODO check actual types..
                        return true;
                    }
                }
                Type supertype = ((ClassSymbol) type).getSuperclass();
                return hasMethod(supertype.tsym, methodName, argumentTypes);
            }
        }.handle();
    }
}
