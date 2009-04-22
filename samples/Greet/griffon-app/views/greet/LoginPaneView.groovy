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