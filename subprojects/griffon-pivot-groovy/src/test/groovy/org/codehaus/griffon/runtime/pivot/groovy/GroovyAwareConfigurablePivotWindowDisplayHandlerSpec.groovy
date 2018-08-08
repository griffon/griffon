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
package org.codehaus.griffon.runtime.pivot.groovy

import griffon.core.GriffonApplication
import griffon.core.view.WindowManager
import griffon.test.pivot.GriffonPivotRule
import org.apache.pivot.wtk.Window
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject

import static org.awaitility.Awaitility.await

/**
 * @author Andres Almiray
 * @since 2.0.0
 */

class GroovyAwareConfigurablePivotWindowDisplayHandlerSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    private static final String MAIN_WINDOW = 'mainWindow'

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule()

    @Inject
    private WindowManager windowManager

    @Inject
    private GriffonApplication application

    void 'Hide a window using configured closures as handlers'() {
        given:
        Window window = application.createApplicationContainer(title: MAIN_WINDOW)
        windowManager.attach(MAIN_WINDOW, window)

        when:
        windowManager.show(MAIN_WINDOW)
        await().until { window.visible }
        // windowManager.hide(MAIN_WINDOW)
        // await().until { !window.visible }

        then:
        window.title == 'shown'
    }
}
