package net.javatoday.common.base;

import java.util.function.BiFunction;

/**
 * Determines an output value for two arguments.
 * <p>
 * Note: Java 8+ users consider switching to {@link BiFunction}.
 *
 * @param <T> the type of the first argument
 * @param <U> the type of the second argument
 * @param <R> the type of result
 * @author Zhenya Leonov
 */
public interface BinaryFunction<T, U, R> {

    /**
     * Returns the result of applying this binary function to the specified arguments.
     *
     * @param t the first argument
     * @param u the second argument
     * @return the result of applying this binary function to the specified arguments
     */
    public R apply(T t, U u);

}
