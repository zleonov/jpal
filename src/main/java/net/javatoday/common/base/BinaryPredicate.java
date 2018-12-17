package net.javatoday.common.base;

import java.util.function.BiPredicate;

/**
 * Determines a {@code true} or {@code false} value for two arguments.
 * <p>
 * Note: Java 8+ users consider switching to {@link BiPredicate}.
 * 
 * @param <T> the type of the first argument
 * @param <U> the type of the second argument
 * @author Zhenya Leonov
 */
public interface BinaryPredicate<T, U> {

    /**
     * Returns the result of applying this binary predicate to the specified arguments. This method is <i>generally
     * expected</i>, but not absolutely required to execute without any observable side effects.
     *
     * @param t the first argument
     * @param u the second argument
     * @return the result of applying this binary predicate to the specified arguments
     */
    public boolean apply(T t, U u);

}
