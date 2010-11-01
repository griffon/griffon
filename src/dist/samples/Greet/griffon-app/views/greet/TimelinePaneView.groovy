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

/**
 * @author Danno Ferrin
 */

package greet

import java.awt.GridBagConstraints
import javax.swing.JComponent
import javax.swing.KeyStroke

greetController = app.controllers.Greet

tweetLineFont = new java.awt.Font("Ariel", 0, 12)
tweetTimeFont = new java.awt.Font("Ariel", 0, 9)

timeline = scrollPane {
    timelinePanel = panel(new ScrollablePanel(), border:emptyBorder(3)) {
        gridBagLayout()
    }
}
timeline.verticalScrollBar.model.stateChanged = controller.scrollListener
timelinePanel.componentResized = controller.repositionView

keyStrokeAction(timeline, condition:"WHEN_IN_FOCUSED_WINDOW", action: controller.scrollUp, keyStroke:KeyStroke.getKeyStroke("UP"))
keyStrokeAction(timeline, condition:"WHEN_IN_FOCUSED_WINDOW", action: controller.scrollDown, keyStroke:KeyStroke.getKeyStroke("DOWN"))
keyStrokeAction(timelinePanel, condition:"WHEN_IN_FOCUSED_WINDOW", action: controller.scrollPageUp, keyStroke:KeyStroke.getKeyStroke("PAGE_UP"))
keyStrokeAction(timelinePanel, condition:"WHEN_IN_FOCUSED_WINDOW", action: controller.scrollPageDown, keyStroke:KeyStroke.getKeyStroke("PAGE_DOWN"))

tweetLinePanels = [:]

tweetLineScript = new TweetLine()
dmLineScript = new DMLine()
replyAction = action(greetController.&selectReplyToTweet, icon:imageIcon(resource:"/sound_grey.png"))
hyperlinkAction = action(greetController.&showHyperlink)
showTweetAction = action(greetController.&showUser)
tweetLineGBC = gbc(gridwidth:GridBagConstraints.REMAINDER, fill:GridBagConstraints.HORIZONTAL, weightx:1.0, insets:[3,3,3,3])

def updateTweets() {
    edt {
        Map<String, JComponent> keptTweetPanels = [:]
        def tweetCache = tweet = greetController.microblogService.tweetCache
        def dmCache = greetController.microblogService.dmCache
        model.tweets.each {tweetID ->
            def tweetLine = tweetLinePanels[tweetID]
            if (tweetLine) {
                tweetLinePanels.remove(tweetID)
            } else {
                tweet = tweetCache[tweetID]
                if (tweet) {
                    tweetLine = build(tweetLineScript);
                } else {
                    dm = dmCache[tweetID]
                    if (dm) {
                        tweetLine = build(dmLineScript);
                    } else {
                        return
                    }
                }
            }
            keptTweetPanels[tweetID] = tweetLine
        }
        timelinePanel.removeAll()
        keptTweetPanels.each {k, v ->
            timelinePanel.add(v, tweetLineGBC)
        }
        tweetLinePanels = keptTweetPanels
        timelinePanel.revalidate()
    }
}

timeline
