/*
 * Copyright 2008-2014 the original author or authors.
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

import java.util.prefs.Preferences

/**
 * @author Danno Ferrin
 */
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

                MicroblogService microblogService = greetController.microblogService
                microblogService.urlBase = urlBase
                if (microblogService.login(username, password as char[])) {
                	app.models.Greet.primaryUser = username

                    Preferences prefs = Preferences.userNodeForPackage(this.getClass())
                    prefs.put("user", model.loginUser);
                    prefs.put("serviceURL", model.serviceURL)


                    def tabbedPane = app.views.Greet.tweetsTabbedPane

                    microblogService.getReplies()
                    microblogService.getTweets(username)
                    def userPane = buildMVCGroup('UserPane', "UserPane_$username",
                        user:microblogService.userCache[username], closable:false);
                    tabbedPane.addTab("@$username", userPane.view.userPane)


                    def timelinePane = buildMVCGroup('TimelinePane');
                    timelinePane.model.tweetListGenerator = {MicroblogService service ->
                        timelinePane.model.tweets
                    }
                    greetController.friendsTimelineModel = timelinePane.model

                    // insure our refresh is the first
                    greetController.timelinePaneControllerQueue.remove(timelinePane.controller)
                    greetController.timelinePaneControllerQueue.add(0, timelinePane.controller)

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
