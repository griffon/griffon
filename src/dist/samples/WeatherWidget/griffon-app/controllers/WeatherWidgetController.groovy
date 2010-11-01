/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Danno Ferrin
 */
class WeatherWidgetController {

    WeatherWidgetModel model
    WeatherWidgetView view

    void mvcGroupInit(Map args) {
        createMVCGroup('SmallForecast', 'small1')
        createMVCGroup('SmallForecast', 'small2')
        createMVCGroup('SmallForecast', 'small3')
        createMVCGroup('SmallForecast', 'small4')

    }

    def updateWeather = {evt = null ->
        doOutside {
            def observationData = loadCurrentConditions(model.location)
            def forecastData = loadForecast(model.location)

            edt {
                model.current = Integer.parseInt(observationData[model.celsius?'temp_c':'temp_f'] as String)
                model.locationName = observationData.observation_location.full

                def today = forecastData.simpleforecast.forecastday[0]

                model.low = Integer.parseInt(today.low[model.celsius?'celsius':'fahrenheit'] as String)
                model.high = Integer.parseInt(today.high[model.celsius?'celsius':'fahrenheit'] as String)
                model.state = today.icon

                (1..4).each {
                    def day = forecastData.simpleforecast.forecastday[it]
                    def smallModel = app.models["small$it"]

                    smallModel.day = day.date.weekday
                    smallModel.low = Integer.parseInt(day.low[model.celsius?'celsius':'fahrenheit'] as String)
                    smallModel.high = Integer.parseInt(day.high[model.celsius?'celsius':'fahrenheit'] as String)
                    smallModel.state = day.icon
                }
            }
        }
    }

    def loadForecast(String location) {
        XmlSlurper slurper = new XmlSlurper()
        def text = new URL("http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query=$location").openStream().text
        return slurper.parse(new StringReader(text))
    }

    def loadCurrentConditions(String location) {
        XmlSlurper slurper = new XmlSlurper()
        def text = new URL("http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=$location").openStream().text
        return slurper.parse(new StringReader(text))
    }



    def showPreferences = {
        if (app.views.PrefsPanel == null) {
            createMVCGroup('PrefsPanel', parent:view.hw.JDialog)
        }
    }
}
