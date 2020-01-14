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
}
