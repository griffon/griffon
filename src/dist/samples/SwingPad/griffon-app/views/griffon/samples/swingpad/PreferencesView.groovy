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

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    panel(constraints: 'grow, wrap') {
        borderLayout(hgap: 10, vgap: 10)
        label('''
            <html><p>These are not the preferences you're looking for.</p>
            <br/>
            <p>Move along.</p></html>
        '''.stripIndent(12).trim(), constraints: CENTER)
    }
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
            keyStroke: 'ESCAPE',
            condition: 'in focused window',
            action: hideAction)
}
