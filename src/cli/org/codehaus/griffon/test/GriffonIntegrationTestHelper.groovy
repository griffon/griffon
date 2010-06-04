/*
 * Copyright 2009-2010 the original author or authors.
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

package org.codehaus.griffon.test

import junit.framework.TestSuite
import griffon.util.BuildSettings
import griffon.core.GriffonApplication

/**
 * @author Andres Almiray
 */
class GriffonIntegrationTestHelper extends DefaultGriffonTestHelper {
    GriffonApplication app

    GriffonIntegrationTestHelper(
            BuildSettings settings,
            ClassLoader parentLoader,
            Closure resourceResolver,
            GriffonApplication app) {
        super(settings, parentLoader, resourceResolver)
        this.app = app
    }

    TestSuite createTestSuite() {
        new GriffonTestSuite(this.app, this.testSuffix)
    }

    TestSuite createTestSuite(Class clazz) {
        new GriffonTestSuite(this.app, clazz, this.testSuffix)
    }
}
