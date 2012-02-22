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

import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.KeyStroke

greetFrame = application(title:"Greet - A Griffon Twitter Client",
  size:[320,640],
  locationByPlatform:true,
  iconImage: imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]
) {
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
        tweetBox = textArea(
            enabled: bind {!model.tweeting}, rows: 0,
            lineWrap:true, wrapStyleWord:true)
    }
    button(refreshTweetsAction, text:null, icon:imageIcon('/arrow_refresh.png'),
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
            enabled:bind {tweetAction.enabled },
            gridwidth:1, fill:BOTH, weightx:1.0, insets:[3,3,3,3]
    )
    tweetButton = button(tweetAction, contentMargin:[6,6,6,6],
        enabled:bind {tweetAction.enabled && tweetBox.text.length() in  1..140},
        gridwidth:1, insets:[3,0,3,3])
  }
}

// this is to get the tweet box to grow when we reach the end of the line
tweetBox.document.undoableEditHappened = {
    doLater {
        mainPanel.revalidate()
        tweetBoxPane.minimumSize = tweetBoxPane.preferredSize
    }
}

//could still be more clever
keyStrokeAction(component:tweetBox, keyStroke:KeyStroke.getKeyStroke("ENTER"), action: tweetAction)

bind(source:controller.microblogService, sourceProperty:'status',
     target:model, targetProperty:'statusLine')
