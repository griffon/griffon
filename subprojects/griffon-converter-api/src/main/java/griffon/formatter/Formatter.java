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
package griffon.formatter;

import griffon.annotations.core.Nullable;

/**
 * <p>Strategy for parsing and formatting instances from and to their literal representation.</p>
 *
 * @param <T> the type of instances this {@code Formatter} can handle.
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface Formatter<T> {
    /**
     * <p>Formats an instance into its literal representation.</p>
     * <p>The resulting {@code String} may be set as an argument to {@link Formatter#parse}
     * resulting in a similar instance as the input.</p>
     *
     * @param obj the instance to be formatted. May be {@code null}.
     * @return A {@code String} representing the instance's state.
     */
    @Nullable
    String format(@Nullable T obj);

    /**
     * <p>Parses a literal representation into an instance of type {@code T}.</p>
     * <p>The resulting instance {@code T} may be set as an argument to {@link Formatter#format}
     * resulting in an equal {@code String} as the input.</p>
     *
     * @param str the {@code String} to be parsed. May be {@code null}.
     * @return an instance of type {@code T} whose state is initialized given the
     * parameters of the input {@code String}.
     * @throws ParseException if the {@code String} cannot be parsed.
     */
    @Nullable
    T parse(@Nullable String str) throws ParseException;
}
