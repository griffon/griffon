/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.test.report.junit

import griffon.build.GriffonBuildListener

class JUnitReportProcessor implements GriffonBuildListener {
    void receiveGriffonBuildEvent(String name, Object[] args) {
        if (name == "TestProduceReports") {
            def buildBinding = args[0]
            buildBinding.with { 
                ant.junitreport(todir: "${testReportsDir}") {
                    fileset(dir: testReportsDir) {
                        include(name: "TEST-*.xml")
                    }
                    report(format: "frames", todir: "${testReportsDir}/html")
                }
            }
        }
    }
}
