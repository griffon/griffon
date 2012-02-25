/*
 * Copyright 2008-2010 the original author or authors.
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
import twitter4j.Twitter
import twitter4j.conf.ConfigurationBuilder
import twitter4j.TwitterFactory
import twitter4j.auth.RequestToken
import twitter4j.auth.AccessToken
import java.awt.Desktop

import twitter4j.TwitterException
import javax.swing.JOptionPane
import twitter4j.Status
import twitter4j.User
import twitter4j.DirectMessage

/**
 * @author Danno Ferrin
 */
class MicroblogService {
    private static Twitter twitter;
    private static final CONSUMER_KEY = 'lv6TOd1CUAtSoHd9WsQ'
    private static final CONSUMER_SECRET = 'UybvJwlVbvrd9i2VwbXWeDMEgdXqyMp2XVB107U'

    def instantiateTwitter = { String login ->
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
        configurationBuilder.with {
            debugEnabled = true
            OAuthConsumerKey = CONSUMER_KEY
            OAuthConsumerSecret = CONSUMER_SECRET
        }
        twitter = new TwitterFactory(configurationBuilder.build()).instance
        Long userId = twitter.showUser(login).id
        if (!accessTokens.containsKey(userId)) {
            RequestToken requestToken = twitter.OAuthRequestToken;
            AccessToken accessToken = null;
            while (null == accessToken) {
                if (Desktop.desktopSupported) {
                    Desktop.desktop.browse(new URI(requestToken.authorizationURL))
                }
                String pin = (String) JOptionPane.showInputDialog(null,
                    'Please provide a PIN from the application authorization page.',
                    'Application authorization', JOptionPane.QUESTION_MESSAGE)
                try {
                    if (pin && pin.length() > 0) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.OAuthAccessToken;
                    }
                } catch (TwitterException e) {
                    if (401 == e?.statusCode) {
                        System.out.println("Unable to get the access token.");
                    } else {
                        e?.printStackTrace();
                    }
                }
            }
            twitter.setOAuthAccessToken(accessToken)
            accessTokens.put(userId, accessToken)
            //persist to the accessToken for future reference.
            storeAccessToken(twitter.verifyCredentials().id, accessToken);
        } else {
            twitter.setOAuthAccessToken(accessTokens.get(userId))
        }
    }

    static final DateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)

    Map tweetCache = new CacheMap(500)
    Map directMessageCache = new CacheMap(50)
    Map userCache = new CacheMap(200)

    @Bindable String status = ""
    User authenticatedUser
    XmlSlurper slurper = new XmlSlurper()

    Status storeTweet(Status tweet) {
        tweetCache[tweet.id] = tweet
        return tweet
    }

    DirectMessage storeDirectMessage(DirectMessage directMessage) {
        directMessageCache[directMessage.id] = directMessage
        return directMessage
    }

    User storeUser(User user) {
        userCache[user.screenName] = user
        return user
    }

    def withStatus(String status, Closure c) {
        setStatus(status)
        try {
            def o = c()
            setStatus("")
            return o
        } catch (Throwable t) {
            def message
            switch (t.message) {
                case ~'.* 400 .*':
                    message = "Error $status : Rate Limit Reached"; break
                case ~'.* 401 .*':
                case ~'.*Server redirected too many.*':
                    message = "Error $status : Incorrect Password"; break
                default:
                    message = "Error $status : $t.message"; break
            }

            setStatus(message)
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

    boolean login(String username) {
        withStatus("Logging in") {
            instantiateTwitter(username)
            authenticatedUser = getUser(username)
        }
        true
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
            def list = twitter.getFriendsIDs(user.screeName)
            while (list.size()) {
                list.collect(friends) {
                    storeUser(it)
                }
                page++
                try {
                    list = twitter.getFriendsIDs(user.screenName, page)
                } catch (Exception e) {
                    break
                }
            }
        }
        withStatus("Loading Friends Images") {
            return friends.each {
                loadImage(it.profileImageURL)
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
            def list = twitter.getFollowersIDs(user.screenName)//slurpAPIStream("$urlBase/statuses/followers/${user.screen_name}.xml")
            while (list.size()) {
                list.collect(friends) {
                    storeUser(it)
                }
                page++
                try {
                    list = twitter.getFollowersIDs(user.screenName, page)//slurpAPIStream("$urlBase/statuses/followers/${user.screen_name}.xml&page=$page")
                } catch (Exception e) { 
                    break 
                }
            }
        }
        withStatus("Loading Followers Images") {
            return friends.each {
                loadImage(it.profileImageURL)
            }
        }
    }

    def follow(String userID, boolean notificaitons = false) {
        withStatus("Following $userID") {
            twitter.createFriendship(userID)
        }
    }

    def unfollow(String userID) {
        withStatus("UInfollowing $userID") {
            twitter.destroyFriendship(userID)
        }
    }

    boolean currentUserFollows(String toUserID) {
        follows(authenticatedUser?.screenName, toUserID)
    }

    boolean follows(String fromUserID, String toUserID) {
        withStatus("Checking Following") {
            if(fromUserID && toUserID) {
                return twitter.existsFriendship(fromUserID, toUserID)
            }
        }
        return false
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
            timeline = twitter.getFriendsTimeline().collect {
                storeTweet(it)
            }
        }
        withStatus("Loading Timeline Images") {
            return timeline.each {
                loadImage(it.user.profileImageURL)
            }
        }
    }

    List<Status> getReplies() {
        List<Status> replies = []
        withStatus("Loading Replies") {
            replies = twitter.mentions.collect {
                storeTweet(it)
            }
        }
        withStatus("Loading Replies Images") {
            return replies.each { Status reply ->
                loadImage(reply?.user?.profileImageURL)
            }
        }
    }

    List<Status> getTweets() {
        return getTweets(authenticatedUser)
    }

    List<Status> getTweets(String friend) {
        return getTweets(getUser(friend))
    }

    List<Status> getTweets(User friend) {
        List<Status> statuses = []
        withStatus("Loading Tweets") {
            statuses = twitter.getUserTimeline(friend.screenName).collect {
                storeTweet(it)
            }
        }
        withStatus("Loading Tweet Images") {
            return statuses.each { status ->
                loadImage(status?.user?.profileImageURL)
            }
        }
    }

    List<DirectMessage> getDirectMessages() {
        def directMessages = []
        withStatus("Loading Direct Messages") {
            directMessages = twitter.directMessages.collect {
                storeDirectMessage(it)
            }
        }
        withStatus("Loading Direct Messages Images") {
            return directMessages.each { DirectMessage directMessage ->
                loadImage(directMessage?.sender?.profileImageURL)
            }
        }
    }

    List<DirectMessage> getDirectMessagesSent() {
        def directMessages = []
        withStatus("Loading Sent Direct Messages") {
            directMessages = twitter.getSentDirectMessages().collect {
                storeDirectMessage(it)
            }
        }
        withStatus("Loading Direct Messages Images") {
            return directMessages.each {
                loadImage(it.sender.profileImageURL)
            }
        }
    }

    User getUser(String screenName) {
        withStatus("Loading User ${screenName}") {
            return storeUser(twitter.showUser(screenName))
        }
    }

    Status tweet(String message, String inReplyToStatusId = null) {
        withStatus("Tweeting") {
            Status status = twitter.updateStatus(message)
            if (inReplyToStatusId) {
                status.inReplyToStatusId(inReplyToStatusId)
            }
            return storeTweet(status)
        }
    }

    def untweet(Long statusId) {
        withStatus("Deleting Tweet") {
            twitter.destroyStatus(statusId)

            // if we get here, delete succeeded
            tweetCache.remove(statusId)
        }
    }

    Map sendDirectMessage(String toUserID, String message) {
        withStatus("Sending Direct Message") {
            return storeDirectMessage(twitter.sendDirectMessage(toUserID, message))
        }
    }


    Map destroyDirectMessage(String directMessageId) {
        withStatus("Deleting DM") {
            twitter.destroyDirectMessage(directMessageId)

            // if we get here, delete succeeded
            directMessageCache.remove(directMessageId)
        }
    }

    // no need to read these, swing seems to cache these so the EDT won't stall
    def loadImage(image) {
        // no-op for now
        // if (!imageMap[image]) {
        //     Thread.start {imageMap[image] = new DelayedImageIcon(48, 48, new URL(image))}
        // }
    }

    static String timeAgo(date) {
        return (date as String) ? timeAgo(twitterFormat.parse(date as String)) : ''
    }

    static String timeAgo(Date d) {
        if (d.time == 0) return 'never'
        int secs = (System.currentTimeMillis() - d.time) / 1000
        def dir = (secs < 0) ? "from now" : "ago"
        if (secs < 0) secs = -secs
        def parts
        switch (secs) {
            case 0..119:
                parts = [1, "minute", dir]; break
            case 120..3599:
                parts = [(secs / 60) as int, "minutes", dir]; break
            case 3600..7199:
                parts = [1, "hour", dir]; break
            case 7200..86399:
                parts = [(secs / 3600) as int, "hours", dir]; break
            case 86400..172799:
                parts = [1, "day", dir]; break
            default:
                parts = [(secs / 86400) as int, "days", dir]; break
        }
        return parts.join(" ")
    }

    private Map<Long, AccessToken> accessTokens = new HashMap<Long, AccessToken>() {
        {
            File storage = new File('accessToken.storage')
            if(storage.exists()) {
                storage.withReader('UTF-8') {
                    String string
                    while ((string = it.readLine())) {
                        if (string ==~ /.*,.*,.*/) {
                            def (userId, accessToken, accessTokenSecret) = string.split(',')
                            put(userId as Long, new AccessToken(accessToken, accessTokenSecret))
                        }
                    }
                }
            } else {
                storage.createNewFile()
            }
        }
    }
    private void storeAccessToken(Long userId, AccessToken accessToken) {
        try {
            File storage = new File('accessToken.storage')
            storage << "${userId},${accessToken.token},${accessToken.tokenSecret}"
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private AccessToken loadAccessToken(Long userId) {
        return accessTokens.get(userId);
    }
}