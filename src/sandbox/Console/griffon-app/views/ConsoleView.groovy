import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants

consoleFrame = application(title:'Console', pack:true, locationByPlatform:true) {
    menuBar {
        menu("File") {
            menuItem("New Window", actionPerformed:controller.&newWindow)
            menuItem("Prefeneces", actionPerformed:controller.&prefernces)
            menuItem("Close Window", actionPerformed:{consoleFrame.dispose()})
        }
    }

    panel(border:emptyBorder(6)) {
        borderLayout()

        scrollPane(constraints:CENTER) {
            editorPane = new RSyntaxTextArea()
            editorPane.restoreDefaultSyntaxHighlightingColorScheme()
            widget(editorPane, syntaxEditingStyle: SyntaxConstants.GROOVY_SYNTAX_STYLE,
                text:bind(target:model, targetProperty:'scriptSource'),
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
