/*
 * Copyright 2004-2011 the original author or authors.
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

import groovy.xml.MarkupBuilder

/**
 * Gant script which generates stats for a Griffon project.
 *
 * @author Glen Smith
 * @author Andres Almiray
 */

target(stats: "Generates basic stats for a Griffon project") {
    EMPTY = /^\s*$/
    SLASH_SLASH = /^\s*\/\/.*/
    SLASH_STAR_STAR_SLASH = /^(.*)\/\*(.*)\*\/(.*)$/

    // TODO - handle slash_star comments inside strings
    DEFAULT_LOC_MATCHER = { file ->
        def loc = 0
        def comment = 0
        file.eachLine { line ->
            if (!line.trim().length() || line ==~ EMPTY) return
            else if (line ==~ SLASH_SLASH) return
            else {
                def m = line =~ SLASH_STAR_STAR_SLASH
                if (m.count && m[0][1] ==~ EMPTY && m[0][3] ==~ EMPTY) return
                int open = line.indexOf("/*")
                int close = line.indexOf("*/")
                if (open != -1 && (close - open) <= 1) comment++
                else if (close != -1 && comment) {
                    comment--
                    if (!comment) return
                }
            }
            if (!comment) loc++
        }
        loc
    }

    LOC_MATCHERS_PER_TYPE = [
            '.groovy': DEFAULT_LOC_MATCHER,
            '.java': DEFAULT_LOC_MATCHER
    ]

    // maps file path to
    def pathToInfo = [
            [name: 'Models',              path: 'models',           filetype: ['.groovy', '.java']],
            [name: 'Views',               path: 'views',            filetype: ['.groovy', '.java']],
            [name: 'Controllers',         path: 'controllers',      filetype: ['.groovy', '.java']],
            [name: 'Services',            path: 'services',         filetype: ['.groovy', '.java']],
            [name: 'Lifecycle',           path: 'lifecycle',        filetype: ['.groovy']],
            [name: 'Groovy/Java Sources', path: 'src.main',         filetype: ['.groovy', '.java']],
            [name: 'Unit Tests',          path: 'test.unit',        filetype: ['.groovy', '.java']],
            [name: 'Integration Tests',   path: 'test.integration', filetype: ['.groovy', '.java']],
            [name: 'Scripts',             path: 'scripts',          filetype: ['.groovy']],
            [name: 'Configuration',       path: 'conf',             filetype: ['.groovy']]
    ]

    event("StatsStart", [pathToInfo])

    def searchPath = new File(basedir)
    searchPath.eachFileRecurse { file ->
        def match = pathToInfo.find { info ->
            def fixedPath = file.path - searchPath.canonicalPath //fix problem when project inside dir "jobs" (eg. hudson stores projects under jobs-directory)
            fixedPath =~ info.path && info.filetype.any {s -> file.path.endsWith(s)}
        }
        // skip basic configuration files but count the rest
        if (match && match.name == 'Configuration' && match.path == 'conf' &&
                file.name in ['Application.groovy', 'BuildConfig.groovy', 'Builder.groovy', 'Config.groovy']) return
        if (match && file.isFile()) {
            match.filecount = match.filecount ? match.filecount + 1 : 1
            // strip whitespace
            def ext = file.path.substring(file.path.lastIndexOf('.'))
            loc = match.locmatcher ? match.locmatcher(file) : (LOC_MATCHERS_PER_TYPE[ext] ? LOC_MATCHERS_PER_TYPE[ext](file) : DEFAULT_LOC_MATCHER(file))
            match.loc = match.loc ? match.loc + loc : loc
        }
    }

    def totalFiles = 0
    def totalLOC = 0

    pathToInfo.each { info ->
        if (info.filecount) {
            totalFiles += info.filecount
            totalLOC += info.loc
        }
    }

    output(pathToInfo, totalFiles.toString(), totalLOC.toString(), new PrintWriter(System.out))
    if (argsMap.xml) xmlOutput(pathToInfo, totalFiles.toString(), totalLOC.toString())
    if (argsMap.html) htmlOutput(pathToInfo, totalFiles.toString(), totalLOC.toString())
    if (argsMap.txt) output(pathToInfo, totalFiles.toString(), totalLOC.toString(), new PrintWriter(getOutputFile("txt")))
}

setDefaultTarget(stats)

private getOutputFile(String suffix) {
    def outputDir = buildConfig.griffon.testing.reports.destDir ?: "${basedir}/target"
    new File(outputDir).mkdirs()
    return new File(outputDir, "stats." + suffix)
}

private output(infos, totalFiles, totalLOC, out) {
    out.println '''
    +----------------------+-------+-------+
    | Name                 | Files |  LOC  |
    +----------------------+-------+-------+'''

    infos.each { info ->
        if (info.filecount) {
            out.println "    | " +
                    info.name.padRight(20, " ") + " | " +
                    info.filecount.toString().padLeft(5, " ") + " | " +
                    info.loc.toString().padLeft(5, " ") + " | "
        }
    }

    out.println "    +----------------------+-------+-------+"
    out.println "    | Totals               | " + totalFiles.padLeft(5, " ") + " | " + totalLOC.padLeft(5, " ") + " | "
    out.println "    +----------------------+-------+-------+\n"
    out.flush()
}

private xmlOutput(infos, totalFiles, totalLOC) {
    new MarkupBuilder(new FileWriter(getOutputFile("xml"))).stats {
        infos.each { info ->
            if (info.filecount) {
                category {
                    name(info.name)
                    fileCount(info.filecount.toString())
                    loc(info.loc.toString())
                }
            }
        }
        category {
            name("Total")
            fileCount(totalFiles)
            loc(totalLOC)
        }
    }
}

private htmlOutput(infos, totalFiles, totalLOC) {
    int i = 0
    new MarkupBuilder(new FileWriter(getOutputFile("html"))).html {
        table(border: 1) {
            tr {
                th("Name")
                th("Files")
                th("LOC")
            }
            infos.each { info ->
                if (info.filecount) {
                    tr(style: (i++) % 2 ? 'background-color:lightblue' : 'background-color:FFF') {
                        td(info.name)
                        td(info.filecount.toString())
                        td(info.loc.toString())
                    }
                }
            }
            tr(style: "background-color:lightgreen") {
                b {
                    td("Total")
                    td(totalFiles)
                    td(totalLOC)
                }
            }
        }
    }
}
