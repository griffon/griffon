/*
 * Copyright 2008-2016 the original author or authors.
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
package org.codehaus.griffon.compile.core.ast.type;

import org.kordamp.gipsy.transform.AbstractFilePersistence;
import org.kordamp.jipsy.processor.Logger;
import org.kordamp.jipsy.processor.SimpleFileFilter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Andres Almiray
 * @since 2.1.0
 */
public class TypePersistence extends AbstractFilePersistence {
    public TypePersistence(String name, String root, File outputDir, Logger logger) {
        super(outputDir, name, logger, root + "/META-INF/types/");
    }

    @Override
    protected FileFilter getFileFilter() {
        return SimpleFileFilter.INSTANCE;
    }
}
