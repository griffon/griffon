/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.plugins.logging;

import org.apache.log4j.Logger
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.PatternLayout
import org.apache.log4j.Level
import org.apache.log4j.FileAppender
import org.apache.log4j.xml.XMLLayout
import org.apache.log4j.HTMLLayout
import org.apache.log4j.SimpleLayout
import org.apache.log4j.jdbc.JDBCAppender
import org.apache.log4j.varia.NullAppender
import org.apache.log4j.net.SMTPAppender
import org.codehaus.griffon.util.GriffonUtil
import org.apache.log4j.helpers.LogLog

/**
 * Encapsulates the configuration of Log4j
 *
 * @author Graeme Rocher
 * @since 1.1
 */
class Log4jConfig {

    static final DEFAULT_PATTERN_LAYOUT = new PatternLayout(conversionPattern:'[%r] %c{2} %m%n')

    static final LAYOUTS = [xml: XMLLayout, html:HTMLLayout, simple:SimpleLayout, pattern:PatternLayout]
    static final APPENDERS = [jdbc:JDBCAppender, "null":NullAppender, console:ConsoleAppender, file:FileAppender]

    private appenders = [:]

    def methodMissing(String name, args) {
        if(APPENDERS.containsKey(name) && args) {
            def appender = APPENDERS[name].newInstance(args[0])
            if(!appender.name) {
                LogLog.error "Appender of type $name doesn't define a name attribute, and hence is ignored."
            }
            else {
                appenders[appender.name] = appender
            }
            return appenders[name]
        }
        else if(LAYOUTS.containsKey(name) && args) {
            return LAYOUTS[name].newInstance(args[0])
        }

        LogLog.error "Method missing when configuring log4j: $name"
    }

    def configure() {
        configure {}
    }
    def configure(Closure callable) {

        Logger root = Logger.getRootLogger()

        def consoleAppender = createConsoleAppender()
        root.setLevel Level.ERROR
        root.addAppender consoleAppender


        off 'org.springframework',
        'org.hibernate'

        Logger logger = Logger.getLogger("StackTrace")
        logger.additivity = false
        if(GriffonUtil.isDevelopmentEnv()) {
            def fileAppender = createFullstackTraceAppender()
            logger.addAppender fileAppender
        }

        callable.delegate = this
        callable.resolveStrategy = Closure.DELEGATE_FIRST

        try {
            callable.call()
        } catch (Exception e) {
            println "WARNING: Exception occured configuring log4j logging: $e.message"
        }

    }

    private createConsoleAppender() {
        def consoleAppender = new ConsoleAppender(layout:DEFAULT_PATTERN_LAYOUT, name:"stdout")
        consoleAppender.activateOptions()
        appenders.console = consoleAppender
        return consoleAppender
    }

    private createFullstackTraceAppender() {
        def fileAppender = new FileAppender(layout:DEFAULT_PATTERN_LAYOUT, name:"stacktraceLog")
        fileAppender.file = "stacktrace.log"
        fileAppender.activateOptions()
        appenders.stacktrace = fileAppender
        return fileAppender
    }

    def debug(Object[] packages) {
        eachLogger(packages) { Logger logger ->
            logger.level = Level.DEBUG
        }
    }

    def appenders(Closure callable) {
        callable.delegate = this
        callable.resolveStrategy = Closure.DELEGATE_FIRST
        callable.call()
    }


    def debug(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.DEBUG)
    }

    def error(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.ERROR)
    }


    def info(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.INFO)
    }

    def warn(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.WARN)
    }

    def all(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.ALL)
    }

    def off(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.OFF)
    }

    def fatal(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.FATAL)
    }

    def trace(Map appenderAndPackages) {
        setLogLevelForAppenderToPackageMap(appenderAndPackages, Level.TRACE)
    }
    
    private setLogLevelForAppenderToPackageMap(appenderAndPackages, Level level) {
        appenderAndPackages?.each { appender, packages ->
            eachLogger(packages) { Logger logger ->
                logger.level = level
                if(appenders[appender]) {
                    logger.addAppender appenders[appender]
                }
                else {
                    LogLog.error "Appender $appender not found configuring logger ${logger.getName()}"
                }
            }
        }

    }

    def eachLogger(packages, Closure callable) {
        if(packages instanceof String || packages instanceof GString) {
            Logger logger = Logger.getLogger(packages)
            callable(logger)
        }
        else {

            for(p in packages) {
                p = p?.toString()
                if(p) {
                    Logger logger = Logger.getLogger(p)
                    callable(logger)
                }
            }
        }

    }

    def error(Object[] packages) {
        eachLogger(packages) { logger ->
            logger.level = Level.ERROR
        }
    }

    def off(Object[] packages) {
        eachLogger(packages) { logger ->
            logger.level = Level.OFF
        }
    }

    def fatal(Object[] packages) {
        eachLogger(packages) { logger ->
            logger.level = Level.FATAL
        }
    }

    def warn(Object[] packages) {
        eachLogger(packages) { logger ->
            logger.level = Level.WARN
        }
    }

    def info(Object[] packages) {
        eachLogger(packages) { logger ->
            logger.level = Level.INFO
        }
    }

    def trace(Object[] packages) {
        eachLogger(packages) { logger ->
            logger.trace = Level.TRACE
        }
    }

    def all(Object[] packages) {
        eachLogger(packages) { logger ->
            logger.level = Level.ALL
        }
    }


    def removeAppender(String name) {
        Logger.getRootLogger().removeAppender name
    }
}