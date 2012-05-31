/*
 * Copyright 2012 the original author or authors.
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

package fx.browser

import griffon.transform.Threading

/**
 * @author Andres Almiray
 */
class FxBrowserController {
    def model
    def view

    void mvcGroupInit(Map<String, Object> args) {
        execInsideUIAsync {
            view.urlField.text = 'http://griffon-framework.org'
            openUrl()
        }
    }

    @Threading(Threading.Policy.SKIP)
    def backAction = {
        String url = model.getPreviousUrl()
        if (url) {
            view.browser.engine.load(url)
            view.urlField.text = url
        }
    }

    @Threading(Threading.Policy.SKIP)
    def forwardAction = {
        String url = model.getNextUrl()
        if (url) {
            view.browser.engine.load(url)
            view.urlField.text = url
        }
    }

    @Threading(Threading.Policy.SKIP)
    def reloadAction = {
        view.browser.engine.reload()
    }

    @Threading(Threading.Policy.SKIP)
    def openUrl = {
        String url = model.url
        if (url.indexOf('://') < 0) url = 'http://' + url
        if (view.browser.engine.location == url) return
        view.browser.engine.load(url)
        model.addToHistory(url)
    }
}
