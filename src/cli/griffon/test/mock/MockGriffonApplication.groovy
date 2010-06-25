/* 
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.test.mock

import griffon.core.GriffonApplication
import griffon.core.BaseGriffonApplication
import griffon.util.UIThreadHelper
import griffon.util.UIThreadHandler

/**
 * @author Andres Almiray
 */
class MockGriffonApplication {
    @Delegate private final BaseGriffonApplication _base
    Closure applicationContainerGenerator

    Class appConfigClass = MockApplication
    Class configClass = MockConfig
    Class builderClass = MockBuilderConfig
    Class eventsClass = MockEvents
    UIThreadHandler uiThreadHandler

    MockGriffonApplication() {
        _base = new BaseGriffonApplication(this)
    }

    Object createApplicationContainer() {
        applicationContainerGenerator?.call() ?: [:]
    }

    void setUiThreadHanlder(UIThreadHandler uiThreadHandler) {
        UIThreadHelper.instance.setUIThreadHandler(uiThreadHandler)
    }

    Class getAppConfigClass() {
        this.appConfigClass
    }

    Class getConfigClass() {
        this.configClass
    }

    Class getBuilderClass() {
        this.builderClass
    }

    Class getEventsClass() {
        this.eventsClass
    }

    void bootstrap() {
        // empty
    }
 
    void realize() {
        // empty
    }
 
    void show() {
        // empty
    }
}
