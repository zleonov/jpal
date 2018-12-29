/*
 * Copyright (C) 2018 Zhenya Leonov
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
package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * More static utility methods which operate on or return {@link Map}s.
 * 
 * @author Zhenya Leonov
 * @see Maps
 */
public class MoreMaps {

    private MoreMaps() {
    }

    /**
     * Creates an empty mutable {@code LinkedHashMap} with the specified ordering mode.
     * 
     * @param accessOrder the ordering mode: {@code true} for access-order, {@code false} for insertion-order
     * @return an empty mutable {@code LinkedHashMap} with the specified ordering mode
     * @see Maps#newLinkedHashMap()
     * @see Maps#newLinkedHashMap(Map)
     * @see Maps#newLinkedHashMapWithExpectedSize(int)
     */
    public static <K, V> Map<K, V> newLinkedHashMap(final boolean accessOrder) {
        return new LinkedHashMap<K, V>(16, .75F, accessOrder);
    }

    /**
     * Creates a mutable {@code LinkedHashMap} with the specified ordering mode, containing the same mappings as the
     * specified map.
     * 
     * @param m           the map whose mappings this map should contain
     * @param accessOrder the ordering mode: {@code true} for access-order, {@code false} for insertion-order
     * @return a mutable {@code LinkedHashMap} with the specified ordering mode, containing the same mappings as the
     *         specified map
     * @see LinkedHashMap
     */
    public static <K, V> Map<K, V> newLinkedHashMap(final Map<? extends K, ? extends V> m, final boolean accessOrder) {
        checkNotNull(m, "m == null");
        final Map<K, V> map = new LinkedHashMap<K, V>(Math.max((int) (m.size() / .75F) + 1, 16), .75F, accessOrder);
        map.putAll(m);
        return map;
    }

    /**
     * Creates an empty mutable {@code LinkedHashMap} with the given ordering mode and enough capacity to hold the specified
     * number of entries without rehashing.
     * 
     * @param expectedSize the expected size
     * @param accessOrder  the ordering mode: {@code true} for access-order, {@code false} for insertion-order
     * @return an empty mutable {@code LinkedHashMap} with the given ordering mode and enough capacity to hold the specified
     *         number of entries without rehashing
     * @see LinkedHashMap
     */
    public static <K, V> Map<K, V> newLinkedHashMapWithExpectedSize(final int expectedSize, final boolean accessOrder) {
        checkArgument(expectedSize >= 0);
        return new LinkedHashMap<K, V>(Math.max(expectedSize * 2, 16), .75F, accessOrder);
    }

}
