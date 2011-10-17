/*
 * Copyright 2009-2011 the original author or authors.
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

import java.util.concurrent.Callable;

/**
 * A Callable that can have arguments.
 * Instances of this class can be seen as substitutes for Closures when dealing
 * with non-Groovy languages. There are several Griffon core and Griffon plugin APIs
 * that accept a {@code CallableWithArgs} where a Closure would be used.</p>
 * <p>Example:</p>
 * <pre>
Callable&lt;Boolean&gt; c = new CallableWithArgs&lt;Boolean&gt;() {
    public Boolean call(Object[] args) {
        return "Hello".equals(args[0]);
    }
};

c.setArgs("Hello");
assert c.call()
c.setArgs("world!");
assert !c.call();
assert !c.call(new Object[]{ "again" });
 * </pre>
 *
 *
 * @author Andres Almiray
 */
public abstract class CallableWithArgs<V> implements Callable<V> {
    private static final Object[] NO_ARGS = new Object[0];
    private final Object[] lock = new Object[0];
    private Object[] args = NO_ARGS;

    public void setArgs(Object[] args) {
        if (args == null) {
            args = NO_ARGS;
        }
        synchronized (lock) {
            this.args = new Object[args.length];
            System.arraycopy(args, 0, this.args, 0, args.length);
        }
    }

    public Object[] getArgs() {
        synchronized (lock) {
            return args;
        }
    }

    public final V call() {
        Object[] copy = null;
        synchronized (lock) {
            copy = new Object[args.length];
            System.arraycopy(args, 0, copy, 0, args.length);
        }
        return call(copy);
    }

    public abstract V call(Object[] args);
}
