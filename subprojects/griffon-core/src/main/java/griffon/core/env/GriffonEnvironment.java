/*
 * Copyright 2008-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.core.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Properties;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public class GriffonEnvironment {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonEnvironment.class);

    private static final String BUILD_DATE;
    private static final String BUILD_TIME;
    private static final String BUILD_REVISION;
    private static final String GRIFFON_VERSION;

    static {
        String buildDate = null;
        String buildTime = null;
        String buildRevision = null;
        String version = null;

        try {
            Properties griffonProperties = new Properties();
            URL griffonPropertiesResource = GriffonEnvironment.class.getClassLoader().getResource("META-INF/griffon-core.properties");

            if (griffonPropertiesResource != null) {
                griffonProperties.load(griffonPropertiesResource.openStream());
                buildDate = griffonProperties.getProperty("build.date");
                buildTime = griffonProperties.getProperty("build.time");
                buildRevision = griffonProperties.getProperty("build.revision");
                version = griffonProperties.getProperty("griffon.version");
            }

            if (isBlank(buildDate) || isBlank(buildTime) || isBlank(version)) {
                LOG.error("Unable to read Griffon version from META-INF/griffon-core.properties. Are you sure the griffon-core jar is in the classpath?");
                buildDate = buildTime = buildRevision = version = "";
            }
        } catch (Exception e) {
            LOG.error("Unable to read Griffon version from META-INF/griffon-core.properties. Are you sure the griffon-core jar is in the classpath? " + e.getMessage(), e);
            buildDate = buildTime = buildRevision = version = "";
        }

        BUILD_DATE = buildDate;
        BUILD_TIME = buildTime;
        BUILD_REVISION = buildRevision;
        GRIFFON_VERSION = version;
    }

    private GriffonEnvironment() {
        // disable instantiation
    }

    public static String getGriffonVersion() {
        return GRIFFON_VERSION;
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

    public static String getBuildDateTime() {
        return BUILD_DATE + "T" + BUILD_TIME;
    }

    public static String getBuildDate() {
        return BUILD_DATE;
    }

    public static String getBuildTime() {
        return BUILD_TIME;
    }

    public static String getBuildRevision() {
        return BUILD_REVISION;
    }

    public static String prettyPrint() {
        padLeft("Griffon", 8, " ");

        final StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------------------------------------------\n")
            .append(padLeft("Griffon", 9, " "))
            .append(" ")
            .append(getGriffonVersion())
            .append("\n------------------------------------------------------------\n\n");
        entry("Build", getBuildDateTime(), sb);
        entry("Revision", getBuildRevision(), sb);
        entry("JVM", getJvmVersion(), sb);
        entry("OS", getOsVersion(), sb);
        return sb.toString();
    }

    private static void entry(String label, String version, StringBuilder sb) {
        sb.append(padLeft(label, 8, " "))
            .append(": ")
            .append(version)
            .append("\n");
    }

    private static String padLeft(String self, Number numberOfChars, String padding) {
        int numChars = numberOfChars.intValue();
        if (numChars <= self.length()) {
            return self;
        } else {
            return getPadding(padding, numChars - self.length()) + self;
        }
    }

    private static String getPadding(String padding, int length) {
        if (padding.length() < length) {
            return multiply(padding, length / padding.length() + 1).substring(0, length);
        } else {
            return padding.substring(0, length);
        }
    }

    private static String multiply(String self, Number factor) {
        int size = factor.intValue();
        if (size == 0)
            return "";
        else if (size < 0) {
            throw new IllegalArgumentException("multiply() should be called with a number of 0 or greater not: " + size);
        }
        StringBuilder answer = new StringBuilder(self);
        for (int i = 1; i < size; i++) {
            answer.append(self);
        }
        return answer.toString();
    }
}
