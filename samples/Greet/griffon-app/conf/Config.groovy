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
                keystore = '${base.dir}/griffon-app/conf/keys/productionKeystore'
                alias = 'greetkey'
                sigfile = "GRIFFON"
                //storepass = 'BadStorePassword' // no value means we will prompt
                //keypass   = 'BadKeyPassword' // no value means we will prompt
                lazy = false // sign, regardless of existing signatures
            }
        }

        griffon {
            jars {
                sign = true
                pack = true
                destDir = '${basedir}/../../bin-dist/greet'
                jarName = "${appName}.jar"
            }
            webstart {
                codebase = "http://svn.codehaus.org/groovy/trunk/groovy/modules/griffon/bin-dist/greet/"
                jnlp = "greet.jnlp"
            }
        }
    }
}

griffon {
    jars {
        sign = false
        pack = false
        destDir = "${basedir}/staging"
        jarName = "${appName}.jar"
    }
    webstart {
        codebase = "${new File(griffon.jars.destDir).toURI().toASCIIString()}"
        jnlp = "greet.jnlp"
    }
    applet {
        jnlp = "applet.jnlp"
        html = "applet.html"
    }
}
