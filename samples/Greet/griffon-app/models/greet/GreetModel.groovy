package greet

import groovy.beans.Bindable

class GreetModel {

    @Bindable boolean allowSelection = false
    @Bindable boolean allowTweet = true
    @Bindable def focusedUser = ""
    @Bindable def friends  = []
    @Bindable def tweets   = []
    @Bindable def replies  = []
    @Bindable def timeline = []
    @Bindable def statuses = []
    @Bindable long lastUpdate = 0
    @Bindable String statusLine
    
}