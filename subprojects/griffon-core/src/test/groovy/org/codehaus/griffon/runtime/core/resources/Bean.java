/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.core.resources;

import griffon.core.resources.InjectedResource;

public class Bean {
    @InjectedResource
    private String privateField;

    private String fieldBySetter;

    @InjectedResource
    private int privateIntField;

    private int intFieldBySetter;

    @InjectedResource
    public void setValue(String value) {
        this.fieldBySetter = value;
    }

    @InjectedResource
    public void setIntValue(int value) { this.intFieldBySetter = value; }

    @InjectedResource(key = "sample.key.no_args")
    private String fieldWithKey;

    @InjectedResource(key = "sample.key.with_args", args = {"1", "2"})
    private String fieldWithKeyAndArgs;

    @InjectedResource(key = "sample.key.no_args.with_default", defaultValue = "DEFAULT_NO_ARGS")
    private String fieldWithKeyNoArgsWithDefault;

    @InjectedResource(key = "sample.key.no_args.with_default", args = {"1", "2"}, defaultValue = "DEFAULT_WITH_ARGS")
    private String fieldWithKeyWithArgsWithDefault;

    @InjectedResource
    private String notFound;
}
