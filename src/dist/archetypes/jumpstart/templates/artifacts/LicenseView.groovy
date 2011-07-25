@artifact.package@actions {
    action(hideAction,
       name: app.getMessage('application.action.Close.name', 'Close'),
       mnemonic: app.getMessage('application.action.Close.mnemonic', 'C'),
       shortDescription: app.getMessage('application.action.Close.short_description', 'Close')
    )
}

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    scrollPane(constraints: 'grow, wrap') {
        textArea(editable: false, text: bind{ model.license },
            caretPosition: bind('license', source: model, converter: {0i}))
    } 
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: hideAction)
}
