/*
 * Copyright 2008-2017 the original author or authors.
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

import org.junit.Before;
import org.junit.Test;
import org.kordamp.jipsy.processor.testutils.NoOutputTestBase;
import org.kordamp.jipsy.processor.testutils.TestInitializer;
import org.kordamp.jipsy.processor.testutils.TestLogger;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class AnnotationHandlerCollectorTest extends NoOutputTestBase {
    private TestInitializer initializer;
    private TestLogger logger;
    private AnnotationHandlerCollector collector;

    @Before
    public void loadFrameWork() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("annotation1", "type1=provider1\n");
        map.put("annotation2", "type1=provider1\ntype2=provider2\n");
        map.put("annotation3", "type1=provider1\ntype3=provider3\n");
        initializer = new TestInitializer(map);
        logger = new TestLogger();
        collector = new AnnotationHandlerCollector(initializer, logger);
    }

    @Test(expected = NullPointerException.class)
    public void testGetAnnotationHandlerNull() {
        collector.getAnnotationHandler(null, null);
    }

    @Test
    public void testGetAnnotationHandlerExisting() {
        assertEquals(0, collector.handlers().size());
        collector.getAnnotationHandler("annotation", "type");
        assertEquals(1, collector.handlers().size());
        logger.reset();
        String handler = collector.getAnnotationHandler("annotation", "type");
        assertTrue(logger.records().isEmpty());
        assertEquals("type", handler);
        assertEquals(1, collector.handlers().size());
    }

    @Test
    public void testGetAnnotationHandlerNew() {
        assertEquals(0, collector.handlers().size());
        logger.reset();
        assertEquals("type", collector.getAnnotationHandler("annotation", "type"));
        assertEquals(1, collector.handlers().size());
    }

    @Test
    public void testAnnotationHandlersEmpty() {
        Map<String, String> handlers = collector.handlers();
        assertEquals(0, handlers.size());
    }

    @Test
    public void testAnnotationHandlersOne() {
        collector.getAnnotationHandler("annotation", "type");
        Map<String, String> handlers = collector.handlers();
        assertEquals(1, handlers.size());
        assertTrue(handlers.containsKey("annotation"));
    }

    @Test
    public void testAnnotationHandlersMore() {
        collector.getAnnotationHandler("annotation1", "type1");
        collector.getAnnotationHandler("annotation2", "type2");
        Map<String, String> handlers = collector.handlers();
        assertEquals(2, handlers.size());
        assertTrue(handlers.containsKey("annotation1"));
        assertTrue(handlers.containsKey("annotation2"));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAnnotationHandlerNull() {
        collector.removeAnnotationHandler(null);
    }

    @Test
    public void testRemoveAnnotationHandlerWhenEmpty() {
        collector.removeAnnotationHandler("provider1");
        assertEquals(1, logger.records().size());
        assertEquals("Removing provider1\n", logger.getFileContent());
    }

    @Test
    public void testRemoveAnnotationHandlerWhenInNotOneAnnotationHandler() {
        collector.getAnnotationHandler("annotation1", "type1");
        logger.reset();
        collector.removeAnnotationHandler("provider2");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemoveAnnotationHandlerWhenInOneAnnotationHandler() {
        collector.getAnnotationHandler("annotation1", "type1");
        logger.reset();
        collector.removeAnnotationHandler("provider1");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemoveAnnotationHandlerWhenInTwoAnnotationHandlers() {
        collector.getAnnotationHandler("annotation1", "type1");
        collector.getAnnotationHandler("annotation2", "type2");
        logger.reset();
        collector.removeAnnotationHandler("provider1");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemoveAnnotationHandlerWhenInSomeAnnotationHandlers() {
        collector.getAnnotationHandler("annotation1", "type1");
        collector.getAnnotationHandler("annotation2", "type2");
        collector.getAnnotationHandler("annotation3", "type3");
        logger.reset();
        collector.removeAnnotationHandler("provider2");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testToStringEmpty() {
        collector.toString();
    }

    @Test
    public void testToStringNonExistingAnnotationHandler() {
        collector.getAnnotationHandler("nonExistingAnnotationHandler", null);
        collector.toString();
    }

    @Test
    public void testToStringExistingAnnotationHandler() {
        collector.getAnnotationHandler("annotation1", null);
        collector.toString();
    }

    @Test
    public void testToStringMoreExistingAnnotationHandlers() {
        collector.getAnnotationHandler("annotation1", null);
        collector.getAnnotationHandler("annotation2", null);
        collector.toString();
    }
}