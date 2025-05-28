package software.leonov.common.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;

/**
 * Static utility methods for working with {@link Throwable}s.
 * 
 * @author Zhenya Leonov
 */
public final class Throwing {

    private Throwing() {
    }

    /**
     * {@link Throwable#addSuppressed(Throwable) Appends} all {@code thrown} exceptions to the {@code first} exception.
     * 
     * @param <X>    the type of the {@code first} exception
     * @param first  the first exception
     * @param thrown the exceptions to be suppressed
     * @return the first exception
     */
    public static <X extends Throwable> X suppressAll(final X first, final Iterable<? extends Throwable> thrown) {
        checkNotNull(first, "first == null");
        checkNotNull(thrown, "thrown == null");
        thrown.forEach(first::addSuppressed);
        return first;
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
     *     } catch (final IKnowWhatToDoWithThisException e) {
     *         ...
     *     } catch (final Throwable t) {
     *         throw uncheckedException(t);
     *     }
     * }
     * </pre>
     * 
     * <b>Warning:</b> This method breaks Java's exception handling idiom and can lead to horrible errors when misused. See
     * <a target="_blank" href="https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html">Unchecked
     * Exceptions — The Controversy</a> for further discussion. It is only safe to use if you ensure the caller will catch
     * all possible checked exceptions that could occur. If in doubt <b>do not use</b>.
     * 
     * @param <T> the type of throwable
     * @param t   the specified throwable
     * @return this method does not return - the return type is only for your convenience to make the compiler happy
     * @throws T always
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException uncheckedException(final Throwable t) throws T {
        checkNotNull(t, "t == null");
        throw (T) t;
    }

}
