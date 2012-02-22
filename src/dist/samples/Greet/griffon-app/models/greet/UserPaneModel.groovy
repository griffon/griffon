/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package greet

import groovy.beans.Bindable
import java.beans.PropertyChangeListener
import twitter4j.User

/**
 * @author Danno Ferrin
 */
class UserPaneModel {
    User user
    String screenName
    @Bindable boolean busy
    @Bindable boolean following
    @Bindable boolean follows
    @Bindable boolean showTweets
    @Bindable boolean showReplies
    @Bindable boolean showDirectMessages

    void mvcGroupInit(Map args) {
        MicroblogService microblogService = app.controllers.Greet.microblogService

        user = args.user
        screenName = user.screenName
        following = microblogService.currentUserFollows(user.screenName)
        follows = microblogService.follows(user.screenName, microblogService.authenticatedUser.screenName)

        addPropertyChangeListener("showTweets", args.controller.&updateTimeline as PropertyChangeListener)
        addPropertyChangeListener("showReplies", args.controller.&updateTimeline as PropertyChangeListener)
        addPropertyChangeListener("showDirectMessages", args.controller.&updateTimeline as PropertyChangeListener)
    }
}
