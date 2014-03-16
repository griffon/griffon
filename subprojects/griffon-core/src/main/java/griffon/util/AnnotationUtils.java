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
package griffon.util;

import griffon.inject.BindTo;
import griffon.inject.DependsOn;
import griffon.inject.Typed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Qualifier;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.*;

import static griffon.util.GriffonNameUtils.getLogicalPropertyName;
import static griffon.util.GriffonNameUtils.getPropertyName;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class AnnotationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationUtils.class);

    private AnnotationUtils() {

    }

    @Nonnull
    public static List<Annotation> harvestQualifiers(@Nonnull Class<?> klass) {
        requireNonNull(klass, "Argument 'class' must not be null");
        List<Annotation> list = new ArrayList<>();
        Annotation[] annotations = klass.getAnnotations();
        for (Annotation annotation : annotations) {
            if (AnnotationUtils.isAnnotatedWith(annotation, Qualifier.class)) {
                // special case @BindTo is only used during tests
                if (BindTo.class.isAssignableFrom(annotation.getClass())) {
                    continue;
                }
                // special case for @Named
                if (Named.class.isAssignableFrom(annotation.getClass())) {
                    Named named = (Named) annotation;
                    if (isBlank(named.value())) {
                        list.add(named(getPropertyName(klass)));
                        continue;
                    }
                }
                list.add(annotation);
            }
        }
        return list;
    }

    public static boolean isAnnotatedWith(@Nonnull Object instance, @Nonnull Class<? extends Annotation> annotationType) {
        return isAnnotatedWith(requireNonNull(instance, "Argument 'instance' must not be null").getClass(), annotationType);
    }

    public static boolean isAnnotatedWith(@Nonnull Class<?> clazz, @Nonnull Class<? extends Annotation> annotationType) {
        requireNonNull(clazz, "Argument 'class' must not be null");
        requireNonNull(annotationType, "Argument 'annotationType' must not be null");

        //noinspection ConstantConditions
        while (clazz != null) {
            for (Annotation annotation : clazz.getAnnotations()) {
                if (annotationType.equals(annotation.annotationType())) {
                    return true;
                }
            }
            for (Class<?> iface : clazz.getInterfaces()) {
                if (isAnnotatedWith(iface, annotationType)) {
                    return true;
                }
            }

            clazz = clazz.getSuperclass();
        }
        return false;
    }

    @Nonnull
    public static <T> T requireAnnotation(@Nonnull T instance, @Nonnull Class<? extends Annotation> annotationType) {
        if (!isAnnotatedWith(instance, annotationType)) {
            throw new IllegalArgumentException("Instance of " + instance.getClass() + " is not annotated with " + annotationType.getName());
        }
        return instance;
    }

    @Nonnull
    public static <T> Class<T> requireAnnotation(@Nonnull Class<T> klass, @Nonnull Class<? extends Annotation> annotationType) {
        if (!isAnnotatedWith(klass, annotationType)) {
            throw new IllegalArgumentException("Class " + klass.getName() + " is not annotated with " + annotationType.getName());
        }
        return klass;
    }

    @Nonnull
    public static String[] getDependsOn(@Nonnull Object instance) {
        DependsOn dependsOn = instance.getClass().getAnnotation(DependsOn.class);
        return dependsOn != null ? dependsOn.value() : new String[0];
    }

    @Nonnull
    public static String nameFor(@Nonnull Object instance, @Nonnull String suffix) {
        Named annotation = instance.getClass().getAnnotation(Named.class);
        if (annotation != null && !isBlank(annotation.value())) {
            return annotation.value();
        } else {
            return getLogicalPropertyName(instance.getClass().getName(), suffix);
        }
    }

    @Nonnull
    public static <T> Map<String, T> mapInstancesByName(@Nonnull Collection<T> instances, @Nonnull String suffix) {
        Map<String, T> map = new LinkedHashMap<>();

        for (T instance : instances) {
            map.put(nameFor(instance, suffix), instance);
        }

        return map;
    }

    @Nonnull
    public static <T> Map<String, T> sortByDependencies(@Nonnull Collection<T> instances, @Nonnull String suffix, @Nonnull String type) {
        return sortByDependencies(instances, suffix, type, Collections.<String>emptyList());
    }

    @Nonnull
    public static <T> Map<String, T> sortByDependencies(@Nonnull Collection<T> instances, @Nonnull String suffix, @Nonnull String type, @Nonnull List<String> order) {
        requireNonNull(instances, "Argument 'instances' must not be null");
        requireNonNull(suffix, "Argument 'suffix' must not be null");
        requireNonNull(type, "Argument 'type' must not be null");
        requireNonNull(order, "Argument 'order' must not be null");

        Map<String, T> instancesByName = mapInstancesByName(instances, suffix);

        Map<String, T> map = new LinkedHashMap<>();
        map.putAll(instancesByName);

        if (!order.isEmpty()) {
            Map<String, T> tmp1 = new LinkedHashMap<>(instancesByName);
            Map<String, T> tmp2 = new LinkedHashMap<>();
            //noinspection ConstantConditions
            for (String name : order) {
                if (tmp1.containsKey(name)) {
                    tmp2.put(name, tmp1.remove(name));
                }
            }
            tmp2.putAll(tmp1);
            map.clear();
            map.putAll(tmp2);
        }

        List<T> sorted = new ArrayList<>();
        Set<String> instanceDeps = new LinkedHashSet<>();

        while (!map.isEmpty()) {
            int processed = 0;

            LOG.debug("Current {} order is {}", type, instancesByName.keySet());

            for (Iterator<Map.Entry<String, T>> iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, T> entry = iter.next();
                String instanceName = entry.getKey();
                String[] dependsOn = getDependsOn(entry.getValue());

                LOG.trace("Processing {} '{}'", type, instanceName);
                LOG.trace("  depends on '{}'", Arrays.toString(dependsOn));

                if (dependsOn.length != 0) {
                    LOG.trace("  checking {} '{}' dependencies ({})", type, instanceName, dependsOn.length);

                    boolean failedDep = false;
                    for (String dep : dependsOn) {
                        LOG.trace("  checking {} '{}' dependencies: ", type, instanceName, dep);
                        if (!instanceDeps.contains(dep)) {
                            // dep not in the list yet, we need to skip adding this to the list for now
                            LOG.trace("  skipped {} '{}', since dependency '{}' not yet added", type, instanceName, dep);
                            failedDep = true;
                            break;
                        } else {
                            LOG.trace("  {} '{}' dependency '{}' already added", type, instanceName, dep);
                        }
                    }

                    if (failedDep) {
                        // move on to next dependency
                        continue;
                    }
                }

                LOG.trace("  adding {} '{}', since all dependencies have been added", type, instanceName);
                sorted.add(entry.getValue());
                instanceDeps.add(instanceName);
                iter.remove();
                processed++;
            }

            if (processed == 0) {
                // we have a cyclical dependency, warn the user and load in the order they appeared originally
                LOG.warn("  unresolved {} dependencies detected", type);
                LOG.warn("  continuing with original {} order", type);
                for (Map.Entry<String, T> entry : map.entrySet()) {
                    String instanceName = entry.getKey();
                    String[] dependsOn = getDependsOn(entry.getValue());

                    // display this as a cyclical dep
                    LOG.warn("  {} {} ", type, instanceName);
                    if (dependsOn.length != 0) {
                        for (String dep : dependsOn) {
                            LOG.warn("    depends on {}", dep);
                        }
                    } else {
                        // we should only have items left in the list with deps, so this should never happen
                        // but a wise man once said...check for true, false and otherwise...just in case
                        LOG.warn("  problem while resolving dependencies.");
                        LOG.warn("  unable to resolve dependency hierarchy.");
                    }
                }
                break;
                // if we have processed all the instances, we are done
            } else if (sorted.size() == instancesByName.size()) {
                LOG.trace("{} dependency ordering complete", type);
                break;
            }
        }

        instancesByName = mapInstancesByName(sorted, suffix);
        LOG.debug("computed {} order is {}", type, instancesByName.keySet());

        return instancesByName;
    }

    @Nonnull
    public static Named named(@Nonnull String name) {
        return new NamedImpl(requireNonNull(name, "Argument 'name' must not be null"));
    }

    @Nonnull
    public static Typed typed(@Nonnull Class<?> clazz) {
        return new TypedImpl(requireNonNull(clazz, "Argument 'clazz' must not be null"));
    }

    @Nonnull
    public static BindTo bindto(@Nonnull Class<?> clazz) {
        return new BindToImpl(requireNonNull(clazz, "Argument 'clazz' must not be null"));
    }

    /**
     * @author Andres Almiray
     * @since 2.0.0
     */
    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class NamedImpl implements Named, Serializable {
        private final String value;

        public NamedImpl(String value) {
            this.value = requireNonNull(value, "value");
        }

        public String value() {
            return this.value;
        }

        public int hashCode() {
            // This is specified in java.lang.Annotation.
            return (127 * "value".hashCode()) ^ value.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof Named)) {
                return false;
            }

            Named other = (Named) o;
            return value.equals(other.value());
        }

        public String toString() {
            return "@" + Named.class.getName() + "(value=" + value + ")";
        }

        public Class<? extends Annotation> annotationType() {
            return Named.class;
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * @author Andres Almiray
     * @since 2.0.0
     */
    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class TypedImpl implements Typed, Serializable {
        private final Class<?> value;

        public TypedImpl(Class<?> value) {
            this.value = requireNonNull(value, "value");
        }

        public Class<?> value() {
            return this.value;
        }

        public int hashCode() {
            // This is specified in java.lang.Annotation.
            return (127 * "value".hashCode()) ^ value.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof Typed)) {
                return false;
            }

            Typed other = (Typed) o;
            return value.equals(other.value());
        }

        public String toString() {
            return "@" + Typed.class.getName() + "(value=" + value + ")";
        }

        public Class<? extends Annotation> annotationType() {
            return Typed.class;
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * @author Andres Almiray
     * @since 2.0.0
     */
    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class BindToImpl implements BindTo, Serializable {
        private final Class<?> value;

        public BindToImpl(Class<?> value) {
            this.value = requireNonNull(value, "value");
        }

        public Class<?> value() {
            return this.value;
        }

        public int hashCode() {
            // This is specified in java.lang.Annotation.
            return (127 * "value".hashCode()) ^ value.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof BindTo)) {
                return false;
            }

            BindTo other = (BindTo) o;
            return value.equals(other.value());
        }

        public String toString() {
            return "@" + BindTo.class.getName() + "(value=" + value + ")";
        }

        public Class<? extends Annotation> annotationType() {
            return BindTo.class;
        }

        private static final long serialVersionUID = 0;
    }
}
