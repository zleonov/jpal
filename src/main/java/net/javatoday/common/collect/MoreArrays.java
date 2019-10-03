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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.ObjectArrays;

/**
 * Static utility methods which operate on or return arrays.
 * <p>
 * This class is useful for working with legacy APIs. It holds an advantage over similar Java or Guava equivalents
 * because its methods do not create intermediate objects. Still, it is often preferable to represent arrays as
 * {@code List}s, consider using {@link Arrays#asList(Object[])} or Guava's {@link ImmutableList#copyOf(Object[])}.
 * <p>
 * When not working with legacy APIs users should always prefer {@code Collection} types to arrays.
 * <p>
 * <b>Note:</b> Methods in this class accept {@link java.util.function.Predicate java.util.function.Predicate}s while
 * their Guava counterparts accept {@link com.google.common.base.Predicate com.google.common.base.Predicate}s, the two
 * objects are interchangeable, see {@link com.google.common.base.Predicate com.google.common.base.Predicate} for more
 * information.
 * <p>
 * Below is table of methods provided in this class and their closest Guava and Java equivalents:
 * 
 * <pre>
 * <table border="1" cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th align="center">Method</th><th align="center">List idioms</th><th align="center">Functional idioms</th>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#contains(Object[], Object)}</td>
 *     <td> Java: {@link Arrays#asList(Object[])}{@link List#contains(Object) .contains(Object)}<br>Guava: {@link ImmutableList#copyOf(Object[])}{@link List#contains(Object) .contains(Object)}</td>
 *     <td> Java: {@link Arrays#stream(Object[])}{@link Stream#anyMatch(Predicate) .anyMatch(}{@link Predicate#isEqual(Object) Predicate.isEqual(Object))}<br>Guava: {@link Iterators#contains(Iterator, Object) Iterators.contains(}{@link Iterators#forArray(Object[]) Iterators.forArray(Object[]), Object)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#contains(Object[], Predicate)}</td>
 *     <td colspan=
"2"> Java: {@link Arrays#asList(Object[])}{@link List#stream() .stream()}{@link Stream#anyMatch(Predicate) .anyMatch(Predicate)}<br>Guava: {@link Iterators#any(Iterator, Predicate) Iterators.any(}{@link Iterators#forArray(Object[]) Iterators.forArray(Object[]), Predicate)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#indexOf(Object[], Object)}</td>
 *     <td> Java: {@link Arrays#asList(Object[])}{@link List#indexOf(Object) .indexOf(Object)}<br>Guava: {@link ImmutableList#copyOf(Object[])}{@link List#indexOf(Object) .indexOf(Object)}</td>
 *     <td>Guava: {@link Iterators#indexOf(Iterator, com.google.common.base.Predicate) Iterators.indexOf(}{@link Iterators#forArray(Object[]) Iterators.forArray(Object[]), }{@link Predicates#equalTo(Object) Predicates.equalTo(Object))}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#indexOf(Object[], Object, int)}</td>
 *     <td>N/A</td>
 *     <td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#indexOf(Object[], Predicate)}</td>
 *     <td>N/A</td>
 *     <td>Guava: {@link Iterators#indexOf(Iterator, com.google.common.base.Predicate) Iterators.indexOf(}{@link Iterators#forArray(Object[]) Iterators.forArray(Object[]), Predicate)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#indexOf(Object[], Predicate, int)}</td>
 *     <td>N/A</td>
 *     <td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#lastIndexOf(Object[], Object)}</td>
 *     <td> Java: {@link Arrays#asList(Object[])}{@link List#lastIndexOf(Object) .lastIndexOf(Object)}<br>Guava: {@link ImmutableList#copyOf(Object[])}{@link List#lastIndexOf(Object) .lastIndexOf(Object)}</td>
 *     <td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#lastIndexOf(Object[], Object, int)}</td>
 *     <td>N/A</td>
 *     <td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#lastIndexOf(Object[], Predicate)}</td>
 *     <td>N/A</td>
 *     <td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MoreArrays#lastIndexOf(Object[], Predicate, int)}</td>
 *     <td>N/A</td>
 *     <td>N/A</td>
 *   </tr>
 * </table>
 * </pre>
 * 
 * @author Zhenya Leonov
 * @see Arrays
 * @see ObjectArrays
 * @see Iterators#forArray(Object[])
 * @see Arrays#stream(Object[])
 */
final public class MoreArrays {

    private MoreArrays() {
    };

    /**
     * Returns {@code true} if the specified array contains the given element, {@code false} otherwise.
     * 
     * @param array   the specified array
     * @param element the object to look find
     * @return {@code true} if the specified array contains the given element, {@code false} otherwise
     */
    public static <T> boolean contains(final T[] array, final Object element) {
        return indexOf(array, element, 0) > -1;
    }

    /**
     * Returns {@code true} if the specified array contains the an element which satisfies the given predicate,
     * {@code false} otherwise.
     * <p>
     * <b>Note:</b> If the array contains {@code null} elements the predicate must be able to handle {@code null} inputs to
     * avoid a {@code NullPointerException}.
     * 
     * @param array     the specified array
     * @param predicate the given predicate
     * @return {@code true} if the specified array contains the an element which satisfies the given predicate,
     *         {@code false} otherwise
     */
    public static <T> boolean contains(final T[] array, final Predicate<? super T> predicate) {
        return indexOf(array, predicate, 0) > -1;
    }

