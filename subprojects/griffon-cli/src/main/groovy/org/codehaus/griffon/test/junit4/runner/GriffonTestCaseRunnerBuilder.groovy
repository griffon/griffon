/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.codehaus.griffon.test.junit4.runner

import griffon.core.GriffonApplication
import org.codehaus.griffon.test.GriffonTestTargetPattern
import org.codehaus.griffon.test.support.GriffonTestMode
import org.junit.runner.Runner
import org.junit.runners.model.RunnerBuilder

class GriffonTestCaseRunnerBuilder extends RunnerBuilder {
    final mode
    final app
    final testTargetPatterns

    GriffonTestCaseRunnerBuilder(GriffonTestTargetPattern[] testTargetPatterns) {
        this(null, null, testTargetPatterns)
    }

    GriffonTestCaseRunnerBuilder(GriffonTestMode mode, GriffonApplication app, GriffonTestTargetPattern[] testTargetPatterns) {
        this.mode = mode
        this.app = app
        this.testTargetPatterns = testTargetPatterns
        validateMode()
    }

    protected validateMode() {
        if (mode && app == null) {
            throw new IllegalStateException("mode $mode requires an application")
        }
    }

    Runner runnerForClass(Class testClass) {
        if (mode) {
            new GriffonTestCaseRunner(testClass, mode, app, * testTargetPatterns)
        } else {
            new GriffonTestCaseRunner(testClass, * testTargetPatterns)
        }
    }
}
