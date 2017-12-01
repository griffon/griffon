/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TransitionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.effects.TransitionListener {
    private CallableWithArgs<Void> transitionCompleted;

    public CallableWithArgs<Void> getTransitionCompleted() {
        return this.transitionCompleted;
    }


    public void setTransitionCompleted(CallableWithArgs<Void> transitionCompleted) {
        this.transitionCompleted = transitionCompleted;
    }


    public void transitionCompleted(org.apache.pivot.wtk.effects.Transition arg0) {
        if (transitionCompleted != null) {
            transitionCompleted.call(arg0);
        }
    }

}
