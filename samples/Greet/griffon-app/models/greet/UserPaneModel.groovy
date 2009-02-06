package greet

import groovy.beans.Bindable

class UserPaneModel {
    Map user
    String screenName
    @Bindable boolean busy
    @Bindable boolean following
    @Bindable boolean follows
    @Bindable boolean showTweets
    @Bindable boolean showReplies
    @Bindable boolean showDirectMessages
}