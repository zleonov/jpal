package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.NavigableSet;

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
     * Creates a {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements initial elements
     * @return a {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     */
    @SafeVarargs
    public static <E extends Comparable<? super E>> Skiplist<E> newSkiplist(final E... elements) {
        checkNotNull(elements, "elements == null");
        final Skiplist<E> sl = Skiplist.create();
        Collections.addAll(sl, elements);
        return sl;
    }

    /**
     * Creates a {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements initial elements
     * @return a {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     */

    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> Skiplist<E> newSkiplist(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final Skiplist<E> sl = Skiplist.create();
        Iterators.addAll(sl, elements);
        return sl;
    }

    /**
     * Creates a {@code Treelist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements initial elements
     * @return a {@code Treelist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     */
    @SafeVarargs
    public static <E extends Comparable<? super E>> Treelist<E> newTreelist(final E... elements) {
        checkNotNull(elements, "elements == null");
        final Treelist<E> tl = Treelist.create();
        Collections.addAll(tl, elements);
        return tl;
    }

    /**
     * Creates a {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     * ordering</i>.
     * 
     * @param elements initial elements
     * @return a {@code Skiplist} containing the specified initial elements sorted according to their <i>natural
     *         ordering</i>
     */

    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> Treelist<E> newTreelist(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final Treelist<E> treelist = Treelist.create();
        Iterators.addAll(treelist, elements);
        return treelist;
    }

    /**
     * Returns an {@code Iterator} over the specified {@code Collection} in reverse sequential order. If the specified
     * collection is an instance of {@code Deque} or {@code NavigableSet} the iterator is obtained by invoking the
     * {@code descendingIterator()} method, else an unmodifiable iterator is returned by calling the
     * {@code reverseOrder(Iterator)} method.
     * 
     * @param c the specified collection
     * @return an iterator over the specified collection in reverse sequential order
     */
    @SuppressWarnings({ "unchecked" })
    public static <E> Iterator<E> reverseOrder(final Collection<? extends E> c) {
        checkNotNull(c, "c == null");
        if (c instanceof Deque)
            return ((Deque<E>) c).descendingIterator();
        if (c instanceof NavigableSet<?>)
            return (Iterator<E>) ((NavigableSet<?>) c).descendingIterator();
        else
            return Iteration.reverseOrder(c.iterator());
    }

}
