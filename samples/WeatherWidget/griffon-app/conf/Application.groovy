application {
    title='WeatherWidget'
    startupGroups = ['WeatherWidget']

    autoShutdown = false
}
mvcGroups {
    'SmallForecast' {
        model = 'SmallForecastModel'
        view = 'SmallForecastView'
    }

    'PrefsPanel' {
        model = 'PrefsPanelModel'
        view = 'PrefsPanelView'
        controller = 'PrefsPanelController'
    }

    'WeatherWidget' {
        model = 'WeatherWidgetModel'
        controller = 'WeatherWidgetController'
        view = 'WeatherWidgetView'
    }

}
