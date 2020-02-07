/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package ds.merry;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;

import java.util.NoSuchElementException;

/**
 * A {@link ObjectIntMap} that also stores keys in an {@link Array} using the insertion order; like ObjectIntMap, keys
 * are objects and values are primitive ints, which can save time and memory. Iteration over the
 * {@link #entries()}, {@link #keys()}, and {@link #values()} is ordered and faster than an unordered map. Keys can also
 * be accessed and the order changed using {@link #orderedKeys()}. There is some additional overhead for put and remove.
 * When used for faster iteration versus ObjectIntMap and the order does not actually matter, copying during remove
 * can be greatly reduced by setting {@link Array#ordered} to false for {@link ObjectIntOrderedMap#orderedKeys()}.
 * <br>
 * This map uses Fibonacci hashing to help distribute what may be very bad hashCode() results across the whole capacity.
 * See <a href="https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">Malte Skarupke's blog post</a>
 * for more information on Fibonacci hashing. It uses linear probing to resolve collisions, which is far from the academically
 * optimal algorithm, but performs considerably better in practice than most alternatives, and combined with Fibonacci hashing, it
 * can handle "normal" generated hashCode() implementations, and not just theoretically optimal hashing functions. Even if all
 * hashCode()s this is given collide, it will still work, just slowly; the older libGDX implementation using cuckoo hashing would
 * crash with an OutOfMemoryError with under 50 collisions.
 * <br>
 * This map performs very fast contains and remove (typically O(1), worst case O(n) due to moving items in an Array, but still
 * very fast). Add may be a bit slower, depending on hash collisions, but this data structure is somewhat collision-resistant.
 * Load factors greater than 0.91 greatly increase the chances the map will have to rehash to the next higher POT size.
 * Memory usage is excellent, and the aforementioned collision-resistance helps avoid too much capacity resizing.
 * <br>
 * The <a href="http://codecapsule.com/2013/11/17/robin-hood-hashing-backward-shift-deletion/">backward-shift algorithm</a>
 * used during removal apparently is key to the good performance of this implementation, even though this doesn't use Robin Hood
 * hashing; the performance of {@link #remove(Object, int)} has improved considerably over the previous libGDX version.
 * <br>
 * Iteration should be faster with ObjectIntOrderedMap than with ObjectIntMap.
 *
 * @author Tommy Ettinger
 * @author Nathan Sweet
 */
public class ObjectIntOrderedMap<K> extends ObjectIntMap<K> {
	private final Array<K> keys;

	public ObjectIntOrderedMap () {
		keys = new Array();
	}

	public ObjectIntOrderedMap (int initialCapacity) {
		super(initialCapacity);
		keys = new Array(initialCapacity);
	}

	public ObjectIntOrderedMap (int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		keys = new Array(initialCapacity);
	}

	public ObjectIntOrderedMap (ObjectIntOrderedMap<? extends K> map) {
		super(map);
		keys = new Array(map.keys);
	}

	public void put (K key, int value) {
		if (key == null)
			throw new IllegalArgumentException("key cannot be null.");
		int[] valueTable = this.valueTable;
		int b = place(key);
		int loc = locateKey(key, b);
		// an identical key already exists
		if (loc != -1) {
			valueTable[loc] = value;
			return;
		}
		keys.add(key);
		K[] keyTable = this.keyTable;
		for (int i = b; ; i = (i + 1) & mask) {
			// space is available so we insert and break (resize is later)
			if (keyTable[i] == null) {
				keyTable[i] = key;
				valueTable[i] = value;
				break;
			}
		}
		if (++size >= threshold) {
			resize(keyTable.length << 1);
		}
	}

	public void putAll (ObjectIntOrderedMap<K> map) {
		ensureCapacity(map.size);
		keys.ensureCapacity(map.size);
		final K[] keys = map.keys.items;
		K k;
		for (int i = 0, n = map.keys.size; i < n; i++) {
			put((k = keys[i]), map.get(k, 0));
		}
	}

	public int remove (K key, int defaultValue) {
		keys.removeValue(key, false);
		return super.remove(key, defaultValue);
	}

	public int removeIndex (int index) {
		return super.remove(keys.removeIndex(index), 0);
	}

	/**
	 * Changes the key {@code before} to {@code after} without changing its position in the order or its value.
	 * Returns true if {@code after} has been added to the OrderedMap and {@code before} has been removed;
	 * returns false if {@code after} is already present or {@code before} is not present. If you are iterating
	 * over an OrderedMap and have an index, you should prefer {@link #alterIndex(int, Object)}, which doesn't
	 * need to search for an index like this does and so can be faster.
	 * @param before a key that must be present for this to succeed
	 * @param after a key that must not be in this map for this to succeed
	 * @return true if {@code before} was removed and {@code after} was added, false otherwise
	 */
	public boolean alter(K before, K after)
	{
		if(containsKey(after))
			return false;
		final int index = keys.indexOf(before, false);
		if(index == -1)
			return false;
		super.put(after, super.remove(before, 0));
		keys.set(index, after);
		return true;
	}

	/**
	 * Changes the key at the given {@code index} in the order to {@code after}, without changing the ordering of other entries or
	 * any values If {@code after} is already present, this returns false; it will also return false if {@code index} is invalid
	 * for the size of this map. Otherwise, it returns true. Unlike {@link #alter(Object, Object)}, this operates in constant
	 * time.
	 * @param index the index in the order of the key to change; must be non-negative and less than {@link #size}
	 * @param after the key that will replace the contents at {@code index}; this key must not be present for this to succeed
	 * @return true if {@code after} successfully replaced the key at {@code index}, false otherwise
	 */
	public boolean alterIndex(int index, K after)
	{
		if(index < 0 || index >= size || containsKey(after))
			return false;
		super.put(after, super.remove(keys.get(index), 0));
		keys.set(index, after);
		return true;
	}

