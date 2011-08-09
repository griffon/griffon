/* 
 * Copyright 2010-2011 the original author or authors.
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

import griffon.swing.SwingApplication
import java.util.concurrent.CountDownLatch
import org.codehaus.griffon.runtime.core.AbstractGriffonApplication

/**
 * Customizable implementation og {@code SwingApplication} useful for testing.<p>
 * You can override the values of all config classes before {@code initialize()} is called.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
class MockSwingGriffonApplication extends SwingApplication {
    /** Defaults to {@code griffon.test.mock.MockApplication} */
    Class appConfigClass = MockApplication
    /** Defaults to {@code griffon.test.mock.MockConfig} */
    Class configClass = MockConfig
    /** Defaults to {@code griffon.test.mock.MockBuilderConfig} */
    Class builderClass = MockBuilderConfig
    /** Defaults to {@code griffon.test.mock.MockEvents} */
    Class eventsClass = MockEvents

    MockSwingGriffonApplication(String[] args = AbstractGriffonApplication.EMPTY_ARGS) {
        super(args)
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

    void bootstrap() {}
    
    void realize() {
        CountDownLatch latch = new CountDownLatch(1)
        execOutside {
            super.bootstrap()
            latch.countDown()
        }
        latch.await()
        super.realize()
    }
}
