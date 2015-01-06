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
package griffon.plugins.preferences.persistors;

import griffon.core.GriffonApplication;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.*;
import java.util.Collections;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public class SerializingPreferencesPersistor extends AbstractMapBasedPreferencesPersistor {
    @Inject
    public SerializingPreferencesPersistor(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> read(@Nonnull InputStream inputStream) throws IOException {
        try {
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Object o = ois.readObject();
            ois.close();
            return (Map<String, Object>) o;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @Override
    protected void write(@Nonnull Map<String, Object> map, @Nonnull OutputStream outputStream) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(map);
        oos.close();
    }
}
