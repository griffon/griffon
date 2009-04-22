application {
    title='Greet'
    startupGroups = ['Greet']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "LoginPane"
    LoginPane {
        model = 'greet.LoginPaneModel'
        actions = 'greet.LoginPaneActions'
        view = 'greet.LoginPaneView'
        controller = 'greet.LoginPaneController'
    }

    // MVC Group for "UserPane"
    UserPane {
        model = 'greet.UserPaneModel'
        controller = 'greet.UserPaneController'
        view = 'greet.UserPaneView'
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
        actions = 'greet.GreetActions'
        controller = 'greet.GreetController'
        view = 'greet.GreetView'
    }

}
