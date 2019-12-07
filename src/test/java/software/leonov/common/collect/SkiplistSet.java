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
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.SortedSet;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;

/**
 * <b>Note: This class should only be used for testing. It provides no practical benefit when compared to
 * {@link java.util.TreeSet java.util.TreeSet} and lacks navigation methods defined in the {@link NavigableSet}
 * interface.</b>
 * <p>
 * A {@code Set} implementation based on a modified <a href="http://en.wikipedia.org/wiki/Skip_list">skip list</a>.
 * Elements are sorted from <i>least</i> to <i>greatest</i> according to their <i>natural ordering</i>, or by an
 * explicit {@link Comparator} provided at creation. Attempting to remove or insert {@code null} elements is prohibited.
 * Querying for {@code null} elements is allowed. Inserting non-comparable elements will result in a
 * {@code ClassCastException}.
 * <p>
 * The iterators obtained from the {@link #iterator()} method are <i>fail-fast</i>. Attempts to modify the elements in
 * this list at any time after an iterator is created, in any way except through the iterator's own remove method, will
 * result in a {@code ConcurrentModificationException}.
 * <p>
 * This set is not <i>thread-safe</i>. If multiple threads modify this set concurrently it must be synchronized
 * externally.
 * <p>
 * This implementation uses a comparator (whether or not one is explicitly provided) to perform all element comparisons.
 * Two elements which are deemed equal by the comparator's {@code compare(E, E)} method are, from the standpoint of this
 * set, equal.
 * <p>
 * Invented by <a href="http://www.cs.umd.edu/~pugh/">Bill Pugh</a> in 1990, A skip list is a probabilistic data
 * structure for maintaining items in sorted order. Strictly speaking it is impossible to make any hard guarantees
 * regarding the worst-case performance of this class. Practical performance is <i>expected</i> to be logarithmic with
 * an extremely high degree of probability as the list grows.
 * <p>
 * The underlying array-based skip list provides the following expected case running time (where <i>n</i> is the size of
 * this set and <i>m</i> is the size of the specified collection which is iterable in linear time):
 * 
 * <pre>
 * <table border="1" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;">Method</th>
 *     <th style="text-align:center;">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #addAll(Collection) addAll(Collection)}<br/>{@link #containsAll(Collection) containsAll(Collection)}<br/>{@link #retainAll(Collection) retainAll(Collection)}<br/>{@link #removeAll(Collection) removeAll(Collection)}</td>
 *     <td style="text-align:center;"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #add(Object) add(E)}<br/>{@link #contains(Object)}<br/>{@link #remove(Object)}<br/>{@link Iterator#remove()}</td>
 *     <td style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #clear() clear()}<br/>{@link #isEmpty() isEmpty()}<br/>{@link #size()}</td>
 *     <td style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * </pre>
 * 
 * @author Zhenya Leonov
 * @param <E> the type of elements maintained by this list
 * @see Skiplist
 */
