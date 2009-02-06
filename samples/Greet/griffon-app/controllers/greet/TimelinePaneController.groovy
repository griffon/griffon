package greet

import java.awt.Point
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.JViewport
import javax.swing.event.ChangeEvent

class TimelinePaneController {
    // these will be injected by Griffon
    TimelinePaneModel model
    TimelinePaneView view

    TwitterService twitterService

    void mvcGroupInit(Map args) {
        app.controllers.Greet.timelinePaneControllerQueue.add(this)
    }

    public void updateTimeline(evt) {
        model.updatingTimeline = true
        try {
            model.tweets = model.tweetListGenerator(twitterService)
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
}
