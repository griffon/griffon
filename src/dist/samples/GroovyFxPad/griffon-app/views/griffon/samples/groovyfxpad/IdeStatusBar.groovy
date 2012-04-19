/*
 * Copyright 2007-2012 the original author or authors.
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
 */

/**
 * @author Andres Almiray
 */

package griffon.samples.groovyfxpad

vbox {
    separator()
    panel {
        gridBagLayout()
        label(id: 'status', text: bind { model.status },
            constraints:gbc(weightx: 1.0,
               anchor: GridBagConstraints.WEST,
               fill: GridBagConstraints.HORIZONTAL,
               insets: [1,3,1,3])
        )
        separator(orientation:SwingConstants.VERTICAL, constraints: gbc(fill:GridBagConstraints.VERTICAL))
        label(text: bind{ model.rowAndCol },
         constraints: gbc(insets: [1,3,1,3])
        )
    }
}
