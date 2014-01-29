/*
 * Copyright 2011 Eike Kettner
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

package org.codehaus.griffon.runtime.tasks;

import griffon.plugins.tasks.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.EventListener;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

// note, this is a modified version from the same named class in Raffael Herzogs "cru-beans" project.

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 */
public class EventEmitter<T extends EventListener> implements Iterable<T> {
    private static final Logger log = LoggerFactory.getLogger(EventEmitter.class);

    private final Class<T> listenerClass;
    private final CopyOnWriteArrayList<T> listeners = new CopyOnWriteArrayList<T>();
    private final T emitter;

    private volatile ExceptionHandler<T> exceptionHandler = ExceptionHandler.RETHROW_EXCEPTION_HANDLER;

    @SuppressWarnings({"unchecked"})
    public EventEmitter(Class<T> clazz, ClassLoader loader, ExceptionHandler<T> handler) {
        ClassLoader l = loader;
        if (l == null) {
            l = clazz.getClassLoader();
            if (l == null) {
                log.warn("Given ClassLoader is null. Using ContextClassLoader from current thread.");
                l = Thread.currentThread().getContextClassLoader();
            }
        }
        emitter = (T) Proxy.newProxyInstance(l, new Class<?>[]{clazz}, createInvocationHandler());
        this.listenerClass = clazz;
        if (handler != null) {
            this.exceptionHandler = handler;
        }
    }


    public static <T extends EventListener> EventEmitter<T> newEmitter(Class<T> listenerClass) {
        return newEmitter(listenerClass, listenerClass.getClassLoader());
    }

    public static <T extends EventListener> EventEmitter<T> newEmitter(Class<T> listenerClass, ClassLoader loader) {
        return newEmitter(listenerClass, loader, null);
    }

    public static <T extends EventListener> EventEmitter<T> newEmitter(Class<T> listenerClass, ExceptionHandler<T> handler) {
        return newEmitter(listenerClass, listenerClass.getClassLoader(), handler);
    }

    public static <T extends EventListener> EventEmitter<T> newEmitter(Class<T> listenerClass, ClassLoader loader, ExceptionHandler<T> handler) {
        return new EventEmitter<T>(listenerClass, loader, null);
    }


    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public T[] getListeners() {
        Object[] objArray = listeners.toArray();
        T[] listeners = (T[]) Array.newInstance(listenerClass, objArray.length);
        System.arraycopy(objArray, 0, listeners, 0, objArray.length);
        return listeners;
    }

    public void setExceptionHandler(ExceptionHandler<T> exceptionHandler) {
        if (exceptionHandler != null) {
            this.exceptionHandler = exceptionHandler;
        }
    }

    public int getListenerCount() {
        return listeners.size();
    }

    public boolean isEmpty() {
        return listeners.isEmpty();
    }

    public Iterator<T> iterator() {
        return listeners.iterator();
    }

    public T emitter() {
        return emitter;
    }

    protected InvocationHandler createInvocationHandler() {
        return new EventInvocationHandler();
    }

    protected class EventInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (T l : EventEmitter.this) {
                boolean keeprunning = invokeListener(l, method, args);
                if (!keeprunning) {
                    break;
                }
            }
            return null;
        }

        protected boolean invokeListener(T l, Method method, Object[] args) throws Throwable {
            try {
                method.invoke(l, args);
                return true;
            } catch (InvocationTargetException e) {
                return exceptionHandler.handleException(l, e.getTargetException());
            }
        }
    }
}
