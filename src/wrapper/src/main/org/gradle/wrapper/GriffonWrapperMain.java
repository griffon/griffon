/*
 * Copyright 2007-2010 the original author or authors.
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

import java.io.File;

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
        addSystemProperties(args);
        
        if (isDebug()) {
            System.out.println(ALWAYS_UNPACK_ENV + " env variable: " + System.getenv(ALWAYS_UNPACK_ENV));
            System.out.println(ALWAYS_DOWNLOAD_ENV + " env variable: " + System.getenv(ALWAYS_DOWNLOAD_ENV));
        }
        boolean alwaysDownload = Boolean.parseBoolean(System.getenv(ALWAYS_DOWNLOAD_ENV));
        boolean alwaysUnpack = Boolean.parseBoolean(System.getenv(ALWAYS_UNPACK_ENV));

        new Wrapper().execute(
                args,
                new Install(alwaysDownload, alwaysUnpack, new Download(), new PathAssembler(griffonUserHome())),
                new BootstrapMainStarter());
    }

    private static void addSystemProperties(String[] args) {
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(args));
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File(griffonUserHome(), "griffon.properties")));
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File("griffon.properties")));
    }

    private static String griffonUserHome() {
        String griffonUserHome = System.getProperty(GRIFFON_USER_HOME_PROPERTY_KEY);
        if (griffonUserHome != null) {
            return griffonUserHome;
        } else if((griffonUserHome = System.getenv(GRIFFON_USER_HOME_ENV_KEY)) != null) {
            return griffonUserHome;
        } else {
            return DEFAULT_GRIFFON_USER_HOME;
        }
    }

    static boolean isDebug() {
        String prop = System.getProperty(DEBUG_PROPERTY_KEY);
        return prop != null && !prop.toUpperCase().equals("FALSE");
    }
}
