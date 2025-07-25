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
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Predicate;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/**
 * More static utility methods which operate on or return {@link List}s.
 * <p>
 * <b>Note:</b> Methods in this class accept {@link java.util.function.Predicate java.util.function.Predicate}s which
 * are interchangeable with their their Guava counterparts {@link com.google.common.base.Predicate
 * com.google.common.base.Predicate}s, see {@link com.google.common.base.Predicate com.google.common.base.Predicate} for
 * more information.
 * 
 * @author Zhenya Leonov
 * @see Maps
 * @see MoreMultimaps
 * @see Collections
 * @see Collections2
 * @see Lists
 * @see MoreLists
 * @see Sets
 * @see MoreSets
 * @see Iterables
 * @see Iterators
 * @see Iteration
 * @see Queues
 * @see MoreQueues
 * @see Arrays
 * @see ObjectArrays
 * @see MoreArrays
 */
public class MoreLists {

    private MoreLists() {
    }

    /**
     * Returns the index of the first occurrence of the specified element in the given list, starting the search at
     * {@code fromIndex}, or -1 if there is no such element.
     * 
     * @param <E>       the element type
     * @param list      the given list
     * @param element   the element to look for
     * @param fromIndex the index to start the search from
     * @return the index of the first occurrence of the specified element in the given list, starting the search at
     *         {@code fromIndex}, or -1 if there is no such element
     * @see List#indexOf(Object)
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
     * <b>Note:</b> If the specified list allows {@code null} elements the given predicate must be able to handle
     * {@code null} inputs to avoid a {@code NullPointerException}.
     * 
     * @param <E>       the element type
     * @param list      the specified list
     * @param predicate the given predicate
     * @param fromIndex the index to start the search from (inclusive)
     * @return the index of the first occurrence in the specified list of an element which satisfies the given predicate,
     *         starting the search at {@code fromIndex}, or -1 if there is no such element
     * @see Iterables#indexOf(Iterable, Predicate)
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
     * @param <E>       the element type
     * @param list      the specified list
     * @param element   the element to look for
     * @param fromIndex the index to start the search from
     * @return the index of the last occurrence in the specified element in the given list, searching backward starting at
     *         {@code fromIndex}, or -1 if there is no such element
     * @see List#indexOf(Object)
     * @see List#lastIndexOf(Object)
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
     * <b>Note:</b> If the specified list allows {@code null} elements the given predicate must be able to handle
     * {@code null} inputs as well to avoid a {@code NullPointerException}.
     * 
     * @param <E>       the element type
     * @param list      the specified list
     * @param predicate the given predicate
     * @return the index of the last occurrence in the specified list of an element which satisfies the given predicate, or
     *         -1 if there is no such element
     * @see Iterables#indexOf(Iterable, Predicate)
     */
    public static <E> int lastIndexOf(final List<E> list, final Predicate<? super E> predicate) {
        return lastIndexOf(list, predicate, list.size() - 1);
    }

    /**
     * Returns the index of the last occurrence in the specified list of an element which satisfies the given predicate,
     * searching backward starting at {@code fromIndex}, or -1 if there is no such element.
     * <p>
     * <b>Note:</b> If the specified list allows {@code null} elements the given predicate must be able to handle
     * {@code null} inputs as well to avoid a {@code NullPointerException}.
     * 
     * @param <E>       the element type
     * @param list      the specified list
     * @param predicate the given predicate
     * @param fromIndex the index to start the search from
     * @return the index of the last occurrence in the specified list of an element which satisfies the given predicate,
     *         searching backward starting at {@code fromIndex}, or -1 if there is no such element
     * @see Iterables#indexOf(Iterable, Predicate)
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
     * Returns a view of a portion of the specified list between the given index (inclusive) and the end of the list.
     * <p>
     * This method is the {@code List} analog of {@link String#substring(int)} and is equivalent to
     * {@code list.subList(int, list.size())}.
     * 
     * @param <E>  the type of elements in the list
     * @param list the specified list
     * @param from low endpoint (inclusive) of the sub-list
     * @return a view of a portion of the specified list between the given {@code from} index (inclusive) and the end of the
     *         list
     */
    public static <E> List<E> sublist(final List<E> list, final int from) {
        checkNotNull(list, "list == null");
        checkArgument(from >= 0, "from < 0", from);
        checkArgument(from <= list.size(), "from > list.size()", from, list.size());
        return list.subList(from, list.size());
    }

    /**
     * Returns the first element in the specified list.
     * 
     * @param <E>  the type of elements in the list
     * @param list the specified list
     * @return the first element in the specified list
     */
    public static <E> E getFirst(final List<? extends E> list) {
        checkNotNull(list, "list == null");
        return list.get(0);
    }

    /**
     * Returns the last element in the specified list.
     * 
     * @param <E>  the type of elements in the list
     * @param list the specified list
     * @return the last element in the specified list
     */
    public static <E> E getLast(final List<? extends E> list) {
        checkNotNull(list, "list == null");
        return list.get(list.size() - 1);
    }

    /**
     * Adds the specified element to the beginning of the list.
     * 
     * @param <E>     the type of elements in the list
     * @param list    the list to modify
     * @param element the element to add
     */
    public static <E> void addFirst(final List<? super E> list, final E element) {
        checkNotNull(list, "list == null");
        list.add(0, element);
    }

    /**
     * Adds the specified element to the end of the list.
     * 
     * @param <E>     the type of elements in the list
     * @param list    the list to modify
     * @param element the element to add
     */
    public static <E> void addLast(final List<? super E> list, final E element) {
        checkNotNull(list, "list == null");
        list.add(element);
    }

    /**
     * Removes the first element in the specified list.
     * 
     * @param <E>  the type of elements in the list
     * @param list the list to modify
     * @return the removed element
     */
    public static <E> E removeFirst(final List<E> list) {
        checkNotNull(list, "list == null");
        return list.remove(0);
    }

    /**
     * Removes the last element in the specified list.
     * 
     * @param <E>  the type of elements in the list
     * @param list the list to modify
     * @return the removed element
     */
    public static <E> E removeLast(final List<E> list) {
        checkNotNull(list, "list == null");
        return list.remove(list.size() - 1);
    }

    /**
     * Returns a {@link List#listIterator(int) list iterator} over the elements in this list positioned at the end of the
     * list. An initial call to {@link ListIterator#previous previous} will return the last element of the list.
     * 
     * @param <E> the type of elements in the list
     * @return a {@link List#listIterator(int) list iterator} over the elements in this list positioned at the end of the
     *         list
     */
    public static <E> ListIterator<E> fromLast(final List<E> list) {
        checkNotNull(list, "list == null");
        return list.listIterator(list.size());
    }

}
