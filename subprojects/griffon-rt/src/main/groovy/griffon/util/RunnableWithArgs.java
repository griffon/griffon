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

/**
 * A Runnable that can have arguments. <p>
 * Instances of this class can be seen as substitutes for Closures when dealing
 * with non-Groovy languages. There are several Griffon core and Griffon plugin APIs
 * that accept a {@code RunnableWithArgs} where a Closure would be used.</p>
 * <p>Example:</p>
 * <pre>
Runnable r = new RunnableWithArgs() {
    public void run(Object[] args) {
        System.out.println("Hello "+ args[0]);
    }
};

r.setArgs("world!");
r.run();
// prints Hello world!
r.run(new Object[]{ "again" });
// prints Hello again
 * </pre>
 *
 * @author Andres Almiray
 */
public abstract class RunnableWithArgs implements Runnable {
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

    public final void run() {
        Object[] copy = null;
        synchronized (lock) {
            copy = new Object[args.length];
            System.arraycopy(args, 0, copy, 0, args.length);
        }
        run(copy);
    }

    public abstract void run(Object[] args);
}
