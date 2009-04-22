package greet

refreshTweetsAction = action(
        name: 'Refresh',
        enabled: bind {!model.refreshing},
        closure: controller.&refreshTweets
    )

tweetAction = action(
        name: 'Tweet',
        enabled: bind {!model.tweeting},
        closure: controller.&tweet
    )

