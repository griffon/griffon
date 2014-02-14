/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.pivot.support;

import griffon.core.CallableWithArgs;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Component;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class PivotAction extends Action {
    private String name;
    private String description;
    private CallableWithArgs<Void> callable;

    public PivotAction() {
        this(null);
    }

    public PivotAction(CallableWithArgs<Void> callable) {
        this.callable = callable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CallableWithArgs<Void> getCallable() {
        return callable;
    }

    public void setCallable(CallableWithArgs<Void> callable) {
        this.callable = callable;
    }

    public String toString() {
        return "Action[" + name + ", " + description + "]";
    }

    @Override
    public void perform(Component component) {
        requireNonNull(callable, "Argument 'callable' cannot be null for " + this);
        callable.call(component);
    }
}
