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

import griffon.util.GriffonUtil

import org.codehaus.griffon.test.junit4.JUnit4GriffonTestType
import org.codehaus.griffon.test.support.GriffonTestMode
import org.codehaus.griffon.test.report.junit.JUnitReportProcessor

import org.codehaus.griffon.test.GriffonTestType
import org.codehaus.griffon.test.GriffonTestTargetPattern
import org.codehaus.griffon.test.event.GriffonTestEventPublisher
import org.codehaus.griffon.test.event.GriffonTestEventConsoleReporter

/**
 * Gant script that runs the Griffon unit tests
 *
 * @author Graeme Rocher (Grails 0.4)
 */

includeTargets << griffonScript("_GriffonBootstrap")
includeTargets << griffonScript("_GriffonSettings")
includeTargets << griffonScript("_GriffonClean")

// Miscellaneous 'switches' that affect test operation
testOptions = [:]

// The four test phases that we can run.
unitTests = [ "unit" ]
integrationTests = [ "integration" ]
otherTests = [ "cli" ]

// The potential phases for execution, modify this by responding to the TestPhasesStart event
phasesToRun = ["unit", "integration", "other"]

TEST_PHASE_WILDCARD = ' _ALL_PHASES_ '
TEST_TYPE_WILDCARD = ' _ALL_TYPES_ '
targetPhasesAndTypes = [:]

// Passed to the test runners to facilitate event publishing
testEventPublisher = new GriffonTestEventPublisher(event)

// Add a listener to write test status updates to the console
eventListener.addGriffonBuildListener(new GriffonTestEventConsoleReporter(System.out))

// Add a listener to generate our JUnit reports.
eventListener.addGriffonBuildListener(new JUnitReportProcessor())

// A list of test names. These can be of any of this forms:
//
//   org.example.*
//   org.example.**.*
//   MyService
//   MyService.testSomeMethod
//   org.example.other.MyService.testSomeMethod
//
// The default pattern runs all tests.
testNames = buildConfig.griffon.testing.patterns ?: ['**.*']
testTargetPatterns = null // created in allTests()

// Controls which result formats are generated. By default both XML
// and plain text files are created. You can override this in your
// own scripts.
reportFormats = [ "xml", "plain" ]

// If true, only run the tests that failed before.
reRunTests = false

// Where the report files are created.
testReportsDir = griffonSettings.testReportsDir
// Where the test source can be found
testSourceDir = griffonSettings.testSourceDir

// The 'styledir' argument to the 'junitreport' ant task (null == default provided by Ant)
junitReportStyleDir = new File(griffonSettings.griffonHome, "lib")

// Set up an Ant path for the tests.
ant.path(id: "griffon.test.classpath", testClasspath)

createTestReports = true

testsFailed = false

