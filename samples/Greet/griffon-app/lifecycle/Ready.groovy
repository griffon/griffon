/*
 * Copyright 2008-2010 the original author or authors.
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

/**
 * @author Danno Ferrin
 */

import greet.TwitterService

app.controllers.Greet.twitterService = new TwitterService()
app.views.Greet.bind(source:app.controllers.Greet.twitterService, sourceProperty:'status', target:app.models.Greet, targetProperty:'statusLine')

app.models.LoginPane.loggingIn = false
def focusField = app.views.LoginPane.twitterNameField
if (focusField.text) {
    focusField = app.views.LoginPane.twitterPasswordField
}
focusField.requestFocusInWindow()
