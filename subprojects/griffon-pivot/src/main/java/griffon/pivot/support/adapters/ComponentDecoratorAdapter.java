/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ComponentDecoratorAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentDecoratorListener {
    private CallableWithArgs<?> decoratorInserted;
    private CallableWithArgs<?> decoratorUpdated;
    private CallableWithArgs<?> decoratorsRemoved;

    public CallableWithArgs<?> getDecoratorInserted() {
        return this.decoratorInserted;
    }

    public CallableWithArgs<?> getDecoratorUpdated() {
        return this.decoratorUpdated;
    }

    public CallableWithArgs<?> getDecoratorsRemoved() {
        return this.decoratorsRemoved;
    }


    public void setDecoratorInserted(CallableWithArgs<?> decoratorInserted) {
        this.decoratorInserted = decoratorInserted;
    }

    public void setDecoratorUpdated(CallableWithArgs<?> decoratorUpdated) {
        this.decoratorUpdated = decoratorUpdated;
    }

    public void setDecoratorsRemoved(CallableWithArgs<?> decoratorsRemoved) {
        this.decoratorsRemoved = decoratorsRemoved;
    }


    public void decoratorInserted(org.apache.pivot.wtk.Component arg0, int arg1) {
        if (decoratorInserted != null) {
            decoratorInserted.call(arg0, arg1);
        }
    }

    public void decoratorUpdated(org.apache.pivot.wtk.Component arg0, int arg1, org.apache.pivot.wtk.effects.Decorator arg2) {
        if (decoratorUpdated != null) {
            decoratorUpdated.call(arg0, arg1, arg2);
        }
    }

    public void decoratorsRemoved(org.apache.pivot.wtk.Component arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (decoratorsRemoved != null) {
            decoratorsRemoved.call(arg0, arg1, arg2);
        }
    }

}
