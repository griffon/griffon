import groovy.beans.Bindable

class ConsoleModel {

    String scriptSource
    @Bindable def scriptResult
    @Bindable boolean enabled = true

}
