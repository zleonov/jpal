/*
<<<<<<< HEAD
 * Copyright (C) 2019 Zhenya Leonov
=======
 * Copyright (C) 2018 Zhenya Leonov
>>>>>>> branch 'master' of git@github.com:zleonov/jpal.git
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
package net.javatoday.common.base;

import java.util.Comparator;

import com.google.common.collect.Ordering;

/**
 * Static utility methods that operate on or return {@link Comparator}s or Guava's {@link Ordering}.
 * 
 * @author Zhenya Leonov
 */
public final class Compare {

    private static final Ordering<Comparable<?>> NATURAL_ORDERING_NULLS_FIRST = Ordering.natural().nullsFirst();
    private static final Ordering<Comparable<?>> NATURAL_ORDERING_NULLS_LAST = Ordering.natural().nullsLast();

    private Compare() {
    }

    /**
     * Returns a serializable {@code Ordering} that orders elements according to their <i>natural order</i> and treats
     * {@code null}s as less than all other values.
     * 
     * @return a serializable {@code Ordering} that orders elements according to their <i>natural order</i> and treats
     *         {@code null}s as less than all other values
     */
    @SuppressWarnings("unchecked")
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <T extends Comparable<? super T>> Ordering<T> naturalNullsFirst() {
        return (Ordering<T>) NATURAL_ORDERING_NULLS_FIRST;
    }

    /**
     * Returns a serializable {@code Ordering} that orders elements according to their <i>natural order</i> and treats
     * {@code null}s as greater than all other values.
     * 
     * @return a serializable {@code Ordering} that orders elements according to their <i>natural order</i> and treats
     *         {@code null}s as greater than all other values
     */
    @SuppressWarnings("unchecked")
    // Use <T extends Comparable<?>> instead of the technically correct <T extends Comparable<? super T>> if using Java 6.
    public static <T extends Comparable<? super T>> Ordering<T> naturalNullsLast() {
        return (Ordering<T>) NATURAL_ORDERING_NULLS_LAST;
    }

}
