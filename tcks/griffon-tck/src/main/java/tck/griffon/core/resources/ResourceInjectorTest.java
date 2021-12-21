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

import griffon.annotations.resources.InjectedResource;
import griffon.core.resources.ResourceInjector;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Verifies that implementations of {@code ResourceInjector} can inject resources on a target bean.
 * The following key/value pairs must be resolvable
 * <p>
 * <pre>
 * tck.griffon.core.resources.ResourceInjectorTest.Bean.privateField=privateField
 * tck.griffon.core.resources.ResourceInjectorTest.Bean.value=fieldBySetter
 * tck.griffon.core.resources.ResourceInjectorTest.Bean.privateIntField=42
 * tck.griffon.core.resources.ResourceInjectorTest.Bean.intValue=21
 * sample.key.no_args=no_args
 * sample.key.with_args=with_args {0} {1}
 * tck.griffon.core.resources.ResourceInjectorTest.SuperBean.superPrivateField=superPrivateField
 * tck.griffon.core.resources.ResourceInjectorTest.SuperBean.superValue=superFieldBySetter
 * tck.griffon.core.resources.ResourceInjectorTest.SuperBean.superPrivateIntField=420
 * tck.griffon.core.resources.ResourceInjectorTest.SuperBean.superIntValue=210
 * sample.super.key.no_args=super_no_args
 * sample.super.key.with_args=super_with_args {0} {1}
 * </pre>
 *
 * @author Andres Almiray
 */
public abstract class ResourceInjectorTest {
    protected abstract ResourceInjector resolveResourcesInjector();

    @Test
    public void injectResourcesOnBean() {
        // given:
        Bean bean = new Bean();

        // when:
        resolveResourcesInjector().injectResources(bean);

        // then:
        assertAll(
            () -> assertThat(bean.privateField(), equalTo("privateField")),
            () -> assertThat(bean.fieldBySetter(), equalTo("fieldBySetter")),
            () -> assertThat(bean.privateIntField(), equalTo(42)),
            () -> assertThat(bean.intFieldBySetter(), equalTo(21)),
            () -> assertThat(bean.fieldWithKey(), equalTo("no_args")),
            () -> assertThat(bean.fieldWithKeyAndArgs(), equalTo("with_args 1 2")),
            () -> assertThat(bean.fieldWithKeyNoArgsWithDefault(), equalTo("DEFAULT_NO_ARGS")),
            () -> assertThat(bean.fieldWithKeyWithArgsWithDefault(), equalTo("DEFAULT_WITH_ARGS")),
            () -> assertThat(bean.notFound(), nullValue()),
            () -> assertThat(bean.superPrivateField(), equalTo("superPrivateField")),
            () -> assertThat(bean.superFieldBySetter(), equalTo("superFieldBySetter")),
            () -> assertThat(bean.superPrivateIntField(), equalTo(420)),
            () -> assertThat(bean.superIntFieldBySetter(), equalTo(210)),
            () -> assertThat(bean.superFieldWithKey(), equalTo("super_no_args")),
            () -> assertThat(bean.superFieldWithKeyAndArgs(), equalTo("super_with_args 1 2")),
            () -> assertThat(bean.superFieldWithKeyNoArgsWithDefault(), equalTo("SUPER_DEFAULT_NO_ARGS")),
            () -> assertThat(bean.superFieldWithKeyWithArgsWithDefault(), equalTo("SUPER_DEFAULT_WITH_ARGS")),
            () -> assertThat(bean.superNotFound(), nullValue())
        );
    }

    public static class SuperBean {
        @InjectedResource
        private String superPrivateField;

        private String superFieldBySetter;

        @InjectedResource
        private int superPrivateIntField;

        private int superIntFieldBySetter;

        @InjectedResource
        public void setSuperValue(String value) {
            this.superFieldBySetter = value;
        }

        @InjectedResource
        public void setSuperIntValue(int value) {
            this.superIntFieldBySetter = value;
        }

        @InjectedResource(value = "sample.super.key.no_args")
        private String superFieldWithKey;

        @InjectedResource(value = "sample.super.key.with_args", args = {"1", "2"})
        private String superFieldWithKeyAndArgs;

        @InjectedResource(value = "sample.super.key.no_args.with_default", defaultValue = "SUPER_DEFAULT_NO_ARGS")
        private String superFieldWithKeyNoArgsWithDefault;

        @InjectedResource(value = "sample.super.key.no_args.with_default", args = {"1", "2"}, defaultValue = "SUPER_DEFAULT_WITH_ARGS")
        private String superFieldWithKeyWithArgsWithDefault;

        @InjectedResource
        private String superNotFound;

        public String superPrivateField() {
            return superPrivateField;
        }

        public String superFieldBySetter() {
            return superFieldBySetter;
        }

        public int superPrivateIntField() {
            return superPrivateIntField;
        }

        public int superIntFieldBySetter() {
            return superIntFieldBySetter;
        }

        public String superFieldWithKey() {
            return superFieldWithKey;
        }

        public String superFieldWithKeyAndArgs() {
            return superFieldWithKeyAndArgs;
        }

        public String superFieldWithKeyNoArgsWithDefault() {
            return superFieldWithKeyNoArgsWithDefault;
        }

        public String superFieldWithKeyWithArgsWithDefault() {
            return superFieldWithKeyWithArgsWithDefault;
        }

        public String superNotFound() {
            return superNotFound;
        }
    }

    public static class Bean extends SuperBean {
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
        public void setIntValue(int value) {
            this.intFieldBySetter = value;
        }

        @InjectedResource(value = "sample.key.no_args")
        private String fieldWithKey;

        @InjectedResource(value = "sample.key.with_args", args = {"1", "2"})
        private String fieldWithKeyAndArgs;

        @InjectedResource(value = "sample.key.no_args.with_default", defaultValue = "DEFAULT_NO_ARGS")
        private String fieldWithKeyNoArgsWithDefault;

        @InjectedResource(value = "sample.key.no_args.with_default", args = {"1", "2"}, defaultValue = "DEFAULT_WITH_ARGS")
        private String fieldWithKeyWithArgsWithDefault;

        @InjectedResource
        private String notFound;

        public String privateField() {
            return privateField;
        }

        public String fieldBySetter() {
            return fieldBySetter;
        }

        public int privateIntField() {
            return privateIntField;
        }

        public int intFieldBySetter() {
            return intFieldBySetter;
        }

        public String fieldWithKey() {
            return fieldWithKey;
        }

        public String fieldWithKeyAndArgs() {
            return fieldWithKeyAndArgs;
        }

        public String fieldWithKeyNoArgsWithDefault() {
            return fieldWithKeyNoArgsWithDefault;
        }

        public String fieldWithKeyWithArgsWithDefault() {
            return fieldWithKeyWithArgsWithDefault;
        }

        public String notFound() {
            return notFound;
        }
    }
}
