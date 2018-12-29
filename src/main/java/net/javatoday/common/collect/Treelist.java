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
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkPositionIndexes;
import static com.google.common.base.Preconditions.checkState;
import static net.javatoday.common.collect.Treelist.Color.BLACK;
import static net.javatoday.common.collect.Treelist.Color.RED;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.RandomAccess;
import java.util.SortedSet;

import com.google.common.collect.ForwardingListIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;

/**
 * A {@link Sortedlist} implementation, based on a modified
 * <a href="http://en.wikipedia.org/wiki/Red-black_tree">Red-Black Tree</a>. Elements are sorted from <i>least</i> to
 * <i>greatest</i> according to their <i>natural ordering</i>, or by an explicit {@link Comparator} provided at
 * creation. Attempting to insert {@code null} elements is prohibited. Querying for {@code null} elements is allowed.
 * Inserting non-comparable elements will result in a {@code ClassCastException}.
 * <p>
 * The iterators obtained from the {@link #iterator()} and {@link #listIterator()} methods are <i>fail-fast</i>.
 * Attempts to modify the elements in this sorted-list at any time after an iterator is created, in any way except
 * through the iterator's own remove method, will result in a {@code ConcurrentModificationException}. Further, the list
 * iterator does not support the {@code add(E)} and {@code set(E)} operations.
 * <p>
 * This sorted-list is not <i>thread-safe</i>. If multiple threads modify this sorted-list concurrently it must be
 * synchronized externally.
 * <p>
 * This implementation uses a comparator (whether or not one is explicitly provided) to perform all element comparisons.
 * Two elements which are deemed equal by the comparator's {@code compare(E, E)} method are, from the standpoint of this
 * list, equal. Further, no guarantee is made as to the final order of <i>equal</i> elements. Ties may be broken
 * arbitrarily.
 * <p>
 * The underlying Red-Black Tree provides the following amortized running time (where <i>n</i> is the size of this
 * sorted-list and <i>m</i> is the size of the specified collection which is iterable in linear time):
 * <p>
 * <table border="1" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;">Method</th>
 *     <th style="text-align:center;">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}<br/>
 *       {@link #containsAll(Collection) containsAll(Collection)}<br/>
 *       {@link #retainAll(Collection) retainAll(Collection)}<br/>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td style="text-align:center;"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #indexOf(Object)}<br/>
 *       {@link #lastIndexOf(Object)}<br/>
 *       {@link #get(int)}<br/>
 *       {@link #remove(int)}<br/>
 *       {@link #listIterator(int)}
 *     </td>
 *     <td style="text-align:center;"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}<br/>
 *       {@link #contains(Object)}<br/>
 *       {@link #remove(Object)}
 *     </td>
 *     <td style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}<br/>
 *       {@link #isEmpty() isEmpty()}<br/>
 *       {@link #size()}<br/>
 *       {@link Iterator#remove()}<br/>
 *       {@link ListIterator#remove()}
 *     </td>
 *     <td style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * <p>
 * The {@code subList} views exhibit identical time complexity, with the
 * exception of the {@code clear()} operation which runs in linear time
 * proportional to the size of the view.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements maintained by this list
 * @see Skiplist
 */
