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
package org.codehaus.griffon.runtime.injection;

import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.event.EventRouter;
import griffon.inject.Prototype;
import griffon.util.ServiceLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.CglibSubclassingInstantiationStrategy;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static griffon.util.GriffonNameUtils.getPropertyName;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.ServiceLoaderUtils.load;
import static java.util.Arrays.asList;
import static org.codehaus.griffon.runtime.injection.BeanUtils.asAutowireCandidateQualifiers;
import static org.codehaus.griffon.runtime.injection.BeanUtils.toAutowireCandidateQualifiers;

/**
 * Code lifted from {@code juzu.impl.inject.spi.spring.SpringInjector} original by Julien Viet
 *
 * @author Andres Almiray
 * @since 2.4.0
 */
public class SpringContext {
    public static final String SPRING_SCANNED_PACKAGES_KEY = "griffon.spring.scanned.packages";

    private static final Logger LOG = LoggerFactory.getLogger(SpringContext.class);
    private static final String CONFIGURATION_CLASS_PATH = "META-INF/services" + Configuration.class.getName();

    private final Map<String, Object> instances = new LinkedHashMap<>();
    private final Map<String, AbstractSpringBean> beans = new LinkedHashMap<>();
    private final GriffonApplication application;

    public SpringContext(@Nonnull GriffonApplication application) {
        this.application = application;
    }

    public Map<String, Object> getInstances() {
        return instances;
    }

    private <T> SpringContext declareBean(AbstractSpringBean<T> bean) {
        beans.put(bean.getName(), bean);
        return this;
    }

    @Nonnull
    public ApplicationContext create() {
        return create(null);
    }

