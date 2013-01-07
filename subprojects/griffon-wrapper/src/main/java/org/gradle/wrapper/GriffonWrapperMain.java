/*
 * Copyright 2007-2013 the original author or authors.
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

package org.gradle.wrapper;

import org.gradle.cli.CommandLineParser;
import org.gradle.cli.CommandLineParserFactory;
import org.gradle.cli.SystemPropertiesCommandLineConverter;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperties;
import static org.gradle.wrapper.SystemPropertiesHandler.getSystemProperties;
import static org.gradle.wrapper.WrapperExecutor.forWrapperPropertiesFile;

/**
 * @author Hans Dockter
 * @author Andres Almiray
 */
public class GriffonWrapperMain {
    public static final String ALWAYS_UNPACK_ENV = "GRIFFON_WRAPPER_ALWAYS_UNPACK";
    public static final String ALWAYS_DOWNLOAD_ENV = "GRIFFON_WRAPPER_ALWAYS_DOWNLOAD";
    public static final String DEFAULT_GRIFFON_USER_HOME = System.getProperty("user.home") + "/.griffon";
    public static final String GRIFFON_USER_HOME_PROPERTY_KEY = "griffon.user.home";
    public static final String GRIFFON_USER_HOME_ENV_KEY = "GRIFFON_USER_HOME";
    public static final String DEBUG_PROPERTY_KEY = "griffon.wrapper.debug";

    public static void main(String[] args) throws Exception {
        File wrapperJar = wrapperJar();
        File propertiesFile = wrapperProperties(wrapperJar);
        File rootDir = rootDir(wrapperJar);

        Properties systemProperties = getProperties();
        systemProperties.putAll(parseSystemPropertiesFromArgs(args));

        addSystemProperties(rootDir);

        boolean alwaysDownload = parseBoolean(System.getenv(ALWAYS_DOWNLOAD_ENV));
        boolean alwaysUnpack = parseBoolean(System.getenv(ALWAYS_UNPACK_ENV));

        forWrapperPropertiesFile(propertiesFile, System.out).execute(
                args,
                new Install(alwaysDownload, alwaysUnpack, new Download(), new PathAssembler(griffonUserHome())),
                new BootstrapMainStarter());
    }

    private static Map<String, String> parseSystemPropertiesFromArgs(String[] args) {
        SystemPropertiesCommandLineConverter converter = new SystemPropertiesCommandLineConverter();
        converter.setCommandLineParserFactory(new CommandLineParserFactory() {
            public CommandLineParser create() {
                return new CommandLineParser().allowUnknownOptions();
            }
        });

        return converter.convert(Arrays.asList(args));
    }

    private static void addSystemProperties(File rootDir) {
        getProperties().putAll(getSystemProperties(new File(griffonUserHome(), "griffon.properties")));
        getProperties().putAll(getSystemProperties(new File(rootDir, "griffon.properties")));
    }

    private static File rootDir(File wrapperJar) {
        return wrapperJar.getParentFile().getParentFile().getParentFile();
    }

    private static File wrapperProperties(File wrapperJar) {
        return new File(wrapperJar.getParent(), wrapperJar.getName().replaceFirst("\\.jar$", ".properties"));
    }

    private static File wrapperJar() {
        URI location;
        try {
            location = GriffonWrapperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!location.getScheme().equals("file")) {
            throw new RuntimeException(String.format("Cannot determine classpath for wrapper Jar from codebase '%s'.", location));
        }
        return new File(location.getPath());
    }

    private static File griffonUserHome() {
        String griffonUserHome = System.getProperty(GRIFFON_USER_HOME_PROPERTY_KEY);
        if (griffonUserHome != null) {
            return new File(griffonUserHome);
        } else if ((griffonUserHome = System.getenv(GRIFFON_USER_HOME_ENV_KEY)) != null) {
            return new File(griffonUserHome);
        } else {
            return new File(DEFAULT_GRIFFON_USER_HOME);
        }
    }

    static boolean isDebug() {
        String prop = System.getProperty(DEBUG_PROPERTY_KEY);
        return prop != null && !prop.toUpperCase().equals("FALSE");
    }
}
