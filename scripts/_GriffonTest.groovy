/*
* Copyright 2004-2008 the original author or authors.
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

//import griffon.util.GriffonWebUtil as GWU

import java.lang.reflect.Modifier
import junit.framework.AssertionFailedError
import junit.framework.TestCase
import junit.framework.TestResult
import junit.framework.TestSuite
import junit.framework.Test
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.codehaus.griffon.commons.GriffonContext
import org.codehaus.griffon.util.GriffonUtil
import org.codehaus.griffon.support.GriffonTestSuite
//import org.codehaus.griffon.support.PersistenceContextInterceptor
//import org.codehaus.griffon.web.servlet.GriffonApplicationAttributes
import org.codehaus.griffon.util.GriffonNameUtils
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.context.request.RequestContextHolder
/**
 * Gant script that runs the Griffon unit tests
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonBootstrap")

generateLog4jFile = true
griffonApp = null
appCtx = null
result = new TestResult()
compilationFailures = []
testReportsDir = griffonSettings.testReportsDir

ant.path(id: "griffon.test.classpath", testClasspath)

def processResults = { boolean reportsCreated = true ->
    String msg = "\nTests PASSED"
    int retval = 0
    if (result && (result.errorCount() > 0 || result.failureCount() > 0 || compilationFailures.size > 0)) {
        msg = "\nTests FAILED: ${result.errorCount()} errors, ${result.failureCount()} failures, ${compilationFailures.size} compilation errors"
        retval = 1
    }

    if (reportsCreated) msg += " - view reports in ${testReportsDir}."
    event("StatusFinal", [msg])
    return retval
}

unitOnly = false
integrationOnly = false
xmlOnly = false
reRunTests = false

target(testApp: "The test app implementation target") { Map args = [:] ->
    depends(classpath, compileTests, packageTests)

    ant.mkdir(dir: testReportsDir)
    ant.mkdir(dir: "${testReportsDir}/html")
    ant.mkdir(dir: "${testReportsDir}/plain")

    if (args["unitOnly"]) {
        unitOnly = true
    }
    if (args["integrationOnly"]) {
        integrationOnly = true
    }
    if (args["xmlOnly"]) {
        xmlOnly = true
    }
    if (args["reRunTests"]) {
        reRunTests = true
    }

    def reportsCreated = false
    try {
        event("AllTestsStart", ["Starting test-app"])
        if (!integrationOnly) {
            runUnitTests()
        }
        if (!unitOnly) {
            packageApp()
            runIntegrationTests()
        }
        event("AllTestsEnd", ["Finishing test-app"])
        if (!xmlOnly && !args["noReports"]) {
            reportsCreated = true
            produceReports()
        }
    }
    catch (Exception ex) {
        GriffonUtil.deepSanitize(ex)
        println "Error occured running tests: $ex.message"
        ex.printStackTrace()
        return 1
    }

    processResults(reportsCreated)
}

target(packageTests: "Puts some useful things on the classpath") {
    ant.copy(todir: griffonSettings.testClassesDir.path) {
        fileset(dir: "${basedir}", includes: metadataFile.name)
    }
    ant.copy(todir: griffonSettings.testClassesDir.path, failonerror: false) {
//        fileset(dir: "${basedir}/griffon-app/conf", includes: "**", excludes: "*.groovy, log4j*, hibernate, spring")
        fileset(dir: "${basedir}/griffon-app/conf", includes: "**", excludes: "*.groovy, log4j*, keys, webstart")
//        fileset(dir: "${basedir}/griffon-app/conf/hibernate", includes: "**/**")
        fileset(dir: "${basedir}/src/main") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy")
        }
        fileset(dir: "${basedir}/test/unit") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy)")
        }
        fileset(dir: "${basedir}/test/integration") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy)")
        }
    }

}
target(compileTests: "Compiles the test cases") {
    depends(compile)
    event("CompileStart", ['tests'])

    def destDir = griffonSettings.testClassesDir
    ant.mkdir(dir: destDir.path)
    try {
        def classpathId = "griffon.test.classpath"
        ant.groovyc(destdir: destDir,
//                projectName: griffonAppName,
                classpathref: classpathId) {
            javac(classpathref:classpathId, debug:"yes", target: '1.5')
            src(path:"${basedir}/test/unit")
            src(path:"${basedir}/test/integration")
        }
    }
    catch (Exception e) {
        event("StatusFinal", ["Compilation Error: ${e.message}"])
        return 1
    }

    classLoader.addURL(destDir.toURI().toURL())

    event("CompileEnd", ['tests'])
}

