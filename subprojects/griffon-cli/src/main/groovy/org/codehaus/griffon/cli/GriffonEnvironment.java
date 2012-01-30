/*
 * Copyright 2012 the original author or authors.
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

package org.codehaus.griffon.cli;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static griffon.util.BuildSettingsHolder.getSettings;
import static griffon.util.GriffonNameUtils.isBlank;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.padLeft;

/**
 * @author Andres Almiray
 */
public class GriffonEnvironment {
    private static final Log LOG = LogFactory.getLog(GriffonEnvironment.class);

    private static final String GRIFFON_IMPLEMENTATION_TITLE = "griffon-rt";
    private static final String BUILD_DATE;
    private static final String BUILD_TIME;

    static {
        String buildDate = null;
        String buildTime = null;

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] manifests = resolver.getResources("classpath*:META-INF/MANIFEST.MF");
            Manifest griffonManifest = null;
            for (int i = 0; i < manifests.length; i++) {
                Resource r = manifests[i];
                InputStream inputStream = null;
                Manifest mf = null;
                try {
                    inputStream = r.getInputStream();
                    mf = new Manifest(inputStream);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
                String implTitle = mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
                if (!isBlank(implTitle) && implTitle.equals(GRIFFON_IMPLEMENTATION_TITLE)) {
                    griffonManifest = mf;
                    break;
                }
            }

            if (griffonManifest != null) {
                buildDate = griffonManifest.getMainAttributes().getValue("Build-Date");
                buildTime = griffonManifest.getMainAttributes().getValue("Build-Time");
            }

            if (isBlank(buildDate) || isBlank(buildTime)) {
                LOG.error("Unable to read Griffon version from MANIFEST.MF. Are you sure the griffon-rt jar is on the classpath?");
                buildDate = buildTime = "";
            }
        } catch (Exception e) {
            LOG.error("Unable to read Griffon version from MANIFEST.MF. Are you sure it the griffon-rt jar is on the classpath? " + e.getMessage(), e);
            buildDate = buildTime = "";
        }

        BUILD_DATE = buildDate;
        BUILD_TIME = buildTime;
    }

    public static String getGriffonVersion() {
        return getSettings().getGriffonVersion();
    }

    public static String getGroovyVersion() {
        return getSettings().getGroovyVersion();
    }

    public static String getAntVersion() {
        return getSettings().getAntVersion();
    }

    public static String getSlf4jVersion() {
        return getSettings().getSlf4jVersion();
    }

    public static String getSpringVersion() {
        return getSettings().getSpringVersion();
    }

    public static String getJvmVersion() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("java.version"))
                .append(" (")
                .append(System.getProperty("java.vendor"))
                .append(" ")
                .append(System.getProperty("java.vm.version"))
                .append(")");
        return sb.toString();
    }

    public static String getOsVersion() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("os.name"))
                .append(" ")
                .append(System.getProperty("os.version"))
                .append(" ")
                .append(System.getProperty("os.arch"));
        return sb.toString();
    }

    public static String prettyPrint() {
        padLeft("Griffon", 8);


        final StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------------------------------------------\n")
                .append(padLeft("Griffon", 9))
                .append(" ")
                .append(getGriffonVersion())
                .append("\n------------------------------------------------------------\n\n");
        entry("Build", BUILD_DATE + " " + BUILD_TIME, sb);
        entry("Groovy", getGroovyVersion(), sb);
        entry("Ant", getAntVersion(), sb);
        entry("Slf4j", getSlf4jVersion(), sb);
        entry("Spring", getSpringVersion(), sb);
        entry("JVM", getJvmVersion(), sb);
        entry("OS", getOsVersion(), sb);
        return sb.toString();
    }

    private static void entry(String label, String version, StringBuilder sb) {
        sb.append(padLeft(label, 8))
                .append(": ")
                .append(version)
                .append("\n");
    }
}
