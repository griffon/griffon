/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.javafx.beans.binding

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.embed.swing.JFXPanel
import spock.lang.Specification
import spock.lang.Unroll

import static griffon.javafx.beans.binding.UIThreadAwareBindings.uiThreadAwareChangeListener
import static griffon.javafx.beans.binding.UIThreadAwareBindings.uiThreadAwareInvalidationListener
import static java.util.concurrent.TimeUnit.SECONDS
import static javafx.collections.FXCollections.observableArrayList
import static javafx.collections.FXCollections.observableHashMap
import static javafx.collections.FXCollections.observableMap
import static javafx.collections.FXCollections.observableSet
import static org.awaitility.Awaitility.await

/**
 * @author Andres Almiray
 */
@Unroll
class UIThreadAwareBindingsSpec extends Specification {
    static {
        new JFXPanel()
    }

    void "Verify update is triggered inside UI thread for #type"() {
        given:
        source.set(value1)

        ValueHolder cvh = new ValueHolder()
        ValueHolder ivh = new ValueHolder()

        def property = UIThreadAwareBindings."uiThreadAware${type}"(source)
        ChangeListener changeListener = { v, o, n ->
            cvh.value = n
            cvh.insideUIThread = Platform.isFxApplicationThread()
        }
        InvalidationListener invalidationListener = { o ->
            ivh.insideUIThread = Platform.isFxApplicationThread()
        }

        property.addListener(changeListener)
        property.addListener(invalidationListener)

        expect:
        property.getValue() == value1

        when: 'trigger change outside UI thread'
        setValue(type, source, property, value2)
        await().timeout(2, SECONDS).until { cvh.value != null }

        then:
        value2 == cvh.value
        cvh.insideUIThread
        ivh.insideUIThread

        when: 'trigger change inside UI thread'
        // reset
        ivh.insideUIThread = false
        cvh.insideUIThread = false
        cvh.value = null

        Platform.runLater { setValue(type, source, property, value1) }
        await().timeout(2, SECONDS).until { cvh.value != null }

        then:
        value1 == cvh.value
        cvh.insideUIThread
        ivh.insideUIThread

        when: 'register UI aware listeners'
        // reset
        ivh.insideUIThread = false
        cvh.insideUIThread = false
        cvh.value = null

        property.removeListener(changeListener)
        property.removeListener(invalidationListener)
        changeListener = uiThreadAwareChangeListener(changeListener)
        invalidationListener = uiThreadAwareInvalidationListener(invalidationListener)
        property.addListener(changeListener)
        property.addListener(invalidationListener)

        setValue(type, source, property, value2)
        await().timeout(2, SECONDS).until { cvh.value != null }

        then:
        value2 == cvh.value
        cvh.insideUIThread
        ivh.insideUIThread

        when:
        property.bind(target)

        then:
        target.value == property.value

        when:
        // reset
        ivh.insideUIThread = false
        cvh.insideUIThread = false
        cvh.value = null

        target.set(value1)
        await().timeout(2, SECONDS).until { property.value != value2 }

        then:
        target.value == property.value
        // target.value == cvh.value
        // cvh.insideUIThread
        // ivh.insideUIThread

        cleanup:
        property?.removeListener(changeListener)
        property?.removeListener(invalidationListener)

        where:
        type              | source                      | target                      | value1                | value2
        'BooleanProperty' | new SimpleBooleanProperty() | new SimpleBooleanProperty() | false                 | true
        'IntegerProperty' | new SimpleIntegerProperty() | new SimpleIntegerProperty() | 0                     | 1
        'LongProperty'    | new SimpleLongProperty()    | new SimpleLongProperty()    | 0l                    | 1l
        'FloatProperty'   | new SimpleFloatProperty()   | new SimpleFloatProperty()   | 0f                    | 1f
        'DoubleProperty'  | new SimpleDoubleProperty()  | new SimpleDoubleProperty()  | 0d                    | 1d
        'StringProperty'  | new SimpleStringProperty()  | new SimpleStringProperty()  | 'foo'                 | 'bar'
        'ObjectProperty'  | new SimpleObjectProperty()  | new SimpleObjectProperty()  | []                    | [:]
        'MapProperty'     | new SimpleMapProperty()     | new SimpleMapProperty()     | observableHashMap()   | observableMap([a: 1])
        'ListProperty'    | new SimpleListProperty()    | new SimpleListProperty()    | observableArrayList() | observableArrayList(1)
        'SetProperty'     | new SimpleSetProperty()     | new SimpleSetProperty()     | observableSet()       | observableSet(1)
        'PropertyBoolean' | new SimpleBooleanProperty() | new SimpleBooleanProperty() | false                 | true
        'PropertyInteger' | new SimpleIntegerProperty() | new SimpleIntegerProperty() | 0                     | 1
        'PropertyLong'    | new SimpleLongProperty()    | new SimpleLongProperty()    | 0l                    | 1l
        'PropertyFloat'   | new SimpleFloatProperty()   | new SimpleFloatProperty()   | 0f                    | 1f
        'PropertyDouble'  | new SimpleDoubleProperty()  | new SimpleDoubleProperty()  | 0d                    | 1d
        'PropertyString'  | new SimpleStringProperty()  | new SimpleStringProperty()  | 'foo'                 | 'bar'
    }

