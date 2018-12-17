package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * More static utility methods which operate on or return {@link List}s.
 * 
 * @author Zhenya Leonov
 * @see Lists
 */
public class MoreLists {

    private MoreLists() {
    }

    /**
     * Returns the index of the first occurrence in the specified list (starting the search at the specified index) of an
     * element which satisfies the given predicate, or -1 if there is no such element.
     * <p>
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs to avoid a {@code NullPointerException}.
     * 
     * @param list      the specified list
     * @param fromIndex the index to start the search from (inclusive)
     * @param predicate the given predicate
     * @return the index of the first occurrence in the specified list of an element which satisfies the given predicate, or
     *         -1 if there is no such element
     */
    public static <E> int indexOf(final List<E> list, final int fromIndex, final Predicate<? super E> predicate) {
        checkNotNull(list, "list == null");
        checkNotNull(predicate, "predicate == null");
        final ListIterator<E> e = list.listIterator(fromIndex);
        while (e.hasNext())
            if (predicate.apply(e.next()))
                return e.previousIndex();
        return -1;
    }

    /**
     * Returns the index of the first occurrence in the specified list of an element which satisfies the given predicate, or
     * -1 if there is no such element.
     * <p>
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs to avoid a {@code NullPointerException}.
     * 
     * @param list      the specified list
     * @param predicate the given predicate
     * @return the index of the first occurrence in the specified list of an element which satisfies the given predicate, or
     *         -1 if there is no such element
     */
    public static <E> int indexOf(final List<E> list, final Predicate<? super E> predicate) {
        checkNotNull(list, "list == null");
        checkNotNull(predicate, "predicate == null");
        final ListIterator<E> e = list.listIterator();
        while (e.hasNext())
            if (predicate.apply(e.next()))
                return e.previousIndex();
        return -1;
    }

    /**
     * Returns the index of the first occurrence in the specified sorted-list (starting the search at the specified index)
     * of an element which satisfies the given predicate, or -1 if there is no such element.
     * <p>
     * Note: If the specified sorted-list allows {@code null} elements the given predicate must be able to handle
     * {@code null} inputs to avoid a {@code NullPointerException}.
     * 
     * @param sortedlist the specified sorted-list (starting the search at the specified index)
     * @param fromIndex  the index to start the search from (inclusive)
     * @param predicate  the given predicate
     * @return the index of the first occurrence in the specified sorted-list of an element which satisfies the given
     *         predicate, or -1 if there is no such element
     */
    public static <E> int indexOf(final Sortedlist<E> sortedlist, final int fromIndex, final Predicate<? super E> predicate) {
        checkNotNull(sortedlist, "sortedlist == null");
        checkNotNull(predicate, "predicate == null");
        final ListIterator<E> e = sortedlist.listIterator(fromIndex);
        while (e.hasNext())
            if (predicate.apply(e.next()))
                return e.previousIndex();
        return -1;
    }

    /**
     * Returns the index of the first occurrence in the specified sorted-list of an element which satisfies the given
     * predicate, or -1 if there is no such element.
     * <p>
     * Note: If the specified sorted-list allows {@code null} elements the given predicate must be able to handle
     * {@code null} inputs to avoid a {@code NullPointerException}.
     * 
     * @param sortedlist the specified sorted-list
     * @param predicate  the given predicate
     * @return the index of the first occurrence in the specified sorted-list of an element which satisfies the given
     *         predicate, or -1 if there is no such element
     */
    public static <E> int indexOf(final Sortedlist<E> sortedlist, final Predicate<? super E> predicate) {
        checkNotNull(sortedlist, "sortedlist == null");
        checkNotNull(predicate, "predicate == null");
        final ListIterator<E> e = sortedlist.listIterator();
        while (e.hasNext())
            if (predicate.apply(e.next()))
                return e.previousIndex();
        return -1;
    }

    /**
     * Returns the index of the last occurrence in the specified list of an element which satisfies the given predicate, or
     * -1 if there is no such element.
     * <p>
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs as well to avoid a {@code NullPointerException}.
     * 
     * @param list      the specified list
     * @param predicate the given predicate
     * @return the index of the last occurrence in the specified list of an element which satisfies the given predicate, or
     *         -1 if there is no such element
     */
    public static <E> int lastIndexOf(final List<E> list, final Predicate<? super E> predicate) {
        checkNotNull(list, "list == null");
        checkNotNull(predicate, "predicate == null");
        final ListIterator<E> e = list.listIterator(list.size());
        while (e.hasPrevious())
            if (predicate.apply(e.next()))
                return e.previousIndex();
        return -1;
    }

    /**
     * Returns the index of the last occurrence in the specified sorted-sorted-list of an element which satisfies the given
     * predicate, or -1 if there is no such element.
     * <p>
     * Note: If the specified sorted-list allows {@code null} elements the given predicate must be able to handle
     * {@code null} inputs as well to avoid a {@code NullPointerException}.
     * 
     * @param sortedlist the specified sorted-list
     * @param predicate  the given predicate
     * @return the index of the last occurrence in the specified sorted-list of an element which satisfies the given
     *         predicate, or -1 if there is no such element
     */
    public static <E> int lastIndexOf(final Sortedlist<E> sortedlist, final Predicate<? super E> predicate) {
        checkNotNull(sortedlist, "sortedlist == null");
        checkNotNull(predicate, "predicate == null");
        final ListIterator<E> e = sortedlist.listIterator(sortedlist.size());
        while (e.hasPrevious())
            if (predicate.apply(e.next()))
                return e.previousIndex();
        return -1;
    }

    /**
     * Creates a {@code LinkedList} containing the specified initial elements.
     * 
     * @param elements initial elements
     * @return a {@code LinkedList} containing the specified initial elements
     */
    @SafeVarargs
    public static <E> LinkedList<E> newLinkedList(final E... elements) {
        checkNotNull(elements, "elements == null");
        final LinkedList<E> linkedList = Lists.newLinkedList();
        Collections.addAll(linkedList, elements);
        return linkedList;
    }

    /**
     * Creates a {@code RankList} containing the specified initial elements.
     * 
     * @param elements initial elements
     * @return a {@code RankList} containing the specified initial elements
     */
    @SafeVarargs
    public static <E> RankList<E> newRankList(final E... elements) {
        checkNotNull(elements, "elements == null");
        final RankList<E> rankList = RankList.create();
        Collections.addAll(rankList, elements);
        return rankList;
    }

    /**
     * Creates a {@code RankList} containing the specified initial elements.
     * 
     * @param elements the specified initial elements
     * @return a {@code RankList} containing the specified initial elements
     */
    public static <E> RankList<E> newRankList(final Iterator<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final RankList<E> rankList = RankList.create();
        Iterators.addAll(rankList, elements);
        return rankList;
    }

    /**
     * Returns a view of the portion of the specified list between the {@code fromIndex} to the end of the list.
     * <p>
     * Equivalent to calling {@link List#subList(int, int) list.subList(beginIndex, list.size())}.
     * 
     * @param list      the specified list
     * @param fromIndex the beginning index (inclusive)
     * @return a view of the portion of the specified list between the {@code fromIndex} to the end of the list
     */
    public static <E> List<E> subList(final List<E> list, final int fromIndex) {
        checkNotNull(list, "list == null");
        checkArgument(fromIndex >= 0, "fromIndex < 0");
        return list.subList(fromIndex, list.size());
    }
}
