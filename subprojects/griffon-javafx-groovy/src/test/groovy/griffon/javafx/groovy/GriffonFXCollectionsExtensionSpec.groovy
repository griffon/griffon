/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.javafx.groovy

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import javafx.embed.swing.JFXPanel
import spock.lang.Specification
import spock.lang.Unroll

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await

@Unroll
class GriffonFXCollectionsExtensionSpec extends Specification {
    static {
        // initialize UI toolkit
        new JFXPanel()
    }

    void "Trigger list events inside UI thread (source) "() {
        given:
        ObservableList<String> source = FXCollections.observableArrayList()
        ObservableList<String> target = source.uiThreadAware()
        ListChangeListener<String> witness = new ListChangeListener<String>() {
            boolean changed
            boolean changedInsideUIThread

            @Override
            void onChanged(ListChangeListener.Change<? extends String> c) {
                changed = true
                changedInsideUIThread = Platform.isFxApplicationThread()
            }
        }
        target.addListener(witness)

        when:
        source << 'change'
        await().timeout(2, SECONDS).until { witness.changed }

        then:
        witness.changedInsideUIThread
    }

    void "Trigger list events inside UI thread (target) "() {
        given:
        ObservableList<String> source = FXCollections.observableArrayList()
        ObservableList<String> target = source.uiThreadAware()
        ListChangeListener<String> witness = new ListChangeListener<String>() {
            boolean changed
            boolean changedInsideUIThread

            @Override
            void onChanged(ListChangeListener.Change<? extends String> c) {
                changed = true
                changedInsideUIThread = Platform.isFxApplicationThread()
            }
        }
        target.addListener(witness)

        when:
        target << 'change'
        await().timeout(2, SECONDS).until { witness.changed }

        then:
        witness.changedInsideUIThread
    }

    void "Trigger set events inside UI thread (source) "() {
        given:
        ObservableSet<String> source = FXCollections.observableSet()
        ObservableSet<String> target = source.uiThreadAware()
        SetChangeListener<String> witness = new SetChangeListener<String>() {
            boolean changed
            boolean changedInsideUIThread

            @Override
            void onChanged(SetChangeListener.Change<? extends String> c) {
                changed = true
                changedInsideUIThread = Platform.isFxApplicationThread()
            }
        }
        target.addListener(witness)

        when:
        source << 'change'
        await().until { witness.changed }

        then:
        witness.changedInsideUIThread
    }

    void "Trigger set events inside UI thread (target) "() {
        given:
        ObservableSet<String> source = FXCollections.observableSet()
        ObservableSet<String> target = source.uiThreadAware()
        SetChangeListener<String> witness = new SetChangeListener<String>() {
            boolean changed
            boolean changedInsideUIThread

            @Override
            void onChanged(SetChangeListener.Change<? extends String> c) {
                changed = true
                changedInsideUIThread = Platform.isFxApplicationThread()
            }
        }
        target.addListener(witness)

        when:
        target << 'change'
        await().timeout(2, SECONDS).until { witness.changed }

        then:
        witness.changedInsideUIThread
    }

    void "Trigger map events inside UI thread (source) "() {
        given:
        ObservableMap<String, String> source = FXCollections.observableHashMap()
        ObservableMap<String, String> target = source.uiThreadAware()
        MapChangeListener<String, String> witness = new MapChangeListener<String, String>() {
            boolean changed
            boolean changedInsideUIThread

            @Override
            void onChanged(MapChangeListener.Change<? extends String, ? extends String> c) {
                changed = true
                changedInsideUIThread = Platform.isFxApplicationThread()
            }
        }
        target.addListener(witness)

        when:
        source.key = 'value'
        await().until { witness.changed }

        then:
        witness.changedInsideUIThread
    }

    void "Trigger map events inside UI thread (target) "() {
        given:
        ObservableMap<String, String> source = FXCollections.observableHashMap()
        ObservableMap<String, String> target = source.uiThreadAware()
        MapChangeListener<String, String> witness = new MapChangeListener<String, String>() {
            boolean changed
            boolean changedInsideUIThread

            @Override
            void onChanged(MapChangeListener.Change<? extends String, ? extends String> c) {
                changed = true
                changedInsideUIThread = Platform.isFxApplicationThread()
            }
        }
        target.addListener(witness)

        when:
        target.key = 'value'
        await().timeout(2, SECONDS).until { witness.changed }

        then:
        witness.changedInsideUIThread
    }
}
