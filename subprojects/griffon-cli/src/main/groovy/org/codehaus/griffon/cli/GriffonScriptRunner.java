/*
 * Copyright 2004-2011 the original author or authors.
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
import griffon.util.*;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import groovy.util.AntBuilder;
import org.apache.log4j.LogManager;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.artifacts.ArtifactRepositoryRegistry;
import org.codehaus.griffon.artifacts.ArtifactUtils;
import org.codehaus.griffon.artifacts.model.Plugin;
import org.codehaus.griffon.cli.support.BuildListenerAdapter;
import org.codehaus.griffon.runtime.logging.Log4jConfig;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.core.io.Resource;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.binarySearch;
import static org.codehaus.griffon.artifacts.ArtifactUtils.getInstalledArtifacts;

/**
 * Class that handles Griffon command line interface for running scripts
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class GriffonScriptRunner {
    private static Map<String, String> ENV_ARGS = new HashMap<String, String>();
    // this map contains default environments for several scripts in form 'script-name':'env-code'
    private static Map<String, String> DEFAULT_ENVS = new HashMap<String, String>();

    static {
        ENV_ARGS.put("dev", Environment.DEVELOPMENT.getName());
        ENV_ARGS.put("prod", Environment.PRODUCTION.getName());
        ENV_ARGS.put("test", Environment.TEST.getName());
        DEFAULT_ENVS.put("Console", Environment.TEST.getName());
        DEFAULT_ENVS.put("Shell", Environment.TEST.getName());
        DEFAULT_ENVS.put("Package", Environment.PRODUCTION.getName());
        DEFAULT_ENVS.put("TestApp", Environment.TEST.getName());
        ExpandoMetaClass.enableGlobally();
    }

    private static final Pattern scriptFilePattern = Pattern.compile("^[^_]\\w+\\.groovy$");
    private static final String VAR_SCRIPT_NAME = "scriptName";
    private static final String VAR_SCRIPT_FILE = "scriptFile";
    private static final String VAR_SCRIPT_ENV = "scriptEnv";

    /**
     * Evaluate the arguments to get the name of the script to execute, which environment
     * to run it in, and the arguments to pass to the script. This also evaluates arguments
     * of the form "-Dprop=value" and creates system properties from each one.
     *
     * @param args
     */
    public static void main(String[] args) {
        GriffonExceptionHandler.registerExceptionHandler();
        StringBuilder allArgs = new StringBuilder("");
        for (String arg : args) {
            allArgs.append(" ").append(arg);
        }

        ScriptAndArgs script = processArgumentsAndReturnScriptName(allArgs.toString().trim());

        // Get hold of the Griffon_HOME environment variable if it is
        // available.
        String griffonHome = System.getProperty("griffon.home");

        // Now we can pick up the Griffon version from the Ant project properties.
        BuildSettings build = null;
        try {
            build = new BuildSettings(new File(griffonHome));
        } catch (Exception e) {
            System.err.println("An error occurred loading the griffon-app/conf/BuildConfig.groovy file: " + e.getMessage());
            System.exit(1);
        }

        // Check that Griffon' home actually exists.
        final File griffonHomeInSettings = build.getGriffonHome();
        if (griffonHomeInSettings == null || !griffonHomeInSettings.exists()) {
            exitWithError("Griffon' installation directory not found: " + build.getGriffonHome());
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
            int exitCode = new GriffonScriptRunner(build).executeCommand(script);
            System.exit(exitCode);
        } catch (ScriptNotFoundException ex) {
            System.out.println("Script not found: " + ex.getScriptName());
        } catch (Throwable t) {
            String msg = "Error executing script " + script.name + ": " + t.getMessage();
            System.out.println(msg);
            GriffonExceptionHandler.sanitize(t);
            t.printStackTrace(System.out);
            exitWithError(msg);
        }
    }

    private static void exitWithError(String error) {
        System.out.println(error);
        System.exit(1);
    }

    private static ScriptAndArgs processArgumentsAndReturnScriptName(String allArgs) {
        ScriptAndArgs info = new ScriptAndArgs();

        // Check that we actually have some arguments to process.
        if (allArgs == null || allArgs.length() == 0) return info;

        String[] splitArgs = processSystemArguments(allArgs).trim().split(" ");
        int currentParamIndex = 0;
        if (Environment.isSystemSet()) {
            info.env = Environment.getCurrent().getName();
        } else if (isEnvironmentArgs(splitArgs[currentParamIndex])) {
            // use first argument as environment name and step further
            String env = splitArgs[currentParamIndex++];
            info.env = ENV_ARGS.get(env);
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
        info.name = GriffonUtil.getNameFromScript(paramName);

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

    private static final Map<String, String> SYSTEM_PROPERTIES = new LinkedHashMap<String, String>();

    private static String processSystemArguments(String allArgs) {
        String lastMatch = null;
        Pattern sysPropPattern = Pattern.compile("-D(.+?)=(['\"].+?['\"]|.+?)\\s+?");
        Matcher m = sysPropPattern.matcher(allArgs);
        while (m.find()) {
            String key = m.group(1).trim();
            String value = unquote(m.group(2).trim());
            SYSTEM_PROPERTIES.put(key, value);
            System.setProperty(key, value);
            lastMatch = m.group();
        }

        if (lastMatch != null) {
            int i = allArgs.lastIndexOf(lastMatch) + lastMatch.length();
            allArgs = allArgs.substring(i);
        }
        return allArgs;
    }

    public static String unquote(String s) {
        if ((s.startsWith("'") && s.endsWith("'")) ||
                (s.startsWith("\"") && s.endsWith("\""))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static boolean isEnvironmentArgs(String env) {
        return ENV_ARGS.containsKey(env);
    }

    private BuildSettings settings;
    private PrintStream out = System.out;
    private CommandLineHelper helper = new CommandLineHelper(out);
    private boolean isInteractive = true;

    public GriffonScriptRunner() {
        this(new BuildSettings());
    }

    public GriffonScriptRunner(String griffonHome) {
        this(new BuildSettings(new File(griffonHome)));
    }

    public GriffonScriptRunner(BuildSettings settings) {
        this.settings = settings;
    }

    public PrintStream getOut() {
        return this.out;
    }

    public void setOut(PrintStream outputStream) {
        this.out = outputStream;
        this.helper = new CommandLineHelper(out);
    }

    public int executeCommand(String name, String args) {
        ScriptAndArgs script = new ScriptAndArgs();
        script.name = name;
        script.args = args;
        return executeCommand(script);
    }

    public int executeCommand(String name, String args, String env) {
        ScriptAndArgs script = new ScriptAndArgs();
        script.name = name;
        script.args = args;
        script.env = env;
        return executeCommand(script);
    }

    private int executeCommand(ScriptAndArgs script) {
        settings.getSystemProperties().putAll(SYSTEM_PROPERTIES);

        // Populate the root loader with all libraries that this app
        // depends on. If a root loader doesn't exist yet, create it now.
        if (settings.getRootLoader() == null) {
            settings.setRootLoader((URLClassLoader) GriffonScriptRunner.class.getClassLoader());
        }

        if (script.args != null) {
            // Check whether we are running in non-interactive mode
            // by looking for a "non-interactive" argument.
            String[] argArray = script.args.split("\\s+");
            Pattern pattern = Pattern.compile("^(?:-)?-non-interactive$");
            for (String arg : argArray) {
                if (pattern.matcher(arg).matches()) {
                    isInteractive = false;
                    break;
                }
            }

            System.setProperty("griffon.cli.args", script.args.replace(' ', '\n'));
        } else {
            // If GriffonScriptRunner is executed more than once in a
            // single JVM, we have to make sure that the CLI args are
            // reset.
            System.setProperty("griffon.cli.args", "");
        }

        // Load the BuildSettings file for this project if it exists. Note
        // that this does not load any environment-specific settings.
        BuildSettingsHolder.setSettings(settings);
        settings.loadConfig();
        BuildSettingsHolder.setSettings(settings);
        setLoggingOptions();
        ArtifactRepositoryRegistry.getInstance().configureRepositories();

        /*
        // Either run the script or enter interactive mode.
        if (script.name.equalsIgnoreCase("interactive")) {
            // Can't operate interactively in non-interactive mode!
            if (!isInteractive) {
                out.println("You cannot use '--non-interactive' with interactive mode.");
                return 1;
            }

            setRunningEnvironment(script.name, script.env);
            // This never exits unless an exception is thrown or
            // the process is interrupted via a signal.
            runInteractive();
            return 0;
        }
        */
        return callPluginOrGriffonScript(script);
    }

    private void setLoggingOptions() {
        Object log4jConfig = settings.getConfig().get("log4j");
        if (log4jConfig instanceof Closure) {
            LogManager.resetConfiguration();
            new Log4jConfig().configure((Closure) log4jConfig);
        }
    }

    public void setRunningEnvironment(String scriptName, String env) {
        // Get the default environment if one hasn't been set.
        boolean useDefaultEnv = env == null;
        if (useDefaultEnv) {
            env = DEFAULT_ENVS.get(scriptName);
            env = !isBlank(env) ? env : Environment.DEVELOPMENT.getName();
        }

        System.setProperty("base.dir", settings.getBaseDir().getPath());
        System.setProperty(Environment.KEY, env);
        System.setProperty(Environment.DEFAULT, "true");

        // Add some extra binding variables that are now available.
        settings.setGriffonEnv(env);
        settings.setDefaultEnv(useDefaultEnv);
        settings.loadConfig();
    }

    /**
     * Runs Griffon in interactive mode.
     */
    /*
    private void runInteractive() {
        String message = "Interactive mode ready. Enter a Griffon command or type \"exit\" to quit interactive mode (hit ENTER to run the last command):\n";

        // Disable exiting
        System.setProperty("griffon.disable.exit", "true");
        System.setProperty(KEY_INTERACTIVE_MODE, "true");

        ScriptAndArgs script = new ScriptAndArgs();
        String env = null;
        while (true) {
            // Clear unhelpful system properties.
            System.clearProperty("griffon.env.set");
            System.clearProperty(Environment.KEY);
            env = null;

            out.println("--------------------------------------------------------");
            String enteredName = helper.userInput(message);

            if (enteredName != null && enteredName.trim().length() > 0) {
                script = processArgumentsAndReturnScriptName(enteredName);

                // Update the relevant system property, otherwise the
                // arguments will be "remembered" from the previous run.
                if (script.args != null) {
                    System.setProperty("griffon.cli.args", script.args);
                } else {
                    System.setProperty("griffon.cli.args", "");
                }

                env = script.env != null ? script.env : Environment.DEVELOPMENT.getName();
            }

            if (script.name == null) {
                out.println("You must enter a command.\n");
                continue;
            } else if (script.name.equalsIgnoreCase("exit") || script.name.equalsIgnoreCase("quit")) {
                return;
            }

            long now = System.currentTimeMillis();
            try {
                script.env = env;
                callPluginOrGriffonScript(script);
            } catch (ScriptNotFoundException ex) {
                out.println("No script found for " + script.name);
            } catch (Throwable ex) {
                if (ex.getCause() instanceof ScriptExitException) {
                    out.println("Script exited with code " + ((ScriptExitException) ex.getCause()).getExitCode());
                } else {
                    out.println("Script threw exception");
                    ex.printStackTrace(out);
                }
            }
            long end = System.currentTimeMillis();
            out.println("--------------------------------------------------------");
            out.println("Command " + script.name + " completed in " + (end - now) + "ms");
        }
    }
    */

    private final Map<String, CachedScript> scriptCache = new HashMap<String, CachedScript>();
    private final List<File> scriptsAllowedOutsideOfProject = new ArrayList<File>();
    private static final Closure DO_NOTHING_CLOSURE = new Closure(new Object()) {
        private static final long serialVersionUID = 1L;

        @Override
        public Object call(Object arguments) {
            return null;
        }

        @Override
        public Object call() {
            return null;
        }

        @Override
        public Object call(Object[] args) {
            return null;
        }
    };

    private int callPluginOrGriffonScript(ScriptAndArgs script) {
        // The directory where scripts are cached.
        File scriptCacheDir = new File(settings.getProjectWorkDir(), "scriptCache");
        URLClassLoader classLoader = createClassLoader(script, scriptCacheDir);

        List<File> potentialScripts;
        Resource[] allScripts = settings.pluginSettings.getAvailableScripts(); //getAvailableScripts(settings);
        GantBinding binding;
        if (scriptCache.get(script.name) != null) {
            CachedScript cachedScript = scriptCache.get(script.name);
            potentialScripts = cachedScript.potentialScripts;
            binding = cachedScript.binding;
        } else {
            binding = new GantBinding();
            initializeProjectInputStream(binding);
            potentialScripts = findPotentialScripts(script, allScripts, binding);
        }

        // First try to load the script from its file. If there is no
        // file, then attempt to load it as a pre-compiled script. If
        // that fails, then let the user know and then exit.
        if (potentialScripts.size() > 0) {
            potentialScripts = (List) DefaultGroovyMethods.unique(potentialScripts);
            if (potentialScripts.size() == 1) {
                final File scriptFile = potentialScripts.get(0);
                if (!isGriffonProject() && !isExternalScript(scriptFile)) {
                    out.println(settings.getBaseDir().getPath() + " does not appear to be part of a Griffon application.");
                    out.println("The following commands are supported outside of a project:");
                    Collections.sort(scriptsAllowedOutsideOfProject);
                    for (File file : scriptsAllowedOutsideOfProject) {
                        out.println("\t" + GriffonUtil.getScriptName(file.getName()));
                    }
                    out.println("Run 'griffon help' for a complete list of available scripts.");
                    return -1;
                }
                out.println("Running script " + scriptFile.getAbsolutePath());
                // We can now safely set the default environment
                String scriptFileName = getScriptNameFromFile(scriptFile);
                setRunningEnvironment(scriptFileName, script.env);
                binding.setVariable(VAR_SCRIPT_NAME, scriptFileName);
                binding.setVariable(VAR_SCRIPT_FILE, scriptFile);
                script.name = scriptFileName;

                // Setup the script to call.
                Gant gant = createGantInstance(classLoader, binding, script);
                gant.setUseCache(true);
                gant.setCacheDirectory(scriptCacheDir);
                gant.loadScript(scriptFile);
                return executeWithGantInstance(gant, binding);
            }

            // If there are multiple scripts to choose from and we
            // are in non-interactive mode, then exit with an error
            // code. Otherwise the code will enter an infinite loop.
            if (!isInteractive) {
                out.println("More than one script with the given name is available - " +
                        "cannot continue in non-interactive mode.");
                return 1;
            }

            out.println("Multiple options please select:");
            String[] validArgs = new String[potentialScripts.size()];
            for (int i = 0; i < validArgs.length; i++) {
                out.println("[" + (i + 1) + "] " + potentialScripts.get(i));
                validArgs[i] = String.valueOf(i + 1);
            }

            String enteredValue = helper.forceUserInput("Enter #", validArgs);
            if (enteredValue == null) return 1;

            int number = Integer.parseInt(enteredValue);
            File scriptFile = potentialScripts.get(number - 1);
            out.println("Running script " + scriptFile.getAbsolutePath());
            // We can now safely set the default environment
            String scriptFileName = getScriptNameFromFile(scriptFile);
            setRunningEnvironment(scriptFileName, script.env);
            binding.setVariable(VAR_SCRIPT_NAME, scriptFileName);
            binding.setVariable(VAR_SCRIPT_FILE, scriptFile);
            script.name = scriptFileName;

            // Set up the script to call.
            Gant gant = createGantInstance(classLoader, binding, script);
            gant.loadScript(scriptFile);

            // Invoke the default target.
            return executeWithGantInstance(gant, binding);
        }

        out.println("Running pre-compiled script");

        // Get Gant to load the class by name using our class loader.
        Gant gant = createGantInstance(classLoader, binding, script);

        try {
            loadScriptClass(gant, script.name);
        } catch (ScriptNotFoundException e) {
            if (isInteractive) {
                script.name = fixScriptName(script.name, allScripts);
                if (script.name == null) {
                    throw e;
                }

                loadScriptClass(gant, script.name);
            } else {
                throw e;
            }
        }

        setRunningEnvironment(script.name, script.env);
        binding.setVariable(VAR_SCRIPT_ENV, System.getProperty(Environment.KEY));
        return executeWithGantInstance(gant, binding);
    }

    private Gant createGantInstance(URLClassLoader classLoader, GantBinding binding, ScriptAndArgs script) {
        Gant gant = new Gant(initBinding(binding), classLoader);
        if (griffonInitBuildListener != null && binding.getBuildListeners().contains(griffonInitBuildListener)) {
            binding.removeBuildListener(griffonInitBuildListener);
        }
        griffonInitBuildListener = new GriffonInitBuildListener(settings, binding, gant);
        binding.addBuildListener(griffonInitBuildListener);
        return gant;
    }

    private List<File> findPotentialScripts(ScriptAndArgs script, Resource[] allScripts, GantBinding binding) {
        List<File> potentialScripts;
        // Now find what scripts match the one requested by the user.
        boolean exactMatchFound = false;
        potentialScripts = new ArrayList<File>();
        for (Resource resource : allScripts) {
            File scriptPath = null;
            try {
                scriptPath = resource.getFile();
            } catch (IOException e) {
                GriffonExceptionHandler.sanitize(e);
                settings.debug("Script location " + resource + " has been blacklisted => " + e);
                continue;
            }
            String scriptFileName = scriptPath.getName().substring(0, scriptPath.getName().length() - 7); // trim .groovy extension
            if (scriptFileName.endsWith("_")) {
                scriptsAllowedOutsideOfProject.add(scriptPath);
                scriptFileName = scriptFileName.substring(0, scriptFileName.length() - 1);
            }

            if (scriptFileName.equals(script.name)) {
                potentialScripts.add(scriptPath);
                exactMatchFound = true;
                continue;
            }

            if (!exactMatchFound && ScriptNameResolver.resolvesTo(script.name, scriptFileName))
                potentialScripts.add(scriptPath);
        }

        if (!potentialScripts.isEmpty() && !exactMatchFound) {
            CachedScript cachedScript = new CachedScript();
            cachedScript.binding = binding;
            cachedScript.potentialScripts = potentialScripts;
            scriptCache.put(VAR_SCRIPT_NAME, cachedScript);
        }
        return potentialScripts;
    }

    private URLClassLoader createClassLoader(ScriptAndArgs script, File scriptCacheDir) {
        // The class loader we will use to run Gant. It's the root
        // loader plus all the application's compiled classes.
        URLClassLoader classLoader;
        try {
            // JARs already on the classpath should be added.
            Set<String> existingJars = new HashSet<String>();
            for (URL url : settings.getRootLoader().getURLs()) {
                existingJars.add(url.getFile());
            }

            // Add the remaining JARs (from 'griffonHome', the app, and
            // the plugins) to the root loader.
            boolean skipPlugins = "UninstallPlugin".equals(script.name) || "InstallPlugin".equals(script.name);

            URL[] urls = getClassLoaderUrls(settings, scriptCacheDir, existingJars, skipPlugins);
            addUrlsToRootLoader(settings.getRootLoader(), urls);

            // The compiled classes of the application!
            urls = new URL[]{settings.getClassesDir().toURI().toURL()};
            classLoader = new URLClassLoader(urls, settings.getRootLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid classpath URL", ex);
        }
        return classLoader;
    }

    private void initializeProjectInputStream(GantBinding binding) {
        // Gant does not initialise the default input stream for
        // the Ant project, so we manually do it here.
        AntBuilder antBuilder = (AntBuilder) binding.getVariable("ant");
        Project p = antBuilder.getAntProject();
        try {
            p.setDefaultInputStream(System.in);
        } catch (NoSuchMethodError nsme) {
            // will only happen due to a bug in JRockit
            // note - the only approach that works is to loop through the public methods
            for (Method m : p.getClass().getMethods()) {
                if ("setDefaultInputStream".equals(m.getName()) && m.getParameterTypes().length == 1 &&
                        InputStream.class.equals(m.getParameterTypes()[0])) {
                    try {
                        m.invoke(p, System.in);
                        break;
                    } catch (Exception e) {
                        // shouldn't happen, but let it bubble up to the catch(Throwable)
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void loadScriptClass(Gant gant, String scriptName) {
        try {
            // try externalized script first
            gant.loadScriptClass(scriptName + "_");
        } catch (Exception e) {
            try {
                gant.loadScriptClass(scriptName);
            } catch (Exception ex) {
                if (ex instanceof ClassNotFoundException &&
                        ex.getMessage() != null &&
                        ex.getMessage().contains(scriptName)) {
                    throw new ScriptNotFoundException(scriptName);
                }
            }
        }
    }

    public static String fixScriptName(String scriptName, Resource[] allScripts) {
        try {
            Set<String> names = new HashSet<String>();
            for (Resource resource : allScripts) {
                File script = resource.getFile();
                names.add(script.getName().substring(0, script.getName().length() - 7));
            }
            List<String> mostSimilar = CosineSimilarity.mostSimilar(scriptName, names);
            if (mostSimilar.isEmpty()) {
                return null;
            }
            List<String> topMatches = mostSimilar.subList(0, Math.min(5, mostSimilar.size()));
            return askUserForBestMatch(scriptName, topMatches);
        } catch (Exception e) {
            return null;
        }
    }

    public static String askUserForBestMatch(String scriptName, List<String> topMatches) throws IOException {
        System.out.println("Script '" + scriptName + "' not found, did you mean:");
        int i = 0;
        for (String s : topMatches) {
            System.out.println("   " + ++i + ") " + s);
        }

        int attempts = 0;
        while (true) {
            System.out.print("Please make a selection or enter Q to quit: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String selection = br.readLine().trim();
            if ("Q".equalsIgnoreCase(selection)) {
                System.exit(0);
            }

            try {
                int number = Integer.parseInt(selection);
                if (number > 0 && number <= topMatches.size()) {
                    return topMatches.get(number - 1);
                }
            } catch (NumberFormatException ignored) {
                // ignored
            }

            attempts++;
            if (attempts > 4) {
                exitWithError("Failed to make a correct choice. Aborting.");
            }
        }
    }

    public static int executeWithGantInstance(Gant gant, GantBinding binding) {
        try {
            // gant.prepareTargets();
            // Invoke the default target.
            return gant.executeTargets();
        } catch (RuntimeException e) {
            GriffonExceptionHandler.sanitize(e).printStackTrace();
            return 1;
        }
    }

    private boolean isGriffonProject() {
        return new File(settings.getBaseDir(), "griffon-app").exists();
    }

    private boolean isExternalScript(File scriptFile) {
        return scriptsAllowedOutsideOfProject.contains(scriptFile);
    }

    public static String getScriptNameFromFile(File scriptPath) {
        String scriptFileName = scriptPath.getName().substring(0, scriptPath.getName().length() - 7); // trim .groovy extension
        if (scriptFileName.endsWith("_")) {
            scriptFileName = scriptFileName.substring(0, scriptFileName.length() - 1);
        }
        return scriptFileName;
    }

    public static boolean isContextlessScriptName(File scriptPath) {
        String scriptFileName = scriptPath.getName().substring(0, scriptPath.getName().length() - 7);
        return scriptFileName.endsWith("_");
    }

    /**
     * Prep the binding. We add the location of Griffon_HOME under
     * the variable name "griffonHome". We also add a closure that
     * should be used with "includeTargets &&lt;&lt;" - it takes a string
     * and returns either a file containing the named Griffon script
     * or the script class.
     * <p/>
     * So, this:
     * <p/>
     * includeTargets &&lt;&lt; griffonScript("Init")
     * <p/>
     * will load the "Init" script from $Griffon_HOME/scripts if it
     * exists there; otherwise it will load the Init class.
     */
    public GantBinding initBinding(GantBinding binding) {
        Closure c = settings.getGriffonScriptClosure();
        c.setDelegate(binding);
        binding.setVariable("griffonScript", c);
        c = settings.getIncludePluginScriptClosure();
        c.setDelegate(binding);
        binding.setVariable("includePluginScript", c);
        c = settings.getIncludeScriptClosure();
        c.setDelegate(binding);
        binding.setVariable("includeScript", c);
        c = settings.getResolveResourcesClosure();
        c.setDelegate(binding);
        binding.setVariable("resolveResources", c);
        binding.setVariable("griffonSettings", settings);
        binding.setVariable("pluginSettings", settings.pluginSettings);
        settings.pluginSettings.initBinding(binding);

        // Add other binding variables, such as Griffon version and environment.
        final File basedir = settings.getBaseDir();
        final String baseDirPath = basedir.getPath();
        binding.setVariable("basedir", baseDirPath);
        binding.setVariable("baseFile", basedir);
        binding.setVariable("baseName", basedir.getName());
        binding.setVariable("griffonHome", (settings.getGriffonHome() != null ? settings.getGriffonHome().getPath() : null));
        binding.setVariable("griffonVersion", settings.getGriffonVersion());
        binding.setVariable("userHome", settings.getUserHome());
        binding.setVariable("griffonEnv", settings.getGriffonEnv());
        binding.setVariable("defaultEnv", Boolean.valueOf(settings.getDefaultEnv()));
        binding.setVariable("rootLoader", settings.getRootLoader());
        binding.setVariable("buildConfig", settings.getConfig());
        binding.setVariable("buildConfigFile", new File(baseDirPath + "/griffon-app/conf/BuildConfig.groovy"));

        // Add the project paths too!
        String griffonWork = settings.getGriffonWorkDir().getPath();
        binding.setVariable("griffonWorkDir", griffonWork);
        binding.setVariable("projectWorkDir", settings.getProjectWorkDir().getPath());
        binding.setVariable("projectTargetDir", settings.getProjectTargetDir());
        binding.setVariable("classesDir", settings.getClassesDir());
        binding.setVariable("griffonTmp", griffonWork + "/tmp");
        binding.setVariable("classesDirPath", settings.getClassesDir().getPath());
        binding.setVariable("testDirPath", settings.getTestClassesDir().getPath());
        binding.setVariable("resourcesDirPath", settings.getResourcesDir().getPath());
        binding.setVariable("testResourcesDirPath", settings.getTestResourcesDir().getPath());
        binding.setVariable("pluginsDirPath", settings.getProjectPluginsDir().getPath());
        binding.setVariable("platform", PlatformUtils.getPlatform());

        // Create binding variables that contain the locations of each of the
        // plugins loaded by the application. The name of each variable is of
        // the form <pluginName>PluginDir.

        Map<String, String> installedArtifacts = getInstalledArtifacts(Plugin.TYPE);
        for (Map.Entry<String, String> plugin : installedArtifacts.entrySet()) {
            String pluginName = GriffonUtil.getPropertyNameForLowerCaseHyphenSeparatedName(plugin.getKey());

            String version = plugin.getValue();
            binding.setVariable(pluginName + "PluginVersion", version);
            binding.setVariable(pluginName + "PluginDir", ArtifactUtils.getInstallPathFor(Plugin.TYPE, plugin.getKey(), version));
        }

        return binding;
    }

    /**
     * Returns a list of all the executable Gant scripts available to this application.
     */
    /*
    private static List<File> getAvailableScripts(BuildSettings settings) {
        List<File> scripts = new ArrayList<File>();
        if (settings.getGriffonHome() != null) {
            addCommandScripts(new File(settings.getGriffonHome(), "scripts"), scripts);
        }
        addCommandScripts(new File(settings.getBaseDir(), "scripts"), scripts);
        addCommandScripts(new File(settings.getUserHome(), ".griffon/scripts"), scripts);

        for (File dir : listKnownPluginDirs(settings)) {
            addPluginScripts(dir, scripts);
        }

        Collections.sort(scripts);

        return scripts;
    }
    */

    /**
     * Collects all the command scripts provided by the plugin contained
     * in the given directory and adds them to the given list.
     */
    private static void addPluginScripts(File pluginDir, List<File> scripts) {
        if (!pluginDir.exists()) return;

        File scriptDir = new File(pluginDir, "scripts");
        if (scriptDir.exists()) addCommandScripts(scriptDir, scripts);
    }

    /**
     * Adds all the command scripts (i.e. those whose name does *not* start with an
     * underscore, '_') found in the given directory to the given list.
     */
    private static void addCommandScripts(File dir, List<File> scripts) {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (isCommandScript(file)) {
                    scripts.add(file);
                }
            }
        }
    }

    public static boolean isCommandScript(File file) {
        return scriptFilePattern.matcher(file.getName()).matches();
    }

    /**
     * Creates a new root loader with the Griffon libraries and the
     * application's plugin libraries on the classpath.
     */
    private static URL[] getClassLoaderUrls(BuildSettings settings, File cacheDir, Set<String> excludes, boolean skipPlugins) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();

        // If 'griffonHome' is set, make sure the script cache directory takes precedence
        // over the "griffon-scripts" JAR by adding it first.
        if (settings.getGriffonHome() != null) {
            urls.add(cacheDir.toURI().toURL());
        }

        // Add the "resources" directory so that config files and the
        // like can be picked up off the classpath.
        if (settings.getResourcesDir() != null && settings.getResourcesDir().exists()) {
            urls.add(settings.getResourcesDir().toURI().toURL());
        }

        // Add build-only dependencies to the project
        System.out.println("Resolving dependencies...");
        long now = System.currentTimeMillis();
        // add dependencies required by the build system
        final List<File> buildDependencies = settings.getBuildDependencies();
        if (buildDependencies.isEmpty()) {
            exitWithError("Required Griffon build dependencies were not found. Either GRIFFON_HOME is not set or your dependencies are misconfigured in griffon-app/conf/BuildConfig.groovy");
        }
        addDependenciesToURLs(excludes, urls, buildDependencies);
        // Add the project's test dependencies (which include runtime dependencies) because most of them
        // will be required for the build to work.
        // addDependenciesToURLs(excludes, urls, settings.getTestDependencies());

        System.out.println("Dependencies resolved in " + (System.currentTimeMillis() - now) + "ms.");

        /*
        // Add the libraries of project plugins.
        if (!skipPlugins) {
            for (File dir : listKnownPluginDirs(settings)) {
                addPluginLibs(dir, urls, settings);
            }
        }
        */
        return urls.toArray(new URL[urls.size()]);
    }

    private static void addDependenciesToURLs(Set<String> excludes, List<URL> urls, List<File> runtimeDeps) throws MalformedURLException {
        if (runtimeDeps == null) {
            return;
        }

        for (File file : runtimeDeps) {
            if (file == null || urls.contains(file)) {
                continue;
            }

            if (excludes != null && !excludes.contains(file.getName())) {
                urls.add(file.toURI().toURL());
                excludes.add(file.getName());
            }
        }
    }

    /**
     * List all plugin directories that we know about.
     *
     * @param settings The build settings for this project.
     * @return A list of all known plugin directories, or an empty list if there are none.
     */
    /*
    @SuppressWarnings("unchecked")
    public static List<File> listKnownPluginDirs(BuildSettings settings) {
        List<File> dirs = new ArrayList<File>();

        // Next up, the project's plugins directory.
        dirs.addAll(asList(listPluginDirs(settings.getProjectPluginsDir())));

        return dirs;
    }
    */

    /**
     * Adds all the libraries in a plugin to the given list of URLs.
     *
     * @param pluginDir The directory containing the plugin.
     * @param urls      The list of URLs to add the plugin JARs to.
     * @param settings
     */
    /*
    private static void addPluginLibs(File pluginDir, List<URL> urls, BuildSettings settings) throws MalformedURLException {
        if (!pluginDir.exists()) return;

        // otherwise just add them
        File libDir = new File(pluginDir, "lib");
        if (libDir.exists()) {
            final IvyDependencyManager dependencyManager = settings.getDependencyManager();
            String pluginName = getPluginName(pluginDir);
            Collection<?> excludes = dependencyManager.getPluginExcludes(pluginName);
            // TODO filter out platform directories
            // TODO add native directories
            addLibs(libDir, urls, excludes != null ? excludes : Collections.emptyList());
        }

        libDir = new File(pluginDir, "addon");
        if (libDir.exists()) {
            final IvyDependencyManager dependencyManager = settings.getDependencyManager();
            String pluginName = getPluginName(pluginDir);
            Collection<?> excludes = dependencyManager.getPluginExcludes(pluginName);
            addLibs(libDir, urls, excludes != null ? excludes : Collections.emptyList());
        }
    }
    */

    /**
     * Adds all the JAR files in the given directory to the list of URLs. Excludes any
     * "standard-*.jar" and "jstl-*.jar" because these are added to the classpath in another
     * place. They depend on the servlet version of the app and so need to be treated specially.
     */
    private static void addLibs(File dir, List<URL> urls, Collection<?> excludes) throws MalformedURLException {
        if (!dir.exists()) {
            return;
        }

        for (File file : dir.listFiles()) {
            boolean include = true;
            for (Object me : excludes) {
                String exclude = me.toString();
                if (file.getName().contains(exclude)) {
                    include = false;
                    break;
                }
            }
            if (include) {
                urls.add(file.toURI().toURL());
            }
        }
    }

    /**
     * Lists all the sub-directories (non-recursively) of the given
     * directory that look like directories that contain a plugin.
     * If there are no directories, an empty array is returned. We
     * basically check that the name of each directory looks about
     * right.
     */
    /*
    private static File[] listPluginDirs(File dir) {
        File[] dirs = dir.listFiles(new FileFilter() {
            public boolean accept(File path) {
                return path.isDirectory() &&
                        !path.getName().startsWith(".") &&
                        path.getName().indexOf('-') > -1;
            }
        });

        return dirs == null ? new File[0] : dirs;
    }
    */

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
     *
     * @param loader The root loader whose classpath we want to extend.
     * @param urls   The URLs to add to the root loader's classpath.
     */
    private static void addUrlsToRootLoader(URLClassLoader loader, URL[] urls) {
        URL[] existingUrls = loader.getURLs();
        try {
            Class loaderClass = loader.getClass();
            Method method = loaderClass.getMethod("addURL", URL.class);
            top:
            for (URL url : urls) {
                String path = url.getPath();
                if (path.endsWith(".jar")) {
                    path = path.substring(path.lastIndexOf("/"));
                    for (URL xurl : existingUrls) {
                        String xpath = xurl.getPath();
                        if (xpath.endsWith(path)) continue top;
                    }
                }
                method.invoke(loader, url);
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Cannot dynamically add URLs to GriffonScriptRunner's" +
                            " class loader - make sure that it is loaded by Groovy's" +
                            " RootLoader or a sub-class.");
        }
    }

    /**
     * Gets the name of a plugin based on its directory. The method
     * basically finds the plugin descriptor and uses the name of the
     * class to determine the plugin name. To be honest, this class
     * shouldn't be plugin-aware in my view, so hopefully this will
     * only be a temporary method.
     *
     * @param pluginDir The directory containing the plugin.
     * @return The name of the plugin contained in the given directory.
     */
    /*
    private static String getPluginName(File pluginDir) {
        // Get the plugin descriptor from the given directory and use
        // it to infer the name of the plugin.
        File desc = new File(pluginDir, "plugin.json");
        if (!desc.exists()) {
            throw new RuntimeException("Cannot find plugin descriptor in plugin directory '" + pluginDir + "'.");
        }
        return Release.makeFromFile(Plugin.TYPE, desc).getArtifact().getName();
    }
    */

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

    private BuildListener griffonInitBuildListener;

    private static class GriffonInitBuildListener extends BuildListenerAdapter {
        private final BuildSettings settings;
        private final Binding binding;
        private final Gant gant;

        private GriffonInitBuildListener(BuildSettings settings, Binding binding, Gant gant) {
            this.settings = settings;
            this.binding = binding;
            this.gant = gant;
            // preload basic stuff that should always be there
            setupScript("_GriffonSettings");
            setupScript("_GriffonArgParsing");
            setupScript("_GriffonEvents");
            setupScript("_GriffonProxy");
            setupScript("_ResolveDependencies");
            File scriptFile = (File) binding.getVariable(VAR_SCRIPT_FILE);
            gant.loadScript(scriptFile);
            gant.prepareTargets();

            gant.setAllPerTargetPreHooks(DO_NOTHING_CLOSURE);
            gant.setAllPerTargetPostHooks(DO_NOTHING_CLOSURE);
        }

        private void setupScript(String scriptName) {
            gant.loadScript(scriptFileFor(scriptName));
            gant.prepareTargets();
        }

        private File scriptFileFor(String scriptName) {
            return new File(settings.getGriffonHome(), "scripts/" + scriptName + ".groovy");
        }

        @Override
        public void targetStarted(BuildEvent buildEvent) {
            String targetName = buildEvent.getTarget().getName();
            String defaultTargetName = (String) binding.getVariable("defaultTarget");
            File scriptFile = (File) binding.getVariable(VAR_SCRIPT_FILE);
            if (defaultTargetName.equals(targetName)) {
                List<String> targets = new ArrayList<String>();
                targets.add("parseArguments");
                if (binarySearch(CONFIGURE_PROXY_EXCLUSIONS, targetName) > -1) targets.add("configureProxy");
                if (!isContextlessScriptName(scriptFile) &&
                        binarySearch(RESOLVE_DEPENDENCIES_EXCLUSIONS, targetName) < 0) {
                    targets.add("resolveDependencies");
                    targets.add("loadEventHooks");
                }
                settings.debug("** " + targets + " **");
                gant.executeTargets("dispatch", targets);
            }
        }

        private static String[] CONFIGURE_PROXY_EXCLUSIONS = {
                "addProxy", "clearProxy", "removeProxy", "setProxy", "configureProxy",
                "help", "integrateWith", "setVersion", "showHelp", "stats",
                "createAddon", "createPlugin", "upgrade"
        };

        private static String[] RESOLVE_DEPENDENCIES_EXCLUSIONS = {
                "integrateWith", "setVersion", "stats", "upgrade"
        };
    }
}
