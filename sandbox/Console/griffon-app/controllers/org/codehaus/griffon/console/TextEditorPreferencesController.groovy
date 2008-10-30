package org.codehaus.griffon.console

import java.awt.event.ActionEvent

class TextEditorPreferencesController {

    // these will be injected by Griffon
    def model
    def view
    def builder

    def startup() {
        model.readFromObject(builder.editorPane)
        view.prefsPane.show()
    }

    def okButtonPressed(ActionEvent evt = null) {
        applyButtonPressed(evt)
        cancelButtonPressed(evt)
    }

    def cancelButtonPressed(ActionEvent evt = null) {
        view.prefsPane.dispose()
    }

    def applyButtonPressed(ActionEvent evt = null) {
        model.applyToObject(builder.editorPane)
    }
}