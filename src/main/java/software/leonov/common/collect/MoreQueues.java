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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.function.Predicate;

import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingQueue;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/**
 * More static utility methods which operate on or return {@link Queue}s.
 * 
 * @author Zhenya Leonov
 * @see Maps
 * @see MoreMaps
 * @see Collections
 * @see Collections2
 * @see Lists
 * @see MoreLists
 * @see Sets
 * @see Iterables
 * @see Iterators
 * @see Iteration
 * @see Queues
 * @see MoreQueues
 * @see Arrays
 * @see ObjectArrays
 * @see MoreArrays
 */
public class MoreQueues {

    private MoreQueues() {
    }

    /**
     * Attempts to insert the given elements into the specified queue.
     * <p>
     * When using a capacity-restricted queue some or all of the elements maybe rejected, thus this method is generally
     * preferable to {@link Collections#addAll(Collection, Object...) Collections.addAll(Queue, E...)} which can fail to
     * insert an element only by throwing an exception.
     * 
     * @param <E>      the element type
     * @param queue    the specified queue
     * @param elements the elements to insert into the queue
     * @return {@code true} if at least one elements was inserted into the queue, {@code false} otherwise
     * @throws ClassCastException       if the class of one or more elements prevents them from being inserted into this
     *                                  queue
     * @throws NullPointerException     if one or more elements is {@code null} and this queue does not permit null elements
     * @throws IllegalArgumentException if some property of one or more element prevents them from being inserted into this
     *                                  queue
     */
    @SafeVarargs
    public static <E> boolean offerAll(final Queue<? super E> queue, final E... elements) {
        checkNotNull(queue, "queue == null");
        checkNotNull(elements, "elements == null");
        boolean result = false;
        for (final E e : elements)
            result |= queue.offer(e);
        return result;
    }

    /**
     * Attempts to insert the given elements into the specified queue.
     * <p>
     * When using a capacity-restricted queue some or all of the elements maybe rejected, thus this method is generally
     * preferable to {@link Iterables#addAll(Collection, Iterable) Iterables.addAll(Queue, Iterable)} which can fail to
     * insert an element only by throwing an exception.
     * 
     * @param <E>      the element type
     * @param queue    the specified queue
     * @param elements the elements to insert into the queue
     * @return {@code true} if at least one elements was inserted into the queue, {@code false} otherwise
     * @throws ClassCastException       if the class of one or more elements prevents them from being inserted into this
     *                                  queue
     * @throws NullPointerException     if one or more elements is {@code null} and this queue does not permit null elements
     * @throws IllegalArgumentException if some property of one or more element prevents them from being inserted into this
     *                                  queue
     */
    public static <E> boolean offerAll(final Queue<? super E> queue, final Iterable<E> elements) {
        checkNotNull(elements, "elements == null");
        return offerAll(queue, elements.iterator());
    }

    /**
     * Attempts to insert the given elements into the specified queue
     * <p>
     * When using a capacity-restricted queue some or all of the elements maybe rejected, thus this method is generally
     * preferable to {@link Iterators#addAll(Collection, Iterator) Iterators.addAll(Queue, Iterator)} which can fail to
     * insert an element only by throwing an exception.
     * 
     * @param <E>      the element type
     * @param queue    the specified queue
     * @param elements the elements to insert into the queue
     * @return {@code true} if at least one elements was inserted into the queue, {@code false} otherwise
     * @throws ClassCastException       if the class of one or more elements prevents them from being inserted into this
     *                                  queue
     * @throws NullPointerException     if one or more elements is {@code null} and this queue does not permit null elements
     * @throws IllegalArgumentException if some property of one or more element prevents them from being inserted into this
     *                                  queue
     */
    public static <E> boolean offerAll(final Queue<? super E> queue, final Iterator<E> elements) {
        checkNotNull(queue, "queue == null");
        checkNotNull(elements, "elements == null");
        boolean result = false;
        while (elements.hasNext())
            result |= queue.offer(elements.next());
        return result;
    }

