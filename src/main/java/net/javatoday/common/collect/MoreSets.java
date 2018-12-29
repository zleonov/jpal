/*
<<<<<<< HEAD
 * Copyright (C) 2019 Zhenya Leonov
=======
 * Copyright (C) 2018 Zhenya Leonov
>>>>>>> branch 'master' of git@github.com:zleonov/jpal.git
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * More static utility methods which operate on or return {@link Set}s.
 * 
 * @author Zhenya Leonov
 * @see Sets
 */
public class MoreSets {

    private MoreSets() {
    }

    /**
     * Creates a mutable {@code LinkedHashSet} containing the specified initial elements.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code LinkedHashSet} containing the specified initial elements
     * @see Sets#newLinkedHashSet()
     * @see Sets#newLinkedHashSetWithExpectedSize(int)
     * @see Sets#newLinkedHashSet(Iterable)
     */
    @SafeVarargs
    public static <E> LinkedHashSet<E> newLinkedHashSet(final E... elements) {
        checkNotNull(elements, "elements == null");
        final LinkedHashSet<E> set = Sets.newLinkedHashSet();
        Collections.addAll(set, elements);
        return set;
    }

    /**
     * Creates a mutable {@code LinkedHashSet} containing the specified initial elements.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code LinkedHashSet} containing the specified initial elements
     * @see Sets#newLinkedHashSet()
     * @see Sets#newLinkedHashSetWithExpectedSize(int)
     * @see Sets#newLinkedHashSet(Iterable)
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final LinkedHashSet<E> set = Sets.newLinkedHashSet();
        Iterators.addAll(set, elements);
        return set;
    }

    /**
     * Creates a mutable {@code TreeSet} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code TreeSet} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     * @see Sets#newTreeSet()
     * @see Sets#newTreeSet(java.util.Comparator)
     * @see Sets#newTreeSet(Iterable)
     */
    @SafeVarargs
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(final E... elements) {
        checkNotNull(elements, "elements == null");
        final TreeSet<E> treeSet = Sets.newTreeSet();
        Collections.addAll(treeSet, elements);
        return treeSet;
    }

    /**
     * Creates a mutable {@code TreeSet} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code TreeSet} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     * @see Sets#newTreeSet()
     * @see Sets#newTreeSet(java.util.Comparator)
     * @see Sets#newTreeSet(Iterable)
     */
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final TreeSet<E> treeSet = Sets.newTreeSet();
        Iterators.addAll(treeSet, elements);
        return treeSet;
    }

}
