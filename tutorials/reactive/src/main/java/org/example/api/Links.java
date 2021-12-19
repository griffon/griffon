/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example.api;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static griffon.util.GriffonNameUtils.isNotBlank;

public class Links {
    private static final Pattern REL_PATTERN = Pattern.compile("rel=\"(.*)\"");

    private String first;
    private String next;
    private String prev;
    private String last;

    public static Links of(@Nonnull String input) {
        return new Links(input);
    }

    private Links(@Nonnull String input) {
        if (isNotBlank(input)) {
            for (String s : input.split(",")) {
                String[] parts = s.split(";");
                Matcher matcher = REL_PATTERN.matcher(parts[1].trim());
                if (matcher.matches()) {
                    switch (matcher.group(1).toLowerCase()) {
                        case "first":
                            first = normalize(parts[0]);
                            break;
                        case "next":
                            next = normalize(parts[0]);
                            break;
                        case "prev":
                            prev = normalize(parts[0]);
                            break;
                        case "last":
                            last = normalize(parts[0]);
                            break;
                    }
                }
            }
        }
    }

    private String normalize(String url) {
        url = url.trim();
        if (url.startsWith("<") && url.endsWith(">")) {
            url = url.substring(1, url.length() - 1);
        }
        return url;
    }

    public boolean hasFirst() {
        return isNotBlank(first);
    }

    public boolean hasNext() {
        return isNotBlank(next);
    }

    public boolean hasPrev() {
        return isNotBlank(prev);
    }

    public boolean hasLast() {
        return isNotBlank(last);
    }

    @Nullable
    public String first() {
        return first;
    }

    @Nullable
    public String next() {
        return next;
    }

    @Nullable
    public String prev() {
        return prev;
    }

    @Nullable
    public String last() {
        return last;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Links{");
        sb.append("first='").append(first).append('\'');
        sb.append(", next='").append(next).append('\'');
        sb.append(", prev='").append(prev).append('\'');
        sb.append(", last='").append(last).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
