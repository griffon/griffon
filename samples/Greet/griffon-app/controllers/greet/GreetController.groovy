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

import java.awt.Cursor
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.Timer
import javax.swing.event.HyperlinkEvent
import javax.swing.UIManager
import javax.swing.border.EmptyBorder
import java.awt.Insets

/**
 * @author Danno Ferrin
 */
class GreetController {
    // these will be injected by Griffon
    GreetModel model
    GreetView view

    TimelinePaneModel friendsTimelineModel

    def timelinePaneControllerQueue = []

    MicroblogService microblogService

    Timer refreshTimer
    long nextTimelineUpdate
    long nextRepliesUpdate

    void mvcGroupInit(Map args) {
        def loginPane = buildMVCGroup('LoginPane');
        view.loginPanel = loginPane.view.loginPanel

        refreshTimer = new Timer(120000, args.actions.refreshTweetsAction)
    }

    void refreshTweets(evt) {
        edt { refreshTimer.stop() }

        doOutside {
            try {
                long time = System.currentTimeMillis()
                boolean forceRefresh = !(evt.source instanceof Timer)

                // friends timeline
                if (forceRefresh
                    || (time > nextTimelineUpdate)
                ) {
                    def lastID = friendsTimelineModel.tweets.collect { it as long }.max() ?: '0'
                    def newTweets = microblogService.getFriendsTimeline(lastID as String, lastID == '0' ? 100 : 200).collect {it.id}
                    def cachedIDs = microblogService.tweetCache.keySet()
                    newTweets.addAll(friendsTimelineModel.tweets.findAll { cachedIDs.contains(it) })
                    friendsTimelineModel.tweets = newTweets
                    nextTimelineUpdate = time + 120000 // 2 minutes
                }

                // replies
                //DMs from/to
                if (forceRefresh
                    || (time > nextRepliesUpdate)
                ) {
                    microblogService.getReplies()
                    microblogService.getDirectMessages()
                    microblogService.getDirectMessagesSent()
                    nextRepliesUpdate = time + 360000 // 6 minutes
                }

                timelinePaneControllerQueue.each { it.updateTimeline(evt) }
            } finally {
                edt {
                    model.refreshing = false
                    refreshTimer.start()
                }
            }
        }
    }

    void tweet(evt) {
        model.tweeting = true
        doOutside {
            def cleanup = { model.tweeting = false }
            try {
                if (model.sendingDM) {
                    microblogService.sendDM(model.targetUser, view.tweetBox.text)
                } else  {
                    microblogService.tweet(view.tweetBox.text, model.targetTweet)
                }
                cleanup = {
                    view.tweetBox.text = ""
                    model.targetTweet = null
                    model.targetUser = null
                    model.sendingDM = false
                    model.tweeting = false
                }
                edt { refreshTweets(evt) }
            } finally {
                edt(cleanup)
            }
        }
    }


    def hyperlinkPressed(HyperlinkEvent evt) {
        switch (evt.getEventType()) {
            case HyperlinkEvent.EventType.ACTIVATED:
                displayURL(evt.URL)
                break;
            case HyperlinkEvent.EventType.ENTERED:
                evt.getSource().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
            case HyperlinkEvent.EventType.EXITED:
                evt.getSource().setCursor(null);
                break;
        }
    }

    def displayURL(URL url) {
        doOutside {
            if (url.toExternalForm() =~ 'http://twitter.com/\\w+') {
                selectUser(url.file.substring(1))
            } else {
                // TODO wire in the jnlp libs into the build
                ("javax.jnlp.ServiceManager" as Class).lookup('javax.jnlp.BasicService').showDocument(url)
            }
        }
    }

    def selectUser(String username) {
        doOutside {
            def mvcName = "UserPane_$username"
            if (app.views[mvcName]) {
                microblogService.getTweets(username)
                edt {
                    app.controllers[mvcName].updateTimeline(null)
                    view.tweetsTabbedPane.selectedComponent = app.views[mvcName].userPane
                }
            } else {
                microblogService.getUser(username)
                microblogService.getTweets(username)
                edt {
                    def userPane = buildMVCGroup('UserPane', mvcName,
                        user:microblogService.userCache[username], closable:true);

                    view.tweetsTabbedPane.addTab("@$username", userPane.view.userPane)

                    userPane.controller.updateTimeline(null)
                    doLater {
                        view.tweetsTabbedPane.selectedComponent = app.views[mvcName].userPane
                    }
                }
            }
        }
    }


    def selectReplyToTweet(ActionEvent evt) {
        String tweetID = evt.actionCommand
        def tweet = microblogService.tweetCache[tweetID]
        if ((tweet == null) || (model.targetTweet == tweetID)) {
            model.targetTweet = null
            model.targetUser = null
        } else {
            model.targetTweet = tweetID
            model.targetUser = tweet.user.screen_name
            view.tweetBox.text = "@$model.targetUser ${view.tweetBox.text.replaceAll("@$model.targetUser ", '')}"
        }
    }

    def showUser(ActionEvent evt) {
        selectUser(evt.actionCommand)
    }

    def showHyperlink(ActionEvent evt) {
        displayURL(new URL((evt.actionCommand =~ 'http://[^"]+')[0] as String))
    }

    static Closure marginButtonTweaker = {builder, node, attrs ->
        def insets = attrs.remove('contentMargin')
        if (insets) {
            node.margin = insets
            def border = node.border
            def borderInsets = border.getBorderInsets(node)

            node.margin = new Insets(insets[0]*2 - borderInsets.top,
                    insets[1]*2 - borderInsets.left,
                    insets[2]*2 - borderInsets.bottom,
                    insets[3]*2 - borderInsets.right )
        }
    }

    static Closure borderButtonTweaker = {builder, node, attrs ->
        def insets = attrs.remove('contentMargin')
        if (insets) {
            def borderInsets = node.border.getBorderInsets(node)
            node.border = new EmptyBorder(*insets)
        }
    }

    static Closure getButtonMarginDelegate() {
        switch (UIManager.getLookAndFeel().getName()) {
            case ~/.*[Nn]imbus/ :

                return borderButtonTweaker
            case ~/.*[Ww]indows/ :
                try {
                    return (('com.sun.java.swing.plaf.windows.XPStyle' as Class).getXP()
                        ? borderButtonTweaker
                        : marginButtonTweaker)
                } catch (Throwable e) {
                    return marginButtonTweaker
                }
            default:
                return marginButtonTweaker
        }
    }
}
