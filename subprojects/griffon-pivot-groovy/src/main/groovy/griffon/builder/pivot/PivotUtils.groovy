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
package griffon.builder.pivot

import org.apache.pivot.collections.Dictionary
import org.apache.pivot.collections.List as PivotList
import org.apache.pivot.collections.Map as PivotMap
import org.apache.pivot.collections.Sequence
import org.apache.pivot.collections.Set as PivotSet
import org.apache.pivot.util.ListenerList
import org.apache.pivot.wtk.Component
import org.apache.pivot.wtk.Form
import org.apache.pivot.wtk.TabPane
import org.apache.pivot.wtk.text.Span

import static griffon.util.GriffonNameUtils.getSetterName

/**
 * @author Andres Almiray
 */
final class PivotUtils {
    private PivotUtils() {
        // prevent instantiation
    }

    private static final String ENHANCED = "_ENHANCED_METACLASS_"

    static boolean hasBeenEnhanced(Class klass) {
        MetaClassRegistry mcr = GroovySystem.metaClassRegistry
        MetaClass mc = mcr.getMetaClass(klass)
        if (!(mc instanceof ExpandoMetaClass)) return false
        return mc.hasMetaProperty(ENHANCED)
    }

    static void enhance(Class klass, Map enhancedMethods) {
        MetaClassRegistry mcr = GroovySystem.metaClassRegistry
        MetaClass mc = mcr.getMetaClass(klass)
        boolean init = false
        if (!(mc instanceof ExpandoMetaClass) ||
            (mc instanceof ExpandoMetaClass && !mc.isModified())) {
            mcr.removeMetaClass klass
            mc = new ExpandoMetaClass(klass)
            init = true
        }
        // if mc is an EMC that was initialized previously
        // with additional methods/properties and it does
        // not allow modifications after init, then the next
        // block will throw an exception
        enhancedMethods.each { k, v ->
            if (mc.getMetaMethod(k) == null) {
                mc.registerInstanceMethod(k, v)
            }
        }
        mc.registerBeanProperty(ENHANCED, true)
        if (init) {
            mc.initialize()
            mcr.setMetaClass(klass, mc)
        }
    }

    static enhanceClasses() {
        enhance(Sequence, [
            iterator: {-> new FixedIterator(delegate, true) },
            size: {-> delegate.getLength() },
            getAt: { i -> delegate.get(i) },
            putAt: { i, e -> delegate.update(i, e) }
        ])
        enhance(ListenerList, [
            leftShift: { e -> delegate.add(e) }
        ])
        enhance(PivotList, [
            leftShift: { e -> delegate.add(e) }
        ])
        enhance(PivotSet, [
            leftShift: { e -> delegate.add(e) },
            size: {-> delegate.getCount() }
        ])
        enhance(Dictionary, [
            putAt: { k, v -> delegate.put(k, v) },
            getAt: { k -> delegate.get(k) }
        ])
        enhance(PivotMap, [
            size: {-> delegate.getCount() }
        ])
        enhance(Span, [
            asRange: {-> new IntRange(delegate.start, delegate.end) },
            size: {-> delegate.getLength() }
        ])
        enhance(Component, [
            setFormLabel: { label -> Form.setLabel(delegate, label ? label.toString() : null) },
            setFormFlag: { flag -> flag ? Form.setFlag(delegate, flag) : Form.setFlag(delegate, (Form.Flag) null) },
            setTabLabel: { label -> TabPane.setLabel(delegate, label ? label.toString() : null) },
        ])
    }

    static void setBeanProperty(String propertyName, value, bean) {
        try {
            // use setter method instead of property access
            // workaround to multiple property setters & single property getter
            String setter = getSetterName(propertyName)
            bean."$setter"(value)
        } catch (MissingPropertyException mpe) {
            bean.styles.put(propertyName, value)
        }
    }
}
