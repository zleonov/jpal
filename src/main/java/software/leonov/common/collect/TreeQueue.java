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
import static software.leonov.common.collect.TreeQueue.Color.BLACK;
import static software.leonov.common.collect.TreeQueue.Color.RED;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedSet;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;

import software.leonov.common.base.Compare;

/**
 * An optionally bounded priority {@link Queue} based on a modified
 * <a target="_blank" href="http://en.wikipedia.org/wiki/Red-black_tree">Red-Black Tree</a>. The elements of this queue
 * are sorted according to their <i>natural ordering</i>, or by an explicit {@link Comparator} provided at creation.
 * Attempting to insert {@code null} elements will succeed if the {@code Comparator} supports {@code null} values.
 * Inserting non-comparable elements will result in a {@code ClassCastException}. This queue uses the same general
 * ordering rules as a {@link PriorityQueue PriorityQueue}. The first element (the head) of this queue is considered to
 * be the <i>least</i> element with respect to the specified ordering. A comparator is used ({@link Ordering#natural()
 * whether or not one is explicitly provided}) to perform all element comparisons. Two elements which are deemed equal
 * by the comparator's {@code compare(E, E)} method have equal priority from the standpoint of this queue. Elements with
 * equal priority are sorted according to their insertion order.
 * <p>
 * Besides the regular {@link #peek() peek()}, {@link #poll() poll()}, {@link #remove() remove()} operations specified
 * in the {@code Queue} interface, this implementation provides additional {@link #peekLast() peekLast()},
 * {@link #pollLast() pollLast()}, {@link #removeLast() removeLast()} methods to examine the elements at the tail of the
 * queue.
 * <p>
 * If this queue is bounded and becomes full the {@code offer(E)} method behaves according to the following policy: if
 * the element to be added has higher priority than the lowest priority element currently in the queue, the new element
 * is added and the lowest priority element is removed; else the new element is rejected. The {@code add(E)} and
 * {@code addAll(Collection)} operations will throw an {@code IllegalStateException} when the queue is full and a new
 * element is rejected; as required by the contract of {@link Queue#add Queue.add(E)}.
 * <p>
 * If the maximum size of this queue is not specified it will have an intrinsic capacity of {@code Integer.MAX_VALUE}
 * elements.
 * <p>
 * Bounded priority queues are useful when implementing <i>n-best</i> algorithms (e.g. finding the <i>best</i> <i>n</i>
 * elements in an arbitrary collection).
 * <p>
 * The {@link #iterator() iterator()} and {@link #descendingIterator()} methods return <i>fail-fast</i> iterators which
 * are guaranteed to traverse the elements of the queue in ascending and descending priority order, respectively.
 * Attempts to modify the elements in this queue at any time after an iterator is created, in any way except through the
 * iterator's own remove method, will result in a {@code ConcurrentModificationException}.
 * <p>
 * This queue is not <i>thread-safe</i>. If multiple threads modify this queue concurrently it must be synchronized
 * externally.
 * <p>
 * The underlying Red-Black Tree provides the following running time compared to a {@link PriorityQueue PriorityQueue}
 * (where <i>n</i> is the size of this queue and <i>m</i> is the size of the specified collection which is iterable in
 * linear time):
 * 
 * <pre>
 * <table border="1" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;" rowspan="2">Method</th>
 *     <th style="text-align:center;" colspan="2">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td style="text-align:center;"><b>TreeQueue</b><br>(<i>amortized</i>)</td>
 *     <td style="text-align:center;"><b>PriorityQueue</b><br>(<i>worst-case</i>)</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #addAll(Collection) addAll(Collection)}<br>{@link #containsAll(Collection) containsAll(Collection)}<br>{@link #retainAll(Collection) retainAll(Collection)}<br>{@link #removeAll(Collection) removeAll(Collection)}</td>
 *     <td colspan="2" style="text-align:center;"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #add(Object) add(E)}<br>{@link #offer(Object) offer(E)}<br>{@link #remove(Object)}</td>
 *     <td colspan="2" style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #contains(Object)}</td>
 *     <td bgcolor="FFCC99" style="text-align:center;"><i>O(log n)</i></td>
 *     <td bgcolor="FFCCCC" rowspan="2" style="text-align:center;"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #clear()}</td>
 *     <td bgcolor="FFCC99" rowspan="2" style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #poll()}<br>{@link #remove() remove()}<br></td>
 *     <td bgcolor="FFCCCC" style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #element() element()}<br>{@link #isEmpty() isEmpty()}<br>{@link #peek()}<br>{@link #size()}</td>
 *     <td colspan="2" style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 *   <tr>
 *     <td>{@link #peekLast() peekLast()}<br>{@link #pollLast() pollLast()}<br>{@link #removeLast() removeLast()}</td>
 *     <td style="text-align:center;"><i>O(1)</i></td>
 *     <td style="text-align:center;">&nbsp</td>
 *   </tr>
 * </table>
 * </pre>
 * 
 * @author Zhenya Leonov
 * @param <E> the type of elements held in this queue
 */
