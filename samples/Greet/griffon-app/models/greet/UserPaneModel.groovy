package greet

import groovy.beans.Bindable
import java.beans.PropertyChangeListener

class UserPaneModel {
    Map user
    String screenName
    @Bindable boolean busy
    @Bindable boolean following
    @Bindable boolean follows
    @Bindable boolean showTweets
    @Bindable boolean showReplies
    @Bindable boolean showDirectMessages

    void mvcGroupInit(Map args) {
        TwitterService twitterService = app.controllers.Greet.twitterService

        user = args.user
        screenName = user.screen_name
        following = twitterService.currentUserFollows(args.user.id)
        follows = twitterService.follows(args.user.id, twitterService.authenticatedUser.id)

        addPropertyChangeListener("showTweets", args.controller.&updateTimeline as PropertyChangeListener)
        addPropertyChangeListener("showReplies", args.controller.&updateTimeline as PropertyChangeListener)
        addPropertyChangeListener("showDirectMessages", args.controller.&updateTimeline as PropertyChangeListener)
    }
}