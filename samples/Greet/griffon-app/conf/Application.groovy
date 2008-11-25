application {
    title="Greet"
    startupGroups=['Greet']
}

// MVC Group for "Greet"
mvcGroups {
    Greet {
        model = 'greet.GreetModel'
        view = 'greet.GreetView'
        controller = 'greet.GreetController'
    }
}
