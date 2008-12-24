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

package org.codehaus.griffon.cli;

import gant.Gant;
import org.codehaus.griffon.util.BuildSettings;
import org.codehaus.griffon.util.BuildSettingsHolder;
import org.codehaus.griffon.util.GriffonNameUtils;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import org.codehaus.gant.GantBinding;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import griffon.util.GriffonExceptionHandler;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that handles Griffon command line interface for running scripts
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

public class GriffonScriptRunner {
    private static Map ENV_ARGS = new HashMap();
    // this map contains default environments for several scripts in form 'script-name':'env-code'
    private static Map DEFAULT_ENVS = new HashMap();

    static {
        ENV_ARGS.put("dev", BuildSettings.ENV_DEVELOPMENT);
        ENV_ARGS.put("prod", BuildSettings.ENV_PRODUCTION);
        ENV_ARGS.put("test", BuildSettings.ENV_TEST);
        DEFAULT_ENVS.put("Console", BuildSettings.ENV_TEST);
        DEFAULT_ENVS.put("Shell", BuildSettings.ENV_TEST);
        DEFAULT_ENVS.put("TestApp", BuildSettings.ENV_TEST);
        DEFAULT_ENVS.put("RunWebtest", BuildSettings.ENV_TEST);
        ExpandoMetaClass.enableGlobally();
    }

    private static final Pattern scriptFilePattern = Pattern.compile("^[^_]\\w+\\.groovy$");
    private static final Pattern pluginDescriptorPattern = Pattern.compile("^(\\S+)GriffonPlugin.groovy$");

    public static void main(String[] args) throws MalformedURLException {
        GriffonExceptionHandler.registerExceptionHandler();

        // Evaluate the arguments to get the name of the script to
        // execute, which environment to run it in, and the arguments
        // to pass to the script. This also evaluates arguments of the
        // form "-Dprop=value" and creates system properties from each
        // one.
        String allArgs = args.length > 0 ? args[0].trim() : "";
        ScriptAndArgs script = processArgumentsAndReturnScriptName(allArgs);

        // Get hold of the Griffon_HOME environment variable if it is
        // available.
        String griffonHome = System.getProperty("griffon.home");

        // Now we can pick up the Griffon version from the Ant project
        // properties.
        BuildSettings build = new BuildSettings(griffonHome);

        // Check that Griffon' home actually exists.
        final File griffonHomeInSettings = build.getGriffonHome();
        if (griffonHomeInSettings == null || !griffonHomeInSettings.exists()) {
            System.out.println("Griffon' installation directory not found: " + build.getGriffonHome());
            System.exit(1);
        }

        // Show a nice header in the console when running commands.
        System.out.println(
"Welcome to Griffon " + build.getGriffonVersion() + " - http://griffon.codehaus.org/" + '\n' +
"Licensed under Apache Standard License 2.0" + '\n' +
"Griffon home is " + (griffonHome == null ? "not set" : "set to: " + griffonHome) + '\n');

        // If there aren't any arguments, then we don't have a command
        // to execute. So we have to exit.
        if (script.name == null) {
            System.out.println("No script name specified. Use 'griffon help' for more info or 'griffon interactive' to enter interactive mode");
            System.exit(0);
        }

        System.out.println("Base Directory: " + build.getBaseDir().getPath());

        try {
            GriffonScriptRunner runner = new GriffonScriptRunner(build);
            int exitCode = runner.executeCommand(script.name, script.args, script.env);
            System.exit(exitCode);
        }
        catch (Throwable t) {
            System.out.println("Error executing script " + script.name + ": " + t.getMessage());
            sanitizeStacktrace(t);
            t.printStackTrace(System.out);
            System.exit(1);
        }
    }

