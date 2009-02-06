package greet

import java.awt.Cursor
import java.awt.Font
import javax.swing.UIManager

Font buttonFont = UIManager.getDefaults().getFont("ToggleButton.font")
buttonFont = buttonFont.deriveFont((buttonFont.getSize2D() - 2f) as float)

userPane = panel() {
    gridBagLayout()
    panel(anchor:NORTHWEST, gridheight:4) {
        gridBagLayout()
        label(icon: imageIcon(new URL(model.user.profile_image_url as String)),
            anchor:NORTHWEST, gridwidth:REMAINDER, insets:[6,6,6,3]
        )
        toggleButton("Tweets",
            contentMargin:[2,2,2,2], font:buttonFont, focusable:false,
            selected:bind(target:model, targetProperty:'showTweets', value:true),
            fill:HORIZONTAL, gridwidth:REMAINDER, insets:[1,6,1,3],
        )
        toggleButton("Replies",
            contentMargin:[2,2,2,2], font:buttonFont, focusable:false,
            selected:bind(target:model, targetProperty:'showReplies', value:true),
            fill:HORIZONTAL, gridwidth:REMAINDER, insets:[1,6,1,3],
        )
        toggleButton("DMs",
            contentMargin:[2,2,2,2], font:buttonFont, focusable:false,
            selected:bind(target:model, targetProperty:'showDirectMessages', value:true), 
            fill:HORIZONTAL, gridwidth:REMAINDER, insets:[1,6,1,3],
        )
    }

    vbox(insets:[6,3,6,3], anchor:NORTHWEST, weightx:1.0, gridheight:4, fill:HORIZONTAL) {

        label(model.user.screen_name as String)
        label(model.user.name as String)
        label(model.user.location as String)
        label(mouseClicked: { app.controllers.Greet.displayURL(new URL(model.user.url)) },
            text:"<html><a href='$model.user.url'>$model.user.url</a>" as String,
            cursor:Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        label("<html>$model.user.description" as String)
    }

    if (closable) {
        closeButton = button(icon:imageIcon('/cross.png'), actionPerformed:controller.&close,
          borderPainted:false, contentAreaFilled:false, contentMargin:[6,6,6,6],
          mouseEntered: {closeButton.contentAreaFilled = true; closeButton.borderPainted = true},
          mouseExited: {closeButton.contentAreaFilled = false; closeButton.borderPainted = false},
          anchor:NORTHEAST, gridwidth:REMAINDER, insets:[3,3,0,6])

        button("Follow", enabled:bind {!model.busy}, visible: bind {!model.following},
            margin:[0,0,0,0], actionPerformed:controller.&follow,
            fill:HORIZONTAL, gridwidth:REMAINDER, insets:[0,3,0,6])
        button("Unfollow", enabled:bind {!model.busy}, visible: bind {model.following},
            margin:[0,0,0,0], actionPerformed:controller.&unfollow, 
            fill:HORIZONTAL, gridwidth:REMAINDER, insets:[0,3,0,6])
//        button("Block", enabled:false, margin:[0,0,0,0],
//            fill:HORIZONTAL, gridwidth:REMAINDER, insets:[0,3,0,6])
        button("DM", enabled:bind {!model.busy}, visible: bind {model.follows},
            margin:[0,0,0,0], actionPerformed:controller.&directMessage, 
            fill:HORIZONTAL, gridwidth:REMAINDER, insets:[0,3,0,6])
    }
    panel(fill:HORIZONTAL, gridheight:3 + (closable?0:1), gridwidth:REMAINDER,
        preferredSize:[0,0], minimumSize:[0,0], maximumSize:[0,0])

    container(timelinePane, 
        fill:BOTH, gridwidth:3, weighty:1.0)
}

//...

userPane