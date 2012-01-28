class @archetype.name@GriffonArchetype {
    // the archetype version
    String version = '@archetype.version@'
    // the version or versions of Griffon the archetype is designed for
    String griffonVersion = '@griffon.version@ > *'
    // the archetype license
    String license = '<UNKNOWN>'
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = ''

    List authors = [
        [
            name: 'Your Name',
            email: 'your@email.com'
        ]
    ]
    String title = 'Archetype summary/headline'
    // accepts Markdown syntax. See http://daringfireball.net/projects/markdown/ for details
    String description = '''
Brief description of @archetype.name@.

Usage
----
Lorem ipsum

Configuration
-------------
Lorem ipsum
'''
}
