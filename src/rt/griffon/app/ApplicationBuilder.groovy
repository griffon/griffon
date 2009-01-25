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
package griffon.app

/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Sep 4, 2008
 * Time: 10:22:22 AM
 */
class ApplicationBuilder extends FactoryBuilderSupport {

    public ApplicationBuilder(boolean init = true) {
        super(init)
    }

    public void registerVisuals() {
        registerFactory 'application', new ApplicationFactory()
        addAttributeDelegate(ApplicationBuilder.&clientPropertyAttributeDelegate)
    }

    public static clientPropertyAttributeDelegate(def builder, def node, def attributes) {
        def clientPropertyMap = attributes.remove("clientProperty")
        clientPropertyMap.each { key, value ->
           node.putClientProperty key, value
        }
        attributes.findAll { it.key =~ /clientProperty(\w)/ }.each { key, value ->
           attributes.remove(key)
           node.putClientProperty(key - "clientProperty", value)
        }
    }
}