package greet

import groovy.beans.Bindable
import java.util.prefs.Preferences

@Bindable class LoginPaneModel {
    boolean loggingIn = true
    String loginUser
    String loginPassword
    String serviceURL

    void mvcGroupInit(Map args) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass())
        loginUser = prefs.get("user", "");
        serviceURL = prefs.get("serviceURL", "http://twitter.com")
    }

}
