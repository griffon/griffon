/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.plugins.domain

import spock.lang.Specification

class PersistentSpecSupport extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    protected void insertEmployeesIntoDataset() {
        [
            [name: 'Alice', lastname: 'Alison'],
            [name: 'Alice', lastname: 'Elmerson'],
            [name: 'Bob', lastname: 'Alison'],
            [name: 'Bob', lastname: 'Elmerson'],
            [name: 'Charles', lastname: 'Charleson'],
            [name: 'David', lastname: 'Davison'],
            [name: 'Elmer', lastname: 'Elmerson']
        ].each { data ->
            Employee.create(data).save()
        }
    }
}