    private static ScriptAndArgs processArgumentsAndReturnScriptName(String allArgs) {
        ScriptAndArgs info = new ScriptAndArgs();

        // Check that we actually have some arguments to process.
        if (allArgs == null || allArgs.length() == 0) return info;

        String[] splitArgs = processSystemArguments(allArgs).trim().split(" ");
        int currentParamIndex = 0;
        if (isEnvironmentArgs(splitArgs[currentParamIndex])) {
            // use first argument as environment name and step further
            String env = splitArgs[currentParamIndex++];
            info.env = (String) ENV_ARGS.get(env);
        }

        if (currentParamIndex >= splitArgs.length) {
            System.out.println("You should specify a script to run. Run 'griffon help' for a complete list of available scripts.");
            System.exit(0);
        }

        // use current argument as script name and step further
        String paramName = splitArgs[currentParamIndex++];
        if (paramName.charAt(0) == '-') {
            paramName = paramName.substring(1);
        }
        info.name = GriffonNameUtils.getNameFromScript(paramName);

        if (currentParamIndex < splitArgs.length) {
            // if we have additional params provided - store it in system property
            StringBuilder b = new StringBuilder(splitArgs[currentParamIndex]);
            for (int i = currentParamIndex + 1; i < splitArgs.length; i++) {
                b.append(' ').append(splitArgs[i]);
            }
            info.args = b.toString();
        }
        return info;
    }

    private static String processSystemArguments(String allArgs) {
        String lastMatch = null;
        Pattern sysPropPattern = Pattern.compile("-D(.+?)=(.+?)\\s+?");
        Matcher m = sysPropPattern.matcher(allArgs);
        while (m.find()) {
            System.setProperty(m.group(1).trim(), m.group(2).trim());
            lastMatch = m.group();
        }

        if (lastMatch != null) {
            int i = allArgs.lastIndexOf(lastMatch) + lastMatch.length();
            allArgs = allArgs.substring(i);
        }
        return allArgs;
    }

    private static boolean isEnvironmentArgs(String env) {
        return ENV_ARGS.containsKey(env);
    }

    private BuildSettings settings;
    private PrintStream out = System.out;

    public GriffonScriptRunner() {
        this(new BuildSettings());
    }

    public GriffonScriptRunner(String griffonHome) {
        this(new BuildSettings(griffonHome));
    }

    public GriffonScriptRunner(BuildSettings settings) {
        this.settings = settings;
    }

    public PrintStream getOut() {
        return this.out;
    }

    public void setOut(PrintStream outputStream) {
        this.out = outputStream;
    }

    public int executeCommand(String scriptName, String args) {
        return executeCommand(scriptName, args, null);
    }

    public int executeCommand(String scriptName, String args, String env) {
        // Populate the root loader with all libraries that this app
        // depends on. If a root loader doesn't exist yet, create it
        // now.
        ClassLoader classLoader = GriffonScriptRunner.class.getClassLoader();
        settings.setRootLoader((URLClassLoader) classLoader);

        // Get the default environment if one hasn't been set.
        boolean useDefaultEnv = env == null;
        if (useDefaultEnv) {
            env = (String) DEFAULT_ENVS.get(scriptName);
            env = env != null ? env : BuildSettings.ENV_DEVELOPMENT;
        }

        System.setProperty("base.dir", settings.getBaseDir().getPath());
        System.setProperty(BuildSettings.ENVIRONMENT, env);
        System.setProperty(BuildSettings.ENVIRONMENT_DEFAULT, "true");

        if (args != null) {
            System.setProperty("griffon.cli.args", args.replace(' ', '\n'));
        }
        else {
            // If GriffonScriptRunner is executed more than once in a
            // single JVM, we have to make sure that the CLI args are
            // reset.
            System.setProperty("griffon.cli.args", "");
        }

        // Load the BuildSettings file for this project if it exists. Note
        // that this does not load any environment-specific settings.
        settings.loadConfig();

        // Add some extra binding variables that are now available.
        settings.setGriffonEnv(env);
        settings.setDefaultEnv(useDefaultEnv);

        BuildSettingsHolder.setSettings(settings);


        // Either run the script or enter interactive mode.
        if (scriptName.equalsIgnoreCase("interactive")) {
            // This never exits unless an exception is thrown or
            // the process is interrupted via a signal.
            runInteractive();
            return 0;
        }
        else {
            return callPluginOrGriffonScript(scriptName);
        }
    }

    /**
     * Runs Griffon in interactive mode.
     */
    private void runInteractive() {
        String message = "Interactive mode ready, type your command name in to continue (hit ENTER to run the last command):\n";
        //disable exiting
//        System.metaClass.static.exit = {int code ->}
        System.setProperty("griffon.interactive.mode", "true");
        int messageNumber = 0;
        ScriptAndArgs script = new ScriptAndArgs();
        while (true) {
            out.println("--------------------------------------------------------");
            String enteredName = userInput(message);

            if (enteredName != null && enteredName.trim().length() > 0) {
                script = processArgumentsAndReturnScriptName(enteredName);
            }

            if (script.name == null) {
                out.println("You must enter a command.\n");
                continue;
            }

            long now = System.currentTimeMillis();
            callPluginOrGriffonScript(script.name);
            long end = System.currentTimeMillis();
            out.println("--------------------------------------------------------");
            out.println("Command [" + script.name + " completed in " + (end - now) + "ms");
        }
    }

