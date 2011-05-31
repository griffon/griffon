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

import java.awt.Point
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JViewport

/**
 * @author Danno Ferrin
 */
class TimelinePaneController {
    // these will be injected by Griffon
    TimelinePaneModel model
    TimelinePaneView view

    MicroblogService microblogService

    void mvcGroupInit(Map args) {
        app.controllers.Greet.timelinePaneControllerQueue.add(this)
    }

    public void updateTimeline(evt) {
        model.updatingTimeline = true
        try {
            model.tweets = model.tweetListGenerator(microblogService)
            edt {
                view.updateTweets()
                doLater() {
                    repositionView()
                }
            }
        } finally {
            doLater() {
                model.updatingTimeline = false
            }
        }

    }

    Closure repositionView = {evt = null ->
        // scroll the cached "top" to the top
        Point newPoint = new Point(0, (model.referenceTweetPanel?.y ?: 0) - model.referenceOffset)
        doLater {
            view.timeline.viewport.viewPosition = newPoint
        }
    }

    Closure scrollListener = {def evt = null->
        if (!model.updatingTimeline) {
            // store who is the "top"
            JViewport viewport = view.timeline.viewport
            int viewY = viewport.viewPosition.@y
            int keptTweetsPos = 0
            for (JComponent tweetPanel in view.tweetLinePanels.values()) {
                if (tweetPanel.y > viewY) {
                    if (tweetPanel.y > 10) {
                        model.referenceTweetPanel = tweetPanel
                        model.referenceOffset = tweetPanel.y - viewY
                    } else {
                        model.referenceTweetPanel = null
                        model.referenceOffset = 0
                    }
                    return
                }
            }
            model.referenceTweetPanel = null
            model.referenceOffset = 0
        }
    }

    Action scrollUp = action {
        println "Up"
        JViewport viewport = view.timeline.viewport
        Point p = new Point(viewport.viewPosition)
        p.@y -= view.timeline.verticalScrollBar.unitIncrement
        viewport.viewPosition = p
    }

    Action scrollDown = action {
        println "down"
        JViewport viewport = view.timeline.viewport
        Point p = new Point(viewport.viewPosition)
        p.@y += view.timeline.verticalScrollBar.unitIncrement
        viewport.viewPosition = p
    }

    Action scrollPageUp = action {
        println "page up"
        JViewport viewport = view.timeline.viewport
        Point p = new Point(viewport.viewPosition)
        p.@y -= view.timeline.verticalScrollBar.blockIncrement
        viewport.viewPosition = p
    }

    Action scrollPageDown = action {
        println "page down"
        JViewport viewport = view.timeline.viewport
        Point p = new Point(viewport.viewPosition)
        p.@y += view.timeline.verticalScrollBar.blockIncrement
        viewport.viewPosition = p
    }
}
