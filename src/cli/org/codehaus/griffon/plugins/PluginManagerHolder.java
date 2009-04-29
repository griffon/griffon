/*
 * Copyright 2004-2006 Graeme Rocher
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
package org.codehaus.griffon.plugins;

/**
 * Manages a thread bound plugin manager instance
 * 
 * @author Graeme Rocher
 * @since 0.4
 *
 */
public abstract class PluginManagerHolder {

	private static final ThreadLocal pluginManagerHolder = new InheritableThreadLocal();
	
	/**
	 * Bind the given GriffonPluginManager instance to the current Thread
	 * @param pluginManager The GriffonPluginManager to expose
	 */
	public static void setPluginManager(GriffonPluginManager pluginManager) {
		pluginManagerHolder.set(pluginManager);
	}
	
	/**
	 * Retrieves the GriffonPluginManager bound to the current Thread
	 * @return The GriffonPluginManager or null
	 */
	public static GriffonPluginManager getPluginManager() {
		return (GriffonPluginManager)pluginManagerHolder.get();
	}
	
	/**
	 * Retrieves the bound GriffonPluginManager that resides in the current Thread
	 * @return The GriffonPluginManager
	 * @throws IllegalStateException When there is no bound GriffonPluginManager
	 */
	public static GriffonPluginManager currentPluginManager() {
		GriffonPluginManager current = getPluginManager();
		if(current == null)
			throw new IllegalStateException("No thread-bound PluginManager");
		return current;
	}
}
