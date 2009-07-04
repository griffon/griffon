import groovy.beans.Bindable

@Bindable class AbstractForecastModel {
    int low = 17
    int high = 35
    int current = 27
    String state = "clear"

}

