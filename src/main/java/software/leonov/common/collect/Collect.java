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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingQueue;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

/**
 * Static utility methods for working with {@code Collection}s.
 * 
 * @author Zhenya Leonov
 * @see Collections
 * @see Collections2
 */
// See: https://bugs.openjdk.java.net/browse/JDK-6394757
final public class Collect {

    private Collect() {
    }

    /**
     * Attempts to remove all of {@code elements} from the specified collection, returning {@code true} if at least one
     * element was removed.
     * 
     * @param c        the collection
     * @param elements the elements to remove
     * @return {@code true} if at least one element was removed, {@code false} otherwise
     */
    public static boolean removeAll(final Collection<?> c, final Stream<?> elements) {
        checkNotNull(elements, "elements == null");
        return removeAll(c, elements.iterator());
    }

    /**
     * Attempts to remove all of {@code elements} from the specified collection, returning {@code true} if at least one
     * element was removed.
     * 
     * @param c        the collection
     * @param elements the elements to remove
     * @return {@code true} if at least one element was removed, {@code false} otherwise
     */
    public static boolean removeAll(final Collection<?> c, final Iterable<?> elements) {
        checkNotNull(elements, "elements == null");
        return removeAll(c, elements.iterator());
    }

    /**
     * Attempts to remove all of {@code elements} from the specified collection, returning {@code true} if at least one
     * element was removed.
     * 
     * @param c        the collection
     * @param elements the elements to remove
     * @return {@code true} if at least one element was removed, {@code false} otherwise
     */
    public static boolean removeAll(final Collection<?> c, final Iterator<?> elements) {
        checkNotNull(c, "c == null");
        checkNotNull(elements, "elements == null");

        boolean modified = false;

        while (elements.hasNext())
            try {
                modified |= c.remove(elements.next());
            } catch (final ClassCastException | NullPointerException e) {
            }

        return modified;
    }

    /**
     * Returns {@code true} if a collection contains all of the specified elements.
     * 
     * @param c        the collection
     * @param elements the specified elements
     * @return {@code true} if a collection contains all of the specified elements, {@code false} otherwise
     */
    public static boolean containsAll(final Collection<?> c, final Stream<?> elements) {
        checkNotNull(elements, "elements == null");
        return containsAll(c, elements.iterator());
    }

    /**
     * Returns {@code true} if a collection contains all of the specified elements.
     * 
     * @param c        the collection
     * @param elements the specified elements
     * @return {@code true} if a collection contains all of the specified elements, {@code false} otherwise
     */
    public static boolean containsAll(final Collection<?> c, final Iterable<?> elements) {
        checkNotNull(elements, "elements == null");
        return containsAll(c, elements.iterator());
    }

