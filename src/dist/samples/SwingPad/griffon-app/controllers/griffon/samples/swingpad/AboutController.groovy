/*
 * Copyright 2007-2012 the original author or authors.
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
 */

package griffon.samples.swingpad

import griffon.transform.Threading

import javax.swing.SwingUtilities

/**
 * @author Andres Almiray
 */
class AboutController extends DialogController {
    def view

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def credits = {
        def window = SwingUtilities.windowForComponent(view.content)
        withMVCGroup('credits') { m, v, c -> c.show(window) }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def license = {
        def window = SwingUtilities.windowForComponent(view.content)
        withMVCGroup('license') { m, v, c -> c.show(window) }
    }
}
