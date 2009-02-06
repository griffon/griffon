package greet

loginPanel = scrollPane(border: null) {
    panel(border: emptyBorder(3)) {
        gridBagLayout()

        label("Welcome to Greet!",
            gridwidth: REMAINDER, insets: [3, 3, 15, 3]
        )

        label("Username:",
            anchor: EAST, insets: [3, 3, 3, 3])
        twitterNameField = textField(action: controller.loginAction,
            text: bind(target: model, targetProperty: 'loginUser', value: model.loginUser),
            gridwidth: REMAINDER, fill: HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])

        label("Password:",
            anchor: EAST, insets: [3, 3, 3, 3])
        twitterPasswordField = passwordField(action: controller.loginAction,
            text: bind(target: model, targetProperty: 'loginPassword', value: ''),
            gridwidth: REMAINDER, fill: HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])

        label("Service:",
                anchor: EAST, insets: [3, 3, 3, 3])
        twitterServiceComboBox = comboBox(items: ["http://twitter.com", "http://identi.ca/api"], editable: true,
            selectedItem: bind(target: model, targetProperty: 'serviceURL', value: model.serviceURL),
            enabled: bind {twitterNameField.enabled},
            gridwidth: REMAINDER, fill: HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])
        twitterServiceComboBox.editor.editorComponent.action = controller.loginAction

        panel()
        button(controller.loginAction, //defaultButton: true,
            gridwidth: REMAINDER, anchor: EAST, insets: [3, 3, 15, 3])

        panel(gridwidth: REMAINDER, weighty: 1.0, size: [0, 0]) // spacer
    }
}