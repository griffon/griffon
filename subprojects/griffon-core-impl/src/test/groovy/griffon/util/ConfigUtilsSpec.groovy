/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.util

import griffon.annotations.core.Nonnull
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static griffon.util.ExpandableResourceBundle.wrapResourceBundle
import static java.util.Arrays.asList

@Unroll
class ConfigUtilsSpec extends Specification {
    @Shared
    private static Map<String, Object> map = [
        single: 'single',
        'key.string': 'string',
        'key.number': 'number'
    ]

    @Shared
    private static Map<String, Object> nestingMap = [
        single: 'single',
        key: [
            string: 'string',
            number: 'number'
        ]
    ]

    @Shared
    private static ResourceBundle mapResourceBundle = new MapResourceBundle()

    @Shared
    private static ResourceBundle expandableResourceBundle = wrapResourceBundle(new MapResourceBundle())

    def 'Calling getConfigValue() on #source.getClass() with #key gives #value'() {
        expect:
        value == ConfigUtils.getConfigValue(source, key)

        where:
        source                   | key          | value
        map                      | 'single'     | 'single'
        map                      | 'key.string' | 'string'
        map                      | 'key.number' | 'number'
        nestingMap               | 'single'     | 'single'
        nestingMap               | 'key.string' | 'string'
        nestingMap               | 'key.number' | 'number'
        nestingMap               | 'key'        | [string: 'string', number: 'number']
        mapResourceBundle        | 'single'     | 'single'
        mapResourceBundle        | 'key.string' | 'string'
        mapResourceBundle        | 'key.number' | 'number'
        expandableResourceBundle | 'single'     | 'single'
        expandableResourceBundle | 'key.string' | 'string'
        expandableResourceBundle | 'key.number' | 'number'
        expandableResourceBundle | 'key'        | [string: 'string', number: 'number']
    }


    def 'Calling isValueDefined() on #source.getClass() with #key gives #value'() {
        expect:
        value == ConfigUtils.isValueDefined(source, key)

        where:
        source                   | key          | value
        map                      | 'single'     | true
        map                      | 'key.string' | true
        map                      | 'key.number' | true
        map                      | 'key'        | false
        nestingMap               | 'single'     | true
        nestingMap               | 'key.string' | true
        nestingMap               | 'key.number' | true
        nestingMap               | 'key'        | true
        mapResourceBundle        | 'single'     | true
        mapResourceBundle        | 'key.string' | true
        mapResourceBundle        | 'key.number' | true
        mapResourceBundle        | 'key'        | false
        expandableResourceBundle | 'single'     | true
        expandableResourceBundle | 'key.string' | true
        expandableResourceBundle | 'key.number' | true
        expandableResourceBundle | 'key'        | true
    }

    def 'Calling containsKey() on #source.getClass() with #key gives #value'() {
        expect:
        value == ConfigUtils.containsKey(source, key)

        where:
        source                   | key          | value
        map                      | 'single'     | true
        map                      | 'key.string' | true
        map                      | 'key.number' | true
        map                      | 'key'        | false
        nestingMap               | 'single'     | true
        nestingMap               | 'key.string' | true
        nestingMap               | 'key.number' | true
        nestingMap               | 'key'        | true
        mapResourceBundle        | 'single'     | true
        mapResourceBundle        | 'key.string' | true
        mapResourceBundle        | 'key.number' | true
        mapResourceBundle        | 'key'        | false
        expandableResourceBundle | 'single'     | true
        expandableResourceBundle | 'key.string' | true
        expandableResourceBundle | 'key.number' | true
        expandableResourceBundle | 'key'        | true
    }

    def 'Read Java based configuration'() {
        expect:
        value == ConfigUtils.getConfigValue(wrapResourceBundle(source), key)

        where:
        source                 | key                           | value
        new NestedJavaConfig() | 'application.title'           | 'Swing + Java'
        new NestedJavaConfig() | 'application.startupGroups'   | ['sample']
        new NestedJavaConfig() | 'application.autoShutdown'    | true
        new NestedJavaConfig() | 'mvcGroups.sample.model'      | 'sample.SampleModel'
        new NestedJavaConfig() | 'mvcGroups.sample.view'       | 'sample.SampleView'
        new NestedJavaConfig() | 'mvcGroups.sample.controller' | 'sample.SampleController'
        new FlatJavaConfig()   | 'application.title'           | 'Swing + Java'
        new FlatJavaConfig()   | 'application.startupGroups'   | ['sample']
        new FlatJavaConfig()   | 'application.autoShutdown'    | true
        new FlatJavaConfig()   | 'mvcGroups.sample.model'      | 'sample.SampleModel'
        new FlatJavaConfig()   | 'mvcGroups.sample.view'       | 'sample.SampleView'
        new FlatJavaConfig()   | 'mvcGroups.sample.controller' | 'sample.SampleController'
    }
}

class NestedJavaConfig extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        CollectionUtils.map(entries)
            .e("application", CollectionUtils.map()
                .e("title", "Swing + Java")
                .e("startupGroups", asList("sample"))
                .e("autoShutdown", true))
            .e("mvcGroups", CollectionUtils.map()
                .e("sample", CollectionUtils.map()
                    .e("model", "sample.SampleModel")
                    .e("view", "sample.SampleView")
                    .e("controller", "sample.SampleController"))
            )
    }
}

class FlatJavaConfig extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        CollectionUtils.map(entries)
            .e("application.title", "Swing + Java")
            .e("application.startupGroups", asList("sample"))
            .e("application.autoShutdown", true)
            .e("mvcGroups.sample.model", "sample.SampleModel")
            .e("mvcGroups.sample.view", "sample.SampleView")
            .e("mvcGroups.sample.controller", "sample.SampleController")
    }
}