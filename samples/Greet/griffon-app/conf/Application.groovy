application {
    title="Greet"
    startupGroups=['Greet']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true
}

mvcGroups {
    // MVC Group for "Greet"
    Greet {
        model = 'greet.GreetModel'
        view = 'greet.GreetView'
        controller = 'greet.GreetController'
    }
}
