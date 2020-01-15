package com.github.tommyettinger.merry;

import com.badlogic.gdx.utils.Json;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Tommy Ettinger on 1/13/2020.
 */
public class JsonTest {
	@Test
	public void testObjectSet()
	{
		Json json = new Json();
		ObjectSet<String> set = ObjectSet.with("Robin", "Hood", "and his band of", "Merry", "Men");
		String pretty = json.prettyPrint(set);
		System.out.println(pretty);
		ObjectSet from = json.fromJson(ObjectSet.class, pretty);
		Assert.assertEquals(from, set);
	}
	@Test
	public void testOrderedSet()
	{
		Json json = new Json();
		OrderedSet<String> set = OrderedSet.with("Robin", "Hood", "and his band of", "Merry", "Men");
		String pretty = json.prettyPrint(set);
		System.out.println(pretty);
		OrderedSet from = json.fromJson(OrderedSet.class, pretty);
		Assert.assertEquals(from, set);
	}
	@Test
	public void testObjectMapString()
	{
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
	@Test
	public void testObjectMapOther()
	{
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
	@Test
	public void testOrderedMapString()
	{
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
	@Test
	public void testOrderedMapOther()
	{
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
}