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

import org.jdesktop.swingx.tips.DefaultTip

import java.awt.Toolkit
import java.awt.event.KeyEvent

String keyMask = KeyEvent.getKeyModifiersText(Toolkit.defaultToolkit.menuShortcutKeyMask).toUpperCase()

app.config.tipsModel.add(
    new DefaultTip('', "<html>You can invoke code completion for Groovy keywords and DSL nodes just by striking <b>${keyMask} + SHIFT + SPACE</b></html>.")
)
app.config.tipsModel.add(
    new DefaultTip('', "<html>A list of all available nodes can be seen if you strike <b>${keyMask} + I</b> or select the Nodes option from the Help menu</html>.")
)