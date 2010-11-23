/* 
 * Copyright 2004-2010 the original author or authors.
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

package org.codehaus.griffon.plugins

import griffon.util.PluginBuildSettings
import org.springframework.core.io.Resource
import groovy.util.slurpersupport.GPathResult

/**
 * A class used mainly by the build system that encapsulates access to information
 * about the underlying plugin by delegating to the methods in GriffonPluginUtils
 * 
 * @author Graeme Rocher (Grials 1.1)
 */
public class PluginInfo extends GroovyObjectSupport implements GriffonPluginInfo {
    Resource pluginDir
    PluginBuildSettings pluginBuildSettings
    def metadata
    String name
    String version

    public PluginInfo(Resource pluginXml, PluginBuildSettings pluginBuildSettings) {
        super();
        if(pluginXml) {
            try {
                this.pluginDir = pluginXml.createRelative(".");
            }catch(e) {
                // ignore
            }            
        }
            
        this.metadata = parseMetadata(pluginXml)
        this.pluginBuildSettings = pluginBuildSettings
    }

    GPathResult parseMetadata(Resource pluginXml) {
        InputStream input 
        try {
            input = pluginXml.getInputStream()
            return new XmlSlurper().parse(input)
        }
        finally { input?.close() }
    }

    /**
     * Returns the plugin's version
     */
    String getVersion() {
        if(!version) {
            version = metadata.@version.text()
        }
        return version
    }

    /**
     * Returns the plugin's name
     */
    String getName() {
        if(!name) {
            name = metadata.@name.text()
        }
        return name
    }

    /**
     * Obtains the plugins directory
     */
    Resource getPluginDirectory() {
        return pluginDir
    }

    /**
     * Returns the location of the descriptor
     */
    Resource getDescriptor() {
        GriffonPluginUtils.getDescriptorForPlugin(pluginDir)
    }

    String getFullName() {
        "${getName()}-${getVersion()}"
    }
    
    Map getProperties() {
        return [name:getName(), version:getVersion()];
    }
    
    def getProperty(String name) {
        try {
            return super.getProperty(name)
        }
        catch(MissingPropertyException mpe) {
            return metadata[name].text()
        }
    }
}
