/*
 * Copyright 2007-2011 the original author or authors.
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

import org.fife.ui.rtextarea.SearchEngine

/**
 * @author Andres Almiray
 */
class FinderController extends DialogController {
    def findPreviousAction = {
        SearchEngine.find(app.views.SwingPad.codeEditor,
                          model.toFind,
                          false,
                          model.matchCase,
                          model.wholeWord,
                          model.regex)
    }

    def findNextAction = {
        SearchEngine.find(app.views.SwingPad.codeEditor,
                          model.toFind,
                          true,
                          model.matchCase,
                          model.wholeWord,
                          model.regex)
    }

    def replaceAction = {
        int matches = SearchEngine.replace(app.views.SwingPad.codeEditor,
                              model.toFind,
                              model.replaceWith,
                              true,
                              model.matchCase,
                              model.wholeWord,
                              model.regex)
        model.replaced = matches? "$matches replaced".toString() : ''
    }

    def replaceAllAction = {
        int matches = SearchEngine.replaceAll(app.views.SwingPad.codeEditor,
                              model.toFind,
                              model.replaceWith,
                              model.matchCase,
                              model.wholeWord,
                              model.regex)
        model.replaced = matches? "$matches replaced".toString() : ''
    }
}
