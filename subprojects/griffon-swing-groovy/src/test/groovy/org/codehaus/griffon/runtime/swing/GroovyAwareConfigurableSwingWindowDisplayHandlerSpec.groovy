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
package org.codehaus.griffon.runtime.swing

import griffon.core.test.GriffonUnitRule
import griffon.core.view.WindowManager
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import javax.swing.JFrame

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * @author Andres Almiray
 * @since 2.0.0
 */

class GroovyAwareConfigurableSwingWindowDisplayHandlerSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    private static final String MAIN_WINDOW = 'mainWindow'
    private static final String DISPLAYED = 'displayed'

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private WindowManager windowManager

    void 'Show a window using configured closures as handlers'() {
        given:
        JFrame window = new JFrame(MAIN_WINDOW)
        windowManager.attach(MAIN_WINDOW, window)
        assert !window.rootPane.getClientProperty(DISPLAYED)

        when:
        windowManager.show(MAIN_WINDOW)
        await().atMost(2, SECONDS)

        then:
        window.rootPane.getClientProperty(DISPLAYED) == 'true'
    }

    void 'Hide a window using configured closures as handlers'() {
        given:
        JFrame window = new JFrame(MAIN_WINDOW)
        windowManager.attach(MAIN_WINDOW, window)
        assert !window.rootPane.getClientProperty(DISPLAYED)

        when:
        windowManager.show(MAIN_WINDOW)
        await().until { window.visible }
        windowManager.hide(MAIN_WINDOW)
        await().until { !window.visible }

        then:
        window.rootPane.getClientProperty(DISPLAYED) == 'false'
    }
}
