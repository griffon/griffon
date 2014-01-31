/*
 * Copyright 2009-2013 the original author or authors.
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
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import lombok.javac.JavacNode;

import static lombok.javac.handlers.HandlerUtils.makeType;
import static lombok.javac.handlers.HandlerUtils.toList;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.recursiveSetGeneratedBy;
import static lombok.javac.handlers.types.JCNoType.voidType;

/**
 * @author Andres Almiray
 */
public class AstBuilder {
    public static ClassDefBuilder defClass(String className) {
        return new ClassDefBuilder(className);
    }

    public static MethodDefBuilder defMethod(String methodName) {
        return new MethodDefBuilder(methodName);
    }

    public static VariableDefBuilder defVar(String variableName) {
        return new VariableDefBuilder(variableName);
    }

    public static class ClassDefBuilder {
        private final String className;

        private long modifiers;
        private List<JCTree.JCTypeParameter> typeParameters;
        private JCTree.JCExpression superClass;
        private List<String> interfaces;
        private List<JCTree> members;

        public ClassDefBuilder(String className) {
            this.className = className;

            modifiers = Flags.PUBLIC;
            typeParameters = List.nil();
            interfaces = List.nil();
            members = List.nil();
        }

        public ClassDefBuilder modifiers(long mods) {
            modifiers = mods;
            return this;
        }

        public ClassDefBuilder extending(JCTree.JCExpression superClass) {
            this.superClass = superClass;
            return this;
        }

        public ClassDefBuilder implementing(Class... interfaces) {
            ListBuffer<String> types = new ListBuffer<>();
            for (Class type : interfaces) {
                types.append(type.getName());
            }
            this.interfaces = toList(types);
            return this;
        }

        public ClassDefBuilder implementing(String... interfaces) {
            ListBuffer<String> types = new ListBuffer<>();
            for (String type : interfaces) {
                types.append(type);
            }
            this.interfaces = toList(types);
            return this;
        }

