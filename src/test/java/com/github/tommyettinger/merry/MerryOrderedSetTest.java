/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tommyettinger.merry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

public class MerryOrderedSetTest {

	MerryOrderedSet hs;

	static Object[] objArray;

	static {
		objArray = new Object[1000];
		for (int i = 0; i < objArray.length; i++)
			objArray[i] = new Integer(i);
	}

	@Test public void test_Constructor () {
		// Test for method com.github.tommyettinger.merry.MerryOrderedSet()
		MerryOrderedSet hs2 = new MerryOrderedSet();
		Assert.assertEquals("Created incorrect MerryOrderedSet", 0, hs2.size);
	}

	@Test public void test_ConstructorI () {
		// Test for method com.github.tommyettinger.merry.MerryOrderedSet(int)
		MerryOrderedSet hs2 = new MerryOrderedSet(5);
		Assert.assertEquals("Created incorrect MerryOrderedSet", 0, hs2.size);
		try {
			new MerryOrderedSet(-1);
		} catch (IllegalArgumentException e) {
			return;
		}
		Assert.fail("Failed to throw IllegalArgumentException for capacity < 0");
	}

	@Test public void test_ConstructorIF () {
		// Test for method com.github.tommyettinger.merry.MerryOrderedSet(int, float)
		MerryOrderedSet hs2 = new MerryOrderedSet(5, (float)0.5);
		Assert.assertEquals("Created incorrect MerryOrderedSet", 0, hs2.size);
		try {
			new MerryOrderedSet(0, 0);
		} catch (IllegalArgumentException e) {
			return;
		}
		Assert.fail("Failed to throw IllegalArgumentException for initial load factor <= 0");
	}

	@Test public void test_ConstructorLjava_util_Collection () {
		// Test for method com.github.tommyettinger.merry.MerryOrderedSet(java.util.Collection)
		MerryOrderedSet hs2 = MerryOrderedSet.with(objArray);
		for (int counter = 0; counter < objArray.length; counter++)
			Assert.assertTrue("MerryOrderedSet does not contain correct elements", hs.contains(objArray[counter]));
		Assert.assertTrue("MerryOrderedSet created from collection incorrect size", hs2.size == objArray.length);
	}

	@Test public void test_addLjava_lang_Object () {
		// Test for method boolean com.github.tommyettinger.merry.MerryOrderedSet.add(java.lang.Object)
		int size = hs.size;
		hs.add(new Integer(8));
		Assert.assertTrue("Added element already contained by set", hs.size == size);
		hs.add(new Integer(-9));
		Assert.assertTrue("Failed to increment set size after add", hs.size == size + 1);
		Assert.assertTrue("Failed to add element to set", hs.contains(new Integer(-9)));
	}

	@Test public void test_clear () {
		// Test for method void com.github.tommyettinger.merry.MerryOrderedSet.clear()
		MerryOrderedSet orgSet = new MerryOrderedSet(hs);
		hs.clear();
		Iterator i = orgSet.iterator();
		Assert.assertEquals("Returned non-zero size after clear", 0, hs.size);
		while (i.hasNext())
			Assert.assertTrue("Failed to clear set", !hs.contains(i.next()));
	}

	@Test public void test_containsLjava_lang_Object () {
		// Test for method boolean
		// com.github.tommyettinger.merry.MerryOrderedSet.contains(java.lang.Object)
		Assert.assertTrue("Returned false for valid object", hs.contains(objArray[90]));
		Assert.assertTrue("Returned true for invalid Object", !hs.contains(new Object()));
	}

	@Test public void test_isEmpty () {
		// Test for method boolean com.github.tommyettinger.merry.MerryOrderedSet.isEmpty()
		Assert.assertTrue("Empty set returned false", new MerryOrderedSet().isEmpty());
		Assert.assertTrue("Non-empty set returned true", !hs.isEmpty());
	}

	@Test public void test_iterator () {
		// Test for method java.util.Iterator com.github.tommyettinger.merry.MerryOrderedSet.iterator()
		Iterator i = hs.iterator();
		int x = 0;
		int j;
		for (j = 0; i.hasNext(); j++) {
			Object oo = i.next();
			if (oo != null) {
				Integer ii = (Integer)oo;
				Assert.assertTrue("Incorrect element found", ii.intValue() == j);
			}
			++x;
		}
		Assert.assertTrue("Returned iteration of incorrect size", hs.size == x);
	}

	@Test public void test_removeLjava_lang_Object () {
		// Test for method boolean
		// com.github.tommyettinger.merry.MerryOrderedSet.remove(java.lang.Object)
		int size = hs.size;
		hs.remove(new Integer(98));
		Assert.assertTrue("Failed to remove element", !hs.contains(new Integer(98)));
		Assert.assertTrue("Failed to decrement set size", hs.size == size - 1);
	}

	@Test public void test_size () {
		// Test for method int com.github.tommyettinger.merry.MerryOrderedSet.size
		Assert.assertTrue("Returned incorrect size", hs.size == (objArray.length));
		hs.clear();
		Assert.assertEquals("Cleared set returned non-zero size", 0, hs.size);
	}

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 */
	@Before public void setUp () {
		hs = new MerryOrderedSet();
		for (int i = 0; i < objArray.length; i++)
			hs.add(objArray[i]);
	}

	/**
	 * Tears down the fixture, for example, close a network connection. This
	 * method is called after a test is executed.
	 */
	@After public void tearDown () {
	}
}
