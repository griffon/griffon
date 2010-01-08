/*
 * Copyright 2008-2010 the original author or authors.
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

/**
 * @author Danno Ferrin
 */

package greet

loginPanel = scrollPane(border: null) {
    panel(border: emptyBorder(3)) {
        gridBagLayout()

        label("Welcome to Greet!",
            gridwidth: REMAINDER, insets: [3, 3, 15, 3]
        )

        label("Username:",
            anchor: EAST, insets: [3, 3, 3, 3])
        twitterNameField = textField(action: loginAction,
            text: bind(target: model, 'loginUser', value: model.loginUser),
            gridwidth: REMAINDER, fill: HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])

        label("Password:",
            anchor: EAST, insets: [3, 3, 3, 3])
        twitterPasswordField = passwordField(action: loginAction,
            text: bind(target: model, 'loginPassword', value: ''),
            gridwidth: REMAINDER, fill: HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])

        label("Service:",
                anchor: EAST, insets: [3, 3, 3, 3])
        twitterServiceComboBox = comboBox(items: ["http://twitter.com", "http://identi.ca/api"], editable: true,
            selectedItem: bind(target: model, 'serviceURL', value: model.serviceURL),
            enabled: bind {twitterNameField.enabled},
            gridwidth: REMAINDER, fill: HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])
        twitterServiceComboBox.editor.editorComponent.action = loginAction

        panel()
        button(loginAction,
            gridwidth: REMAINDER, anchor: EAST, insets: [3, 3, 15, 3])

        panel(gridwidth: REMAINDER, weighty: 1.0, size: [0, 0]) // spacer
    }
}