        public ClassDefBuilder implementing(List<String> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public ClassDefBuilder typeParams(List<JCTree.JCTypeParameter> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public ClassDefBuilder withMembers(List<JCTree> members) {
            this.members = members;
            return this;
        }

        public ClassDefBuilder withMembers(JCTree... members) {
            ListBuffer<JCTree> defs = new ListBuffer<>();
            for (JCTree member : members) {
                defs.append(member);
            }
            return withMembers(HandlerUtils.toList(defs));
        }

        public JCTree.JCClassDecl $(JavacNode context) {
            return build(context);
        }

        public JCTree.JCClassDecl build(JavacNode context) {
            TreeMaker m = context.getTreeMaker();

            ListBuffer<JCTree.JCExpression> implemented = new ListBuffer<>();
            for (String type : interfaces) {
                implemented.append(chainDotsString(context, type));
            }

            return context.getTreeMaker().ClassDef(
                m.Modifiers(modifiers),
                context.toName(className),
                typeParameters,
                superClass,
                toList(implemented),
                members);
        }
    }

    public static class MethodDefBuilder {
        private final String methodName;

        private long modifiers;
        private String returnType;
        private JCTree.JCExpression returnTypeExpr;
        private List<JCTree.JCTypeParameter> typeParameters;
        private List<JCTree.JCVariableDecl> params;
        private List<JCTree.JCExpression> throwables;
        private List<JCTree.JCStatement> statements;
        private String throwable;

        public MethodDefBuilder(String methodName) {
            this.methodName = methodName;

            modifiers = Flags.PUBLIC;
            returnType = Void.TYPE.getName();
            typeParameters = List.nil();
            params = List.nil();
            throwables = List.nil();
            statements = List.<JCTree.JCStatement>nil();
        }

        public MethodDefBuilder modifiers(long mods) {
            modifiers = mods;
            return this;
        }

        public MethodDefBuilder returning(Class clazz) {
            return returning(clazz.getName());
        }

        public MethodDefBuilder returning(String className) {
            returnType = className;
            return this;
        }

        public MethodDefBuilder returning(JCTree.JCExpression type) {
            this.returnTypeExpr = type;
            return this;
        }

        public MethodDefBuilder typeParams(List<JCTree.JCTypeParameter> typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public MethodDefBuilder withParams(ListBuffer<JCTree.JCVariableDecl> params) {
            return withParams(HandlerUtils.toList(params));
        }

        public MethodDefBuilder withParams(List<JCTree.JCVariableDecl> params) {
            this.params = params;
            return this;
        }

        public MethodDefBuilder throwing(List<JCTree.JCExpression> throwables) {
            this.throwables = throwables;
            return this;
        }

        public MethodDefBuilder throwing(String throwable) {
            this.throwable = throwable;
            return this;
        }

        public MethodDefBuilder throwing(Class throwable) {
            this.throwable = throwable.getName();
            return this;
        }

        public MethodDefBuilder withBody(List<JCTree.JCStatement> statements) {
            this.statements = statements;
            return this;
        }

        public MethodDefBuilder withBody(JCTree.JCStatement statement) {
            return withBody(List.of(statement));
        }

        public JCTree.JCMethodDecl $(JavacNode context) {
            return build(context);
        }

        public JCTree.JCMethodDecl build(JavacNode context) {
            TreeMaker m = context.getTreeMaker();

            JCTree.JCExpression returns = returnTypeExpr;
            if (returns == null) {
                returns = m.Type(voidType());
                if (!returnType.equals(Void.TYPE.getName())) {
                    returns = makeType(returnType, context);
                }
            }


            List<JCTree.JCExpression> throwablesExpression = null;
            if (throwables != null) {
                throwablesExpression = throwables;
            } else if (throwable != null) {
                ListBuffer<JCTree.JCExpression> throwing = new ListBuffer<JCTree.JCExpression>();
                throwing.append(chainDotsString(context, throwable));
                throwablesExpression = toList(throwing);
            }

            return context.getTreeMaker().MethodDef(
                m.Modifiers(modifiers),
                context.toName(methodName),
                returns,
                typeParameters,
                params,
                throwablesExpression,
                m.Block(0, statements),
                null);
        }
    }

    public static class VariableDefBuilder {
        private final String variableName;

        private long modifiers;
        private String varType;
        private JCTree.JCExpression type;
        private JCTree.JCExpression value;
        private List<JCTree.JCExpression> args;

        public VariableDefBuilder(String variableName) {
            this.variableName = variableName;

            modifiers = 0;
            varType = Object.class.getName();
        }

        public VariableDefBuilder modifiers(long mods) {
            modifiers = mods;
            return this;
        }

        public VariableDefBuilder type(Class clazz) {
            return type(clazz.getName());
        }

        public VariableDefBuilder type(String className) {
            varType = className;
            return this;
        }

        public VariableDefBuilder type(JCTree.JCExpression type) {
            this.type = type;
            return this;
        }

        public VariableDefBuilder withValue(JCTree.JCExpression value) {
            this.value = value;
            return this;
        }

        public VariableDefBuilder withArgs(JCTree.JCExpression... args) {
            this.args = List.from(args);
            return this;
        }

        public JCTree.JCVariableDecl $(JavacNode context) {
            return build(context, context.get());
        }

        public JCTree.JCVariableDecl build(JavacNode context) {
            return build(context, context.get());
        }

        public JCTree.JCVariableDecl $(JavacNode context, JCTree source) {
            return build(context, source);
        }

        public JCTree.JCVariableDecl build(JavacNode context, JCTree source) {
            TreeMaker m = context.getTreeMaker().at(source.pos);

            JCTree.JCExpression typeExpression = type != null ? type : makeType(varType, context, m);
            JCTree.JCExpression initExpression = value;
            if (value == null && args != null) {
                initExpression = m.NewClass(null, null, typeExpression, args, null);
            }

            return recursiveSetGeneratedBy(m.VarDef(
                m.Modifiers(modifiers),
                context.toName(variableName),
                typeExpression,
                initExpression), source);
        }
    }
}
