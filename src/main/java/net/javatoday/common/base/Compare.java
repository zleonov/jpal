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
