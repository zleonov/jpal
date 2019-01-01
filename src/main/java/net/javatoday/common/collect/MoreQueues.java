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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Queues;

/**
 * More static utility methods which operate on or return {@link Queue}s.
 * 
 * @author Zhenya Leonov
 * @see Queues
 */
public class MoreQueues {

    private MoreQueues() {
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
