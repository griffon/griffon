import javax.swing.JOptionPane
import griffon.core.ShutdownHandler

onBootstrapEnd = { app ->
    app.config.shutdown.proceed = false

    app.addShutdownHandler([
            canShutdown: { a ->
                app.config.shutdown.proceed = JOptionPane.showConfirmDialog(
                        app.windowManager.windows.find {it.focused},
                        app.getMessage('application.confirm.shutdown.message', 'Do you really want to exit?'),
                        app.getMessage('application.confirm.shutdown.title','Exit'),
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION
                return app.config.shutdown.proceed
            },
            onShutdown: { a -> }
    ] as ShutdownHandler)
}

onShutdownAborted = { app ->
    app.config.shutdown.proceed = false
}