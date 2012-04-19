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

spanCount = 1

if (model.includeCredits) spanCount++
if (model.includeLicense) spanCount++

rowConstraints = "center, span $spanCount, wrap".toString()

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    label(icon: imageIcon('/groovyfx-logo-64x64.png'), constraints: "center, span $spanCount, wrap".toString())
    label(GriffonNameUtils.capitalize(app.getMessage('application.title', app.config.application.title)) +
          ' ' + Metadata.current.getApplicationVersion(),
        font: current.font.deriveFont(Font.BOLD),
        constraints: rowConstraints)
    label(text: bind{ model.description }, constraints: rowConstraints)
    scrollPane(preferredSize: [320, 160], constraints: rowConstraints) {
        table {
            tableFormat = defaultTableFormat(columnNames: ['Name', 'Version'])
            eventTableModel(source: model.plugins, format: tableFormat)
            installTableComparatorChooser(source: model.plugins)
        }
    }
    if(model.includeCredits) button(creditsAction, constraints: 'left')
    if(model.includeLicense) button(licenseAction, constraints: (model.includeCredits?'center':'left'))
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: hideAction)
}
