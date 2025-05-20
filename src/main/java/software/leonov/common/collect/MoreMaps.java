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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Queues;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Static utility methods for working with {@code Map}s.
 * 
 * @author Zhenya Leonov
 * @see Maps
 * @see Collections
 * @see Collections2
 * @see Lists
 * @see MoreLists
 * @see Sets
 * @see Iterables
 * @see Iterators
 * @see Iteration
 * @see Queues
 * @see MoreQueues
 * @see Arrays
 * @see ObjectArrays
 * @see MoreArrays
 */
// See: https://bugs.openjdk.java.net/browse/JDK-6394757
final public class MoreMaps {

    private MoreMaps() {
    }

    /**
     * Returns a specialized {@link SetMultimap} implementation for use with {@code Enum} type keys, which uses a
     * {@link HashSet} to store values.
     * <p>
     * Attempts to insert a {@code null} keys will throw {@code NullPointerException}s. Attempts to remove or test for the
     * presence of a {@code null} key will succeed. Values may be {@code null}.
     * 
     * @param <K>  the type of {@code Enum} keys
     * @param <V>  the type of values
     * @param type the {@code Class} of the key type for this {@code Enum} {@code Multimap}
     * @return a specialized {@link SetMultimap} implementation for use with {@code Enum} type keys
     */
    public static <K extends Enum<K>, V> SetMultimap<K, V> newEnumHashMultimap(final Class<K> type) {
        checkNotNull(type, "type == null");
        return Multimaps.newSetMultimap(new EnumMap<>(type), HashSet::new);
    }

    /**
     * Returns a specialized {@link SetMultimap} implementation for use with {@code Enum} type keys, which uses a
     * {@link LinkedHashSet} to store values.
     * <p>
     * Attempts to insert a {@code null} keys will throw {@code NullPointerException}s. Attempts to remove or test for the
     * presence of a {@code null} key will succeed. Values may be {@code null}.
     * 
     * @param <K>  the type of {@code Enum} keys
     * @param <V>  the type of values
     * @param type the {@code Class} of the key type for this {@code Enum} {@code Multimap}
     * @return a specialized {@link SetMultimap} implementation for use with {@code Enum} type keys
     */
    public static <K extends Enum<K>, V> SetMultimap<K, V> newEnumLinkedHashMultimap(final Class<K> type) {
        checkNotNull(type, "type == null");
        return Multimaps.newSetMultimap(new EnumMap<>(type), LinkedHashSet::new);
    }

    /**
     * Returns a specialized {@link SetMultimap} implementation for use with {@code Enum} type keys, which uses an
     * {@link ArrayList} to store values.
     * <p>
     * Attempts to insert a {@code null} keys will throw {@code NullPointerException}s. Attempts to remove or test for the
     * presence of a {@code null} key will succeed. Values may be {@code null}.
     * 
     * @param <K>  the type of {@code Enum} keys
     * @param <V>  the type of values
     * @param type the {@code Class} of the key type for this {@code Enum} {@code Multimap}
     * @return a specialized {@link ListMultimap} implementation for use with {@code Enum} type keys
     */
    public static <K extends Enum<K>, V> ListMultimap<K, V> newEnumListMultimap(final Class<K> type) {
        checkNotNull(type, "type == null");
        return Multimaps.newListMultimap(new EnumMap<>(type), ArrayList::new);
    }

}