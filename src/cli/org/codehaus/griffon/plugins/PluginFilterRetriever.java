/*
 * Copyright 2008-2010 the original author or authors.
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

import java.util.Properties;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Implements mechanism for figuring out what <code>PluginFilter</code>
 * implementation to use based on a set of provided configuration properties
 * @author Phil Zoio
 */
public class PluginFilterRetriever {

	public PluginFilter getPluginFilter(Properties properties) {

		Assert.notNull(properties);

		String includes = properties.getProperty("plugin.includes");
		String excludes = properties.getProperty("plugin.excludes");

		return getPluginFilter(includes, excludes);
	}

	PluginFilter getPluginFilter(String includes, String excludes) {
		PluginFilter pluginFilter = null;

		if (includes != null) {
			String[] includesArray = StringUtils
					.commaDelimitedListToStringArray(includes);
			pluginFilter = new IncludingPluginFilter(includesArray);
		} else if (excludes != null) {
			String[] excludesArray = StringUtils
					.commaDelimitedListToStringArray(excludes);
			pluginFilter = new ExcludingPluginFilter(excludesArray);
		} else {
			pluginFilter = new IdentityPluginFilter();
		}
		return pluginFilter;
	}
}
