/*
 * Copyright 2004-2012 the original author or authors.
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
import groovy.lang.MissingPropertyException;
import groovy.util.AntBuilder;
import org.apache.log4j.LogManager;
import org.apache.tools.ant.Project;
import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.artifacts.ArtifactRepositoryRegistry;
import org.codehaus.griffon.cli.parsing.CommandLine;
import org.codehaus.griffon.cli.parsing.CommandLineParser;
import org.codehaus.griffon.cli.parsing.DefaultCommandLine;
import org.codehaus.griffon.cli.parsing.ParseException;
import org.codehaus.griffon.plugins.PluginInfo;
import org.codehaus.griffon.runtime.logging.Log4jConfig;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.core.io.Resource;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Pattern;

import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

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
    public static final String VAR_SCRIPT_NAME = "scriptName";
    public static final String VAR_SCRIPT_FILE = "scriptFile";
    public static final String VAR_SCRIPT_ENV = "scriptEnv";
    public static final String VAR_SCRIPT_ARGS_MAP = "argsMap";
    public static final String VAR_SCRIPT_UNPARSED_ARGS = "unparsedArgs";
    public static final String VAR_SYS_PROPERTIES = "sysProperties";
    public static final String KEY_SCRIPT_ARGS = "griffon.cli.args";

    /**
     * Evaluate the arguments to get the name of the script to execute, which environment
     * to run it in, and the arguments to pass to the script. This also evaluates arguments
     * of the form "-Dprop=value" and creates system properties from each one.
     *
     * @param args
     */
    public static void main(String[] args) {
        GriffonExceptionHandler.registerExceptionHandler();

        CommandLine commandLine = getCommandLine(args);

        // Get hold of the Griffon_HOME environment variable if it is
        // available.
        String griffonHome = System.getProperty("griffon.home");
        ScriptAndArgs script = processArgumentsAndReturnScriptName(commandLine);

        // Remember unparsed commandline args for the benefit of scripts that want to process them
        script.unparsedArgs = args;

        if (commandLine.hasOption(CommandLine.HELP_ARGUMENT)) {
            System.out.println(getCommandLineParser().getHelpMessage());
            System.exit(0);
        }

        BuildSettings build = null;
        try {
            build = new BuildSettings(new File(griffonHome));
            BuildSettingsHolder.setSettings(build);
        } catch (Exception e) {
            System.err.println("An error occurred loading the griffon-app/conf/BuildConfig.groovy file: " + e.getMessage());
            System.exit(1);
        }

        if (commandLine.hasOption(CommandLine.VERSION_ARGUMENT) || commandLine.hasOption(CommandLine.VERSION_ARGUMENT_ALIAS)) {
            System.out.println(GriffonEnvironment.prettyPrint());
            System.exit(0);
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

        GriffonSetup.run();

        // If there aren't any arguments, then we don't have a command
        // to execute. So we have to exit.
        if (script.name == null) {
            System.out.println("No script name specified. Use 'griffon help' for more info or 'griffonsh' to enter interactive mode");
            System.exit(0);
        }

        System.out.println("Base Directory: " + build.getBaseDir().getPath());

        try {
            int exitCode = new GriffonScriptRunner(build).doExecuteCommand(script);
            System.exit(exitCode);
        } catch (ScriptNotFoundException ex) {
            System.out.println("Script not found: " + ex.getScriptName());
        } catch (Throwable t) {
            String msg = "Error executing script " + script.name + ": " + t.getMessage();
            System.out.println(msg);
            sanitize(t);
            t.printStackTrace(System.out);
            exitWithError(msg);
        }
    }

    public static CommandLineParser getCommandLineParser() {
        CommandLineParser parser = new CommandLineParser();
        parser.addOption(CommandLine.NON_INTERACTIVE_ARGUMENT, "Whether to allow the command line to request input");
        parser.addOption(CommandLine.HELP_ARGUMENT, "Command line help");
        parser.addOption(CommandLine.VERSION_ARGUMENT, "Current Griffon version");
        return parser;
    }

    public static CommandLine getCommandLine(String[] args) {
        CommandLineParser parser = getCommandLineParser();

        try {
            if (args.length == 0) {
                return new DefaultCommandLine();
            } else {
                return parser.parseString(DefaultGroovyMethods.join(args, " "));
            }
        } catch (ParseException e) {
            System.err.println("Error processing command line arguments: " + sanitize(e).getMessage());
            System.exit(1);
        }

        return null;
    }

    private static void exitWithError(String error) {
        System.out.println(error);
        System.exit(1);
    }

    private static ScriptAndArgs processArgumentsAndReturnScriptName(CommandLine commandLine) {
        ScriptAndArgs info = new ScriptAndArgs();
        processSystemArguments(commandLine, info);
        return processAndReturnArguments(commandLine, info);
    }

    private static ScriptAndArgs processAndReturnArguments(CommandLine commandLine, ScriptAndArgs info) {
        if (Environment.isSystemSet()) {
            info.env = Environment.getCurrent().getName();
        } else if (commandLine.getEnvironment() != null) {
            info.env = commandLine.getEnvironment();
        }

        info.name = GriffonUtil.getNameFromScript(commandLine.getCommandName());
        info.params.addAll(commandLine.getRemainingArgs());
        info.options.putAll(commandLine.getOptions());
        return info;
    }

    private static void processSystemArguments(CommandLine allArgs, ScriptAndArgs info) {
        Properties systemProps = allArgs.getSystemProperties();
        if (systemProps != null) {
            for (Map.Entry<Object, Object> entry : systemProps.entrySet()) {
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
                info.sysProperties.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
    }

    public static String unquote(String s) {
        if ((s.startsWith("'") && s.endsWith("'")) ||
                (s.startsWith("\"") && s.endsWith("\""))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
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

    public BuildSettings getSettings() {
        return settings;
    }

    public PrintStream getOut() {
        return this.out;
    }

    public void setOut(PrintStream outputStream) {
        this.out = outputStream;
        this.helper = new CommandLineHelper(out);
    }

    public int executeCommand(String name, String args) {
        String cmd = name;
        if (!isBlank(args)) cmd += " " + args;
        String[] newArgs = cmd.split(" ");
        ScriptAndArgs script = new ScriptAndArgs();
        processAndReturnArguments(getCommandLine(newArgs), script);
        return doExecuteCommand(script);
    }

    public int executeCommand(String name, String args, String env) {
        String cmd = env + " " + name;
        if (!isBlank(args)) cmd += " " + args;
        String[] newArgs = cmd.split(" ");
        ScriptAndArgs script = new ScriptAndArgs();
        processAndReturnArguments(getCommandLine(newArgs), script);
        return doExecuteCommand(script);
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public void setInteractive(boolean interactive) {
        isInteractive = interactive;
    }

    public void setup() {
        // Populate the root loader with all libraries that this app
        // depends on. If a root loader doesn't exist yet, create it now.
        if (settings.getRootLoader() == null) {
            settings.setRootLoader((URLClassLoader) GriffonScriptRunner.class.getClassLoader());
        }

        // Load the BuildSettings file for this project if it exists. Note
        // that this does not load any environment-specific settings.
        BuildSettingsHolder.setSettings(settings);
        settings.loadConfig();
        BuildSettingsHolder.setSettings(settings);
        setLoggingOptions();
        ArtifactRepositoryRegistry.getInstance().configureRepositories();
    }

    private int doExecuteCommand(ScriptAndArgs script) {
        setup();

        settings.debug("Executing script name: " + script.name + " env: " + script.env + " options: " + script.options + " params: " + script.params);

        script.options.put("params", script.params);
        return callPluginOrGriffonScript(script);
    }

    public void setLoggingOptions() {
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

    private final Map<String, CachedScript> scriptCache = new HashMap<String, CachedScript>();
    private final List<File> scriptsAllowedOutsideOfProject = new ArrayList<File>();
    public static final Closure DO_NOTHING_CLOSURE = new Closure(GriffonScriptRunner.class) {
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
        List<File> potentialScripts;
        Resource[] allScripts = settings.pluginSettings.getAvailableScripts();
        GantBinding binding = new GantBinding();
        initializeProjectInputStream(binding);
        potentialScripts = findPotentialScripts(script, allScripts, binding);

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
                binding.setVariable(VAR_SCRIPT_ARGS_MAP, script.options);
                binding.setVariable(VAR_SCRIPT_UNPARSED_ARGS, script.unparsedArgs);
                binding.setVariable(VAR_SYS_PROPERTIES, script.sysProperties);
                script.name = scriptFileName;

                // Setup the script to call.
                Gant gant = createGantInstance(binding);
                // gant.loadScript(scriptFile);
                return executeWithGantInstanceNoException(gant, binding);
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
            binding.setVariable(VAR_SCRIPT_ARGS_MAP, script.options);
            binding.setVariable(VAR_SCRIPT_UNPARSED_ARGS, script.unparsedArgs);
            binding.setVariable(VAR_SYS_PROPERTIES, script.sysProperties);
            script.name = scriptFileName;

            // Set up the script to call.
            Gant gant = createGantInstance(binding);
            // gant.loadScript(scriptFile);

            // Invoke the default target.
            return executeWithGantInstanceNoException(gant, binding);
        }
        return attemptPrecompiledScriptExecute(script, binding, allScripts);
    }

    private int attemptPrecompiledScriptExecute(ScriptAndArgs script, GantBinding binding, Resource[] allScripts) {
        out.println("Running pre-compiled script");

        // Must be called before the binding is initialised.
        setRunningEnvironment(script.name, script.env);

        Gant gant = createGantInstance(binding);
        String scriptName = script.name;
        binding.setVariable(VAR_SCRIPT_NAME, scriptName);
        binding.setVariable(VAR_SCRIPT_ARGS_MAP, script.options);
        binding.setVariable(VAR_SCRIPT_UNPARSED_ARGS, script.unparsedArgs);
        binding.setVariable(VAR_SYS_PROPERTIES, script.sysProperties);

        try {
            loadScriptClass(gant, scriptName);
        } catch (ScriptNotFoundException e) {
            if (isInteractive) {
                scriptName = fixScriptName(scriptName, allScripts);
                if (scriptName == null) {
                    throw e;
                }

                loadScriptClass(gant, scriptName);

                // at this point if they were calling a script that has a non-default
                // env (e.g. war or test-app) it wouldn't have been correctly set, so
                // set it now, but only if they didn't specify the env (e.g. "grails test war" -> "grails test war")

                if (Boolean.TRUE.toString().equals(System.getProperty(Environment.DEFAULT))) {
                    setRunningEnvironment(scriptName, script.env);
                    settings.setDefaultEnv(false);
                    System.setProperty(Environment.DEFAULT, Boolean.FALSE.toString());
                }
            } else {
                throw e;
            }
        }

        return executeWithGantInstanceNoException(gant, binding);
    }

    public Gant createGantInstance(GantBinding binding) {
        URLClassLoader classLoader = createClassLoader();
        Gant gant = new Gant(initBinding(binding), classLoader);
        gantCustomizer = new GantCustomizer(settings, binding, gant);
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
                sanitize(e);
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

    private URLClassLoader createClassLoader() {
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

            URL[] urls = getClassLoaderUrls(settings, existingJars);
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
            gantCustomizer.prepareTargets();
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

    public int executeWithGantInstance(Gant gant, GantBinding binding) {
        try {
            gantCustomizer.prepareTargets();
            return gant.executeTargets();
        } catch (RuntimeException e) {
            sanitize(e);
            throw e;
        }
    }

    public int executeWithGantInstance(Gant gant, GantBinding binding, List<String> targets) {
        try {
            gantCustomizer.prepareTargets();
            return gant.executeTargets("dispatch", targets);
        } catch (RuntimeException e) {
            sanitize(e);
            throw e;
        }
    }

    private int executeWithGantInstanceNoException(Gant gant, GantBinding binding) {
        try {
            return executeWithGantInstance(gant, binding);
        } catch (RuntimeException e) {
            e.printStackTrace();
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

    public static boolean isContextlessScriptName(String scriptName) {
        if (scriptName.endsWith(".groovy")) {
            scriptName = scriptName.substring(0, scriptName.length() - 7);
        } else if (scriptName.endsWith(".class")) {
            scriptName = scriptName.substring(0, scriptName.length() - 6);
        }
        return scriptName.endsWith("_");
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
        binding.setVariable("artifactSettings", settings.artifactSettings);
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

        Map<String, PluginInfo> installedArtifacts = settings.pluginSettings.getPlugins();
        for (PluginInfo pluginInfo : installedArtifacts.values()) {
            String pluginName = GriffonUtil.getPropertyNameForLowerCaseHyphenSeparatedName(pluginInfo.getName());
            String version = pluginInfo.getVersion();
            binding.setVariable(pluginName + "PluginVersion", version);
            binding.setVariable(pluginName + "PluginDir", pluginInfo.getDirectory());
        }

        return binding;
    }

    public static boolean isCommandScript(File file) {
        return scriptFilePattern.matcher(file.getName()).matches();
    }

    /**
     * Creates a new root loader with the Griffon libraries and the
     * application's plugin libraries on the classpath.
     */
    private static URL[] getClassLoaderUrls(BuildSettings settings, Set<String> excludes) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();

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
        System.out.println("Dependencies resolved in " + (System.currentTimeMillis() - now) + "ms.");

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
     * Contains details about a Griffon command invocation such as the
     * name of the corresponding script, the environment (if specified),
     * and the arguments to the command.
     */
    private static class ScriptAndArgs {
        public String name;
        public String env;
        public String[] unparsedArgs;
        public List<String> params = new ArrayList<String>();
        public Map<String, Object> options = new LinkedHashMap<String, Object>();
        public Map<String, String> sysProperties = new LinkedHashMap<String, String>();
    }

    private GantCustomizer gantCustomizer;

    public static class GantCustomizer {
        private final BuildSettings settings;
        private final Binding binding;
        private final Gant gant;
        private boolean prepared = false;

        private GantCustomizer(BuildSettings settings, Binding binding, Gant gant) {
            this.settings = settings;
            this.binding = binding;
            this.gant = gant;
        }

        private void setupScript(String scriptName) {
            File scriptFile = scriptFileFor(scriptName);
            if (scriptFile != null && scriptFile.exists()) {
                gant.loadScript(scriptFile);
            } else {
                gant.loadScriptClass(scriptName);
            }
            gant.prepareTargets();
        }

        private File scriptFileFor(String scriptName) {
            return new File(settings.getGriffonHome(), "scripts/" + scriptName + ".groovy");
        }

        public void prepareTargets() {
            if (!prepared) {
                // preload basic stuff that should always be there
                setupScript("_GriffonSettings");
                setupScript("_GriffonEvents");
                setupScript("_GriffonProxy");
                setupScript("_GriffonResolveDependencies");
                setupScript("_GriffonClasspath");

                String scriptName = (String) binding.getVariable(VAR_SCRIPT_NAME);
                File scriptFile = null;
                try {
                    scriptFile = (File) binding.getVariable(VAR_SCRIPT_FILE);
                } catch (MissingPropertyException mpe) {
                    //ignore
                }
                if (scriptFile != null && scriptFile.exists()) {
                    scriptName = scriptFile.getName();
                    if (scriptName.endsWith(".groovy")) {
                        scriptName = scriptName.substring(0, scriptName.length() - ".groovy".length());
                    }
                }

                List<String> targets = new ArrayList<String>();
                if (binarySearch(CONFIGURE_PROXY_EXCLUSIONS, scriptName) < 0) targets.add("configureProxy");
                if (!isContextlessScriptName(scriptName)) {
                    if (binarySearch(CHECK_VERSION_EXCLUSIONS, scriptName) < 0) {
                        targets.add("checkVersion");
                    }
                    if (!isExcludedFromDependencyResolution(scriptName)) {
                        targets.add("resolveFrameworkDependencies");
                        targets.add("resolveDependencies");
                    }
                    targets.add("loadEventHooks");
                } else if (binarySearch(FRAMEWORK_PLUGIN_INCLUSIONS, scriptName) >= 0) {
                    targets.add("resolveFrameworkDependencies");
                    if (settings.isGriffonProject() && !isExcludedFromDependencyResolution(scriptName)) {
                        targets.add("resolveDependencies");
                    }
                    targets.add("loadEventHooks");
                }

                settings.debug("** " + targets + " **");
                gant.setAllPerTargetPreHooks(DO_NOTHING_CLOSURE);
                gant.setAllPerTargetPostHooks(DO_NOTHING_CLOSURE);
                gant.executeTargets("dispatch", targets);

                if (scriptFile != null) {
                    gant.loadScript(scriptFile);
                }
                prepared = true;
            }
            gant.prepareTargets();
            gant.setAllPerTargetPreHooks(DO_NOTHING_CLOSURE);
            gant.setAllPerTargetPostHooks(DO_NOTHING_CLOSURE);
        }

        private boolean isExcludedFromDependencyResolution(String scriptName) {
            if (binarySearch(RESOLVE_DEPENDENCIES_EXCLUSIONS, scriptName) >= 0) {
                return true;
            }
            List<String> exclusions = (List<String>) getConfigValue(settings.getConfig(), "griffon.dependency.resolution.command.exclusions", new ArrayList<String>());
            return exclusions.contains(scriptName);
        }

        private static String[] CONFIGURE_PROXY_EXCLUSIONS = {
                "AddProxy", "ClearProxy", "RemoveProxy", "SetProxy", "ConfigureProxy",
                "SetVersion", "Stats",
                "CreateAddon", "CreatePlugin", "Upgrade",
                "CreateCommandAlias", "Doc", "ClearDependencyCache"
        };

        private static String[] RESOLVE_DEPENDENCIES_EXCLUSIONS = {
                "SetVersion", "Stats", "Upgrade",
                "CreateCommandAlias", "Doc", "_GriffonResolveDependencies"
        };

        private static String[] CHECK_VERSION_EXCLUSIONS = {
                "Upgrade"
        };

        private static String[] FRAMEWORK_PLUGIN_INCLUSIONS = {
                "CreateApp_", "CreateAddon_", "CreatePlugin_", "CreateArchetype_", "Help_"
        };

        static {
            sort(CONFIGURE_PROXY_EXCLUSIONS);
            sort(RESOLVE_DEPENDENCIES_EXCLUSIONS);
            sort(CHECK_VERSION_EXCLUSIONS);
            sort(FRAMEWORK_PLUGIN_INCLUSIONS);
        }
    }
}
