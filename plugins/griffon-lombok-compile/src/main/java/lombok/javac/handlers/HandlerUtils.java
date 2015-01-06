/*
 * Copyright 2008-2015 the original author or authors.
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
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import griffon.util.MethodDescriptor;
import lombok.core.AST;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacASTMaker;
import lombok.javac.handlers.ast.JavacType;
import lombok.javac.handlers.types.JCBooleanType;

import javax.lang.model.element.Modifier;
import java.util.Set;

import static lombok.javac.handlers.JavacHandlerUtil.annotationTypeMatches;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.types.JCNoType.voidType;

/**
 * @author Andres Almiray
 */
public class HandlerUtils {
    public static final List<JCTree.JCExpression> NIL_EXPRESSION = List.<JCTree.JCExpression>nil();

    public static JCTree.JCExpression makeType(String type, JavacNode node) {
        return makeType(type, node, node.getTreeMaker());
    }

    public static JCTree.JCExpression makeType(String type, JavacNode node, TreeMaker maker) {
        if (type.startsWith("[L")) {
            return maker.TypeArray(chainDotsString(node, type.substring(2, type.length() - 1)));
        } else if (type.startsWith("[")) {
            return maker.TypeArray(chainDotsString(node, type.substring(1, type.length() - 1)));
        } else if (JCBooleanType.type.equals(type)) {
            return node.getTreeMaker().Type(JCBooleanType.booleanType());
        }
        return chainDotsString(node, type);
    }

    public static JavacNode findTypeNodeFrom(JavacNode node) {
        JavacNode n = node;
        while (n != null && !isTypeDeclaration(n)) n = n.up();
        if (!isTypeDeclaration(n)) return null;
        return n;
    }

    public static boolean isTypeDeclaration(JavacNode node) {
        return node != null && node.get() instanceof JCTree.JCClassDecl;
    }

    public static JCTree.JCExpression thisExpression(JavacNode typeNode) {
        return typeNode.getTreeMaker().at(((JCTree) typeNode.get()).pos).Ident(typeNode.toName("this"));
    }

    public static List<JCTree.JCExpression> extractArgNames(List<JCTree.JCVariableDecl> params, TreeMaker m) {
        ListBuffer<JCTree.JCExpression> args = new ListBuffer<>();
        for (JCTree.JCVariableDecl param : params) {
            args.append(m.Ident(param.getName()));
        }
        return toList(args);
    }

    public static MethodDescriptor methodDescriptorFor(JCTree.JCMethodDecl method) {
        java.util.List<JCTree.JCVariableDecl> parameters = method.getParameters();
        String[] parameterTypes = new String[parameters.size()];

        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = parameters.get(i).getType().type.toString();
        }

        int modifiers = toJavacModifier(method.getModifiers().getFlags());

