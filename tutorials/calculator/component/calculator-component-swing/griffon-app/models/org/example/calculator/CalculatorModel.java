package org.example.calculator;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

@ArtifactProviderFor(GriffonModel.class)
public class CalculatorModel extends AbstractGriffonModel {
    private String op1;
    private String op2;
    private String result;

    public String getOp1() {
        return op1;
    }

    public void setOp1(String op1) {
        firePropertyChange("op1", this.op1, this.op1 = op1);
    }

    public String getOp2() {
        return op2;
    }

    public void setOp2(String op2) {
        firePropertyChange("op2", this.op2, this.op2 = op2);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        firePropertyChange("result", this.op2, this.result = result);
    }
}