    private final Map scriptCache = new HashMap();
    private final List scriptsAllowedOutsideOfProject = new ArrayList();

    private int callPluginOrGriffonScript(String scriptName) {
        // The class loader we will use to run Gant. It's the root
        // loader plus all the application's compiled classes.
        URLClassLoader classLoader;
        try {
            // JARs already on the classpath should be excluded.
            Set existingJars = new HashSet();
            for (URL url : settings.getRootLoader().getURLs()) {
                existingJars.add(url.getFile());
            }

            // Add the remaining JARs (from 'griffonHome', the app, and
            // the plugins) to the root loader.
            URL[] urls = getClassLoaderUrls(settings, existingJars);
            addUrlsToRootLoader(settings.getRootLoader(), urls);

            // The compiled classes of the application!
            urls = new URL[] { settings.getClassesDir().toURI().toURL() };
            classLoader = new URLClassLoader(urls, settings.getRootLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid classpath URL", ex);
        }

        List potentialScripts;
        GantBinding binding;
        if (scriptCache.get(scriptName) != null) {
            CachedScript cachedScript = (CachedScript) scriptCache.get(scriptName);
            potentialScripts = cachedScript.potentialScripts;
            binding = cachedScript.binding;
        }
        else {
            binding = new GantBinding();
            List list = getAvailableScripts(settings);

            potentialScripts = new ArrayList();
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                File scriptPath = (File) iter.next();
                String scriptFileName = scriptPath.getName().substring(0,scriptPath.getName().length()-7); // trim .groovy extension
                if(scriptFileName.endsWith("_")) {
                    scriptsAllowedOutsideOfProject.add(scriptPath);
                    scriptFileName = scriptFileName.substring(0, scriptFileName.length()-1);
                }

                if (scriptFileName.equals(scriptName)) potentialScripts.add(scriptPath);
            }

            if (!potentialScripts.isEmpty()) {
                CachedScript cachedScript = new CachedScript();
                cachedScript.binding = binding;
                cachedScript.potentialScripts = potentialScripts;
                scriptCache.put("scriptName", cachedScript);
            }
        }

        // Prep the binding with important variables.
        initBinding(binding);

        // First try to load the script from its file. If there is no
        // file, then attempt to load it as a pre-compiled script. If
        // that fails, then let the user know and then exit.
        if (potentialScripts.size() > 0) {
            potentialScripts = (List) DefaultGroovyMethods.unique(potentialScripts);
            if (potentialScripts.size() == 1) {
                final File scriptFile = (File) potentialScripts.get(0);
                if(!isGriffonProject() && !isExternalScript(scriptFile)) {
                    out.println(settings.getBaseDir().getPath() + " does not appear to be part of a Griffon application.");
                    out.println("The following commands are supported outside of a project:");
                    Collections.sort(scriptsAllowedOutsideOfProject);
                    for (Iterator iter = scriptsAllowedOutsideOfProject.iterator(); iter.hasNext();) {
                        File file = (File) iter.next();
                        out.println("\t" + GriffonNameUtils.getScriptName(file.getName()));
                    }
                    out.println("Run 'griffon help' for a complete list of available scripts.");
                    return -1;
                }
                else {
                    out.println("Running script " + scriptFile.getAbsolutePath());

                    // Setup the script to call.
                    Gant gant = new Gant(binding, classLoader);
                    gant.setUseCache(true);
                    gant.setCacheDirectory(new File(settings.getGriffonWorkDir(), "scriptCache"));
                    gant.loadScript(scriptFile);

                    // Invoke the default target.
                    return gant.processTargets().intValue();
                }
            }
            else {
                out.println("Multiple options please select:");
                String[] validArgs = new String[potentialScripts.size()];
                for (int i = 0; i < validArgs.length; i++) {
                    out.println("[" + (i + 1) + "] " + potentialScripts.get(i));
                    validArgs[i] = String.valueOf(i + 1);
                }

                String enteredValue = userInput("Enter #", validArgs);
                if (enteredValue == null) return 1;

                int number = Integer.valueOf(enteredValue);

                out.println("Running script "+ ((File) potentialScripts.get(number - 1)).getAbsolutePath());

                // Set up the script to call.
                Gant gant = new Gant(binding, classLoader);
                gant.loadScript((File) potentialScripts.get(number - 1));

                // Invoke the default target.
                return gant.processTargets().intValue();
            }
        }
        else {
            try {
                out.println("Running pre-compiled script");

                // Get Gant to load the class by name using our class loader.
                Gant gant = new Gant(binding, classLoader);
                try {
                    gant.loadScriptClass(scriptName+"_");
                    // try externalized script first
                }
                catch (Exception e) {
                    gant.loadScriptClass(scriptName);
                }

                return gant.processTargets().intValue();
            }
            catch (Exception ex) {
                out.println("Unable to load script '" + scriptName + "' (error: " + ex.getMessage() + ")");
                out.println("Run 'griffon help' for a complete list of available scripts.");
                return 1;
            }
        }
    }

