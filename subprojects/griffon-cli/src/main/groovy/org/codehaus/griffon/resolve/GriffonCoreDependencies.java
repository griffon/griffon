/*
 * Copyright 2004-2014 the original author or authors.
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
package org.codehaus.griffon.resolve;

import griffon.util.BuildSettings;
import groovy.lang.Closure;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.codehaus.griffon.resolve.config.DependencyConfigurationConfigurer;
import org.codehaus.griffon.resolve.config.JarDependenciesConfigurer;
import org.codehaus.griffon.resolve.config.RepositoriesConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates information about the core dependencies of Griffon.
 * <p/>
 * This may eventually expand to expose information such as Spring version etc.
 * and be made available in the binding for user dependency declarations.
 */
public class GriffonCoreDependencies {
    public final String griffonVersion;
    public final BuildSettings buildSettings;

    public GriffonCoreDependencies(String griffonVersion, BuildSettings buildSettings) {
        this.griffonVersion = griffonVersion;
        this.buildSettings = buildSettings;
    }

    private void registerDependencies(IvyDependencyManager dependencyManager, String scope, ModuleRevisionId[] dependencies, boolean transitive) {
        for (ModuleRevisionId mrid : dependencies) {
            EnhancedDefaultDependencyDescriptor descriptor = new EnhancedDefaultDependencyDescriptor(mrid, false, false, scope);
            descriptor.setInherited(true);
            descriptor.setTransitive(transitive);
            dependencyManager.registerDependency(scope, descriptor);
        }
    }

    private void registerDependencies(IvyDependencyManager dependencyManager, String scope, ModuleRevisionId[] dependencies, String... excludes) {
        for (ModuleRevisionId mrid : dependencies) {
            EnhancedDefaultDependencyDescriptor descriptor = new EnhancedDefaultDependencyDescriptor(mrid, false, false, scope);
            descriptor.setInherited(true);
            if (excludes != null) {
                for (String exclude : excludes) {
                    descriptor.exclude(exclude);
                }
            }
            dependencyManager.registerDependency(scope, descriptor);
        }
    }

    /*
    private EnhancedDefaultDependencyDescriptor registerDependency(IvyDependencyManager dependencyManager, String scope, ModuleRevisionId mrid) {
        EnhancedDefaultDependencyDescriptor descriptor = new EnhancedDefaultDependencyDescriptor(mrid, false, false, scope);
        descriptor.setInherited(true);
        dependencyManager.registerDependency(scope, descriptor);
        return descriptor;
    }
    */

    private Map<String, String> classifier(String value) {
        Map attrs = new HashMap<String, String>(1);
        attrs.put("m:classifier", value);
        return attrs;
    }

