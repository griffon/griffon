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
package tck.griffon.core.i18n;

import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;
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
public abstract class MessageSourceTest {
    protected static final Object[] TWO_ARGS = new Object[]{"apple", "doctor"};
    protected static final String DEFAULT_VALUE = "not found";
    protected static final String KEY_PROVERB = "key.proverb";
    protected static final String KEY_PROVERB_BOGUS = "key.proverb.bogus";
    protected static final String KEY_BOGUS = "key.bogus";
    protected static final String PROVERB_FORMAT = "An {0} a day keeps the {1} away";
    protected static final String PROVERB_TEXT = "An apple a day keeps the doctor away";
    private Locale defaultLocale;

    protected abstract MessageSource resolveMessageSource();

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
    public void verify_getMessage_withArguments_withLocale() {
        // given:
        MessageSource messageSource = resolveMessageSource();

        // expect:
        assertAll(
            () -> assertThat(messageSource.getMessage(KEY_PROVERB),
                equalTo(PROVERB_FORMAT)),
            () -> assertThat(messageSource.getMessage(KEY_PROVERB, Locale.getDefault()),
                equalTo(PROVERB_FORMAT)),
            () -> assertThat(messageSource.getMessage(KEY_PROVERB, TWO_ARGS),
                equalTo(PROVERB_TEXT)),
            () -> assertThat(messageSource.getMessage(KEY_PROVERB, TWO_ARGS, Locale.getDefault()),
                equalTo(PROVERB_TEXT))
        );
    }

    @Test
    public void verify_getMessage_withArguments_withLocale_withDefaultValue() {
        // given:
        MessageSource messageSource = resolveMessageSource();

        // expect:
        assertAll(
            () -> assertThat(messageSource.getMessage(KEY_PROVERB_BOGUS, DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE)),
            () -> assertThat(messageSource.getMessage(KEY_PROVERB_BOGUS, Locale.getDefault(), DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE)),
            () -> assertThat(messageSource.getMessage(KEY_PROVERB_BOGUS, TWO_ARGS, DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE)),
            () -> assertThat(messageSource.getMessage(KEY_PROVERB_BOGUS, TWO_ARGS, Locale.getDefault(), DEFAULT_VALUE),
                equalTo(DEFAULT_VALUE))
        );
    }

    @Test
    public void verify_getMessage_withUnknownKey_withArguments_withLocale() {
        // given:
        MessageSource messageSource = resolveMessageSource();

        // expect:
        assertAll(
            () -> assertThrows(NoSuchMessageException.class,
                () -> messageSource.getMessage(KEY_BOGUS)),
            () -> assertThrows(NoSuchMessageException.class,
                () -> messageSource.getMessage(KEY_BOGUS, Locale.getDefault())),
            () -> assertThrows(NoSuchMessageException.class,
                () -> messageSource.getMessage(KEY_BOGUS, TWO_ARGS)),
            () -> assertThrows(NoSuchMessageException.class,
                () -> messageSource.getMessage(KEY_BOGUS, TWO_ARGS, Locale.getDefault()))
        );
    }
}
