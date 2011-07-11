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
    tabbedPane(constraints: 'grow, wrap') {
        scrollPane(title: app.getMessage('application.dialog.Credits.writtenby', 'Written by'), constraints: 'grow') {
            textArea(editable: false, text: bind{ model.credits },
                caretPosition: bind('credits', source: model, converter: {0i}))
        }
    }   
    button(closeAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: closeAction)
}
