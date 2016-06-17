/*
 * Copyright 2016 the original author or authors.
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
package org.example.calculator.pm;

import org.example.calculator.Calculation;
import org.example.calculator.Calculator;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.action.ServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import javax.inject.Inject;
import java.util.List;

import static java.lang.Long.parseLong;
import static org.example.calculator.CalculatorPM.ATTR_ERROR;
import static org.example.calculator.CalculatorPM.ATTR_OP1;
import static org.example.calculator.CalculatorPM.ATTR_OP2;
import static org.example.calculator.CalculatorPM.ATTR_RESULT;
import static org.example.calculator.CalculatorPM.COMMAND_DIV;
import static org.example.calculator.CalculatorPM.COMMAND_MUL;
import static org.example.calculator.CalculatorPM.COMMAND_SUB;
import static org.example.calculator.CalculatorPM.COMMAND_SUM;
import static org.example.calculator.CalculatorPM.PM_CALCULATION;

public class CalculatorAction implements ServerAction {
    @Inject
    private Calculator calculator;

    @Inject
    private ServerModelStore serverModelStore;

    @Override
    public void registerIn(ActionRegistry registry) {
        registry.register(COMMAND_SUM, new CommandHandler() {
            @Override
            public void handleCommand(Command command, List response) {
                PresentationModel pm = serverModelStore.findPresentationModelById(PM_CALCULATION);
                Calculation c = calculator.sum(getAsLong(pm, ATTR_OP1), getAsLong(pm, ATTR_OP2));
                handleCalculation(response, pm, c);
            }
        });

        registry.register(COMMAND_SUB, new CommandHandler() {
            @Override
            public void handleCommand(Command command, List response) {
                PresentationModel pm = serverModelStore.findPresentationModelById(PM_CALCULATION);
                Calculation c = calculator.sub(getAsLong(pm, ATTR_OP1), getAsLong(pm, ATTR_OP2));
                handleCalculation(response, pm, c);
            }
        });

        registry.register(COMMAND_MUL, new CommandHandler() {
            @Override
            public void handleCommand(Command command, List response) {
                PresentationModel pm = serverModelStore.findPresentationModelById(PM_CALCULATION);
                Calculation c = calculator.mul(getAsLong(pm, ATTR_OP1), getAsLong(pm, ATTR_OP2));
                handleCalculation(response, pm, c);
            }
        });

        registry.register(COMMAND_DIV, new CommandHandler() {
            @Override
            public void handleCommand(Command command, List response) {
                PresentationModel pm = serverModelStore.findPresentationModelById(PM_CALCULATION);
                Calculation c = calculator.div(getAsLong(pm, ATTR_OP1), getAsLong(pm, ATTR_OP2));
                handleCalculation(response, pm, c);
            }
        });
    }

    private void handleCalculation(List response, PresentationModel pm, Calculation c) {
        ServerAttribute error = (ServerAttribute) pm.findAttributeByPropertyName(ATTR_ERROR);
        ServerAttribute result = (ServerAttribute) pm.findAttributeByPropertyName(ATTR_RESULT);
        if (c.hasError()) {
            ServerDolphin.changeValue(response, error, c.getError());
            ServerDolphin.changeValue(response, result, 0L);
        } else {
            ServerDolphin.changeValue(response, error, "");
            ServerDolphin.changeValue(response, result, c.getResult());
        }
    }

    private long getAsLong(PresentationModel pm, String attrId) {
        return parseLong(String.valueOf(pm.findAttributeByPropertyName(attrId).getValue()));
    }
}
