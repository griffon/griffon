/*
 * Copyright 2010-2014 the original author or authors.
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

package griffon.core.formatters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public abstract class AbstractFormatter<T> implements Formatter<T> {
    protected static int parseHexInt(@Nonnull String val, @Nonnull Class<?> klass) throws ParseException {
        try {
            return Integer.parseInt(String.valueOf(requireNonNull(val)).trim(), 16) & 0xFF;
        } catch (NumberFormatException e) {
            throw parseError(val, klass, e);
        }
    }

    protected static int parseHexInt(@Nonnull Number val) {
        return requireNonNull(val).intValue() & 0xFF;
    }

    protected static ParseException parseError(@Nullable Object value, @Nonnull Class<?> klass) throws ParseException {
        throw new ParseException("Can't convert '" + value + "' into " + requireNonNull(klass).getName());
    }

    protected static ParseException parseError(@Nullable Object value, @Nonnull Class<?> klass, @Nonnull Exception e) throws ParseException {
        throw new ParseException("Can't convert '" + value + "' into " + requireNonNull(klass).getName(), requireNonNull(e));
    }
}