    @Nonnull
    public ApplicationContext create(@Nullable ApplicationContext parent) {
        GriffonApplicationContext applicationContext = null;

        String scannedPackages = System.getProperty(SPRING_SCANNED_PACKAGES_KEY);
        if (!isBlank(scannedPackages)) {
            applicationContext = new GriffonApplicationContext(scannedPackages.split(","));
        } else {
            // configuration classes?
            final List<Class<?>> classes = new ArrayList<>();
            load(getClass().getClassLoader(), CONFIGURATION_CLASS_PATH, new ServiceLoaderUtils.PathFilter() {
                @Override
                public boolean accept(@Nonnull String path) {
                    return true;
                }
            }, new ServiceLoaderUtils.ResourceProcessor() {
                @Override
                public void process(@Nonnull ClassLoader classLoader, @Nonnull String line) {
                    line = line.trim();
                    try {
                        classes.add(classLoader.loadClass(line));
                    } catch (ClassNotFoundException e) {
                        LOG.warn("'" + line + "' could not be resolved as a Class");
                    }
                }
            });

            if (!classes.isEmpty()) {
                applicationContext = new GriffonApplicationContext(classes.toArray(new Class[classes.size()]));
            } else {
                applicationContext = new GriffonApplicationContext();
            }
        }

        if (parent != null) {
            applicationContext.setParent(parent);
        }

        final DefaultListableBeanFactory factory = applicationContext.getDefaultListableBeanFactory();

        factory.setBeanClassLoader(getClass().getClassLoader());
        factory.setInstantiationStrategy(new SingletonInstantiationStrategy(new CglibSubclassingInstantiationStrategy(), instances));

        for (Map.Entry<String, AbstractSpringBean> entry : beans.entrySet()) {
            AbstractSpringBean bean = entry.getValue();
            String name = entry.getKey();
            bean.configure(name, this, factory);
        }

        AutowiredAnnotationBeanPostProcessor beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        beanPostProcessor.setAutowiredAnnotationType(Inject.class);
        beanPostProcessor.setBeanFactory(factory);
        factory.addBeanPostProcessor(beanPostProcessor);

        factory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                System.out.println("Is "+bean+" an artifact? " + (bean instanceof GriffonArtifact));
                /*if (bean instanceof GriffonArtifact) {
                    factory.getBean(EventRouter.class).publishEvent(
                        ApplicationEvent.NEW_INSTANCE.getName(),
                        asList(bean.getClass(), bean)
                    );
                }*/
                return bean;
            }
        });

        CommonAnnotationBeanPostProcessor commonAnnotationBeanProcessor = new CommonAnnotationBeanPostProcessor();
        factory.addBeanPostProcessor(commonAnnotationBeanProcessor);

        Set<Class<?>> cqt = new HashSet<>();
        cqt.add(Named.class);
        CustomAutowireConfigurer configurer = new CustomAutowireConfigurer();
        configurer.setCustomQualifierTypes(cqt);
        QualifierAnnotationAutowireCandidateResolver customResolver = new QualifierAnnotationAutowireCandidateResolver();
        factory.setAutowireCandidateResolver(customResolver);
        configurer.postProcessBeanFactory(factory);

        applicationContext.init();
        System.out.println("************************************");
        String[] names = applicationContext.getBeanDefinitionNames();
        Arrays.sort(names);
        for (String n : names) {
            System.out.println(n);
        }
        System.out.println("************************************");

        return applicationContext;
    }

    public <T> void declareBean(
        @Nonnull Class<T> source,
        @Nullable Class<? extends Annotation> classifierType,
        @Nullable Annotation classifier,
        @Nonnull Class<? extends T> target,
        boolean singleton) {
        List<AutowireCandidateQualifier> autowireQualifiers = pickQualifier(classifierType, classifier);
        String scope = scopeFor(singleton);
        LOG.trace("Declaring bean {} to {} with qualifier {} on {} scope", source, target, qualifier(classifierType, classifier), scope);
        declareBean(new DeclaredBean(source, target, scope, autowireQualifiers));
    }

    public <T> void bindBean(
        @Nonnull Class<T> source,
        @Nullable Class<? extends Annotation> classifierType,
        @Nullable Annotation classifier,
        @Nonnull T instance) {
        List<AutowireCandidateQualifier> autowireQualifiers = pickQualifier(classifierType, classifier);
        LOG.trace("Declaring singleton bean {} with instance {} and qualifiers {}", source, instance, qualifier(classifierType, classifier));
        declareBean(new SingletonBean<>(source, instance, autowireQualifiers));
    }

    public <T> void declareProvider(
        @Nonnull Class<T> source,
        @Nullable Class<? extends Annotation> classifierType,
        @Nullable Annotation classifier,
        @Nonnull Class<? extends Provider<T>> providerType,
        boolean singleton) {
        List<AutowireCandidateQualifier> autowireQualifiers = pickQualifier(classifierType, classifier);
        String scope = scopeFor(singleton);
        LOG.trace("Declaring provider {} for type {} with qualifier {} on {} scope", providerType, source, qualifier(classifierType, classifier), scope);
        declareBean(new DeclaredProviderBean<>(source, scope, autowireQualifiers, providerType));
    }

    public <T> void bindProvider(
        @Nonnull Class<T> source,
        @Nullable Class<? extends Annotation> classifierType,
        @Nullable Annotation classifier,
        @Nonnull Provider<T> provider,
        boolean singleton) {
        List<AutowireCandidateQualifier> autowireQualifiers = pickQualifier(classifierType, classifier);
        String scope = scopeFor(singleton);
        LOG.trace("Declaring provider {} for type {} with qualifier {} on {} scope", provider, source, qualifier(classifierType, classifier), scope);
        declareBean(new SingletonProviderBean<>(source, scope, autowireQualifiers, provider));
    }

    private String scopeFor(boolean singleton) {
        return singleton ? getPropertyName(Singleton.class) : getPropertyName(Prototype.class);
    }

    @Nonnull
    private List<AutowireCandidateQualifier> pickQualifier(@Nullable Class<? extends Annotation> annotationType, @Nullable Annotation annotation) {
        if (annotation != null) {
            return asAutowireCandidateQualifiers(annotation);
        } else if (annotationType != null) {
            return toAutowireCandidateQualifiers(annotationType);
        }
        return Collections.emptyList();
    }

    @Nonnull
    private Object qualifier(@Nullable Class<? extends Annotation> annotationType, @Nullable Annotation annotation) {
        if (annotation != null) {
            return annotation;
        } else if (annotationType != null) {
            return annotationType;
        }
        return null;
    }
}
