@artifact.package@actions {
    action(hideAction,
       name: app.getMessage('application.action.Close.name', 'Close'),
       mnemonic: app.getMessage('application.action.Close.mnemonic', 'C'),
       shortDescription: app.getMessage('application.action.Close.short_description', 'Close')
    )
}

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    tabbedPane(constraints: 'grow, wrap') {
        scrollPane(title: app.getMessage('application.dialog.Credits.writtenby', 'Written by')) {
            textArea(editable: false, text: bind{ model.credits },
                caretPosition: bind('credits', source: model, converter: {0i}))
        }
    }   
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: hideAction)
}
