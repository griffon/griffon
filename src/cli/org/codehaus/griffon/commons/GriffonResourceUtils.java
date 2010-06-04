/*
 * Copyright 2004-2010 the original author or authors.
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
package org.codehaus.griffon.commons;

//import java.io.File;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.codehaus.griffon.exceptions.GriffonConfigurationException;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for working with Griffon resources and URLs that represent artifacts
 * within a Griffon application
 *
 * @author Graeme Rocher
 *
 * @since 0.2
 *
 * Created: 20th June 2006
 */
public class GriffonResourceUtils {

    //private static final String FS = File.separator;

    /**
     * The name of the Griffon application directory
     */
    public static final String GRIFFON_APP_DIR = "griffon-app";

    /*
    This pattern will match any resource within a given directory inside griffon-app
    */
    public static Pattern RESOURCE_PATH_PATTERN = Pattern.compile(".+?/"+GRIFFON_APP_DIR+"/(.+?)/(.+?\\.groovy)");

    public static Pattern SPRING_SCRIPTS_PATH_PATTERN = Pattern.compile(".+?/"+GRIFFON_APP_DIR+"/conf/spring/(.+?\\.groovy)");

    public static Pattern[] COMPILER_ROOT_PATTERNS = {
        SPRING_SCRIPTS_PATH_PATTERN,
        RESOURCE_PATH_PATTERN
    };

    /*
    Resources are resolved against the platform specific path and must therefore obey the
    specific File.separator.
    */
    public static final Pattern GRIFFON_RESOURCE_PATTERN_FIRST_MATCH;
    public static final Pattern GRIFFON_RESOURCE_PATTERN_SECOND_MATCH;
    public static final Pattern GRIFFON_RESOURCE_PATTERN_THIRD_MATCH;
    public static final Pattern GRIFFON_RESOURCE_PATTERN_FOURTH_MATCH;

    static {
        String fs = File.separator;
        if (fs.equals("\\")) fs = "\\\\"; // backslashes need escaping in regexes

        GRIFFON_RESOURCE_PATTERN_FIRST_MATCH = Pattern.compile(createGriffonResourcePattern(fs, GRIFFON_APP_DIR +fs +"\\w+"));
        GRIFFON_RESOURCE_PATTERN_THIRD_MATCH = Pattern.compile(createGriffonResourcePattern(fs, "griffon-tests"));
        fs = "/";
        GRIFFON_RESOURCE_PATTERN_SECOND_MATCH = Pattern.compile(createGriffonResourcePattern(fs, GRIFFON_APP_DIR +fs +"\\w+"));
        GRIFFON_RESOURCE_PATTERN_FOURTH_MATCH = Pattern.compile(createGriffonResourcePattern(fs, "griffon-tests"));
    }

    public static final Pattern[] patterns = new Pattern[]{
            GRIFFON_RESOURCE_PATTERN_FIRST_MATCH,
            GRIFFON_RESOURCE_PATTERN_SECOND_MATCH,
            GRIFFON_RESOURCE_PATTERN_THIRD_MATCH,
            GRIFFON_RESOURCE_PATTERN_FOURTH_MATCH
    };
    //private static final Log LOG = LogFactory.getLog(GriffonResourceUtils.class);


    private static String createGriffonResourcePattern(String separator, String base) {
        return ".+"+separator +base+separator +"(.+)\\.groovy";
    }

    /**
     * Returns the class name for a Griffon resource
     *
     * @param path The path to check
     * @return The class name or null if it doesn't exist
     */
    public static String getClassName(String path) {
        for (Pattern pattern : patterns) {
            Matcher m = pattern.matcher(path);
            if (m.find()) {
                return m.group(1).replaceAll("[/\\\\]", ".");
            }
        }
        return null;
    }

    /**
     * Checks whether the specified path is a Griffon path
     *
     * @param path The path to check
     * @return True if it is a Griffon path
     */
    public static boolean isGriffonPath(String path) {
        for (Pattern pattern : patterns) {
            Matcher m = pattern.matcher(path);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }



    private static final Pattern PLUGIN_PATTERN = Pattern.compile(".+?(/plugins/.+?/"+GRIFFON_APP_DIR+"/.+)");

    /**
     * Get the path relative to an artefact folder under griffon-app i.e:
     *
     * Input: /usr/joe/project/griffon-app/conf/BootStrap.groovy
     * Output: BootStrap.groovy
     *
     * Input: /usr/joe/project/griffon-app/domain/com/mystartup/Book.groovy
     * Output: com/mystartup/Book.groovy
     *
     * @param path The path to evaluate
     * @return The path relative to the root folder griffon-app
     */
    public static String getPathFromRoot(String path) {
        for (Pattern aCOMPILER_ROOT_PATTERNS : COMPILER_ROOT_PATTERNS) {
            Matcher m = aCOMPILER_ROOT_PATTERNS.matcher(path);
            if (m.find()) {
                return m.group(m.groupCount());
            }
        }
        return null;
    }
}
