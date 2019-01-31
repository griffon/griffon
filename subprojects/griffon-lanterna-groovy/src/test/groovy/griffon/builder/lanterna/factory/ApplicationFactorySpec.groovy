/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.builder.lanterna.factory

import com.googlecode.lanterna.gui.Window
import griffon.builder.lanterna.LanternaBuilderCustomizer
import griffon.util.groovy.BuilderCustomizer
import griffon.util.groovy.CompositeBuilder
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ApplicationFactorySpec extends Specification {
    void "Can create an Application"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([new LanternaBuilderCustomizer()] as BuilderCustomizer[])
        builder.variables.application = new Expando(
            configuration: [
                'application.title': 'lanterna'
            ],
            createApplicationContainer: { options -> new Window(options.title) },
            windowManager: new Expando(
                attach: { name, window ->
                    builder.variables[name] = window
                }
            )
        )

        when:
        Window window1 = builder.application(title: 'Griffon', name: 'mywindow', id: 'w1')
        Window window2 = builder.application(id: 'w2')

        then:
        window1 != null
        window1 == builder.w1
        window1 == builder.variables.mywindow
        window1.toString() == 'Griffon'
        window2 != null
        window2 == builder.w2
        window2 == builder.variables.window0
        window2.toString() == 'lanterna'
    }
}
