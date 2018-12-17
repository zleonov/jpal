/*
 * Copyright (C) 2012 Zhenya Leonov
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

import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ObjectArrays;

/**
 * More static utility methods which operate on or return arrays, useful for working with legacy APIs.
 * <p>
 * Users are otherwise encouraged to prefer {@code Collection}s to arrays.
 * 
 * @author Zhenya Leonov
 * @see Arrays
 * @see ObjectArrays
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
        checkNotNull(array, "array == null");
        checkNotNull(element, "element == null");
        for (final Object o : array)
            if (Objects.equal(o, element))
                return true;
        return false;
    }

    /**
     * Returns the index of the first occurrence in the specified element in the given array, or -1 if there is no such
     * element.
     * 
     * @param array   the given array
     * @param element the element to look for
     * @return the index of the first occurrence in the specified element in the given array, or -1 if there is no such
     *         element
     */
    public static <T> int indexOf(final T[] array, final Object element) {
        checkNotNull(array, "array == null");
        checkNotNull(element, "element == null");
        for (int i = 0; i < array.length; i++)
            if (element.equals(array[i]))
                return i;
        return -1;
    }

    /**
     * Returns the index of the first occurrence in the specified array of an element which satisfies the given predicate,
     * or -1 if there is no such element.
     * <p>
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs as well to avoid a {@code NullPointerException}.
     * 
     * @param array     the specified array
     * @param predicate the given predicate
     * @return the index of the first occurrence in the specified array of an element which satisfies the given predicate,
     *         or -1 if there is no such element
     */
    public static <T> int indexOf(final T[] array, final Predicate<? super T> predicate) {
        checkNotNull(array, "array == null");
        checkNotNull(predicate, "predicate == null");
        for (int i = 0; i < array.length; i++)
            if (predicate.apply(array[i]))
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
        for (int i = array.length - 1; i >= 0; i--)
            if (array[i].equals(element))
                return i;
        return -1;
    }

    /**
     * Returns the index of the last occurrence in the specified array of an element which satisfies the given predicate, or
     * -1 if there is no such element.
     * <p>
     * Note: If the specified list allows {@code null} elements the given predicate must be able to handle {@code null}
     * inputs as well to avoid a {@code NullPointerException}.
     * 
     * @param array     the specified array
     * @param predicate the given predicate
     * @return the index of the last occurrence in the specified array of an element which satisfies the given predicate, or
     *         -1 if there is no such element
     */
    public static <T> int lastIndexOf(final T[] array, final Predicate<? super T> predicate) {
        checkNotNull(array, "array == null");
        checkNotNull(predicate, "predicate == null");
        for (int i = array.length; i > 0; i--)
            if (predicate.apply(array[i - 1]))
                return i;
        return -1;
    }

}