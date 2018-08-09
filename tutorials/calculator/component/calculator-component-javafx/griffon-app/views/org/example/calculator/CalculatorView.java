/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example.calculator;

import griffon.core.artifact.GriffonView;
import griffon.annotations.inject.MVCMember;
import org.kordamp.jipsy.ServiceProviderFor;
import griffon.util.CollectionUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import griffon.annotations.core.Nonnull;
import javax.inject.Inject;

import static org.example.calculator.CalculatorPM.ATTR_ERROR;
import static org.example.calculator.CalculatorPM.ATTR_OP1;
import static org.example.calculator.CalculatorPM.ATTR_OP2;
import static org.example.calculator.CalculatorPM.ATTR_RESULT;
import static org.example.calculator.CalculatorPM.PM_CALCULATION;
import static org.opendolphin.binding.JFXBinder.bind;

@ServiceProviderFor(GriffonView.class)
public class CalculatorView extends AbstractJavaFXGriffonView {
    private static final String TEXT = "text";

    private CalculatorController controller;

    @MVCMember
    public void setController(@Nonnull CalculatorController controller) {
        this.controller = controller;
    }

    @FXML
    private TextField op1Field;

    @FXML
    private TextField op2Field;

    @FXML
    private TextField resultField;

    @Inject
    private ClientDolphin clientDolphin;

    private Node calculatorNode;

    @Nonnull
    public Node getCalculatorNode() {
        return calculatorNode;
    }

    @Override
    public void initUI() {
        calculatorNode = loadFromFXML();
        connectActions(calculatorNode, controller);
        bindPresentationModel();
    }

    private void bindPresentationModel() {
        ClientPresentationModel pm = clientDolphin.presentationModel(PM_CALCULATION, CollectionUtils.<String, Object>map()
            .e(ATTR_OP1, 0L)
            .e(ATTR_OP2, 0L)
            .e(ATTR_RESULT, 0L)
            .e(ATTR_ERROR, ""));

        // from PM attributes to UI widgets
        bind(ATTR_OP1).of(pm).to(TEXT).of(op1Field);
        bind(ATTR_OP2).of(pm).to(TEXT).of(op2Field);
        bind(ATTR_RESULT).of(pm).to(TEXT).of(resultField);

        // from UI widgets to PM attributes
        bind(TEXT).of(op1Field).to(ATTR_OP1).of(pm);
        bind(TEXT).of(op2Field).to(ATTR_OP2).of(pm);
    }
}
