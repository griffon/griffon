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
package griffon.util;

import griffon.core.editors.URIPropertyEditor;
import griffon.exceptions.TypeConversionException;
import org.junit.Test;

import java.beans.PropertyEditorManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static griffon.util.CollectionUtils.newList;
import static griffon.util.CollectionUtils.newSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeUtilsTest {
    @Test
    public void setEquality() {
        Set<?> s1 = newSet(2, "a");
        Set<?> s2 = newSet("a", 2);
        Set<?> s3 = newSet(3, "a");
        Set<?> s4 = newSet(3);

        assertTrue(TypeUtils.equals((Set) null, (Set) null));
        assertFalse(TypeUtils.equals(s1, (Set) null));
        assertFalse(TypeUtils.equals((Set) null, s2));
        assertTrue(TypeUtils.equals(s1, s1));
        assertTrue(TypeUtils.equals(s1, s2));
        assertFalse(TypeUtils.equals(s1, s3));
        assertFalse(TypeUtils.equals(s1, s4));

        // force equals(Object, Object)
        assertTrue(TypeUtils.equals((Object) s1, (Object) s2));
        assertFalse(TypeUtils.equals((Object) s1, (Object) s3));
    }

    @Test
    public void mapEquality() {
        Map<String, String> m1 = CollectionUtils.<String, String>newMap("k1", "v1", "k2", "v2");
        Map<String, String> m2 = CollectionUtils.<String, String>newMap("k2", "v2", "k1", "v1");
        Map<String, String> m3 = CollectionUtils.<String, String>newMap("k1", "v1", "k3", "v3");
        Map<String, String> m4 = CollectionUtils.<String, String>newMap("k1", "v1");

        assertTrue(TypeUtils.equals((Map) null, (Map) null));
        assertFalse(TypeUtils.equals(m1, (Map) null));
        assertFalse(TypeUtils.equals((Map) null, m2));
        assertTrue(TypeUtils.equals(m1, m1));
        assertTrue(TypeUtils.equals(m1, m2));
        assertFalse(TypeUtils.equals(m1, m3));
        assertFalse(TypeUtils.equals(m1, m4));

        // force equals(Object, Object)
        assertTrue(TypeUtils.equals((Object) m1, (Object) m2));
        assertFalse(TypeUtils.equals((Object) m1, (Object) m3));
    }

    @Test
    public void listEquality() {
        List<?> l1 = newList(2, "a");
        List<?> l2 = newList(2, "a");
        List<?> l3 = newList(3, "a");
        List<?> l4 = newList(3);

        assertTrue(TypeUtils.equals((List) null, (List) null));
        assertFalse(TypeUtils.equals(l1, (List) null));
        assertFalse(TypeUtils.equals((List) null, l2));
        assertTrue(TypeUtils.equals(l1, l1));
        assertTrue(TypeUtils.equals(l1, l2));
        assertFalse(TypeUtils.equals(l1, l3));
        assertFalse(TypeUtils.equals(l1, l4));

        // force equals(Object, Object)
        assertTrue(TypeUtils.equals((Object) l1, (Object) l2));
        assertFalse(TypeUtils.equals((Object) l1, (Object) l3));
    }

    @Test
    public void arrayEquality() {
        Object[] a1 = new Object[]{2, "a"};
        Object[] a2 = new Object[]{2, "a"};
        Object[] a3 = new Object[]{3, "a"};
        Object[] a4 = new Object[]{2};

        assertTrue(TypeUtils.equals((Object[]) null, (Object[]) null));
        assertFalse(TypeUtils.equals(a1, (Object[]) null));
        assertFalse(TypeUtils.equals((Object[]) null, a2));
        assertTrue(TypeUtils.equals(a1, a1));
        assertTrue(TypeUtils.equals(a1, a2));
        assertFalse(TypeUtils.equals(a1, a3));
        assertFalse(TypeUtils.equals(a1, a4));
        assertTrue(TypeUtils.arrayEqual(null, null));
        assertFalse(TypeUtils.arrayEqual(a1, null));
        assertFalse(TypeUtils.arrayEqual(null, a2));

        // force equals(Object, Object)

        assertTrue(TypeUtils.equals((Object) a1, (Object) a2));
        assertFalse(TypeUtils.equals((Object) a1, (Object) a3));
    }

    @Test
    public void objectEquality() {
        Object o1 = new Object();
        Object o2 = new Object();

        assertTrue(TypeUtils.equals((Object) null, (Object) null));
        assertFalse(TypeUtils.equals(o1, (Object) null));
        assertFalse(TypeUtils.equals((Object) null, o2));
        assertTrue(TypeUtils.equals(o1, o1));
        assertFalse(TypeUtils.equals(o1, o2));
        assertTrue(TypeUtils.equals(1, Integer.valueOf(1)));
        assertTrue(TypeUtils.equals("a", "a"));
        assertFalse(TypeUtils.equals("a", "b"));
    }

    @Test
    public void equality() {
        Integer[] integers1 = new Integer[]{Integer.valueOf(1), Integer.valueOf(2)};
        Integer[] integers2 = new Integer[]{Integer.valueOf(1), Integer.valueOf(2), new Integer(3)};
        int[] ints1 = {1, 2};
        int[] ints2 = {1, 2, 3};
        List<Integer> lintegers = asList(Integer.valueOf(1), Integer.valueOf(2));

        assertTrue(TypeUtils.equals(integers1, integers1));
        assertTrue(TypeUtils.equals(ints1, ints1));
        assertFalse(TypeUtils.equals(integers1, ints2));
        assertFalse(TypeUtils.equals(ints1, integers2));
        assertTrue(TypeUtils.equals(integers1, lintegers));
        assertTrue(TypeUtils.equals(lintegers, integers1));
        assertTrue(TypeUtils.equals(ints1, lintegers));
        assertTrue(TypeUtils.equals(lintegers, ints1));

        // force equals(Object, Object)
        assertTrue(TypeUtils.equals((Object) integers1, (Object) integers1));
        assertTrue(TypeUtils.equals((Object) ints1, (Object) ints1));
        assertFalse(TypeUtils.equals((Object) integers1, (Object) ints2));
        assertFalse(TypeUtils.equals((Object) ints1, (Object) integers2));
        assertTrue(TypeUtils.equals((Object) integers1, (Object) lintegers));
        assertTrue(TypeUtils.equals((Object) lintegers, (Object) integers1));
        assertTrue(TypeUtils.equals((Object) ints1, (Object) lintegers));
        assertTrue(TypeUtils.equals((Object) lintegers, (Object) ints1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void castToNumberCantHandleNonNumberValues() {
        assertEquals(1, TypeUtils.castToNumber(1));
        TypeUtils.castToNumber(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void castToNumberWithDefaultCantHandleNonNumberValues() {
        assertEquals(1, TypeUtils.castToNumber(null, 1));
        TypeUtils.castToNumber(new Object(), 1);
    }

    @Test(expected = TypeConversionException.class)
    public void convertValue() throws URISyntaxException {
        URI expected = new URI("http://localhost");
        assertEquals(expected, TypeUtils.convertValue(URI.class, expected));
        PropertyEditorManager.registerEditor(URI.class, URIPropertyEditor.class);
        assertEquals(expected, TypeUtils.convertValue(URI.class, "http://localhost"));
        assertEquals(expected, TypeUtils.convertValue(URI.class, "http://localhost", "format"));
        TypeUtils.convertValue(TypeUtils.class, expected);
    }
}
