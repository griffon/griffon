/*
 * Copyright 2010-2011 the original author or authors.
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

package org.codehaus.griffon.compiler;

import griffon.util.BuildSettings;
import griffon.util.BuildSettingsHolder;
import groovy.lang.Closure;
import groovy.util.ConfigObject;
import org.apache.log4j.LogManager;
import org.codehaus.griffon.runtime.logging.Log4jConfig;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andres Almiray
 * @since 0.9.1
 */
public class GriffonCompilerContext {
    public static final String DISABLE_AST_INJECTION = "griffon.disable.ast.injection";
    public static final String DISABLE_AUTO_IMPORTS = "griffon.disable.auto.imports";
    public static final String DISABLE_LOGGING_INJECTION = "griffon.disable.logging.injection";
    public static final String DISABLE_THREADING_INJECTION = "griffon.disable.threading.injection";

    public static boolean verbose;
    public static String basedir;
    public static String projectName;

    public static Pattern isArtifactPattern;
    public static Pattern isAddonPattern;
    public static Pattern isTestPattern;
    public static Pattern[] scriptPatterns;
    public static Pattern[] excludedArtifacts;
    public static Pattern griffonArtifactPattern;

    private static final String[] ARTIFACT_EXCLUDES = {"conf", "i18n", "resources"};
    private static final boolean isWindows = System.getProperty("os.name").matches("Windows.*");

    private static Pattern normalizePattern(String regex) {
        if (isWindows) {
            StringBuilder b = new StringBuilder();
            int size = regex.length();
            int i = 0;
            while (i < size) {
                char c = regex.charAt(i++);
                if (c == '\\') b.append("\\\\");
                else b.append(c);
            }
            regex = b.toString();
        }

        return Pattern.compile(regex);
    }

    public static void setup() {
        isArtifactPattern = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + ".*$");
        excludedArtifacts = new Pattern[ARTIFACT_EXCLUDES.length];
        int i = 0;
        for (String dir : ARTIFACT_EXCLUDES) {
            excludedArtifacts[i++] = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + dir + File.separator + ".*$");
        }
        griffonArtifactPattern = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + "([a-z]+)" + File.separator + ".*.groovy$");
        isAddonPattern = normalizePattern("^" + basedir + File.separator + ".*GriffonAddon(.groovy|.java)$");
        scriptPatterns = new Pattern[2];
        scriptPatterns[0] = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + "conf" + File.separator + ".*.groovy$");
        scriptPatterns[1] = normalizePattern("^" + basedir + File.separator + "scripts" + File.separator + ".*.groovy$");
        isTestPattern = normalizePattern("^" + basedir + File.separator + "test" + File.separator + ".*.groovy$");

        setLoggingOptions();
    }

    public static boolean isGriffonArtifact(SourceUnit source) {
        if (source == null) return false;
        return isGriffonArtifact(source.getName());
    }

    public static boolean isGriffonArtifact(String path) {
        if (projectName == null) return false;

        for (Pattern p : excludedArtifacts) {
            if (p.matcher(path).matches()) return false;
        }
        return isArtifactPattern.matcher(path).matches();
    }

    public static boolean isGriffonAddon(SourceUnit source) {
        if (source == null) return false;
        return isGriffonAddon(source.getName());
    }

    public static boolean isGriffonAddon(String path) {
        if (projectName == null) return false;
        return isAddonPattern.matcher(path).matches();
    }

    public static boolean isGriffonScript(SourceUnit source) {
        if (source == null) return false;
        return isGriffonScript(source.getName());
    }

    public static boolean isGriffonScript(String path) {
        if (projectName == null) return false;

        for (Pattern p : scriptPatterns) {
            if (p.matcher(path).matches()) return true;
        }
        return false;
    }

    public static boolean isTestSource(SourceUnit source) {
        if (source == null) return false;
        return isTestSource(source.getName());
    }

    public static boolean isTestSource(String path) {
        if (projectName == null) return false;
        return isTestPattern.matcher(path).matches();
    }

    public static String getArtifactPath(SourceUnit source) {
        if (source == null) return null;
        return getArtifactPath(source.getName());
    }

    public static String getArtifactPath(String path) {
        Matcher matcher = griffonArtifactPattern.matcher(path);
        return matcher.matches() ? matcher.group(1) : null;
    }

    /**
     * Merges two String arrays.<p>
     * Never returns null
     */
    public static String[] merge(String[] a, String[] b) {
        if (a == null) a = new String[0];
        if (b == null) b = new String[0];

        List<String> c = new ArrayList<String>();
        for (String s : a) {
            s = s.trim();
            if (!c.contains(s)) c.add(s);
        }
        for (String s : b) {
            s = s.trim();
            if (!c.contains(s)) c.add(s);
        }

        return c.toArray(new String[c.size()]);
    }

    public static ConfigObject getBuildSettings() {
        BuildSettings settings = BuildSettingsHolder.getSettings();
        return settings != null ? settings.getConfig() : new ConfigObject();
    }

    public static Map getFlattenedBuildSettings() {
        return getBuildSettings().flatten(new LinkedHashMap());
    }

    public static boolean getConfigOption(String key) {
        if (System.getProperty(key) != null) return Boolean.getBoolean(key);
        Object value = getFlattenedBuildSettings().get(key);
        if (value != null) return DefaultTypeTransformation.castToBoolean(value);
        return false;
    }

    private static void setLoggingOptions() {
        Object log4jConfig = getBuildSettings().get("log4j");
        if (log4jConfig instanceof Closure) {
            LogManager.resetConfiguration();
            new Log4jConfig().configure((Closure) log4jConfig);
        }
    }
}
