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
package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;
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
     * Returns the index of the first occurrence of the specified element in the given list, starting the search at
     * {@code fromIndex}, or -1 if there is no such element.
     * 
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
     * <b>Note:</b> If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs to avoid a {@code NullPointerException}.
     * 
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
     * <b>Note:</b> If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs as well to avoid a {@code NullPointerException}.
     * 
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
     * <b>Note:</b> If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs as well to avoid a {@code NullPointerException}.
     * 
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

}
