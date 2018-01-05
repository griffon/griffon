/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.builder.core

import griffon.util.BuilderCustomizer
import griffon.util.CompositeBuilder
import groovy.swing.factory.CollectionFactory
import groovy.swing.factory.MapFactory
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager
import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CoreBuilderCustomizerSpec extends Specification {
    void "Threading method #methodName is available in builder"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([
            new TestBuilderCustomizer(),
            newCoreBuilderCustomizer()
        ] as BuilderCustomizer[])

        when:
        builder.build(ParentScript1)

        then:
        methodName in builder.explicitMethods.keySet()

        where:
        methodName << ['runOutsideUI', 'runOutsideUIAsync', 'runInsideUISync', 'runInsideUIAsync', 'runFuture', 'isUIThread']
    }

    void "Root node should be implicitly identified"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([
            new TestBuilderCustomizer(),
            newCoreBuilderCustomizer()
        ] as BuilderCustomizer[])
        builder.variables.mvcId = 'mvc'

        when:
        builder.build(ImplicitRootNodeScript)

        then:
        builder.variables.'mvc-rootNode'?.name == 'name1'
    }

    void "Root node should be explicitly identified"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([
            new TestBuilderCustomizer(),
            newCoreBuilderCustomizer()
        ] as BuilderCustomizer[])
        builder.variables.mvcId = 'mvc'

        when:
        builder.build(ExplicitRootNodeScript)

        then:
        builder.variables.'mvc-rootNode'?.name == 'name2'
    }

    BuilderCustomizer newCoreBuilderCustomizer() {
        CoreBuilderCustomizer customizer = new CoreBuilderCustomizer()
        customizer.@uiThreadManager = new DefaultUIThreadManager()
        customizer.setup()
        customizer
    }
}

class TestBuilderCustomizer extends AbstractBuilderCustomizer {
    TestBuilderCustomizer() {
        setFactories([
            list: new CollectionFactory(),
            map : new MapFactory(),
            bean: new BeanFactory()
        ])
    }
}

class BeanFactory extends AbstractFactory {
    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        new Bean()
    }

    @Override
    boolean isLeaf() {
        false
    }
}

class ParentScript1 extends Script {
    Object run() {
    }
}

class ImplicitRootNodeScript extends Script {
    Object run() {
        bean(name: 'name1') {
            bean(name: 'name2') {
                list()
                map()
            }
        }
    }
}

class ExplicitRootNodeScript extends Script {
    Object run() {
        bean(name: 'name1')
        bean(name: 'name2', rootNode: true)
    }
}

class Bean {
    String name
}