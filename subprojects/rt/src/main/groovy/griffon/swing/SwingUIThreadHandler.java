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
package griffon.swing;

import griffon.util.UIThreadHandler;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Executes code using SwingUtilities.
 *
 * @author Andres Almiray
 */
public class SwingUIThreadHandler implements UIThreadHandler {
    public boolean isUIThread() {
        return SwingUtilities.isEventDispatchThread();
    }

    public void executeAsync(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public void executeSync(Runnable runnable) {
        if(isUIThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch(InterruptedException ie) {
                // ignore
            } catch(InvocationTargetException ite) {
                // ignore
            }
        }
    }

    public void executeOutside(Runnable runnable) {
        if(!isUIThread()) {
            runnable.run();
        } else {
            // TODO use a ThreadPool
            new Thread(runnable).start();
        }
    }
}
