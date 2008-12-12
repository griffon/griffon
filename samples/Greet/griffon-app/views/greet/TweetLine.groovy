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

/**
 * Created by IntelliJ IDEA.
 * User: Danno.Ferrin
 * Date: May 6, 2008
 * Time: 3:28:45 PM
 */
import static java.awt.GridBagConstraints.*
import javax.swing.SwingConstants

def tweetUser = tweet.user
if (!(tweetUser as String)) tweetUser = tweet.parent()

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
        name:tweet.id,
        gridwidth:REMAINDER, fill:HORIZONTAL, weightx:1.0, insets:[3,3,3,3]) {
    gridBagLayout()
    label(icon:imageIcon(new URL(tweetUser.profile_image_url as String)),
        verticalTextPosition:SwingConstants.BOTTOM,
        horizontalTextPosition:SwingConstants.CENTER,
        anchor: NORTH, insets: [6, 6, 6, 3])
    editorPane(contentType:'text/html', text:tweetText,
        hyperlinkUpdate:controller.&hyperlinkPressed,
        opaque: false, editable: false, font: tweetLineFont,
        gridwidth: REMAINDER, weightx: 1.0, fill: BOTH, insets: [3, 3, 3, 6])
    hbox(fill:BOTH, gridwidth:REMAINDER) {
        if (tweet.source != 'web') {
            def bt = label("<html>via $tweet.source" as String, border:emptyBorder(0,6,3,3), font:tweetTimeFont)
            bt.maximumSize = bt.preferredSize
        }
        if (tweet.in_reply_to_screen_name as String) {
            def bt = label("<html>in reply to <a href='http://twitter.com/$tweet.in_reply_to_screen_name'>$tweet.in_reply_to_screen_name</a>" as String, border:emptyBorder(0,6,3,3), font:tweetTimeFont)
            bt.maximumSize = bt.preferredSize
        }
        glue()
        label(TwitterService.timeAgo(tweet.created_at), border:emptyBorder(0,3,3,6), font:tweetTimeFont)
    }
}