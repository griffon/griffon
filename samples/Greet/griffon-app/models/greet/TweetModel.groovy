package greet

import javax.swing.Icon

class TweetModel {

    Icon icon
    String screen_name
    String text
    String tweetText
    String id
    String created_at

    String getTweetedAt() {
        TwitterService.timeAgo(created_at)
    }

    def mvcGroupInit(Map values) {
        def tweetUser = values.tweet.user
        if (!(tweetUser as String)) tweetUser = values.tweet.parent()

        user_name = tweetUser.screen_name

        def tweetText = ("$values.tweet.text"
            // fix regex bug
            .replace('$', '&#36;')
            // change http:// links to real links
            .replaceAll(/(http:\/\/[^' \t\n\r]+)?(.?[^h]*)/, {f,l,t->l?"<a href='$l'>$l</a>$t":"$t"})
            // change @username to twitter links
            .replaceAll(/(?:@(\w*+))?([^@]*)/, {f,l,t->l?"@<a href='http://twitter.com/$l'>$l</a>$t":"$t"})
        )
        tweetText = "<a href='http://twitter.com/$screen_name'><b>$screen_name</b></a> $tweetText"

        icon = values.tweet.iconMap[user_name]
        if (!icon) {
            icon = 
        }

    }
}