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
    }

    void testSystemPropertyHasPrecedenceOverMetadata() {
        // given:
        Feature feature = new Feature()
        feature.@metadata = new Metadata(new ByteArrayInputStream('feature.foo=true\nfeature.bar=false\n'.bytes))
        System.setProperty('feature.foobar', 'true')

        // expect:
        assert feature.isFeatureEnabled('feature.foobar')
        assert feature.isFeatureEnabled('feature.foo')
        assert !feature.isFeatureEnabled('feature.bar')

        // when:
        System.setProperty('feature.bar', 'true')

        // then:
        assert feature.isFeatureEnabled('feature.bar')
    }

    void testExecuteFeatureBlockIfEnabled() {
        // given:
        Feature feature = new Feature()
        feature.setFeatureEnabled('feature.foobar', false)
        boolean executed = false
        def block = { executed = true }

        // when:
        feature.withFeature('feature.foobar', block)

        // then:
        assert !executed

        // when:
        feature.setFeatureEnabled('feature.foobar', true)
        feature.withFeature('feature.foobar', block)

        // then:
        assert executed
    }
}
