/*
 * Copyright 2011-2014 the original author or authors.
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

package griffon.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.injection.Injector;
import griffon.exceptions.BeanInstantiationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @param <T> the type of artifact to create, i.e, {@code com.acme.SampleController}.
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonArtifactInjector<T extends GriffonArtifact> implements Provider<T> {
    private final Class<T> klass;
    private GriffonClass griffonClass;

    @Inject
    private GriffonApplication application;

    @Inject
    private Injector injector;

    public GriffonArtifactInjector(@Nonnull Class<T> klass) {
        this.klass = klass;
    }

    @Override
    public T get() {
        try {
            GriffonClass griffonClass = resolveGriffonClass();
            Constructor<T> constructor = klass.getDeclaredConstructor(GriffonApplication.class, GriffonClass.class);
            T t = constructor.newInstance(application, griffonClass);
            injector.injectMembers(t);
            return t;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new BeanInstantiationException("Could not create an instance of " + klass, e);
        } catch (InvocationTargetException e) {
            throw new BeanInstantiationException("Could not create an instance of " + klass, e.getTargetException());
        }
    }

    @Nullable
    private GriffonClass resolveGriffonClass() {
        if (griffonClass == null) {
            griffonClass = application.getArtifactManager().findGriffonClass(klass);
        }
        return griffonClass;
    }
}
