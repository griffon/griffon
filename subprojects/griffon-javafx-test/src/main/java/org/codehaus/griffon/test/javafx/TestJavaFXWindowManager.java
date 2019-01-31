/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.test.javafx;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.javafx.JavaFXWindowDisplayHandler;
import org.codehaus.griffon.runtime.javafx.DefaultJavaFXWindowManager;

import javax.inject.Named;

import static griffon.test.javafx.TestContext.getTestContext;
import static griffon.util.GriffonNameUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 2.3.0
 */
public class TestJavaFXWindowManager extends DefaultJavaFXWindowManager {
    public TestJavaFXWindowManager(@Nonnull GriffonApplication application, @Nonnull @Named("windowDisplayHandler") JavaFXWindowDisplayHandler windowDisplayHandler) {
        super(application, windowDisplayHandler);
    }

    @Nullable
    @Override
    protected Object resolveStartingWindowFromConfiguration() {
        String startingWindowName = getTestContext().getWindowName();
        return isNotBlank(startingWindowName) ? startingWindowName : super.resolveStartingWindowFromConfiguration();
    }
}