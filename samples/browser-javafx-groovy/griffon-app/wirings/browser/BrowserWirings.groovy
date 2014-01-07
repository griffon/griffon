package browser

import javafx.beans.value.ChangeListener

// The following statements must be executed inside the UI thread
// as this script is built outside of the UI thread as it's not
// marked as a View script

application.getUIThreadManager().runInsideUIAsync { ->
    // use the (Closure as Type) notation in order to gain access to
    // binding variables. Anonymous class definition will fail as the
    // instance won't have access to builder binding variables

    browser.engine.loadWorker.messageProperty().addListener({ observableValue, oldValue, newValue ->
        builder.model.status = newValue
        // update history and textField value if a link
        // was clicked. Sadly this check is brittle as we
        // rely on 'parsing' the message value *sigh*
        if (newValue.startsWith('Loading http')) {
            String url = newValue[8..-1]
            builder.urlField.text = url
        }
    } as ChangeListener)

    browser.engine.loadWorker.progressProperty().addListener({ observableValue, oldValue, newValue ->
        if (newValue > 0.0d && newValue < 1.0d) {
            builder.model.status = "Loading: ${(newValue * 100).intValue()} %"
        }
    } as ChangeListener)
}