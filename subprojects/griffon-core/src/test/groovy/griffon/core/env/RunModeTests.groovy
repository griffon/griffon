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

class RunModeTests extends GroovyTestCase {
    protected void setUp() {
        System.clearProperty(RunMode.KEY)
    }

    protected void tearDown() {
        System.clearProperty(RunMode.KEY)
    }

    void testIsSystemSet() {
        assert !RunMode.isSystemSet()
        System.setProperty(RunMode.KEY, 'STANDALONE')
        assert RunMode.isSystemSet()
    }

    void testGetCurrent() {
        assert RunMode.current == RunMode.STANDALONE

        System.setProperty(RunMode.KEY, 'STANDALONE')
        assert RunMode.current == RunMode.STANDALONE

        System.setProperty(RunMode.KEY, 'WEBSTART')
        assert RunMode.current == RunMode.WEBSTART

        System.setProperty(RunMode.KEY, 'APPLET')
        assert RunMode.current == RunMode.APPLET

        System.setProperty(RunMode.KEY, 'TEST RUN MODE')
        assert RunMode.current == RunMode.CUSTOM
        assert RunMode.current.name == 'TEST RUN MODE'
    }

    void testGetRunMode() {
        assert RunMode.STANDALONE == RunMode.getRunMode('standalone')
        assert RunMode.WEBSTART == RunMode.getRunMode('webstart')
        assert RunMode.APPLET == RunMode.getRunMode('applet')
        assert !RunMode.getRunMode('custom')
    }
}
