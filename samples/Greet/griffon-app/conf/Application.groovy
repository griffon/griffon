application {
    title="Greet"
    startupGroups=['Greet']
}

// MVC Group for "Greet"
mvcGroups {
    // MVC Group for "greet.Tweet"
    Tweet {
        model = 'greet.TweetModel'
        view = 'greet.TweetView'
        controller = 'greet.TweetController'
    }

    Greet {
        model = 'greet.GreetModel'
        view = 'greet.GreetView'
        controller = 'greet.GreetController'
    }
}
