/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2021 the original author or authors.
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
package tck.griffon.core.resources;

import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public abstract class ResourceResolverTest {
    protected static final Object[] TWO_ARGS = new Object[]{"apple", "doctor"};
    protected static final String DEFAULT_VALUE = "not found";
    protected static final String KEY_PROVERB = "key.proverb";
    protected static final String KEY_PROVERB_BOGUS = "key.proverb.bogus";
    protected static final String KEY_BOGUS = "key.bogus";
    protected static final String KEY_INTEGER = "key.integer";
    protected static final String PROVERB_FORMAT = "An {0} a day keeps the {1} away";
    protected static final String PROVERB_TEXT = "An apple a day keeps the doctor away";

    protected abstract ResourceResolver resolveResourceResolver();

    private Locale defaultLocale;

    @BeforeEach
    public void setup() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterEach
    public void cleanup() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void verify_resolveResource_withArguments_withLocale() {
        // given:
        ResourceResolver resourceResolver = resolveResourceResolver();

        // expect:
        assertAll(
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB),
                equalTo(PROVERB_FORMAT)),
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB, Locale.getDefault()),
                equalTo(PROVERB_FORMAT)),
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB, TWO_ARGS),
                equalTo(PROVERB_TEXT)),
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB, TWO_ARGS, Locale.getDefault()),
                equalTo(PROVERB_TEXT))
        );
    }

    @Test
    public void verify_resolveResource_withArguments_withLocale_withDefaultValue() {
        // given:
        ResourceResolver resourceResolver = resolveResourceResolver();

        // expect:
        assertAll(
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE)),
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, Locale.getDefault(), DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE)),
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, TWO_ARGS, DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE)),
            () -> assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, TWO_ARGS, Locale.getDefault(), DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE))
        );
    }

    @Test
    public void verify_resolveResourceConverted_withArguments_withLocale() {
        // given:
        ResourceResolver resourceResolver = resolveResourceResolver();

        // expect:
        int value = 42;
        assertAll(
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, Integer.class),
                equalTo(value)),
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, Locale.getDefault(), Integer.class),
                equalTo(value)),
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, TWO_ARGS, Integer.class),
                equalTo(value)),
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, TWO_ARGS, Locale.getDefault(), Integer.class),
                equalTo(value))
        );
    }

    @Test
    public void verify_resolveResourceConverted_withArguments_withLocale_withDefaultValue() {
        // given:
        ResourceResolver resourceResolver = resolveResourceResolver();

        // expect:
        int defaultValue = 21;
        assertAll(
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, defaultValue, Integer.class),
                equalTo(defaultValue)),
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, Locale.getDefault(), defaultValue, Integer.class),
                equalTo(defaultValue)),
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS, defaultValue, Integer.class),
                equalTo(defaultValue)),
            () -> assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS, Locale.getDefault(), defaultValue, Integer.class),
                equalTo(defaultValue))
        );
    }

    @Test
    public void verify_resolveResource_withUnknownKey_withArguments_withLocale() {
        // given:
        ResourceResolver resourceResolver = resolveResourceResolver();

        // expect:
        assertAll(
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResource(KEY_BOGUS)),
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResource(KEY_BOGUS, Locale.getDefault())),
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResource(KEY_BOGUS, TWO_ARGS)),
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResource(KEY_BOGUS, TWO_ARGS, Locale.getDefault()))
        );
    }

    @Test
    public void verify_resolveResourceConverted_withUnknownKey_withArguments_withLocale() {
        // given:
        ResourceResolver resourceResolver = resolveResourceResolver();

        // expect:
        assertAll(
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResourceConverted(KEY_BOGUS, Integer.class)),
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResourceConverted(KEY_BOGUS, Locale.getDefault(), Integer.class)),
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS, Integer.class)),
            () -> assertThrows(NoSuchResourceException.class,
                () -> resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS, Locale.getDefault(), Integer.class))
        );
    }
}