final public class SkiplistSet<E> extends AbstractSet<E> implements SortedCollection<E>, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private static final double P = .5;
    private static final int MAX_LEVEL = 32;
    transient int size = 0;
    private transient int level = 1;
    private transient Random random = new Random();
    private transient Node<E> head = new Node<E>(null, MAX_LEVEL);
    private final Comparator<? super E> comparator;
    private transient int[] index = new int[MAX_LEVEL];
    transient int modCount = 0;

    private SkiplistSet(final Comparator<? super E> comparator) {
        this.comparator = comparator;
        for (int i = 0; i < MAX_LEVEL; i++)
            head.next[i] = head;
    }

    /**
     * Creates a new {@code SkiplistSet} that orders its elements according to their <i>natural ordering</i>.
     * 
     * @return a new {@code SkiplistSet} that orders its elements according to their <i>natural ordering</i>
     */
    // @SuppressWarnings("rawtypes")
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <E extends Comparable<? super E>> SkiplistSet<E> create() {
        return new SkiplistSet<E>(Ordering.natural());
    }

    /**
     * Creates a new {@code SkiplistSet} that uses the specified comparator to order its elements.
     * 
     * @param comparator the specified comparator
     * @return a new {@code SkiplistSet} that uses the specified comparator to order its elements
     * 
     */
    public static <E> SkiplistSet<E> create(final Comparator<? super E> comparator) {
        return new SkiplistSet<E>(comparator);
    }

    /**
     * Creates a new {@code SkiplistSet} containing the specified initial elements. If {@code elements} is an instance of
     * {@link SortedSet}, {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or {@code SortedCollection}, this set will be
     * ordered according to the same ordering. Otherwise, this set will be ordered according to the <i>natural ordering</i>
     * of its elements.
     * 
     * @param elements the collection whose elements are to be placed into the set
     * @return a new {@code SkiplistSet} containing the elements of the specified collection
     * @throws ClassCastException   if elements of the specified collection cannot be compared to one another according to
     *                              this set's ordering
     * @throws NullPointerException if any of the elements of the specified collection or the collection itself is
     *                              {@code null}
     */
    @SuppressWarnings({ "unchecked" })
    public static <E> SkiplistSet<E> create(final Collection<? extends E> elements) {
        checkNotNull(elements, "elements == null");
        final Comparator<? super E> comparator;
        if (elements instanceof SortedSet<?>)
            comparator = ((SortedSet<? super E>) elements).comparator();
        else if (elements instanceof PriorityQueue<?>)
            comparator = ((PriorityQueue<? super E>) elements).comparator();
        else if (elements instanceof SortedCollection<?>)
            comparator = ((SortedCollection<? super E>) elements).comparator();
        else if (elements instanceof MinMaxPriorityQueue<?>)
            comparator = ((MinMaxPriorityQueue<? super E>) elements).comparator();
        else
            comparator = (Comparator<? super E>) Ordering.natural();
        final SkiplistSet<E> set = SkiplistSet.create(comparator);
        set.addAll(elements);
        return set;
    }

    /**
     * Returns the comparator used to order the elements in this list. If one was not explicitly provided a <i>natural
     * order</i> comparator is returned.
     * 
     * @return the comparator used to order this list
     */
    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    /**
     * Inserts the specified element into this list in sorted order.
     */
    @Override
    public boolean add(E e) {
        checkNotNull(e, "e == null");
        @SuppressWarnings("unchecked")
        final Node<E>[] update = new Node[MAX_LEVEL];
        final int newLevel = randomLevel();
        Node<E> x = head;
        Node<E> y = head;
        int i;
        int idx = 0;
        for (i = level - 1; i >= 0; i--) {
            while (x.next[i] != y && comparator.compare(x.next[i].element, e) < 0)
                x = x.next[i];
            y = x.next[i];
            update[i] = x;
            index[i] = idx;
        }
        if (newLevel > level) {
            for (i = level; i < newLevel; i++)
                update[i] = head;
            level = newLevel;
        }
        if (x.next().element != null && comparator.compare(x.next().element, e) == 0)
            return false;
        x = new Node<E>(e, newLevel);
        for (i = 0; i < level; i++) {
            if (i < newLevel) {
                x.next[i] = update[i].next[i];
                update[i].next[i] = x;
            }
        }
        modCount++;
        size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return o != null && search((E) o) != null;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> node = head.next();
            private Node<E> last = null;
            private int index = 0;
            private int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public E next() {
                checkForConcurrentModification();
                if (!hasNext())
                    throw new NoSuchElementException();
                index++;
                last = node;
                node = node.next();
                return last.element;
            }

            @Override
            public void remove() {
                checkForConcurrentModification();
                checkState(last != null);
                // System.out.println("removing " + last.element);
                SkiplistSet.this.remove(last);
                index--;
                expectedModCount = modCount;
                last = null;
            }

            void checkForConcurrentModification() {
                if (expectedModCount != modCount)
                    throw new ConcurrentModificationException();
            }
        };
    }

    private boolean remove(final Node<E> node) {
        @SuppressWarnings("unchecked")
        final Node<E>[] update = new Node[MAX_LEVEL];
        Node<E> curr = head;
        for (int i = level - 1; i >= 0; i--) {
            while (curr.next[i] != head && comparator.compare(curr.next[i].element, node.element) <= 0 && curr.next[i] != node)
                curr = curr.next[i];
            update[i] = curr;
        }
        curr = curr.next();
        delete(curr, update);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if (o == null)
            return false;
        final Node<E>[] update = new Node[MAX_LEVEL];
        final E element = (E) o;
        Node<E> curr = head;
        for (int i = level - 1; i >= 0; i--) {
            while (curr.next[i] != head && comparator.compare(curr.next[i].element, element) < 0)
                curr = curr.next[i];
            update[i] = curr;
        }
        curr = curr.next();
        if (curr == head || comparator.compare(curr.element, element) != 0)
            return false;
        delete(curr, update);
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < MAX_LEVEL; i++) {
            head.next[i] = head;
        }
        modCount++;
        size = 0;
    }

    /**
     * Returns a shallow copy of this {@code SkiplistSet}. The elements themselves are not cloned.
     * 
     * @return a shallow copy of this skip list
     */
    @SuppressWarnings("unchecked")
    @Override
    public SkiplistSet<E> clone() throws CloneNotSupportedException {
        SkiplistSet<E> clone;
        try {
            clone = (SkiplistSet<E>) super.clone();
        } catch (java.lang.CloneNotSupportedException e) {
            throw new InternalError();
        }
        for (int i = 0; i < MAX_LEVEL; i++) {
            clone.head.next[i] = clone.head;
        }
        clone.random = new Random();
        clone.level = 1;
        clone.modCount = 0;
        clone.size = 0;
        clone.addAll(this);
        return clone;
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        oos.defaultWriteObject();
        oos.writeInt(size);
        for (E e : this)
            oos.writeObject(e);
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
        head = new Node<E>(null, MAX_LEVEL);
        for (int i = 0; i < MAX_LEVEL; i++) {
            head.next[i] = head;
        }
        // update = new Node[MAX_LEVEL];
        index = new int[MAX_LEVEL];
        random = new Random();
        level = 1;
        int size = ois.readInt();
        for (int i = 0; i < size; i++)
            add((E) ois.readObject());
    }

    // skip list

    private static class Node<E> {
        private E element;
        private final Node<E>[] next;

        @SuppressWarnings("unchecked")
        private Node(final E element, final int size) {
            this.element = element;
            next = new Node[size];
        }

        private Node<E> next() {
            return next[0];
        }
    }

    private int randomLevel() {
        int randomLevel = 1;
        while (randomLevel < MAX_LEVEL - 1 && random.nextDouble() < P)
            randomLevel++;
        return randomLevel;
    }

    private void delete(final Node<E> node, final Node<E>[] update) {
        for (int i = 0; i < level; i++)
            if (update[i].next[i] == node) {
                update[i].next[i] = node.next[i];
            }
        while (head.next[level - 1] == head && level > 1)
            level--;
        modCount++;
        size--;
    }

    private Node<E> search(final E element) {
        Node<E> curr = head;
        for (int i = level - 1; i >= 0; i--)
            while (curr.next[i] != head && comparator.compare(curr.next[i].element, element) < 0)
                curr = curr.next[i];
        curr = curr.next();
        if (curr != head && comparator.compare(curr.element, element) == 0)
            return curr;
        return null;
    }

}