// log4j configuration
log4j {
    appender.stdout = 'org.apache.log4j.ConsoleAppender'
    appender.'stdout.layout'='org.apache.log4j.PatternLayout'
    appender.'stdout.layout.ConversionPattern'='[%r] %c{2} %m%n'
    appender.errors = 'org.apache.log4j.FileAppender'
    appender.'errors.layout'='org.apache.log4j.PatternLayout'
    appender.'errors.layout.ConversionPattern'='[%r] %c{2} %m%n'
    appender.'errors.File'='stacktrace.log'
    rootLogger='error,stdout'
    logger {
        griffon='error'
        StackTrace='error,errors'
        org {
            codehaus.griffon.commons='info' // core / classloading
        }
    }
    additivity.StackTrace=false
}

// key signing information
environments {
    development {
        signingkey {
            params {
                sigfile = 'GRIFFON'
                keystore = "${basedir}/griffon-app/conf/keys/devKeystore"
                alias = 'development'
                storepass = 'BadStorePassword'
                keypass   = 'BadKeyPassword'
                lazy      = true // only sign when unsigned
            }
        }

    }
    test {
        griffon {
            jars {
                sign = false
                pack = false
            }
        }
    }
    production {
        signingkey {
            params {
                sigfile = 'GRIFFON'
                keystore = 'CHANGE ME'
                alias = 'CHANGE ME'
                // NOTE: for production keys it is more secure to rely on key prompting
                // no value means we will prompt //storepass = 'BadStorePassword'
                // no value means we will prompt //keypass   = 'BadKeyPassword'
                lazy = false // sign, regardless of existing signatures
            }
        }

        griffon {
            jars {
                sign = true
                pack = true
                destDir = "${basedir}/staging"
            }
            webstart {
                codebase = 'CHANGE ME'
            }
        }
    }
}

griffon {
    memory {
        //max = '64m'
        //min = '2m'
        //maxPermSize = '64m'
    }
    jars {
        sign = false
        pack = false
        destDir = "${basedir}/staging"
        jarName = "${appName}.jar"
    }
    extensions {
        jarUrls = []
        jnlpUrls = []
    }
    webstart {
        codebase = "${new File(griffon.jars.destDir).toURI().toASCIIString()}"
        jnlp = 'application.jnlp'
    }
    applet {
        jnlp = 'applet.jnlp'
        html = 'applet.html'
    }
}
