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
    // Valid values are: swing, javafx, swt, pivot, qt
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = ''
    // Map of Bnd directives and/or Manifest entries
    // see http://www.aqute.biz/Bnd/Bnd for reference
    Map osgiManifest = [
        'Bundle-Description': 'Plugin summary/headline'
    ]

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
}
