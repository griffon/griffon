/*
 * Copyright 2010 the original author or authors.
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

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.codehaus.groovy.control.SourceUnit;

/**
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public class GriffonCompilerContext {
    public static boolean verbose;
    public static String basedir;
    public static String projectName;

    public static Pattern isArtifactPattern;
    public static Pattern isAddonPattern;
    public static Pattern isTestPattern;
    public static Pattern[] scriptPatterns;
    public static Pattern[] excludedArtifacts;
    public static Pattern groovyArtifactPattern;

    private static final String[] ARTIFACT_EXCLUDES = { "conf", "i18n", "resources" };
    private static final boolean isWindows = System.getProperty("os.name").matches("Windows.*");

    private static Pattern normalizePattern(String regex) {
        if(isWindows) {
            StringBuilder b = new StringBuilder();
            int size = regex.length();
            int i = 0;
            while(i < size) {
                char c = regex.charAt(i++);
                if(c == '\\') b.append("\\\\"); else b.append(c);
            }
            regex = b.toString();
        }

        return Pattern.compile(regex);
    }

    public static void setup() {
        isArtifactPattern = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + ".*$");
        excludedArtifacts = new Pattern[ARTIFACT_EXCLUDES.length];
        int i = 0;
        for(String dir : ARTIFACT_EXCLUDES) {
            excludedArtifacts[i++] = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + dir + File.separator + ".*$");        
        }
        groovyArtifactPattern = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + "([a-z]+)" + File.separator + ".*.groovy$");
        isAddonPattern = normalizePattern("^" + basedir + File.separator + ".*GriffonAddon.groovy$");
        scriptPatterns = new Pattern[2];
        scriptPatterns[0] = normalizePattern("^" + basedir + File.separator + "griffon-app" + File.separator + "conf" + File.separator + ".*.groovy$");
        scriptPatterns[1] = normalizePattern("^" + basedir + File.separator + "scripts" + File.separator + ".*.groovy$");
        isTestPattern = normalizePattern("^" + basedir + File.separator + "test" + File.separator + ".*.groovy$");
    }

    public static boolean isGriffonArtifact(SourceUnit source) {
         if(projectName == null) return false;
    
         for(Pattern p : excludedArtifacts) {
             if(p.matcher(source.getName()).matches()) return false;
         }
         return isArtifactPattern.matcher(source.getName()).matches();  
    }

    public static boolean isGriffonAddon(SourceUnit source) {
         if(projectName == null) return false;
         return isAddonPattern.matcher(source.getName()).matches();
    }

    public static boolean isGriffonScript(SourceUnit source) {
         if(projectName == null) return false;

         for(Pattern p : scriptPatterns) {
             if(p.matcher(source.getName()).matches()) return true;
         }
         return false;
    }

    public static boolean isTestSource(SourceUnit source) {
         if(projectName == null) return false;
         return isTestPattern.matcher(source.getName()).matches();
    }

    /**
     * Merges two String arays.<p>
     * Never returns null
     */
    public static String[] merge(String[] a, String[] b) {
        if(a == null) a = new String[0];
        if(b == null) b = new String[0];

        List<String> c = new ArrayList<String>();
        for(String s : a) { s = s.trim(); if(!c.contains(s)) c.add(s); }
        for(String s : b) { s = s.trim(); if(!c.contains(s)) c.add(s); }

        return c.toArray(new String[c.size()]);    
    }
}
