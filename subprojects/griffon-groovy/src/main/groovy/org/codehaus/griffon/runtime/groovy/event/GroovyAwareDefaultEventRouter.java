/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.event;

import groovy.lang.Closure;
import org.codehaus.griffon.runtime.core.event.DefaultEventRouter;

import javax.annotation.Nonnull;
import java.lang.reflect.Proxy;

import static griffon.util.GriffonClassUtils.getFieldValue;
import static java.lang.reflect.Proxy.getInvocationHandler;

/**
 * @author Andres Almiray
 * @since 2.5.0
 */
public class GroovyAwareDefaultEventRouter extends DefaultEventRouter {
    @Override
    protected boolean isNestedListener(@Nonnull Object listener, @Nonnull Object owner) {
        if (listener instanceof Proxy) {
            try {
                Object delegate = getFieldValue(getInvocationHandler(listener), "delegate");
                if (delegate instanceof Closure) {
                    return owner.equals(((Closure) delegate).getOwner());
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return super.isNestedListener(listener, owner);
    }
}
