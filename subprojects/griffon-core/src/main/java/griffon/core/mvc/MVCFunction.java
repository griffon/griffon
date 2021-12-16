/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.core.mvc;

import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonView;

import javax.annotation.Nullable;

/**
 * An specialized function for working with MVC members.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface MVCFunction<M extends GriffonModel, V extends GriffonView, C extends GriffonController> {
    /**
     * Applies this function<p>
     *  @param model      the Model member of the group
     * @param view       the View member of the group
     * @param controller the Controller member of the group
     */
    void apply(@Nullable M model, @Nullable V view, @Nullable C controller);
}
