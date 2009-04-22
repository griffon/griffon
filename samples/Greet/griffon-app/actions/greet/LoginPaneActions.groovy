package greet

loginAction = action(
        name: 'Login',
        enabled: bind {!model.loggingIn},
        closure: controller.&login
    )
