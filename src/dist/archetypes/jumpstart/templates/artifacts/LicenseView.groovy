@artifact.package@actions {
    action(id: 'closeAction',
       name: app.getMessage('application.action.Close.name', 'Close'),
       closure: controller.hide,
       mnemonic: app.getMessage('application.action.Close.mnemonic', 'C'),
       shortDescription: app.getMessage('application.action.Close.description', 'Close')
    )
}

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    scrollPane(constraints: 'grow, wrap') {
        textArea(editable: false, text: bind{ model.license },
            caretPosition: bind('license', source: model, converter: {0i}))
    } 
    button(closeAction, constraints: 'right')
}