    /**
     * Returns the index of the first occurrence of the specified element in the given array, or -1 if there is no such
     * element.
     * 
     * @param array   the given array
     * @param element the element to look for
     * @return the index of the first occurrence in the specified element in the given array, or -1 if there is no such
     *         element
     */
    public static <T> int indexOf(final T[] array, final Object element) {
        return indexOf(array, element, 0);
    }

    /**
     * Returns the index of the first occurrence of the specified element in the given array, starting the search at
     * {@code fromIndex}, or -1 if there is no such element.
     * 
     * @param array     the given array
     * @param element   the element to look for
     * @param fromIndex the index to start the search from
     * @return the index of the first occurrence in the specified element in the given array, starting the search at
     *         {@code fromIndex}, or -1 if there is no such element
     */
    public static <T> int indexOf(final T[] array, final Object element, final int fromIndex) {
        checkNotNull(array, "array == null");
        checkElementIndex(fromIndex, array.length);
        for (int i = fromIndex; i < array.length; i++)
            if (Objects.equals(array[i], element))
                return i;
        return -1;
    }

    /**
     * Returns the index of the first occurrence in the specified array of an element which satisfies the given predicate,
     * or -1 if there is no such element.
     * <p>
     * <b>Note:</b> If the array contains {@code null} elements the predicate must be able to handle {@code null} inputs to
     * avoid a {@code NullPointerException}.
     * 
     * @param array     the specified array
     * @param predicate the given predicate
     * @return the index of the first occurrence in the specified array of an element which satisfies the given predicate,
     *         or -1 if there is no such element
     */
    public static <T> int indexOf(final T[] array, final Predicate<? super T> predicate) {
        return indexOf(array, predicate, 0);
    }

    /**
     * Returns the index of the first occurrence in the specified array of an element which satisfies the given predicate,
     * starting the search at {@code fromIndex}, or -1 if there is no such element.
     * <p>
     * <b>Note:</b> If the array contains {@code null} elements the predicate must be able to handle {@code null} inputs to
     * avoid a {@code NullPointerException}.
     * 
     * @param array     the specified array
     * @param predicate the given predicate
     * @param fromIndex the index to start the search from
     * @return the index of the first occurrence in the specified array of an element which satisfies the given predicate,
     *         starting the search at {@code fromIndex}, or -1 if there is no such element
     */
    public static <T> int indexOf(final T[] array, final Predicate<? super T> predicate, final int fromIndex) {
        checkNotNull(array, "array == null");
        checkNotNull(predicate, "predicate == null");
        checkElementIndex(fromIndex, array.length);
        for (int i = fromIndex; i < array.length; i++)
            if (predicate.test(array[i]))
                return i;
        return -1;
    }

    /**
     * Returns the index of the last occurrence in the specified element in the given array, or -1 if there is no such
     * element.
     * 
     * @param array   the given array
     * @param element the element to look for
     * @return the index of the last occurrence in the specified element in the given array, or -1 if there is no such
     *         element
     */
    public static <T> int lastIndexOf(final T[] array, final Object element) {
        checkNotNull(array, "array == null");
        return lastIndexOf(array, element, array.length - 1);
    }

    /**
     * Returns the index of the last occurrence in the specified element in the given array, searching backward starting at
     * {@code fromIndex}, or -1 if there is no such element.
     * 
     * @param array     the given array
     * @param element   the element to look for
     * @param fromIndex the index to start the search from
     * @return the index of the last occurrence in the specified element in the given array, searching backward starting at
     *         {@code fromIndex}, or -1 if there is no such element
     */
    public static <T> int lastIndexOf(final T[] array, final Object element, final int fromIndex) {
        checkNotNull(array, "array == null");
        checkElementIndex(fromIndex, array.length);
        for (int i = fromIndex; i >= 0; i--)
            if (Objects.equals(array[i], element))
                return i;
        return -1;
    }

    /**
     * Returns the index of the last occurrence in the specified array of an element which satisfies the given predicate, or
     * -1 if there is no such element.
     * <p>
     * <b>Note:</b> If the array contains {@code null} elements the predicate must be able to handle {@code null} inputs to
     * avoid a {@code NullPointerException}.
     * 
     * @param array     the specified array
     * @param predicate the given predicate
     * @return the index of the last occurrence in the specified array of an element which satisfies the given predicate, or
     *         -1 if there is no such element
     */
    public static <T> int lastIndexOf(final T[] array, final Predicate<? super T> predicate) {
        checkNotNull(array, "array == null");
        return lastIndexOf(array, predicate, array.length - 1);
    }

    /**
     * Returns the index of the last occurrence in the specified array of an element which satisfies the given predicate,
     * searching backward starting at {@code fromIndex}, or -1 if there is no such element.
     * <p>
     * <b>Note:</b> If the array contains {@code null} elements the predicate must be able to handle {@code null} inputs to
     * avoid a {@code NullPointerException}.
     * 
     * @param array     the specified array
     * @param predicate the given predicate
     * @param fromIndex the index to start the search from
     * @return the index of the last occurrence in the specified array of an element which satisfies the given predicate,
     *         searching backward starting at {@code fromIndex}, or -1 if there is no such element
     */
    public static <T> int lastIndexOf(final T[] array, final Predicate<? super T> predicate, final int fromIndex) {
        checkNotNull(array, "array == null");
        checkNotNull(predicate, "predicate == null");
        checkElementIndex(fromIndex, array.length);
        for (int i = fromIndex; i > 0; i--)
            if (predicate.test(array[i]))
                return i;
        return -1;
    }

}