target(produceReports: "Outputs aggregated xml and html reports") {
    ant.junitreport(todir: "${testReportsDir}") {
        fileset(dir: testReportsDir) {
            include(name: "TEST-*.xml")
        }
        report(format: "frames", todir: "${testReportsDir}/html")
    }
}


def populateTestSuite = {suite, testFiles, classLoader,/* ctx,*/ String base ->
    for (r in testFiles) {
        try {
            def fileName = r.URL.toString()
            def endIndex = -8
            if (fileName.endsWith(".java")) {
                endIndex = -6
            }
            def className = fileName[fileName.indexOf(base) + base.size()..endIndex].replace('/' as char, '.' as char)
            def c = classLoader.loadClass(className)
            if (TestCase.isAssignableFrom(c) && !Modifier.isAbstract(c.modifiers)) {
                suite.addTest(new GriffonTestSuite(/*ctx,*/ c))
            }
            else {
                event("StatusUpdate", ["Test ${r.filename} is not a valid test case. It does not implement junit.framework.TestCase or is abstract!"])
            }
        } catch (Exception e) {
            compilationFailures << r.file.name
            event("StatusFinal", ["Error loading test: ${e.message}"])
            GriffonUtil.deepSanitize(e).printStackTrace(System.out)
            return 1
        }
    }
}
def runTests = {suite, TestResult result, Closure callback ->
    for (TestSuite test in suite.tests()) {
        new File("${testReportsDir}/TEST-${test.name}.xml").withOutputStream {xmlOut ->
            new File("${testReportsDir}/plain/TEST-${test.name}.txt").withOutputStream {plainOut ->

                def savedOut = System.out
                def savedErr = System.err

                try {
                    def junitTest = new JUnitTest(test.name)
                    def outBytes = new ByteArrayOutputStream()
                    def errBytes = new ByteArrayOutputStream()
                    System.out = new PrintStream(outBytes)
                    System.err = new PrintStream(errBytes)
                    def xmlOutput = new XMLFormatter(output: xmlOut)
                    xmlOutput.startTestSuite(junitTest)
                    def plainOutput = null
                    if (!xmlOnly){
                        plainOutput = new PlainFormatter(output: plainOut)
                        plainOutput.startTestSuite(junitTest)
                    }
                    savedOut.println "Running test ${test.name}..."
                    def start = System.currentTimeMillis()
                    def runCount = 0
                    def failureCount = 0
                    def errorCount = 0

                    for (i in 0..<test.testCount()) {
                        def thisTest = new TestResult()
                        thisTest.addListener(xmlOutput)
                        if (!xmlOnly){
                            thisTest.addListener(plainOutput)
                        }
                        def t = test.testAt(i)
                        System.out.println "--Output from ${t.name}--"
                        System.err.println "--Output from ${t.name}--"

                        callback(test, {
                            savedOut.print "                    ${t.name}..."
                            event("TestStart", [test, t, thisTest])
                            test.runTest(t, thisTest)
                            event("TestEnd", [test, t, thisTest])
                            thisTest
                        })
                        runCount += thisTest.runCount()
                        failureCount += thisTest.failureCount()
                        errorCount += thisTest.errorCount()

                        if (thisTest.errorCount() > 0 || thisTest.failureCount() > 0) {
                            savedOut.println "FAILURE"
                            thisTest.errors().each {result.addError(t, it.thrownException())}
                            thisTest.failures().each {result.addFailure(t, it.thrownException())}
                        }
                        else {savedOut.println "SUCCESS"}
                    }
                    junitTest.setCounts(runCount, failureCount, errorCount);
                    junitTest.setRunTime(System.currentTimeMillis() - start)

                    def outString = outBytes.toString()
                    def errString = errBytes.toString()
                    if (!xmlOnly){
                        new File("${testReportsDir}/TEST-${test.name}-out.txt").write(outString)
                        new File("${testReportsDir}/TEST-${test.name}-err.txt").write(errString)
                        plainOutput.setSystemOutput(outString)
                        plainOutput.setSystemError(errString)
                        plainOutput.endTestSuite(junitTest)
                    }
                    xmlOutput.setSystemOutput(outString)
                    xmlOutput.setSystemError(errString)
                    xmlOutput.endTestSuite(junitTest)
                } finally {
                    System.out = savedOut
                    System.err = savedErr
                }

            }
        }
    }
}
target(runUnitTests: "Run Griffon' unit tests under the test/unit directory") {
    try {
        loadApp()
        // build views, models and controllers
        // TODO review!!
        griffonApp.realize()

        def testFiles = resolveTestResources {"test/unit/${it}.groovy"}
        testFiles.addAll(resolveTestResources {"test/unit/${it}.java"})
        testFiles = testFiles.findAll {it.exists()}
        if (testFiles.size() == 0) {
            event("StatusUpdate", ["No tests found in test/unit to execute"])
            return
        }

        def suite = new TestSuite()
        classLoader.addURL(new File("test/unit").toURI().toURL())
        populateTestSuite(suite, testFiles, classLoader,/* appCtx,*/ "test/unit/")
        if (suite.testCount() > 0) {

            event("TestSuiteStart", ["unit"])
            int testCases = suite.countTestCases()
            println "-------------------------------------------------------"
            println "Running ${testCases} Unit Test${testCases > 1 ? 's' : ''}..."

            def start = new Date()
            runTests(suite, result) {test, invocation ->
//                for (cls in griffonApp.allArtefacts) {
//                    def emc = new ExpandoMetaClass(cls, true, true)
//                    emc.initialize()
//                    def log = LogFactory.getLog(cls)
//                    emc.getLog = {-> log }
//                    GroovySystem.metaClassRegistry.setMetaClass(cls, emc)
//                }
                invocation()
            }
            def end = new Date()

            event("TestSuiteEnd", ["unit", suite])
            event("StatusUpdate", ["Unit Tests Completed in ${end.time - start.time}ms"])
            println "-------------------------------------------------------"
        }
    }
    catch (Exception e) {
        event("StatusFinal", ["Error running unit tests: ${e.toString()}"])
        GriffonUtil.deepSanitize(e)
        e.printStackTrace()
    }
}

