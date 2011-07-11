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
    panel(constraints: 'grow, wrap')
    button(closeAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: closeAction)
}
