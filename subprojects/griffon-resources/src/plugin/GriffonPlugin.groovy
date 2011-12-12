class @plugin.name@GriffonPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Griffon the plugin is designed for
    def griffonVersion = '@griffon.version@ > *' 
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are included in plugin packaging
    def pluginIncludes = []
    // the plugin license
    def license = '<UNKNOWN>'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    def toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    def platforms = []

    // TODO Fill in these fields
    def author = 'Your name'
    def authorEmail = ''
    def title = 'Plugin summary/headline'
    def description = '''
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = 'http://griffon.codehaus.org/@plugin.name@+Plugin'
}
