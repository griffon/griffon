/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.core.controller

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DefaultToolkitActionSpec extends Specification {
    void 'Create and execute an action'() {
        given:
        boolean invoked = false
        Runnable runnable = { -> invoked = true }
        DefaultToolkitAction action = new DefaultToolkitAction()
        action.runnable = runnable

        when:
        action.name = 'action'
        action.execute()

        then:
        invoked
        action.name == 'action'
        action.runnable == runnable
        action.toString() == 'Action[action]'
    }
}
