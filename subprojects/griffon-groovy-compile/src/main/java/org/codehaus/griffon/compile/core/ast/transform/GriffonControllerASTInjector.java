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
package org.codehaus.griffon.compile.core.ast.transform;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_EXCEPTIONS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.THIS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.args;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.call;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectMethod;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.param;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.params;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.stmnt;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.var;
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonControllerASTInjector extends GriffonMvcArtifactASTInjector {
    private static final ClassNode OBJECT_ARRAY_TYPE = makeClassSafe(Object[].class);
    private static final String INVOKE_ACTION = "invokeAction";
    private static final String ACTION_NAME = "actionName";
    private static final String ARGS = "args";

    public void inject(@Nonnull ClassNode classNode, @Nonnull String artifactType) {
        super.inject(classNode, artifactType);

        // void invokeAction(String actionName, Object... args)
        injectMethod(classNode, new MethodNode(
            INVOKE_ACTION,
            Modifier.PUBLIC,
            VOID_TYPE,
            params(
                param(STRING_TYPE, ACTION_NAME),
                param(OBJECT_ARRAY_TYPE, ARGS)),
            NO_EXCEPTIONS,
            stmnt(call(
                applicationProperty(classNode, "actionManager"),
                INVOKE_ACTION,
                args(THIS, var(ACTION_NAME), var(ARGS))))
        ));
    }
}