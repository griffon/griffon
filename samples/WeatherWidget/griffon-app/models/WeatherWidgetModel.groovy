import groovy.beans.Bindable
import java.util.prefs.Preferences

@Bindable class WeatherWidgetModel extends AbstractForecastModel {
    String location = Preferences.userNodeForPackage(PrefsPanelController).get("WeatherLocation", "MSY")
    String locationName = "\u00a0"
    boolean celsius = false
}