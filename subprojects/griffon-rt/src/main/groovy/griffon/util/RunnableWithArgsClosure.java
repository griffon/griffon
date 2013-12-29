/*
 * Copyright 2009-2014 the original author or authors.
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
package griffon.util;

import groovy.lang.Closure;

/**
 * A Closure that wraps a RunnableWithArgs.
 *
 * @author Andres Almiray
 */
public class RunnableWithArgsClosure extends Closure {
    private final RunnableWithArgs runnable;

    public RunnableWithArgsClosure(RunnableWithArgs runnable) {
        this(new Object(), runnable);
    }

    public RunnableWithArgsClosure(Object owner, RunnableWithArgs runnable) {
        super(owner);
        this.runnable = runnable;
    }

    protected Object doCall(Object[] args) {
        try {
            runnable.run(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
