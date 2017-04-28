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
package griffon.util;

import griffon.core.editors.PropertyEditorResolver;
import griffon.core.editors.URIPropertyEditor;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("UnnecessaryBoxing")
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
    @SuppressWarnings("UnnecessaryBoxing")
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

    public void convertValue() throws URISyntaxException {
        URI expected = new URI("http://localhost");
        assertEquals(expected, TypeUtils.convertValue(URI.class, expected));
        PropertyEditorResolver.registerEditor(URI.class, URIPropertyEditor.class);
        assertEquals(expected, TypeUtils.convertValue(URI.class, "http://localhost"));
        assertEquals(expected, TypeUtils.convertValue(URI.class, "http://localhost", "format"));
        TypeUtils.convertValue(TypeUtils.class, expected);
    }

    @Test
    public void convertBooleans() {
        assertEquals(true, (boolean) TypeUtils.convertValue(Boolean.TYPE, Boolean.TRUE));
        assertEquals(Boolean.TRUE, TypeUtils.convertValue(Boolean.class, Boolean.TRUE));
    }

    @Test
    @SuppressWarnings("UnnecessaryBoxing")
    public void convertCharacters() {
        assertEquals('A', (char) TypeUtils.convertValue(Character.TYPE, Character.valueOf('A')));
        assertEquals(Character.valueOf('A'), TypeUtils.convertValue(Character.class, Character.valueOf('A')));
    }

    @Test
    public void convertNumbersIntoPrimitiveByte() {
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, Byte.valueOf("10")));
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, Short.valueOf("10")));
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, Integer.valueOf("10")));
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, Long.valueOf("10")));
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, Float.valueOf("10")));
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, Double.valueOf("10")));
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, new BigInteger("10")));
        assertEquals((byte) 10, (byte) TypeUtils.convertValue(Byte.TYPE, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoWrapperByte() {
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, Byte.valueOf("10")));
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, Short.valueOf("10")));
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, Integer.valueOf("10")));
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, Long.valueOf("10")));
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, Float.valueOf("10")));
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, Double.valueOf("10")));
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, new BigInteger("10")));
        assertEquals(Byte.valueOf("10"), TypeUtils.convertValue(Byte.class, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoPrimitiveShort() {
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, Byte.valueOf("10")));
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, Short.valueOf("10")));
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, Integer.valueOf("10")));
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, Long.valueOf("10")));
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, Float.valueOf("10")));
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, Double.valueOf("10")));
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, new BigInteger("10")));
        assertEquals((short) 10, (short) TypeUtils.convertValue(Short.TYPE, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoWrapperShort() {
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, Byte.valueOf("10")));
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, Short.valueOf("10")));
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, Integer.valueOf("10")));
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, Long.valueOf("10")));
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, Float.valueOf("10")));
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, Double.valueOf("10")));
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, new BigInteger("10")));
        assertEquals(Short.valueOf("10"), TypeUtils.convertValue(Short.class, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoPrimitiveInteger() {
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, Byte.valueOf("10")));
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, Short.valueOf("10")));
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, Integer.valueOf("10")));
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, Long.valueOf("10")));
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, Float.valueOf("10")));
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, Double.valueOf("10")));
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, new BigInteger("10")));
        assertEquals((int) 10, (int) TypeUtils.convertValue(Integer.TYPE, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoWrapperInteger() {
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, Byte.valueOf("10")));
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, Short.valueOf("10")));
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, Integer.valueOf("10")));
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, Long.valueOf("10")));
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, Float.valueOf("10")));
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, Double.valueOf("10")));
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, new BigInteger("10")));
        assertEquals(Integer.valueOf("10"), TypeUtils.convertValue(Integer.class, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoPrimitiveLong() {
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, Byte.valueOf("10")));
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, Short.valueOf("10")));
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, Integer.valueOf("10")));
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, Long.valueOf("10")));
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, Float.valueOf("10")));
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, Double.valueOf("10")));
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, new BigInteger("10")));
        assertEquals((long) 10, (long) TypeUtils.convertValue(Long.TYPE, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoWrapperLong() {
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, Byte.valueOf("10")));
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, Short.valueOf("10")));
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, Integer.valueOf("10")));
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, Long.valueOf("10")));
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, Float.valueOf("10")));
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, Double.valueOf("10")));
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, new BigInteger("10")));
        assertEquals(Long.valueOf("10"), TypeUtils.convertValue(Long.class, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoPrimitiveFloat() {
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, Byte.valueOf("10")), 0);
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, Short.valueOf("10")), 0);
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, Integer.valueOf("10")), 0);
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, Long.valueOf("10")), 0);
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, Float.valueOf("10")), 0);
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, Double.valueOf("10")), 0);
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, new BigInteger("10")), 0);
        assertEquals((float) 10, (float) TypeUtils.convertValue(Float.TYPE, new BigDecimal("10")), 0);
    }

    @Test
    public void convertNumbersIntoWrapperFloat() {
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, Byte.valueOf("10")));
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, Short.valueOf("10")));
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, Integer.valueOf("10")));
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, Long.valueOf("10")));
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, Float.valueOf("10")));
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, Double.valueOf("10")));
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, new BigInteger("10")));
        assertEquals(Float.valueOf("10"), TypeUtils.convertValue(Float.class, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoPrimitiveDouble() {
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, Byte.valueOf("10")), 0);
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, Short.valueOf("10")), 0);
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, Integer.valueOf("10")), 0);
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, Long.valueOf("10")), 0);
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, Float.valueOf("10")), 0);
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, Double.valueOf("10")), 0);
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, new BigInteger("10")), 0);
        assertEquals((double) 10, (double) TypeUtils.convertValue(Double.TYPE, new BigDecimal("10")), 0);
    }

    @Test
    public void convertNumbersIntoWrapperDouble() {
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, Byte.valueOf("10")));
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, Short.valueOf("10")));
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, Integer.valueOf("10")));
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, Long.valueOf("10")));
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, Float.valueOf("10")));
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, Double.valueOf("10")));
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, new BigInteger("10")));
        assertEquals(Double.valueOf("10"), TypeUtils.convertValue(Double.class, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoWrapperBigInteger() {
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, Byte.valueOf("10")));
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, Short.valueOf("10")));
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, Integer.valueOf("10")));
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, Long.valueOf("10")));
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, Float.valueOf("10")));
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, Double.valueOf("10")));
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, new BigInteger("10")));
        assertEquals(BigInteger.valueOf(10L), TypeUtils.convertValue(BigInteger.class, new BigDecimal("10")));
    }

    @Test
    public void convertNumbersIntoWrapperBigDecimal() {
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, Byte.valueOf("10")));
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, Short.valueOf("10")));
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, Integer.valueOf("10")));
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, Long.valueOf("10")));
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, Float.valueOf("10")));
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, Double.valueOf("10")));
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, new BigInteger("10")));
        assertEquals(BigDecimal.valueOf(10d), TypeUtils.convertValue(BigDecimal.class, new BigDecimal("10.0")));
    }

    @Test
    public void castToBoolean() {
        assertTrue(TypeUtils.castToBoolean(Boolean.TRUE));
        assertTrue(TypeUtils.castToBoolean("TRUE"));
        assertTrue(TypeUtils.castToBoolean("true"));
        assertFalse(TypeUtils.castToBoolean(Boolean.FALSE));
        assertFalse(TypeUtils.castToBoolean("FALSE"));
        assertFalse(TypeUtils.castToBoolean("false"));
        assertTrue(TypeUtils.castToBoolean(null, true));
        assertFalse(TypeUtils.castToBoolean(null, false));
        assertTrue(TypeUtils.castToBoolean(true, true));
        assertTrue(TypeUtils.castToBoolean(true, false));
    }

    @Test
    public void castToChar() {
        assertEquals('A', TypeUtils.castToChar(Character.valueOf('A')));
        assertEquals('A', TypeUtils.castToChar("AB"));
        assertEquals('A', TypeUtils.castToChar("AB", 'B'));
        assertEquals('B', TypeUtils.castToChar(null, 'B'));
    }

    @Test
    public void castToByte() {
        assertEquals((byte) 10, TypeUtils.castToByte(Byte.valueOf("10")));
        assertEquals((byte) 10, TypeUtils.castToByte(Integer.valueOf("10")));
        assertEquals((byte) 10, TypeUtils.castToByte("10"));
        assertEquals((byte) 10, TypeUtils.castToByte("10", (byte) 10));
        assertEquals((byte) 10, TypeUtils.castToByte(null, (byte) 10));
    }

    @Test
    public void castToShort() {
        assertEquals((short) 10, TypeUtils.castToShort(Short.valueOf("10")));
        assertEquals((short) 10, TypeUtils.castToShort(Integer.valueOf("10")));
        assertEquals((short) 10, TypeUtils.castToShort("10"));
        assertEquals((short) 10, TypeUtils.castToShort("10", (short) 10));
        assertEquals((short) 10, TypeUtils.castToShort(null, (short) 10));
    }

    @Test
    public void castToInt() {
        assertEquals(10, TypeUtils.castToInt(Short.valueOf("10")));
        assertEquals(10, TypeUtils.castToInt(Integer.valueOf("10")));
        assertEquals(10, TypeUtils.castToInt("10"));
        assertEquals(10, TypeUtils.castToInt("10", 10));
        assertEquals(10, TypeUtils.castToInt(null, 10));
    }

    @Test
    public void castToLong() {
        assertEquals(10L, TypeUtils.castToLong(Integer.valueOf("10")));
        assertEquals(10L, TypeUtils.castToLong(Long.valueOf("10")));
        assertEquals(10L, TypeUtils.castToLong("10"));
        assertEquals(10L, TypeUtils.castToLong("10", 10L));
        assertEquals(10L, TypeUtils.castToLong(null, 10L));
    }

    @Test
    public void castToFloat() {
        assertEquals(10f, TypeUtils.castToFloat(Integer.valueOf("10")), 0f);
        assertEquals(10f, TypeUtils.castToFloat(Float.valueOf("10")), 0f);
        assertEquals(10f, TypeUtils.castToFloat("10"), 0f);
        assertEquals(10f, TypeUtils.castToFloat("10", 10f), 0f);
        assertEquals(10f, TypeUtils.castToFloat(null, 10f), 0f);
    }

    @Test
    public void castToDouble() {
        assertEquals(10d, TypeUtils.castToDouble(Integer.valueOf("10")), 0d);
        assertEquals(10d, TypeUtils.castToDouble(Double.valueOf("10")), 0d);
        assertEquals(10d, TypeUtils.castToDouble("10"), 0d);
        assertEquals(10d, TypeUtils.castToDouble("10", 10d), 0d);
        assertEquals(10d, TypeUtils.castToDouble(null, 10d), 0d);
    }

    @Test
    public void castToBigInteger() {
        assertEquals(BigInteger.valueOf(10), TypeUtils.castToBigInteger(Short.valueOf("10")));
        assertEquals(BigInteger.valueOf(10), TypeUtils.castToBigInteger(BigInteger.valueOf(10)));
        assertEquals(BigInteger.valueOf(10), TypeUtils.castToBigInteger("10"));
        assertEquals(BigInteger.valueOf(10), TypeUtils.castToBigInteger("10", BigInteger.valueOf(10)));
        assertEquals(BigInteger.valueOf(10), TypeUtils.castToBigInteger(null, BigInteger.valueOf(10)));
    }

    @Test
    public void castToBigDecimal() {
        assertEquals(BigDecimal.valueOf(10), TypeUtils.castToBigDecimal(Short.valueOf("10")));
        assertEquals(BigDecimal.valueOf(10), TypeUtils.castToBigDecimal(BigDecimal.valueOf(10)));
        assertEquals(BigDecimal.valueOf(10), TypeUtils.castToBigDecimal("10"));
        assertEquals(BigDecimal.valueOf(10), TypeUtils.castToBigDecimal("10", BigDecimal.valueOf(10)));
        assertEquals(BigDecimal.valueOf(10), TypeUtils.castToBigDecimal(null, BigDecimal.valueOf(10)));
    }
}
