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
import java.util.concurrent.Callable;
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
 * The main advantage methods in this class have has over {@link Throwables#propagate(Throwable)} is not filling up the
 * stack trace with unnecessary bloat that comes from wrapping a checked exception in a {@code RuntimeException}.
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
 * <b>Warning:</b> This class breaks Java's exception handling idiom and can lead to horrible errors when misused. See
 * <a target="_blank" href="https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html">Unchecked
 * Exceptions — The Controversy</a> for further discussion. If in doubt <b>do not use</b>.
 * 
 * @author Zhenya Leonov
 */
public final class Unchecked {

    private Unchecked() {
    }

    /**
     * Propagates the specified {@code Throwable} as if it is always an instance of {@code RuntimeException} without
     * wrapping it in a {@code RuntimeException}.
     * 
     * @param t the specified throwable
     * @return the specified throwable
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException uncheckedException(final Throwable t) throws T {
        throw (T) t;
    }

    /**
     * Returns a callable which uses the specified callable's {@link Callable#call() call()} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param callable the specified callable
     * @return a callable which uses the specified callable's {@link Callable#call() call()} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <V> Callable<V> call(final Callable<? extends V> callable) {
        checkNotNull(callable, "callable == null");
        return () -> {
            try {
                return callable.call();
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Invokes the {@link Runnable#run() run()} method of the specified runnable, {@link #uncheckedException(Throwable)
     * rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param runnable the specified runnable
     */
    public static Runnable run(final _Runnable runnable) {
        checkNotNull(runnable, "runnable == null");
        return () -> {
            try {
                runnable.run();
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }
    
    public static interface _Runnable {        
        void run() throws Exception;        
    }
    
    public static interface _Supplier<T> {
        T get() throws Exception;
    }

    /**
     * Returns a consumer which uses the specified consumer's {@link Consumer#accept(Object) accept(T)} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param consumer the specified consumer
     * @return a consumer which uses the specified consumer's {@link Consumer#accept(Object) accept(T)} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Consumer<T> accept(final Consumer<? super T> consumer) {
        checkNotNull(consumer, "consumer == null");
        return i -> {
            try {
                consumer.accept(i);
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Returns a consumer which uses the specified consumer's {@link BiConsumer#accept(Object, Object) accept(T, T)} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param consumer the specified consumer
     * @return a consumer which uses the specified consumer's {@link BiConsumer#accept(Object, Object) accept(T, T)} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T, U> BiConsumer<T, U> accept(final BiConsumer<? super T, ? super U> consumer) {
        checkNotNull(consumer, "consumer == null");
        return (i, j) -> {
            try {
                consumer.accept(i, j);
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Returns function which uses the specified function's {@link Function#apply(Object) apply(T)} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param function the specified function
     * @return function which uses the specified function's {@link Function#apply(Object) apply(T)} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T, R> Function<T, R> apply(final Function<? super T, ? extends R> function) {
        checkNotNull(function, "function == null");
        return i -> {
            try {
                return function.apply(i);
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Returns a function which uses the specified function's {@link BiFunction#apply(Object, Object) apply(T, T)} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param function the specified function
     * @return a function which uses the specified function's {@link BiFunction#apply(Object, Object) apply(T, T)} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T, U, R> BiFunction<T, U, R> apply(final BiFunction<? super T, ? super U, ? extends R> function) {
        checkNotNull(function, "function == null");
        return (i, j) -> {
            try {
                return function.apply(i, j);
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Returns a predicate which uses the specified predicate's {@link Predicate#test(Object) test(T)} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param predicate the specified predicate
     * @return a predicate which uses the specified predicate's {@link Predicate#test(Object) test(T)} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Predicate<T> test(final Predicate<? super T> predicate) {
        checkNotNull(predicate, "predicate == null");
        return i -> {
            try {
                return predicate.test(i);
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Returns a predicate which uses the specified predicate's {@link BiPredicate#test(Object, Object) test(T, T)} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param predicate the specified predicate
     * @return a predicate which uses the specified predicate's {@link BiPredicate#test(Object, Object) test(T, T)} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T, U> BiPredicate<T, U> test(final BiPredicate<? super T, ? super U> predicate) {
        checkNotNull(predicate, "predicate == null");
        return (i, j) -> {
            try {
                return predicate.test(i, j);
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Returns a supplier which uses the specified supplier's {@link Supplier#get() get()} method,
     * {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param supplier the specified supplier
     * @return a supplier which uses the specified supplier's {@link Supplier#get() get()} method,
     *         {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Supplier<T> get(final _Supplier<? extends T> supplier) {
        checkNotNull(supplier, "supplier == null");
        return () -> {
            try {
                return supplier.get();
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

    /**
     * Returns a comparator which uses the specified comparator's {@link Comparator#compare(Object, Object) compare(T, T)}
     * method, {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked.
     * 
     * @param comparator the specified comparator
     * @return a comparator which uses the specified comparator's {@link Comparator#compare(Object, Object) compare(T, T)}
     *         method, {@link #uncheckedException(Throwable) rethrowing} any checked exceptions as if they were unchecked
     */
    public static <T> Comparator<T> compare(final Comparator<? super T> comparator) {
        checkNotNull(comparator, "comparator == null");
        return (left, right) -> {
            try {
                return comparator.compare(left, right);
            } catch (final Throwable t) {
                throw uncheckedException(t);
            }
        };
    }

//    public static void main(String[] args) {
//
//        List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
//        integers.forEach(accept(i -> {
//            System.out.println(50 / i);
//        }));
//    }

}
