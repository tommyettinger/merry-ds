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

import com.badlogic.gdx.utils.Array;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tests.support.Support_MapTest2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ObjectMapTest {
	class MockMap extends ObjectMap {
		public Entries entries () {
			return null;
		}

		public int size () {
			return 0;
		}
	}

	private static class MockMapNull extends ObjectMap {
		public Entries entries () {
			return null;
		}

		public int size () {
			return 10;
		}
	}

	interface MockInterface {
		public String mockMethod ();
	}

	class MockClass implements MockInterface {
		public String mockMethod () {
			return "This is a MockClass";
		}
	}

	class MockHandler implements InvocationHandler {

		Object obj;

		public MockHandler (Object o) {
			obj = o;
		}

		public Object invoke (Object proxy, Method m, Object[] args) throws Throwable {

			Object result = null;

			try {

				result = m.invoke(obj, args);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
			return result;
		}

	}

	ObjectMap hm;

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

	@Test public void test_Constructor () {
		// Test for method com.github.tommyettinger.merry.ObjectMap()
		new Support_MapTest2(new ObjectMap<String, String>()).runTest();

		ObjectMap hm2 = new ObjectMap<>();
		Assert.assertEquals("Created incorrect ObjectMap", 0, hm2.size);
	}

	@Test public void test_ConstructorI () {
		// Test for method com.github.tommyettinger.merry.ObjectMap(int)
		ObjectMap hm2 = new ObjectMap(5);
		Assert.assertEquals("Created incorrect ObjectMap", 0, hm2.size);
		try {
			new ObjectMap(-1);
		} catch (IllegalArgumentException e) {
			return;
		}
		Assert.fail("Failed to throw IllegalArgumentException for initial capacity < 0");

		ObjectMap empty = new ObjectMap(0);
		Assert.assertNull("Empty hashmap access", empty.get("nothing"));
		empty.put("something", "here");
		Assert.assertTrue("cannot get element", empty.get("something") == "here");
	}

	@Test public void test_ConstructorIF () {
		// Test for method com.github.tommyettinger.merry.ObjectMap(int, float)
		ObjectMap hm2 = new ObjectMap(5, (float)0.5);
		Assert.assertEquals("Created incorrect ObjectMap", 0, hm2.size);
		try {
			new ObjectMap(0, 0);
		} catch (IllegalArgumentException e) {
			return;
		}
		Assert.fail("Failed to throw IllegalArgumentException for initial load factor <= 0");

		ObjectMap empty = new ObjectMap(0, 0.75f);
		Assert.assertNull("Empty hashtable access", empty.get("nothing"));
		empty.put("something", "here");
		Assert.assertTrue("cannot get element", empty.get("something") == "here");
	}

	@Test public void test_ConstructorLjava_util_Map () {
		ObjectMap myMap = new OrderedMap();
		for (int counter = 0; counter < hmSize; counter++)
			myMap.put(objArray2[counter], objArray[counter]);
		ObjectMap hm2 = new ObjectMap(myMap);
		for (int counter = 0; counter < hmSize; counter++)
			Assert.assertTrue("Failed to construct correct ObjectMap",
				hm.get(objArray2[counter]) == hm2.get(objArray2[counter]));

//        try {
//            ObjectMap mockMap = new MockMapNull();
//            hm = new ObjectMap(mockMap);
//            fail("Should throw NullPointerException");
//        } catch (NullPointerException e) {
//            //empty
//        }

		ObjectMap map = new ObjectMap();
		map.put("a", "a");
		SubMap map2 = new SubMap(map);
		Assert.assertTrue(map2.containsKey("a"));
		Assert.assertTrue(map2.containsValue("a", false));
	}

	@Test public void test_clear () {
		hm.clear();
		Assert.assertEquals("Clear failed to reset size", 0, hm.size);
		for (int i = 0; i < hmSize; i++)
			Assert.assertNull("Failed to clear all elements", hm.get(objArray2[i]));

		// Check clear on a large loaded map of Integer keys
		ObjectMap<Integer, String> map = new ObjectMap<Integer, String>();
		for (int i = -32767; i < 32768; i++) {
			map.put(i, "foobar");
		}
		map.clear();
		Assert.assertEquals("Failed to reset size on large integer map", 0, hm.size);
		for (int i = -32767; i < 32768; i++) {
			Assert.assertNull("Failed to clear integer map values", map.get(i));
		}
	}

	@Test public void test_containsKeyLjava_lang_Object () {
		// Test for method boolean
		// com.github.tommyettinger.merry.ObjectMap.containsKey(java.lang.Object)
		Assert.assertTrue("Returned false for valid key", hm.containsKey(new Integer(876).toString()));
		Assert.assertTrue("Returned true for invalid key", !hm.containsKey("KKDKDKD"));

//		ObjectMap m = new ObjectMap();
//		m.put(null, "test");
//		assertTrue("Failed with null key", m.containsKey(null));
//		assertTrue("Failed with missing key matching null hash", !m
//				.containsKey(new Integer(0)));
	}

	@Test public void test_containsValueLjava_lang_Object () {
		// Test for method boolean
		// com.github.tommyettinger.merry.ObjectMap.containsValue(java.lang.Object)
		Assert.assertTrue("Returned false for valid value", hm.containsValue(new Integer(875), false));
		Assert.assertTrue("Returned true for invalid valie", !hm.containsValue(new Integer(-9), false));
	}

	@Test public void test_entrySet () {
		// Test for method java.util.Set com.github.tommyettinger.merry.ObjectMap.entrySet(
		ObjectMap.Entries s = hm.entries();
		Iterator i = s.iterator();
		Assert.assertTrue("Returned set of incorrect size", hm.size == s.map.size);
		while (i.hasNext()) {
			ObjectMap.Entry m = (ObjectMap.Entry)i.next();
			Assert.assertTrue("Returned incorrect entry set", hm.containsKey(m.key) && hm.containsValue(m.value, false));
		}

		ObjectMap.Entries iter = s.iterator();
		iter.reset();
		s.map.remove(iter.next());
		Assert.assertEquals(1001, s.map.size);
	}

	@Test public void test_getLjava_lang_Object () {
		// Test for method java.lang.Object
		// com.github.tommyettinger.merry.ObjectMap.get(java.lang.Object)
		Assert.assertNull("Get returned non-null for non existent key", hm.get("T"));
		hm.put("T", "HELLO");
		Assert.assertEquals("Get returned incorrect value for existing key", "HELLO", hm.get("T"));

//		ObjectMap m = new ObjectMap();
//		m.put(null, "test");
//		assertEquals("Failed with null key", "test", m.get(null));
//		assertNull("Failed with missing key matching null hash", m
//				.get(new Integer(0)));

		// Regression for HARMONY-206
		ReusableKey k = new ReusableKey();
		ObjectMap map = new ObjectMap();
		k.setKey(1);
		map.put(k, "value1");

		k.setKey(18);
		Assert.assertNull(map.get(k));

		k.setKey(17);
		Assert.assertNull(map.get(k));
	}

//	/**
//	 * Tests for proxy object keys and values
//	 */
//	public void test_proxies() {
//        // Regression for HARMONY-6237
//        MockInterface proxyKey = (MockInterface) Proxy.newProxyInstance(
//                MockInterface.class.getClassLoader(),
//                new Class[] { MockInterface.class }, new MockHandler(
//                        new MockClass()));
//        MockInterface proxyValue = (MockInterface) Proxy.newProxyInstance(
//                MockInterface.class.getClassLoader(),
//                new Class[] { MockInterface.class }, new MockHandler(
//                        new MockClass()));
//
//        // Proxy key
//        Object val = new Object();
//        hm.put(proxyKey, val);
//
//        assertEquals("Failed with proxy object key", val, hm
//                .get(proxyKey));
//        assertTrue("Failed to find proxy key", hm.containsKey(proxyKey));
//        assertEquals("Failed to remove proxy object key", val,
//                hm.remove(proxyKey));
//        assertFalse("Should not have found proxy key", hm.containsKey(proxyKey));
//        
//        // Proxy value
//        Object k = new Object();
//        hm.put(k, proxyValue);
//        
//        assertTrue("Failed to find proxy object as value", hm.containsValue(proxyValue));
//        
//        // Proxy key and value
//        ObjectMap map = new ObjectMap();
//        map.put(proxyKey, proxyValue);
//        assertTrue("Failed to find proxy key", map.containsKey(proxyKey));
//        assertEquals(1, map.size());
//        Object[] entries = map.entrySet().toArray();
//        Map.Entry entry = (Map.Entry)entries[0];
//        assertTrue("Failed to find proxy association", map.entrySet().contains(entry));
//	}

	@Test public void test_isEmpty () {
		// Test for method boolean com.github.tommyettinger.merry.ObjectMap.isEmpty()
		Assert.assertTrue("Returned false for new map", new ObjectMap().isEmpty());
		Assert.assertTrue("Returned true for non-empty", !hm.isEmpty());
	}

	@Test public void test_keySet () {
		// Test for method java.util.Set com.github.tommyettinger.merry.ObjectMap.keySet()
		ObjectMap.Keys s = hm.keys();
		Assert.assertTrue("Returned set of incorrect size()", s.map.size == hm.size);
//		for (int i = 0; i < objArray.length; i++)
//			assertTrue("Returned set does not contain all keys", s
//					.contains(objArray[i].toString()));

//		ObjectMap m = new ObjectMap();
//		m.put(null, "test");
//		assertTrue("Failed with null key", m.keys().contains(null));
//		assertNull("Failed with null key", m.keys().iterator().next());

		ObjectMap map = new ObjectMap(101);
		map.put(new Integer(1), "1");
		map.put(new Integer(102), "102");
		map.put(new Integer(203), "203");
		Iterator it = map.keys().iterator();
		Integer remove1 = (Integer)it.next();
		it.hasNext();
		it.remove();
		Integer remove2 = (Integer)it.next();
		it.remove();
		ArrayList list = new ArrayList(Arrays.asList(new Integer(1), new Integer(102), new Integer(203)));
		list.remove(remove1);
		list.remove(remove2);
		Assert.assertTrue("Wrong result", it.next().equals(list.get(0)));
		Assert.assertEquals("Wrong size", 1, map.size);
		Assert.assertTrue("Wrong contents", map.keys().iterator().next().equals(list.get(0)));

		ObjectMap map2 = new ObjectMap(101);
		map2.put(new Integer(1), "1");
		map2.put(new Integer(4), "4");
		Iterator it2 = map2.keys().iterator();
		Integer remove3 = (Integer)it2.next();
		Integer next;
		if (remove3.intValue() == 1)
			next = new Integer(4);
		else
			next = new Integer(1);
		it2.hasNext();
		it2.remove();
		Assert.assertTrue("Wrong result 2", it2.next().equals(next));
		Assert.assertEquals("Wrong size 2", 1, map2.size);
		Assert.assertTrue("Wrong contents 2", map2.keys().iterator().next().equals(next));
	}

	@Test public void test_putLjava_lang_ObjectLjava_lang_Object () {
		hm.put("KEY", "VALUE");
		Assert.assertEquals("Failed to install key/value pair", "VALUE", hm.get("KEY"));

//        ObjectMap<Object,Object> m = new ObjectMap<Object,Object>();
//        m.put(new Short((short) 0), "short");
//        m.put(null, "test");
//        m.put(new Integer(0), "int");
//        assertEquals("Failed adding to bucket containing null", "short", m
//                .get(new Short((short) 0)));
//        assertEquals("Failed adding to bucket containing null2", "int", m
//                .get(new Integer(0)));

		// Check my actual key instance is returned
		ObjectMap<Integer, String> map = new ObjectMap<Integer, String>();
		for (int i = -32767; i < 32768; i++) {
			map.put(i, "foobar");
		}
		Integer myKey = new Integer(0);
		// Put a new value at the old key position
		map.put(myKey, "myValue");
		Assert.assertTrue(map.containsKey(myKey));
		Assert.assertEquals("myValue", map.get(myKey));
		boolean found = false;
		for (Iterator<Integer> itr = map.keys().iterator(); itr.hasNext(); ) {
			Integer key = itr.next();
			if (found = myKey == (key)) {
				break;
			}
		}
		Assert.assertFalse("Should not find new key instance in hashmap", found);

		// Add a new key instance and check it is returned
		Assert.assertNotNull(map.remove(myKey));
		map.put(myKey, "myValue");
		Assert.assertTrue(map.containsKey(myKey));
		Assert.assertEquals("myValue", map.get(myKey));
		for (Iterator<Integer> itr = map.keys().iterator(); itr.hasNext(); ) {
			Integer key = itr.next();
			if (found = myKey == (key)) {
				break;
			}
		}
		Assert.assertTrue("Did not find new key instance in hashmap", found);

		// Ensure keys with identical hashcode are stored separately
		ObjectMap<Object, Object> objmap = new ObjectMap<Object, Object>();
		for (int i = 0; i < 32768; i++) {
			objmap.put(i, "foobar");
		}
		// Put non-equal object with same hashcode
		MyKey aKey = new MyKey();
		Assert.assertNull(objmap.put(aKey, "value"));
		Assert.assertNull(objmap.remove(new MyKey()));
		Assert.assertEquals("foobar", objmap.get(0));
		Assert.assertEquals("value", objmap.get(aKey));
	}

	static class MyKey {
		public MyKey () {
			super();
		}

		public int hashCode () {
			return 0;
		}
	}

	@Test public void test_putAllLjava_util_Map () {
		// Test for method void com.github.tommyettinger.merry.ObjectMap.putAll(java.util.Map)
		ObjectMap hm2 = new ObjectMap();
		hm2.putAll(hm);
		for (int i = 0; i < 1000; i++)
			Assert.assertTrue("Failed to clear all elements", hm2.get(new Integer(i).toString()).equals((new Integer(i))));

//        ObjectMap mockMap = new MockMap();
//        hm2 = new ObjectMap();
//        hm2.putAll(mockMap);
//        assertEquals("Size should be 0", 0, hm2.size);
	}

//    @Test
//    public void test_putAllLjava_util_Map_Null() {
//        ObjectMap hashMap = new ObjectMap();
//        try {
//            hashMap.putAll(new MockMapNull());
//            Assert.fail("Should throw NullPointerException");
//        } catch (NullPointerException e) {
//            // expected.
//        }
//
////        try {
////            hashMap = new ObjectMap(new MockMapNull());
////            fail("Should throw NullPointerException");
////        } catch (NullPointerException e) {
////            // expected.
////        }
//    } 

	@Test public void test_removeLjava_lang_Object () {
		int size = hm.size;
		Integer y = new Integer(9);
		Integer x = ((Integer)hm.remove(y.toString()));
		Assert.assertTrue("Remove returned incorrect value", x.equals(new Integer(9)));
		Assert.assertNull("Failed to remove given key", hm.get(new Integer(9)));
		Assert.assertTrue("Failed to decrement size", hm.size == (size - 1));
		Assert.assertNull("Remove of non-existent key returned non-null", hm.remove("LCLCLC"));

//		ObjectMap m = new ObjectMap();
//		m.put(null, "test");
//		assertNull("Failed with same hash as null",
//				m.remove(new Integer(0)));
//		assertEquals("Failed with null key", "test", m.remove(null));

		ObjectMap<Integer, Object> map = new ObjectMap<Integer, Object>();
		for (int i = 0; i < 32768; i++) {
			map.put(i, "const");
		}
		Object[] values = new Object[32768];
		for (int i = 0; i < 32768; i++) {
			values[i] = Integer.toString(i);
			map.put(i, values[i]);
		}
//        Integer problem = map.findKey("15056", false);
//		 System.out.println(problem);
//		 int loc = map.locateKey(problem,  map.place(problem));
//		 System.out.println("Initial problem value: " + ((Object[])map.keyTable)[loc] + ", loc = " + loc);
		for (int i = 32767; i >= 0; i--) {
			Object obj = map.remove(i);
//        	 loc = map.locateKey(problem,  map.place(problem));
//        	 if(loc < 0)
//				  System.out.println("Problem value not found when i == " + i);
//        	 else if(((Object[])map.keyTable)[loc] == null)
//				  System.out.println("Problem value changed when i == " + i);
			if (!values[i].equals(obj))
				System.out.println("i is " + i);
			Assert.assertEquals("Failed to remove same value", values[i], obj);
		}

		// Ensure keys with identical hashcode are removed properly
		map = new ObjectMap<Integer, Object>();
		for (int i = -32767; i < 32768; i++) {
			map.put(i, "foobar");
		}
		// Remove non equal object with same hashcode
//        assertNull(map.remove(new MyKey()));
		Assert.assertEquals("foobar", map.get(0));
		map.remove(0);
		Assert.assertNull(map.get(0));
	}

	/**
	 * Compatibility test to ensure we rehash the same way as the RI.
	 * Not required by the spec, but some apps seem sensitive to it.
	 */
	@Test public void test_rehash () {
		// This map should rehash on adding the ninth element.
		ObjectMap<MyKey, Integer> hm = new ObjectMap<MyKey, Integer>(10, 0.5f);

		// Ordered set of keys.
		MyKey[] keyOrder = new MyKey[9];
		for (int i = 0; i < keyOrder.length; i++) {
			keyOrder[i] = new MyKey();
		}

		// Store eight elements
		for (int i = 0; i < 8; i++) {
			hm.put(keyOrder[i], i);
		}
		// Check expected ordering (inverse of adding order)
		Array<MyKey> returnedKeys = hm.keys().toArray();
		for (int i = 0; i < 8; i++) {
			Assert.assertSame(keyOrder[i], returnedKeys.get(i));
		}

		// The next put causes a rehash
		hm.put(keyOrder[8], 8);
		// Check expected new ordering (adding order)
		returnedKeys = hm.keys().toArray();
		for (int i = 0; i < 9; i++) {
			Assert.assertSame(keyOrder[i], returnedKeys.get(i));
		}
	}

	@Test public void test_size () {
		// Test for method int com.github.tommyettinger.merry.ObjectMap.size()
		Assert.assertTrue("Returned incorrect size", hm.size == (objArray.length + 1));
	}

	@Test public void test_values () {
		// Test for method java.util.Collection com.github.tommyettinger.merry.ObjectMap.values()
		ObjectMap.Values c = hm.values();
		Assert.assertTrue("Returned collection of incorrect size()", c.map.size == hm.size);
//		for (int i = 0; i < objArray.length; i++)
//			assertTrue("Returned collection does not contain all keys", c
//					.contains(objArray[i]));

		ObjectMap myObjectMap = new ObjectMap();
		for (int i = 0; i < 100; i++)
			myObjectMap.put(objArray2[i], objArray[i]);
		ObjectMap.Values values = myObjectMap.values();
//		new Support_UnmodifiableCollectionTest(
//				"Test Returned Collection From ObjectMap.values()", values)
//				.runTest();
		values.hasNext();
		Object removed = values.next();
		values.remove();
		Assert.assertTrue("Removing from the values collection should remove from the original map",
			!myObjectMap.containsValue(removed, false));

	}

	@Test public void test_toString () {

		ObjectMap m = new ObjectMap();
		m.put(m, m);
		String result = m.toString();
		Assert.assertTrue("should contain self ref", result.indexOf("(this") > -1);
	}

	static class ReusableKey {
		private int key = 0;

		public void setKey (int key) {
			this.key = key;
		}

		public int hashCode () {
			return key;
		}

		public boolean equals (Object o) {
			if (o == this) {
				return true;
			}
			if (!(o instanceof ReusableKey)) {
				return false;
			}
			return key == ((ReusableKey)o).key;
		}
	}
//    
//	public void test_Map_Entry_hashCode() {
//        //Related to HARMONY-403
//	    ObjectMap<Integer, Integer> map = new ObjectMap<Integer, Integer>(10);
//	    Integer key = new Integer(1);
//	    Integer val = new Integer(2);
//	    map.put(key, val);
//	    int expected = key.hashCode() ^ val.hashCode();
//	    assertEquals(expected, map.hashCode());
//	    key = new Integer(4);
//	    val = new Integer(8);
//	    map.put(key, val);
//	    expected += key.hashCode() ^ val.hashCode();
//	    assertEquals(expected, map.hashCode());
//	}

	/*
	 * Regression test for HY-4750
	 */
	@Test public void test_EntrySet () {
//        ObjectMap map = new ObjectMap();
//        map.put(new Integer(1), "ONE");

//        ObjectMap.Entries entrySet = map.entries();
//        Iterator e = entrySet.iterator();
//        Object real = e.next();
//        ObjectMap.Entry copyEntry = new MockEntry();
//        assertEquals(real, copyEntry);
		//assertTrue(entrySet.contains(copyEntry));

		//entrySet.remove(copyEntry);
		//assertFalse(entrySet.contains(copyEntry));
	}

	private static class MockEntry extends ObjectMap.Entry {

		public Object getKey () {
			return new Integer(1);
		}

		public Object getValue () {
			return "ONE";
		}

		public Object setValue (Object object) {
			return null;
		}
	}

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 */
	@Before public void setUp () {
		hm = new ObjectMap();
		for (int i = 0; i < objArray.length; i++)
			hm.put(objArray2[i], objArray[i]);
		hm.put("test", null);
//		hm.put(null, "test");
	}

	class SubMap<K, V> extends ObjectMap<K, V> {
		public SubMap (ObjectMap<? extends K, ? extends V> m) {
			super(m);
		}

		public V put (K key, V value) {
			throw new UnsupportedOperationException();
		}
	}
}
