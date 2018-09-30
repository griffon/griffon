/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.test.core;

import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import org.codehaus.griffon.test.core.DefaultGriffonApplication;
import org.codehaus.griffon.test.core.TestApplicationBootstrapper;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class GriffonUnitExtension extends AbstractGriffonUnitExtension {
    public static class Builder {
        private String[] startupArgs = DefaultGriffonApplication.EMPTY_ARGS;
        private Class<? extends GriffonApplication> applicationClass = DefaultGriffonApplication.class;
        private Class<? extends ApplicationBootstrapper> applicationBootstrapper = TestApplicationBootstrapper.class;

        public Builder startupArgs(String[] args) {
            if (args != null) {
                startupArgs = args;
            }
            return this;
        }

        public Builder applicationClass(Class<? extends GriffonApplication> clazz) {
            if (clazz != null) {
                applicationClass = clazz;
            }
            return this;
        }

        public Builder applicationBootstrapper(Class<? extends ApplicationBootstrapper> bootstrapper) {
            if (bootstrapper != null) {
                applicationBootstrapper = bootstrapper;
            }
            return this;
        }

        public GriffonUnitExtension build() {
            return new GriffonUnitExtension(startupArgs, applicationClass, applicationBootstrapper);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public GriffonUnitExtension() {
        this(DefaultGriffonApplication.EMPTY_ARGS, DefaultGriffonApplication.class, TestApplicationBootstrapper.class);
    }

    protected GriffonUnitExtension(String[] startupArgs, Class<? extends GriffonApplication> applicationClass, Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        super(startupArgs, applicationClass, applicationBootstrapper);
    }
}
