@artifact.package@actions {
    action(id: 'cancelAction',
       name: 'Cancel',
       closure: controller.hide,
       mnemonic: 'C',
       shortDescription: 'Cancel'
    )
    action(id: 'okAction',
       name: 'Ok',
       closure: controller.hide,
       mnemonic: 'K',
       shortDescription: 'Ok'
    )
}

panel(id: 'content') {
    borderLayout()
    label('Content goes here', constraints: CENTER)
    panel(constraints: SOUTH) {
        gridLayout(cols: 2, rows: 1)
        button(cancelAction)
        button(okAction)
    }
    
    keyStrokeAction(component: current,
        keyStroke: "ESCAPE",
        condition: "in focused window",
        action: cancelAction)
}
