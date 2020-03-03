package ds.merry;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class LongMapTest {
	@Test
	public void testMatchingPut (){
		LongMap<Integer> merryMap = new LongMap<>();
		HashMap<Long, Integer> jdkMap = new HashMap<>();
		long stateA = 0L, stateB = 1L;
		int merryRepeats = 0, jdkRepeats = 0;
		long item;
		for (int i = 0; i < 0x100000; i++) { // a million should do
			stateA += 0xC6BC279692B5C323L;
			item = (stateA ^ stateA >>> 31) * (stateB += 0x9E3779B97F4A7C16L);
			item &= item >>> 24; // causes 64-bit state to get crammed into 40 bits, with item biased toward low bit counts 
			if(merryMap.put(item, i) != null) merryRepeats++;
			if(jdkMap.put(item, i) != null) jdkRepeats++;
			Assert.assertEquals(merryMap.size, jdkMap.size());
		}
		Assert.assertEquals(merryRepeats, jdkRepeats);
	}
	@Test
	public void testMatchingMix(){
		LongMap<Integer> merryMap = new LongMap<>();
		HashMap<Long, Integer> jdkMap = new HashMap<>();
		long stateA = 0L, stateB = 1L;
		int merryRemovals = 0, jdkRemovals = 0;
		long item;
		for (int i = 0; i < 0x100000; i++) { // 1 million should do
			stateA += 0xC6BC279692B5C323L;
			item = (stateA ^ stateA >>> 31) * (stateB += 0x9E3779B97F4A7C16L);
			item &= item >>> 24; // causes 64-bit state to get crammed into 40 bits, with item biased toward low bit counts 
			if(merryMap.remove(item) == null) merryMap.put(item, i); 
			else merryRemovals++;
			if(jdkMap.remove(item) == null) jdkMap.put(item, i); 
			else jdkRemovals++;
			if(merryRemovals != jdkRemovals)
				System.out.println(i);
			Assert.assertEquals(merryMap.size, jdkMap.size());
		}
		Assert.assertEquals(merryRemovals, jdkRemovals);
		if(merryMap.size > jdkMap.size())
		{
			System.out.println("MerryMap is bigger: " + merryMap.size + " Merry, vs. " + jdkMap.size() + " JDK");
			for(Long k : jdkMap.keySet())
				merryMap.remove(k);
			System.out.println("M: " + merryMap.size);
			System.out.println("J: " + jdkMap.size());

		}
		else if(jdkMap.size() > merryMap.size)
		{
			System.out.println("JDKMap is bigger: " + (jdkMap.size() - merryMap.size));
			LongMap.Keys ks = merryMap.keys();
			while (ks.hasNext)
				jdkMap.remove(ks.next());
			System.out.println("M: " + merryMap.size);
			System.out.println("J: " + jdkMap.size());
		}
	}
}
