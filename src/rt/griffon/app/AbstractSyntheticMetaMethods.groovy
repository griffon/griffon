/*
 * Copyright 2007-2008 the original author or authors.
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
package griffon.app

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
 */
class AbstractSyntheticMetaMethods {
    private static final String ENHANCED = "_ENHANCED_METACLASS_"

    static boolean hasBeenEnhanced(Class klass) {
        MetaClassRegistry mcr = GroovySystem.metaClassRegistry
        MetaClass mc = mcr.getMetaClass(klass)
        if( !(mc instanceof ExpandoMetaClass) ) return false
        return mc.hasMetaProperty(ENHANCED)
    }

    static void enhance(Class klass, Map enhancedMethods) {
        MetaClassRegistry mcr = GroovySystem.metaClassRegistry
        MetaClass mc = mcr.getMetaClass(klass)
        boolean init = false
        if( !(mc instanceof ExpandoMetaClass) ||
             (mc instanceof ExpandoMetaClass && !mc.isModified()) ) {
            mcr.removeMetaClass klass
            mc = new ExpandoMetaClass(klass)
            init = true
        }
        // if mc is an EMC that was initialized previously
        // with additional methods/properties and it does
        // not allow modifications after init, then the next
        // block will throw an exception
        enhancedMethods.each {k, v ->
            if (mc.getMetaMethod(k) == null) {
                mc.registerInstanceMethod(k, v)
            }
        }
        mc.registerBeanProperty(ENHANCED,true)
        if (init) {
            mc.initialize()
            mcr.setMetaClass(klass, mc)
        }
    }
}