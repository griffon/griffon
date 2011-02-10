/*
 * Copyright 2003-2011 the original author or authors.
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
package org.codehaus.griffon.compiler;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.classgen.*;
import org.codehaus.groovy.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Injects conditional logging on Griffon artifacts.<p>
 * Based on the work done for LogASTTransformation and Slf4jLoggingStrategy by: Guillaume Laforge,
 * Jochen Theodorou, Dinko Srkoc, Hamlet D'Arcy, Raffaele Cigni, Alberto Vilches Raton, Tomasz Bujok,
 * Martin Ghados, Matthias Cullmann, Alberto Mijares.
 *
 * @author Andres Almiray
 */
public class LoggingInjectionOperation extends CompilationUnit.PrimaryClassNodeOperation {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingInjectionOperation.class);
    private static final String LOG_VARIABLE_NAME = "log";

    public void call(final SourceUnit source, final GeneratorContext context, final ClassNode classNode) throws CompilationFailedException {
        if(!GriffonCompilerContext.isGriffonArtifact(source) && !GriffonCompilerContext.isGriffonAddon(source)) return;

        final LoggingStrategy loggingStrategy = new Slf4jLoggingStrategy();

        ClassCodeExpressionTransformer transformer = new ClassCodeExpressionTransformer() {
            @Override
            protected SourceUnit getSourceUnit() {
                return source;
            }

            public Expression transform(Expression exp) {
                if (exp == null) return null;
                if (exp instanceof MethodCallExpression) {
                    return transformMethodCallExpression(exp);
                } else if(exp instanceof ClosureExpression) {
                    visitClosureExpression((ClosureExpression) exp);
                }
                return super.transform(exp);
            }

            private Expression transformMethodCallExpression(Expression exp) {
                MethodCallExpression mce = (MethodCallExpression) exp;
                if (!(mce.getObjectExpression() instanceof VariableExpression)) {
                    return exp;
                }
                VariableExpression variableExpression = (VariableExpression) mce.getObjectExpression();
                if (!variableExpression.getName().equals(LOG_VARIABLE_NAME)
                        || !(variableExpression.getAccessedVariable() instanceof DynamicVariable)) {
                    return exp;
                }
                String methodName = mce.getMethodAsString();
                if (methodName == null) return exp;
                if (usesSimpleMethodArgumentsOnly(mce)) return exp;

                if (!loggingStrategy.isLoggingMethod(methodName)) return exp;
                return loggingStrategy.wrapLoggingMethodCall(variableExpression, methodName, exp, source); 
            }

            private boolean usesSimpleMethodArgumentsOnly(MethodCallExpression mce) {
                Expression arguments = mce.getArguments();
                if (arguments instanceof TupleExpression) {
                    TupleExpression tuple = (TupleExpression) arguments;
                    for (Expression exp : tuple.getExpressions()) {
                        if (!isSimpleExpression(exp)) return false;
                    }
                    return true;
                }
                return !isSimpleExpression(arguments);
            }

            private boolean isSimpleExpression(Expression exp) {
                if (exp instanceof ConstantExpression) return true;
                if (exp instanceof VariableExpression) return true;
                return false;
            }

        };
        transformer.visitClass(classNode);
    }

    /**
     * A LoggingStrategy defines how to wire a new logger instance into an existing class.
     * It is meant to be used with the @Log family of annotations to allow you to
     * write your own Log annotation provider.
     */
    public interface LoggingStrategy {
        boolean isLoggingMethod(String methodName);
        Expression wrapLoggingMethodCall(Expression logVariable, String methodName, Expression originalExpression, SourceUnit source);
    }
    
    private static class Slf4jLoggingStrategy implements LoggingStrategy {
        public boolean isLoggingMethod(String methodName) {
            return methodName.matches("error|warn|info|debug|trace");
        }

        public Expression wrapLoggingMethodCall(Expression logVariable, String methodName, Expression originalExpression, final SourceUnit source) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Wrapping logging expression found at ["+originalExpression.getLineNumber()+","+originalExpression.getColumnNumber()+
                          "] in "+source.getName());
            }

            MethodCallExpression condition = new MethodCallExpression(
                    logVariable,
                    "is" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1, methodName.length()) + "Enabled",
                    ArgumentListExpression.EMPTY_ARGUMENTS);

            return new TernaryExpression(
                    new BooleanExpression(condition),
                    originalExpression,
                    ConstantExpression.NULL);
        }
    }
}
