/* 
 * Copyright 2010-2014 the original author or authors.
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

import griffon.core.UIThreadManager
import griffon.util.UIThreadHandler
import org.codehaus.griffon.runtime.core.AbstractGriffonApplication

/**
 * Customizable implementation og {@code GriffonApplication} useful for testing.<p>
 * You can override the values of all config classes before {@code initialize()} is called.
 *
 * @author Andres Almiray
 */
class MockGriffonApplication extends AbstractGriffonApplication {
    Closure applicationContainerGenerator

    /** Defaults to {@code griffon.test.mock.MockApplication} */
    Class appConfigClass = MockApplication
    /** Defaults to {@code griffon.test.mock.MockConfig} */
    Class configClass = MockConfig
    /** Defaults to {@code griffon.test.mock.MockBuilderConfig} */
    Class builderClass = MockBuilderConfig
    /** Defaults to {@code griffon.test.mock.MockEvents} */
    Class eventsClass = MockEvents
    UIThreadHandler uiThreadHandler

    MockGriffonApplication() {
        super()
    }

    MockGriffonApplication(String[] args) {
        super(args)
    }

    /**
     * Returns the value form the execution of {@code applicationContainerGenerator} or an empty map
     */
    Object createApplicationContainer() {
        applicationContainerGenerator?.call() ?: [:]
    }

    void setUiThreadHandler(UIThreadHandler uiThreadHandler) {
        UIThreadManager.instance.setUIThreadHandler(uiThreadHandler)
    }

    Class getAppConfigClass() {
        this.@appConfigClass
    }

    Class getConfigClass() {
        this.@configClass
    }

    Class getBuilderClass() {
        this.@builderClass
    }

    Class getEventsClass() {
        this.@eventsClass
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
