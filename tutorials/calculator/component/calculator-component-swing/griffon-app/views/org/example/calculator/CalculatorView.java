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
import org.kordamp.jipsy.annotations.ServiceProviderFor;
import griffon.util.CollectionUtils;
import net.miginfocom.swing.MigLayout;
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import griffon.annotations.core.Nonnull;
import javax.inject.Inject;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static griffon.util.GriffonClassUtils.getPropertyValue;
import static griffon.util.GriffonClassUtils.setPropertyValue;
import static org.example.calculator.CalculatorPM.ATTR_ERROR;
import static org.example.calculator.CalculatorPM.ATTR_OP1;
import static org.example.calculator.CalculatorPM.ATTR_OP2;
import static org.example.calculator.CalculatorPM.ATTR_RESULT;
import static org.example.calculator.CalculatorPM.PM_CALCULATION;
import static org.opendolphin.binding.Binder.bind;

@ServiceProviderFor(GriffonView.class)
public class CalculatorView extends AbstractSwingGriffonView {
    private static final String TEXT = "text";

    private CalculatorController controller;
    private CalculatorModel model;

    @MVCMember
    public void setController(@Nonnull CalculatorController controller) {
        this.controller = controller;
    }

    @MVCMember
    public void setModel(@Nonnull CalculatorModel model) {
        this.model = model;
    }

    @Inject
    private ClientDolphin clientDolphin;

    private JPanel calculatorWidget;

    @Nonnull
    public JComponent getCalculatorWidget() {
        return calculatorWidget;
    }

    @Override
    public void initUI() {
        calculatorWidget = new JPanel(new MigLayout("fill", "[label, pref!][grow, 100!][pref!]"));

        calculatorWidget.add(new JLabel("Operand 1:"), "left");
        JTextField op1Field = new JTextField();
        op1Field.setName(ATTR_OP1);
        op1Field.setHorizontalAlignment(SwingConstants.RIGHT);
        op1Field.getDocument().addDocumentListener(new TextFieldBinder(ATTR_OP1, op1Field));
        calculatorWidget.add(op1Field, "grow");
        Action action = toolkitActionFor(controller, "sum");
        JButton button = new JButton(action);
        button.setText("+");
        calculatorWidget.add(button, "right, wrap");

        calculatorWidget.add(new JLabel("Operand 2:"), "left");
        JTextField op2Field = new JTextField();
        op2Field.setName(ATTR_OP2);
        op2Field.setHorizontalAlignment(SwingConstants.RIGHT);
        op2Field.getDocument().addDocumentListener(new TextFieldBinder(ATTR_OP2, op2Field));
        calculatorWidget.add(op2Field, "grow");
        action = toolkitActionFor(controller, "sub");
        button = new JButton(action);
        button.setText("-");
        calculatorWidget.add(button, "right, wrap");

        calculatorWidget.add(new JLabel("Result"), "left");
        final JTextField resultField = new JTextField();
        resultField.setName(ATTR_RESULT);
        resultField.setEditable(false);
        resultField.setHorizontalAlignment(SwingConstants.RIGHT);
        model.addPropertyChangeListener(ATTR_RESULT, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                resultField.setText(String.valueOf(evt.getNewValue()));
            }
        });
        calculatorWidget.add(resultField, "grow");
        action = toolkitActionFor(controller, "mul");
        button = new JButton(action);
        button.setText("*");
        calculatorWidget.add(button, "right, wrap");

        calculatorWidget.add(new JLabel(), "left");
        calculatorWidget.add(new JLabel(), "left");
        action = toolkitActionFor(controller, "div");
        button = new JButton(action);
        button.setText("/");
        calculatorWidget.add(button, "right, wrap");

        bindPresentationModel();
    }

    private void bindPresentationModel() {
        ClientPresentationModel pm = clientDolphin.presentationModel(PM_CALCULATION, CollectionUtils.<String, Object>map()
            .e(ATTR_OP1, 0L)
            .e(ATTR_OP2, 0L)
            .e(ATTR_RESULT, 0L)
            .e(ATTR_ERROR, ""));

        // from PM attributes to model
        bind(ATTR_OP1).of(pm).to(ATTR_OP1).of(model);
        bind(ATTR_OP2).of(pm).to(ATTR_OP2).of(model);
        bind(ATTR_RESULT).of(pm).to(ATTR_RESULT).of(model);

        // from model to PM attributes
        bind(ATTR_OP1).of(model).to(ATTR_OP1).of(pm);
        bind(ATTR_OP2).of(model).to(ATTR_OP2).of(pm);
    }

    private class TextFieldBinder implements DocumentListener {
        private final String propertyName;
        private final JTextField widget;

        private TextFieldBinder(String propertyName, JTextField widget) {
            this.propertyName = propertyName;
            this.widget = widget;
            model.addPropertyChangeListener(propertyName, new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Object newValue = evt.getNewValue();
                    String val = String.valueOf(newValue);
                    if (newValue != null && !val.equals(widget.getText())) {
                        widget.setText(val);
                    }
                }
            });
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            compareAndSet();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            compareAndSet();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            compareAndSet();
        }

        private void compareAndSet() {
            Object oldValue = getPropertyValue(model, propertyName);
            String newValue = widget.getText();
            if (newValue != null && !newValue.equals(oldValue)) {
                setPropertyValue(model, propertyName, newValue);
            }
        }
    }
}