    void "Check update is triggered inside UI thread for #type"() {
        given:
        source.set(value1)

        ValueHolder cvh = new ValueHolder()
        ValueHolder ivh = new ValueHolder()

        def property = UIThreadAwareBindings."uiThreadAware${type}"(source)
        ChangeListener changeListener = { v, o, n ->
            cvh.value = n
            cvh.insideUIThread = Platform.isFxApplicationThread()
        }
        InvalidationListener invalidationListener = { o ->
            ivh.insideUIThread = Platform.isFxApplicationThread()
        }

        property.addListener(changeListener)
        property.addListener(invalidationListener)

        expect:
        property.getValue() == value1

        when: 'trigger change outside UI thread'
        setValue(type, source, property, value2)
        await().timeout(2, SECONDS).until { cvh.value != null }

        then:
        value2 == cvh.value
        cvh.insideUIThread
        ivh.insideUIThread

        when: 'trigger change inside UI thread'
        // reset
        ivh.insideUIThread = false
        cvh.insideUIThread = false
        cvh.value = null

        Platform.runLater { setValue(type, source, property, value1) }
        await().timeout(2, SECONDS).until { cvh.value != null }

        then:
        value1 == cvh.value
        cvh.insideUIThread
        ivh.insideUIThread

        when: 'register UI aware listeners'
        // reset
        ivh.insideUIThread = false
        cvh.insideUIThread = false
        cvh.value = null

        property.removeListener(changeListener)
        property.removeListener(invalidationListener)
        changeListener = uiThreadAwareChangeListener(changeListener)
        invalidationListener = uiThreadAwareInvalidationListener(invalidationListener)
        property.addListener(changeListener)
        property.addListener(invalidationListener)

        setValue(type, source, property, value2)
        await().timeout(2, SECONDS).until { cvh.value != null }

        then:
        value2 == cvh.value
        value2 == property.get()
        cvh.insideUIThread
        ivh.insideUIThread

        cleanup:
        property.removeListener(changeListener)
        property.removeListener(invalidationListener)

        where:
        type                | source                      | target                      | value1 | value2
        'ObservableBoolean' | new SimpleBooleanProperty() | new SimpleBooleanProperty() | false  | true
        'ObservableInteger' | new SimpleIntegerProperty() | new SimpleIntegerProperty() | 0      | 1
        'ObservableLong'    | new SimpleLongProperty()    | new SimpleLongProperty()    | 0l     | 1l
        'ObservableFloat'   | new SimpleFloatProperty()   | new SimpleFloatProperty()   | 0f     | 1f
        'ObservableDouble'  | new SimpleDoubleProperty()  | new SimpleDoubleProperty()  | 0d     | 1d
        'ObservableString'  | new SimpleStringProperty()  | new SimpleStringProperty()  | 'foo'  | 'bar'
        'Observable'        | new SimpleObjectProperty()  | new SimpleObjectProperty()  | []     | [:]
    }

    static class ValueHolder {
        Object value
        boolean insideUIThread
    }

    private static void setValue(type, source, observable, value) {
        if (type.startsWith('Property')) {
            observable.setValue(value)
        } else if (type.startsWith('Observable')) {
            source.set(value)
        } else {
            observable.set(value)
        }
    }
}
