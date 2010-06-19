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

package org.codehaus.griffon.test.support

class GriffonTestInterceptor {
    private test
    private testClassSuffixes

    GriffonTestInterceptor(Object test, String[] testClassSuffixes) {
        this.test = test
        this.testClassSuffixes = testClassSuffixes
    }

    void init() {
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

    protected getControllerName() {
        ControllerNameExtractor.extractControllerNameFromTestClassName(test.class.name, testClassSuffixes)
    }
}