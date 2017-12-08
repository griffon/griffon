package griffon.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonClassUtils.isGetterMethod;
import static griffon.util.GriffonClassUtils.isSetterMethod;
import static griffon.util.GriffonNameUtils.getPropertyName;
import static java.util.Collections.unmodifiableMap;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PropertyDescriptorResolver {
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> DESCRIPTORS = new ConcurrentHashMap<>();

    public static Map<String, PropertyDescriptor> findDescriptors(Class<?> klass) {
        return DESCRIPTORS.computeIfAbsent(klass, PropertyDescriptorResolver::fetchPropertyMetadata);
    }

    public static PropertyDescriptor findDescriptorFor(Class<?> klass, String propertyName) {
        Map<String, PropertyDescriptor> metadata = DESCRIPTORS.computeIfAbsent(klass, PropertyDescriptorResolver::fetchPropertyMetadata);
        return metadata.get(propertyName);
    }

    private static Map<String, PropertyDescriptor> fetchPropertyMetadata(Class<?> klass) {
        Map<String, PropertyDescriptor> metadata = new HashMap<>();

        Map<String, PropertyDescriptorBuilder> builders = new HashMap<>();
        for (Method method : klass.getMethods()) {
            if (isGetterMethod(method)) {
                String name = getPropertyName(method);
                Class<?> type = method.getReturnType();
                PropertyDescriptorBuilder builder = builders.computeIfAbsent(name, PropertyDescriptorBuilder::new);
                if (builder.getType() != null && !type.equals(builder.getType())) {
                    builders.remove(name);
                }
                builder.withType(type);
                builder.withReadMethod(method);
            } else if (isSetterMethod(method)) {
                String name = getPropertyName(method);
                Class<?> type = method.getParameters()[0].getType();
                PropertyDescriptorBuilder builder = builders.computeIfAbsent(name, PropertyDescriptorBuilder::new);
                if (builder.getType() != null && !type.equals(builder.getType())) {
                    builders.remove(name);
                }
                builder.withType(type);
                builder.withWriteMethod(method);
            }
        }

        for (PropertyDescriptorBuilder builder : builders.values()) {
            metadata.put(builder.getName(), builder.build());
        }

        return unmodifiableMap(metadata);
    }

    private static class PropertyDescriptorBuilder {
        private final String name;
        private Class<?> type;
        private Method readMethod;
        private Method writeMethod;

        public PropertyDescriptorBuilder(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public PropertyDescriptorBuilder withType(Class<?> type) {
            this.type = type;
            return this;
        }

        public PropertyDescriptorBuilder withReadMethod(Method readMethod) {
            this.readMethod = readMethod;
            return this;
        }

        public PropertyDescriptorBuilder withWriteMethod(Method writeMethod) {
            this.writeMethod = writeMethod;
            return this;
        }

        public PropertyDescriptor build() {
            return new PropertyDescriptor(name, type, readMethod, writeMethod);
        }
    }
}
