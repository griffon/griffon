/*
 * Copyright 2007-2014 the original author or authors.
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

import org.jdesktop.swingx.JXTipOfTheDay
import griffon.samples.groovyfxpad.GroovFXPadUtils

onBootstrapStart = { app ->
    app.builderConfig = new ConfigSlurper().parse('''
root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = '*'
    }
}

jx {
    'groovy.swing.SwingXBuilder' {
        view = '*'
    }
}

root.'griffon.builder.jide.JideBuilder'.view = '*'
''')
}

onReadyEnd = { app ->
    app.execInsideUIAsync {
        JXTipOfTheDay totd = new JXTipOfTheDay(app.config.tipsModel)
        totd.showDialog(app.windowManager.startingWindow, GroovFXPadUtils.PREFERENCES)
    }
}
