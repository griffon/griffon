/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.core.util.groovy

import griffon.annotations.core.Nonnull
import griffon.core.ApplicationClassLoader
import griffon.core.env.Environment
import griffon.core.env.GriffonEnvironment
import griffon.core.env.Metadata
import griffon.core.util.GriffonApplicationUtils
import griffon.util.CollectionUtils
import org.codehaus.groovy.runtime.InvokerHelper

import javax.inject.Inject

import static griffon.util.StringUtils.isBlank
import static griffon.util.StringUtils.isNotBlank

/**
 * Updated version of {@code groovy.util.ConfigSlurper}.<br/>
 * New features include:
 * <ul>
 *     <li>Ability to specify multiple conditional blocks, not just "environments".</li>
 * </ul>
 *
 * @author Graeme Rocher (Groovy 1.5)
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
class ConfigReader {
    private static final ENVIRONMENTS_METHOD = 'environments'
    GroovyClassLoader classLoader
    private Map bindingVars = [:]

    private Stack<String> currentConditionalBlock = new Stack<String>()
    private final Map<String, String> conditionValues = [:]
    private final Stack<Map<String, ConfigObject>> conditionalBlocks = new Stack<Map<String, ConfigObject>>()

    static class Provider implements javax.inject.Provider<ConfigReader> {
        @Inject private ApplicationClassLoader applicationClassLoader
        @Inject private Metadata metadata
        @Inject private Environment environment

        @Override
        ConfigReader get() {
            ConfigReader configReader = new ConfigReader(applicationClassLoader)
            configReader.setBinding(CollectionUtils.map()
                .e("userHome", System.getProperty("user.home"))
                .e("appName", metadata.getApplicationName())
                .e("appVersion", metadata.getApplicationVersion())
                .e("griffonVersion", GriffonEnvironment.getGriffonVersion()))
            configReader.registerConditionalBlock("environments", environment.getName())
            configReader.registerConditionalBlock("projects", metadata.getApplicationName())
            configReader.registerConditionalBlock("platforms", GriffonApplicationUtils.getPlatform())
            configReader
        }
    }

    @Inject
    ConfigReader(@Nonnull ApplicationClassLoader applicationClassLoader) {
        classLoader = new GroovyClassLoader(applicationClassLoader.get())
    }

    void registerConditionalBlock(String blockName, String blockValue) {
        if (isNotBlank(blockName)) {
            if (isBlank(blockValue)) {
                conditionValues.remove(blockName)
            } else {
                conditionValues[blockName] = blockValue
            }
        }
    }

    Map<String, String> getConditionalBlockValues() {
        Collections.unmodifiableMap(conditionValues)
    }

    String getEnvironment() {
        return conditionValues[ENVIRONMENTS_METHOD]
    }

    void setEnvironment(String environment) {
        conditionValues[ENVIRONMENTS_METHOD] = environment
    }

    void setBinding(Map vars) {
        this.bindingVars = vars
    }

    /**
     * Parses a ConfigObject instances from an instance of java.util.Properties
     * @param The java.util.Properties instance
     */
    ConfigObject parse(Properties properties) {
        ConfigObject config = new ConfigObject()
        for (key in properties.keySet()) {
            def tokens = key.split(/\./)

            def current = config
            def last
            def lastToken
            def foundBase = false
            for (token in tokens) {
                if (foundBase) {
                    // handle not properly nested tokens by ignoring
                    // hierarchy below this point
                    lastToken += "." + token
                    current = last
                } else {
                    last = current
                    lastToken = token
                    current = current."${token}"
                    if (!(current instanceof ConfigObject)) foundBase = true
                }
            }

            if (current instanceof ConfigObject) {
                if (last[lastToken]) {
                    def flattened = last.flatten()
                    last.clear()
                    flattened.each { k2, v2 -> last[k2] = v2 }
                    last[lastToken] = properties.get(key)
                } else {
                    last[lastToken] = properties.get(key)
                }
            }
            current = config
        }
        return config
    }
    /**
     * Parse the given script as a string and return the configuration object
     *
     * @see ConfigReader#parse(groovy.lang.Script)
     */
    ConfigObject parse(String script) {
        return parse(classLoader.parseClass(script))
    }

    /**
     * Create a new instance of the given script class and parse a configuration object from it
     *
     * @see ConfigReader#parse(groovy.lang.Script)
     */
    ConfigObject parse(Class<? extends Script> scriptClass) {
        return parse(scriptClass.newInstance())
    }

    /**
     * Parse the given script into a configuration object (a Map)
     * @param script The script to parse
     * @return A Map of maps that can be navigating with dot de-referencing syntax to obtain configuration entries
     */
    ConfigObject parse(Script script) {
        return parse(script, null)
    }

    /**
     * Parses a Script represented by the given URL into a ConfigObject
     *
     * @param scriptLocation The location of the script to parse
     * @return The ConfigObject instance
     */
    ConfigObject parse(URL scriptLocation) {
        return parse(classLoader.parseClass(scriptLocation.text).newInstance(), scriptLocation)
    }

    /**
     * Parses the passed groovy.lang.Script instance using the second argument to allow the ConfigObject
     * to retain an reference to the original location other Groovy script
     *
     * @param script The groovy.lang.Script instance
     * @param location The original location of the Script as a URL
     * @return The ConfigObject instance
     */
    ConfigObject parse(Script script, URL location) {
        def config = location ? new ConfigObject(location) : new ConfigObject()
        GroovySystem.metaClassRegistry.removeMetaClass(script.class)
        def mc = script.class.metaClass
        def prefix = ""
        Stack<Map<String, Object>> stack = new Stack<>()
        stack << [config: config, scope: [:]]
        def pushStack = { co ->
            stack << [config: co, scope: stack.peek().scope.clone()]
        }
        def assignName = { name, co ->
            def current = stack.peek()
            /*
            def cfg = current.config
            if (cfg instanceof ConfigObject) {
                String[] keys = name.split(/\./)
                for (int i = 0; i < keys.length - 1; i++) {
                    String key = keys[i]
                    if (!cfg.containsKey(key)) {
                        cfg[key] = new ConfigObject()
                    }
                    cfg = cfg.get(key)
                }
                name = keys[keys.length - 1]
            }
            cfg[name] = co
            */
            current.config[name] = co
            current.scope[name] = co
        }
        mc.getProperty = { String name ->
            def current = stack.peek()
            def result
            if (current.config.get(name)) {
                result = current.config.get(name)
            } else if (current.scope[name]) {
                result = current.scope[name]
            } else {
                try {
                    result = InvokerHelper.getProperty(this, name)
                } catch (GroovyRuntimeException e) {
                    result = new ConfigObject()
                    assignName.call(name, result)
                }
            }
            result
        }

        ConfigObject overrides = new ConfigObject()
        mc.invokeMethod = { String name, args ->
            def result
            if (args.length == 1 && args[0] instanceof Closure) {
                if (name in conditionValues.keySet()) {
                    try {
                        currentConditionalBlock.push(name)
                        conditionalBlocks.push([:])
                        args[0].call()
                    } finally {
                        currentConditionalBlock.pop()
                        for (entry in conditionalBlocks.pop().entrySet()) {
                            def c = stack.peek().config
                            (c != config ? c : overrides).merge(entry.value)
                        }
                    }
                } else if (currentConditionalBlock.size() > 0) {
                    String conditionalBlockKey = currentConditionalBlock.peek()
                    if (name == conditionValues[conditionalBlockKey]) {
                        def co = new ConfigObject()
                        conditionalBlocks.peek()[conditionalBlockKey] = co

                        pushStack.call(co)
                        try {
                            currentConditionalBlock.pop()
                            args[0].call()
                        } finally {
                            currentConditionalBlock.push(conditionalBlockKey)
                        }
                        stack.pop()
                    }
                } else {
                    def co
                    if (stack.peek().config.get(name) instanceof ConfigObject) {
                        co = stack.peek().config.get(name)
                    } else {
                        co = new ConfigObject()
                    }

                    assignName.call(name, co)
                    pushStack.call(co)
                    args[0].call()
                    stack.pop()
                }
            } else if (args.length == 2 && args[1] instanceof Closure) {
                try {
                    prefix = name + '.'
                    assignName.call(name, args[0])
                    args[1].call()
                } finally { prefix = "" }
            } else {
                MetaMethod mm = mc.getMetaMethod(name, args)
                if (mm) {
                    result = mm.invoke(delegate, args)
                } else {
                    throw new MissingMethodException(name, getClass(), args)
                }
            }
            result
        }
        script.metaClass = mc

        def setProperty = { String name, value ->
            assignName.call(prefix + name, value)
        }
        def binding = new ConfigBinding(setProperty)
        if (this.bindingVars) {
            binding.getVariables().putAll(this.bindingVars)
        }
        script.binding = binding

        script.run()

        config.merge(overrides)

        return config
    }
}