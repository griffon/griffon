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

/**
 * A Runnable that can have arguments.
 *
 * @author Andres Almiray
 */
public abstract class RunnableWithArgs implements Runnable {
    private static final Object[] NO_ARGS = new Object[0];
    private final Object LOCK = new Object();
    private Object[] args = NO_ARGS;

    public void setArgs(Object[] args) {
        if(args == null) {
            args = NO_ARGS;
        }
        synchronized(LOCK) {
            this.args = new Object[args.length];
            System.arraycopy(args, 0, this.args, 0, args.length);
        }
    }

    public Object[] getArgs() {
        return args;
    }

    public final void run() {
        Object[] copy = null;
        synchronized(LOCK) {
            copy = new Object[args.length];
            System.arraycopy(args, 0, copy, 0, args.length);
        }
        run(copy);
    }

    public abstract void run(Object[] args);
}