    private boolean isGriffonProject() {
        return new File(settings.getBaseDir(), "griffon-app").exists();
    }

    private boolean isExternalScript(File scriptFile) {
        return scriptsAllowedOutsideOfProject.contains(scriptFile);
    }

    /**
     * Prep the binding. We add the location of Griffon_HOME under
     * the variable name "griffonHome". We also add a closure that
     * should be used with "includeTargets <<" - it takes a string
     * and returns either a file containing the named Griffon script
     * or the script class.
     *
     * So, this:
     *
     *   includeTargets << griffonScript("Init")
     *
     * will load the "Init" script from $Griffon_HOME/scripts if it
     * exists there; otherwise it will load the Init class.
     */
    private void initBinding(Binding binding) {
        Closure c = settings.getGriffonScriptClosure();
        c.setDelegate(binding);
        binding.setVariable("griffonScript", c);
        binding.setVariable("griffonSettings", settings);

        // Add other binding variables, such as Griffon version and
        // environment.
        binding.setVariable("basedir", settings.getBaseDir().getPath());
        binding.setVariable("baseFile", settings.getBaseDir());
        binding.setVariable("baseName", settings.getBaseDir().getName());
        binding.setVariable("griffonHome", (settings.getGriffonHome() != null ? settings.getGriffonHome().getPath() : null));
        binding.setVariable("griffonVersion", settings.getGriffonVersion());
        binding.setVariable("userHome", settings.getUserHome());
        binding.setVariable("griffonEnv", settings.getGriffonEnv());
        binding.setVariable("defaultEnv", Boolean.valueOf(settings.getDefaultEnv()));
        binding.setVariable("buildConfig", settings.getConfig());
        binding.setVariable("rootLoader", settings.getRootLoader());

        // Add the project paths too!
        binding.setVariable("griffonWorkDir", settings.getGriffonWorkDir().getPath());
        binding.setVariable("projectWorkDir", settings.getProjectWorkDir().getPath());
        binding.setVariable("classesDirPath", settings.getClassesDir().getPath());
        binding.setVariable("testDirPath", settings.getTestClassesDir().getPath());
        binding.setVariable("resourcesDirPath", settings.getResourcesDir().getPath());
        binding.setVariable("pluginsDirPath", settings.getProjectPluginsDir().getPath());
        binding.setVariable("globalPluginsDirPath", settings.getGlobalPluginsDir().getPath());

        // Create binding variables that contain the locations of each of the
        // plugins loaded by the application. The name of each variable is of
        // the form <pluginName>PluginDir.
        try {
            List descriptors = new ArrayList();
            File desc = getPluginDescriptor(settings.getBaseDir());
            if (desc != null) descriptors.add(desc);
            descriptors.addAll(Arrays.asList(getPluginDescriptors(settings.getProjectPluginsDir())));
            descriptors.addAll(Arrays.asList(getPluginDescriptors(settings.getGlobalPluginsDir())));

            for (int i = 0, n = descriptors.size(); i < n; i++) {
                desc = (File) descriptors.get(i);
                Matcher matcher = pluginDescriptorPattern.matcher(desc.getName());
                matcher.find();
                String pluginName = GriffonNameUtils.getPropertyName(matcher.group(1));

                // Add the plugin path to the binding.
                binding.setVariable(pluginName + "PluginDir", desc.getParentFile());
            }
        }
        catch (Exception e) {
            // No plugins found.
        }
    }