target(runIntegrationTests: "Runs Griffon' tests under the test/integration directory") {
    try {
        // allow user to specify test to run like this...
        //   griffon test-app Author
        //   griffon test-app AuthorController
        def testFiles = resolveTestResources {"test/integration/${it}.groovy"}
        testFiles.addAll(resolveTestResources {"test/integration/${it}.java"})

        if (testFiles.size() == 0) {
            event("StatusUpdate", ["No tests found in test/integration to execute"])
            return
        }

        loadApp()
        configureApp()
        // build views, models and controllers
        // TODO review!!
//        def app = appCtx.getBean(GriffonApplication.APPLICATION_ID)
//        if (app.parentContext == null) {
//            app.applicationContext = appCtx
//        }
        def classLoader = griffonContext.classLoader
//        def classLoader = app.classLoader
        def suite = new TestSuite()

//        populateTestSuite(suite, testFiles, classLoader, appCtx, "test/integration/")
        populateTestSuite(suite, testFiles, classLoader, "test/integration/")
        if (suite.testCount() > 0) {
            int testCases = suite.countTestCases()
            println "-------------------------------------------------------"
            println "Running ${testCases} Integration Test${testCases > 1 ? 's' : ''}..."

            event("TestSuiteStart", ["integration"])

//            def beanNames = appCtx.getBeanNamesForType(PersistenceContextInterceptor)
//            def interceptor = null
//            if (beanNames.size() > 0) interceptor = appCtx.getBean(beanNames[0])


            try {
//                interceptor?.init()

                def start = new Date()

                def savedOut = System.out
                runTests(suite, result) {test, invocation ->
//                    name = test.name[0..-6]
//                    def webRequest = GWU.bindMockWebRequest(appCtx)
//                    webRequest.getServletContext().setAttribute(GriffonApplicationAttributes.APPLICATION_CONTEXT, appCtx)
//
//                    // @todo this is horrible and dirty, should find a better way
//                    if (name.endsWith("Controller")) {
//                        webRequest.controllerName = GriffonNameUtils.getLogicalPropertyName(name, "Controller")
//                    }
//                    else {
//                        // Provide a default 'current' controller name.
//                        webRequest.controllerName = "test"
//                    }
//
//                    def callable = {status ->
//                        invocation()
//                        status?.setRollbackOnly()
//                    }
//                    if (test.isTransactional()) {
//                        if (appCtx.transactionManager) {
//                            def template = new TransactionTemplate(appCtx.transactionManager)
//                            template.execute(callable as TransactionCallback)
//                        } else {
//                            System.out = savedOut
//                            println "Error: There is no test datasource defined and integration test ${test.name} does not set transactional = false"
//                            println "Tests aborted"
//                            return 1
//                        }
//                    }
//                    else {
//                        callable.call()
//                    }
//                    RequestContextHolder.setRequestAttributes(null);
                    invocation()
                }
                def end = new Date()

                event("TestSuiteEnd", ["integration", suite])
                println "Integration Tests Completed in ${end.time - start.time}ms"
                println "-------------------------------------------------------"

            }
            finally {
//                interceptor?.destroy()
            }
        }
    }
    catch (Throwable e) {
        event("StatusUpdate", ["Error executing tests ${e.message}"])
        GriffonUtil.deepSanitize(e)
        e.printStackTrace(System.out)
        event("StatusFinal", ["Error running tests: ${e.toString()}"])
        return 1
    }
}

