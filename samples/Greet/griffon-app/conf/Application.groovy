application {
    title='Greet'
    startupGroups = ['Greet']

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "LoginPane"
    LoginPane {
        model = 'greet.LoginPaneModel'
        view = 'greet.LoginPaneView'
        controller = 'greet.LoginPaneController'
    }

    // MVC Group for "UserPane"
    UserPane {
        model = 'greet.UserPaneModel'
        view = 'greet.UserPaneView'
        controller = 'greet.UserPaneController'
    }

    // MVC Group for "TimelinePane"
    TimelinePane {
        model = 'greet.TimelinePaneModel'
        view = 'greet.TimelinePaneView'
        controller = 'greet.TimelinePaneController'
    }

    // MVC Group for "Greet"
    Greet {
        model = 'greet.GreetModel'
        view = 'greet.GreetView'
        controller = 'greet.GreetController'
    }

}
