/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
        action(id: 'clickAction', name: 'Click', closure: { controller.click(it) })
    }
    gridLayout(cols: 1, rows: 3)
    textField(id: 'input', text: bind('value', target: model), columns: 20)
    textField(id: 'output', text: bind { model.value }, columns: 20, editable: false)
    button(action: clickAction)
}