/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greet

import groovy.beans.Bindable
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * @author Danno Ferrin
 */
class TwitterService {

    Map tweetCache = new CacheMap(500)
    Map userCache = new CacheMap(200)
    Map userStatuses = new CacheMap(200)

    String urlBase;
    @Bindable String status = "\u00a0"
    def authenticatedUser
    XmlSlurper slurper = new XmlSlurper()
    def imageMap = [:]

    TwitterService(urlBase = "http://twitter.com") {
        this.urlBase = urlBase
    }

    def storeTweet(tweet, user=null) {
        def mapTweet = [:]
        tweet.children().each {
            mapTweet[it.name()] = it as String
        }
        tweetCache[mapTweet.id] = mapTweet
        if (user) {
            mapTweet.user = user
        } else {
            mapTweet.user = storeUser(tweet.user)
        }
        def oldStatus = mapTweet.user.status ?: [id:'0']
        if ((mapTweet.id as long) > (oldStatus.id as long)) {
            userStatuses[mapTweet.user.screen_name] = mapTweet
        } else {
        }

        return mapTweet
    }

    def storeUser(user) {
        def mapUser = [:]
        user.children().each {
            mapUser[it.name()] = it as String
        }
        userCache[mapUser.screen_name] = mapUser
        mapUser.status = null
        if (user.status as String) {
            storeTweet(user.status, mapUser)
        } else if (userStatuses[mapUser.screen_name] == null) {
            userStatuses[mapUser.screen_name] = [
                created_at:'Thu Jan 01 00:00:00 +0000 1970',
                id:'0',
                text:'',
                source:'web',
                truncated:'false',
                in_reply_to_status_id:'',
                in_reply_to_user_id:'',
                favorited:'false',
                in_reply_to_screen_name:'',
                user:mapUser]
        }
        return mapUser
    }

    def withStatus(status, c) {
        setStatus(status)
        try {
            def o = c()
            setStatus("\u00a0")
            return o
        } catch (Throwable t) {
            setStatus("Error $status : ${t.message =~ '400'?'Rate Limit Reached':t}")
            throw t
        }
    }

    def slurpAPIStream(String url) {
        def text = ""
        try {
            text = new URL(url).openStream().text
            synchronized (slurper) {
                return slurper.parse(new StringReader(text))
            }
        } catch (Exception e) {
            System.err.println text
            throw e
        }
    }

    boolean login(def name, def password) {
        withStatus("Logging in") {
            Authenticator.setDefault(
                [getPasswordAuthentication : {
                    return new PasswordAuthentication(name, password) }
                ] as Authenticator)
            slurpAPIStream("$urlBase/account/verify_credentials.xml")
            authenticatedUser = getUser(name)
        }
    }

    def getFriends() {
        getFriends(authenticatedUser)
    }

    def getFriends(String user) {
        return getFriends(getUser(user))
    }

    def getFriends(user) {
        def friends = [user]
        withStatus("Loading Friends") {
            def page = 1
            def list = slurpAPIStream("$urlBase/statuses/friends/${user.screen_name}.xml")
            while (list.user.size()) {
                list.user.collect(friends) {storeUser(it)}
                page++
                try {
                    list = slurpAPIStream("$urlBase/statuses/friends/${user.screen_name}.xml&page=$page")
                } catch (Exception e) { break }
            }
        }
        withStatus("Loading Friends Images") {
            return friends.each {
                loadImage(it.profile_image_url as String)
            }
        }
    }

    def getFriendsTimeline() {
        getFriendsTimeline(user)
    }

    def getFriendsTimeline(String friend) {
        getFriendsTimeline(getUser(friend))
    }

    def getFriendsTimeline(user) {
        def timeline = []
        withStatus("Loading Timeline") {
            timeline =  slurpAPIStream(
                    //"$urlBase/statuses/friends_timeline/${user.screen_name}.xml"
                    "$urlBase/statuses/friends_timeline.xml"
                ).status.collect {storeTweet(it)}
        }
        withStatus("Loading Timeline Images") {
            return timeline.each {
                loadImage(it.user.profile_image_url as String)
            }
        }
    }

    def getReplies() {
        def replies = []
        withStatus("Loading Replies") {
            replies = slurpAPIStream(
                    "$urlBase/statuses/replies.xml"
                ).status.collect {storeTweet(it)}
        }
        withStatus("Loading Replies Images") {
            return replies.each {
                loadImage(it.user.profile_image_url as String)
            }
        }
    }

    def getTweets() {
        return getTweets(user)
    }

    def getTweets(String friend) {
        return getTweets(getUser(frield))
    }

    def getTweets(friend) {
        def tweets = []
        withStatus("Loading Tweets") {
            tweets = slurpAPIStream(
                    "$urlBase/statuses/user_timeline/${friend.screen_name}.xml"
                ).status.collect {storeTweet(it)}
        }
        withStatus("Loading Tweet Images") {
            return tweets.each {
                loadImage(it.user.profile_image_url as String)
            }
        }
    }

    def getUser(String screen_name) {
        withStatus("Loading User $screen_name") {
            if (screen_name.contains('@')) {
                return storeUser(slurpAPIStream(
                       "$urlBase/users/show.xml?email=${screen_name}"
                    ))
            } else {
                return storeUser(slurpAPIStream(
                        "$urlBase/users/show/${screen_name}.xml"
                    ))
            }
        }
    }

    def tweet(message) {
        withStatus("Tweeting") {
            def urlConnection = new URL("$urlBase/statuses/update.xml").openConnection()
            urlConnection.doOutput = true
            urlConnection.outputStream << "source=greet&status=${URLEncoder.encode(message, 'UTF-8')}"
            return slurper.parse(urlConnection.inputStream)
        }
    }

    // no need to read these, swing seems to cache these so the EDT won't stall
    def loadImage(image) {
        if (!imageMap[image]) {
            Thread.start {imageMap[image] = new javax.swing.ImageIcon(new URL(image))}
        }
    }

    static final DateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy")

    static def timeAgo(date) {
        if (date as String)
            return timeAgo(twitterFormat.parse(date as String))
        else
            return ""
    }

    static def timeAgo(Date d) {
        if (d.getTime() == 0) return 'never'
        int secs = (System.currentTimeMillis() - d.getTime()) / 1000
        def dir = (secs < 0) ? "from now" : "ago"
        if (secs < 0) secs = -secs
        def parts
        switch (secs) {
            case 0..119:
                parts = [1,"minute", dir]; break
            case 120..3599:
                parts = [(secs / 60) as int, "minutes", dir]; break
            case 3600..7199:
                parts = [1, "hour", dir]; break
            case 7200..86399:
                parts = [(secs / 3600) as int, "hours", dir]; break
            case 86400..172799:
                parts = [1, "day", dir]; break
            default :
                parts = [(secs / 86400) as int, "days", dir]; break
        }
        return parts.join(" ")
    }

}
