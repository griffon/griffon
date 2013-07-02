/*
 * Copyright 2009-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.Event;
import griffon.core.EventRouter;
import griffon.util.RunnableWithArgs;
import groovy.lang.Closure;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@code EventRouter} where all operations are ignored.
 *
 * @author Andres Almiray
 * @since 1.2.0
 */
public class NoopEventRouter implements EventRouter {

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void publish(String eventName) {
    }

    @Override
    public void publish(String eventName, List params) {
    }

    @Override
    public void publish(Event event) {
    }

    @Override
    public void publishOutsideUI(String eventName) {
    }

    @Override
    public void publishOutsideUI(String eventName, List params) {
    }

    @Override
    public void publishOutsideUI(Event event) {
    }

    @Override
    public void publishAsync(String eventName) {
    }

    @Override
    public void publishAsync(String eventName, List params) {
    }

    @Override
    public void publishAsync(Event event) {
    }

    @Override
    public void addEventListener(Object listener) {
    }

    @Override
    public void addEventListener(Map<String, Object> listener) {
    }

    @Override
    public void removeEventListener(Object listener) {
    }

    @Override
    public void removeEventListener(Map<String, Object> listener) {
    }

    @Override
    public void addEventListener(String eventName, Closure listener) {
    }

    @Override
    public void addEventListener(String eventName, RunnableWithArgs listener) {
    }

    @Override
    public void addEventListener(Class<? extends Event> eventClass, Closure listener) {
    }

    @Override
    public void addEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
    }

    @Override
    public void removeEventListener(String eventName, Closure listener) {
    }

    @Override
    public void removeEventListener(String eventName, RunnableWithArgs listener) {
    }

    @Override
    public void removeEventListener(Class<? extends Event> eventClass, Closure listener) {
    }

    @Override
    public void removeEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
    }
}
