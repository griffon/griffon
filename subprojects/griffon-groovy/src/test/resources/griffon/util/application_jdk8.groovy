/*
 * Copyright 2008-2016 the original author or authors.
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
application(title: app.config.application.title, pack: true) {
    actions {
        action(name: 'Click', id: 'clickAction', closure: { controller.click(it) })
    }
    gridLayout(rows: 3, cols: 1)
    textField(columns: 20, text: bind('value', target: model), id: 'input')
    textField(editable: false, columns: 20, text: bind { model.value }, id: 'output')
    button(action: clickAction)
}