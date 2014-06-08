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
package griffon.util

import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class ServiceLoaderUtilsSpec extends Specification {
    def "Read service entries from #path"() {
        given:
        def processor = new ServiceLoaderUtils.LineProcessor() {
            List<String> lines = []

            @Override
            void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                lines << line
            }
        }

        when:
        ServiceLoaderUtils.load(getClass().classLoader, path, String, processor)

        then:
        processor.lines == result

        where:
        path              | result
        'META-INF/entry/'  | ['one', 'two', 'three']
        'META-INF/entry' | ['one', 'two', 'three']
        'META-INF/bogus'  | []
    }
}
