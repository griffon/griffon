package greet

import javax.swing.JComponent

class TimelinePaneModel {
    List tweets
    Closure tweetListGenerator
    JComponent referenceTweetPanel
    int referenceOffset
    boolean updatingTimeline
}