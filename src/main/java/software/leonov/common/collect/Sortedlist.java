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

import java.util.List;
import java.util.ListIterator;

/**
 * A {@link SortedCollection} which may contain duplicate elements and allows users to access or remove elements by
 * their integer index.
 * <p>
 * This is the <i>list</i> analog of sorted set.
 * 
 * @author Zhenya Leonov
 * @param <E> the type of elements held in this sorted-list
 */
public interface Sortedlist<E> extends SortedCollection<E> {

    /**
     * Returns a {@link List} view of this sorted-list.
     * 
     * @return a {@link List} view of this sorted-list
     */
    public List<E> asList();

    /**
     * Returns the element at the specified position.
     * 
     * @param index index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the specified index is out of range
     */
    public E get(int index);

    /**
     * Removes and returns the element at the specified position (optional operation).
     * 
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is out of range
     */
    public E remove(int index);

    /**
     * Returns the index of the first occurrence of the specified element, or -1 if the element is not present.
     * 
     * @param o element to search for
     * @return the index of the first occurrence of the specified element, or -1 if the element is not present
     */
    public int indexOf(Object o);

    /**
     * Returns the index of the last occurrence of the specified element, or -1 if the element is not present.
     * 
     * @param o element to search for
     * @return the index of the last occurrence of the specified element, or -1 if the element is not present
     */
    public int lastIndexOf(Object o);

    /**
     * Returns a list iterator over the elements in this sorted-list.
     * <p>
     * The returned iterator does not support the {@link ListIterator#add(Object) add(E)} and
     * {@link ListIterator#set(Object) set(E)} operations.
     * 
     * @return a list iterator over the elements in this sorted-list
     */
    public ListIterator<E> listIterator();

    /**
     * Returns a list iterator over the elements in this sorted-list, starting at the specified position.
     * <p>
     * The returned iterator does not support the {@link ListIterator#add(Object) add(E)} and
     * {@link ListIterator#set(Object) set(E)} operations.
     * 
     * @param index index of the first element to be returned from the collection iterator by a call to
     *              {@link ListIterator#next() next()}
     * @return a list iterator over the elements in this sorted-list, starting at the specified position
     */
    public ListIterator<E> listIterator(int index);

    /**
     * Returns a view of the portion of this sorted-list whose elements range between {@code fromIndex}, inclusive, and
     * {@code toIndex}, exclusive. If {@code fromIndex} and {@code toIndex} are equal, the returned sorted-list is empty.
     * The returned sorted-list is backed by this sorted-list, so changes in the returned sorted-list are reflected in this
     * sorted-list, and vice-versa.
     * <p>
     * The returned sorted-list will throw an {@code IllegalArgumentException} on an attempt to insert an element outside
     * its range.
     * 
     * @param fromIndex low endpoint (inclusive) of the returned sorted-list
     * @param toIndex   high endpoint (exclusive) of the returned sorted-list
     * @return a view of the portion of this sorted-list within the specified range
     * @throws IndexOutOfBoundsException if {@code fromIndex < 0 || toIndex > size || fromIndex > toIndex}
     */
    public Sortedlist<E> sublist(int fromIndex, int toIndex);

    /**
     * Returns the hash code value for this sorted-list. The hash code of a sorted-list is defined to be the result of the
     * following calculation:
     * 
     * <pre>
     * int hashCode = 1;
     * for (E e : this)
     *     hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
     * </pre>
     * 
     * @return the hash code value for this sorted-list
     */
    @Override
    public int hashCode();

    /**
     * Compares the specified object with this sorted-list for equality. Returns {@code true} if and only if the specified
     * object is also a sorted-list, both sorted-lists have the same size, and all corresponding pairs of elements in the
     * two sorted-lists are <i>equal</i> according to the definition of this sorted-list.
     * 
     * @return {@code true} if the specified object is {@code equal} to this sorted-list
     */
    @Override
    public boolean equals(Object o);

}