/*
 * Copyright (C) 2018 Zhenya Leonov
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.function.Predicate;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * Static utility methods for working with {@code Collection}s.
 * 
 * @author Zhenya Leonov
 * @see Collections
 * @see Collections2
 */
final public class Collect {

    private Collect() {
    }

    /**
     * Returns {@code true} if a collection contains all of the specified elements.
     * 
     * @param c        the collection
     * @param elements the specified elements
     * @return {@code true} if a collection contains all of the specified elements, {@code false otherwise}
     */
    public static boolean containsAll(final Collection<?> c, final Iterable<?> elements) {
        checkNotNull(c, "c == null");
        checkNotNull(elements, "elements == null");
        return elements instanceof Collection ? c.containsAll((Collection<?>) elements) : containsAll(c, elements.iterator());
    }

    /**
     * Returns {@code true} if a collection contains all of the specified elements.
     * <p>
     * Guava equivalent: {@code Iterators.all(Iterator, Predicates.in(Collection))}
     * 
     * @param c        the collection
     * @param elements the specified elements
     * @return {@code true} if a collection contains all of the specified elements, {@code false otherwise}
     */
    public static boolean containsAll(final Collection<?> c, final Iterator<?> elements) {
        checkNotNull(c, "c == null");
        checkNotNull(elements, "elements == null");

        try {
            while (elements.hasNext())
                if (!c.contains(elements.next()))
                    return false;
        } catch (final ClassCastException e) {
            return false;
        } catch (final NullPointerException e) {
            return false;
        }
        return true;
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
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
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
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
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
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
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

    /**
     * Attempts to insert the given elements into the specified queue. When using a capacity-restricted queue, some or all
     * of the elements maybe rejected.
     * <p>
     * This method is generally preferable to {@link Collections#addAll(Collection, Object...) Collections.addAll(Queue,
     * E...)}, which can fail to insert an element only by throwing an exception.
     * 
     * @param queue    the specified queue
     * @param elements the elements to insert into the queue
     * @return {@code true} if at least one elements was inserted into the queue; {@code false} otherwise
     * @throws ClassCastException       if the class of one or more elements prevents them from being inserted into this
     *                                  queue
     * @throws NullPointerException     if one or more elements is {@code null} and this queue does not permit null elements
     * @throws IllegalArgumentException if some property of one or more element prevents them from being inserted into this
     *                                  queue
     */
    @SafeVarargs
    public static <E> boolean offerAll(Queue<? super E> queue, E... elements) {
        checkNotNull(queue, "queue == null");
        checkNotNull(elements, "elements == null");
        boolean result = false;
        for (final E e : elements)
            result |= queue.offer(e);
        return result;
    }

    /**
     * Attempts to insert the given elements into the specified queue. When using a capacity-restricted queue, some or all
     * of the elements maybe rejected.
     * <p>
     * This method is generally preferable to {@link Iterables#addAll(Collection, Iterable) Iterables.addAll(Queue,
     * Iterable)}, which can fail to insert an element only by throwing an exception.
     * 
     * @param queue    the specified queue
     * @param elements the elements to insert into the queue
     * @return {@code true} if at least one elements was inserted into the queue; {@code false} otherwise
     * @throws ClassCastException       if the class of one or more elements prevents them from being inserted into this
     *                                  queue
     * @throws NullPointerException     if one or more elements is {@code null} and this queue does not permit null elements
     * @throws IllegalArgumentException if some property of one or more element prevents them from being inserted into this
     *                                  queue
     */
    public static <E> boolean offerAll(Queue<? super E> queue, Iterable<E> elements) {
        checkNotNull(elements, "elements == null");
        return offerAll(queue, elements.iterator());
    }

    /**
     * Attempts to insert the given elements into the specified queue. When using a capacity-restricted queue, some or all
     * of the elements maybe rejected.
     * <p>
     * This method is generally preferable to {@link Iterators#addAll(Collection, Iterator) Iterators.addAll(Queue,
     * Iterator)}, which can fail to insert an element only by throwing an exception.
     * 
     * @param queue    the specified queue
     * @param elements the elements to insert into the queue
     * @return {@code true} if at least one elements was inserted into the queue; {@code false} otherwise
     * @throws ClassCastException       if the class of one or more elements prevents them from being inserted into this
     *                                  queue
     * @throws NullPointerException     if one or more elements is {@code null} and this queue does not permit null elements
     * @throws IllegalArgumentException if some property of one or more element prevents them from being inserted into this
     *                                  queue
     */
    public static <E> boolean offerAll(Queue<? super E> queue, Iterator<E> elements) {
        checkNotNull(queue, "queue == null");
        checkNotNull(elements, "elements == null");
        boolean result = false;
        while (elements.hasNext())
            result |= queue.offer(elements.next());
        return result;
    }

}
