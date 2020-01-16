package com.github.tommyettinger.merry;

import com.badlogic.gdx.utils.Json;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Tommy Ettinger on 1/13/2020.
 */
public class JsonTest {
	@Test public void testObjectSet () {
		Json json = new Json();
		ObjectSet<String> set = ObjectSet.with("Robin", "Hood", "and his band of", "Merry", "Men");
		String pretty = json.prettyPrint(set);
		System.out.println(pretty);
		ObjectSet from = json.fromJson(ObjectSet.class, pretty);
		Assert.assertEquals(from, set);
	}

	@Test public void testOrderedSet () {
		Json json = new Json();
		OrderedSet<String> set = OrderedSet.with("Robin", "Hood", "and his band of", "Merry", "Men");
		String pretty = json.prettyPrint(set);
		System.out.println(pretty);
		OrderedSet from = json.fromJson(OrderedSet.class, pretty);
		Assert.assertEquals(from, set);
	}

	@Test public void testObjectMapString () {
		Json json = new Json();
		ObjectMap<String, Integer> map = new ObjectMap<>();
		map.put("Robin", 0);
		map.put("Hood", 1);
		map.put("and his band of", 2);
		map.put("Merry", 3);
		map.put("Men", 4);
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		ObjectMap from = json.fromJson(ObjectMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testObjectMapOther () {
		Json json = new Json();
		ObjectMap<Integer, String> map = new ObjectMap<>();
		map.put(0, "Robin");
		map.put(1, "Hood");
		map.put(2, "and his band of");
		map.put(3, "Merry");
		map.put(4, "Men");
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		ObjectMap from = json.fromJson(ObjectMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testOrderedMapString () {
		Json json = new Json();
		OrderedMap<String, Integer> map = new OrderedMap<>();
		map.put("Robin", 0);
		map.put("Hood", 1);
		map.put("and his band of", 2);
		map.put("Merry", 3);
		map.put("Men", 4);
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		OrderedMap from = json.fromJson(OrderedMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testOrderedMapOther () {
		Json json = new Json();
		OrderedMap<Integer, String> map = new OrderedMap<>();
		map.put(0, "Robin");
		map.put(1, "Hood");
		map.put(2, "and his band of");
		map.put(3, "Merry");
		map.put(4, "Men");
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		OrderedMap from = json.fromJson(OrderedMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testIntSet () {
		Json json = new Json();
		IntSet set = IntSet.with(42, 0, 23, 1337, 9001, -111, -2147483648);
		String pretty = json.prettyPrint(set);
		System.out.println(pretty);
		IntSet from = json.fromJson(IntSet.class, pretty);
		Assert.assertEquals(from, set);
	}

	@Test public void testIntMap () {
		Json json = new Json();
		IntMap<String> map = new IntMap<>();
		map.put(0, "Robin");
		map.put(1, "Hood");
		map.put(2, "and his band of");
		map.put(3, "Merry");
		map.put(4, "Men");
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		IntMap from = json.fromJson(IntMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testIntIntMap () {
		Json json = new Json();
		IntIntMap map = new IntIntMap();
		map.put(0, 42);
		map.put(1, 0);
		map.put(2, 23);
		map.put(3, 1337);
		map.put(4, -2147483648);
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		IntIntMap from = json.fromJson(IntIntMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testIntFloatMap () {
		Json json = new Json();
		IntFloatMap map = new IntFloatMap();
		map.put(0, 42.42f);
		map.put(1, 0.0f);
		map.put(2, 23.23f);
		map.put(3, 1337.1337f);
		map.put(4, Float.NEGATIVE_INFINITY);
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		IntFloatMap from = json.fromJson(IntFloatMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testLongMap () {
		Json json = new Json();
		LongMap<String> map = new LongMap<>();
		map.put(0, "Robin");
		map.put(1, "Hood");
		map.put(-23, "and his band of");
		map.put(0x1337BEEFC0DEDEAL, "Merry");
		map.put(Long.MIN_VALUE, "Men");
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		LongMap from = json.fromJson(LongMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	/**
	 * This will definitely fail because the identity equality check at the end won't have the same
	 * identities for keys.
	 */
	@Test @Ignore public void testIdentityMap () {
		Json json = new Json();
		IdentityMap<Integer, String> map = new IdentityMap<>();
		map.put(new Integer(256), "Robin");
		map.put(new Integer(256), "Hood");
		map.put(new Integer(256), "and his band of");
		map.put(new Integer(256), "Merry");
		map.put(new Integer(256), "Men");
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		IdentityMap from = json.fromJson(IdentityMap.class, pretty);
		Assert.assertEquals(from, map);
	}

	@Test public void testObjectIntMap () {
		Json json = new Json();
		ObjectIntMap<String> map = new ObjectIntMap<>();
		map.put("Robin", 0);
		map.put("Hood", 1);
		map.put("and his band of", 2);
		map.put("Merry", 3);
		map.put("Men", 4);
		String pretty = json.prettyPrint(map);
		System.out.println(pretty);
		ObjectIntMap from = json.fromJson(ObjectIntMap.class, pretty);
		Assert.assertEquals(from, map);
	}
}