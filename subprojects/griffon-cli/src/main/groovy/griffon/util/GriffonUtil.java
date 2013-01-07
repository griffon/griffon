/* 
 * Copyright 2004-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT c;pWARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Writable;
import groovy.util.slurpersupport.GPathResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.codehaus.griffon.cli.CommandLineConstants.KEY_FULL_STACKTRACE;

/**
 * Griffon utility methods for command line and GUI applications
 *
 * @author Graeme Rocher (Grails 0.2)
 */
public class GriffonUtil extends GriffonNameUtils {
    private GriffonUtil() {
    }

    private static final Log LOG = LogFactory.getLog(GriffonUtil.class);
    private static final Log STACK_LOG = LogFactory.getLog("StackTrace");
    private static final String GRIFFON_IMPLEMENTATION_TITLE = "griffon-rt";
    private static final String GRIFFON_VERSION;
    private static final String[] GRIFFON_PACKAGES = new String[]{
            // "org.codehaus.griffon.",
            "org.codehaus.groovy.runtime.",
            "org.codehaus.groovy.reflection.",
            "org.codehaus.groovy.ast.",
            "org.codehaus.gant.",
            // "griffon.",
            "groovy.",
            "sun.",
            "java.lang.reflect."
    };

    static {
        Package p = GriffonUtil.class.getPackage();
        String version = p != null ? p.getImplementationVersion() : null;
        if (version == null || isBlank(version)) {
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
                    version = griffonManifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                }

                if (isBlank(version)) {
                    LOG.error("Unable to read Griffon version from MANIFEST.MF. Are you sure the griffon-rt jar is on the classpath?");
                    version = "Unknown";
                }
            } catch (Exception e) {
                version = "Unknown";
                LOG.error("Unable to read Griffon version from MANIFEST.MF. Are you sure it the griffon-rt jar is on the classpath? " + e.getMessage(), e);
            }
        }

        GRIFFON_VERSION = version;
    }

    /**
     * Retrieves the current execution environment
     *
     * @return The environment Griffon is executing under
     */
    public static String getEnvironment() {
        return Environment.getCurrent().getName();
    }

    /**
     * Retrieves whether the current execution environment is the development one
     *
     * @return True if it is the development environment
     */
    public static boolean isDevelopmentEnv() {
        return Environment.getCurrent() == Environment.DEVELOPMENT;
    }

    public static String getGriffonVersion() {
        return GRIFFON_VERSION;
    }

    /**
     * Logs warning message about deprecation of specified property or method of some class.
     *
     * @param clazz            A class
     * @param methodOrPropName Name of deprecated property or method
     */
    public static void deprecated(Class clazz, String methodOrPropName) {
        deprecated(clazz, methodOrPropName, getGriffonVersion());
    }

    /**
     * Logs warning message about deprecation of specified property or method of some class.
     *
     * @param clazz            A class
     * @param methodOrPropName Name of deprecated property or method
     * @param version          Version of Griffon release in which property or method were deprecated
     */
    public static void deprecated(Class<?> clazz, String methodOrPropName, String version) {
        deprecated("Property or method [" + methodOrPropName + "] of class [" + clazz.getName() +
                "] is deprecated in [" + version +
                "] and will be removed in future releases");
    }

    /**
     * Logs warning message about some deprecation and code style related hints.
     *
     * @param message Message to display
     */
    public static void deprecated(String message) {
        LOG.warn("[DEPRECATED] " + message);
    }

    /**
     * Logs warning message to griffon.util.GriffonUtil logger which is turned on in development mode.
     *
     * @param message Message to display
     */
    public static void warn(String message) {
        LOG.warn("[WARNING] " + message);
    }


    /**
     * <p>Remove all apparently Griffon-internal trace entries from the exception instance<p>
     * <p>This modifies the original instance and returns it, it does not clone</p>
     *
     * @param t
     * @return The exception passed in, after cleaning the stack trace
     */
    public static Throwable sanitize(Throwable t) {
        // Note that this getProperty access may well be synced...
        if (!Boolean.valueOf(System.getProperty(KEY_FULL_STACKTRACE)).booleanValue()) {
            StackTraceElement[] trace = t.getStackTrace();
            List<StackTraceElement> newTrace = new ArrayList<StackTraceElement>();
            for (int i = 0; i < trace.length; i++) {
                StackTraceElement stackTraceElement = trace[i];
                if (isApplicationClass(stackTraceElement.getClassName())) {
                    newTrace.add(stackTraceElement);
                }
            }

            // Only trim the trace if there was some application trace on the stack
            // if not we will just skip sanitizing and leave it as is
            if (newTrace.size() > 0) {
                // We don't want to lose anything, so log it
                STACK_LOG.error("Sanitizing stacktrace:", t);
                StackTraceElement[] clean = new StackTraceElement[newTrace.size()];
                newTrace.toArray(clean);
                t.setStackTrace(clean);
            }
        }
        return t;
    }

    public static void printSanitizedStackTrace(Throwable t, PrintWriter p) {
        t = sanitize(t);

        StackTraceElement[] trace = t.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            StackTraceElement stackTraceElement = trace[i];
            p.println("at " + stackTraceElement.getClassName()
                    + "(" + stackTraceElement.getMethodName()
                    + ":" + stackTraceElement.getLineNumber() + ")");
        }
    }

    public static void printSanitizedStackTrace(Throwable t) {
        printSanitizedStackTrace(t, new PrintWriter(System.err));
    }

    public static boolean isApplicationClass(String className) {
        for (int i = 0; i < GRIFFON_PACKAGES.length; i++) {
            String griffonPackage = GRIFFON_PACKAGES[i];
            if (className.startsWith(griffonPackage)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Extracts the root cause of the exception, no matter how nested it is</p>
     *
     * @param t the throwable to sanitize
     * @return The deepest cause of the exception that can be found
     */
    public static Throwable extractRootCause(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }

    /**
     * <p>Get the root cause of an exception and sanitize it for display to the user</p>
     * <p>This will MODIFY the stacktrace of the root cause exception object and return it</p>
     *
     * @param t the throwable to sanitize
     * @return The root cause exception instance, with its stace trace modified to filter out griffon runtime classes
     */
    public static Throwable sanitizeRootCause(Throwable t) {
        return sanitize(extractRootCause(t));
    }

    /**
     * <p>Sanitize the exception and ALL nested causes</p>
     * <p>This will MODIFY the stacktrace of the exception instance and all its causes irreversibly</p>
     *
     * @param t
     * @return The root cause exception instances, with stack trace modified to filter out griffon runtime classes
     */
    public static Throwable deepSanitize(Throwable t) {
        Throwable current = t;
        while (current.getCause() != null) {
            current = sanitize(current.getCause());
        }
        return sanitize(t);
    }

    /**
     * Writes out a GPathResult (i.e. the result of parsing XML using
     * XmlSlurper) to the given writer.
     *
     * @param result The root node of the XML to write out.
     * @param output Where to write the XML to.
     * @throws java.io.IOException If the writing fails due to a closed stream
     *                             or unwritable file.
     */
    public static void writeSlurperResult(GPathResult result, Writer output) throws IOException {
        Binding b = new Binding();
        b.setVariable("node", result);
        // this code takes the XML parsed by XmlSlurper and writes it out using StreamingMarkupBuilder
        // don't ask me how it works, refer to John Wilson ;-)
        Writable w = (Writable) new GroovyShell(b).evaluate(
                "new groovy.xml.StreamingMarkupBuilder().bind {" +
                        " mkp.declareNamespace(\"\":  \"http://java.sun.com/xml/ns/j2ee\");" +
                        " mkp.yield node}");
        w.writeTo(output);
    }

    /**
     * Retrieves the script name representation of the supplied class. For example
     * MyFunkyGriffonScript would be my-funky-griffon-script
     *
     * @param clazz The class to convert
     * @return The script name representation
     */
    public static String getScriptName(Class clazz) {
        return getHyphenatedName(clazz);
    }

    /**
     * Retrieves the script name representation of the given class name.
     * For example MyFunkyGriffonScript would be my-funky-griffon-script.
     *
     * @param name The class name to convert.
     * @return The script name representation.
     */
    public static String getScriptName(String name) {
        return getHyphenatedName(name);
    }

    /**
     * Calculates the class name from a script name in the form
     * my-funk-griffon-script
     *
     * @param scriptName The script name
     * @return A class name
     */
    public static String getNameFromScript(String scriptName) {
        return getClassNameForLowerCaseHyphenSeparatedName(scriptName);
    }

    /**
     * Returns the name of a plugin given the name of the *GriffonPlugin.groovy
     * descriptor file. For example, "DbUtilsGriffonPlugin.groovy" gives
     * "db-utils".
     *
     * @param descriptorName The simple name of the plugin descriptor.
     * @return The plugin name for the descriptor, or <code>null</code>
     *         if <i>descriptorName</i> is <code>null</code>, or an empty string
     *         if <i>descriptorName</i> is an empty string.
     * @throws IllegalArgumentException if the given descriptor name is
     *                                  not valid, i.e. if it doesn't end with "GriffonPlugin.groovy".
     */
    public static String getPluginName(String descriptorName) {
        if (descriptorName == null || descriptorName.length() == 0) {
            return descriptorName;
        }

        if (!descriptorName.endsWith("GriffonPlugin.groovy")) {
            throw new IllegalArgumentException("Plugin descriptor name is not valid: " + descriptorName);
        }

        int pos = descriptorName.indexOf("GriffonPlugin.groovy");
        return getScriptName(descriptorName.substring(0, pos));
    }
}
