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

package griffon.samples.groovyfxpad

/**
 * @author Andres Almiray
 */
class CreditsModel extends AbstractDialogModel {
    @Bindable String credits
    
    private static String CREDITS = null

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        resizable = false
        credits = fetchCreditsText()
    }

    protected String getDialogKey() { 'Credits' }
    protected String getDialogTitle() { 'Credits' }

    @groovy.transform.Synchronized
    static fetchCreditsText() {
        if(CREDITS == null) {
            try {
                CREDITS = getClass().getResource('/credits.txt').text
            } catch(x) {
                CREDITS = ''
            }
        }
        CREDITS
    }
}