def resolveTestResources(patternResolver) {
    def testNames = getTestNames(argsMap["params"])

    if (!testNames) {
        testNames = buildConfig.griffon.testing.patterns ?: ['**/*']
    }

    def testResources = []
    testNames.each {
        def testFiles = resolveResources(patternResolver(it))
        testResources.addAll(testFiles.findAll {it.exists()})
    }
    testResources
}

def getFailedTests() {
    File file = new File("${testReportsDir}/TESTS-TestSuites.xml")
    if (file.exists()) {
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
    } else {
        return []
    }
}

def getTestNames(testNames) {
    if (reRunTests) {
        testNames += getFailedTests()
    }

    // If a list of test class names is provided, split it into ant
    // file patterns.
    def nameSuffix = 'Tests'
    if (buildConfig.griffon.testing.nameSuffix) {
        nameSuffix = buildConfig.griffon.testing.nameSuffix
    }

    if (testNames) {
        testNames = testNames.collect {
            // If the test name includes a package, replace it with the
            // corresponding file path.
            if (it.indexOf('.') != -1) {
                it = it.replace('.' as char, '/' as char)
            }
            else {
                // Allow the test class to be in any package.
                it = "**/$it"
            }
            return "${it}${nameSuffix}"
        }
    }

    return testNames
}
/**
 * Extended junit formatters that santizes stack traces
 *
 */
class PlainFormatter extends org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter{

    public void addFailure(Test test, Throwable throwable) {
        GriffonUtil.deepSanitize(throwable)
        super.addFailure(test, (Throwable)throwable);
    }


    public void addError(Test test, Throwable throwable) {
        GriffonUtil.deepSanitize(throwable)
        super.addError(test, throwable);
    }


}
class XMLFormatter extends org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter{

    public void addFailure(Test test, Throwable throwable) {
        GriffonUtil.deepSanitize(throwable)
        super.addFailure(test, (Throwable)throwable);
    }

    public void addError(Test test, Throwable throwable) {
        GriffonUtil.deepSanitize(throwable)
        super.addError(test, throwable);
    }

}
