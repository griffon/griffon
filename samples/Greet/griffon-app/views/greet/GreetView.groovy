package greet

import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.InputMap
import javax.swing.ActionMap
import javax.swing.KeyStroke

greetFrame = application(title:"Greet - A Groovy Twitter Client", size:[320,640], locationByPlatform:true) {

  mainPanel = panel {

    gridBagLayout()
    tweetsTabbedPane = tabbedPane(tabPlacement:JTabbedPane.TOP,
        gridwidth: REMAINDER, weighty: 1.0, fill: BOTH, preferredSize:[100, 100])
    {
        widget(loginPanel, title: '@', border:null)
    }
    separator(fill: HORIZONTAL, gridwidth: REMAINDER)
    tweetKind = label(text:bind {
            boolean aUser = model.targetUser
            boolean aDM = model.sendingDM
            (aUser
                ? (aDM
                    ? "Send $model.targetUser a direct message."
                    : "Reply to $model.targetUser:")
                : "What are you doing?")},
        enabled: bind {!model.tweeting},
        fill:HORIZONTAL, insets:[3,6,1,3], gridwidth:REMAINDER)
    tweetBoxPane = scrollPane(fill: BOTH, weightx:1.0, insets:[1,3,1,3], gridwidth:REMAINDER,
            verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER
    ) {
        tweetBox = textArea(//action: controller.tweetAction,
            enabled: bind {!model.tweeting}, rows: 0,
            lineWrap:true, wrapStyleWord:true)
    }
    button(controller.refreshTweetsAction, text:null, icon:imageIcon('/arrow_refresh.png'),
        gridwidth: 1, insets: [3, 3, 3, 0], fill:HORIZONTAL, contentMargin:[6,6,6,6])
    statusLine = progressBar(value:bind {Math.min(140, tweetBox.text.length())},
            string: bind {
                String status = model.statusLine;
                int count = tweetBox.text.length();
                status ?: ((count <= 140)
                    ? "${140 - count} characters left"
                    : "${count - 140} characters too many")
            },
            minimum:0, maximum:140, stringPainted: true,
            enabled:bind {controller.tweetAction.enabled },
            gridwidth:1, fill:BOTH, weightx:1.0, insets:[3,3,3,3]
    )
    tweetButton = button(controller.tweetAction, contentMargin:[6,6,6,6],
        enabled:bind {controller.tweetAction.enabled && tweetBox.text.length() in  1..140},
        gridwidth:1, insets:[3,0,3,3])
  }
}

// this is to get the tweet box to grow when we reach the end of the line
bean(tweetBoxPane, minimumSize: bind(source:tweetBox.document, sourceEvent:'undoableEditHappened', sourceValue:{doLater {mainPanel.revalidate()}; tweetBoxPane.preferredSize}))

// can't yet think of a clever way to do this...
InputMap inputMap = tweetBox.inputMap
ActionMap actionMap = tweetBox.actionMap
inputMap.put(KeyStroke.getKeyStroke("ENTER"), "tweet")
actionMap.put("tweet", controller.tweetAction)
