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
package software.leonov.common.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Throwables;

/**
 * A collection of <b>unsafe</b> utility methods which allow users to treat all exceptions as unchecked exceptions.
 * <p>
 * All {@code eval} methods in this class accept {@link FunctionalInterface}s as their arguments, therefore they can be
 * used as the assignment targets for lambda expression or method reference.
 * <p>
 * <b>Warning:</b> This class breaks Java's exception handling idiom and can lead to horrible errors when misused. See
 * <a target="_blank" href="https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html">Unchecked
 * Exceptions — The Controversy</a> for further discussion. If in doubt <b>do not use</b>.
 * 
 * @author Zhenya Leonov
 */
public final class Try {

    /**
     * Mirror of the {@code BiConsumer} interface whose {@code accept(T, U)} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedBiConsumer<T, U> {

        /**
         * Performs this operation on the given arguments.
         *
         * @param t the first argument
         * @param u the second argument
         */
        void accept(T t, U u) throws Exception;
    }

    /**
     * Mirror of the {@code BiFunction} interface whose {@code apply(T, U)} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedBiFunction<T, U, R> {

        /**
         * Applies this function to the given arguments.
         *
         * @param t the first argument
         * @param u the second argument
         * @return the function result
         */
        R apply(T t, U u) throws Exception;
    }

    /**
     * Mirror of the {@code BiPredicate} interface whose {@code test(T, U)} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedBiPredicate<T, U> {

        /**
         * Evaluates this predicate on the given arguments.
         *
         * @param t the first argument
         * @param u the second argument
         * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
         */
        boolean test(T t, U u) throws Exception;
    }

    /**
     * Mirror of the {@code Comparator} interface whose {@code compare(T, T)} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedComparator<T> {

        /**
         * Compares its two arguments for order.
         *
         * @param o1 the first argument
         * @param o2 the second argument
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than
         *         the second
         */
        int compare(T o1, T o2) throws Exception;
    }

    /**
     * Mirror of the {@code Consumer} interface whose {@code accept(T)} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedConsumer<T> {

        /**
         * Performs this operation on the given argument.
         *
         * @param t the input argument
         */
        void accept(T t) throws Exception;
    }

    /**
     * Mirror of the {@code Function} interface whose {@code apply(T)} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedFunction<T, R> {

        /**
         * Applies this function to the given argument.
         *
         * @param t the function argument
         * @return the function result
         */
        R apply(T t) throws Exception;
    }

    /**
     * Mirror of the {@code Predicate} interface whose {@code test(T)} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedPredicate<T> {

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param t the input argument
         * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
         */
        boolean test(T t) throws Exception;
    }

    /**
     * Mirror of the {@code Runnable} interface whose {@code run()} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedRunnable {

        /**
         * The general contract of the method {@code run()} is that it may take any action whatsoever.
         */
        void run() throws Exception;
    }

    /**
     * Mirror of the {@code Supplier} interface whose {@code get()} method can throw a checked exception.
     */
    @FunctionalInterface
    public static interface CheckedSupplier<T> {

        /**
         * Returns a result.
         *
         * @return a result
         */
        T get() throws Exception;
    }

    private Try() {
    }

    /**
     * Returns a {@link BiConsumer} which delegates to the underlying {@link CheckedBiConsumer CheckedBiConsumer
     * CheckedBiConsumer CheckedBiConsumer}, {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were
     * unchecked.
     * 
     * @param consumer the underlying checked bi-consumer
     * @return a {@link BiConsumer} which delegates to the underlying {@link CheckedBiConsumer CheckedBiConsumer
     *         CheckedBiConsumer CheckedBiConsumer}, {@link #unchecked(Throwable) rethrowing} any checked exceptions as if
     *         they were unchecked
     */
    public static <T, U> BiConsumer<T, U> eval(final CheckedBiConsumer<? super T, ? super U> consumer) {
        checkNotNull(consumer, "consumer == null");
        return (i, j) -> {
            try {
                consumer.accept(i, j);
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link BiFunction} which delegates to the underlying {@link CheckedBiFunction CheckedBiFunction},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param function the underlying checked bi-function
     * @return a {@link BiFunction} which delegates to the underlying {@link CheckedBiFunction CheckedBiFunction},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T, U, R> BiFunction<T, U, R> eval(final CheckedBiFunction<? super T, ? super U, ? extends R> function) {
        checkNotNull(function, "function == null");
        return (i, j) -> {
            try {
                return function.apply(i, j);
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link BiPredicate} which delegates to the underlying {@link CheckedBiPredicate CheckedBiPredicate},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param predicate the underlying checked bi-predicate
     * @return a {@link BiPredicate} which delegates to the underlying {@link CheckedBiPredicate CheckedBiPredicate},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T, U> BiPredicate<T, U> eval(final CheckedBiPredicate<? super T, ? super U> predicate) {
        checkNotNull(predicate, "predicate == null");
        return (i, j) -> {
            try {
                return predicate.test(i, j);
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link Comparator} which delegates to the underlying {@link CheckedComparator CheckedComparator},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param comparator the specified checked comparator
     * @return a {@link Comparator} which delegates to the underlying {@link CheckedComparator CheckedComparator},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Comparator<T> eval(final CheckedComparator<? super T> comparator) {
        checkNotNull(comparator, "comparator == null");
        return (left, right) -> {
            try {
                return comparator.compare(left, right);
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link Consumer} which delegates to the underlying {@link CheckedConsumer CheckedConsumer},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param consumer the underlying checked consumer
     * @return a {@link Consumer} which delegates to the underlying {@link CheckedConsumer CheckedConsumer},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Consumer<T> eval(final CheckedConsumer<? super T> consumer) {
        checkNotNull(consumer, "consumer == null");
        return i -> {
            try {
                consumer.accept(i);
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link Function} which delegates to the underlying {@link CheckedFunction CheckedFunction},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     * 
     * @param function the underlying checked function
     * @return a {@link Function} which delegates to the underlying {@link CheckedFunction CheckedFunction},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T, R> Function<T, R> eval(final CheckedFunction<? super T, ? extends R> function) {
        checkNotNull(function, "function == null");
        return i -> {
            try {
                return function.apply(i);
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link Predicate} which delegates to the underlying {@link CheckedPredicate CheckedPredicate},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param predicate the underlying checked predicate
     * @return a {@link Predicate} which delegates to the underlying {@link CheckedPredicate CheckedPredicate},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Predicate<T> eval(final CheckedPredicate<? super T> predicate) {
        checkNotNull(predicate, "predicate == null");
        return i -> {
            try {
                return predicate.test(i);
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link Runnable} which delegates to the underlying {@link CheckedRunnable CheckedRunnable},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param runnable the underlying checked runnable
     * @return a {@link Runnable} which delegates to the underlying {@link CheckedRunnable CheckedRunnable},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static Runnable eval(final CheckedRunnable runnable) {
        checkNotNull(runnable, "runnable == null");
        return () -> {
            try {
                runnable.run();
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Returns a {@link Supplier} which delegates to the underlying {@link CheckedSupplier CheckedSupplier},
     * {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param supplier the underlying checked supplier
     * @return a {@link Supplier} which delegates to the underlying {@link CheckedSupplier CheckedSupplier},
     *         {@link #unchecked(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Supplier<T> eval(final CheckedSupplier<? extends T> supplier) {
        checkNotNull(supplier, "supplier == null");
        return () -> {
            try {
                return supplier.get();
            } catch (final Exception e) {
                throw unchecked(e);
            }
        };
    }

    /**
     * Propagates the specified {@code Throwable} as if it is always an instance of {@code RuntimeException} without
     * wrapping it in a {@code RuntimeException}.
     * 
     * The main advantage this method has over {@link Throwables#propagate(Throwable)} is not filling up the stack trace
     * with unnecessary bloat that comes from wrapping a checked exception in a {@code RuntimeException}.
     * <p>
     * For example:
     * 
     * <pre>
     * T doSomething() { // does not throw a checked exception
     *     try {
     *         return someMethodThatCouldThrowAnything();
     *     } catch (IKnowWhatToDoWithThisException e) {
     *         ...
     *     } catch (Throwable t) {
     *         throw unchecked(t);
     *     }
     * }
     * </pre>
     * 
     * <b>Warning:</b> This method breaks Java's exception handling idiom and can lead to horrible errors when misused. See
     * <a target="_blank" href="https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html">Unchecked
     * Exceptions — The Controversy</a> for further discussion. If in doubt <b>do not use</b>.
     * 
     * @param t the specified throwable
     * @return the specified throwable
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException unchecked(final Throwable t) throws T {
        throw (T) t;
    }

//    public static void main(String[] args) {
//
//        List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
//        integers.forEach(i -> {
//            System.out.println(50 / i);
//        });
//    }

//    public static void main(String[] str) throws InterruptedException, ExecutionException {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        Future future = executorService.submit(() -> {
//            new ByteArrayInputStream(new byte[] {}).close();
//            return "abc";
//        });
//
//        System.out.println(future.get());
//
//        executorService.shutdown();
//    }

}
