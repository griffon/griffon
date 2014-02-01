/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.shiro;

import griffon.core.artifact.GriffonController;
import griffon.plugins.shiro.SecurityFailureHandler;
import org.apache.shiro.subject.Subject;

import javax.annotation.Nonnull;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractSecurityFailureHandler implements SecurityFailureHandler {
    @Nonnull
    protected String qualifyActionName(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return controller.getClass().getName() + "." + actionName;
    }

    public void handleFailure(@Nonnull Subject subject, @Nonnull Kind kind, @Nonnull GriffonController controller, @Nonnull String actionName) {
        requireNonNull(subject, "Argument 'subject' cannot be null");
        requireNonNull(kind, "Argument 'kind' cannot be null");
        requireNonNull(controller, "Argument 'controller' cannot be null");
        requireNonBlank(actionName, "Argument 'actionName' cannot be blank");
    }
}