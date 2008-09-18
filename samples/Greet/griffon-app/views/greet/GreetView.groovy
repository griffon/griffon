/*
 * Copyright 2008 the original author or authors.
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
import java.awt.event.ActionListener
import java.beans.PropertyChangeListener
import javax.swing.DefaultComboBoxModel
import javax.swing.JScrollPane
import javax.swing.ListCellRenderer
import javax.swing.Timer

tweetLineFont = new java.awt.Font("Ariel", 0, 12)
tweetTimeFont = new java.awt.Font("Ariel", 0, 9)
def userCell = label(border: emptyBorder(3))
userCellRenderer = {list, user, index, isSelected, isFocused ->
    if (user) {
        userCell.icon = controller.twitterService.imageMap[user.profile_image_url as String]
        userCell.text = "<html>$user.screen_name<br>$user.name<br>$user.location<br>"
    } else {
        userCell.icon = null
        userCell.text = null
    }
    userCell
} as ListCellRenderer

application(title:"Greet - A Groovy Twitter Client", size:[320,640], locationByPlatform:true) {

  mainPanel = panel(cursor: bind {model.allowSelection ? null : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)}) {

    cardSwitcher = cardLayout()

    panel(border: emptyBorder(3),
        constraints:'login'
    ) {
        gridBagLayout()

        label("Welcome to Greet!",
            gridwidth:REMAINDER, insets:[3, 3, 15, 3]
        )

        label("Username:",
            anchor: EAST, insets: [3, 3, 3, 3])
        twitterNameField = textField(action:controller.loginAction,
            gridwidth: REMAINDER, fill:HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])

        label("Password:",
            anchor: EAST, insets: [3, 3, 3, 3])
        twitterPasswordField = passwordField(action:controller.loginAction,
            gridwidth: REMAINDER, fill:HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])

        label("Service:",
            anchor: EAST, insets: [3, 3, 3, 3])
        twitterServiceComboBox = comboBox(items:["http://twitter.com", "http://identi.ca/api"], editable:true,
            enabled:bind{twitterNameField.enabled},
            gridwidth: REMAINDER, fill:HORIZONTAL, weightx: 1.0, insets: [3, 3, 3, 3])
        twitterServiceComboBox.editor.editorComponent.action = controller.loginAction

        panel()
        button(controller.loginAction, //defaultButton: true,
            gridwidth: REMAINDER, anchor: EAST, insets: [3, 3, 15, 3])

        panel(gridwidth: REMAINDER,  weighty:1.0, size:[0,0]) // spacer
    }

    panel(constraints:'running') {
        gridBagLayout()
        users = comboBox(renderer: userCellRenderer, action: controller.userSelected,
            selectedItem:bind {model.focusedUser},
            gridwidth: REMAINDER, insets: [6, 6, 3, 6], fill: HORIZONTAL
        )
        label('Search:', insets: [3, 6, 3, 3])
        searchField = textField(columns: 20, action: controller.filterTweets,
            insets: [3, 3, 3, 3], weightx: 1.0, fill: BOTH)
        button(controller.filterTweets,
            gridwidth: REMAINDER, insets: [3, 3, 3, 6], fill:HORIZONTAL)
        tabbedPane(gridwidth: REMAINDER, weighty: 1.0, fill: BOTH) {
            scrollPane(title: 'Timeline') {
                timelinePanel = panel(new ScrollablePanel(), border:emptyBorder(3))
            }
            scrollPane(title: 'Replies') {
                repliesPanel = panel(new ScrollablePanel(), border:emptyBorder(3))
            }
            scrollPane(title: 'Tweets') {
                tweetPanel = panel(new ScrollablePanel(), border:emptyBorder(3))
            }
            scrollPane(title: 'Statuses') {
                statusPanel = panel(new ScrollablePanel(), border:emptyBorder(3))
            }
        }
        separator(fill: HORIZONTAL, gridwidth: REMAINDER)
        tweetBox = textField(action: controller.tweetAction,
            fill:BOTH, weightx:1.0, insets:[3,3,1,3], gridwidth:2)
        tweetButton = button(controller.tweetAction,
            enabled:bind {controller.tweetAction.enabled && tweetBox.text.length() in  1..140},
            gridwidth:REMAINDER, insets:[3,3,1,3])
        progressBar(value:bind {Math.min(140, tweetBox.text.length())},
                string: bind { int count = tweetBox.text.length();
                    ((count <= 140)
                        ? "${140 - count} characters left"
                        : "${count - 140} characters too many")
                },
                minimum:0, maximum:140, stringPainted: true,
                gridwidth:REMAINDER, fill:HORIZONTAL, insets:[1,3,1,3]
        )
        separator(fill: HORIZONTAL, gridwidth: REMAINDER)

    }
  }

  jxstatusBar {
    statusLine = label(text: bind {model.statusLine})
  }
}

model.addPropertyChangeListener("friends", {evt ->
    edt { users.model = new DefaultComboBoxModel(evt.newValue as Object[]) }
} as PropertyChangeListener)

// add data change listeners
model.addPropertyChangeListener("lastUpdate", {evt ->
    [timeline:timelinePanel, replies:repliesPanel, tweets:tweetPanel, statuses:statusPanel].each {p, w ->
        edt {
            def oldName = null
            def topInset = 0
            JScrollPane parentScrollPane = w.parent.parent
            def scrollPos = parentScrollPane.verticalScrollBar.value
            if (w.componentCount) {
                oldName = w.components[0].name
                topInset = w.components[0].y
            }

            w.removeAll()
            panel(w) {
                gridBagLayout()
                model."$p".each {
                    tweet = it
                    build(TweetLine)
                }
            }
            boolean found = false
            if (oldName && (w.components[0].name != oldName)) w.components.each { tweetLine ->
                if (tweetLine.name == oldName) {
                    found = true
                    doLater {
                        float pos = tweetLine.y - topInset
                        if (scrollPos < (tweetLine.height + tweetLine.y)) {
                            float step = pos / 500 * 18
                            Timer t
                            t = new Timer(18,  {
                                if (pos <= 0) {
                                    t.stop()
                                } else {
                                    parentScrollPane.verticalScrollBar.setValue((pos -= step) as int)
                                }
                            } as ActionListener);
                            t.repeats = true
                            t.start()
                        } else {
                            parentScrollPane.verticalScrollBar.value = scrollPos + pos
                        }
                    }
                }
            }
            if (!found)
                doLater { parentScrollPane.verticalScrollBar.value = scrollPos}
        }
    }
} as PropertyChangeListener)

refreshTimer = new Timer(180000, controller.filterTweets)
model.addPropertyChangeListener("focusedUser", {refreshTimer.start()} as PropertyChangeListener)
