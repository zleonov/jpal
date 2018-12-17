package net.javatoday.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * Static utility methods pertaining to {@code BinaryPredicate} instances.
 * <p>
 * The methods in this class return serializable {@code BinaryPredicate}s as long as they are given serializable
 * parameters.
 * 
 * @author Zhenya Leonov
 */
public final class BinaryPredicates {

    private BinaryPredicates() {
    }

    private enum Constants implements BinaryPredicate<Object, Object> {
        ALWAYS_TRUE {
            @Override
            public boolean apply(Object a, Object b) {
                return true;
            }
        },

        ALWAYS_FALSE {
            @Override
            public boolean apply(Object a, Object b) {
                return false;
            }
        },

        BOTH_NULL {
            @Override
            public boolean apply(Object a, Object b) {
                return a == null && b == null;
            }
        },

        BOTH_NOT_NULL {
            @Override
            public boolean apply(Object a, Object b) {
                return a != null && b != null;
            }
        };

        @SuppressWarnings("unchecked")
        private <T, U> BinaryPredicate<T, U> safeCast() {
            return (BinaryPredicate<T, U>) this;
        }
    }

    private static class OrPredicate<T, U> implements BinaryPredicate<T, U>, Serializable {

        private static final long serialVersionUID = 2618799120921008218L;

        private final List<? extends BinaryPredicate<? super T, ? super U>> parts;

        private OrPredicate(final List<? extends BinaryPredicate<? super T, ? super U>> parts) {
            this.parts = parts;
        }

        @Override
        public boolean apply(T t, U u) {
            for (int i = 0; i < parts.size(); i++)
                if (parts.get(i).apply(t, u))
                    return true;
            return false;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with AndPredicate (from Guava)
            return parts.hashCode() + 0x12adbc2c;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof OrPredicate) {
                OrPredicate<?, ?> that = (OrPredicate<?, ?>) obj;
                return parts.equals(that.parts);
            }
            return false;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("parts", Iterables.toString(parts)).toString();
        }

    }

    private static class AndPredicate<T, U> implements BinaryPredicate<T, U>, Serializable {
        private static final long serialVersionUID = 380785103078896860L;

        private final List<? extends BinaryPredicate<? super T, ? super U>> parts;

        private AndPredicate(final List<? extends BinaryPredicate<? super T, ? super U>> parts) {
            this.parts = parts;
        }

        @Override
        public boolean apply(T t, U u) {
            for (int i = 0; i < parts.size(); i++)
                if (!parts.get(i).apply(t, u))
                    return false;
            return true;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with OrPredicate OrPredicate (from Guava)
            return parts.hashCode() + 0x56372c2c;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof AndPredicate) {
                AndPredicate<?, ?> that = (AndPredicate<?, ?>) obj;
                return parts.equals(that.parts);
            }
            return false;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("parts", Iterables.toString(parts)).toString();
        }

    }

    /**
     * Returns a {@code BinaryPredicate} that always evaluates to {@code true}.
     * 
     * @return a {@code BinaryPredicate} that always evaluates to {@code true}
     */
    public static <T, U> BinaryPredicate<T, U> alwaysTrue() {
        return Constants.ALWAYS_TRUE.safeCast();
    }

    /**
     * Returns a {@code BinaryPredicate} that always evaluates to {@code false}.
     * 
     * @return a {@code BinaryPredicate} that always evaluates to {@code false}
     */
    public static <T, U> BinaryPredicate<T, U> alwaysFalse() {
        return Constants.ALWAYS_FALSE.safeCast();
    }

    /**
     * Returns a {@code BinaryPredicate} that evaluates to {@code true} if the objects being tested are both not
     * {@code null}.
     * 
     * 
     * @return a {@code BinaryPredicate} that evaluates to {@code true} if the objects being tested are both not
     *         {@code null}
     */
    public static <T, U> BinaryPredicate<T, U> bothNotNull() {
        return Constants.BOTH_NOT_NULL.safeCast();
    }

    /**
     * Returns a {@code BinaryPredicate} that evaluates to {@code true} if the objects being tested are both {@code null}.
     * 
     * @return a {@code BinaryPredicate} that evaluates to {@code true} if the objects being tested are both {@code null}
     */
    public static <T, U> BinaryPredicate<T, U> bothNull() {
        return Constants.BOTH_NULL.safeCast();
    }

    /**
     * Returns a {@code BinaryPredicate} that evaluates to {@code true} if any one of its {@code parts} evaluates to
     * {@code true}.
     * <p>
     * This method creates a defensive copy of {@code parts} so future changes to it the array won't affect the behavior of
     * the returned {@code BinaryPredicate}.
     * 
     * @param first  the first {@code BinaryPredicate}
     * @param second the second {@code BinaryPredicate}
     * @param rest   the rest of the {@code BinaryPredicate}s
     * @return a {@code BinaryPredicate} that evaluates to {@code true} if any one of its {@code parts} evaluates to
     *         {@code true}
     */
    @SafeVarargs
    public static <T, U> BinaryPredicate<T, U> or(final BinaryPredicate<? super T, ? super U> first, final BinaryPredicate<? super T, ? super U> second, final BinaryPredicate<? super T, ? super U>... rest) {
        checkNotNull(first, "first == null");
        checkNotNull(second, "second == null");
        checkNotNull(rest, "rest == null");

        final ImmutableList<BinaryPredicate<? super T, ? super U>> parts = ImmutableList.<BinaryPredicate<? super T, ? super U>>builder().add(first).add(second).addAll(Iterators.forArray(rest)).build();

        return new OrPredicate<T, U>(parts);
    }

    /**
     * Returns a {@code BinaryPredicate} that evaluates to {@code true} if all of its {@code parts} evaluate to
     * {@code true}.
     * <p>
     * This method creates a defensive copy of {@code parts} so future changes to it the array won't affect the behavior of
     * the returned {@code BinaryPredicate}.
     * 
     * @param first  the first {@code BinaryPredicate}
     * @param second the second {@code BinaryPredicate}
     * @param rest   the rest of the {@code BinaryPredicate}s
     * @return a {@code BinaryPredicate} that evaluates to {@code true} if any all of its {@code parts} evaluates to
     *         {@code true}
     */
    @SafeVarargs
    public static <T, U> BinaryPredicate<T, U> and(final BinaryPredicate<? super T, ? super U> first, final BinaryPredicate<? super T, ? super U> second, final BinaryPredicate<? super T, ? super U>... rest) {
        checkNotNull(first, "first == null");
        checkNotNull(second, "second == null");
        checkNotNull(rest, "rest == null");

        final ImmutableList<BinaryPredicate<? super T, ? super U>> parts = ImmutableList.<BinaryPredicate<? super T, ? super U>>builder().add(first).add(second).addAll(Iterators.forArray(rest)).build();

        return new AndPredicate<T, U>(parts);
    }

}