	public void clear (int maximumCapacity) {
		keys.clear();
		super.clear(maximumCapacity);
	}

	public void clear () {
		keys.clear();
		super.clear();
	}

	public Array<K> orderedKeys () {
		return keys;
	}

	public Entries<K> iterator () {
		return entries();
	}

	/**
	 * Returns an iterator for the entries in the map. Remove is supported.
	 * <p>
	 * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
	 * Use the {@link OrderedMapEntries} constructor for nested or multithreaded iteration.
	 */
	public Entries<K> entries () {
		if (Collections.allocateIterators)
			return new OrderedMapEntries(this);
		if (entries1 == null) {
			entries1 = new OrderedMapEntries(this);
			entries2 = new OrderedMapEntries(this);
		}
		if (!entries1.valid) {
			entries1.reset();
			entries1.valid = true;
			entries2.valid = false;
			return entries1;
		}
		entries2.reset();
		entries2.valid = true;
		entries1.valid = false;
		return entries2;
	}

	/**
	 * Returns an iterator for the values in the map. Remove is supported.
	 * <p>
	 * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
	 * Use the {@link OrderedMapValues} constructor for nested or multithreaded iteration.
	 */
	public Values values () {
		if (Collections.allocateIterators)
			return new OrderedMapValues(this);
		if (values1 == null) {
			values1 = new OrderedMapValues(this);
			values2 = new OrderedMapValues(this);
		}
		if (!values1.valid) {
			values1.reset();
			values1.valid = true;
			values2.valid = false;
			return values1;
		}
		values2.reset();
		values2.valid = true;
		values1.valid = false;
		return values2;
	}

	/**
	 * Returns an iterator for the keys in the map. Remove is supported.
	 * <p>
	 * If {@link Collections#allocateIterators} is false, the same iterator instance is returned each time this method is called.
	 * Use the {@link OrderedMapKeys} constructor for nested or multithreaded iteration.
	 */
	public Keys<K> keys () {
		if (Collections.allocateIterators)
			return new OrderedMapKeys(this);
		if (keys1 == null) {
			keys1 = new OrderedMapKeys(this);
			keys2 = new OrderedMapKeys(this);
		}
		if (!keys1.valid) {
			keys1.reset();
			keys1.valid = true;
			keys2.valid = false;
			return keys1;
		}
		keys2.reset();
		keys2.valid = true;
		keys1.valid = false;
		return keys2;
	}

	public String toString () {
		if (size == 0)
			return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		Array<K> keys = this.keys;
		for (int i = 0, n = keys.size; i < n; i++) {
			K key = keys.get(i);
			if (i > 0)
				buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(get(key, 0));
		}
		buffer.append('}');
		return buffer.toString();
	}

	static public class OrderedMapEntries<K> extends Entries<K> {
		private Array<K> keys;

		public OrderedMapEntries (ObjectIntOrderedMap<K> map) {
			super(map);
			keys = map.keys;
		}

		public void reset () {
			currentIndex = -1;
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public Entry next () {
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			currentIndex = nextIndex;
			entry.key = keys.get(nextIndex);
			entry.value = map.get(entry.key, 0);
			nextIndex++;
			hasNext = nextIndex < map.size;
			return entry;
		}

		public void remove () {
			if (currentIndex < 0)
				throw new IllegalStateException("next must be called before remove.");
			map.remove(entry.key, 0);
			nextIndex--;
			currentIndex = -1;
		}
	}

	static public class OrderedMapKeys<K> extends Keys<K> {
		private Array<K> keys;

		public OrderedMapKeys (ObjectIntOrderedMap<K> map) {
			super(map);
			keys = map.keys;
		}

		public void reset () {
			currentIndex = -1;
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public K next () {
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			K key = keys.get(nextIndex);
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < map.size;
			return key;
		}

		public void remove () {
			if (currentIndex < 0)
				throw new IllegalStateException("next must be called before remove.");
			((ObjectIntOrderedMap)map).removeIndex(currentIndex);
			nextIndex = currentIndex;
			currentIndex = -1;
		}

		public Array<K> toArray (Array<K> array) {
			array.addAll(keys, nextIndex, keys.size - nextIndex);
			nextIndex = keys.size;
			hasNext = false;
			return array;
		}

		public Array<K> toArray () {
			return toArray(new Array(true, keys.size - nextIndex));
		}
	}

	static public class OrderedMapValues extends Values {
		private Array keys;

		public OrderedMapValues (ObjectIntOrderedMap<?> map) {
			super(map);
			keys = map.keys;
		}

		public void reset () {
			currentIndex = -1;
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public int next () {
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			int value = map.get(keys.get(nextIndex), 0);
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < map.size;
			return value;
		}

		public void remove () {
			if (currentIndex < 0)
				throw new IllegalStateException("next must be called before remove.");
			((ObjectIntOrderedMap)map).removeIndex(currentIndex);
			nextIndex = currentIndex;
			currentIndex = -1;
		}

		public IntArray toArray (IntArray array) {
			int n = keys.size;
			array.ensureCapacity(n - nextIndex);
			Object[] keys = this.keys.items;
			for (int i = nextIndex; i < n; i++)
				array.add(map.get(keys[i], 0));
			currentIndex = n - 1;
			nextIndex = n;
			hasNext = false;
			return array;
		}

		public IntArray toArray () {
			return toArray(new IntArray(true, keys.size - nextIndex));
		}
	}
}
