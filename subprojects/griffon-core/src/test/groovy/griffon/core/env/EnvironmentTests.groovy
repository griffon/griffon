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
package griffon.core.env

public class EnvironmentTests extends GroovyTestCase {

    protected void tearDown() {
        System.setProperty(Environment.KEY, '')

        Metadata.getCurrent().clear()
    }

    void testGetCurrent() {
        System.setProperty('griffon.env', 'prod')

        assert Environment.PRODUCTION == Environment.getCurrent()

        System.setProperty('griffon.env', 'dev')

        assert Environment.DEVELOPMENT == Environment.getCurrent()

        System.setProperty('griffon.env', 'soe')

        assert Environment.CUSTOM == Environment.getCurrent()

    }

    void testGetEnvironment() {
        assert Environment.DEVELOPMENT == Environment.getEnvironment('dev')
        assert Environment.TEST == Environment.getEnvironment('test')
        assert Environment.PRODUCTION == Environment.getEnvironment('prod')
        assert !Environment.getEnvironment('doesntexist')
    }

    void testSystemPropertyOverridesMetadata() {
        Metadata.getInstance(new ByteArrayInputStream('griffon.env=production'.bytes))

        assert Environment.PRODUCTION == Environment.getCurrent()

        System.setProperty('griffon.env', 'dev')

        assert Environment.DEVELOPMENT == Environment.getCurrent()

        System.setProperty('griffon.env', '')

        assert Environment.PRODUCTION == Environment.getCurrent()

        Metadata.getInstance(new ByteArrayInputStream(''.bytes))

        assert Environment.DEVELOPMENT == Environment.getCurrent()
    }
}
