/* Copyright 2004-2005 the original author or authors.
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
package org.codehaus.griffon.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Writable;
import groovy.util.slurpersupport.GPathResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.codehaus.griffon.commons.ApplicationAttributes;
//import org.codehaus.griffon.commons.ApplicationHolder;
//import org.codehaus.griffon.commons.DefaultGriffonContext;
//import org.codehaus.griffon.commons.GriffonContext;
//import org.codehaus.griffon.commons.spring.GriffonRuntimeConfigurator;
//import org.codehaus.griffon.support.MockApplicationContext;
//import org.codehaus.griffon.support.MockResourceLoader;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.mock.web.MockServletContext;
//import org.springframework.util.Assert;
//import org.codehaus.griffon.util.BuildSettings;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.commons.GriffonContextHolder;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 *
 * Griffon utility methods for command line and GUI applications
 *
 * @author Graeme Rocher
 * @since 0.2
 *
 * @version $Revision: 7651 $
 * First Created: 02-Jun-2006
 * Last Updated: $Date: 2008-11-17 04:43:26 -0700 (Mon, 17 Nov 2008) $
 *
 */
public class GriffonUtil {

	private static final Log LOG  = LogFactory.getLog(GriffonUtil.class);
    private static final Log STACK_LOG  = LogFactory.getLog("StackTrace");
    private static final String GRIFFON_IMPLEMENTATION_TITLE = "griffon-rt";
    private static final String GRIFFON_VERSION;
    private static final String[] GRIFFON_PACKAGES = new String[] {
            "org.codehaus.griffon.",
            "org.codehaus.groovy.runtime.",
            "org.codehaus.groovy.reflection.",
            "org.codehaus.groovy.ast.",
            "org.codehaus.gant.",
            "griffon.",
            "groovy.",
            "org.mortbay.",
            "sun.",
            "java.lang.reflect.",
            "org.springframework.",
            "com.opensymphony.",
            "org.hibernate.",
            "javax.servlet."
    };

    static {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String version = null;
        try {
            Resource[] manifests = resolver.getResources("classpath*:META-INF/MANIFEST.MF");
            Manifest griffonManifest = null;
            for (int i = 0; i < manifests.length; i++) {
                Resource r = manifests[i];
                Manifest mf = new Manifest(r.getInputStream());
                String implTitle = mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
                if (!isBlank(implTitle) && implTitle.equals(GRIFFON_IMPLEMENTATION_TITLE)) {
                    griffonManifest = mf;
                    break;
                }
            }

            if(griffonManifest != null) {
                version = griffonManifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            }

            if(isBlank(version)) {
                LOG.error("Unable to read Griffon version from MANIFEST.MF. Are you sure the griffon-core jar is on the classpath? " );
                version = "Unknown";
            }
        } catch (IOException e) {
            version = "Unknown";
            StackTraceUtils.deepSanitize(e).printStackTrace();            
            LOG.error("Unable to read Griffon version from MANIFEST.MF. Are you sure it the griffon-core jar is on the classpath? " + e.getMessage(), e);
        }

        GRIFFON_VERSION = version;
    }

    private static final String PRODUCTION_ENV_SHORT_NAME = "prod";
    private static final String DEVELOPMENT_ENVIRONMENT_SHORT_NAME = "dev";
    private static final String TEST_ENVIRONMENT_SHORT_NAME = "test";

    private static Map envNameMappings = new HashMap() {{
        put(DEVELOPMENT_ENVIRONMENT_SHORT_NAME, BuildSettings.ENV_DEVELOPMENT);
        put(PRODUCTION_ENV_SHORT_NAME, BuildSettings.ENV_PRODUCTION);
        put(TEST_ENVIRONMENT_SHORT_NAME, BuildSettings.ENV_TEST);
    }};


    /**
     * <p>Bootstraps a Griffon application from the current classpath. The method will look for an applicationContext.xml file in the classpath
     * that must contain a bean of type GriffonContext and id GriffonContext
     *
     * <p>The method will then bootstrap Griffon with the GriffonContext and load all Griffon plug-ins found in the path
     *
     * @return The Griffon ApplicationContext instance
     */
//    public static ApplicationContext bootstrapGriffonFromClassPath() {
//		LOG.info("Loading Griffon environment");
//		ApplicationContext parent = new ClassPathXmlApplicationContext("applicationContext.xml");
//		DefaultGriffonContext application = (DefaultGriffonContext)parent.getBean("GriffonContext", DefaultGriffonContext.class);
//
//        return createGriffonContextContext(parent, application);
//	}

//    private static ApplicationContext createGriffonContextContext(ApplicationContext parent, GriffonContext application) {
//        GriffonRuntimeConfigurator config = new GriffonRuntimeConfigurator(application,parent);
//        MockServletContext servletContext = new MockServletContext(new MockResourceLoader());
//        ConfigurableApplicationContext appCtx = (ConfigurableApplicationContext)config.configure(servletContext);
//        servletContext.setAttribute( ApplicationAttributes.APPLICATION_CONTEXT, appCtx);
//        Assert.notNull(appCtx);
//        return appCtx;
//    }

