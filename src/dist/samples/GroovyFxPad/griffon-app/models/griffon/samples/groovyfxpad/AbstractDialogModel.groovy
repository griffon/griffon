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
abstract class AbstractDialogModel {
    @Bindable String title
    @Bindable int width = 0
    @Bindable int height = 0
    @Bindable boolean resizable = true
    @Bindable boolean modal = true

    protected abstract String getDialogKey()
    protected abstract String getDialogTitle()

    void mvcGroupInit(Map<String, Object> args) {
        title = GriffonNameUtils.capitalize(app.getMessage('application.dialog.'+dialogKey+'.title', dialogTitle))
    }
}
