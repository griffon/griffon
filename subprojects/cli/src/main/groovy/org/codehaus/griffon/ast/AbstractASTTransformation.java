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

import griffon.util.ApplicationHolder;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.objectweb.asm.Opcodes;

import java.util.Collections;

import static org.codehaus.griffon.ast.GriffonASTUtils.NO_ARGS;
import static org.codehaus.griffon.ast.GriffonASTUtils.call;

/**
 * Base class for all of Griffon's ASTTransformation implementations.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public abstract class AbstractASTTransformation implements ASTTransformation, Opcodes {
    private static final ClassNode APPLICATION_HOLDER_TYPE = ClassHelper.makeWithoutCaching(ApplicationHolder.class);
    private static final ClassNode COLLECTIONS_CLASS = ClassHelper.makeWithoutCaching(Collections.class);

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.getLineNumber();
        int col = expr.getColumnNumber();
        source.getErrorCollector().addErrorAndContinue(
                new SyntaxErrorMessage(new SyntaxException(msg + '\n', line, col), source)
        );
    }

    public static Expression emptyMap() {
        return call(COLLECTIONS_CLASS, "emptyMap", NO_ARGS);
    }

    public static Expression applicationInstance() {
        return call(APPLICATION_HOLDER_TYPE, "getApplication", NO_ARGS);
    }
}
