@artifact.package@actions {
    action(hideAction,
       name: app.getMessage('application.action.Close.name', 'Close'),
       mnemonic: app.getMessage('application.action.Close.mnemonic', 'C'),
       shortDescription: app.getMessage('application.action.Close.short_description', 'Close')
    )
}

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    panel(constraints: 'grow, wrap')
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: hideAction)
}
