import java.util.prefs.Preferences

class PrefsPanelController {
    // these will be injected by Griffon
    PrefsPanelModel model
    PrefsPanelView view

    def doOK = { evt = null ->
        app.models.WeatherWidget.location = model.location
        app.models.WeatherWidget.celsius = model.isCelsius
        app.controllers.WeatherWidget.updateWeather()
        Preferences.userNodeForPackage(PrefsPanelController).put("WeatherLocation", model.location)
        doCancel(evt)
    }

    def doCancel = { evt = null ->
        view.prefsPanel.dispose()
        destroyMVCGroup('PrefsPanel')
    }
}