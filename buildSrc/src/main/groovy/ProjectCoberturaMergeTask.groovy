/*
 * Copyright 2008-2015 the original author or authors.
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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import net.sourceforge.cobertura.merge.Main

/**
 * @author Andres Almiray
 */
class ProjectCoberturaMergeTask extends DefaultTask {
    @TaskAction
    void merge() {
        def args = []
        def coberturaDir = new File(project.buildDir, 'cobertura')
        args << '--datafile'
        args << "${coberturaDir.absolutePath}/cobertura.ser"
        args << '--basedir'
        args << project.projectDir.absolutePath
        project.subprojects.each { prj ->
            if (prj.name.endsWith('compile')) return
            File coberturaOutputDir = new File(prj.projectDir, 'build/cobertura')
            if (coberturaOutputDir.exists()) {
                coberturaOutputDir.eachFile {
                    args << "${it.absolutePath - project.projectDir.absolutePath}"
                }
            }
        }
        Main.main(args as String[])
    }
}