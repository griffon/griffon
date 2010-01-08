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

import java.beans.PropertyChangeListener

/**
 * @author Danno Ferrin
 */
class UserPaneController {
    // these will be injected by Griffon
    UserPaneModel model
    UserPaneView view

    TimelinePaneController timelinePaneController

    TwitterService twitterService

    void mvcGroupInit(Map args) {
        twitterService = app.controllers.Greet.twitterService
        def timelinePane = buildMVCGroup('TimelinePane', "TimelinePane_user_$args.user.screen_name");
        timelinePaneController = timelinePane.controller

        timelinePane.controller.twitterService = twitterService
        timelinePane.model.tweetListGenerator = {TwitterService twitterService ->
            def thisScreenName = model.screenName
            def tweets = twitterService.tweetCache.values().findAll {
                ((model.showTweets && (it.user.screen_name == thisScreenName))
                || (model.showReplies && (it.in_reply_to_screen_name == thisScreenName)))
            }
            tweets.addAll (twitterService.dmCache.values().findAll {
                model.showDirectMessages && ((it.sender_screen_name == thisScreenName)
                || (it.recipient_screen_name == thisScreenName))
            })
            tweets.sort( {a, b-> b.created_at <=> a.created_at}).collect { it.id }
        }

        view.timelinePane = timelinePane.view.timeline
    }

    def updateTimeline(evt) {
        timelinePaneController.updateTimeline(evt)
    }

    def close(evt) {
        view.userPane.parent.remove(view.userPane)
        destroyMVCGroup("UserPane_$model.user.screen_name")
        destroyMVCGroup("TimelinePane_user_$model.user.screen_name")
    }

    def follow(evt) {
        model.busy = true
        String userID = model.user.id
        doOutside {
            try {
                twitterService.follow(userID)
            } finally {
                edt {
                    model.following = twitterService.currentUserFollows(userID)
                    model.busy = false
                }
            }
        }
    }

    def unfollow(evt) {
      model.busy = true
      String userID = model.user.id
      doOutside {
          try {
              twitterService.unfollow(userID)
          } finally {
              edt {
                  model.following = twitterService.currentUserFollows(userID)
                  model.busy = false
              }
          }
      }
    }

    def directMessage(evt) {
        app.models.Greet.sendingDM = true
        app.models.Greet.targetUser = model.user.screen_name
    }
}
