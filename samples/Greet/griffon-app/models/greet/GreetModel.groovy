package greet

import groovy.beans.Bindable

class GreetModel {
    @Bindable String primaryUser
    @Bindable boolean tweeting = true
    @Bindable boolean sendingDM = false
    @Bindable String targetUser
    @Bindable String targetTweet
    @Bindable boolean refreshing = true
    @Bindable String statusLine = ""
}