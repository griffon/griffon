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
import static java.awt.GridBagConstraints.*
import javax.swing.SwingConstants

/**
 * Created by IntelliJ IDEA.
 * User: Danno.Ferrin
 * Date: May 6, 2008
 * Time: 3:28:45 PM
 */
def tweetUser = tweet.user

def tweetText = ("$tweet.text"
    // fis regex bug
    .replace('$', '&#36;')
    // change http:// links to real links
    .replaceAll(/(http:\/\/[^' \t\n\r]+)?(.?[^h]*)/, {f,l,t->l?"<a href='$l'>$l</a>$t":"$t"})
    // change @username to twitter links
    .replaceAll(/(?:@(\w*+))?([^@]*)/, {f,l,t->l?"@<a href='http://twitter.com/$l'>$l</a>$t":"$t"})
)
tweetText = "<a href='http://twitter.com/${tweetUser.screen_name}'><b>${tweetUser.screen_name}</b></a> $tweetText"

panel(new RoundedPanel(foreground: java.awt.Color.WHITE, opaque:true),
        name:tweet.id) {
    gridBagLayout()
    label(icon:new DelayedImageIcon(48, 48, new URL(tweetUser.profile_image_url as String)),
        verticalTextPosition:SwingConstants.BOTTOM,
        horizontalTextPosition:SwingConstants.CENTER,
        anchor: NORTH, insets: [6, 6, 6, 3])
    editorPane(contentType:'text/html', text:tweetText,
        hyperlinkUpdate:app.controllers.Greet.&hyperlinkPressed,
        opaque: false, editable: false, font: tweetLineFont,
        gridwidth: REMAINDER, weightx: 1.0, fill: BOTH, insets: [3, 3, 3, 6])
    hbox(fill:BOTH, gridwidth:REMAINDER) {
        hstrut(6)
        button(replyAction, actionCommand:tweet.id, border:null,
            icon:imageIcon(resource:"/sound_grey.png"), rolloverIcon:imageIcon(resource:"/sound_spearmint.png"), pressedIcon:imageIcon(resource:"/sound_white.png"),
            contentAreaFilled:false, font:tweetTimeFont)
        if (tweet.source != 'web') {
            hstrut(3)
            def bt = button(hyperlinkAction, actionCommand:tweet.source,
                text:"<html>via $tweet.source" as String, border:null,
                contentAreaFilled:false, font:tweetTimeFont, cursor:Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
            bt.maximumSize = bt.preferredSize
        }
        if (tweet.in_reply_to_screen_name) {
            hstrut(3)
            def bt = button(showTweetAction, actionCommand:tweet.in_reply_to_screen_name,
                text:"<html>re: <a href='http://twitter.com/$tweet.in_reply_to_screen_name'>$tweet.in_reply_to_screen_name</a>" as String,
                border:null, contentAreaFilled:false, font:tweetTimeFont,
                cursor:Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
            bt.maximumSize = bt.preferredSize
        }
        glue()
        label(new TimeLabel(tweet.created_at), border:emptyBorder(0,3,3,6), font:tweetTimeFont)
    }
}