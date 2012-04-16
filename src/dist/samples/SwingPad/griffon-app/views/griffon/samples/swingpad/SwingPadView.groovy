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

package griffon.samples.swingpad

build(SwingPadActions)

application(title: GriffonNameUtils.capitalize(app.getMessage('application.title', app.config.application.title)),
        preferredSize: [800, 600],
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    widget(build(SwingPadMenuBar))
    migLayout(layoutConstraints: 'fill')
    toolBar(build(SwingPadToolBar), constraints: 'north')
    widget(build(SwingPadContent), constraints: 'center, grow')
    widget(build(SwingPadStatusBar), constraints: 'south, grow')
}