    /**
     * Returns a closure suitable for passing to a DependencyDefinitionParser that will configure
     * the necessary core dependencies for Griffon.
     */
    @SuppressWarnings({"serial", "rawtypes"})
    public Closure createDeclaration() {
        return new Closure(this, this) {
            @SuppressWarnings("unused")
            public Object doCall() {
                DependencyConfigurationConfigurer rootDelegate = (DependencyConfigurationConfigurer) getDelegate();

                rootDelegate.log("warn");

                // Repositories

                rootDelegate.repositories(new Closure(this, GriffonCoreDependencies.this) {
                    public Object doCall() {
                        RepositoriesConfigurer repositoriesDelegate = (RepositoriesConfigurer) getDelegate();

                        // repositoriesDelegate.griffonPlugins();
                        repositoriesDelegate.griffonHome();

                        return null;
                    }
                });

                // Dependencies

                rootDelegate.dependencies(new Closure(this, GriffonCoreDependencies.this) {
                    public Object doCall() {
                        JarDependenciesConfigurer dependenciesDelegate = (JarDependenciesConfigurer) getDelegate();
                        IvyDependencyManager dependencyManager = dependenciesDelegate.getDependencyManager();

                        // dependencies needed by the Griffon build system

                        String griffonVersion = buildSettings.getGriffonVersion();
                        String springVersion = buildSettings.getSpringVersion();
                        String antVersion = buildSettings.getAntVersion();
                        String slf4jVersion = buildSettings.getSlf4jVersion();
                        String log4jVersion = buildSettings.getLog4jVersion();
                        String groovyVersion = buildSettings.getGroovyVersion();

                        ModuleRevisionId[] buildDependencies = {
                                ModuleRevisionId.newInstance("org.apache.ant", "ant", antVersion),
                                ModuleRevisionId.newInstance("org.apache.ant", "ant-launcher", antVersion),
                                ModuleRevisionId.newInstance("org.apache.ant", "ant-junit", antVersion),
                                ModuleRevisionId.newInstance("org.fusesource.jansi", "jansi", "1.9"),
                                ModuleRevisionId.newInstance("jline", "jline" ,"0.9.94"),
                                ModuleRevisionId.newInstance("commons-io", "commons-io", "2.4"),
                                ModuleRevisionId.newInstance("commons-lang", "commons-lang", "2.6"),
                                ModuleRevisionId.newInstance("commons-codec", "commons-codec", "1.6"),
                                ModuleRevisionId.newInstance("commons-collections", "commons-collections", "3.2.1"),
                                ModuleRevisionId.newInstance("commons-beanutils", "commons-beanutils", "1.8.3"),
                                ModuleRevisionId.newInstance("org.apache.httpcomponents", "httpcore", "4.1.2"),
                                ModuleRevisionId.newInstance("org.apache.httpcomponents", "httpclient", "4.1.2"),
                                ModuleRevisionId.newInstance("com.jcraft", "jsch", "0.1.48"),
                                ModuleRevisionId.newInstance("com.jcraft", "jzlib", "1.1.1"),
                                ModuleRevisionId.newInstance("org.codehaus.groovy.modules.http-builder", "http-builder", "0.6"),
                                ModuleRevisionId.newInstance("xerces", "xercesImpl", "2.9.1"),
                                ModuleRevisionId.newInstance("net.sf.ezmorph", "ezmorph", "1.0.6"),
                                ModuleRevisionId.newInstance("xml-resolver", "xml-resolver", "1.2"),
                                ModuleRevisionId.newInstance("org.codehaus.griffon", "griffon-cli", griffonVersion),
                                ModuleRevisionId.newInstance("org.codehaus.griffon", "griffon-scripts", griffonVersion),
                                ModuleRevisionId.newInstance("org.codehaus.griffon", "griffon-rt", griffonVersion),
                                ModuleRevisionId.newInstance("org.springframework", "org.springframework.core", springVersion),
                                ModuleRevisionId.newInstance("org.springframework", "org.springframework.beans", springVersion),
                                ModuleRevisionId.newInstance("org.springframework", "org.springframework.context", springVersion),
                                ModuleRevisionId.newInstance("org.springframework", "org.springframework.context.support", springVersion),
                                ModuleRevisionId.newInstance("net.sf.json-lib", "json-lib", "2.4", classifier("jdk15")),
                                ModuleRevisionId.newInstance("biz.aQute", "bndlib", "1.50.0")
                        };
                        registerDependencies(dependencyManager, "build", buildDependencies);

                        ModuleRevisionId[] loggingDependencies = {
                                ModuleRevisionId.newInstance("log4j", "log4j", log4jVersion),
                                ModuleRevisionId.newInstance("org.slf4j", "slf4j-api", slf4jVersion),
                                ModuleRevisionId.newInstance("org.slf4j", "slf4j-log4j12", slf4jVersion),
                                ModuleRevisionId.newInstance("org.slf4j", "jcl-over-slf4j", slf4jVersion),
                                ModuleRevisionId.newInstance("org.slf4j", "jul-to-slf4j", slf4jVersion)
                        };
                        registerDependencies(dependencyManager, "build", loggingDependencies, "mail", "jms", "jmxtools", "jmxri");

                        ModuleRevisionId[] groovyDependencies = {
                                ModuleRevisionId.newInstance("org.codehaus.groovy", "groovy-all", groovyVersion)
                        };
                        registerDependencies(dependencyManager, "build", groovyDependencies, false);

                        // dependencies needed when creating docs
                        ModuleRevisionId[] docDependencies = {
                                ModuleRevisionId.newInstance("org.xhtmlrenderer", "core-renderer", "R8"),
                                ModuleRevisionId.newInstance("com.lowagie", "itext", "2.0.8"),
                                ModuleRevisionId.newInstance("org.grails", "grails-docs", "2.2.4"),
                                ModuleRevisionId.newInstance("org.grails", "grails-gdoc-engine", "1.0.1"),
                                ModuleRevisionId.newInstance("org.yaml", "snakeyaml", "1.9"),
                                ModuleRevisionId.newInstance("commons-lang", "commons-lang", "2.6"),
                                ModuleRevisionId.newInstance("radeox", "radeox", "1.0-b2")
                        };
                        registerDependencies(dependencyManager, "docs", docDependencies);
                        registerDependencies(dependencyManager, "docs", loggingDependencies, "mail", "jms", "jmxtools", "jmxri");

                        // dependencies needed at compile time
                        registerDependencies(dependencyManager, "compile", groovyDependencies, false);

                        ModuleRevisionId[] griffonDependencies = {
                                ModuleRevisionId.newInstance("org.codehaus.griffon", "griffon-rt", griffonVersion),
                                ModuleRevisionId.newInstance("org.slf4j", "slf4j-api", slf4jVersion)
                        };
                        registerDependencies(dependencyManager, "compile", griffonDependencies);

                        // dependencies needed for running tests
                        ModuleRevisionId[] testDependencies = {
                                ModuleRevisionId.newInstance("junit", "junit", "4.11"),
                                ModuleRevisionId.newInstance("org.hamcrest", "hamcrest-core", "1.3")

                        };
                        registerDependencies(dependencyManager, "test", testDependencies);

                        // dependencies needed at runtime only
                        registerDependencies(dependencyManager, "runtime", loggingDependencies, "mail", "jms", "jmxtools", "jmxri");

                        return null;
                    }
                }); // end dependencies closure

                return null;
            }
        }; // end root closure
    }
}
