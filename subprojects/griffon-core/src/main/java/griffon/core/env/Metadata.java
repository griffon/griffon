/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.core.env;

import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents the application Metadata and loading mechanics
 *
 * @author Graeme Rocher (Grails 1.1)
 */

public class Metadata extends Properties {
    private static final long serialVersionUID = 5403698530204420558L;

    public static final String FILE = "application.properties";
    public static final String APPLICATION_VERSION = "application.version";
    public static final String APPLICATION_NAME = "application.name";
    public static final String APPLICATION_GRIFFON_VERSION = "application.griffon.version";
    public static final String GRIFFON_START_DIR = "griffon.start.dir";
    public static final String GRIFFON_WORKING_DIR = "griffon.working.dir";

    private static final Pattern SKIP_PATTERN = Pattern.compile("^.*/griffon-.*.jar!/application.properties$");
    private static Reference<Metadata> metadata = new SoftReference<>(new Metadata());

    private boolean initialized;
    private File metadataFile;
    private boolean dirty;

    private Metadata() {
        super();
    }

    private Metadata(File f) {
        this.metadataFile = f;
    }

    /**
     * Resets the current state of the Metadata so it is re-read
     */
    public static void reset() {
        Metadata m = metadata.get();
        if (m != null) {
            m.clear();
            m.initialized = false;
            m.dirty = false;
        }
    }

    /**
     * @return Returns the metadata for the current application
     */
    public static Metadata getCurrent() {
        Metadata m = metadata.get();
        if (m == null) {
            metadata = new SoftReference<>(new Metadata());
            m = metadata.get();
        }
        if (!m.initialized) {
            InputStream input = null;
            try {
                // GRIFFON-108 enable reading metadata from a local file IF AND ONLY IF
                // current environment == 'dev'.
                // must read environment directly from System.properties to avoid a
                // circular problem
                // if(Environment.getEnvironment(System.getProperty(Environment.KEY)) == Environment.DEVELOPMENT) {
                //     input = new FileInputStream(FILE);
                // }

                // GRIFFON-255 there may be multiple versions of "application.properties" in the classpath
                // due to addon packaging. Avoid any URLS that look like plugin dirs or addon jars
                input = fetchApplicationProperties(Metadata.class.getClassLoader());

                if (input != null) {
                    m.load(input);
                }
            } catch (Exception e) {
                throw new RuntimeException("Cannot load application metadata:" + e.getMessage(), e);
            } finally {
                closeQuietly(input);
                m.initialized = true;
            }
        }

        return m;
    }

    /**
     * Loads a Metadata instance from a Reader
     *
     * @param inputStream The InputStream
     * @return a Metadata instance
     */
    public static Metadata getInstance(InputStream inputStream) {
        Metadata m = new Metadata();
        metadata = new FinalReference<>(m);

        try {
            m.load(inputStream);
            m.initialized = true;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load application metadata:" + e.getMessage(), e);
        }
        return m;
    }