final public class TreeQueue<E> extends AbstractQueue<E> implements SortedCollection<E>, BoundedQueue<E>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    private transient int size = 0;
    private transient Node nil = new Node();
    private transient Node min = nil;
    private transient Node max = nil;
    private transient Node root = nil;
    private transient int modCount = 0;
    private final Comparator<? super E> comparator;
    private final int capacity;

    private TreeQueue(final int capacity, final Comparator<? super E> comparator) {
        this.capacity = capacity;
        this.comparator = comparator;
    }

    /**
     * Creates a new unbounded {@code TreeQueue} that orders its elements according to their <i>natural ordering</i>.
     * 
     * @return a new unbounded {@code TreeQueue} that orders its elements according to their <i>natural ordering</i>
     */
    // @SuppressWarnings("rawtypes")
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <E extends Comparable<? super E>> TreeQueue<E> create() {
        return orderedBy(Ordering.natural()).create();
    }

    /**
     * Creates a new unbounded {@code TreeQueue} containing the specified initial elements. If {@code elements} is an
     * instance of {@link SortedSet} , {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or {@code SortedCollection} this
     * queue will be ordered according to the same ordering. Otherwise, this queue will be ordered according to the
     * <i>natural ordering</i> of its elements.
     * 
     * @param elements the collection whose elements are to be placed into the queue
     * @return a new unbounded {@code TreeQueue} containing the elements of the specified collection
     * @throws ClassCastException   if elements of the specified collection cannot be compared to one another according to
     *                              this queue's ordering
     * @throws NullPointerException if any of the elements of the specified collection or the collection itself is
     *                              {@code null}
     */
    @SuppressWarnings({ "unchecked" })
    public static <E> TreeQueue<E> create(final Iterable<? extends E> elements) {
        checkNotNull(elements, "elements == null");

        Comparator<? super E> comparator = null;
        if (elements instanceof SortedSet<?>)
            comparator = ((SortedSet<? super E>) elements).comparator();
        else if (elements instanceof PriorityQueue<?>)
            comparator = ((PriorityQueue<? super E>) elements).comparator();
        else if (elements instanceof SortedCollection<?>)
            comparator = ((SortedCollection<? super E>) elements).comparator();
        else if (elements instanceof MinMaxPriorityQueue<?>)
            comparator = ((MinMaxPriorityQueue<? super E>) elements).comparator();

        if (comparator == null)
            comparator = (Comparator<? super E>) Ordering.natural();

        return orderedBy(comparator).create(elements);
    }

    /**
     * Returns a new builder configured to build {@code TreeQueue} instances that use the specified comparator for ordering.
     * 
     * @param comparator the specified comparator
     * @return a new builder configured to build {@code TreeQueue} instances that use the specified comparator for ordering
     */
    public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
        checkNotNull(comparator, "comparator == null");
        return new Builder<B>(comparator);
    }

    /**
     * Returns a new builder configured to build {@code TreeQueue} instances that are limited to the specified maximum
     * number of elements.
     * 
     * @param maximumSize the maximum number of elements which can be placed in this queue
     * @return a new builder configured to build {@code TreeQueue} instances that are limited to the specified maximum
     *         number of elements
     */
    public static Builder<Comparable<?>> maximumSize(final int maximumSize) {
        checkState(maximumSize > 0, "maximumSize < 1");
        return new Builder<Comparable<?>>(Ordering.natural()).maximumSize(maximumSize);
    }

    /**
     * A builder for the creation of {@code TreeQueue} instances. Instances of this builder are obtained calling
     * {@link TreeQueue#orderedBy(Comparator)} and {@link TreeQueue#maximumSize(int)}.
     * 
     * @author Zhenya Leonov
     * @param <B> the upper bound of the type of queues this builder can produce (for example a {@code Builder<Number>} can
     *            produce a {@code TreeQueue<Float>} or a {@code TreeQueue<Integer>}
     */
    public static final class Builder<B> {

        private final Comparator<B> comparator;
        private int maximumSize = Integer.MAX_VALUE;

        private Builder(final Comparator<B> comparator) {
            this.comparator = comparator;
        }

        /**
         * Configures this builder to build {@code TreeQueue} instances that are limited to the specified maximum number of
         * elements.
         * 
         * @param maximumSize the maximum number of elements which can be placed in this queue
         * @return this builder
         */
        public Builder<B> maximumSize(final int maximumSize) {
            checkState(maximumSize > 0, "maximumSize < 1");
            this.maximumSize = maximumSize;
            return this;
        }

        /**
         * Builds an empty {@code TreeQueue} using the previously specified options.
         * 
         * @return an empty {@code TreeQueue} using the previously specified options
         */
        public <T extends B> TreeQueue<T> create() {
            return new TreeQueue<T>(maximumSize, comparator);
        }

        /**
         * Builds a new {@code TreeQueue} using the previously specified options, and having the given initial elements.
         * 
         * @param elements the initial elements to be placed in this queue
         * @return a new {@code TreeQueue} using the previously specified options, and having the given initial elements
         */
        public <T extends B> TreeQueue<T> create(final Iterable<? extends T> elements) {
            checkNotNull(elements, "elements == null");
            final TreeQueue<T> queue = new TreeQueue<T>(maximumSize, comparator);
            for (final T element : elements)
                queue.offer(element);
            return queue;
        }
    }

    /**
     * Removes all of the elements from this queue. The queue will be empty after this call returns.
     */
    @Override
    public void clear() {
        modCount++;
        root = nil;
        min = nil;
        max = nil;
        size = 0;
    }

    /**
     * Returns the comparator used to order the elements in this queue. If one was not explicitly provided a <i>natural
     * order</i> comparator is returned.
     * 
     * @return the comparator used to order this queue
     */
    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public boolean remove(Object o) {
//        if (o == null)
//            return false;
        @SuppressWarnings("unchecked")
        final Node node = search((E) o);
        if (node == null)
            return false;
        delete(node);
        return true;
    }

    /**
     * Retrieves and removes the last element of this queue. This method differs from {@link #pollLast pollLast()} only in
     * that it throws an exception if this queue is empty.
     * 
     * @return the last element of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public E removeLast() {
        final E e = pollLast();
        if (e != null)
            return e;
        else
            throw new NoSuchElementException();
    }

    /**
     * Retrieves and removes the last element of this queue, or returns {@code null} if this queue is empty.
     * 
     * @return the last element of this queue, or {@code null} if this queue is empty
     */
    public E pollLast() {
        if (isEmpty())
            return null;
        final E e = max.element;
        delete(max);
        return e;
    }

    /**
     * Retrieves, but does not remove, the last element of this queue, or returns {@code null} if this queue is empty.
     * 
     * @return the last element of this queue, or {@code null} if this queue is empty
     */
    public E peekLast() {
        if (isEmpty())
            return null;
        return max.element;
    }

    @Override
    public boolean offer(E e) {
//        checkNotNull(e, "e == null");
        if (size() == capacity)
            if (comparator().compare(e, peekLast()) < 0)
                pollLast();
            else
                return false;
        final Node newNode = new Node(e);
        insert(newNode);
        return true;
    }

    @Override
    public E poll() {
        if (isEmpty())
            return null;
        final E e = min.element;
        delete(min);
        return e;
    }

    @Override
    public E peek() {
        if (isEmpty())
            return null;
        return min.element;
    }

    /**
     * Returns the number of elements in this queue.
     * 
     * @return the number of elements in this queue
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public int remainingCapacity() {
        return capacity - size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
//        return o != null && search((E) o) != null;
        return search((E) o) != null;
    }

    /**
     * Returns an iterator over the elements of this queue in priority order from first (head) to last (tail).
     * 
     * @return an iterator over the elements of this queue in priority order
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node next = min;
            private Node last = nil;
            private int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return next != nil;
            }

            @Override
            public void remove() {
                checkForConcurrentModification();
                if (last == nil)
                    throw new IllegalStateException();
                if (last.left != nil && last.right != nil)
                    next = last;
                delete(last);
                expectedModCount = modCount;
                last = nil;
            }

            @Override
            public E next() {
                checkForConcurrentModification();
                if (next == nil)
                    throw new NoSuchElementException();
                last = next;
                next = successor(next);
                return last.element;
            }

            private void checkForConcurrentModification() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        };
    }

    /**
     * Returns an iterator over the elements of this queue in reverse order from last (tail) to first (head).
     * 
     * @return an iterator over the elements of this queue in reverse order
     */
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private Node next = max;
            private Node last = nil;
            private int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return next != nil;
            }

            @Override
            public void remove() {
                checkForConcurrentModification();
                checkState(last != nil);
                if (last.left != nil && last.right != nil)
                    next = last;
                delete(last);
                expectedModCount = modCount;
                last = nil;
            }

            @Override
            public E next() {
                checkForConcurrentModification();
                if (next == nil)
                    throw new NoSuchElementException();
                last = next;
                next = predecessor(next);
                return last.element;
            }

            private void checkForConcurrentModification() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        };
    }

    /**
     * Returns a shallow copy of this {@code TreeQueue}. The elements themselves are not cloned.
     * 
     * @return a shallow copy of this queue
     */
    @SuppressWarnings("unchecked")
    @Override
    public TreeQueue<E> clone() {
        TreeQueue<E> clone;
        try {
            clone = (TreeQueue<E>) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new InternalError();
        }
        clone.nil = new Node();
        clone.modCount = 0;
        clone.root = clone.nil;
        clone.min = clone.nil;
        clone.max = clone.nil;
        clone.size = 0;
        clone.addAll(this);
        return clone;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeInt(size);
        for (E e : this)
            oos.writeObject(e);
    }

    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        nil = new Node();
        root = nil;
        max = nil;
        min = nil;
        int size = ois.readInt();
        for (int i = 0; i < size; i++)
            add((E) ois.readObject());
    }

    /*
     * Red-Black Tree
     */

    static enum Color {
        BLACK, RED;
    }

    private class Node {
        private E element = null;
        private Node parent, left, right;
        private Color color = BLACK;

        private Node() {
            parent = this;
            right = this;
            left = this;
        }

        private Node(final E element) {
            this.element = element;
            parent = nil;
            right = nil;
            left = nil;
        }
    }

    /**
     * Introduction to Algorithms (CLR) Second Edition
     * 
     * <pre>
     * RB-INSERT(T, z)
     * y = nil[T]
     * x = root[T]
     * while x != nil[T]
     *    do y = x
     *       if key[z] < key[x]
     *          then x = left[x]
     *          else x = right[x]
     * p[z] = y
     * if y = nil[T]
     *    then root[T] = z
     *    else if key[z] < key[y]
     *            then left[y] = z
     *            else right[y] = z
     * left[z] = nil[T]
     * right[z] = nil[T]
     * color[z] = RED
     * RB-INSERT-FIXUP(T, z)
     * </pre>
     */
    private boolean insert(final Node z) {
        modCount++;
        Node x = root;
        Node y = nil;
        while (x != nil) {
            y = x;
            int cmp = comparator.compare(z.element, x.element);
            if (cmp == 0)
                return false;
            else if (cmp < 0)
                x = x.left;
            else
                x = x.right;
        }
        z.parent = y;
        if (y == nil)
            root = z;
        else if (comparator.compare(z.element, y.element) < 0)
            y.left = z;
        else
            y.right = z;
        fixAfterInsertion(z);
        if (min == nil || comparator.compare(z.element, min.element) < 0)
            min = z;
        size++;
        return true;
    }

    /**
     * Introduction to Algorithms (CLR) Second Edition
     * 
     * <pre>
     * RB-DELETE-FIXUP(T, z)
     * if left[z] = nil[T] or right[z] = nil[T]
     *    then y = z
     *    else y = TREE-SUCCESSOR(z)
     * if left[y] != nil[T]
     *    then x = left[y]
     *    else x = right[y]
     * p[x] = p[y]
     * if p[y] = nil[T]
     *    then root[T] = x
     *    else if y = left[p[y]]
     *            then left[p[y]] = x
     *            else right[p[y]] = x
     * if y != z
     *    then key[z] = key[y]
     *         copy y's satellite data into z
     * if color[y] = BLACK
     *    then RB-DELETE-FIXUP(T, x)
     * return y
     * </pre>
     */
    private void delete(Node z) {
        size--;
        modCount++;
        Node x, y;
        if (min == z)
            min = successor(z);
        if (z.left == nil || z.right == nil)
            y = z;
        else
            y = successor(z);
        if (y.left != nil)
            x = y.left;
        else
            x = y.right;
        x.parent = y.parent;
        if (y.parent == nil)
            root = x;
        else if (y == y.parent.left)
            y.parent.left = x;
        else
            y.parent.right = x;
        if (y != z)
            z.element = y.element;
        if (y.color == Color.BLACK)
            fixAfterDeletion(x);
    }

    private Node search(final E e) {
        Node n = root;
        while (n != nil) {
            int cmp = comparator.compare(e, n.element);
            if (cmp == 0)
                return n;
            if (cmp < 0)
                n = n.left;
            else
                n = n.right;
        }
        return null;
    }

    /**
     * Introduction to Algorithms (CLR) Second Edition
     * 
     * <pre>
     * TREE-SUCCESSOR(x)
     * if right[x] != NIL
     *    then return TREE-MINIMUM(right[x])
     * y = p[x]
     * while y != NIL and x = right[y]
     *    do x = y
     *       y = p[y]
     * return y
     * </pre>
     */
    private Node successor(Node x) {
        if (x == nil)
            return nil;
        if (x.right != nil) {
            Node y = x.right;
            while (y.left != nil)
                y = y.left;
            return y;
        }
        Node y = x.parent;
        while (y != nil && x == y.right) {
            x = y;
            y = y.parent;
        }
        return y;
    }

    private Node predecessor(Node x) {
        if (x == nil)
            return nil;
        if (x.left != nil) {
            Node y = x.left;
            while (y.right != nil)
                y = y.right;
            return y;
        }
        Node y = x.parent;
        while (y != nil && x == y.left) {
            x = y;
            y = y.left;
        }
        return y;
    }

    /**
     * Introduction to Algorithms (CLR) Second Edition
     * 
     * <pre>
     * LEFT-ROTATE(T, x)
     * y = right[x]                         Set y.
     * right[x] = left[y]                   Turn y's left subtree into x's right subtree.
     * if left[y] != nil[T]
     *    then p[left[y]] = x
     * p[y] = p[x]                          Link x's parent to y.
     * if p[x] = nil[T]
     *    then root[T] = y
     *    else if x = left[p[x]]
     *            then left[p[x]] = y
     *            else right[p[x]] = y
     * left[y] = x                          Put x on y's left.
     * p[x] = y
     * </pre>
     */
    private void leftRotate(final Node x) {
        if (x != nil) {
            Node n = x.right;
            x.right = n.left;
            if (n.left != nil)
                n.left.parent = x;
            n.parent = x.parent;
            if (x.parent == nil)
                root = n;
            else if (x.parent.left == x)
                x.parent.left = n;
            else
                x.parent.right = n;
            n.left = x;
            x.parent = n;
        }
    }

    private void rightRotate(final Node x) {
        if (x != nil) {
            Node n = x.left;
            x.left = n.right;
            if (n.right != nil)
                n.right.parent = x;
            n.parent = x.parent;
            if (x.parent == nil)
                root = n;
            else if (x.parent.right == x)
                x.parent.right = n;
            else
                x.parent.left = n;
            n.right = x;
            x.parent = n;
        }
    }

    /**
     * Introduction to Algorithms (CLR) Second Edition
     * 
     * <pre>
     * RB-INSERT-FIXUP(T, z)
     * while color[p[z]] = RED
     *    do if p[z] = left[p[p[z]]]
     *          then y = right[p[p[z]]]
     *               if color[y] = RED
     *                  then color[p[z]] = BLACK                    Case 1
     *                       color[y] = BLACK                       Case 1 
     *                       color[p[p[z]]] = RED                   Case 1
     *                       z = p[p[z]]                            Case 1
     *                  else if z = right[p[z]]
     *                          then z = p[z]                       Case 2
     *                               LEFT-ROTATE(T, z)              Case 2
     *                       color[p[z]] = BLACK                    Case 3
     *                       color[p[p[z]]] = RED                   Case 3
     *                       RIGHT-ROTATE(T, p[p[z]])               Case 3
     *          else (same as then clause
     *                        with right and left exchanged)
     * color[root[T]] = BLACK
     * </pre>
     */
    private void fixAfterInsertion(Node z) {
        z.color = RED;
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right;
                if (y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node y = z.parent.parent.left;
                if (y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    /**
     * Introduction to Algorithms (CLR) Second Edition
     * 
     * <pre>
     * RB-DELETE-FIXUP(T, x)
     * while x != root[T] and color[x] = BLACK
     *    do if x = left[p[x]]
     *          then w = right[p[x]]
     *               if color[w] = RED
     *                  then color[w] = BLACK                               Case 1
     *                       color[p[x]] = RED                              Case 1
     *                       LEFT-ROTATE(T, p[x])                           Case 1
     *                       w = right[p[x]]                                Case 1
     *               if color[left[w]] = BLACK and color[right[w]] = BLACK
     *                  then color[w] = RED                                 Case 2
     *                       x = p[x]                                       Case 2
     *                  else if color[right[w]] = BLACK
     *                          then color[left[w]] = BLACK                 Case 3
     *                               color[w] = RED                         Case 3
     *                               RIGHT-ROTATE(T,w)                      Case 3
     *                               w = right[p[x]]                        Case 3
     *                       color[w] = color[p[x]]                         Case 4
     *                       color[p[x]] = BLACK                            Case 4
     *                       color[right[w]] = BLACK                        Case 4
     *                       LEFT-ROTATE(T, p[x])                           Case 4
     *                       x = root[T]                                    Case 4
     *          else (same as then clause with right and left exchanged)
     * color[x] = BLACK
     * </pre>
     */
    private void fixAfterDeletion(Node x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.right.color == BLACK) {
                        w.left.color = BLACK;
                        w.color = RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(w.parent);
                    w = x.parent.left;
                }
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    w.parent.color = BLACK;
                    w.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }

}