package griffon.util.groovy

application(title: app.config.application.title, pack: true) {
    actions {
        action(id: 'clickAction', name: 'Click', closure: { controller.click(it) })
    }
    gridLayout(cols: 1, rows: 3)
    textField(id: 'input', text: bind('value', target: model), columns: 20)
    textField(id: 'output', text: bind { model.value }, columns: 20, editable: false)
    button(action: clickAction)
}