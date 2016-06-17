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