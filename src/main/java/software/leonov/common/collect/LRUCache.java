/*
 * Copyright (C) 2019 Zhenya Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.leonov.common.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A special {@link LinkedHashMap#LinkedHashMap(int, float, boolean) LinkedHashMap} which has a maximum size and whose
 * iteration order is the order in which its entries were last accessed, from least-recently to most-recently.
 * <p>
 * This map can be treated as a simple
 * <a target="_blank" href="https://en.wikipedia.org/wiki/Cache_replacement_policies#Least_recently_used_(LRU)">LRU</a> cache. Invoking
 * the {@link Map#put(Object, Object)}, {@link Map#putIfAbsent(Object, Object)}, {@link Map#get(Object)},
 * {@link Map#getOrDefault(Object, Object)}, {@link Map#compute(Object, BiFunction)} ,
 * {@link Map#computeIfAbsent(Object, Function)}, {@link Map#computeIfPresent(Object, BiFunction)}, or
 * {@link Map#merge(Object, Object, BiFunction)} methods results in an access to the corresponding entry (assuming it
 * exists after the invocation completes). The {@link Map#replace(Object, Object)} and
 * {@link Map#replace(Object, Object, Object)} methods only result in an access of the entry if the value is replaced.
 * The {@link Map#putAll(Map)} method generates one entry access for each mapping in the specified map, in the order
 * that key-value mappings are provided by the specified map's entry set iterator. <i>No other methods generate entry
 * accesses.</i> In particular, operations on collection-views do <i>not</i> affect the order of iteration of the
 * backing map.
 * <p>
 * <b>Note:</b> This class is provided as a convenience to developers who are making casual use of caching idioms.
 * Production projects should consider using Guava's
 * <a target="_blank" href="https://github.com/google/guava/wiki/CachesExplained">Caching facility</a>.
 * 
 * @author Zhenya Leonov
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
final public class LRUCache<K, V> extends LinkedHashMap<K, V> implements BoundedMap<K, V> {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private final int maximumSize;

    private LRUCache(final int initialCapacity, final float loadFactor, final int maximumSize) {
        super(initialCapacity, loadFactor, true);
        this.maximumSize = maximumSize;
    }

    /**
     * Creates a new mutable {@code LRUCache} with the specified maximum size.
     * 
     * @param maximumSize the maximum size of this map
     * @return a new mutable {@code LRUCache} with the specified maximum size
     */
    public static <K, V> LRUCache<K, V> create(final int maximumSize) {
        checkArgument(maximumSize > 0, "maximumSize < 1");
        return new LRUCache<K, V>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, maximumSize);
    }

    /**
     * Creates a new mutable {@code LRUCache} with the specified initial capacity, load factor, and maximum size.
     * 
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @param maximumSize     the maximum size of this map
     * @return a new mutable {@code LRUCache} with the specified initial capacity, load factor, and maximum size
     */
    public static <K, V> LRUCache<K, V> create(final int initialCapacity, final float loadFactor, final int maximumSize) {
        checkArgument(maximumSize > 0, "maximumSize < 1");
        checkArgument(initialCapacity >= 0, "initialCapacity < 0");
        checkArgument(loadFactor > 0, "loadFactor < 1");
        checkArgument(!Float.isNaN(loadFactor), "Float.isNaN(loadFactor)");
        return new LRUCache<K, V>(initialCapacity, loadFactor, maximumSize);
    }

    /**
     * Creates a new mutable {@code LRUCache} with the specified initial capacity and maximum size.
     * 
     * @param initialCapacity the initial capacity
     * @param maximumSize     the maximum size of this map
     * @return a new mutable {@code LRUCache} with the specified initial capacity and maximum size
     */
    public static <K, V> LRUCache<K, V> create(final int initialCapacity, final int maximumSize) {
        checkArgument(maximumSize > 0, "maximumSize < 1");
        checkArgument(initialCapacity >= 0, "initialCapacity < 0");
        return new LRUCache<K, V>(initialCapacity, DEFAULT_LOAD_FACTOR, maximumSize);
    }

    /**
     * Creates a new mutable {@code LRUCache} whose maximum size is equal to and containing the same mappings as the
     * specified map.
     * 
     * @param m the specified map
     * @return a new mutable {@code LRUCache} whose maximum size is equal to and containing the same mappings as the
     *         specified map
     */
    public static <K, V> LRUCache<K, V> create(final Map<? extends K, ? extends V> m) {
        checkNotNull(m, "m == null");
        final LRUCache<K, V> map = new LRUCache<K, V>(m.size(), DEFAULT_LOAD_FACTOR, m.size());
        map.putAll(m);
        return map;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maximumSize;
    }

    @Override
    public int remainingCapacity() {
        return maximumSize - size();
    }

}
