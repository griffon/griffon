/*
 * Copyright 2008-2016 the original author or authors.
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

import org.codehaus.griffon.runtime.core.env.RunModeProvider

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
        assert getCurrentRunMode() == RunMode.STANDALONE

        System.setProperty(RunMode.KEY, 'STANDALONE')
        assert getCurrentRunMode() == RunMode.STANDALONE

        System.setProperty(RunMode.KEY, 'WEBSTART')
        assert getCurrentRunMode() == RunMode.WEBSTART

        System.setProperty(RunMode.KEY, 'APPLET')
        assert getCurrentRunMode() == RunMode.APPLET

        System.setProperty(RunMode.KEY, 'TEST RUN MODE')
        assert getCurrentRunMode() == RunMode.CUSTOM
        assert getCurrentRunMode().name == 'TEST RUN MODE'
    }

    void testGetRunMode() {
        assert RunMode.STANDALONE == RunMode.resolveRunMode('standalone')
        assert RunMode.WEBSTART == RunMode.resolveRunMode('webstart')
        assert RunMode.APPLET == RunMode.resolveRunMode('applet')
        assert !RunMode.resolveRunMode('custom')
    }

    private static RunMode getCurrentRunMode() {
        return new RunModeProvider().get()
    }
}