    /**
     * Bootstraps Griffon with the given GriffonContext instance
     *
     * @param application The GriffonContext instance
     * @return A Griffon ApplicationContext
     */
//    public static ApplicationContext bootstrapGriffonFromApplication(GriffonContext application) {
//        MockApplicationContext parent = new MockApplicationContext();
//        parent.registerMockBean(GriffonContext.APPLICATION_ID, application);
//
//        return createGriffonContextContext(parent, application);
//    }

	/**
	 * Bootstraps Griffon from the given parent ApplicationContext which should contain a bean definition called "GriffonContext"
	 * of type GriffonContext
	 */
//	public static ApplicationContext bootstrapGriffonFromParentContext(ApplicationContext parent) {
//		DefaultGriffonContext application = (DefaultGriffonContext)parent.getBean("GriffonContext", DefaultGriffonContext.class);
//
//        return createGriffonContextContext(parent, application);
//	}


    /**
     * Retrieves the current execution environment
     *
     * @return The environment Griffon is executing under
     */
    public static String getEnvironment() {
        GriffonContext app = GriffonContextHolder.getGriffonContext();


        String envName = null;
        if(app!=null) {
            Map metadata = app.getMetadata();
            if(metadata!=null)
                envName = (String)metadata.get(BuildSettings.ENVIRONMENT);
        }
        if(isBlank(envName))
            envName = System.getProperty(BuildSettings.ENVIRONMENT);

        if(isBlank(envName)) {
            return BuildSettings.ENV_DEVELOPMENT;
        }
        else {
            if(envNameMappings.containsKey(envName)) {
                return (String)envNameMappings.get(envName);
            }
            else {
                return envName;
            }
        }
    }

    /**
     * Retrieves whether the current execution environment is the development one
     *
     * @return True if it is the development environment
     */
    public static boolean isDevelopmentEnv() {
        return BuildSettings.ENV_DEVELOPMENT.equals(GriffonUtil.getEnvironment());
    }


    public static String getGriffonVersion() {
        return GRIFFON_VERSION;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * Logs warning message about deprecation of specified property or method of some class.
     *
     * @param clazz A class
     * @param methodOrPropName Name of deprecated property or method
     */
    public static void deprecated(Class clazz, String methodOrPropName ) {
    	deprecated(clazz, methodOrPropName, getGriffonVersion());
    }

    /**
     * Logs warning message about deprecation of specified property or method of some class.
     *
     * @param clazz A class
     * @param methodOrPropName Name of deprecated property or method
     * @param version Version of Griffon release in which property or method were deprecated
     */
    public static void deprecated(Class clazz, String methodOrPropName, String version ) {
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
     * @param t
     * @return The exception passed in, after cleaning the stack trace
     */
    public static Throwable sanitize(Throwable t) {
        // Note that this getProperty access may well be synced...
        if (!Boolean.valueOf(System.getProperty("griffon.full.stacktrace"))) {
            StackTraceElement[] trace = t.getStackTrace();
            List<StackTraceElement> newTrace = new ArrayList<StackTraceElement>();
            for (int i = 0; i < trace.length; i++) {
                StackTraceElement stackTraceElement = trace[i];
                if (isApplicationClass(stackTraceElement.getClassName())) {
                    newTrace.add( stackTraceElement);
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
            p.println(  "at "+stackTraceElement.getClassName()
                        +"("+stackTraceElement.getMethodName()
                        +":"+stackTraceElement.getLineNumber()+")");
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
     * @param t the throwable to sanitize
     * @return The root cause exception instance, with its stace trace modified to filter out griffon runtime classes
     */
    public static Throwable sanitizeRootCause(Throwable t) {
        return sanitize(extractRootCause(t));
    }

    /**
     * <p>Sanitize the exception and ALL nested causes</p>
     * <p>This will MODIFY the stacktrace of the exception instance and all its causes irreversibly</p>
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
     * @param result The root node of the XML to write out.
     * @param output Where to write the XML to.
     * @throws java.io.IOException If the writing fails due to a closed stream
     * or unwritable file.
     */
    public static void writeSlurperResult(GPathResult result, Writer output) throws IOException {
        Binding b = new Binding();
        b.setVariable("node", result);
        // this code takes the XML parsed by XmlSlurper and writes it out using StreamingMarkupBuilder
        // don't ask me how it works, refer to John Wilson ;-)
        Writable w = (Writable)new GroovyShell(b).evaluate(
                "new groovy.xml.StreamingMarkupBuilder().bind {" +
                        " mkp.declareNamespace(\"\":  \"http://java.sun.com/xml/ns/j2ee\");" +
                        " mkp.yield node}");
        w.writeTo(output);
    }
}