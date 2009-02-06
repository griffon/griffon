package greet

import groovy.beans.Bindable

class LoginPaneModel {
    @Bindable boolean loggingIn = true
    @Bindable String loginUser
    @Bindable String loginPassword
    @Bindable String serviceURL
}