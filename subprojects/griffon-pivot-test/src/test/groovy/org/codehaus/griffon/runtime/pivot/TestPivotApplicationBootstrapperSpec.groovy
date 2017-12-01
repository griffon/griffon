/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.pivot

import griffon.core.GriffonApplication
import org.apache.pivot.wtk.ApplicationContext
import org.apache.pivot.wtk.Display
import org.codehaus.griffon.runtime.core.DefaultGriffonApplication
import org.codehaus.griffon.runtime.core.TestApplicationBootstrapper
import spock.lang.Specification

import javax.swing.SwingUtilities

class TestPivotApplicationBootstrapperSpec extends Specification {
    void 'Can register Display on Injector'() {
        given:
        Display display = createDisplay()
        GriffonApplication application = new DefaultGriffonApplication()

        when:
        TestApplicationBootstrapper bootstrapper = new TestPivotApplicationBootstrapper(application, display)
        bootstrapper.testCase = new Object()
        bootstrapper.bootstrap()

        then:
        application.injector.getInstance(Display) == display
    }

    public Display createDisplay() {
        Display display = null
        SwingUtilities.invokeAndWait {
            display = new Display(Stub(ApplicationContext.DisplayHost))
        }
        display
    }
}
