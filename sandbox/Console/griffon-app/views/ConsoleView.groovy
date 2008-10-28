consoleWindow = application(title:'Console', pack:true, locationByPlatform:true) {
    menuBar {
        menu("File") {
            menuItem("New Window", actionPerformed:controller.&newWindow)
            menuItem("Close Window", actionPerformed:{consoleWindow.dispose()})
        }
    }

    panel(border:emptyBorder(6)) {
        borderLayout()

        scrollPane(constraints:CENTER) {
            textArea(text:bind(target:model, targetProperty:'scriptSource'),
                enabled: bind {model.enabled},
                columns:40, rows:10)
        }

        hbox(constraints:SOUTH) {
            button("Execute", actionPerformed:controller.&executeScript,
                enabled: bind {model.enabled})
            hstrut(5)
            label("Result:")
            hstrut(5)
            label(text:bind {model.scriptResult})
        }
    }
}
