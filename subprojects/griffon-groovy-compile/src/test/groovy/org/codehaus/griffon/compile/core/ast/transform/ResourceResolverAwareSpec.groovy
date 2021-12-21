/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.compile.core.ast.transform

import griffon.core.resources.ResourceResolver
import spock.lang.Specification

import java.lang.reflect.Method

class ResourceResolverAwareSpec extends Specification {
    def 'ResourceResolverASTTransformation is applied to a bean via @ResourceResolverAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''import griffon.annotations.resources.ResourceResolverAware
            @griffon.annotations.resources.ResourceResolverAware
            class Bean { }
            new Bean()
            ''')

        then:
        bean instanceof ResourceResolver
        ResourceResolver.methods.each { Method target ->
            assert bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }
}
