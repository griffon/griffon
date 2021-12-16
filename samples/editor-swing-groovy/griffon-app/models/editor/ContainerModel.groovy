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
package editor

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable
import griffon.transform.PropertyListener

@ArtifactProviderFor(GriffonModel)
class ContainerModel {
    final DocumentModel documentModel = new DocumentModel()

    @Observable
    @PropertyListener(mvcUpdater)
    String mvcIdentifier

    // listens to changes on the mvcId property
    private mvcUpdater = { e ->
        Document document = null
        if (e.newValue) {
            document = application.mvcGroupManager.models[mvcIdentifier].document
        } else {
            document = new Document()
        }
        documentModel.document = document
    }
}
