/*
 * Copyright 2012 the original author or authors.
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

import org.codehaus.griffon.artifacts.model.Release;
import org.springframework.core.io.Resource;

/**
 * @author Andres Almiray
 * @since 0.9.6
 */
public class PluginInfo {
    private final String name;
    private final String version;
    private final Resource directory;
    private final Release release;

    public PluginInfo(String name, Resource directory, Release release) {
        this.name = name;
        this.version = release.getVersion();
        this.directory = directory;
        this.release = release;
    }

    public String getName() {
        return name;
    }

    public Resource getDirectory() {
        return directory;
    }

    public Release getRelease() {
        return release;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginInfo that = (PluginInfo) o;

        if (!name.equals(that.name)) return false;
        if (!version.equals(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PluginInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", directory=" + directory +
                ", release=" + release +
                '}';
    }
}
