class @plugin.name@GriffonPlugin {
    // the plugin version
    String version = '@plugin.version@'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '@griffon.version@ > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = '<UNKNOWN>'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []

    // TODO Fill in these fields
    List authors = [
        [
            name: 'Your Name',
            email: 'your@email.com'
        ]
    ]
    String title = 'Plugin summary/headline'
    // accepts Markdown syntax. See http://daringfireball.net/projects/markdown/ for details
    String description = '''
Brief description of @plugin.name@.

Usage
----
Lorem ipsum

Configuration
-------------
Lorem ipsum
'''

    // URL to the plugin's documentation
    String documentation = 'http://griffon.codehaus.org/@plugin.name@+Plugin'
}
