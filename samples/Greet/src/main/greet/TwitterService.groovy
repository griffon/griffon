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
import groovy.util.slurpersupport.GPathResult
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * @author Danno Ferrin
 */
class TwitterService {

    static final DateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy")

    Map tweetCache = new CacheMap(500)
    Map dmCache = new CacheMap(50)
    Map userCache = new CacheMap(200)

    String urlBase;
    @Bindable String status = ""
    Map authenticatedUser
    XmlSlurper slurper = new XmlSlurper()

    TwitterService(String urlBase = "http://twitter.com") {
        this.urlBase = urlBase
    }

    Map storeTweet(GPathResult tweet, Map user=null) {
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
        mapTweet.created_at = twitterFormat.parse(mapTweet.created_at).getTime()
        return mapTweet
    }

    Map storeDM(GPathResult dm) {
        def mapDM = [:]
        dm.children().each {
            mapDM[it.name()] = it as String
        }
        mapDM.sender = storeUser(dm.sender)
        mapDM.recipient = storeUser(dm.recipient)
        mapDM.created_at = twitterFormat.parse(mapDM.created_at).getTime() 
        dmCache[mapDM.id] = mapDM
        return mapDM
    }

    Map storeUser(GPathResult user) {
        def mapUser = [:]
        user.children().each {
            mapUser[it.name()] = it as String
        }
        userCache[mapUser.screen_name] = mapUser
        mapUser.status = null
        if (user.status as String) {
            storeTweet(user.status, mapUser)
        }
        return mapUser
    }

    def withStatus(String status, Closure c) {
        setStatus(status)
        try {
            def o = c()
            setStatus("")
            return o
        } catch (Throwable t) {
            setStatus("Error $status : ${t.message =~ '400'?'Rate Limit Reached':t}")
            throw t
        }
    }