        return new MethodDescriptor(method.getName().toString(), parameterTypes, modifiers);
    }

    public static boolean hasAnnotation(JavacNode node, Class annotationClass) {
        for (JavacNode child : node.down()) {
            if (child.getKind() == AST.Kind.ANNOTATION) {
                if (annotationTypeMatches(annotationClass, child)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInstanceField(JavacNode field) {
        if (field.getKind() != AST.Kind.FIELD) return false;
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl) field.get();
        //Skip fields that start with $
        if (fieldDecl.name.toString().startsWith("$")) return false;
        //Skip static fields.
        if ((fieldDecl.mods.flags & Flags.STATIC) != 0) return false;
        return true;
    }

    public static JavacNode findMethod(String methodName, JavacNode node) {
        while (node != null && !(node.get() instanceof JCTree.JCClassDecl)) {
            node = node.up();
        }

        if (node != null && node.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl) node.get()).defs) {
                if (def instanceof JCTree.JCMethodDecl) {
                    String name = ((JCTree.JCMethodDecl) def).name.toString();
                    if (name.equals(methodName)) {
                        return node.getNodeFor(def);
                    }
                }
            }
        }

        return null;
    }

    public static int toJavacModifier(JCTree.JCModifiers modifiers) {
        return toJavacModifier(modifiers.getFlags());
    }

    public static int toJavacModifier(Set<Modifier> modifiers) {
        int mods = 0;

        for (Modifier mod : modifiers) {
            switch (mod) {
                case PUBLIC:
                    mods |= java.lang.reflect.Modifier.PUBLIC;
                    break;
                case PROTECTED:
                    mods |= java.lang.reflect.Modifier.PROTECTED;
                    break;
                case PRIVATE:
                    mods |= java.lang.reflect.Modifier.PRIVATE;
                    break;
                case STATIC:
                    mods |= java.lang.reflect.Modifier.STATIC;
                    break;
                case ABSTRACT:
                    mods |= java.lang.reflect.Modifier.ABSTRACT;
                    break;
                case FINAL:
                    mods |= java.lang.reflect.Modifier.FINAL;
                    break;
                case NATIVE:
                    mods |= java.lang.reflect.Modifier.NATIVE;
                    break;
                case SYNCHRONIZED:
                    mods |= java.lang.reflect.Modifier.SYNCHRONIZED;
                    break;
                case TRANSIENT:
                    mods |= java.lang.reflect.Modifier.TRANSIENT;
                    break;
                case VOLATILE:
                    mods |= java.lang.reflect.Modifier.VOLATILE;
                    break;
                case STRICTFP:
                    mods |= java.lang.reflect.Modifier.STRICT;
                    break;
            }
        }

        return mods;
    }

    public static JavacNode getField(JavacNode node, String fieldName) {
        while (node != null && !(node.get() instanceof JCTree.JCClassDecl)) {
            node = node.up();
        }

        if (node != null && node.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl) node.get()).defs) {
                if (def instanceof JCTree.JCVariableDecl) {
                    if (((JCTree.JCVariableDecl) def).name.contentEquals(fieldName)) {
                        return node.getNodeFor(def);
                    }
                }
            }
        }

        return null;
    }

    public static JCTree.JCExpression readField(JavacNode fieldNode) {
        return readField(fieldNode, null);
    }

    public static JCTree.JCExpression readField(JavacNode fieldNode, JCTree.JCExpression receiver) {
        TreeMaker maker = fieldNode.getTreeMaker();

        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl) fieldNode.get();

        if (receiver == null) {
            if ((fieldDecl.mods.flags & Flags.STATIC) == 0) {
                receiver = maker.Ident(fieldNode.toName("this"));
            } else {
                JavacNode containerNode = fieldNode.up();
                if (containerNode != null && containerNode.get() instanceof JCTree.JCClassDecl) {
                    JCTree.JCClassDecl container = (JCTree.JCClassDecl) fieldNode.up().get();
                    receiver = maker.Ident(container.name);
                }
            }
        }

        return receiver == null ? maker.Ident(fieldDecl.name) : maker.Select(receiver, fieldDecl.name);
    }

    // -- COPIED FROM LOMBOK !!!
    // -- REMOVE ONCE THESE METHODS BECOME AVAILABLE IN THE NEXT RELEASE

    public static <T> com.sun.tools.javac.util.List<T> toList(ListBuffer<T> collection) {
        return collection == null ? com.sun.tools.javac.util.List.<T>nil() : collection.toList();
    }

    public static class TokenBuilder {
        public final JavacNode context;

        public TokenBuilder(JavacNode context) {
            this.context = context;
        }

        public JavacNode getContext() {
            return context;
        }

        public TreeMaker getTreeMaker() {
            return context.getTreeMaker();
        }

        public Name name(String name) {
            return context.toName(name);
        }

        public JCTree.JCModifiers mods(int mods) {
            return context.getTreeMaker().Modifiers(mods);
        }

        public JCTree.JCExpression type(Class clazz) {
            return type(clazz.getName());
        }

        public JCTree.JCExpression type(String type) {
            return chainDotsString(context, type);
        }

        public JCTree.JCExpression void_t() {
            return context.getTreeMaker().Type(voidType());
        }

        public JCTree.JCVariableDecl param(int modifiers, Class clazz, String identifier) {
            return param(modifiers, clazz.getName(), identifier);
        }

        public JCTree.JCVariableDecl param(int modifiers, String clazz, String identifier) {
            return context.getTreeMaker().VarDef(mods(modifiers), name(identifier), type(clazz), null);
        }

        public JCTree.JCExpression dotExpr(String expr) {
            return chainDotsString(context, expr);
        }

        public JCTree.JCMethodInvocation call(JCTree.JCExpression method) {
            return call(NIL_EXPRESSION, method, NIL_EXPRESSION);
        }

        public JCTree.JCMethodInvocation call(JCTree.JCExpression method, List<JCTree.JCExpression> args) {
            return call(NIL_EXPRESSION, method, args);
        }

        public JCTree.JCMethodInvocation call(List<JCTree.JCExpression> typeArgs, JCTree.JCExpression method, List<JCTree.JCExpression> args) {
            return context.getTreeMaker().Apply(typeArgs, method, args);
        }

        public void addInterface(String interfaceName, JavacNode node) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) node.get();
            final ListBuffer<JCTree.JCExpression> implementing = ListBuffer.lb();
            implementing.appendList(classDecl.implementing);
            implementing.append(chainDotsString(node, interfaceName));
            classDecl.implementing = implementing.toList();
        }

        public void setSuperclass(String superclassName, JavacNode node) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) node.get();
            classDecl.extending = dotExpr(superclassName);
        }

        public JCTree.JCExpression staticCallExpr(String classType, String methodName) {
            return staticCallExpr(classType, methodName, NIL_EXPRESSION);
        }

        public JCTree.JCExpression staticCallExpr(String classType, String methodName, List<JCTree.JCExpression> args) {
            if (args == null) {
                args = NIL_EXPRESSION;
            }
            JCTree.JCExpression expr = dotExpr(classType + "." + methodName);
            return context.getTreeMaker().Apply(NIL_EXPRESSION, expr, args);
        }

        public JCTree.JCExpression invoke(JCTree.JCExpression receiver, String methodName) {
            return invoke(receiver, methodName, NIL_EXPRESSION);
        }

        public JCTree.JCExpression invoke(JCTree.JCExpression receiver, String methodName, List<JCTree.JCExpression> args) {
            if (args == null) {
                args = NIL_EXPRESSION;
            }
            return call(context.getTreeMaker().Select(receiver, name(methodName)), args);
        }
    }

    public static JCMethodDecl injectMethod(JavacType type, final lombok.ast.AbstractMethodDecl<?> methodDecl) {
        JavacASTMaker builder = new JavacASTMaker(type.node(), type.get());
        final JCMethodDecl method = builder.build(methodDecl, JCMethodDecl.class);
        JavacHandlerUtil.injectMethod(type.node(), method);
        if (methodDecl instanceof lombok.ast.WrappedMethodDecl) {
            lombok.ast.WrappedMethodDecl node = (lombok.ast.WrappedMethodDecl) methodDecl;
            MethodSymbol methodSymbol = (MethodSymbol) node.getWrappedObject();
            JCClassDecl tree = type.get();
            ClassSymbol c = tree.sym;
            c.members_field.enter(methodSymbol, c.members_field, methodSymbol.enclClass().members_field);
            method.sym = methodSymbol;
        }
        return method;
    }
}
