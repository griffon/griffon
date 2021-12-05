/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.compile.core.processor.annotation;

import org.kordamp.jipsy.processor.AbstractResourcePersistence;
import org.kordamp.jipsy.processor.Logger;
import org.kordamp.jipsy.processor.SimpleFileFilter;

import javax.annotation.processing.Filer;
import java.io.FileFilter;

/**
 * @author Andres Almiray
 */
public class AnnotationHandlerPersistence extends AbstractResourcePersistence {
    public AnnotationHandlerPersistence(String name, String root, Filer filer, Logger logger) {
        super(filer, name, logger, root + "META-INF/annotations/");
    }

    @Override
    protected FileFilter getFileFilter() {
        return SimpleFileFilter.INSTANCE;
    }
}
