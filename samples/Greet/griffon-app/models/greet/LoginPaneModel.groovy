package greet

import groovy.beans.Bindable
import java.util.prefs.Preferences

class LoginPaneModel {
    @Bindable boolean loggingIn = true
    @Bindable String loginUser
    @Bindable String loginPassword
    @Bindable String serviceURL

    void mvcGroupInit(Map args) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass())
        loginUser = prefs.get("user", "");
        serviceURL = prefs.get("serviceURL", "http://twitter.com")
    }

}