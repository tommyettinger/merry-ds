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

import java.util.Iterator;

public class MerryObjectSetTest extends junit.framework.TestCase {

	MerryObjectSet hs;

	static Object[] objArray;
	{
		objArray = new Object[1000];
		for (int i = 0; i < objArray.length; i++)
			objArray[i] = new Integer(i);
	}

	@Test
	public void test_Constructor() {
		// Test for method com.github.tommyettinger.merry.MerryObjectSet()
		MerryObjectSet hs2 = new MerryObjectSet();
		assertEquals("Created incorrect MerryObjectSet", 0, hs2.size);
	}

	@Test
	public void test_ConstructorI() {
		// Test for method com.github.tommyettinger.merry.MerryObjectSet(int)
		MerryObjectSet hs2 = new MerryObjectSet(5);
		assertEquals("Created incorrect MerryObjectSet", 0, hs2.size);
		try {
			new MerryObjectSet(-1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail(
				"Failed to throw IllegalArgumentException for capacity < 0");
	}

	@Test
	public void test_ConstructorIF() {
		// Test for method com.github.tommyettinger.merry.MerryObjectSet(int, float)
		MerryObjectSet hs2 = new MerryObjectSet(5, (float) 0.5);
		assertEquals("Created incorrect MerryObjectSet", 0, hs2.size);
		try {
			new MerryObjectSet(0, 0);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail(
				"Failed to throw IllegalArgumentException for initial load factor <= 0");
	}

	@Test
	public void test_ConstructorLjava_util_Collection() {
		// Test for method com.github.tommyettinger.merry.MerryObjectSet(java.util.Collection)
		MerryObjectSet hs2 = MerryObjectSet.with(objArray);
		for (int counter = 0; counter < objArray.length; counter++)
			assertTrue("MerryObjectSet does not contain correct elements", hs
					.contains(objArray[counter]));
		assertTrue("MerryObjectSet created from collection incorrect size",
				hs2.size == objArray.length);
	}

	@Test
	public void test_addLjava_lang_Object() {
		// Test for method boolean com.github.tommyettinger.merry.MerryObjectSet.add(java.lang.Object)
		int size = hs.size;
		hs.add(new Integer(8));
		assertTrue("Added element already contained by set", hs.size == size);
		hs.add(new Integer(-9));
		assertTrue("Failed to increment set size after add",
				hs.size == size + 1);
		assertTrue("Failed to add element to set", hs.contains(new Integer(-9)));
	}

	@Test
	public void test_clear() {
		// Test for method void com.github.tommyettinger.merry.MerryObjectSet.clear()
		MerryObjectSet orgSet = new MerryObjectSet(hs);
		hs.clear();
		Iterator i = orgSet.iterator();
		assertEquals("Returned non-zero size after clear", 0, hs.size);
		while (i.hasNext())
			assertTrue("Failed to clear set", !hs.contains(i.next()));
	}

	@Test
	public void test_containsLjava_lang_Object() {
		// Test for method boolean com.github.tommyettinger.merry.MerryObjectSet.contains(java.lang.Object)
		assertTrue("Returned false for valid object", hs.contains(objArray[90]));
		assertTrue("Returned true for invalid Object", !hs
				.contains(new Object()));

//		MerryObjectSet s = new MerryObjectSet();
//		s.add(null);
//		assertTrue("Cannot handle null", s.contains(null));
	}

	@Test
	public void test_isEmpty() {
		// Test for method boolean com.github.tommyettinger.merry.MerryObjectSet.isEmpty()
		assertTrue("Empty set returned false", new MerryObjectSet().isEmpty());
		assertTrue("Non-empty set returned true", !hs.isEmpty());
	}

	@Test
	public void test_iterator() {
		// Test for method java.util.Iterator com.github.tommyettinger.merry.MerryObjectSet.iterator()
		Iterator i = hs.iterator();
		int x = 0;
		while (i.hasNext()) {
			assertTrue("Failed to iterate over all elements", hs.contains(i
					.next()));
			++x;
		}
		assertTrue("Returned iteration of incorrect size", hs.size == x);

//		MerryObjectSet s = new MerryObjectSet();
//		s.add(null);
//		assertNull("Cannot handle null", s.iterator().next());
	}

	@Test
	public void test_removeLjava_lang_Object() {
		// Test for method boolean com.github.tommyettinger.merry.MerryObjectSet.remove(java.lang.Object)
		int size = hs.size;
		hs.remove(new Integer(98));
		assertTrue("Failed to remove element", !hs.contains(new Integer(98)));
		assertTrue("Failed to decrement set size", hs.size == size - 1);

//		MerryObjectSet s = new MerryObjectSet();
//		s.add(null);
//		assertTrue("Cannot handle null", s.remove(null));
	}

	@Test
	public void test_size() {
		// Test for method int com.github.tommyettinger.merry.MerryObjectSet.size
		assertTrue("Returned incorrect size", hs.size == (objArray.length));
		hs.clear();
		assertEquals("Cleared set returned non-zero size", 0, hs.size);
	}
	
   @Test
    public void test_toString() {
        MerryObjectSet s = new MerryObjectSet();
        s.add(s);
        String result = s.toString();
        assertTrue("should contain self ref", result.indexOf("(this") > -1);
    }

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 */
	protected void setUp() {
		hs = new MerryObjectSet();
		for (int i = 0; i < objArray.length; i++)
			hs.add(objArray[i]);
//		hs.add(null);
	}

	/**
	 * Tears down the fixture, for example, close a network connection. This
	 * method is called after a test is executed.
	 */
	protected void tearDown() {
	}
}
