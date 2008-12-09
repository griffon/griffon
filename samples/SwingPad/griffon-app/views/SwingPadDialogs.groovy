/*
 * Copyright 2007-2008 the original author or authors.
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

import java.awt.Insets

dialog(title: 'Groovy executing', id: 'runWaitDialog', modal: true ) {
   vbox(border: emptyBorder(6)) {
       label(text: "Groovy is now executing. Please wait.", alignmentX: 0.5f)
       vstrut()
       button( interruptAction, margin: new Insets(10, 20, 10, 20),
               alignmentX: 0.5f)
    }
}
