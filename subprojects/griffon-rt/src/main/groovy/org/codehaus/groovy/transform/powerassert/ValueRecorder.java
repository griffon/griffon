/*
 * Copyright 2008-2011 the original author or authors.
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

package org.codehaus.groovy.transform.powerassert;

import org.codehaus.groovy.runtime.powerassert.Value;

import java.util.List;

/**
 * Records values produced during evaluation of an assertion statement's truth
 * expression.
 *
 * @author Peter Niederwieser
 */
public class ValueRecorder extends org.codehaus.groovy.runtime.powerassert.ValueRecorder {
    public void clear() {
        super.clear();
    }

    public Object record(Object value, int anchor) {
        return super.record(value, anchor);
    }

    public List<Value> getValues() {
        return super.getValues();
    }
}
