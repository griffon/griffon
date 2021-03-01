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
package griffon.transform

import griffon.core.artifact.GriffonView
import spock.lang.Specification

import java.lang.reflect.Method

class GriffonViewSpec extends Specification {
    def 'GriffonViewASTTransformation is applied to a view class'() {
        given:
        GroovyClassLoader gcl = new GroovyClassLoader()

        when:
        def clazz = gcl.parseClass('''
            @org.kordamp.jipsy.annotations.ServiceProviderFor(griffon.core.artifact.GriffonView)
            class SimpleView {
                void initUI() { }
            }
            ''')

        then:
        GriffonView.isAssignableFrom(clazz)
    }

    def 'GriffonViewASTTransformation is applied to a view class with a custom superclass'() {
        given:
        GroovyClassLoader gcl = new GroovyClassLoader()

        when:
        gcl.parseClass('''
            class BaseView { }
            ''')

        def clazz = gcl.parseClass('''
            @org.kordamp.jipsy.annotations.ServiceProviderFor(griffon.core.artifact.GriffonView)
            class SimpleView extends BaseView {
                void initUI() { }
            }
            ''')

        then:
        GriffonView.isAssignableFrom(clazz)
        GriffonView.methods.each { Method target ->
            assert clazz.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }
}
