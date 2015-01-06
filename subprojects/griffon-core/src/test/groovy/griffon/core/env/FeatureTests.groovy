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
package griffon.core.env

public class FeatureTests extends GroovyTestCase {
    protected void tearDown() {
        System.setProperty('feature.foo', '')
        System.setProperty('feature.bar', '')
        System.setProperty('feature.foobar', '')

        Metadata.getCurrent().clear()
    }

    void testSystemPropertyHasPrecedenceOverMetadata() {
        // given:
        Metadata.getInstance(new ByteArrayInputStream('feature.foo=true\nfeature.bar=false\n'.bytes))
        System.setProperty('feature.foobar', 'true')

        // expect:
        assert Feature.isFeatureEnabled('feature.foobar')
        assert Feature.isFeatureEnabled('feature.foo')
        assert !Feature.isFeatureEnabled('feature.bar')

        // when:
        System.setProperty('feature.bar', 'true')

        // then:
        assert Feature.isFeatureEnabled('feature.bar')
    }

    void testExecuteFeatureBlockIfEnabled() {
        // given:
        Feature.setFeatureEnabled('feature.foobar', false)
        boolean executed = false
        def block = { executed = true }

        // when:
        Feature.withFeature('feature.foobar', block)

        // then:
        assert !executed

        // when:
        Feature.setFeatureEnabled('feature.foobar', true)
        Feature.withFeature('feature.foobar', block)

        // then:
        assert executed
    }
}
