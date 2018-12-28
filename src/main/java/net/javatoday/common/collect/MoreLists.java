package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * More static utility methods which operate on or return {@link List}s.
 * 
 * @author Zhenya Leonov
 * @see Lists
 * @see List#indexOf(Object)
 * @see Iterables#contains(Iterable, Object)
 * @see Iterables#any(Iterable, Predicate)
 * @see Iterables#indexOf(Iterable, Predicate)
 */
public class MoreLists {

    private MoreLists() {
    }

    /**
     * Returns the index of the first occurrence of the specified element in the given list, starting the search at
     * {@code fromIndex}, or -1 if there is no such element.
     * 
     * @param list      the given list
     * @param element   the element to look for
     * @param fromIndex the index to start the search from
     * @return the index of the first occurrence of the specified element in the given list, starting the search at
     *         {@code fromIndex}, or -1 if there is no such element
     */
    public static <E> int indexOf(final List<E> list, final Object element, final int fromIndex) {
        checkNotNull(list, "list == null");
        checkElementIndex(fromIndex, list.size());

        if (list instanceof RandomAccess) { // no need to create an iterator
            for (int i = fromIndex; i < list.size(); i++)
                if (Objects.equals(list.get(i), element))
                    return i;
        } else {
            final ListIterator<E> iterator = list.listIterator(fromIndex);
            while (iterator.hasNext())
                if (Objects.equals(iterator.next(), element))
                    return iterator.previousIndex();
        }

        return -1;
    }

    /**
     * Returns the index of the first occurrence in the specified list of an element which satisfies the given predicate,
     * starting the search at {@code fromIndex}, or -1 if there is no such element.
     * <p>
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs to avoid a {@code NullPointerException}.
     * 
     * @param list      the specified list
     * @param predicate the given predicate
     * @param fromIndex the index to start the search from (inclusive)
     * @return the index of the first occurrence in the specified list of an element which satisfies the given predicate,
     *         starting the search at {@code fromIndex}, or -1 if there is no such element
     */
    public static <E> int indexOf(final List<E> list, final Predicate<? super E> predicate, final int fromIndex) {
        checkNotNull(list, "list == null");
        checkNotNull(predicate, "predicate == null");
        checkElementIndex(fromIndex, list.size());

        if (list instanceof RandomAccess) { // no need to create an iterator
            for (int i = fromIndex; i < list.size(); i++)
                if (predicate.test(list.get(i)))
                    return i;
        } else {
            final ListIterator<E> iterator = list.listIterator(fromIndex);
            while (iterator.hasNext())
                if (predicate.test(iterator.next()))
                    return iterator.previousIndex();
        }

        return -1;
    }

    /**
     * Returns the index of the last occurrence in the specified element in the given list, searching backward starting at
     * {@code fromIndex}, or -1 if there is no such element.
     * 
     * @param list      the specified list
     * @param element   the element to look for
     * @param fromIndex the index to start the search from
     * @return the index of the last occurrence in the specified element in the given list, searching backward starting at
     *         {@code fromIndex}, or -1 if there is no such element
     */
    public static <E> int lastIndexOf(final List<E> list, final Object element, final int fromIndex) {
        checkNotNull(list, "list == null");
        checkElementIndex(fromIndex, list.size());

        if (list instanceof RandomAccess) { // no need to create an iterator
            for (int i = fromIndex; i >= 0; i--)
                if (Objects.equals(list.get(i), element))
                    return i;
        } else {
            final ListIterator<E> iterator = list.listIterator(fromIndex + 1);
            while (iterator.hasPrevious())
                if (Objects.equals(iterator.previous(), element))
                    return iterator.nextIndex();
        }

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
        return lastIndexOf(list, predicate, list.size() - 1);
    }

    /**
     * Returns the index of the last occurrence in the specified list of an element which satisfies the given predicate,
     * searching backward starting at {@code fromIndex}, or -1 if there is no such element.
     * <p>
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs as well to avoid a {@code NullPointerException}.
     * 
     * @param list      the specified list
     * @param predicate the given predicate
     * @param fromIndex the index to start the search from
     * @return the index of the last occurrence in the specified list of an element which satisfies the given predicate,
     *         searching backward starting at {@code fromIndex}, or -1 if there is no such element.
     */
    public static <E> int lastIndexOf(final List<E> list, final Predicate<? super E> predicate, final int fromIndex) {
        checkNotNull(list, "list == null");
        checkNotNull(predicate, "predicate == null");
        checkElementIndex(fromIndex, list.size());

        if (list instanceof RandomAccess) { // no need to create an iterator
            for (int i = fromIndex; i >= 0; i--)
                if (predicate.test(list.get(i)))
                    return i;
        } else {
            final ListIterator<E> iterator = list.listIterator(fromIndex + 1);
            while (iterator.hasPrevious())
                if (predicate.test(iterator.previous()))
                    return iterator.nextIndex();
        }

        return -1;
    }

    /**
     * Creates a mutable {@code LinkedList} containing the specified initial elements.
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
     * Creates a mutable {@code RankList} containing the specified initial elements.
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
     * Creates a mutable {@code RankList} containing the specified initial elements.
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
        checkElementIndex(fromIndex, list.size());
        return list.subList(fromIndex, list.size());
    }
}
