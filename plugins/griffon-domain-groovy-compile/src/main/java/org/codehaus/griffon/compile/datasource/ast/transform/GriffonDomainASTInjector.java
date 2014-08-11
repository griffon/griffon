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
package org.codehaus.griffon.compile.datasource.ast.transform;

import griffon.plugins.domain.Event;
import org.codehaus.griffon.compile.core.ast.transform.GriffonMvcArtifactASTInjector;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.EmptyStatement;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.LIST_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonDomainASTInjector extends GriffonMvcArtifactASTInjector {
    public void inject(@Nonnull ClassNode classNode, @Nonnull String artifactType) {
        super.inject(classNode, artifactType);

        for (Event event : Event.values()) {
            // void <eventname>()
            injectMethod(classNode, new MethodNode(
                event.name(),
                Modifier.PUBLIC,
                VOID_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                new EmptyStatement()
            ));
        }

        injectMethod(classNode, new MethodNode(
            "beforeValidate",
            Modifier.PUBLIC,
            VOID_TYPE,
            params(param(makeClassSafe(LIST_TYPE), "propertyNames")),
            NO_EXCEPTIONS,
            new EmptyStatement()
        ));
    }
}