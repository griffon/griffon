import java.awt.event.ActionEvent
import griffon.util.GriffonApplicationHelper

class ConsoleController {

    GroovyShell shell = new GroovyShell()

    // these will be injected by Griffon
    def model
    def view

    def preferencesMVC
    def consoleName = "Console"

    def executeScript(ActionEvent evt = null) {
        model.enabled = false
        doOutside {
            def result
            try {
                result = shell.evaluate(model.scriptSource)
            } finally {
                edt {
                    model.enabled = true
                    model.scriptResult = result
                }
            }
        }
    }

    def newWindow(ActionEvent evy = null) {
        int num = 1
        while (app.views["Consoel$num"]) num++;
        def (m, v, c) = GriffonApplicationHelper.createMVCGroup(app, 'Console', "Console$num")

        c.consoleName = "Console$num"
	
        // copy our bindings over to the new shell
	    shell.context.variables.each {key, value -> c.shell.context.variables[key] = value }

	    // show the new MVC window
	    v.consoleFrame.show()
    }

    def prefernces(ActionEvent evt = null) {
        if (preferencesMVC == null) {
             preferencesMVC = GriffonApplicationHelper.createMVCGroup(
                app, 'TextEditorPreferences', "${consoleName}Prefs",
                [consoleFrame:view.consoleFrame, editorPane: view.editorPane])
        }
        def (m,v,c) = preferencesMVC
        c.startup()
    }
}
