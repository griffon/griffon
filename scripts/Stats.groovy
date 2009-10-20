/*
 * Copyright 2004-2005 the original author or authors.
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
 * Gant script which generates stats for a Griffon project.
 *
 * @author Glen.Smith
 * @author Andres.Almiray
 */

includeTargets << griffonScript("_GriffonSettings")
includeTargets << griffonScript("Init")

target (stats: "Generates basic stats for a Griffon project") {
    def EMPTY = /^\s*$/
    def SLASH_SLASH = /^\s*\/\/.*/
    def SLASH_STAR_STAR_SLASH = /^(.*)\/\*(.*)\*\/(.*)$/

    // TODO - handle slash_star comments inside strings
    def DEFAULT_LOC_MATCHER = { file ->
        loc = 0
        comment = 0
        file.eachLine { line ->
            if(line ==~ EMPTY) return
            else if(line ==~ SLASH_SLASH) return
            else {
                def m = line =~ SLASH_STAR_STAR_SLASH
                if(m.count && m[0][1] ==~ EMPTY && m[0][3] ==~ EMPTY) return
                int open = line.indexOf("/*")
                int close = line.indexOf("*/")
                if(open != -1 && (close-open) <= 1) comment++
                else if(close != -1 && comment) comment--
            }
            if(!comment) loc++
        } 
        loc
    }
   
    // TODO -- map multi-line clojure comments
    // TODO -- move stats handlers to plugins: clojure, javafx, scala, wizard

    // maps file path to
    def pathToInfo = [
        [name: "Models",              path: "models",           filetype: [".groovy"]],
        [name: "Views",               path: "views",            filetype: [".groovy"]],
        [name: "Controllers",         path: "controllers",      filetype: [".groovy"]],
        [name: "Lifecycle",           path: "lifecycle",        filetype: [".groovy"]],
        [name: "Groovy/Java Sources", path: "src.main",         filetype: [".groovy",".java"]],
//        [name: "Services",            path: "services",         filetype: [".groovy"]],
//        [name: "Wizards",             path: "wizards",          filetype: [".groovy"]],
//        [name: "Common Sources",      path: "src.commons",      filetype: [".groovy",".java"]],
//        [name: "Clojure Sources",     path: "src.clojure",      filetype: [".clj"], locmatcher: {file ->
//            def loc = 0
//            file.eachLine { line ->
//                if(line ==~ EMPTY || line ==~ /^\s*\;.*/) return
//                loc++
//            }
//            loc
//        }],
//        [name: "JavaFX Sources",      path: "src.javafx",       filetype: [".fx"]],
//        [name: "Scala Sources",       path: "src.scala",        filetype: [".scala"]],
        [name: "Unit Tests",          path: "test.unit",        filetype: [".groovy"]],
        [name: "Integration Tests",   path: "test.integration", filetype: [".groovy"]],
        [name: "Scripts",             path: "scripts",          filetype: [".groovy"]],
    ]

    event("StatsStart", [pathToInfo])

    new File(basedir).eachFileRecurse { file ->
        def match = pathToInfo.find { info ->
            file.path =~ info.path &&
            info.filetype.any{ s -> file.path.endsWith(s) }
            //file.path.endsWith(info.filetype)
        }
        if (match && file.isFile() ) {
            match.filecount = match.filecount ? match.filecount+1 : 1
            // strip whitespace
            loc = match.locmatcher ? match.locmatcher(file) : DEFAULT_LOC_MATCHER(file)
            match.loc = match.loc ? match.loc + loc : loc
        }
    }

    def totalFiles = 0
    def totalLOC = 0

    println '''
    +----------------------+-------+-------+
    | Name                 | Files |  LOC  |
    +----------------------+-------+-------+'''

    pathToInfo.each { info ->

        if (info.filecount) {
            println "    | " +
                info.name.padRight(20," ") + " | " +
                info.filecount.toString().padLeft(5, " ") + " | " +
                info.loc.toString().padLeft(5," ") + " | "
            totalFiles += info.filecount
            totalLOC += info.loc
        }

    }

    println "    +----------------------+-------+-------+"
    println "    | Totals               | " + totalFiles.toString().padLeft(5, " ") + " | " + totalLOC.toString().padLeft(5, " ") + " | "
    println "    +----------------------+-------+-------+\n"
}

setDefaultTarget(stats)