target(allTests: "Runs the project's tests.") {
    def dependencies = [compile, packagePlugins]
    if (testOptions.clean) dependencies = [clean] + dependencies
    depends(*dependencies)
    
    packageFiles(basedir)

    ant.mkdir(dir: testReportsDir)
    ant.mkdir(dir: "${testReportsDir}/html")
    ant.mkdir(dir: "${testReportsDir}/plain")

    // If we are to run the tests that failed, replace the list of
    // test names with the failed ones.
    if (reRunTests) testNames = getFailedTests()
    
    testTargetPatterns = testNames.collect { new GriffonTestTargetPattern(it) } as GriffonTestTargetPattern[]
    if(isPluginProject && !isAddonPlugin) phasesToRun.remove('integration')
    if(!phasesToRun) {
        println "No test phases were defined. Aborting"
        System.exit(1)
    }
    
    event("TestPhasesStart", [phasesToRun])
    
    // Handle pre 0.9 style testing configuration
    def convertedPhases = [:]
    phasesToRun.each { phaseName ->
        def types = binding."${phaseName}Tests"
        if (types) {
            convertedPhases[phaseName] = types.collect { rawType ->
                if (rawType instanceof CharSequence) {
                    def rawTypeString = rawType.toString()
                    if (phaseName in ['integration']) {
                        def mode = new GriffonTestMode(
                            autowire: true
                        )
                        new JUnit4GriffonTestType(rawTypeString, rawTypeString, mode)
                    } else {
                        new JUnit4GriffonTestType(rawTypeString, rawTypeString)
                    }
                } else {
                    rawType
                }
            }
        }
    }

    // Using targetPhasesAndTypes, filter down convertedPhases into filteredPhases
    filteredPhases = null
    if (targetPhasesAndTypes.size() == 0) {
        filteredPhases = convertedPhases // no type or phase targeting was applied
    } else {
        filteredPhases = [:]
        convertedPhases.each { phaseName, types ->
            if (targetPhasesAndTypes.containsKey(phaseName) || targetPhasesAndTypes.containsKey(TEST_PHASE_WILDCARD)) {
                def targetTypesForPhase = (targetPhasesAndTypes[phaseName] ?: []) + (targetPhasesAndTypes[TEST_PHASE_WILDCARD] ?: [])
                types.each { type ->
                    if (type.name in targetTypesForPhase || TEST_TYPE_WILDCARD in targetTypesForPhase) {
                        if (!filteredPhases.containsKey(phaseName)) filteredPhases[phaseName] = []
                        filteredPhases[phaseName] << type
                    }
                }
            }
        }
    }

    try {
        griffonSettings.testDependencies.each {
            addUrlIfNotPresent rootLoader, it
            addUrlIfNotPresent classLoader, it
        }
        griffonSettings.runtimeDependencies.each {
            addUrlIfNotPresent rootLoader, it
            addUrlIfNotPresent classLoader, it
        }

        if(isDebugEnabled()) {
            debug "=== RootLoader urls === "
            rootLoader.URLs.each{debug("  $it")}
        }

        // Process the tests in each phase that is configured to run.
        filteredPhases.each { phase, types ->
            currentTestPhaseName = phase
            
            // Add a blank line before the start of this phase so that it
            // is easier to distinguish
            println()

            event("StatusUpdate", ["Starting $phase test phase"])
            event("TestPhaseStart", [phase])

            "${phase}TestPhasePreparation"()

            // Now run all the tests registered for this phase.
            types.each(processTests)

            // Perform any clean up required.
            this."${phase}TestPhaseCleanUp"()

            event("TestPhaseEnd", [phase])
            currentTestPhaseName = null
        }
    } finally {
        String msg = testsFailed ? "\nTests FAILED" : "\nTests PASSED"
        if (createTestReports) {
            event("TestProduceReports", [])
            msg += " - view reports in ${testReportsDir}"
        }
        event("StatusFinal", [msg])
        event("TestPhasesEnd", [])
    }

    testsFailed ? 1 : 0
}

/**
 * Compiles and runs all the tests of the given type and then generates
 * the reports for them.
 * @param type The type of the tests to compile (not the test phase!)
 * For example, "unit", "jsunit", "webtest", etc.
 */
processTests = { GriffonTestType type ->
    currentTestTypeName = type.name
    
    def relativePathToSource = type.relativeSourcePath
    def dest = null
    if (relativePathToSource) {
        def source = new File("${testSourceDir}", relativePathToSource)
        if (!source.exists()) return // no source, no point continuing

        dest = new File(griffonSettings.testClassesDir, relativePathToSource)
        compileTests(type, source, dest)
    }
    
    runTests(type, dest)
    currentTestTypeName = null
}

/**
 * Compiles all the test classes for a particular type of test, for
 * example "unit" or "webtest". Assumes that the source files are in
 * the "test/$type" directory. It also compiles the files to distinct
 * directories for each test type: "$testClassesDir/$type".
 * @param type The type of the tests to compile (not the test phase!)
 * For example, "unit", "jsunit", "webtest", etc.
 */
