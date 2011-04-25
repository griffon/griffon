/*
 * Copyright 2009-2011 the original author or authors.
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

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.messages.*;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.*;
import org.objectweb.asm.Opcodes;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.util.Map;
import java.util.List;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import griffon.util.Threading;
import griffon.util.UIThreadHelper;
import griffon.util.GriffonClassUtils;
import griffon.util.GriffonClassUtils.MethodDescriptor;
import org.codehaus.griffon.compiler.GriffonCompilerContext;
import static org.codehaus.griffon.ast.GriffonASTUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles generation of code for the {@code @Threading} annotation.
 * <p/>
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class ThreadingASTTransformation implements ASTTransformation, Opcodes {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadingASTTransformation.class);

    private static ClassNode MY_TYPE = new ClassNode(Threading.class);
    private static ClassNode UITHREAD_HELPER_CLASS = ClassHelper.makeWithoutCaching(UIThreadHelper.class);
    private static final String COMPILER_THREADING_KEY = "compiler.threading";

    public static final String EXECUTE_OUTSIDE = "executeOutside";
    public static final String EXECUTE_SYNC = "executeSync";
    public static final String EXECUTE_ASYNC = "executeAsync";

    /**
     * Convenience method to see if an annotated node is {@code @Threading}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasThreadingAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (MY_TYPE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes   the ast nodes
     * @param source  the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            addError("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes), nodes[0], source);
        }

        AnnotationNode annotation = (AnnotationNode) nodes[0];
        AnnotatedNode node = (AnnotatedNode) nodes[1];

        Threading.Policy threadingPolicy = getThreadingPolicy(annotation);
        if(threadingPolicy == Threading.Policy.SKIP) return;

        String threadingMethod = EXECUTE_OUTSIDE;
        switch(threadingPolicy) {
            case INSIDE_UITHREAD_SYNC:
                threadingMethod = EXECUTE_SYNC;
                break;
            case INSIDE_UITHREAD_ASYNC:
                threadingMethod = EXECUTE_ASYNC;
                break;
            case OUTSIDE_UITHREAD:
            default:
                break;
        }

        if(node instanceof MethodNode) {
            handleMethodForInjection(node.getDeclaringClass(), (MethodNode) node, threadingMethod);
        } else if(node instanceof PropertyNode) {
            handlePropertyForInjection(node.getDeclaringClass(), (PropertyNode) node, threadingMethod);
        } else if(node instanceof FieldNode) {
            handleFieldForInjection(node.getDeclaringClass(), (FieldNode) node, threadingMethod);
        }
    }

    public static Threading.Policy getThreadingPolicy(AnnotationNode annotation) {
        PropertyExpression value = (PropertyExpression) annotation.getMember("value");
        if(value == null) return Threading.Policy.OUTSIDE_UITHREAD;
        return Threading.Policy.valueOf(value.getPropertyAsString());
    }
    
    public static String getThreadingMethod(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (MY_TYPE.equals(annotation.getClassNode())) {
                Threading.Policy threadingPolicy = getThreadingPolicy(annotation);
                if(threadingPolicy == Threading.Policy.SKIP) return null;
                return getThreadingMethod(threadingPolicy);
            }
        }
        
        return null;
    }

    public static String getThreadingMethod(Threading.Policy threadingPolicy) {
        String threadingMethod = null;
        switch(threadingPolicy) {
            case SKIP:
                break;
            case INSIDE_UITHREAD_SYNC:
                threadingMethod = EXECUTE_SYNC;
                break;
            case INSIDE_UITHREAD_ASYNC:
                threadingMethod = EXECUTE_ASYNC;
                break;
            case OUTSIDE_UITHREAD:
            default:
                threadingMethod = EXECUTE_OUTSIDE;
                break;
        }

        return threadingMethod;
    }

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.getLineNumber();
        int col = expr.getColumnNumber();
        source.getErrorCollector().addErrorAndContinue(
            new SyntaxErrorMessage(new SyntaxException(msg + '\n', line, col), source)
        );
    }

    public static void handleMethodForInjection(ClassNode classNode, MethodNode method) {
        handleMethodForInjection(classNode, method, EXECUTE_OUTSIDE);
    }

    public static void handleMethodForInjection(ClassNode classNode, MethodNode method, String threadingMethod) {
        MethodDescriptor md = methodDescriptorFor(method);
        if(GriffonClassUtils.isPlainMethod(md) &&
           !GriffonClassUtils.isEventHandler(md)) {
            wrapStatements(classNode, method, threadingMethod);
        }
    }

    public static void handlePropertyForInjection(ClassNode classNode, PropertyNode property) {
        handlePropertyForInjection(classNode, property, EXECUTE_OUTSIDE);
    }

    public static void handlePropertyForInjection(ClassNode classNode, PropertyNode property, String threadingMethod) {
        if(property.getModifiers() - Modifier.PUBLIC == 0 &&
           !GriffonClassUtils.isEventHandler(property.getName())) {
            wrapStatements(classNode, property, threadingMethod);
        }
    }

    public static void handleFieldForInjection(ClassNode classNode, FieldNode field) {
        handleFieldForInjection(classNode, field, EXECUTE_OUTSIDE);
    }

    public static void handleFieldForInjection(ClassNode classNode, FieldNode field, String threadingMethod) {
        if(field.getModifiers() - Modifier.PUBLIC == 0 &&
           !GriffonClassUtils.isEventHandler(field.getName())) {
            wrapStatements(classNode, field, threadingMethod);
        }
    }

    private static MethodDescriptor methodDescriptorFor(MethodNode method) {
        if(method == null) return null;
        Parameter[] types = method.getParameters();
        Class[] parameterTypes = new Class[types.length];
        for(int i = 0; i < types.length; i++) {
            parameterTypes[i] = types[i].getType().getTypeClass();
        }
        return new MethodDescriptor(method.getName(), parameterTypes, method.getModifiers());
    }

    private static void wrapStatements(ClassNode declaringClass, MethodNode method, String threadingMethod) {
        if(skipInjection(declaringClass.getName() +"."+ method.getName())) return;
        Statement code = method.getCode();
        Statement wrappedCode = wrapStatements(code, threadingMethod);
        if(code != wrappedCode) {
            method.setCode(wrappedCode);
            if(LOG.isDebugEnabled()) LOG.debug("Modified "+declaringClass.getName()+"."+method.getName()+"() - code wrapped with "+threadingMethod+"{}");
        }
    }

    private static void wrapStatements(ClassNode declaringClass, PropertyNode property, String threadingMethod) {
        wrapStatements(declaringClass, property.getField(), threadingMethod);
    }

    private static void wrapStatements(ClassNode declaringClass, FieldNode field, String threadingMethod) {
        if(skipInjection(declaringClass.getName() +"."+ field.getName())) return;

        boolean modified = false;
        Expression initialExpression = field.getInitialExpression();
        if(initialExpression instanceof ClosureExpression) {
            ClosureExpression closure = (ClosureExpression) initialExpression;
            modified = wrapClosure(closure, threadingMethod);
        } else if(initialExpression instanceof MethodCallExpression) {
            // very special case in order to deal with curried methods
            MethodCallExpression mce = (MethodCallExpression) initialExpression;
            Expression method = mce.getMethod();
            String methodName = ((ConstantExpression) method).getText();
            if(!"curry".equals(methodName)) return;
            List<Expression> args = ((ArgumentListExpression) mce.getArguments()).getExpressions();
            Expression last = args.size() > 0 ? args.get(args.size() - 1) : null;
            if(last instanceof ClosureExpression) {
                ClosureExpression closure = (ClosureExpression) last;
                modified = wrapClosure(closure, threadingMethod);
            }
        }

        if(modified && LOG.isDebugEnabled()) LOG.debug("Modified "+declaringClass.getName()+"."+field.getName()+"() - code wrapped with "+threadingMethod+"{}");
    }

    private static boolean wrapClosure(ClosureExpression closure, String threadingMethod) {
        Statement code = closure.getCode();
        Statement wrappedCode = wrapStatements(closure.getCode(), threadingMethod);
        return code != wrappedCode;
    }

    private static boolean skipInjection(String actionName) {
        Map settings = GriffonCompilerContext.getFlattenedBuildSettings();

        String keyName = COMPILER_THREADING_KEY + "." + actionName;
        while(!COMPILER_THREADING_KEY.equals(keyName)) {
            Object value = settings.get(keyName);
            keyName = keyName.substring(0, keyName.lastIndexOf("."));
            if(value != null && !DefaultTypeTransformation.castToBoolean(value)) return true;
        }

        return false;
    }

    private static Statement wrapStatements(Statement code, String threadingMethod) {
        BlockStatement codeBlock = (BlockStatement) code;
        List<Statement> statements = codeBlock.getStatements();
        if(statements.isEmpty()) return code;
        if(statements.size() == 1 && usesThreadingAlready(statements.get(0))) return code;

        VariableScope variableScope = codeBlock.getVariableScope();

        BlockStatement block = new BlockStatement();
        block.setVariableScope(variableScope);
        ClosureExpression closure = new ClosureExpression(Parameter.EMPTY_ARRAY, code);
        closure.setVariableScope(variableScope);
        block.addStatement(stmnt(new MethodCallExpression(uiThreadHelperInstance(), threadingMethod, args(closure))));
//System.err.println(block.getText());
        return block;
    }

    private static Expression uiThreadHelperInstance() {
        return new StaticMethodCallExpression(
                   UITHREAD_HELPER_CLASS,
                   "getInstance",
                   NO_ARGS);
    }

    private static boolean usesThreadingAlready(Statement stmnt) {
        if(!(stmnt instanceof ExpressionStatement)) return false;
        Expression expr = ((ExpressionStatement) stmnt).getExpression();
        if(!(expr instanceof MethodCallExpression)) return false;
        MethodCallExpression methodExpr = (MethodCallExpression) expr;
        String methodName = ((ConstantExpression)(methodExpr).getMethod()).getText();

        ClassExpression classExpr = null;
            // UIThreadHelper.instance
        if(methodExpr.getObjectExpression() instanceof PropertyExpression) {
            PropertyExpression objExpr = (PropertyExpression) methodExpr.getObjectExpression();
            if(!(objExpr.getProperty() instanceof ConstantExpression)) return false;
            ConstantExpression constExpr = (ConstantExpression) objExpr.getProperty();
            if(!constExpr.getText().equals("instance")) return false;
            if(!(objExpr.getObjectExpression() instanceof ClassExpression)) return false;
            classExpr = (ClassExpression) objExpr.getObjectExpression();
        } else if(methodExpr.getObjectExpression() instanceof MethodCallExpression) {
            MethodCallExpression objExpr = (MethodCallExpression) methodExpr.getObjectExpression();
            if(!(objExpr.getMethod() instanceof ConstantExpression)) return false;
            ConstantExpression constExpr = (ConstantExpression) objExpr.getMethod();
            if(!constExpr.getText().equals("getInstance")) return false;
            if(!(objExpr.getObjectExpression() instanceof ClassExpression)) return false;
            classExpr = (ClassExpression) objExpr.getObjectExpression();
        }
        
        if(classExpr != null) {    
            if(!classExpr.getText().equals(UIThreadHelper.class.getName())) return false;
            return "executeOutside".equals(methodName) || 
                   "executeSync".equals(methodName) || 
                   "executeAsync".equals(methodName) || 
                   "executeFuture".equals(methodName);
        }
        return "execOutside".equals(methodName) || "doOutside".equals(methodName) ||
               "execSync".equals(methodName)    || "edt".equals(methodName) ||
               "execAsync".equals(methodName)   || "doLater".equals(methodName);
    }
}
