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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import com.google.common.collect.Collections2;

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

}
