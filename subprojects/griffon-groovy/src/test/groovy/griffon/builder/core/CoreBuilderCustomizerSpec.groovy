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
package griffon.builder.core

import griffon.util.BuilderCustomizer
import griffon.util.CompositeBuilder
import groovy.swing.factory.CollectionFactory
import groovy.swing.factory.MapFactory
import org.codehaus.griffon.runtime.core.view.AbstractBuilderCustomizer
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CoreBuilderCustomizerSpec extends Specification {
    void "Root node is properly configured"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([
            new TestBuilderCustomizer(),
            new CoreBuilderCustomizer()
        ] as BuilderCustomizer[])

        when:
        builder.build(script)

        then:
        builder.variables[rootName]
        builder.variables[rootName].key == ['value1', 'value2']

        where:
        rootName                                   | script
        'griffon.builder.core.ChildScript1-root'   | ParentScript1
        'griffon.builder.core.ChildScript2-myroot' | ParentScript2
        'griffon.builder.core.ChildScript1-root'   | ParentScript3
        'griffon.builder.core.ChildScript1-root'   | ParentScript4
    }
}

class TestBuilderCustomizer extends AbstractBuilderCustomizer {
    TestBuilderCustomizer() {
        setFactories([
            list: new CollectionFactory(),
            map: new MapFactory()
        ])
    }
}

class ParentScript1 extends Script {
    Object run() {
        build(ChildScript1)
        root(ChildScript1)
    }
}

class ChildScript1 extends Script {
    Object run() {
        root(list {
            map(key: 'value1')
            map(key: 'value2')
        })
    }
}

class ParentScript2 extends Script {
    Object run() {
        build(ChildScript2)
        root(ChildScript2)
    }
}

class ChildScript2 extends Script {
    Object run() {
        root(name: 'myroot', list {
            map(key: 'value1')
            map(key: 'value2')
        })
    }
}

class ParentScript3 extends Script {
    Object run() {
        build(ChildScript1)
        root()
    }
}

class ParentScript4 extends Script {
    Object run() {
        build(ChildScript1)
        root('root')
    }
}
