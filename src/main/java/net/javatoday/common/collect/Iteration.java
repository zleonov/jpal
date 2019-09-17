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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingListIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.UnmodifiableListIterator;

/**
 * Static utility methods for working with {@code Iterable}s and {@code Iterator}s
 * 
 * @author Zhenya Leonov
 * @see Iterators
 * @see Iterables
 */
final public class Iteration {

    private static final Random RANDOM = new Random();

    private static final ListIterator<Object> EMPTY_LIST_ITERATOR = new ListIterator<Object>() {

        @Override
        public final void add(Object e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public Object previous() {
            throw new NoSuchElementException();
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public final void remove() {
            throw new IllegalStateException();
        }

        @Override
        public final void set(Object e) {
            throw new IllegalStateException();
        }
    };

    private static final PeekingIterator<Object> EMPTY_PEEKING_ITERATOR = new PeekingIterator<Object>() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public Object peek() {
            throw new NoSuchElementException();
        }

        @Override
        public final void remove() {
            throw new IllegalStateException();
        }
    };

    private Iteration() {
    }

//    /**
//     * Returns {@code true} if {@code left} and {@code right} contain the same elements.
//     * <p>
//     * If {@code ignoreOrder} is {@code true} the underlying implementation avoids quadratic running time by first checking
//     * if {@code left} and {@code right} are instances of {@code Set}s or {@code Multiset}s respectively, and if not,
//     * copying them into new {@code Multiset}s before comparing them.
//     * <p>
//     * If {@code ignoreOrder} is {@code false} this implementation delegates to
//     * {@link Iterables#elementsEqual(Iterable, Iterable)}.
//     * 
//     * @param left        an iterable
//     * @param right       an iterable
//     * @param ignoreOrder whether or not to ignore the order of the elements
//     * @return {@code true} if {@code left} and {@code right} contain the same elements
//     */
//    public static boolean elementsEqual(final Iterable<?> left, final Iterable<?> right, final boolean ignoreOrder) {
//        checkNotNull(left, "left == null");
//        checkNotNull(right, "right == null");
//
//        if (ignoreOrder) {
//            /*
//             * Does this check really speed things up? or is all the casting just making the average case slower
//             */
//            if (left instanceof Collection && right instanceof Collection && ((Collection<?>) left).size() != ((Collection<?>) right).size())
//                return false;
//
//            if (left instanceof Set && right instanceof Set || left instanceof Multiset && right instanceof Multiset)
//                return left.equals(right);
//            else
//                return HashMultiset.create(left).equals(HashMultiset.create(right));
//        } else
//            return Iterables.elementsEqual(left, right);
//    }

    /**
     * Returns an immutable empty {@code ListIterator}.
     * <p>
     * <b>Note:</b> Java 7+ users should prefer {@link Collections#emptyListIterator()}.
     * 
     * @return an immutable empty {@code ListIterator}
     */
    @SuppressWarnings("unchecked")
    public static <E> ListIterator<E> emptyListIterator() {
        return (ListIterator<E>) EMPTY_LIST_ITERATOR;
    }

    /**
     * Returns an immutable empty {@code PeekingIterator}.
     * 
     * @return an immutable empty {@code PeekingIterator}
     */
    @SuppressWarnings("unchecked")
    public static <E> PeekingIterator<E> emptyPeekingIterator() {
        return (PeekingIterator<E>) EMPTY_PEEKING_ITERATOR;
    }

    /**
     * A convenience method which adapts an iterator to the {@code Iterable} interface suitable for using in Java 5+
     * <i>for-each</i> loops.
     * <p>
     * <b>Warning</b>: Although not explicitly stated in the documentation it is generally assumed that an {@code Iterable}
     * object is able to produce multiple independent {@code Iterator}s. Thus the returned {@code Iterable} does not behave
     * like a general purpose {@code Iterable} because it supports only a single call to {@link Iterable#iterator()}.
     * Invoking the {@code iterator()} method to obtain subsequent iterators will result in an
     * {@code IllegalStateException}.
     * <p>
     * <b>Note:</b> Jave 8+ users should treat this method as deprecated. You can now create an {@code Iterable} from an
     * {@code Iterator} using Java's lambda facility, for example:
     * 
     * <pre>
     * for (final String s : (Iterable&ltString&gt)() -> iterator) {
     *    ...
     * }
     * </pre>
     * 
     * @param iterator the underlying iterator
     * @return an {@code Iterable} which returns the underlying iterator on the first call to {@link Iterable#iterator()}
     */
    public static <T> Iterable<T> forEach(final Iterator<T> iterator) {
        checkNotNull(iterator, "iterator == null");

        return new Iterable<T>() {
            private boolean first = true;

            @Override
            public Iterator<T> iterator() {
                if (!first)
                    throw new IllegalStateException();
                first = false;
                return iterator;
            }

            @Override
            public String toString() {
                return Iterators.toString(iterator);
            }
        };
    }

    /**
     * Returns a random sample of {@code k} elements from the given {@code Iterable} using
     * <a href="https://en.wikipedia.org/wiki/Reservoir_sampling" target="_blank">reservoir sampling</a>.
     * <p>
     * If {@code k} exceeds the size of the given iterable the returned list will contain all of its elements.
     * 
     * @param population the given {@code Iterable}
     * @param k          the number of random items to chose
     * @return a random sample of {@code k} elements from the given {@code Iterable}
     */
    public static <T> List<T> sample(final Iterable<? extends T> population, final int k) {
        return sample(population, k, RANDOM);
    }

    /**
     * Returns a random sample of {@code k} elements from the given {@code Iterable} using
     * <a href="https://en.wikipedia.org/wiki/Reservoir_sampling" target="_blank">reservoir sampling</a>.
     * <p>
     * If {@code k} exceeds the size of the given iterable the returned list will contain all of its elements.
     * 
     * @param population the given {@code Iterable}
     * @param k          the number of random items to chose
     * @param random     the {@code Random} number generator
     * @return a random sample of {@code k} elements from the given {@code Iterable}
     */
    public static <T> List<T> sample(final Iterable<? extends T> population, final int k, final Random random) {
        checkNotNull(population, "population == null");
        return sample(population.iterator(), k, random);
    }

    /**
     * Returns a random sample of {@code k} elements from the given {@code Iterator} using
     * <a href="https://en.wikipedia.org/wiki/Reservoir_sampling" target="_blank">reservoir sampling</a>.
     * <p>
     * If {@code k} exceeds the size of the given iterator the returned list will contain all of its elements.
     * 
     * @param population the given {@code Iterator}
     * @param k          the number of random items to chose
     * @return a random sample of {@code k} elements from the given {@code Iterator}
     */
    public static <T> List<T> sample(final Iterator<? extends T> population, final int k) {
        return sample(population, k, RANDOM);
    }

    /**
     * Returns a random sample of {@code k} elements from the given {@code Iterator} using
     * <a href="https://en.wikipedia.org/wiki/Reservoir_sampling" target="_blank">reservoir sampling</a>.
     * <p>
     * If {@code k} exceeds the size of the given iterator the returned list will contain all of its elements.
     * 
     * 
     * @param population the given {@code Iterator}
     * @param k          the number of random items to chose
     * @param random     the {@code Random} number generator
     * @return a random sample of {@code k} elements from the given {@code Iterator}
     */
    public static <T> List<T> sample(final Iterator<? extends T> population, final int k, final Random random) {
        checkNotNull(population, "population == null");
        checkNotNull(random, "random == null");
        checkArgument(k > 0, "k < 1");

        final List<T> items = Lists.newArrayListWithCapacity(k);
        int count = 0;

        while (population.hasNext()) {
            final T item = population.next();

            count++;
            if (count <= k)
                items.add(item);
            else {
                int r = random.nextInt(count);
                if (r < k)
                    items.set(r, item);
            }
        }

        return items;
    }

    /**
     * Returns a {@code ListIterator} that applies the specified {@code Function} to each element of the given list
     * iterator.
     * <p>
     * The returned list iterator does not support {@code add(E)} and {@code set(E)} but does support {@code remove()} if
     * the given list iterator does.
     * 
     * @param          <E> the type of elements traversed by the given list iterator
     * @param          <T> the type of output of the specified function
     * @param from     the given list iterator
     * @param function the specified function
     * @return a list iterator that applies {@code function} to each element of {@code fromIterator}
     */
    public static <E, T> ListIterator<T> transform(final ListIterator<E> from, final Function<? super E, ? extends T> function) {
        checkNotNull(from, "from == null");
        checkNotNull(function, "function == null");

        return new ForwardingListIterator<T>() {
            @Override
            public void add(T e) {
                throw new UnsupportedOperationException();
            }

            @SuppressWarnings("unchecked")
            @Override
            // ok since type T is only used in next, previous, add, and set
            protected ListIterator<T> delegate() {
                return (ListIterator<T>) from;
            }

            @Override
            public T next() {
                return function.apply(from.next());
            }

            @Override
            public T previous() {
                return function.apply(from.previous());
            }

            @Override
            public void set(T e) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Decorates the specified {@code ListIterator} to prevent {@link ListIterator#remove() remove()},
     * {@link ListIterator#add(Object) add(E)}, and {@link ListIterator#set(Object) set(E)}.
     * 
     * @param listIterator the underlying {@code ListIterator}
     * @return an unmodifiable view of {@code listIterator}
     */
    public static <E> UnmodifiableListIterator<E> unmodifiable(final ListIterator<? extends E> listIterator) {
        checkNotNull(listIterator, "listIterator == null");

        return new UnmodifiableListIterator<E>() {
            @Override
            public boolean hasNext() {
                return listIterator.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return listIterator.hasPrevious();
            }

            @Override
            public E next() {
                return listIterator.next();
            }

            @Override
            public int nextIndex() {
                return listIterator.nextIndex();
            }

            @Override
            public E previous() {
                return listIterator.previous();
            }

            @Override
            public int previousIndex() {
                return listIterator.previousIndex();
            }
        };
    }

    /**
     * Decorates the specified {@code PeekingIterator} to prevent {@link PeekingIterator#remove() remove()}.
     * 
     * @param peekingIterator the underlying {@code PeekingIterator}
     * @return an unmodifiable view of {@code peekingIterator}
     */
    public static <E> UnmodifiablePeekingIterator<E> unmodifiable(final PeekingIterator<? extends E> peekingIterator) {
        checkNotNull(peekingIterator, "peekingIterator == null");

        return new UnmodifiablePeekingIterator<E>() {

            @Override
            public boolean hasNext() {
                return peekingIterator.hasNext();
            }

            @Override
            public E next() {
                return peekingIterator.next();
            }

            @Override
            public E peek() {
                return peekingIterator.peek();
            }
        };
    }

}
