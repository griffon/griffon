import griffon.util.IGriffonApplication

class WeatherWidgetTests extends GroovyTestCase {

    IGriffonApplication app;

    void testModelIsNotBlank() {
        assert app.models.WeatherWidget.locationName != ''
    }
}