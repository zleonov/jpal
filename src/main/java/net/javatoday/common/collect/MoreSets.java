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
     * Creates a {@code LinkedHashSet} containing the specified initial elements.
     * 
     * @param elements initial elements
     * @return a {@code LinkedHashSet} containing the specified initial elements
     */
    @SafeVarargs
    public static <E> LinkedHashSet<E> newLinkedHashSet(final E... elements) {
        checkNotNull(elements, "elements == null");
        final LinkedHashSet<E> set = Sets.newLinkedHashSet();
        Collections.addAll(set, elements);
        return set;
    }

    /**
     * Creates a {@code LinkedHashSet} containing the specified initial elements.
     * 
     * @param iterator the specified initial elements
     * @return a {@code LinkedHashSet} containing the specified initial elements
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet(final Iterator<? extends E> iterator) {
        checkNotNull(iterator, "iterator == null");
        final LinkedHashSet<E> set = Sets.newLinkedHashSet();
        Iterators.addAll(set, iterator);
        return set;
    }

    /**
     * Creates a {@code TreeSet} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements initial elements
     * @return a {@code TreeSet} containing the specified initial elements sorted according to their <i>natural ordering</i>
     */
    @SafeVarargs
    // Java 6 users need to change signature to <E extends Comparable>
    public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(final E... elements) {
        checkNotNull(elements, "elements == null");
        final TreeSet<E> treeSet = Sets.newTreeSet();
        Collections.addAll(treeSet, elements);
        return treeSet;
    }

    /**
     * Creates a {@code TreeSet} containing the elements returned by the specified iterator, sorted according to their
     * <i>natural ordering</i>.
     * 
     * @param elements the iterator whose elements are to be placed into this set
     * @return a {@code TreeSet} containing the elements returned by the specified iterator, sorted according to their
     *         <i>natural ordering</i>
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> TreeSet<E> newTreeSet(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final TreeSet<E> treeSet = Sets.newTreeSet();
        Iterators.addAll(treeSet, elements);
        return treeSet;
    }

}
