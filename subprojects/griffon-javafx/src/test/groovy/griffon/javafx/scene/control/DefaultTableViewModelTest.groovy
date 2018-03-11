/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.javafx.scene.control

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.scene.control.TableView
import org.junit.Test

import java.security.SecureRandom

class DefaultTableViewModelTest {
    static {
        new JFXPanel()
    }

    private int identifier = 0
    private final SecureRandom random = new SecureRandom()

    @Test
    void tableViewModelCreatesColumns() {
        // given:
        TableViewFormat<Measurement> tableFormat = new DefaultTableFormat<>(
            new DefaultTableFormat.Column('name', 0.2d),
            new DefaultTableFormat.Column('amount', 0.1d),
            new DefaultTableFormat.Column('timestamp')
        )
        ObservableList<Measurement> measurements = FXCollections.observableArrayList()
        measurements.add(nextMeasurement())
        measurements.add(nextMeasurement())
        TableViewModel<Measurement> tableModel = new DefaultTableViewModel<>(measurements, tableFormat)
        TableView<Measurement> tableView = new TableView<>()

        // expect:
        assert tableFormat == tableModel.format
        assert measurements == tableModel.source
        assert tableModel.getColumnAt(0).text == 'Name'
        assert tableModel.getColumnAt(1).text == 'Amount'
        assert tableModel.getColumnAt(2).text == 'Timestamp'
        assert tableFormat.getObservableValue(measurements[0], 0).value == 'Sample-0'

        // when:
        tableModel.attachTo(tableView)

        // then:
        assert tableView.columns.size() == 3
        assert tableView.columns.text == ['Name', 'Amount', 'Timestamp']
        assert tableView.items.size() == 2

        // when:
        tableModel.detachFrom(tableView)

        // then:
        assert !tableView.columns.size()
        assert !tableView.items.size()
    }

    Measurement nextMeasurement() {
        return new Measurement('Sample-' + nextIdentifier(), nextAmount())
    }

    private int nextIdentifier() {
        return identifier++
    }

    private int nextAmount() {
        return 10 + random.nextInt(80)
    }
}
