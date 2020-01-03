/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.tommyettinger.merry;

import org.junit.Test;
import tests.support.Support_MapTest2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MerryOrderedMapTest extends junit.framework.TestCase {

	MerryOrderedMap hm;

	final static int hmSize = 1000;

	static Object[] objArray;

	static Object[] objArray2;
	{
		objArray = new Object[hmSize];
		objArray2 = new Object[hmSize];
		for (int i = 0; i < objArray.length; i++) {
			objArray[i] = new Integer(i);
			objArray2[i] = objArray[i].toString();
		}
	}

	 private static class MockMapNull extends MerryOrderedMap {
        @Override
        public Entries entries() {
            return null;
        }
		  {
		  	 size = 10;
		  }
 	}

	@Test
	public void test_Constructor() {
		// Test for method com.github.tommyettinger.merry.MerryOrderedMap()
		new Support_MapTest2(new MerryOrderedMap()).runTest();

		MerryOrderedMap hm2 = new MerryOrderedMap();
		assertEquals("Created incorrect MerryOrderedMap", 0, hm2.size);
	}

	@Test
	public void test_ConstructorI() {
		// Test for method com.github.tommyettinger.merry.MerryOrderedMap(int)
		MerryOrderedMap hm2 = new MerryOrderedMap(5);
		assertEquals("Created incorrect MerryOrderedMap", 0, hm2.size);
		try {
			new MerryOrderedMap(-1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail(
				"Failed to throw IllegalArgumentException for initial capacity < 0");

		MerryOrderedMap empty = new MerryOrderedMap(0);
		assertNull("Empty MerryOrderedMap access", empty.get("nothing"));
		empty.put("something", "here");
		assertTrue("cannot get element", empty.get("something") == "here");
	}

	@Test
	public void test_ConstructorIF() {
		// Test for method com.github.tommyettinger.merry.MerryOrderedMap(int, float)
		MerryOrderedMap hm2 = new MerryOrderedMap(5, (float) 0.5);
		assertEquals("Created incorrect MerryOrderedMap", 0, hm2.size);
		try {
			new MerryOrderedMap(0, 0);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail(
				"Failed to throw IllegalArgumentException for initial load factor <= 0");
		MerryOrderedMap empty = new MerryOrderedMap(0, 0.75f);
		assertNull("Empty hashtable access", empty.get("nothing"));
		empty.put("something", "here");
		assertTrue("cannot get element", empty.get("something") == "here");
	}

	@Test
	public void test_ConstructorLjava_util_Map() {
		// Test for method com.github.tommyettinger.merry.MerryOrderedMap(com.github.tommyettinger.merry.MerryOrderedMap)
		MerryOrderedMap myMap = new MerryOrderedMap();
		for (int counter = 0; counter < hmSize; counter++)
			myMap.put(objArray2[counter], objArray[counter]);
		MerryOrderedMap hm2 = new MerryOrderedMap(myMap);
		for (int counter = 0; counter < hmSize; counter++)
			assertTrue("Failed to construct correct MerryOrderedMap", hm
					.get(objArray2[counter]) == hm2.get(objArray2[counter]));
	}

	@Test
	public void test_getLjava_lang_Object() {
		// Test for method java.lang.Object
		// com.github.tommyettinger.merry.MerryOrderedMap.get(java.lang.Object)
		assertNull("Get returned non-null for non existent key",
				hm.get("T"));
		hm.put("T", "HELLO");
		assertEquals("Get returned incorecct value for existing key", "HELLO", hm.get("T")
				);

//		MerryOrderedMap m = new MerryOrderedMap();
//		m.put(null, "test");
//		assertEquals("Failed with null key", "test", m.get(null));
//		assertNull("Failed with missing key matching null hash", m
//				.get(new Integer(0)));
	}

	@Test
	public void test_putLjava_lang_ObjectLjava_lang_Object() {
		// Test for method java.lang.Object
		// com.github.tommyettinger.merry.MerryOrderedMap.put(java.lang.Object, java.lang.Object)
		hm.put("KEY", "VALUE");
		assertEquals("Failed to install key/value pair", 
				"VALUE", hm.get("KEY"));

//		MerryOrderedMap m = new MerryOrderedMap();
//		m.put(new Short((short) 0), "short");
//		m.put(null, "test");
//		m.put(new Integer(0), "int");
//		assertEquals("Failed adding to bucket containing null", "short", m.get(
//				new Short((short) 0)));
//		assertEquals("Failed adding to bucket containing null2", "int", m.get(
//				new Integer(0)));
	}

	@Test
	public void test_putAllLjava_util_Map() {
		// Test for method void com.github.tommyettinger.merry.MerryOrderedMap.putAll(java.util.Map)
		MerryOrderedMap hm2 = new MerryOrderedMap();
		hm2.putAll(hm);
		for (int i = 0; i < 1000; i++)
			assertTrue("Failed to put all elements", hm2.get(
					new Integer(i).toString()).equals((new Integer(i))));
	}

//    @Test
//    public void test_putAll_Ljava_util_Map_Null() {
//        MerryOrderedMap linkedHashMap = new MerryOrderedMap();
//        try {
//            linkedHashMap.putAll(new MockMapNull());
//            fail("Should throw NullPointerException");
//        } catch (NullPointerException e) {
//            // expected.
//        }
//
//        try {
//            linkedHashMap = new MerryOrderedMap(new MockMapNull());
//            fail("Should throw NullPointerException");
//        } catch (NullPointerException e) {
//            // expected.
//        }
//    } 

	@Test
	public void test_entrySet() {
		// Test for method java.util.Set com.github.tommyettinger.merry.MerryOrderedMap.entrySet()
		MerryObjectMap.Entries s = hm.entries();
		Iterator i = s.iterator();
		assertTrue("Returned set of incorrect size", hm.size == s.map.size);
		while (i.hasNext()) {
			MerryObjectMap.Entry m = (MerryObjectMap.Entry)i.next();
			assertTrue("Returned incorrect entry set", hm.containsKey(m.key)
					&& hm.containsValue(m.value, false));
		}
	}

	@Test
	public void test_keySet() {
		// Test for method java.util.Set com.github.tommyettinger.merry.MerryOrderedMap.keySet()
		MerryObjectMap.Keys s = hm.keys();
		assertTrue("Returned set of incorrect size()", s.map.size == hm.size);
//		for (int i = 0; i < objArray.length; i++)
//			assertTrue("Returned set does not contain all keys",
//				s.contains(objArray[i].toString()));

//		MerryOrderedMap m = new MerryOrderedMap();
//		m.put(null, "test");
//		assertTrue("Failed with null key", m.keySet().contains(null));
//		assertNull("Failed with null key", m.keySet().iterator().next());

		MerryOrderedMap map = new MerryOrderedMap(101);
		map.put(new Integer(1), "1");
		map.put(new Integer(102), "102");
		map.put(new Integer(203), "203");
		Iterator it = map.keys().iterator();
		Integer remove1 = (Integer) it.next();
		it.hasNext();
		it.remove();
		Integer remove2 = (Integer) it.next();
		it.remove();
		ArrayList list = new ArrayList(Arrays.asList(new Integer[] {
				new Integer(1), new Integer(102), new Integer(203) }));
		list.remove(remove1);
		list.remove(remove2);
		assertTrue("Wrong result", it.next().equals(list.get(0)));
		assertEquals("Wrong size", 1, map.size);
		assertTrue("Wrong contents", map.keys().iterator().next().equals(
				list.get(0)));

		 MerryOrderedMap map2 = new MerryOrderedMap(101);
		map2.put(new Integer(1), "1");
		map2.put(new Integer(4), "4");
		Iterator it2 = map2.keys().iterator();
		Integer remove3 = (Integer) it2.next();
		Integer next;
		if (remove3.intValue() == 1)
			next = new Integer(4);
		else
			next = new Integer(1);
		it2.hasNext();
		it2.remove();
		assertTrue("Wrong result 2", it2.next().equals(next));
		assertEquals("Wrong size 2", 1, map2.size);
		assertTrue("Wrong contents 2", map2.keys().iterator().next().equals(next));
	}

	@Test
	public void test_values() {
		// Test for method java.util.Collection com.github.tommyettinger.merry.MerryOrderedMap.values()
		MerryObjectMap.Values c = hm.values();
		assertTrue("Returned collection of incorrect size()", c.map.size == hm.size);
//		for (int i = 0; i < objArray.length; i++)
//			assertTrue("Returned collection does not contain all keys", c
//					.contains(objArray[i]));

		MerryOrderedMap myMerryOrderedMap = new MerryOrderedMap();
		for (int i = 0; i < 100; i++)
			myMerryOrderedMap.put(objArray2[i], objArray[i]);
		 MerryObjectMap.Values values = myMerryOrderedMap.values();
//		new Support_UnmodifiableCollectionTest(
//				"Test Returned Collection From MerryOrderedMap.values()", values)
//				.runTest();
		 values.hasNext();
		 Object removed = values.next();
		values.remove();
		assertTrue(
				"Removing from the values collection should remove from the original map",
				!myMerryOrderedMap.containsValue(removed, false));

	}

	@Test
	public void test_removeLjava_lang_Object() {
		// Test for method java.lang.Object
		// com.github.tommyettinger.merry.MerryOrderedMap.remove(java.lang.Object)
		int size = hm.size;
		Integer y = new Integer(9);
		Integer x = ((Integer) hm.remove(y.toString()));
		assertTrue("Remove returned incorrect value", x.equals(new Integer(9)));
		assertNull("Failed to remove given key", hm.get(new Integer(9)));
		assertTrue("Failed to decrement size", hm.size == (size - 1));
		assertNull("Remove of non-existent key returned non-null", hm
				.remove("LCLCLC"));

//		MerryOrderedMap m = new MerryOrderedMap();
//		m.put(null, "test");
//		assertNull("Failed with same hash as null",
//				m.remove(new Integer(0)));
//		assertEquals("Failed with null key", "test", m.remove(null));
	}

	@Test
	public void test_clear() {
		// Test for method void com.github.tommyettinger.merry.MerryOrderedMap.clear()
		hm.clear();
		assertEquals("Clear failed to reset size", 0, hm.size);
		for (int i = 0; i < hmSize; i++)
			assertNull("Failed to clear all elements",
					hm.get(objArray2[i]));

	}

	@Test
	public void test_containsKeyLjava_lang_Object() {
		// Test for method boolean
		// com.github.tommyettinger.merry.MerryOrderedMap.containsKey(java.lang.Object)
		assertTrue("Returned false for valid key", hm.containsKey(new Integer(
				876).toString()));
		assertTrue("Returned true for invalid key", !hm.containsKey("KKDKDKD"));

//		MerryOrderedMap m = new MerryOrderedMap();
//		m.put(null, "test");
//		assertTrue("Failed with null key", m.containsKey(null));
//		assertTrue("Failed with missing key matching null hash", !m
//				.containsKey(new Integer(0)));
	}

	@Test
	public void test_containsValueLjava_lang_Object() {
		// Test for method boolean
		// com.github.tommyettinger.merry.MerryOrderedMap.containsValue(java.lang.Object)
		assertTrue("Returned false for valid value", hm
				.containsValue(new Integer(875), false));
		assertTrue("Returned true for invalid value", !hm
				.containsValue(new Integer(-9), false));
	}

	@Test
	public void test_isEmpty() {
		// Test for method boolean com.github.tommyettinger.merry.MerryOrderedMap.isEmpty()
		assertTrue("Returned false for new map", new MerryOrderedMap().isEmpty());
		assertTrue("Returned true for non-empty", !hm.isEmpty());
	}

	@Test
	public void test_size() {
		// Test for method int com.github.tommyettinger.merry.MerryOrderedMap.size()
		assertTrue("Returned incorrect size",
				hm.size == (objArray.length + 1));
	}

	@Test
	public void test_ordered_entrySet() {
		int i;
		int sz = 100;
		MerryOrderedMap lhm = new MerryOrderedMap();
		for (i = 0; i < sz; i++) {
			Integer ii = new Integer(i);
			lhm.put(ii, ii.toString());
		}

		MerryObjectMap.Entries s1 = lhm.entries();
		Iterator it1 = s1.iterator();
		assertTrue("Returned set of incorrect size 1", lhm.size == s1.map.size);
		for (i = 0; it1.hasNext(); i++) {
			MerryObjectMap.Entry m = (MerryObjectMap.Entry) it1.next();
			Integer jj = (Integer) m.key;
			assertTrue("Returned incorrect entry set 1", jj.intValue() == i);
		} 
	}

	@Test
	public void test_ordered_keySet() {
		int i;
		int sz = 100;
		MerryOrderedMap lhm = new MerryOrderedMap();
		for (i = 0; i < sz; i++) {
			Integer ii = new Integer(i);
			lhm.put(ii, ii.toString());
		}

		MerryObjectMap.Keys s1 = lhm.keys();
		Iterator it1 = s1.iterator();
		assertTrue("Returned set of incorrect size", lhm.size == s1.map.size);
		for (i = 0; it1.hasNext(); i++) {
			Integer jj = (Integer) it1.next();
			assertTrue("Returned incorrect entry set", jj.intValue() == i);
		}
	}

	@Test
	public void test_ordered_values() {
		int i;
		int sz = 100;
		MerryOrderedMap lhm = new MerryOrderedMap();
		for (i = 0; i < sz; i++) {
			Integer ii = new Integer(i);
			lhm.put(ii, new Integer(i * 2));
		}

		MerryObjectMap.Values s1 = lhm.values();
		Iterator it1 = s1.iterator();
		assertTrue("Returned set of incorrect size 1", lhm.size == s1.map.size);
		for (i = 0; it1.hasNext(); i++) {
			Integer jj = (Integer) it1.next();
			assertTrue("Returned incorrect entry set 1", jj.intValue() == i * 2);
		}
	}
	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 */
	protected void setUp() {
		hm = new MerryOrderedMap();
		for (int i = 0; i < objArray.length; i++)
			hm.put(objArray2[i], objArray[i]);
		hm.put("test", null);
	}

	/**
	 * Tears down the fixture, for example, close a network connection. This
	 * method is called after a test is executed.
	 */
	protected void tearDown() {
	}
}
