import greet.TwitterService

app.controllers.Greet.twitterService = new TwitterService()
app.views.Greet.bind(source:app.controllers.Greet.twitterService, sourceProperty:'status', target:app.models.Greet, targetProperty:'statusLine')

app.models.LoginPane.loggingIn = false
def focusField = app.views.LoginPane.twitterNameField
if (focusField.text) {
    focusField = app.views.LoginPane.twitterPasswordField
}
focusField.requestFocusInWindow()