    /**
     * Returns {@code true} if a collection contains all of the specified elements.
     * <p>
     * <b>Guava equivalent:</b> {@code Iterators.all(Iterator, Predicates.in(Collection))}
     * 
     * @param c        the collection
     * @param elements the specified elements
     * @return {@code true} if a collection contains all of the specified elements, {@code false} otherwise
     */
    public static boolean containsAll(final Collection<?> c, final Iterator<?> elements) {
        checkNotNull(c, "c == null");
        checkNotNull(elements, "elements == null");

        try {
            while (elements.hasNext())
                if (!c.contains(elements.next()))
                    return false;
        } catch (final ClassCastException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Attempts to remove all elements from the specified queue and adds them to the given collection, first by calling
     * {@link BlockingQueue#drainTo(Collection) drainTo(Collection)}, then, if the queue is still not empty because it is a
     * {@link DelayQueue} or another kind of queue for which {@link Queue#poll() poll()} or
     * {@link BlockingQueue#drainTo(Collection) drainTo(Collection)} may fail to remove some elements, this method iterates
     * through {@link Collection#toArray() queue.toArray()} and transfers the remaining elements one by one.
     * 
     * @param queue      the specified queue
     * @param collection the collection to transfer elements into
     * @return the number of elements transferred
     */
    @SuppressWarnings("unchecked")
    public static <T> int drainFully(final BlockingQueue<? extends T> queue, final Collection<? super T> collection) {
        checkNotNull(queue, "queue == null");
        checkNotNull(collection, "collection == null");
        final List<T> tasks = new ArrayList<>();
        int count = queue.drainTo(tasks);
        if (!queue.isEmpty())
            for (final T r : (T[]) queue.toArray())
                if (queue.remove(r)) {
                    tasks.add(r);
                    count++;
                }
        return count;
    }

    /**
     * Returns an unmodifiable (<i>read-only</i>) <b>view</b> of the specified queue. Query operations on the returned queue
     * "read through" to the specified queue, and attempts to modify the returned queue, whether direct or via its iterator,
     * will result in an {@code UnsupportedOperationException}.
     * 
     * @param <E>   the type of elements in the queue
     * @param queue the specified queue
     * @return an unmodifiable view of the specified queue
     */
    public static <E> Queue<E> unmodifiable(final Queue<E> queue) {
        checkNotNull(queue, "queue == null");
        return new UnmodifiableQueue<>(queue);
    }

    /**
     * Returns an unmodifiable (<i>read-only</i>) <b>view</b> of the specified blocking queue. Query operations on the
     * returned queue "read through" to the specified queue, and attempts to modify the returned queue, whether direct or
     * via its iterator, will result in an {@code UnsupportedOperationException}.
     * 
     * @param <E>   the type of elements in the queue
     * @param queue the specified blocking queue
     * @return an unmodifiable view of the specified queue
     */
    public static <E> BlockingQueue<E> unmodifiable(final BlockingQueue<E> queue) {
        checkNotNull(queue, "queue == null");
        return new UnmodifiableBlockingQueue<>(queue);
    }

    /**
     * Returns a specialized {@link SetMultimap} implementation for use with {@code Enum} type keys. See {@link EnumMap} and
     * {@link LinkedHashSet} for the underlying implementation.
     * <p>
     * Attempts to insert a {@code null} keys will throw {@code NullPointerException}s. Attempts to remove or test for the
     * presence of a {@code null} key will succeed. Values may be {@code null}.
     * 
     * @param <K>  the type of {@code Enum} keys
     * @param <V>  the type of values
     * @param type the {@code Class} of the key type for this {@code Enum} {@code Multimap}
     * @return a specialized {@link SetMultimap} implementation for use with {@code Enum} type keys
     */
    public static <K extends Enum<K>, V> SetMultimap<K, V> newEnumSetMultimap(final Class<K> type) {
        checkNotNull(type, "type == null");
        final EnumMap<K, Collection<V>> map = new EnumMap<>(type);
        final SetMultimap<K, V> m = Multimaps.newSetMultimap(map, LinkedHashSet::new);
        return m;
    }

    /**
     * Returns a specialized {@link ListMultimap} implementation for use with {@code Enum} type keys. See {@link EnumMap}
     * and {@link ArrayList} for the underlying implementation.
     * <p>
     * Attempts to insert a {@code null} keys will throw {@code NullPointerException}s. Attempts to remove or test for the
     * presence of a {@code null} key will succeed. Values may be {@code null}.
     * 
     * @param <K>  the type of {@code Enum} keys
     * @param <V>  the type of values
     * @param type the {@code Class} of the key type for this {@code Enum} {@code Multimap}
     * @return a specialized {@link ListMultimap} implementation for use with {@code Enum} type keys
     */
    public static <K extends Enum<K>, V> ListMultimap<K, V> newEnumListMultimap(final Class<K> type) {
        checkNotNull(type, "type == null");
        final EnumMap<K, Collection<V>> map = new EnumMap<>(type);
        final ListMultimap<K, V> m = Multimaps.newListMultimap(map, ArrayList::new);
        return m;
    }

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

    static class UnmodifiableBlockingQueue<E> extends UnmodifiableQueue<E> implements BlockingQueue<E> {

        UnmodifiableBlockingQueue(final BlockingQueue<E> queue) {
            super(queue);
        }

        @Override
        protected BlockingQueue<E> delegate() {
            return (BlockingQueue<E>) super.delegate();
        }

        @Override
        public void put(E e) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public E take() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remainingCapacity() {
            return delegate().remainingCapacity();
        }

        @Override
        public int drainTo(Collection<? super E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int drainTo(Collection<? super E> c, int maxElements) {
            throw new UnsupportedOperationException();
        }

    }

}
