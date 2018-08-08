/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.util.groovy;

import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public class CompositeBuilder extends FactoryBuilderSupport {
    public CompositeBuilder(@Nonnull BuilderCustomizer[] customizers) {
        super(false);
        requireNonNull(customizers, "Argument 'customizers' must not be null");
        for (int i = 0; i < customizers.length; i++) {
            BuilderCustomizer customizer = customizers[customizers.length - 1 - i];
            doRegisterVariables(customizer);
            doRegisterFactories(customizer);
            doRegisterMethods(customizer);
            doRegisterProps(customizer);
        }

        final List<Closure> methodMissingDelegates = new ArrayList<>();
        final List<Closure> propertyMissingDelegates = new ArrayList<>();
        for (BuilderCustomizer customizer : customizers) {
            doRegisterAttributeDelegates(customizer);
            doRegisterPreInstantiateDelegates(customizer);
            doRegisterPostInstantiateDelegates(customizer);
            doRegisterPostNodeCompletionDelegates(customizer);
            doDisposalClosures(customizer);
            if (customizer.getMethodMissingDelegate() != null) {
                methodMissingDelegates.add(customizer.getMethodMissingDelegate());
            }
            if (customizer.getPropertyMissingDelegate() != null) {
                propertyMissingDelegates.add(customizer.getPropertyMissingDelegate());
            }
        }

        final Closure originalMethodMissingDelegate = getMethodMissingDelegate();
        setMethodMissingDelegate(new Closure<Object>(this) {
            private static final long serialVersionUID = -6901410680736336645L;

            protected Object doCall(Object[] args) {
                String methodName = String.valueOf(args[0]);
                for (Closure methodMissingDelegate : methodMissingDelegates) {
                    try {
                        return methodMissingDelegate.call(args);
                    } catch (MissingMethodException mme) {
                        if (!methodName.equals(mme.getMethod())) throw mme;
                    }
                }

                if (originalMethodMissingDelegate != null) {
                    try {
                        return originalMethodMissingDelegate.call(args);
                    } catch (MissingMethodException mme) {
                        if (!methodName.equals(mme.getMethod())) throw mme;
                    }
                }

                Object[] argsCopy = new Object[args.length - 1];
                System.arraycopy(args, 1, argsCopy, 0, argsCopy.length);
                throw new MissingMethodException(methodName, CompositeBuilder.class, argsCopy);
            }
        });

        final Closure originalPropertyMissingDelegate = getMethodMissingDelegate();
        setPropertyMissingDelegate(new Closure<Object>(this) {
            private static final long serialVersionUID = 1055591497264374109L;

            protected Object doCall(Object[] args) {
                String propertyName = String.valueOf(args[0]);
                for (Closure propertyMissingDelegate : propertyMissingDelegates) {
                    try {
                        return propertyMissingDelegate.call(args);
                    } catch (MissingMethodException mme) {
                        if (!propertyName.equals(mme.getMethod()))
                            throw mme;
                    }
                }

                if (originalPropertyMissingDelegate != null) {
                    try {
                        return originalPropertyMissingDelegate.call(args);
                    } catch (MissingMethodException mme) {
                        if (!propertyName.equals(mme.getMethod()))
                            throw mme;
                    }
                }

                throw new MissingPropertyException(propertyName, CompositeBuilder.class);
            }
        });
    }

    private void doRegisterVariables(@Nonnull BuilderCustomizer customizer) {
        for (Map.Entry<String, Object> entry : customizer.getVariables().entrySet()) {
            setVariable(entry.getKey(), entry.getValue());
        }
    }

    private void doRegisterFactories(@Nonnull BuilderCustomizer customizer) {
        for (Map.Entry<String, Factory> entry : customizer.getFactories().entrySet()) {
            registerFactory(entry.getKey(), entry.getValue());
        }
    }

    private void doRegisterMethods(@Nonnull BuilderCustomizer customizer) {
        for (Map.Entry<String, Closure> e : customizer.getMethods().entrySet()) {
            registerExplicitMethod(e.getKey(), e.getValue());
        }
    }

    private void doRegisterProps(@Nonnull BuilderCustomizer customizer) {
        for (Map.Entry<String, Closure[]> entry : customizer.getProps().entrySet()) {
            Closure[] accessors = entry.getValue();
            Closure getter = accessors[0];
            Closure setter = accessors[1];
            registerExplicitProperty(entry.getKey(), getter, setter);
        }
    }

    private void doRegisterAttributeDelegates(@Nonnull BuilderCustomizer customizer) {
        for (Closure c : customizer.getAttributeDelegates()) {
            addAttributeDelegate(c);
        }
    }

    private void doRegisterPreInstantiateDelegates(@Nonnull BuilderCustomizer customizer) {
        for (Closure c : customizer.getPreInstantiateDelegates()) {
            addPreInstantiateDelegate(c);
        }
    }

    private void doRegisterPostInstantiateDelegates(@Nonnull BuilderCustomizer customizer) {
        for (Closure c : customizer.getPostInstantiateDelegates()) {
            addPostInstantiateDelegate(c);
        }
    }

    private void doRegisterPostNodeCompletionDelegates(@Nonnull BuilderCustomizer customizer) {
        for (Closure c : customizer.getPostNodeCompletionDelegates()) {
            addPostNodeCompletionDelegate(c);
        }
    }

    private void doDisposalClosures(@Nonnull BuilderCustomizer customizer) {
        for (Closure c : customizer.getDisposalClosures()) {
            addDisposalClosure(c);
        }
    }
}
