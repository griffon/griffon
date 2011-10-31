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
package org.gradle.wrapper;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hans Dockter
 */
public class BootstrapMainStarter {
    public void start(String[] args, String griffonHome, String version) throws Exception {
        boolean debug = GriffonWrapperMain.isDebug();

        File griffonHomeDir = new File(griffonHome);
        List<File> libs = new ArrayList<File>();
        libs.add(new File(griffonHomeDir, "dist/griffon-cli-" + version + ".jar"));
        libs.add(new File(griffonHomeDir, "dist/griffon-rt-" + version + ".jar"));

        File[] files = new File(griffonHomeDir, "lib").listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getAbsolutePath().endsWith(".jar");
            }
        });
        for (File file : files) {
            libs.add(file);
        }

        if (debug) {
            for (File file : libs) {
                System.out.println(file.getAbsolutePath());
            }
        }

        int i = 0;
        URL[] urls = new URL[libs.size()];
        for (File file : libs) {
            urls[i++] = file.toURI().toURL();
        }

        String griffonHomeDirPath = griffonHomeDir.getCanonicalPath();
        System.setProperty("griffon.home", griffonHomeDir.getCanonicalPath());
        System.setProperty("program.name", "Griffon");
        System.setProperty("base.dir", ".");
        System.setProperty("groovy.starter.conf", griffonHomeDirPath + "/conf/groovy-starter.conf");
        URLClassLoader contextClassLoader = new URLClassLoader(urls);
        // Thread.currentThread().setContextClassLoader(contextClassLoader);
        Class<?> mainClass = contextClassLoader.loadClass("org.codehaus.griffon.cli.support.GriffonStarter");
        Method mainMethod = mainClass.getMethod("main", String[].class);
        mainMethod.invoke(null, new Object[]{args});
    }
}
