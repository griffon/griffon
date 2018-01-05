/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.compile.core.processor.editor;

import org.junit.Before;
import org.junit.Test;
import org.kordamp.jipsy.processor.testutils.NoOutputTestBase;
import org.kordamp.jipsy.processor.testutils.TestInitializer;
import org.kordamp.jipsy.processor.testutils.TestLogger;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class PropertyEditorCollectorTest extends NoOutputTestBase {
    private TestInitializer initializer;
    private TestLogger logger;
    private PropertyEditorCollector collector;

    @Before
    public void loadFrameWork() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("editor1", "type1=provider1\n");
        map.put("editor2", "type1=provider1\ntype2=provider2\n");
        map.put("editor3", "type1=provider1\ntype3=provider3\n");
        initializer = new TestInitializer(map);
        logger = new TestLogger();
        collector = new PropertyEditorCollector(initializer, logger);
    }

    @Test(expected = NullPointerException.class)
    public void testGetPropertyEditorNull() {
        collector.getPropertyEditor(null, null);
    }

    @Test
    public void testGetPropertyEditorExisting() {
        assertEquals(0, collector.editors().size());
        collector.getPropertyEditor("editor", "type");
        assertEquals(1, collector.editors().size());
        logger.reset();
        String editor = collector.getPropertyEditor("editor", "type");
        assertTrue(logger.records().isEmpty());
        assertEquals("type", editor);
        assertEquals(1, collector.editors().size());
    }

    @Test
    public void testGetPropertyEditorNew() {
        assertEquals(0, collector.editors().size());
        logger.reset();
        assertEquals("type", collector.getPropertyEditor("editor", "type"));
        assertEquals(1, collector.editors().size());
    }

    @Test
    public void testPropertyEditorsEmpty() {
        Map<String, String> editors = collector.editors();
        assertEquals(0, editors.size());
    }

    @Test
    public void testPropertyEditorsOne() {
        collector.getPropertyEditor("editor", "type");
        Map<String, String> editors = collector.editors();
        assertEquals(1, editors.size());
        assertTrue(editors.containsKey("editor"));
    }

    @Test
    public void testPropertyEditorsMore() {
        collector.getPropertyEditor("editor1", "type1");
        collector.getPropertyEditor("editor2", "type2");
        Map<String, String> editors = collector.editors();
        assertEquals(2, editors.size());
        assertTrue(editors.containsKey("editor1"));
        assertTrue(editors.containsKey("editor2"));
    }

    @Test(expected = NullPointerException.class)
    public void testRemovePropertyEditorNull() {
        collector.removePropertyEditor(null);
    }

    @Test
    public void testRemovePropertyEditorWhenEmpty() {
        collector.removePropertyEditor("provider1");
        assertEquals(1, logger.records().size());
        assertEquals("Removing provider1\n", logger.getFileContent());
    }

    @Test
    public void testRemovePropertyEditorWhenInNotOnePropertyEditor() {
        collector.getPropertyEditor("editor1", "type1");
        logger.reset();
        collector.removePropertyEditor("provider2");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemovePropertyEditorWhenInOnePropertyEditor() {
        collector.getPropertyEditor("editor1", "type1");
        logger.reset();
        collector.removePropertyEditor("provider1");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemovePropertyEditorWhenInTwoPropertyEditors() {
        collector.getPropertyEditor("editor1", "type1");
        collector.getPropertyEditor("editor2", "type2");
        logger.reset();
        collector.removePropertyEditor("provider1");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemovePropertyEditorWhenInSomePropertyEditors() {
        collector.getPropertyEditor("editor1", "type1");
        collector.getPropertyEditor("editor2", "type2");
        collector.getPropertyEditor("editor3", "type3");
        logger.reset();
        collector.removePropertyEditor("provider2");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testToStringEmpty() {
        collector.toString();
    }

    @Test
    public void testToStringNonExistingPropertyEditor() {
        collector.getPropertyEditor("nonExistingPropertyEditor", null);
        collector.toString();
    }

    @Test
    public void testToStringExistingPropertyEditor() {
        collector.getPropertyEditor("editor1", null);
        collector.toString();
    }

    @Test
    public void testToStringMoreExistingPropertyEditors() {
        collector.getPropertyEditor("editor1", null);
        collector.getPropertyEditor("editor2", null);
        collector.toString();
    }
}