/*
 * Copyright 2011-2013 the original author or authors.
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


package org.codehaus.griffon.runtime.injection;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * Perform production, injection, lifecycle callbacks and cleanup on a
 * non-contextual object.<p>
 * <p/>
 * Original source code<br/>
 * https://github.com/seam/wicket/raw/master/impl/src/main/java/org/jboss/seam/wicket/util/NonContextual.java
 *
 * @param <T>
 * @author pmuir
 * @author cpopetz
 * @author ivaynberg
 * @author aalmiray
 */
public class NonContextual<T> {
    private static final ClassCache CLASS_CACHE = new ClassCache();

    public static <T> NonContextual<T> of(Class<T> clazz, BeanManager manager) {
        NonContextual<T> nc = (NonContextual<T>) CLASS_CACHE.get(clazz);

        if (nc == null) {
            nc = new NonContextual<>(manager, clazz);
            CLASS_CACHE.put(clazz, nc);
        }
        return nc;
    }

    // Store the injection target. The CDI spec doesn't require an
    // implementation
    // to cache it, so we do
    private final InjectionTarget<? extends T> injectionTarget;

    // Store a reference to the CDI BeanManager
    private final BeanManager beanManager;

    /**
     * Create an injector for the given class
     */
    public NonContextual(BeanManager manager, Class<? extends T> clazz) {
        this.beanManager = manager;

        // Generate an "Annotated Type"
        AnnotatedType<? extends T> type = manager.createAnnotatedType(clazz);

        // Generate the InjectionTarget
        this.injectionTarget = manager.createInjectionTarget(type);
    }

    @SuppressWarnings("unchecked")
    public Instance<T> newInstance() {
        return new Instance<T>(beanManager, (InjectionTarget<T>) injectionTarget);
    }

    @SuppressWarnings("unchecked")
    public Instance<T> existingInstance(T instance) {
        return new Instance<T>(beanManager, (InjectionTarget<T>) injectionTarget, instance);
    }

    /**
     * Represents a non-contextual instance
     *
     * @param <T>
     * @author pmuir
     */
    public static class Instance<T> {
        private final CreationalContext<T> ctx;
        private final InjectionTarget<T> injectionTarget;
        private T instance;
        private boolean disposed = false;

        private Instance(BeanManager beanManager, InjectionTarget<T> injectionTarget) {
            this.injectionTarget = injectionTarget;
            this.ctx = beanManager.createCreationalContext(null);
        }

        private Instance(BeanManager beanManager, InjectionTarget<T> injectionTarget, T instance) {
            this.injectionTarget = injectionTarget;
            this.ctx = beanManager.createCreationalContext(null);
            this.instance = instance;
        }

        /**
         * Get the instance
         *
         * @return
         */
        public T get() {
            return instance;
        }

        /**
         * Create the instance
         *
         * @return
         */
        public Instance<T> produce() {
            if (this.instance != null) {
                throw new IllegalStateException("Trying to call produce() on already constructed instance");
            }
            if (disposed) {
                throw new IllegalStateException("Trying to call produce() on an already disposed instance");
            }
            this.instance = injectionTarget.produce(ctx);
            return this;
        }

        /**
         * Inject the instance
         *
         * @return
         */
        public Instance<T> inject() {
            if (this.instance == null) {
                throw new IllegalStateException("Trying to call inject() before produce() was called");
            }
            if (disposed) {
                throw new IllegalStateException("Trying to call inject() on already disposed instance");
            }
            injectionTarget.inject(instance, ctx);
            return this;
        }

        /**
         * Call the @PostConstruct callback
         *
         * @return
         */
        public Instance<T> postConstruct() {
            if (this.instance == null) {
                throw new IllegalStateException("Trying to call postConstruct() before produce() was called");
            }
            if (disposed) {
                throw new IllegalStateException("Trying to call preDestroy() on already disposed instance");
            }
            injectionTarget.postConstruct(instance);
            return this;
        }

        /**
         * Call the @PreDestroy callback
         *
         * @return
         */
        public Instance<T> preDestroy() {
            if (this.instance == null) {
                throw new IllegalStateException("Trying to call preDestroy() before produce() was called");
            }
            if (disposed) {
                throw new IllegalStateException("Trying to call preDestroy() on already disposed instance");
            }
            injectionTarget.preDestroy(instance);
            return this;
        }

        /**
         * Dispose of the instance, doing any necessary cleanup
         */
        public Instance<T> dispose() {
            if (this.instance == null) {
                throw new IllegalStateException("Trying to call dispose() before produce() was called");
            }
            if (disposed) {
                throw new IllegalStateException("Trying to call dispose() on already disposed instance");
            }
            injectionTarget.dispose(instance);
            ctx.release();
            return this;
        }
    }
}
