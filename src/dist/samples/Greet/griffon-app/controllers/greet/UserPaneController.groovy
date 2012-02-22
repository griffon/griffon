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

/**
 * @author Danno Ferrin
 */
class UserPaneController {
    // these will be injected by Griffon
    UserPaneModel model
    UserPaneView view

    TimelinePaneController timelinePaneController

    MicroblogService microblogService

    void mvcGroupInit(Map args) {
        def timelinePane = buildMVCGroup('TimelinePane', "TimelinePane_user_$args.user.screenName");
        timelinePaneController = timelinePane.controller

        timelinePane.model.tweetListGenerator = {MicroblogService microblogService ->
            def thisScreenName = model.screenName
            def tweets = microblogService.tweetCache.values().findAll {
                ((model.showTweets && (it.user.screenName == thisScreenName))
                || (model.showReplies && (it.inReplyToScreenName == thisScreenName)))
            }
            tweets.addAll (microblogService.directMessageCache.values().findAll {
                model.showDirectMessages && ((it.senderScreenName == thisScreenName)
                || (it.recipientScreenName == thisScreenName))
            })
            tweets.sort( {a, b-> b.createdAt <=> a.createdAt}).collect { it.id }
        }

        view.timelinePane = timelinePane.view.timeline
    }

    def updateTimeline(evt) {
        timelinePaneController.updateTimeline(evt)
    }

    def close(evt) {
        view.userPane.parent.remove(view.userPane)
        destroyMVCGroup("UserPane_$model.user.screenName")
        destroyMVCGroup("TimelinePane_user_$model.user.screenName")
    }

    def follow(evt) {
        model.busy = true
        String userID = model.user.id
        doOutside {
            try {
                microblogService.follow(userID)
            } finally {
                edt {
                    model.following = microblogService.currentUserFollows(userID)
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
              microblogService.unfollow(userID)
          } finally {
              edt {
                  model.following = microblogService.currentUserFollows(userID)
                  model.busy = false
              }
          }
      }
    }

    def directMessage(evt) {
        app.models.Greet.sendingDM = true
        app.models.Greet.targetUser = model.user.screenName
    }
}