    /**
     * Returns a list of all the executable Gant scripts available to
     * this application.
     */
    private static List getAvailableScripts(BuildSettings settings) {
        List scripts = new ArrayList();
        if (settings.getGriffonHome() != null) {
            addCommandScripts(new File(settings.getGriffonHome(), "scripts"), scripts);
        }
        addCommandScripts(new File(settings.getBaseDir(), "scripts"), scripts);
        addCommandScripts(new File(settings.getUserHome(), ".griffon/scripts"), scripts);

        addPluginScripts(settings.getProjectPluginsDir(), scripts);
        addPluginScripts(settings.getGlobalPluginsDir(), scripts);
        return scripts;
    }

    /**
     * Collects all the command scripts provided by the plugin contained
     * in the given directory and adds them to the given list.
     */
    private static void addPluginScripts(File pluginDir, List scripts) {
        if (!pluginDir.exists()) return;

        File[] dirs = listDirs(pluginDir);
        for (int i = 0; i < dirs.length; i++) {
            File scriptDir = new File(dirs[i], "scripts");
            if (scriptDir.exists()) addCommandScripts(scriptDir, scripts);
        }
    }

    /**
     * Adds all the command scripts (i.e. those whose name does *not*
     * start with an underscore, '_') found in the given directory to
     * the given list.
     */
    private static void addCommandScripts(File dir, List scripts) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (scriptFilePattern.matcher(files[i].getName()).matches()) {
                    scripts.add(files[i]);
                }
            }
        }
    }

    /**
     * Creates a new root loader with the Griffon libraries and the
     * application's plugin libraries on the classpath.
     */
    private static URL[] getClassLoaderUrls(BuildSettings settings, Set excludes) throws MalformedURLException {
        List urls = new ArrayList();

        // First add the libraries from the Griffon installation directory,
        // if there is one.
        if (settings.getGriffonHome() != null) {
            addLibs(new File(settings.getGriffonHome(), "dist"), urls, excludes);
            addLibs(new File(settings.getGriffonHome(), "lib"), urls, excludes);
        }

        // Next the application's libraries.
        File appLibDir = new File(settings.getBaseDir(), "lib");
        if (appLibDir.exists()) addLibs(appLibDir, urls, excludes);

        // Add the libraries of both project and global plugins.
        addPluginLibs(settings.getProjectPluginsDir(), urls);
        addPluginLibs(settings.getGlobalPluginsDir(), urls);
        return (URL[]) urls.toArray(new URL[0]);
    }

    /**
     * Adds the libraries for all plugins in the given directory to the
     * give list of URLs.
     */
    private static void addPluginLibs(File pluginDir, List urls) throws MalformedURLException {
        if (!pluginDir.exists()) return;

        File[] dirs = listDirs(pluginDir);
        for (int i = 0; i < dirs.length; i++) {
            File libDir = new File(dirs[i], "lib");
            if (libDir.exists()) addLibs(libDir, urls, Collections.EMPTY_SET);
        }
    }

    /**
     * Adds all the JAR files in the given directory to the list of
     * URLs.
     */
    private static void addLibs(File dir, List urls, Set excludes) throws MalformedURLException {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.getName().matches("^.*\\.jar$") && !excludes.contains(file.getName())) {
                    urls.add(file.toURI().toURL());
                }
            }
        }
    }

    /**
     * Lists all the sub-directories (non-recursively) of the given
     * directory. If there are no directories, an empty array is
     * returned.
     */
    private static File[] listDirs(File dir) {
        File[] dirs = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        return dirs == null ? new File[0] : dirs;
    }

    /**
     * Retrieves the locations of all plugin descriptors in the given
     * directory. The method assumes that the directory contains plugins
     * and so it will only look for descriptors in the immediate children
     * of the given directory - it will not search recursively.
     * @param dir The directory containing the plugins.
     * @return An array of plugin descriptor locations.
     */
    private static File[] getPluginDescriptors(File dir) {
        File[] pluginDirs = listDirs(dir);
        List descriptors = new ArrayList(pluginDirs.length);
        for (int i = 0; i < pluginDirs.length; i++) {
            File desc = getPluginDescriptor(pluginDirs[i]);
            if (desc != null) descriptors.add(desc);
        }

        return (File[]) descriptors.toArray(new File[0]);
    }

    /**
     * Retrieves the first plugin descriptor it finds in the given
     * directory. The search is not recursive.
     * @param dir The directory to search in.
     * @return The location of the plugin descriptor, or <code>null</code>
     * if none can be found.
     */
    private static File getPluginDescriptor(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                return s.endsWith("GriffonPlugin.groovy");
            }
        });

        if (files.length > 0) return files[0];
        else return null;
    }

    /**
     * Sanitizes a stack trace using GriffonUtil.deepSanitize(). We use
     * this method so that the GriffonUtil class is loaded from the
     * context class loader. Basically, we don't want this class to
     * have a direct dependency on GriffonUtil otherwise the class loader
     * used to load this class (GriffonScriptRunner) would have to have
     * far more libraries on its classpath than we want.
     */
    private static void sanitizeStacktrace(Throwable t) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class clazz = loader.loadClass("org.codehaus.griffon.util.GriffonUtil");
            Method method = clazz.getMethod("deepSanitize", new Class[] {Throwable.class});
            method.invoke(null, new Object[] {t});
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>A Groovy RootLoader should be used to load GriffonScriptRunner,
     * but this leaves us with a problem. If we want to extend its
     * classpath by adding extra URLs, we have to use the addURL()
     * method that is only public on RootLoader (it's protected on
     * URLClassLoader). Unfortunately, due to the nature of Groovy's
     * RootLoader a declared type of RootLoader in this class is not
     * the same type as GriffonScriptRunner's class loader <i>because
     * the two are loaded by different class loaders</i>.</p>
     * <p>In other words, we can't add URLs via the addURL() method
     * because we can't "see" it from Java. Instead, we use reflection
     * to invoke it.</p>
     * @param loader The root loader whose classpath we want to extend.
     * @param urls The URLs to add to the root loader's classpath.
     */
    private static void addUrlsToRootLoader(URLClassLoader loader, URL[] urls) {
        try {
            Class loaderClass = loader.getClass();
            Method method = loaderClass.getMethod("addURL", URL.class);
            for (URL url : urls) {
                method.invoke(loader, url);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("GriffonScriptRunner loader is not a Groovy RootLoader instance!");
        }
    }

    /**
     * Replacement for AntBuilder.input() to eliminate dependency of
     * GriffonScriptRunner on the Ant libraries. Prints a message and
     * returns whatever the user enters (once they press &lt;return&gt;).
     * @param message The message/question to display.
     * @return The line of text entered by the user. May be a blank
     * string.
     */
    private String userInput(String message) {
        return userInput(message, null);
    }

    /**
     * Replacement for AntBuilder.input() to eliminate dependency of
     * GriffonScriptRunner on the Ant libraries. Prints a message and
     * list of valid responses, then returns whatever the user enters
     * (once they press &lt;return&gt;). If the user enters something
     * that is not in the array of valid responses, the message is
     * displayed again and the method waits for more input. It will
     * display the message a maximum of three times before it gives up
     * and returns <code>null</code>.
     * @param message The message/question to display.
     * @param validResponses An array of responses that the user is
     * allowed to enter. Displayed after the message.
     * @return The line of text entered by the user, or <code>null</code>
     * if the user never entered a valid string.
     */
    private String userInput(String message, String[] validResponses) {
        String responsesString = null;
        if (validResponses != null) {
            responsesString = DefaultGroovyMethods.join(validResponses, ",");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        for (int it = 0; it < 3; it++) {
            out.print(message);
            if (responsesString != null) {
                out.print(" [");
                out.print(responsesString);
                out.print("] ");
            }

            try {
                String line = reader.readLine();

                if (validResponses == null) return line;

                for (String validResponse : validResponses) {
                    if (line != null && line.equals(validResponse)) {
                        return line;
                    }
                }

                out.println();
                out.println("Invalid option '" + line + "' - must be one of: [" + responsesString + "]");
                out.println();
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        // No valid response given.
        out.println("No valid response entered - giving up asking.");
        return null;
    }

    /**
     * Contains details about a Griffon command invocation such as the
     * name of the corresponding script, the environment (if specified),
     * and the arguments to the command.
     */
    private static class ScriptAndArgs {
        public String name;
        public String env;
        public String args;
    }
}