    /**
     * Attempts to remove all elements from the specified queue and adds them to the given collection, first by calling
     * {@link BlockingQueue#drainTo(Collection) drainTo(Collection)}, then, if the queue is still not empty because it is a
     * {@link DelayQueue} or another kind of queue for which {@link Queue#poll() poll()} or
     * {@link BlockingQueue#drainTo(Collection) drainTo(Collection)} may fail to remove some elements, this method iterates
     * through {@link Collection#toArray() queue.toArray()} and transfers the remaining elements one by one.
     * 
     * @param <E>        the element type
     * @param queue      the specified queue
     * @param collection the collection to transfer elements into
     * @return the number of elements transferred
     */
    @SuppressWarnings("unchecked")
    public static <E> int drainFully(final BlockingQueue<? extends E> queue, final Collection<? super E> collection) {
        checkNotNull(queue, "queue == null");
        checkNotNull(collection, "collection == null");
        int count = queue.drainTo(collection);
        if (!queue.isEmpty())
            for (final E r : (E[]) queue.toArray())
                if (queue.remove(r)) {
                    collection.add(r);
                    count++;
                }
        return count;
    }

    /**
     * Returns an unmodifiable (<i>read-only</i>) <b>view</b> of the specified queue. Query operations on the returned queue
     * "read through" to the specified queue, and attempts to modify the returned queue, whether direct or via its iterator,
     * will result in an {@code UnsupportedOperationException}.
     * 
     * @param <E>   the element type
     * @param queue the specified queue
     * @return an unmodifiable view of the specified queue
     */
    public static <E> Queue<E> unmodifiable(final Queue<E> queue) {
        return new UnmodifiableQueue<>(queue);
    }

//    /**
//     * Returns an unmodifiable (<i>read-only</i>) <b>view</b> of the specified blocking queue. Query operations on the
//     * returned queue "read through" to the specified queue, and attempts to modify the returned queue, whether direct or
//     * via its iterator, will result in an {@code UnsupportedOperationException}.
//     * 
//     * @param <E>   the type of elements in the queue
//     * @param queue the specified blocking queue
//     * @return an unmodifiable view of the specified queue
//     */
//    public static <E> BlockingQueue<E> unmodifiable(final BlockingQueue<E> queue) {
//        checkNotNull(queue, "queue == null");
//        return new UnmodifiableBlockingQueue<>(queue);
//    }

    // read operations: contains, containsAll, equals, hashCode, isEmpty, size, toArray, toArray, element
    // write operations: addAll, clear, remove, removeAll, removeIf, retainAll, add, offer, poll, remove
    // traversal/functional: iterator, parallelStream, spliterator, stream, forEach

    static class UnmodifiableQueue<E> extends ForwardingQueue<E> {

        private final Queue<E> queue;

        UnmodifiableQueue(final Queue<E> queue) {
            checkNotNull(queue, "queue == null");
            this.queue = queue;
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<E> iterator() {
            return Iterators.unmodifiableIterator(super.iterator());
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean offer(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E poll() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Queue<E> delegate() {
            return queue;
        }

    }

//    static class UnmodifiableBlockingQueue<E> extends UnmodifiableQueue<E> implements BlockingQueue<E> {
//
//        UnmodifiableBlockingQueue(final BlockingQueue<E> queue) {
//            super(queue);
//        }
//
//        @Override
//        protected BlockingQueue<E> delegate() {
//            return (BlockingQueue<E>) super.delegate();
//        }
//
//        @Override
//        public void put(E e) throws InterruptedException {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public E take() throws InterruptedException {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int remainingCapacity() {
//            return delegate().remainingCapacity();
//        }
//
//        @Override
//        public int drainTo(Collection<? super E> c) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int drainTo(Collection<? super E> c, int maxElements) {
//            throw new UnsupportedOperationException();
//        }
//
//    }

}
