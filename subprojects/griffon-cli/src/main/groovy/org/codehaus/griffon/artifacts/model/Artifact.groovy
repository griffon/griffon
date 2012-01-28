package org.codehaus.griffon.artifacts.model

import griffon.util.GriffonNameUtils

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