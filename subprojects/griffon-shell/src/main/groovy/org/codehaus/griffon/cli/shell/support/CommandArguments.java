/*
 * Copyright 2008-2012 the original author or authors.
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

package org.codehaus.griffon.cli.shell.support;

import org.codehaus.griffon.cli.shell.Argument;
import org.codehaus.griffon.cli.shell.Option;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Andres Almiray
 */
public class CommandArguments {
    public final List<Object> params = new ArrayList<Object>();
    public final Map<Option, Field> options = new TreeMap<Option, Field>(OPTION_COMPARATOR);
    public final Map<Argument, Field> arguments = new LinkedHashMap<Argument, Field>();
    public final List<Argument> orderedArguments = new ArrayList<Argument>();
    public Object subject;

    public static final Comparator<Option> OPTION_COMPARATOR = new Comparator<Option>() {
        @Override
        public int compare(Option a, Option b) {
            return a.name().compareTo(b.name());
        }
    };
}