    /**
     * Loads and returns a new Metadata object for the given File
     *
     * @param file The File
     * @return A Metadata object
     */
    public static Metadata getInstance(File file) {
        Metadata m = new Metadata(file);
        metadata = new FinalReference<>(m);

        if (file != null && file.exists()) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(file);
                m.load(input);
                m.initialized = true;
            } catch (Exception e) {
                throw new RuntimeException("Cannot load application metadata:" + e.getMessage(), e);
            } finally {
                closeQuietly(input);
            }
        }
        return m;
    }

    /**
     * Reloads the application metadata
     *
     * @return The metadata object
     */
    public static Metadata reload() {
        File f = getCurrent().metadataFile;

        if (f != null) {
            return getInstance(f);
        }
        return getCurrent();
    }

    /**
     * @return The application version
     */
    public String getApplicationVersion() {
        return (String) get(APPLICATION_VERSION);
    }

    /**
     * @return The Griffon version used to build the application
     */
    public String getGriffonVersion() {
        return (String) get(APPLICATION_GRIFFON_VERSION);
    }

    /**
     * @return The environment the application expects to run in
     */
    public String getEnvironment() {
        return (String) get(Environment.KEY);
    }

    /**
     * @return The application name
     */
    public String getApplicationName() {
        return (String) get(APPLICATION_NAME);
    }

    /**
     * Obtains a map (name->version) of installed plugins specified in the project metadata
     *
     * @return A map of installed plugins
     */
    public Map<String, String> getInstalledPlugins() {
        Map<String, String> newMap = new LinkedHashMap<>();

        for (Map.Entry<Object, Object> entry : entrySet()) {
            String key = entry.getKey().toString();
            Object val = entry.getValue();
            if (key.startsWith("plugins.") && val != null) {
                newMap.put(key.substring(8), val.toString());
            }
        }
        return newMap;
    }

    public Map<String, String> getArchetype() {
        Map<String, String> newMap = new LinkedHashMap<>();

        for (Map.Entry<Object, Object> entry : entrySet()) {
            String key = entry.getKey().toString();
            Object val = entry.getValue();
            if (key.startsWith("archetype.") && val != null) {
                newMap.put("name", key.substring(10));
                newMap.put("version", val.toString());
                break;
            }
        }
        return newMap;
    }

    /**
     * Returns the application's starting directory.<p>
     * The value comes from the System property 'griffon.start.dir'
     * if set. Result may be null.
     *
     * @return The application start directory path
     */
    public String getGriffonStartDir() {
        String griffonStartDir = (String) get(GRIFFON_START_DIR);
        if (griffonStartDir == null) {
            griffonStartDir = System.getProperty(GRIFFON_START_DIR);
            if (griffonStartDir != null && griffonStartDir.length() > 1 &&
                griffonStartDir.startsWith("\"") && griffonStartDir.endsWith("\"")) {
                // normalize without double quotes
                griffonStartDir = griffonStartDir.substring(1, griffonStartDir.length() - 1);
                System.setProperty(GRIFFON_START_DIR, griffonStartDir);
            }
            if (griffonStartDir != null && griffonStartDir.length() > 1 &&
                griffonStartDir.startsWith("'") && griffonStartDir.endsWith("'")) {
                // normalize without single quotes
                griffonStartDir = griffonStartDir.substring(1, griffonStartDir.length() - 1);
                System.setProperty(GRIFFON_START_DIR, griffonStartDir);
            }
            if (griffonStartDir != null) {
                put(GRIFFON_START_DIR, griffonStartDir);
            }
        }
        return griffonStartDir;
    }

    /**
     * Returns ia non-null value for the application's starting directory.<p>
     * the path to new File(".") if that path is writable, returns
     * the value of 'user.dir' otherwise.
     *
     * @return The application start directory path
     */
    public String getGriffonStartDirSafe() {
        String griffonStartDir = getGriffonStartDir();
        if (griffonStartDir == null) {
            File path = new File(".");
            if (path.canWrite()) {
                return path.getAbsolutePath();
            }
            return System.getProperty("user.dir");
        }
        return griffonStartDir;
    }

    /**
     * @return The application working directory
     */
    public File getGriffonWorkingDir() {
        String griffonWorkingDir = (String) get(GRIFFON_WORKING_DIR);
        if (griffonWorkingDir == null) {
            String griffonStartDir = getGriffonStartDirSafe();
            File workDir = new File(griffonStartDir);
            if (workDir.canWrite()) {
                put(GRIFFON_WORKING_DIR, griffonStartDir);
                return workDir;
            } else {
                try {
                    File temp = File.createTempFile("griffon", ".tmp");
                    temp.deleteOnExit();
                    workDir = new File(temp.getParent(), getApplicationName());
                    put(GRIFFON_WORKING_DIR, workDir.getAbsolutePath());
                    return workDir;
                } catch (IOException ioe) {
                    // ignore ??
                    // should not happen
                }
            }
        }

        return new File(griffonWorkingDir);
    }

    /**
     * Saves the current state of the Metadata object
     */
    public void persist() {
        if (dirty && metadataFile != null) {
            if (!isFileDirty(metadataFile)) {
                dirty = false;
                return;
            }

            FileOutputStream out = null;

            try {
                out = new FileOutputStream(metadataFile);
                store(out, "Griffon Metadata file");
                dirty = false;
            } catch (Exception e) {
                throw new RuntimeException("Error persisting metadata to file [" + metadataFile + "]: " + e.getMessage(), e);
            } finally {
                closeQuietly(out);
            }
        }
    }

    private boolean isFileDirty(File file) {
        InputStream in = null;

        try {
            Properties other = new Properties();
            in = new FileInputStream(metadataFile);
            other.load(in);

            for (Map.Entry<Object, Object> entry : other.entrySet()) {
                if (!containsKey(entry.getKey()) || !get(entry.getKey()).equals(entry.getValue())) {
                    return true;
                }
            }

            for (Map.Entry<Object, Object> entry : entrySet()) {
                if (!other.containsKey(entry.getKey()) || !other.get(entry.getKey()).equals(entry.getValue())) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading metadata to file [" + file + "]: " + e.getMessage(), e);
        } finally {
            closeQuietly(in);
        }
        return false;
    }

    /**
     * @return Returns true if these properties have not changed since they were loaded
     */
    public boolean propertiesHaveNotChanged() {
        return dirty;
    }

    @Override
    public synchronized Object remove(Object o) {
        boolean hasKey = containsKey(o);
        Object value = super.remove(o);
        dirty = dirty || hasKey;
        return value;
    }

    @Override
    public synchronized Object setProperty(String name, String value) {
        return put(name, value);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        if (key instanceof CharSequence) key = key.toString();
        if (value instanceof CharSequence) value = value.toString();
        if (containsKey(key)) {
            Object oldValue = get(key);
            if (oldValue instanceof CharSequence) {
                oldValue = oldValue.toString();
            }
            dirty = dirty || (oldValue != null && !oldValue.equals(value));
        } else {
            dirty = true;
        }
        return super.put(key, value);
    }

    @Override
    public synchronized void putAll(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Overrides, called by the store method.
     */
    @SuppressWarnings("unchecked")
    public synchronized Enumeration keys() {
        Enumeration keysEnum = super.keys();
        Vector keyList = new Vector<>();
        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }

    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {
                // ignored
            }
        }
    }

    static class FinalReference<T> extends SoftReference<T> {
        private T ref;

        public FinalReference(T t) {
            super(t);
            this.ref = t;
        }

        @Override
        public T get() {
            return ref;
        }
    }

    private static InputStream fetchApplicationProperties(ClassLoader classLoader) {
        Enumeration<URL> urls;

        try {
            urls = classLoader.getResources(FILE);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        while (urls.hasMoreElements()) {
            try {
                URL url = urls.nextElement();
                if (SKIP_PATTERN.matcher(url.toString()).matches()) continue;
                return url.openStream();
            } catch (IOException ioe) {
                // skip
            }
        }

        return null;
    }
}
