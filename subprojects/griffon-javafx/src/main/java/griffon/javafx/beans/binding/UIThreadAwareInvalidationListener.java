/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.javafx.beans.binding;

import griffon.annotations.core.Nonnull;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
class UIThreadAwareInvalidationListener extends InvalidationListenerDecorator implements UIThreadAware {
    UIThreadAwareInvalidationListener(@Nonnull InvalidationListener delegate) {
        super(delegate);
    }

    @Override
    public void invalidated(final Observable observable) {
        if (isFxApplicationThread()) {
            getDelegate().invalidated(observable);
        } else {
            runLater(() -> getDelegate().invalidated(observable));
        }
    }
}
