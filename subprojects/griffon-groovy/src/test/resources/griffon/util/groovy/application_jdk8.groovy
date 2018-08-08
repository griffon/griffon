package griffon.util.groovy

application(title: app.config.application.title, pack: true) {
    actions {
        action(name: 'Click', id: 'clickAction', closure: { controller.click(it) })
    }
    gridLayout(rows: 3, cols: 1)
    textField(columns: 20, text: bind('value', target: model), id: 'input')
    textField(editable: false, columns: 20, text: bind { model.value }, id: 'output')
    button(action: clickAction)
}