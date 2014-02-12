/*
 * Copyright 2011-2014 the original author or authors.
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

package org.codehaus.griffon.artifacts.maven

import griffon.util.ConfigReader
import groovy.transform.Canonical

import java.util.regex.Matcher
import java.util.regex.Pattern

import static griffon.util.ConfigUtils.createConfigReader
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 * @since 1.4.0
 */
class PluginDependenciesParser {
    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("([a-zA-Z0-9\\-/\\._+=]*?):([a-zA-Z0-9\\-/\\._+=]+?):([a-zA-Z0-9\\-/\\.,\\]\\[\\(\\)_+=]+)(:([a-zA-Z0-9\\-/\\.,\\]\\[\\(\\)_+=]+))?")

    final Map<String, List<Dependency>> dependencies = [
        runtime: [],
        compile: [],
        build: [],
        test: []
    ]

    private PluginDependenciesParser() {
        this.dependencies = dependencies
    }

    @Canonical
    static class Dependency {
        String groupId
        String artifactId
        String version
        String classifier
    }

    static Map<String, List<Dependency>> traversePluginDependencies(File dependenciesDescriptor) {
        GroovyClassLoader gcl = new GroovyClassLoader(PluginDependenciesParser.class.classLoader)
        Script dependenciesScript = gcl.parseClass(dependenciesDescriptor).newInstance()

        ConfigReader configReader = createConfigReader()
        def pluginConfig = configReader.parse(dependenciesScript)
        def pluginDependencyConfig = pluginConfig.griffon.project.dependency.resolution
        PluginDependenciesParser pluginDependenciesParser = new PluginDependenciesParser()
        if (pluginDependencyConfig instanceof Closure) {
            pluginDependencyConfig.delegate = pluginDependenciesParser
            pluginDependencyConfig.resolveStrategy = Closure.DELEGATE_FIRST
            pluginDependencyConfig()
        }
        return pluginDependenciesParser.dependencies
    }

    void dependencies(Closure cls) {
        cls.delegate = this
        cls.resolveStrategy = Closure.DELEGATE_FIRST
        cls()
    }

    Object methodMissing(String name, Object args) {
        if (args == null) {
            return null
        }

        List<Object> argsList = Arrays.asList((Object[]) args)
        if (argsList.size() == 0) {
            return null
        }

        if (isOnlyStrings(argsList)) {
            addDependencyStrings(name, argsList, null, null)
        } else if (isProperties(argsList)) {
            addDependencyMaps(name, argsList, null)
        } else if (isStringsAndConfigurer(argsList)) {
            addDependencyStrings(name, argsList.subList(0, argsList.size() - 1), null, (Closure<?>) argsList.get(argsList.size() - 1));
        } else if (isPropertiesAndConfigurer(argsList)) {
            addDependencyMaps(name, argsList.subList(0, argsList.size() - 1), (Closure<?>) argsList.get(argsList.size() - 1));
        } else if (isStringsAndProperties(argsList)) {
            addDependencyStrings(name, argsList.subList(0, argsList.size() - 1), (Map<Object, Object>) argsList.get(argsList.size() - 1), null);
        }

        return null;
    }

    private static class ExportHolder {
        boolean export = true;
    }

    private void addDependencyStrings(String scope, List<Object> dependencies, Map<Object, Object> overrides, Closure<?> configurer) {
        for (Object dependency : dependencies) {
            Map<Object, Object> dependencyProperties = extractDependencyProperties(scope, dependency.toString());
            if (dependencyProperties == null) {
                continue;
            }

            if (overrides != null) {
                for (Map.Entry<Object, Object> override : overrides.entrySet()) {
                    dependencyProperties.put(override.getKey().toString(), override.getValue().toString());
                }
            }

            addDependency(scope, dependencyProperties, configurer);
        }
    }

    private void addDependencyMaps(String scope, List<Object> dependencies, Closure<?> configurer) {
        for (Object dependency : dependencies) {
            addDependency(scope, (Map<Object, Object>) dependency, configurer);
        }
    }

    private String nullSafeToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    private void addDependency(String scope, Map<Object, Object> dependency, Closure<?> configurer) {
        if (configurer != null) {
            ExportHolder eh = new ExportHolder()
            configurer.delegate = eh
            configurer.resolveStrategy = Closure.DELEGATE_FIRST
            configurer()
            if (!eh.export) return;
        }

        List list = dependencies.get(scope, [])
        list << new Dependency(
            groupId: nullSafeToString(dependency.get("group")),
            artifactId: nullSafeToString(dependency.get("name")),
            version: nullSafeToString(dependency.get("version")),
            classifier: nullSafeToString(dependency.get("classifier"))
        )
    }

    private Map<Object, Object> extractDependencyProperties(String scope, String dependency) {
        Matcher matcher = DEPENDENCY_PATTERN.matcher(dependency);
        if (matcher.matches()) {
            Map<Object, Object> properties = new HashMap<Object, Object>(3);
            properties.put("name", matcher.group(2));
            properties.put("group", matcher.group(1));
            properties.put("version", matcher.group(3));
            String classifier = matcher.group(4);
            if (!isBlank(classifier) && classifier.startsWith(":")) {
                classifier = classifier.substring(1);
            }
            properties.put("classifier", classifier);
            return properties;
        }
        return null;
    }

    private boolean isOnlyStrings(List<Object> args) {
        for (Object arg : args) {
            if (!(arg instanceof CharSequence)) {
                return false;
            }
        }
        return true;
    }

    private boolean isStringsAndConfigurer(List<Object> args) {
        if (args.size() == 1) {
            return false;
        }
        return isOnlyStrings(args.subList(0, args.size() - 1)) && args.get(args.size() - 1) instanceof Closure;
    }

    private boolean isStringsAndProperties(List<Object> args) {
        if (args.size() == 1) {
            return false;
        }
        return isOnlyStrings(args.subList(0, args.size() - 1)) && args.get(args.size() - 1) instanceof Map;
    }

    private boolean isProperties(List<Object> args) {
        for (Object arg : args) {
            if (!(arg instanceof Map)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPropertiesAndConfigurer(List<Object> args) {
        if (args.size() == 1) {
            return false;
        }
        return isProperties(args.subList(0, args.size() - 1)) && args.get(args.size() - 1) instanceof Closure;
    }
}
