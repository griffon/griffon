/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.core.i18n;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public interface GroovyAwareMessageSource extends MessageSource {
    /**
     * Try to resolve the message.
     *
     * @param key Key to lookup, such as 'log4j.appenders.console'
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     */
    @Nonnull
    String getAt(@Nonnull String key) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param keyAndArgs Key to lookup, such as 'log4j.appenders.console' and arguments
     *                   that will be filled in for params within the message (params look like "{0}" within a
     *                   message, but this might differ between implementations), or null if none.
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     */
    @Nonnull
    String getAt(@Nonnull List<?> keyAndArgs) throws NoSuchMessageException;
}
