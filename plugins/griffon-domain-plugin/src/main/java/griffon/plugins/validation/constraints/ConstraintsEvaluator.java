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
package griffon.plugins.validation.constraints;

import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainClassProperty;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Evaluates and returns constraints.
 *
 * @author Graeme Rocher (Grails 2.0)
 */
public interface ConstraintsEvaluator {
    String PROPERTY_NAME = "CONSTRAINTS";

    /**
     * The default constraints to use
     *
     * @return A map of default constraints
     */
    // Map<String, Object> getDefaultConstraints();

    /**
     * Evaluate constraints for the given class
     *
     * @param cls The class to evaluate constraints for
     * @return A map of constrained properties
     */
    @Nonnull
    Map<String, ConstrainedProperty> evaluate(@Nonnull Class<?> cls);

    /**
     * Evaluate constraints for the given class
     *
     * @param cls The class to evaluate constraints for
     * @return A map of constrained properties
     */
    @Nonnull
    Map<String, ConstrainedProperty> evaluate(@Nonnull GriffonDomainClass<?> cls);

    /**
     * Evaluate constraints for the given object and properties
     *
     * @param object     The object
     * @param properties The domain class properties
     * @return A map of constraints
     */
    @Nonnull
    Map<String, ConstrainedProperty> evaluate(@Nonnull Object object, @Nonnull GriffonDomainClassProperty[] properties);

    /**
     * Evaluate constraints for the given Class and properties
     *
     * @param cls        The object
     * @param properties The domain class properties
     * @return A map of constraints
     */
    @Nonnull
    Map<String, ConstrainedProperty> evaluate(@Nonnull Class<?> cls, @Nonnull GriffonDomainClassProperty[] properties);
}
