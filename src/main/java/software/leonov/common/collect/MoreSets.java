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

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/**
 * Static utility methods which operate on or return {@code Set}s.
 * 
 * @author Zhenya Leonov
 * @see Maps
 * @see MoreMultimaps
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
 * @see MoreSets
 * @see Iterators#forArray(Object[])
 * @see Arrays#stream(Object[])
 */
final public class MoreSets {

    private MoreSets() {
    }

    /**
     * Replaces an equivalent element in the set with the specified element, or adds it if no equivalent element exists.
     * <p>
     * This method first removes any element equivalent to the specified element, then adds the specified element. This is
     * useful when you want to update an element in a set while maintaining set semantics.
     * <p>
     * Example use case: updating a case-insensitive string set where you want to replace "hello" with "HELLO" while
     * maintaining the same logical element.
     *
     * @param <T>     the type of elements in the set
     * @param set     the set to modify
     * @param element the element to replace or add (may be null if the set permits null elements)
     * @return {@code true} if an equivalent element was replaced or {@code false} if the element was simply added
     * @throws ClassCastException            if the element is of a type incompatible with the set
     * @throws UnsupportedOperationException if the set does not support the remove or add operations
     */
    public static <T> boolean replace(final Set<? super T> set, final T element) {
        checkNotNull(set, "set == null");
        final boolean removed = set.remove(element);
        set.add(element);
        return removed;
    }

}