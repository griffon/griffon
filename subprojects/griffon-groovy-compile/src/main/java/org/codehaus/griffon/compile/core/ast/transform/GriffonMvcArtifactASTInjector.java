/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.core.mvc.MVCGroup;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.EmptyStatement;

import static java.lang.reflect.Modifier.PUBLIC;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_EXCEPTIONS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_PARAMS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectMethod;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectProperty;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.param;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.params;
import static org.codehaus.groovy.ast.ClassHelper.MAP_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonMvcArtifactASTInjector extends GriffonArtifactASTInjector {
    private static final ClassNode MVC_GROUP_TYPE = makeClassSafe(MVCGroup.class);

    @Override
    public void inject(@Nonnull ClassNode classNode, @Nonnull String artifactType) {
        super.inject(classNode, artifactType);

        injectProperty(classNode, "mvcGroup", MVC_GROUP_TYPE);

        // void mvcGroupInit(Map args)
        injectMethod(classNode, new MethodNode(
            "mvcGroupInit",
            PUBLIC,
            VOID_TYPE,
            params(param(makeClassSafe(MAP_TYPE), "args")),
            NO_EXCEPTIONS,
            new EmptyStatement()
        ));

        // void mvcGroupDestroy()
        injectMethod(classNode, new MethodNode(
            "mvcGroupDestroy",
            PUBLIC,
            VOID_TYPE,
            NO_PARAMS,
            NO_EXCEPTIONS,
            new EmptyStatement()
        ));
    }
}
