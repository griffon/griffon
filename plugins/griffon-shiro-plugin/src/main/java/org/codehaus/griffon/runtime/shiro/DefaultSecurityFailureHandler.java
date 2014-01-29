/*
 * Copyright 2013-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.shiro;

import griffon.core.artifact.GriffonController;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 */
public class DefaultSecurityFailureHandler extends AbstractSecurityFailureHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityFailureHandler.class);

    public void handleFailure(@Nonnull Subject subject, @Nonnull Kind kind, @Nonnull GriffonController controller, @Nonnull String actionName) {
        super.handleFailure(subject, kind, controller, actionName);

        switch (kind) {
            case USER:
                LOG.info("Subject failed user challenge on {}", qualifyActionName(controller, actionName));
                break;
            case AUTHENTICATION:
                LOG.info("Subject failed authentication challenge on {}", qualifyActionName(controller, actionName));
                break;
            case PERMISSIONS:
                LOG.info("Subject was not granted access to {} due to lack of permissions", qualifyActionName(controller, actionName));
                break;
            case ROLES:
                LOG.info("Subject was not granted access to {} due to lack of roles", qualifyActionName(controller, actionName));
                break;
            case GUEST:
            default:
                LOG.info("Subject failed guest challenge on {}", qualifyActionName(controller, actionName));
        }
    }
}
