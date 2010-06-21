/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.codehaus.griffon.test.junit4

import org.codehaus.griffon.test.junit4.listener.SuiteRunListener
import org.codehaus.griffon.test.junit4.runner.GriffonTestCaseRunnerBuilder
import org.codehaus.griffon.test.junit4.result.JUnit4ResultGriffonTestTypeResultAdapter

import org.codehaus.griffon.test.GriffonTestTypeResult
import org.codehaus.griffon.test.GriffonTestTargetPattern
import org.codehaus.griffon.test.support.GriffonTestTypeSupport
import org.codehaus.griffon.test.support.GriffonTestMode
import org.codehaus.griffon.test.event.GriffonTestEventPublisher
import org.codehaus.griffon.test.report.junit.JUnitReportsFactory

import org.junit.runners.Suite
import org.junit.runner.Result
import org.junit.runner.notification.RunNotifier

import java.lang.reflect.Modifier

/**
 * An {@code GriffonTestType} for JUnit4 tests.
 */
public class JUnit4GriffonTestType extends GriffonTestTypeSupport {
    public static final SUFFIXES = ["Test", "Tests"].asImmutable()

    protected suite
    protected mode

    public JUnit4GriffonTestType(String name, String sourceDirectory) {
        this(name, sourceDirectory, null)
    }

    public JUnit4GriffonTestType(String name, String sourceDirectory, GriffonTestMode mode) {
        super(name, sourceDirectory)
        this.mode = mode
    }

    protected List<String> getTestSuffixes() {
        SUFFIXES
    }

    protected int doPrepare() {
        def testClasses = getTestClasses()
        if (testClasses) {
            suite = createSuite(testClasses)
            suite.testCount()
        } else {
            0
        }
    }

    protected getTestClasses() {
        def classes = []
        eachSourceFile { testTargetPattern, sourceFile ->
            def testClass = sourceFileToClass(sourceFile)
            if (!Modifier.isAbstract(testClass.modifiers)) {
                classes << testClass
            }
        }
        classes
    }

    protected createRunnerBuilder() {
        if (mode) {
            new GriffonTestCaseRunnerBuilder(mode, getApplication(), testTargetPatterns)
        } else {
            new GriffonTestCaseRunnerBuilder(testTargetPatterns)
        }
    }

    protected createSuite(classes) {
        new Suite(createRunnerBuilder(), classes as Class[])
    }

    protected createJUnitReportsFactory() {
        JUnitReportsFactory.createFromBuildBinding(buildBinding)
    }

    protected createListener(eventPublisher) {
        new SuiteRunListener(eventPublisher, createJUnitReportsFactory(), createSystemOutAndErrSwapper())
    }

    protected createNotifier(eventPublisher) {
        def notifier = new RunNotifier()
        notifier.addListener(createListener(eventPublisher))
        notifier
    }

    protected GriffonTestTypeResult doRun(GriffonTestEventPublisher eventPublisher) {
        def notifier = createNotifier(eventPublisher)
        def result = new Result()
        notifier.addListener(result.createListener())
        suite.run(notifier)

        notifier.fireTestRunFinished(result)
        new JUnit4ResultGriffonTestTypeResultAdapter(result)
    }
}
