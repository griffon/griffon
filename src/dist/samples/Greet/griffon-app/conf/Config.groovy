// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error  'org.codehaus.griffon.runtime'

    warn   'griffon.util',
           'griffon.core',
           'griffon.swing'
}


// The following properties have been added by the Upgrade process...
griffon.jars.pack=false // jars were not automatically packed in Griffon 0.0
griffon.jars.sign=true // jars were automatically signed in Griffon 0.0
griffon.extensions.jarUrls = [] // remote jars were not possible in Griffon 0.1
griffon.extensions.jnlpUrls = [] // remote jars were not possible in Griffon 0.1
// may safely be removed, but calling upgrade will restore it
def env = griffon.util.Environment.current.name
signingkey.params.sigfile='GRIFFON' + env
signingkey.params.keystore = "${basedir}/griffon-app/conf/keys/${env}Keystore"
signingkey.params.alias = env
// signingkey.params.storepass = 'BadStorePassword'
// signingkey.params.keyPass = 'BadKeyPassword'
signingkey.params.lazy = true // only sign when unsigned
// you may now tweak memory parameters
//griffon.memory.min='16m'
//griffon.memory.max='64m'
//griffon.memory.maxPermSize='64m'
deploy {
    application {
        title = '@griffonAppName@ @griffonAppVersion@'
        vendor = System.properties['user.name']
        homepage = 'http://localhost/@griffonAppName@'
        description {
            complete = '@griffonAppName@ @griffonAppVersion@'
            oneline  = '@griffonAppName@ @griffonAppVersion@'
            minimal  = '@griffonAppName@ @griffonAppVersion@'
            tooltip  = '@griffonAppName@ @griffonAppVersion@'
        }
        icon {
            fallback {
                name = 'griffon-icon-48x48.png'
                width = '48'
                height = '48'
            }
            splash {
                name = 'griffon.png'
                width = '391'
                height = '123'
            }
            menu {
                name = 'griffon-icon-16x16.png'
                width = '48'
                height = '48'
            }
            desktop {
                name = 'griffon-icon-32x32.png'
                width = '32'
                height = '32'
            }
        }
    }
}
