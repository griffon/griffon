/*
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
package editor

import griffon.transform.Observable
import griffon.transform.PropertyListener

import java.beans.PropertyChangeListener

class DocumentModel extends Document {
    @Observable
    @PropertyListener(documentUpdater)
    Document document = new Document()

    // copies one property value from document to itself
    private proxyUpdater = { e ->
        // owner is a standard property found in closures
        // it points to the instance that contains the closure
        // i.e, the DocumentProxy instance that holds this closure
        owner[e.propertyName] = e.newValue
    } as PropertyChangeListener

    // listens to changes on the document property
    // copies all properties form source to itself
    private documentUpdater = { e ->
        e.oldValue?.removePropertyChangeListener(proxyUpdater)
        e.newValue?.addPropertyChangeListener(proxyUpdater)
        e.newValue?.copyTo(owner)
    }
}