    GPathResult slurpAPIStream(String url) {
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

    boolean login(String name, def password) {
        withStatus("Logging in") {
            Authenticator.setDefault(
                [getPasswordAuthentication : {
                    return new PasswordAuthentication(name, password) }
                ] as Authenticator)
            slurpAPIStream("$urlBase/account/verify_credentials.xml")
            authenticatedUser = getUser(name)
        }
    }

    List<Map> getFriends() {
        getFriends(authenticatedUser)
    }

    List<Map> getFriends(String user) {
        return getFriends(getUser(user))
    }

    List<Map> getFriends(Map user) {
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

    List<Map> getFollowers() {
        getFollowers(authenticatedUser)
    }

    List<Map> getFollowers(String user) {
        return getFriends(getUser(user))
    }

    List<Map> getFollowers(Map user) {
        def friends = [user]
        withStatus("Loading Followers") {
            def page = 1
            def list = slurpAPIStream("$urlBase/statuses/followers/${user.screen_name}.xml")
            while (list.user.size()) {
                list.user.collect(friends) {storeUser(it)}
                page++
                try {
                    list = slurpAPIStream("$urlBase/statuses/followers/${user.screen_name}.xml&page=$page")
                } catch (Exception e) { break }
            }
        }
        withStatus("Loading Followers Images") {
            return friends.each {
                loadImage(it.profile_image_url as String)
            }
        }
    }

    def follow(String userID, boolean notificaitons=false) {
        withStatus("Following $userID") {
            def urlConnection = new URL("$urlBase/friendships/create/${userID}.xml").openConnection()
            urlConnection.requestMethod = "POST"
            return slurper.parse(urlConnection.inputStream)
        }
    }

    def unfollow(String userID) {
        withStatus("UInfollowing $userID") {
            def urlConnection = new URL("$urlBase/friendships/destroy/${userID}.xml").openConnection()
            urlConnection.requestMethod = "DELETE"
            return slurper.parse(urlConnection.inputStream)
        }
    }

    boolean currentUserFollows(String toUserID) {
        follows(authenticatedUser.id, toUserID)
    }

    boolean follows(String fromUserID, String toUserID) {
        withStatus("Checking Following") {
            return Boolean.valueOf(slurpAPIStream(
                    "$urlBase/friendships/exists.xml?user_a=$fromUserID&user_b=$toUserID"
                ).toString())
        }
    }

    String getLargestID(List<Map> tweets) {
        long largestid = 0
        tweets.each {Map tweet ->
            long thisid = tweet.id as long
            if (thisid > largestid) {
                largestid = thisid
            }
        }
        return largestid as String
    }

    List<Map> getFriendsTimeline(String sinceID = '0', int count = 20) {
        def timeline = []
        withStatus("Loading Timeline") {
            timeline =  slurpAPIStream(
                    "$urlBase/statuses/friends_timeline.xml?count=$count&${sinceID=='0'?'':'&since_id='}${sinceID=='0'?'':sinceID}"
                ).status.collect {storeTweet(it)}
        }
        withStatus("Loading Timeline Images") {
            return timeline.each {
                loadImage(it.user.profile_image_url as String)
            }
        }
    }

    List<Map> getReplies() {
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

    List<Map> getTweets() {
        return getTweets(user)
    }

    List<Map> getTweets(String friend) {
        return getTweets(getUser(friend))
    }

    List<Map> getTweets(Map friend) {
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

    List<Map> getDirectMessages() {
        def dms = []
        withStatus("Loading DMs") {
            dms = slurpAPIStream(
                    "$urlBase/direct_messages.xml"
                ).direct_message.collect {storeDM(it)}
        }
        withStatus("Loading DM Images") {
            return dms.each {
                loadImage(it.sender.profile_image_url as String)
            }
        }
    }

    List<Map> getDirectMessagesSent() {
        def dms = []
        withStatus("Loading DMs Sent") {
            dms = slurpAPIStream(
                    "$urlBase/direct_messages/sent.xml"
                ).direct_message.collect {storeDM(it)}
        }
        withStatus("Loading DM Images") {
            return dms.each {
                loadImage(it.sender.profile_image_url as String)
            }
        }
    }

    Map getUser(String screen_name) {
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

    Map tweet(String message, String inReplyToID = null) {
        withStatus("Tweeting") {
            def urlConnection = new URL("$urlBase/statuses/update.xml").openConnection()
            urlConnection.doOutput = true
            urlConnection.outputStream << "source=greet&status=${URLEncoder.encode(message, 'UTF-8')}"
            if (inReplyToID) urlConnection.outputStream << "&in_reply_to_status_id=$inReplyToID"
            return storeTweet(slurper.parse(urlConnection.inputStream))
        }
    }

    def untweet(String tweetID) {
        withStatus("Deleting Tweet") {
            slurpAPIStream("$urlBase/statuses/destroy/${tweetID}.xml")
            // if we get here, delete suceeded
            tweetCache.remove(tweetID)
        }
    }

    Map sendDM(String toUserID, String message) {
        withStatus("DMing") {
            def urlConnection = new URL("$urlBase/direct_messages/new.xml").openConnection()
            urlConnection.doOutput = true
            urlConnection.outputStream << "source=greet&user=$toUserID&text=${URLEncoder.encode(message, 'UTF-8')}"
            return storeDM(slurper.parse(urlConnection.inputStream))
        }
    }


    Map unsendDM(String tweetID) {
        withStatus("Deleting DM") {
            slurpAPIStream("$urlBase/direct_messages/destroy/${tweetID}.xml")
            // if we get here, delete suceeded
            dmCache.remove(tweetID)
        }
    }

    // no need to read these, swing seems to cache these so the EDT won't stall
    def loadImage(image) {
        // no-op for now
//        if (!imageMap[image]) {
//            Thread.start {imageMap[image] = new DelayedImageIcon(48, 48, new URL(image))}
//        }
    }

    static String timeAgo(date) {
        if (date as String)
            return timeAgo(twitterFormat.parse(date as String))
        else
            return ""
    }

    static String timeAgo(Date d) {
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