compileTests = { GriffonTestType type, File source, File dest ->
    event("TestCompileStart", [type])

    def destDir = new File(griffonSettings.testClassesDir.absolutePath, type.name)
    ant.mkdir(dir: destDir.path)

    compileSources(destDir, 'griffon.test.classpath') {
        javac(classpathref: 'griffon.test.classpath', debug:"yes")
        src(path: source)
    }
    addUrlIfNotPresent rootLoader, griffonSettings.classesDir
    addUrlIfNotPresent rootLoader, destDir
    addUrlIfNotPresent classLoader, destDir
 
    if(argsMap.compileTrace) {
        println('-'*80)
        println "[GRIFFON] classLoader urls"
        classLoader.URLs.each{println("  $it")}
        println "[GRIFFON] rootLoader urls"
        rootLoader.URLs.each{println("  $it")}
        println('-'*80)
    }

    event("TestCompileEnd", [type])
}

runTests = { GriffonTestType type, File compiledClassesDir ->
    def testCount = type.prepare(testTargetPatterns, compiledClassesDir, binding)
    
    if (testCount) {
        try {
            event("TestSuiteStart", [type.name])

            println ""
            println "-------------------------------------------------------"
            println "Running ${testCount} $type.name test${testCount > 1 ? 's' : ''}..."

            def start = new Date()
            def result = type.run(testEventPublisher)
            def end = new Date()
            
            event("StatusUpdate", ["Tests Completed in ${end.time - start.time}ms"])

            if (result.failCount > 0) testsFailed = true
            
            println "-------------------------------------------------------"
            println "Tests passed: ${result.passCount}"
            println "Tests failed: ${result.failCount}"
            println "-------------------------------------------------------"
            event("TestSuiteEnd", [type.name])
        } catch (Exception e) {
            event("StatusFinal", ["Error running $type.name tests: ${e.toString()}"])
            GriffonUtil.deepSanitize(e)
            e.printStackTrace()
            testsFailed = true
        } finally {
            type.cleanup()
        }
    }
}
unitTestPhasePreparation = {}
unitTestPhaseCleanUp = {}

/**
 * Initialises a persistence context and bootstraps the application.
 */
integrationTestPhasePreparation = {
    packageTests()
    bootstrap()
}

/**
 * Shuts down the bootstrapped Griffon application.
 */
integrationTestPhaseCleanUp = {

}

otherTestPhasePreparation = {}
otherTestPhaseCleanUp = {}

target(packageTests: "Puts some useful things on the classpath for integration tests.") {
    ant.copy(todir: new File(griffonSettings.testClassesDir, "integration").path) {
        fileset(dir: "${basedir}", includes: metadataFile.name)
    }
    ant.copy(todir: griffonSettings.testClassesDir.path, failonerror: false) {
        fileset(dir: "${basedir}/griffon-app/conf", includes: "**", excludes: "*.groovy, log4j*, metainf, dist")
        fileset(dir: "${griffonSettings.sourceDir}/main") {
            include(name: "**/**")
            exclude(name: "**/*.java")
        }
        fileset(dir: "${testSourceDir}/unit") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy")
        }
        fileset(dir: "${testSourceDir}/integration") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy")
        }
    }
}

def getFailedTests() {
    File file = new File("${testReportsDir}/TESTS-TestSuites.xml")
    if (!file.exists()) {
        return []
    }

    def xmlParser = new XmlParser().parse(file)
    def failedTests = xmlParser.testsuite.findAll { it.'@failures' =~ /.*[1-9].*/ || it.'@errors' =~ /.*[1-9].*/}

    return failedTests.collect {
        String testName = it.'@name'
        testName = testName.replace('Tests', '')
        def pkg = it.'@package'
        if (pkg) {
            testName = pkg + '.' + testName
        }
        return testName
    }
}
