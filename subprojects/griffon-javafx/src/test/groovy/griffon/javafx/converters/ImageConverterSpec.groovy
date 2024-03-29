/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.javafx.converters

import griffon.converter.ConversionException
import javafx.embed.swing.JFXPanel
import javafx.scene.image.Image
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ImageConverterSpec extends Specification {
    @Shared
    private Image sharedImage = new Image(getClass().getClassLoader().getResourceAsStream('griffon-icon-16x16.png'))

    @Shared
    private File basePath = new File('build/resources/test')

    void setupSpec() {
        // force toolkit initialization
        new JFXPanel()
    }

    void "Image format '#format' should be equal to #image"() {
        setup:
        ImageConverter converter = new ImageConverter()

        when:
        Image result = converter.fromObject(format)

        then:
        image == result

        where:
        image       | format
        null        | null
        null        | ''
        null        | ' '
        sharedImage | sharedImage
    }

    void "Valid image format '#format'"() {
        setup:
        ImageConverter converter = new ImageConverter()

        when:
        Image image = converter.fromObject(format)

        then:
        image

        where:
        format << [
            new File(basePath, 'griffon-icon-16x16.png'),
            new File(basePath, 'griffon-icon-16x16.png').toURI(),
            new File(basePath, 'griffon-icon-16x16.png').toURI().toURL(),
            new FileInputStream(new File(basePath, 'griffon-icon-16x16.png'))
        ]
    }

    void "Invalid image format '#format'"() {
        setup:

        ImageConverter converter = new ImageConverter()

        when:
        converter.fromObject(format)

        then:

        thrown(ConversionException)

        where:
        format << [
            'garbage',
            [],
            [1],
            [1, 2],
            [1, 2, 3, 4, 5],
            'F00',
            '#F0',
            '#FF0000FF00',
            ['HH', 'FF', '00'],
            new Object(),
            [new Object()]
        ]
    }
}
