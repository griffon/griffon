/*
 * Copyright 2008 the original author or authors.
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
package griffon.util

import griffon.builder.UberBuilder
import groovy.util.FactoryBuilderSupport

/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Jul 29, 2008
 * Time: 5:58:16 PM
 */
class CompositeBuilderHelper {

    public static FactoryBuilderSupport createBuilder(IGriffonApplication app, Map targets) {
        UberBuilder uberBuilder = new UberBuilder()
        uberBuilder.setProperty('app', app)

        for (prefix in app.builderConfig) {
            String prefixName = prefix.key
            if (prefixName == "root") prefixName = ""
            for (builderClassName in prefix.value) {
                Class builderClass = Class.forName(builderClassName.key) //FIXME get correct classloader
                FactoryBuilderSupport localBuilder = uberBuilder.uberInit(prefixName, builderClass)
                for (partialTarget in builderClassName.value) {
                    if (partialTarget == 'view') {
                        // this needs special handling, skip it for now
                        continue
                    }
                    MetaClass mc = targets[partialTarget.key]?.getMetaClass()
                    if (!mc) continue
                    for (groupName in partialTarget.value) {
                        if (groupName == "*") {
                            //FIXME handle add-all
                            continue
                        }
                        def factories = localBuilder.getLocalFactories()
                        def methods = localBuilder.getLocalExplicitMethods()
                        def properties = localBuilder.getLocalExplicitProperties()
                        def groupItems = localBuilder.getRegistrationGroupItems(groupName)
                        if (!groupItems) {
                            continue
                        }
                        for (itemName in groupItems) {
                            def resolvedName = "${prefixName}${itemName}"
                            if (methods.containsKey(itemName)) {
                                mc."$resolvedName" = methods[itemName]
                            } else if (properties.containsKey(itemName)) {
                                Closure[] accessors = properties[itemName];
                                String beanName
                                if (itemName.length() > 1) {
                                    beanName = itemName[0].toUpperCase() + itemName.substring(1)
                                } else {
                                    beanName = itemName[0].toUpperCase()
                                }
                                if (accessors[0]) {
                                    mc."get$beanName" = accessors[0]
                                }
                                if (accessors[1]) {
                                    mc."set$beanName" = accessors[1]
                                }
                            } else if (factories.containsKey(itemName)) {
                                mc."${resolvedName}" = {Object... args->uberBuilder."$resolvedName"(*args)}
                            }
                        }
                    }
                }
            }
        }
        return uberBuilder
    }

}
