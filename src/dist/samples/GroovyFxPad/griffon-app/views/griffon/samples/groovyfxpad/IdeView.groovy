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

build(IdeActions)

application(title: GriffonNameUtils.capitalize(app.getMessage('application.title', app.config.application.title)),
    preferredSize: [800, 600],
    pack: true,
    locationByPlatform:true,
    iconImage: imageIcon('/groovyfx-logo-48x48.png').image,
    iconImages: [imageIcon('/groovyfx-logo-48x48.png').image,
               imageIcon('/groovyfx-logo-32x32.png').image,
               imageIcon('/groovyfx-logo-16x16.png').image]) {
   widget(build(IdeMenuBar))
   migLayout(layoutConstraints: 'fill')
   toolBar(build(IdeToolBar), constraints: 'north')
   widget(build(IdeContent), constraints: 'center, grow')
   widget(build(IdeStatusBar), constraints: 'south, grow')
}
