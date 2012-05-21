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

import groovyx.javafx.beans.FXBindable

/**
 * @author Andres Almiray
 */
class FxBrowserModel {
    @FXBindable String status = ''
    @FXBindable String url
    List history = []
    int index = -1
    
    String getPreviousUrl() {
        String previousUrl = null
        int previousIndex = index - 1
        if(previousIndex >= 0 && previousIndex < history.size()) {
            previousUrl = history[previousIndex]
            url = previousUrl
            index = previousIndex
        }
        previousUrl
    }
    
    String getNextUrl() {
        String nextUrl = null
        int nextIndex = index + 1
        if(nextIndex < history.size()) {
            nextUrl = history[nextIndex]
            url = nextUrl
            index = nextIndex
        }
        nextUrl
    }
    
    void addToHistory(String url) {
        if(!history) {
            history << url
            index++
        } else if(history[index] != url){
            List tmp = history[0..index]
            tmp << url
            history = tmp
            index = history.size() - 1
        }
    }
}
