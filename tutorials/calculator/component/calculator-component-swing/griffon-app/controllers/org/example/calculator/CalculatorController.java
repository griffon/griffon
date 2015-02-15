package org.example.calculator;

import griffon.core.artifact.GriffonController;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.opendolphin.core.client.ClientDolphin;

import javax.inject.Inject;

import static org.example.calculator.CalculatorPM.COMMAND_DIV;
import static org.example.calculator.CalculatorPM.COMMAND_MUL;
import static org.example.calculator.CalculatorPM.COMMAND_SUB;
import static org.example.calculator.CalculatorPM.COMMAND_SUM;

@ArtifactProviderFor(GriffonController.class)
public class CalculatorController extends AbstractGriffonController {
    @Inject
    private ClientDolphin clientDolphin;

    public void sum() {
        clientDolphin.send(COMMAND_SUM);
    }

    public void sub() {
        clientDolphin.send(COMMAND_SUB);
    }

    public void mul() {
        clientDolphin.send(COMMAND_MUL);
    }

    public void div() {
        clientDolphin.send(COMMAND_DIV);
    }
}