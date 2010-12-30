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

import org.codehaus.groovy.tools.LoaderConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

import griffon.util.GriffonExceptionHandler;

/**
 * @author Graeme Rocher (Grails 1.0)
 */
public class GriffonStarter {
    static void printUsage() {
        System.out.println("possible programs are 'groovyc','groovy','console', and 'groovysh'");
        System.exit(1);
    }

    public static void rootLoader(String args[]) {
        String conf = System.getProperty("groovy.starter.conf",null);
/*
        final String separator = System.getProperty("file.separator");

        // Set some default values for various system properties if
        // they don't already have values.
*/
        String javaVersion = System.getProperty("java.version");
        String griffonHome = System.getProperty("griffon.home");
/*
        if (System.getProperty("base.dir") == null) System.setProperty("base.dir", ".");
        if (System.getProperty("program.name") == null) System.setProperty("program.name", "griffon");
        if (System.getProperty("groovy.starter.conf") == null) {
            System.setProperty(
                    "groovy.starter.conf",
                    griffonHome + separator + "conf" + separator + "groovy-starter.conf");
        }

        // Initialise the Griffon version if it's not set already.
        if (System.getProperty("griffon.version") == null) {
            Properties griffonProps = new Properties();
            FileInputStream is = null;
            try {
                // Load Griffon' "build.properties" file.
                is = new FileInputStream(griffonHome + separator + "build.properties");
                griffonProps.load(is);

                // Extract the Griffon version and store as a system
                // property so that it can be referenced from the
                // starter configuration file.
                System.setProperty("griffon.version", griffonProps.getProperty("griffon.version"));
            }
            catch (IOException ex) { System.out.println("Failed to load Griffon file: " + ex.getMessage()); System.exit(1); }
            finally { if (is != null) try { is.close(); } catch (IOException ex2) {} }
        }

        String conf = System.getProperty("groovy.starter.conf", null);
*/
        LoaderConfiguration lc = new LoaderConfiguration();

        // evaluate parameters
        boolean hadMain=false, hadConf=false, hadCP=false;
        int argsOffset = 0;
        while (args.length-argsOffset>0 && !(hadMain && hadConf && hadCP)) {
            if (args[argsOffset].equals("--classpath")) {
                if (hadCP) break;
                if (args.length==argsOffset+1) {
                    exit("classpath parameter needs argument");
                }
                lc.addClassPath(args[argsOffset+1]);
                argsOffset+=2;
            } else if (args[argsOffset].equals("--main")) {
                if (hadMain) break;
                if (args.length==argsOffset+1) {
                    exit("main parameter needs argument");
                }
                lc.setMainClass(args[argsOffset+1]);
                argsOffset+=2;
            } else if (args[argsOffset].equals("--conf")) {
                if (hadConf) break;
                if (args.length==argsOffset+1) {
                    exit("conf parameter needs argument");
                }
                conf=args[argsOffset+1];
                argsOffset+=2;
            } else {
                break;
            }
        }

        // We need to know the class we want to start
        if (lc.getMainClass()==null) {
            lc.setMainClass("org.codehaus.griffon.cli.GriffonScriptRunner");
        }

        // copy arguments for main class
        String[] newArgs = new String[args.length-argsOffset];
        System.arraycopy(args, argsOffset, newArgs, 0, newArgs.length);

        String basedir = System.getProperty("base.dir");
        if(basedir!=null) {
            try {
                System.setProperty("base.name", new File(basedir).getCanonicalFile().getName());
            } catch (IOException e) {
                // ignore
            }
        }
        // load configuration file
        if (conf!=null) {
            try {
                lc.configure(new FileInputStream(conf));
            } catch (Exception e) {
                System.err.println("exception while configuring main class loader:");
                exit(e);
            }
        }

        Properties metadata = new Properties();
        File metadataFile = new File("./application.properties");
        if(metadataFile.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(metadataFile);
                metadata.load(inputStream);
            } catch (IOException e) {
                // ignore
            }
            finally {
                try {
                    if(inputStream!=null) inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        // create loader and execute main class
        GriffonRootLoader loader = new GriffonRootLoader(GriffonStarter.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(loader);

        // configure class loader
        URL[] urls = lc.getClassPathUrls();
        for (URL url : urls) {
            loader.addURL(url);
        }

        if(javaVersion != null && griffonHome != null) {
            javaVersion = javaVersion.substring(0,3);
            File vmConfig = new File(griffonHome +"/conf/groovy-starter-java-"+javaVersion+".conf");
            if(vmConfig.exists()) {
                InputStream in = null;
                try {
                    in = new FileInputStream(vmConfig);
                    LoaderConfiguration vmLoaderConfig = new LoaderConfiguration();
                    vmLoaderConfig.setRequireMain(false);
                    vmLoaderConfig.configure(in);
                    URL[] vmSpecificClassPath = vmLoaderConfig.getClassPathUrls();
                    for (int i = 0; i < vmSpecificClassPath.length; i++) {
                        loader.addURL(vmSpecificClassPath[i]);

                    }
                } catch (IOException e) {
                    System.out.println("WARNING: I/O error reading VM specific classpath ["+vmConfig+"]: " + e.getMessage() );
                }
                finally {
                    try {
                        if(in != null) in.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }

        Method m=null;
        try {
            Class c = loader.loadClass(lc.getMainClass());
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
        e.printStackTrace();
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
            GriffonExceptionHandler.registerExceptionHandler();
            rootLoader(args);
        } catch (Throwable t) {
            System.out.println("Error starting Griffon: " + t.getMessage());
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }
}

