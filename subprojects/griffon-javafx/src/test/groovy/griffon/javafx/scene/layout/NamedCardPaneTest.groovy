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
package griffon.javafx.scene.layout

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testfx.framework.junit.ApplicationTest
import org.testfx.service.support.WaitUntilSupport

import java.util.concurrent.Callable

import static org.hamcrest.Matchers.equalTo
import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.base.NodeMatchers.isVisible

@Disabled
class NamedCardPaneTest extends ApplicationTest {
    @Override
    void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane()
        ButtonBar buttonBar = new ButtonBar()
        borderPane.setTop(buttonBar)
        NamedCardPane cardPane = new NamedCardPane()
        cardPane.setId('cardPane')
        borderPane.setCenter(cardPane)

        1.upto(5) { i ->
            Label card = new Label('Card ' + i)
            String id = 'card_' + i
            StackPane stack = new StackPane(card)
            stack.setId(id)
            cardPane.getChildren().add(stack)

            Button b = new Button(id)
            b.setOnAction({ cardPane.show(id) } as EventHandler<ActionEvent>)
            buttonBar.getButtons().add(b)
        }

        Scene scene = new Scene(borderPane)
        stage.setScene(scene)
        stage.show()
    }

    @Test
    void browseCards() {
        // given:
        WaitUntilSupport waiter = new WaitUntilSupport()
        NamedCardPane cardPane = lookup('#cardPane').query()
        cardPane.show('card_5')

        // expect:
        // verifyThat('Card 5', isVisible())

        5.downto(1) { i ->
            // when:
            clickOn('card_' + i)

            // then:
            waiter.waitUntil({ cardPane.getSelectedNodeId() } as Callable, equalTo('card_' + i), 1)
            verifyThat('Card ' + i, isVisible())
        }
    }
}
