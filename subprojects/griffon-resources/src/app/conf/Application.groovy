package config

application {
    title = '@griffon.app.class.name@'
    startupGroups = ['@griffon.default.mvc@']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
