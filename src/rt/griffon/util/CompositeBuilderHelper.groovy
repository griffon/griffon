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
package griffon.util

import griffon.builder.UberBuilder
import griffon.core.GriffonApplication

/**
 * Created by IntelliJ IDEA.
 * @author Danno.Ferrin
 * Date: Jul 29, 2008
 * Time: 5:58:16 PM
 */
class CompositeBuilderHelper {

    public static FactoryBuilderSupport createBuilder(GriffonApplication app, Map targets) {
        UberBuilder uberBuilder = new UberBuilder()
        uberBuilder.setProperty('app', app)

        AddonHelper.handleAddonsForBuilders(app, uberBuilder, targets)

        for (node in app.builderConfig) {
            String nodeName = node.key
            if (nodeName == "root") nodeName = ""
            node.value.each { builder -> handleLocalBuilder(uberBuilder, targets, nodeName, builder) }
        }
        return uberBuilder
    }

    private static handleLocalBuilder(UberBuilder uberBuilder, Map targets, String prefixName, builderClassName) {
        Class builderClass = Class.forName(builderClassName.key) //FIXME get correct classloader
        if (!FactoryBuilderSupport.isAssignableFrom(builderClass)) {
            return;
        }
        FactoryBuilderSupport localBuilder = uberBuilder.uberInit(prefixName, builderClass)
        for (partialTarget in builderClassName.value) {
            if (partialTarget == 'view') {
                // this needs special handling, skip it for now
                continue
            }
            MetaClass mc = targets[partialTarget.key]?.getMetaClass()
            if (!mc) continue
            for (String injectionName in partialTarget.value) {
                if (injectionName == "*") {
                    //FIXME handle add-all
                    continue
                }
                def factories = localBuilder.getLocalFactories()
                def methods = localBuilder.getLocalExplicitMethods()
                def props = localBuilder.getLocalExplicitProperties()

                Closure processInjection = {String injectedName ->
                    def resolvedName = "${prefixName}${injectedName}"
                    if (methods.containsKey(injectedName)) {
                        mc."$resolvedName" = methods[injectedName]
                    } else if (props.containsKey(injectedName)) {
                        Closure[] accessors = props[injectedName]
                        String beanName
                        if (injectedName.length() > 1) {
                            beanName = injectedName[0].toUpperCase() + injectedName.substring(1)
                        } else {
                            beanName = injectedName[0].toUpperCase()
                        }
                        if (accessors[0]) {
                            mc."get$beanName" = accessors[0]
                        }
                        if (accessors[1]) {
                            mc."set$beanName" = accessors[1]
                        }
                    } else if (factories.containsKey(injectedName)) {
                        mc."${resolvedName}" = {Object ... args -> uberBuilder."$resolvedName"(* args)}
                    }
                }

                def groupItems = localBuilder.getRegistrationGroupItems(injectionName)
                if (groupItems) {
                    groupItems.each processInjection
                } else {
                    processInjection(injectionName)
                }
            }
        }
    }
}
