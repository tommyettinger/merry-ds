package ds.merry;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Tommy Ettinger on 1/22/2020.
 */
public class ExtremeCaseTest {
	public static class Malice {
		public int e;
		
		public Malice(){
			e = 42;
		}
		public Malice (int e) {
			this.e = e;
		}

		public boolean equals (Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			return e == ((Malice)o).e;
		}

		public int hashCode () {
			return 0;
		}
	}
	@Test public void testObjectSet()
	{
		ObjectSet<Malice> malices = new ObjectSet<>(4050);
		for (int i = -1000; i < 1000; i++) {
			malices.add(new Malice(i));
		}
		System.out.println("Inserted " + malices.size + " items of Malice");
		Assert.assertTrue("Successfully removed item -1000", malices.remove(new Malice(-1000)));
		Assert.assertTrue("Successfully removed item -100", malices.remove(new Malice(-100)));
		Assert.assertTrue("Successfully removed item -10", malices.remove(new Malice(-10)));
		Assert.assertTrue("Successfully removed item -1", malices.remove(new Malice(-1)));
		Assert.assertTrue("Successfully removed item 1", malices.remove(new Malice(1)));
		Assert.assertTrue("Successfully removed item 10", malices.remove(new Malice(10)));
		Assert.assertTrue("Successfully removed item 100", malices.remove(new Malice(100)));
		Assert.assertTrue("Successfully removed item 999", malices.remove(new Malice(999)));
		final int intendedIterations = 2000 - 8;
		Assert.assertEquals("Size is incorrect", intendedIterations, malices.size);
		ObjectSet.ObjectSetIterator<Malice> mi = malices.iterator();
		int i = 0;
		while (mi.hasNext)
		{
			Assert.assertNotNull("Item " + i + " was null during iteration", mi.next());
			mi.remove();
			if(++i >= 1000)
				break;
		}
		Assert.assertTrue("Later item was missing", malices.contains(new Malice(500)));
		Assert.assertEquals("Iteration length was incorrect", i, 1000);
		System.out.println("Finished with size " + malices.size);
	}
	@Test public void testObjectMap()
	{
		IntIntMap iim = new IntIntMap();
		iim.entries().remove();
		ObjectMap<Malice, Integer> malices = new ObjectMap<>(4050);
		for (int i = -1000; i < 1000; i++) {
			malices.put(new Malice(i), i);
		}
		System.out.println("Inserted " + malices.size + " items of Malice");
		Assert.assertNotNull("Successfully removed item -1000", malices.remove(new Malice(-1000)));
		Assert.assertNotNull("Successfully removed item -100", malices.remove(new Malice(-100)));
		Assert.assertNotNull("Successfully removed item -10", malices.remove(new Malice(-10)));
		Assert.assertNotNull("Successfully removed item -1", malices.remove(new Malice(-1)));
		Assert.assertNotNull("Successfully removed item 1", malices.remove(new Malice(1)));
		Assert.assertNotNull("Successfully removed item 10", malices.remove(new Malice(10)));
		Assert.assertNotNull("Successfully removed item 100", malices.remove(new Malice(100)));
		Assert.assertNotNull("Successfully removed item 999", malices.remove(new Malice(999)));
		final int intendedIterations = 2000 - 8;
		Assert.assertEquals("Size is incorrect", intendedIterations, malices.size);
		ObjectMap.Entries<Malice, Integer> mi = malices.iterator();
		ObjectMap.Entry<Malice, Integer> ent;
		int i = 0;
		while (mi.hasNext)
		{
			Assert.assertNotNull("Item " + i + " was null during iteration", ent = mi.next());
			Assert.assertNotNull("Key " + i + " was null during iteration", ent.key);
			Assert.assertNotNull("Value " + i + " was null during iteration", ent.value);
			Assert.assertEquals("Key and Value were mixed up", ent.key.e, (int)ent.value);
			mi.remove();
			if(++i >= 1000)
				break;
		}
		Assert.assertTrue("Later item was missing", malices.containsKey(new Malice(500)));
		Assert.assertEquals("Iteration length was incorrect", i, 1000);
		System.out.println("Finished with size " + malices.size);
	}
}
