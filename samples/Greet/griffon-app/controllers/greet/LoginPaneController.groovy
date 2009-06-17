package greet

import java.util.prefs.Preferences
import javax.swing.Action

class LoginPaneController {

	LoginPaneModel model
    LoginPaneView view

    void login(evt) {
        edt { model.loggingIn = true }
        def username = model.loginUser
        def password = model.loginPassword
        def urlBase = model.serviceURL
        doOutside {
            try {
                GreetController greetController = app.controllers.Greet

                TwitterService twitterService = greetController.twitterService
                twitterService.urlBase = urlBase
                if (twitterService.login(username, password as char[])) {
                	app.models.Greet.primaryUser = username

                    Preferences prefs = Preferences.userNodeForPackage(this.getClass())
                    prefs.put("user", model.loginUser);
                    prefs.put("serviceURL", model.serviceURL)


                    def tabbedPane = app.views.Greet.tweetsTabbedPane

                    twitterService.getReplies()
                    twitterService.getTweets(username)
                    def userPane = buildMVCGroup('UserPane', "UserPane_$username",
                        user:twitterService.userCache[username], closable:false);
                    tabbedPane.addTab("@$username", userPane.view.userPane)


                    def timelinePane = buildMVCGroup('TimelinePane');
                    timelinePane.model.tweetListGenerator = {TwitterService service ->
                        timelinePane.model.tweets
                    }
                    greetController.friendsTimelineModel = timelinePane.model

                    // insure our refresh is the first
                    greetController.timelinePaneControllerQueue.remove(timelinePane.controller)
                    greetController.timelinePaneControllerQueue.add(0, timelinePane.controller)

                    timelinePane.controller.twitterService = twitterService
                    tabbedPane.addTab("Timeline", timelinePane.view.timeline)

                    app.models.Greet.tweeting = false
                    greetController.refreshTweets(evt)

                    tabbedPane.selectedComponent = timelinePane.view.timeline
                    tabbedPane.removeTabAt(0)
                    app.views.Greet.tweetBox.requestFocusInWindow()
                } else {
                    JOptionPane.showMessageDialog(view.mainPanel, "Login failed")
                }
            } catch (Exception e) {
                e.printStackTrace()
            } finally {
                edt { model.loggingIn = false }
            }
        }
    }

}
