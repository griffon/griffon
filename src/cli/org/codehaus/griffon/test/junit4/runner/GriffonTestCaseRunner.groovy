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

package org.codehaus.griffon.test.junit4.runner

import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.codehaus.griffon.test.junit4.JUnit4GriffonTestType
import org.codehaus.griffon.test.GriffonTestTargetPattern

class GriffonTestCaseRunner extends BlockJUnit4ClassRunner {
    final testTargetPatterns

    GriffonTestCaseRunner(Class testClass, GriffonTestTargetPattern[] testTargetPatterns) {
        super(testClass)
        this.testTargetPatterns = testTargetPatterns
    }

    protected List<FrameworkMethod> computeTestMethods() {
        def annotated = super.computeTestMethods()
        testClass.javaClass.methods.each { method ->
            if (method.name.size() > 4 && method.name[0..3] == "test" && method.parameterTypes.size() == 0) {
                def existing = annotated.find { it.method == method }
                if (!existing) {
                    annotated << new FrameworkMethod(method)
                }
            }
        }
        
        def methodMatchingTargetPatterns = testTargetPatterns?.findAll { it.methodTargeting }
        if (methodMatchingTargetPatterns) { // slow lane, filter methods
            def patternsForThisClass = testTargetPatterns.findAll { 
                it.matchesClass(testClass.javaClass.name, JUnit4GriffonTestType.SUFFIXES as String[]) 
            }
            if (patternsForThisClass) {
                annotated.findAll { frameworkMethod ->
                    patternsForThisClass.any { pattern -> pattern.matchesMethod(frameworkMethod.name) }
                }
            } else {
                annotated
            }
        } else { // fast lane
            annotated
        }
    }
}
