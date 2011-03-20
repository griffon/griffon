/*
 * Copyright 2009-2011 the original author or authors.
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

package org.codehaus.griffon.test.support

import griffon.core.GriffonApplication

class GriffonTestInterceptor {
    private test
    private mode
    private app
    private testClassSuffixes

    GriffonTestInterceptor(Object test, GriffonTestMode mode, GriffonApplication app, String[] testClassSuffixes) {
        this.test = test
        this.mode = mode
        this.app = app
        this.testClassSuffixes = testClassSuffixes
    }

    void init() {
        autowireIfNecessary()
    }

    void destroy() {
    }

    void wrap(Closure body) {
        init()
        try {
            body()
        } finally {
            destroy()
        }
    }

    protected autowireIfNecessary() {
        if (mode.autowire) createAutowirer().autowire(test)
    }

    protected getControllerName() {
        ControllerNameExtractor.extractControllerNameFromTestClassName(test.class.name, testClassSuffixes)
    }

    protected createAutowirer() {
        new GriffonTestAutowirer(app)
    }
}
