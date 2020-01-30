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

/**
 * An unordered map that uses identity comparison for its object keys. This implementation uses linear probing with the backward-shift
 * algorithm for removal, and finds space for keys using Fibonacci hashing instead of the more-common power-of-two mask.
 * Null keys are not allowed. Null values are allowed. No allocation is done except when growing the table size. It uses
 * {@link System#identityHashCode(Object)} to hash keys, which may be slower than the hashCode() on some types that have it
 * already computed, like String; for String keys in particular, identity comparison is a challenge and some other map should be
 * used instead. This class implements {@link com.badlogic.gdx.utils.Json.Serializable}, but the behavior of Json serialization
 * with identity equality is uncertain at best. You may want to get separate key and value Arrays with {@link Keys#toArray()} and
 * {@link Values#toArray()} and serialize those, though even that technique may fail with some key types ({@code int[]} and other
 * primitive arrays can be used as keys here, but not serialized).
 * <br>
 * This map uses Fibonacci hashing to help distribute what may be very bad hashCode() results across the
 * whole capacity. See <a href="https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">Malte Skarupke's blog post</a>
 * for more information on Fibonacci hashing. It uses linear probing to resolve collisions, which is far from the academically
 * optimal algorithm, but performs considerably better in practice than most alternatives, and combined with Fibonacci hashing, it
 * can handle "normal" generated hashCode() implementations, and not just theoretically optimal hashing functions. Even if all
 * hashCode()s this is given collide, it will still work, just slowly; the older libGDX implementation using cuckoo hashing would
 * crash with an OutOfMemoryError with under 50 collisions.
 * <br>
 * This map performs very fast contains and remove (typically O(1), worst case O(n) due to occasional probing, but still very
 * fast). Add may be a bit slower, depending on hash collisions, but this data structure is somewhat collision-resistant.
 * Load factors greater than 0.91 greatly increase the chances the map will have to rehash to the next higher POT size.
 * <br>
 * The <a href="http://codecapsule.com/2013/11/17/robin-hood-hashing-backward-shift-deletion/">backward-shift algorithm</a>
 * used during removal apparently is key to the good performance of this implementation, even though this doesn't use Robin Hood
 * hashing; the performance of {@link #remove(Object)} has improved considerably over the previous libGDX version.
 * <br>
 * Iteration should be fast with OrderedSet and OrderedMap, whereas ObjectSet and ObjectMap aren't designed to provide especially
 * quick iteration.
 *
 * @author Tommy Ettinger
 * @author Nathan Sweet
 */
public class IdentityMap<K, V> extends ObjectMap<K, V> {

	/**
	 * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
	 */
	public IdentityMap () {
		super();
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public IdentityMap (int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public IdentityMap (int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new map identical to the specified map.
	 */
	public IdentityMap (IdentityMap<? extends K, ? extends V> map) {
		super(map);
	}

	@Override protected int place (K item) {
		return (int)(System.identityHashCode(item) * 0x9E3779B97F4A7C15L >>> shift);
		//return (System.identityHashCode(item) & mask);
	}

	@Override int locateKey (K key, int placement) {
		for (int i = placement; ; i = i + 1 & mask) {
			// empty space is available
			if (keyTable[i] == null) {
				return -1;
			}
			if (key == (keyTable[i])) {
				return i;
			}
		}
	}
}
