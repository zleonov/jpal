package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * More static utility methods which operate on or return {@link Sortedlist}s.
 * 
 * @author Zhenya Leonov
 * @see Lists
 */
public class Sortedlists {

    private Sortedlists() {
    }

    /**
     * Creates a mutable {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     * @see Skiplist#create()
     * @see Skiplist#orderedBy(java.util.Comparator)
     */
    @SafeVarargs
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <E extends Comparable<? super E>> Skiplist<E> newSkiplist(final E... elements) {
        checkNotNull(elements, "elements == null");
        final Skiplist<E> skiplist = Skiplist.create();
        Collections.addAll(skiplist, elements);
        return skiplist;
    }

    /**
     * Creates a mutable {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     * @see Skiplist#create()
     * @see Skiplist#orderedBy(java.util.Comparator)
     */
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <E extends Comparable<? super E>> Skiplist<E> newSkiplist(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final Skiplist<E> skiplist = Skiplist.create();
        Iterators.addAll(skiplist, elements);
        return skiplist;
    }

    /**
     * Creates a mutable {@code Treelist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code Treelist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     * @see Treelist#create()
     * @see Treelist#orderedBy(java.util.Comparator)
     */
    @SafeVarargs
    public static <E extends Comparable<? super E>> Treelist<E> newTreelist(final E... elements) {
        checkNotNull(elements, "elements == null");
        final Treelist<E> tl = Treelist.create();
        Collections.addAll(tl, elements);
        return tl;
    }

    /**
     * Creates a mutable {@code Treelist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements the specified initial elements
     * @return a mutable {@code Treelist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     * @see Treelist#create()
     * @see Treelist#orderedBy(java.util.Comparator)
     */
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <E extends Comparable<? super E>> Treelist<E> newTreelist(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final Treelist<E> treelist = Treelist.create();
        Iterators.addAll(treelist, elements);
        return treelist;
    }

}
