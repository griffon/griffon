/*
 * Copyright 2011 the original author or authors.
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
 * limitations under the License.
 */

/**
 * @author Andres Almiray
 */

import griffon.util.GriffonNameUtils
import griffon.util.Metadata
import org.springframework.core.io.Resource

includeTargets << griffonScript("_GriffonPlugins")
includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("CreateMvc" )

target(name: 'createApplicationProject',
       description: 'Creates a new application project',
       prehook: null, posthook: null) {
    createProjectWithDefaults()

    argsMap.model = 'MainModel'
    argsMap.view = 'MainView'
    argsMap.controller = 'MainController'
    createMVC()

    createArtifact(
        name: mvcFullQualifiedClassName,
        suffix: 'Actions',
        type: 'Actions',
        template: 'MainActions',
        path: 'griffon-app/views')

    createArtifact(
        name: mvcFullQualifiedClassName,
        suffix: 'MenuBar',
        type: 'MenuBar',
        template: 'MainMenuBar',
        path: 'griffon-app/views')

    createArtifact(
        name: mvcFullQualifiedClassName,
        suffix: 'StatusBar',
        type: 'StatusBar',
        template: 'MainStatusBar',
        path: 'griffon-app/views')

    createArtifact(
        name: mvcFullQualifiedClassName,
        suffix: 'Content',
        type: 'Content',
        template: 'MainContent',
        path: 'griffon-app/views')

    argsMap.model = ''
    argsMap.view = ''
    argsMap.controller = ''

    createArtifact(
        name: qualify('AbstractDialogModel'),
        suffix: '',
        type: 'Model',
        template: 'AbstractDialogModel',
        path: 'griffon-app/models')

    if(fileType == '.java') {
        createArtifact(
            name: qualify('AbstractDialogView'),
            suffix: '',
            type: 'View',
            template: 'AbstractDialogView',
            path: 'griffon-app/views')
    }

    createArtifact(
        name: qualify('DialogController'),
        suffix: '',
        type: 'Controller',
        template: 'DialogController',
        path: 'griffon-app/controllers')

    argsMap.withController = qualify('DialogController')
    argsMap.view = 'AboutView'
    argsMap.model = 'AboutModel'
    argsMap.params[0] = qualify('about')
    createMVC()

    argsMap.view = 'CreditsView'
    argsMap.model = 'CreditsModel'
    argsMap.params[0] = qualify('credits')
    createMVC()

    argsMap.view = 'LicenseView'
    argsMap.model = 'LicenseModel'
    argsMap.params[0] = qualify('license')
    createMVC()

    argsMap.view = 'PreferencesView'
    argsMap.model = 'PreferencesModel'
    argsMap.params[0] = qualify('preferences')
    createMVC()

    argsMap.skipPackagePrompt = true
    createArtifact(
        name: 'Events',
        suffix: '',
        type: 'Events',
        template: 'Events',
        path: 'griffon-app/conf')

    copyResources("${basedir}/griffon-app/resources", 'griffon-app/resources/*')
    copyResources("${basedir}/griffon-app/i18n", 'griffon-app/i18n/*')
    ant.replace(dir: "${basedir}/griffon-app/i18n") {
        replacefilter(token: "@griffon.project.name@", value: GriffonNameUtils.capitalize(griffonAppName))
    }

    File configFile = new File("${basedir}/griffon-app/conf/Config.groovy")
    configFile.append('''
import griffon.swing.SwingUtils
import java.awt.Dialog

swing {
    windowManager {
        defaultShow = {w, app ->
            if(!(w instanceof Dialog)) SwingUtils.centerOnScreen(w)
            w.visible = true
        }
        defaultHide = {w, app ->
            if(w instanceof Dialog || app.windowManager.windows.findAll{it.visible}.size() > 1) {
                w.dispose()
            } else {
                if(app.config.shutdown.proceed) w.dispose()
            }
        }
    }
}
''')

    Metadata md = Metadata.getInstance(new File("${basedir}/application.properties"))
    installPluginExternal md, 'actions'
    installPluginExternal md, 'glazedlists'
    installPluginExternal md, 'miglayout'
}

qualify = { className ->
    (mvcPackageName? mvcPackageName + '.':'') + className
}

copyResources = { destDir, pattern, boolean overwrite = true ->
    new File(destDir.toString()).mkdirs()
    Resource[] resources = resolveResources("file://${griffonSettings.griffonHome}/archetypes/jumpstart/${pattern}")
    resources.each { Resource res ->
        if (res.readable) {
            copyGriffonResource("${destDir}/${res.filename}", res, overwrite)
        }
    }
}

setDefaultTarget(createApplicationProject)
