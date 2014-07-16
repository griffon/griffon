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

package org.codehaus.griffon.gradle.shadow.transformers

import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GriffonFileTransformerSpec extends Specification {
    void "Path #path #transform transformed"() {
        given:
        Transformer transformer = new GriffonFileTransformer()

        when:
        boolean actual = transformer.canTransformResource(path)

        then:
        actual == expected

        where:
        path                || expected
        'META-INF/griffon'  || true
        'META-INF/services' || false

        transform = expected ? 'can be' : 'can not be'
    }
}
