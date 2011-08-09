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
package org.codehaus.griffon.cli.support;

import org.codehaus.groovy.runtime.StackTraceUtils;
import org.codehaus.groovy.tools.LoaderConfiguration;
import org.codehaus.groovy.tools.RootLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

/**
 * @author Graeme Rocher (Grails 1.0)
 */
public class GriffonStarter {
    private static final String GRIFFON_ROOT_CLASSLOADER = "griffon.root.classloader";
    private static final String LOADER_FILE = ".loader";

    public static void rootLoader(String args[]) {
        final String separator = System.getProperty("file.separator");

        // Set some default values for various system properties if
        // they don't already have values.
        String javaVersion = System.getProperty("java.version");
        String griffonHome = System.getProperty("griffon.home");
        if (System.getProperty("base.dir") == null) System.setProperty("base.dir", ".");
        if (System.getProperty("program.name") == null) System.setProperty("program.name", "griffon");
        if (System.getProperty("groovy.starter.conf") == null) {
            System.setProperty(
                    "groovy.starter.conf",
                    griffonHome + separator + "conf" + separator + "groovy-starter.conf");
        }

        String conf = System.getProperty("groovy.starter.conf", null);
        LoaderConfiguration lc = new LoaderConfiguration();

        // evaluate parameters
        boolean hadMain=false, hadConf=false, hadCP=false;
        int argsOffset = 0;
        while (args.length - argsOffset > 0 && !(hadMain && hadConf && hadCP)) {
            if (args[argsOffset].equals("--classpath")) {
                if (hadCP) break;
                if (args.length == argsOffset + 1) {
                    exit("classpath parameter needs argument");
                }
                lc.addClassPath(args[argsOffset + 1]);
                argsOffset += 2;
            } else if (args[argsOffset].equals("--main")) {
                if (hadMain) break;
                if (args.length == argsOffset + 1) {
                    exit("main parameter needs argument");
                }
                lc.setMainClass(args[argsOffset + 1]);
                argsOffset += 2;
            } else if (args[argsOffset].equals("--conf")) {
                if (hadConf) break;
                if (args.length == argsOffset + 1) {
                    exit("conf parameter needs argument");
                }
                conf = args[argsOffset + 1];
                argsOffset += 2;
            } else {
                break;
            }
        }

        // We need to know the class we want to start
        if (lc.getMainClass() == null) {
            lc.setMainClass("org.codehaus.griffon.cli.GriffonScriptRunner");
        }

        // copy arguments for main class
        String[] newArgs = new String[args.length-argsOffset];
        System.arraycopy(args, argsOffset, newArgs, 0, newArgs.length);

        String basedir = System.getProperty("base.dir");
        if (basedir != null) {
            try {
                System.setProperty("base.name", new File(basedir).getCanonicalFile().getName());
            } catch (IOException e) {
                // ignore
            }
        }
        // load configuration file
        if (conf != null) {
            try {
                lc.configure(new FileInputStream(conf));
            } catch (Exception e) {
                System.err.println("exception while configuring main class loader:");
                exit(e);
            }
        }

        // create loader and execute main class
        RootLoader loader = null;
        File loaderFile = new File(LOADER_FILE);
        String loaderClassName = null;
        if (loaderFile.exists()) {
            Properties loaderProps = new Properties();
            FileInputStream input = null;
            try {
                input = new FileInputStream(loaderFile);
                loaderProps.load(input);
                loaderClassName = loaderProps.getProperty(GRIFFON_ROOT_CLASSLOADER);
            } catch (Exception e) {
                StackTraceUtils.deepSanitize(e).printStackTrace();
                System.out.println("ERROR: There was an error loading a Griffon custom classloader using the properties file ["+loaderFile.getAbsolutePath()+"]: " + e.getClass().getName() + ":" + e.getMessage());
            }
            finally {
                try {
                    if (input != null) input.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        if (loaderClassName == null) {
            loaderClassName = System.getProperty(GRIFFON_ROOT_CLASSLOADER);
        }
        if (loaderClassName != null) {
            try {
                Class<?> loaderClass = GriffonStarter.class.getClassLoader().loadClass(loaderClassName);
                loader = (RootLoader) loaderClass.newInstance();
            } catch (Exception e) {
                StackTraceUtils.deepSanitize(e).printStackTrace();
                System.out.println("ERROR: There was an error loading a Griffon custom classloader using the properties file ["+loaderFile.getAbsolutePath()+"]: " + e.getClass().getName() + ":" + e.getMessage());
            }
        }

        if (loader == null) {
            loader = new GriffonRootLoader();
        }

        Thread.currentThread().setContextClassLoader(loader);
        // configure class loader
        for (URL url : lc.getClassPathUrls()) {
            loader.addURL(url);
        }

        if (javaVersion != null && griffonHome != null) {
            javaVersion = javaVersion.substring(0,3);
            File vmConfig = new File(griffonHome +"/conf/groovy-starter-java-"+javaVersion+".conf");
            if (vmConfig.exists()) {
                InputStream in = null;
                try {
                    in = new FileInputStream(vmConfig);
                    LoaderConfiguration vmLoaderConfig = new LoaderConfiguration();
                    vmLoaderConfig.setRequireMain(false);
                    vmLoaderConfig.configure(in);
                    URL[] vmSpecificClassPath = vmLoaderConfig.getClassPathUrls();
                    for (URL aVmSpecificClassPath : vmSpecificClassPath) {
                        loader.addURL(aVmSpecificClassPath);
                    }
                } catch (IOException e) {
                    System.out.println("WARNING: I/O error reading VM specific classpath ["+vmConfig+"]: " + e.getMessage() );
                }
                finally {
                    try {
                        if (in != null) in.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }

        Method m = null;
        try {
            Class<?> c = loader.loadClass(lc.getMainClass());
            m = c.getMethod("main", new Class[]{String[].class});
        } catch (ClassNotFoundException e1) {
            exit(e1);
        } catch (SecurityException e2) {
            exit(e2);
        } catch (NoSuchMethodException e2) {
            exit(e2);
        }
        try {
            m.invoke(null, new Object[]{newArgs});
        } catch (IllegalArgumentException e3) {
            exit(e3);
        } catch (IllegalAccessException e3) {
            exit(e3);
        } catch (InvocationTargetException e3) {
            exit(e3);
        }
    }

    private static void exit(Exception e) {
        StackTraceUtils.deepSanitize(e).printStackTrace();
        System.exit(1);
    }

    private static void exit(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    // after migration from classworlds to the rootloader rename
    // the rootLoader method to main and remove this method as
    // well as the classworlds method
    public static void main(String args[]) {
        try {
            rootLoader(args);
        } catch (Throwable t) {
            System.out.println("Error starting Griffon: " + t.getMessage());
            StackTraceUtils.deepSanitize(t).printStackTrace(System.err);
            System.exit(1);
        }
    }
}
