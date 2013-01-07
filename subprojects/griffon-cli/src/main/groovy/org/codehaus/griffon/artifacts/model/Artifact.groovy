/*
 * Copyright 2011-2013 the original author or authors.
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

package org.codehaus.griffon.artifacts.model

import griffon.util.GriffonNameUtils

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
abstract class Artifact {
    String name = ''
    String title = ''
    String description = ''
    String license = ''
    String source = ''
    String documentation = ''
    List<Author> authors = []
    List<Release> releases = []

    void setName(String name) {
        this.name = name.toLowerCase()
    }

    String getType() {
        GriffonNameUtils.getShortName(getClass()).toLowerCase()
    }

    String getCapitalizedName() {
        GriffonNameUtils.getNaturalName(name)
    }

    String getCapitalizedType() {
        GriffonNameUtils.getNaturalName(type)
    }

    String toString() {
        [
                type: type,
                name: name,
                title: title,
                license: license,
                authors: authors*.toString(),
                releases: releases,
                source: source,
                documentation: documentation
        ]
    }

    abstract Map asMap()

    abstract Map asMap(boolean includeReleases)

    abstract def toJSON()
}