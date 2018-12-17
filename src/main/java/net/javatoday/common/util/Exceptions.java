package net.javatoday.common.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;

/**
 * Static utility methods for working with {@link Throwable}s.
 * 
 * @author Zhenya Leonov
 */
public final class Exceptions {

    private Exceptions() {
    }

    /**
     * Propagates the specified {@code Throwable} as if it is always an instance of {@code RuntimeException} without
     * wrapping it in a {@code RuntimeException}.
     * <p>
     * The main advantage this method has over {@link Throwables#propagate(Throwable)} is not filling up the stack trace
     * with unnecessary bloat that comes from wrapping a checked exception in a {@code RuntimeException}.
     * <p>
     * For example:
     * 
     * <pre>
     * T doSomething() {
     *     try {
     *         return someMethodThatCouldThrowAnything();
     *     } catch (IKnowWhatToDoWithThisException e) {
     *         ...
     *     } catch (Throwable t) {
     *         throw Exceptions.throwUnchecked(t);
     *     }
     * }
     * </pre>
     * 
     * <b>Warning:</b> This method is provided for testing and convenience only. It breaks Java's exception handling
     * paradigm and can lead to horrible errors when misused. See
     * <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html">Unchecked Exceptions — The
     * Controversy</a> for further discussion.
     * 
     * @param t the specified throwable
     * @return the specified throwable
     */
    public static RuntimeException throwUnchecked(final Throwable t) {
        checkNotNull(t, "t == null");

        Exceptions.<RuntimeException>_throw_(t);

        // Must throw an exception satisfy the Java compiler error: This method must return a result of type RuntimeException
        throw new AssertionError();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void _throw_(final Throwable t) throws T {
        checkNotNull(t, "t == null");

        // This is a no-op because of type erasure
        throw (T) t;
    }

}