public class Treelist<E> extends AbstractCollection<E> implements Sortedlist<E>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    transient int size = 0;
    private transient Node nil = new Node();
    private transient Node min = nil;
    private transient Node max = nil;
    private transient Node root = nil;
    transient int modCount = 0;
    private final Comparator<? super E> comparator;
    private transient final List<E> asList = new ListView();

    private Treelist(final Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Creates a new {@code Treelist} that orders its elements according to their <i>natural ordering</i>.
     * 
     * @return a new {@code Treelist} that orders its elements according to their <i>natural ordering</i>
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Comparable> Treelist<E> create() {
        return new Treelist<E>(Ordering.natural());
    }

    /**
     * Creates a new {@code Treelist} containing the specified initial elements. If {@code elements} is an instance of
     * {@link SortedSet}, {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or {@code SortedCollection}, this list will be
     * ordered according to the same ordering. Otherwise, this list will be ordered according to the <i>natural ordering</i>
     * of its elements.
     * 
     * @param elements the collection whose elements are to be placed into the list
     * @return a new {@code Treelist} containing the elements of the specified collection
     * @throws ClassCastException   if elements of the specified collection cannot be compared to one another according to
     *                              this list's ordering
     * @throws NullPointerException if any of the elements of the specified collection or the collection itself is
     *                              {@code null}
     */
    @SuppressWarnings({ "unchecked" })
    public static <E> Treelist<E> from(final Collection<? extends E> elements) {
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
        return orderedBy(comparator).create(elements);
    }

    /**
     * Returns a new builder configured to build {@code Treelist} instances that use the specified comparator for ordering.
     * 
     * @param comparator the specified comparator
     * @return a new builder configured to build {@code Treelist} instances that use the specified comparator for ordering
     */
    public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
        checkNotNull(comparator, "comparator == null");
        return new Builder<B>(comparator);
    }

    /**
     * A builder for the creation of {@code Treelist} instances. Instances of this builder are obtained calling
     * {@link Treelist#orderedBy(Comparator)}.
     * 
     * @author Zhenya Leonov
     * @param <B> the upper bound of the type of queues this builder can produce (for example a {@code Builder<Number>} can
     *        produce a {@code Treelist<Float>} or a {@code Treelist<Integer>}
     */
    public static final class Builder<B> {

        private final Comparator<B> comparator;

        private Builder(final Comparator<B> comparator) {
            this.comparator = comparator;
        }

        /**
         * Builds an empty {@code Treelist} using the previously specified comparator.
         * 
         * @return an empty {@code Treelist} using the previously specified comparator.
         */
        public <T extends B> Treelist<T> create() {
            return new Treelist<T>(comparator);
        }

        /**
         * Builds a new {@code Treelist} using the previously specified comparator, and having the given initial elements.
         * 
         * @param elements the initial elements to be placed in this queue
         * @return a new {@code Treelist} using the previously specified comparator, and having the given initial elements
         */
        public <T extends B> Treelist<T> create(final Iterable<? extends T> elements) {
            checkNotNull(elements, "elements == null");
            final Treelist<T> list = new Treelist<T>(comparator);
            Iterables.addAll(list, elements);
            return list;
        }
    }

    /**
     * Returns a {@link List} view of this sorted-list. The returned list is not serializable and does not support the
     * {@link List#add(Object) add(E)} and {@link List#set(int, Object) set(int, E)} operations.
     * <p>
     * Changes to the returned list, such as element removal, are "write through" to this sorted-list.
     * 
     * @return a {@link List} view of this sorted-list
     */
    @Override
    public List<E> asList() {
        return asList;
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
        Node newNode = new Node(e);
        insert(newNode);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return o != null && search((E) o) != null;
    }

    @Override
    public E get(int index) {
        checkElementIndex(index, size);
        Iterator<E> itor = iterator();
        for (int i = 0; i < index; i++)
            itor.next();
        return itor.next();
    }

    @Override
    public int indexOf(Object o) {
        if (o != null) {
            @SuppressWarnings("unchecked")
            final E e = (E) o;
            final ListIterator<E> itor = listIterator();
            while (itor.hasNext())
                if (comparator.compare(itor.next(), e) == 0)
                    return itor.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o != null) {
            @SuppressWarnings("unchecked")
            final E e = (E) o;
            final ListIterator<E> itor = listIterator();
            while (itor.hasNext())
                if (comparator.compare(itor.next(), e) == 0) {
                    while (itor.hasNext())
                        if (comparator.compare(itor.next(), e) != 0) {
                            itor.previous();
                            break;
                        }
                    return itor.previousIndex();
                }
        }
        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<E>() {
            private int index = 0;
            private Node next = min;
            private Node prev = nil;
            private Node last = nil;
            private int expectedModCount = modCount;

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public boolean hasPrevious() {
                return index > 0;
            }

            @Override
            public E next() {
                checkForConcurrentModification();
                if (index == size())
                    throw new NoSuchElementException();
                prev = next;
                index++;
                next = successor(prev);
                last = prev;
                return prev.element;
            }

            @Override
            public int nextIndex() {
                return index;
            }

            @Override
            public E previous() {
                checkForConcurrentModification();
                if (index == 0)
                    throw new NoSuchElementException();
                next = prev;
                index--;
                prev = predecessor(next);
                last = next;
                return next.element;
            }

            @Override
            public int previousIndex() {
                return index - 1;
            }

            @Override
            public void remove() {
                checkForConcurrentModification();
                checkState(last != nil);
                if (last.left != nil && last.right != nil)
                    next = last;
                delete(last);
                index--;
                expectedModCount = modCount;
                last = nil;
            }

            @Override
            public void set(E e) {
                throw new UnsupportedOperationException();
            }

            private void checkForConcurrentModification() {
                if (expectedModCount != modCount)
                    throw new ConcurrentModificationException();
            }
        };
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index, size);
        ListIterator<E> listIterator = listIterator();
        for (int i = 0; i < index; i++)
            listIterator.next();
        return listIterator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if (o == null)
            return false;
        Node node = search((E) o);
        if (node == null)
            return false;
        delete(node);
        return true;
    }

    @Override
    public E remove(int index) {
        checkElementIndex(index, size);
        ListIterator<E> li = listIterator(index);
        E e = li.next();
        li.remove();
        return e;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        modCount++;
        root = nil;
        min = nil;
        max = nil;
        size = 0;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this)
            hashCode = 31 * hashCode + e.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sortedlist))
            return false;
        try {
            @SuppressWarnings("unchecked")
            final Iterator<E> i = ((Collection<E>) o).iterator();
            for (E e : this)
                if (comparator.compare(e, i.next()) != 0)
                    return false;
            return !i.hasNext();
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public Treelist<E> sublist(int fromIndex, int toIndex) {
        checkPositionIndexes(fromIndex, toIndex, size());
        return new Sublist(this, fromIndex, toIndex);
    }

    /**
     * Returns a shallow copy of this {@code Treelist}. The elements themselves are not cloned.
     * 
     * @return a shallow copy of this tree list
     * @throws CloneNotSupportedException if an attempt is made to clone is a sub-list view of this sorted-list
     */
    @SuppressWarnings("unchecked")
    @Override
    public Treelist<E> clone() throws CloneNotSupportedException {
        Treelist<E> clone;
        try {
            clone = (Treelist<E>) super.clone();
        } catch (java.lang.CloneNotSupportedException e) {
            throw new InternalError();
        }
        clone.nil = new Node();
        clone.min = clone.nil;
        clone.max = clone.nil;
        clone.root = clone.nil;
        clone.size = 0;
        clone.modCount = 0;
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
        nil = new Node();
        root = nil;
        min = nil;
        max = nil;
        int size = ois.readInt();
        for (int i = 0; i < size; i++)
            add((E) ois.readObject());
    }

    @SuppressWarnings("serial")
    private class Sublist extends Treelist<E> {
        private final Treelist<E> list;
        private final int offset;
        private Node from;
        private Node to;

        private void checkForConcurrentModification() {
            if (this.modCount != list.modCount)
                throw new ConcurrentModificationException();
        }

        public Sublist(Treelist<E> list, int fromIndex, int toIndex) {
            super(list.comparator);
            this.list = list;
            from = list.min;
            offset = fromIndex;
            this.modCount = list.modCount;
            this.size = toIndex - fromIndex;
            int i = 0;
            for (; i < fromIndex; i++)
                from = successor(from);
            to = from;
            for (; i < toIndex - 1; i++)
                to = successor(to);
        }

        @Override
        public boolean add(E e) {
            checkForConcurrentModification();
            if (comparator.compare(e, from.element) < 0 || comparator.compare(e, to.element) > 0)
                throw new IllegalArgumentException("element out of range");
            list.add(e);
            this.modCount = list.modCount;
            this.size++;
            if (comparator.compare(to.element, e) <= 0)
                to = successor(to);
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o) {
            checkForConcurrentModification();
            return o != null && search((E) o) != null;
        }

        @Override
        public E get(int index) {
            checkForConcurrentModification();
            checkElementIndex(index, this.size);
            return list.get(index + offset);
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(final int index) {
            checkForConcurrentModification();
            checkPositionIndex(index, Sublist.this.size);
            return new ListIterator<E>() {
                private ListIterator<E> i = list.listIterator(index + offset);

                @Override
                public boolean hasNext() {
                    return nextIndex() < Sublist.this.size;
                }

                @Override
                public E next() {
                    if (hasNext())
                        return i.next();
                    else
                        throw new NoSuchElementException();
                }

                @Override
                public boolean hasPrevious() {
                    return previousIndex() >= 0;
                }

                @Override
                public E previous() {
                    if (hasPrevious())
                        return i.previous();
                    else
                        throw new NoSuchElementException();
                }

                @Override
                public int nextIndex() {
                    return i.nextIndex() - offset;
                }

                @Override
                public int previousIndex() {
                    return i.previousIndex() - offset;
                }

                @Override
                public void remove() {
                    i.remove();
                    Sublist.this.modCount = list.modCount;
                    Sublist.this.size--;
                }

                @Override
                public void set(E e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(E e) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean remove(Object o) {
            checkForConcurrentModification();
            checkNotNull(o, "o == null");
            final Node node = search((E) o);
            if (node == null)
                return false;
            if (node == to)
                to = predecessor(to);
            if (node == from)
                from = successor(from);
            list.delete(node);
            this.modCount = list.modCount;
            this.size--;
            return true;
        }

        @Override
        public E remove(int index) {
            checkForConcurrentModification();
            checkElementIndex(index, this.size);
            if (index == 0)
                from = successor(from);
            if (index == this.size - 1)
                to = predecessor(to);
            final E e = list.remove(index + offset);
            this.modCount = list.modCount;
            this.size--;
            return e;
        }

        @Override
        public int size() {
            checkForConcurrentModification();
            return this.size;
        }

        @Override
        public void clear() {
            checkForConcurrentModification();
            final Iterator<E> iterator = iterator();
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }

        @Override
        public Treelist<E> clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }

        private void writeObject(java.io.ObjectOutputStream oos) throws NotSerializableException {
            throw new NotSerializableException();
        }

        private void readObject(java.io.ObjectInputStream ois) throws NotSerializableException {
            throw new NotSerializableException();
        }

        // Red-Black-Tree

        @Override
        Node search(final E e) {
            final int compareFrom = comparator.compare(e, from.element);
            final int compareTo = comparator.compare(e, to.element);
            if (compareFrom < 0 || compareTo > 0)
                return null;
            if (compareFrom == 0)
                return from;
            else if (compareTo == 0)
                return to;
            else
                return list.search(e);
        }
    }

    private class ListView extends AbstractList<E> implements RandomAccess {

        @Override
        public int size() {
            return Treelist.this.size();
        }

        @Override
        public boolean isEmpty() {
            return Treelist.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return Treelist.this.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return listIterator(0);
        }

        @Override
        public E get(int index) {
            return Treelist.this.get((int) index);
        }

        @Override
        public int indexOf(Object o) {
            return Treelist.this.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return Treelist.this.lastIndexOf(o);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            final ListIterator<E> itor = Treelist.this.listIterator(index);

            return new ForwardingListIterator<E>() {

                @Override
                public void remove() {
                    delegate().remove();
                }

                @Override
                protected ListIterator<E> delegate() {
                    return itor;
                }
            };
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return Treelist.this.sublist(fromIndex, toIndex).asList();
        }

        @Override
        public boolean remove(Object o) {
            final int index = Treelist.this.indexOf(o);
            if (index < 0)
                return false;
            Treelist.this.remove(index);
            return true;
        }

        @Override
        public void clear() {
            Treelist.this.clear();
        }

        @Override
        public E remove(int index) {
            return Treelist.this.remove(index);
        }

    }

    // Red-Black-Tree

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

    Node search(final E e) {
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
     */
    private void insert(Node z) {
        size++;
        modCount++;
        Node x = root;
        Node y = nil;
        while (x != nil) {
            y = x;
            if (comparator.compare(z.element, x.element) < 0)
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
        if (max == nil || comparator.compare(z.element, max.element) >= 0)
            max = z;
        if (min == nil || comparator.compare(z.element, min.element) < 0)
            min = z;
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
     */
    private void delete(Node z) {
        size--;
        modCount++;
        Node x, y;
        if (min == z)
            min = successor(z);
        if (max == z)
            max = predecessor(z);
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