package software.leonov.common.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagateIfPossible;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.io.Closer;

/**
 * An {@link AutoCloseable} that collects {@code AutoCloseable} resources and closes them all when it is {@link #close
 * closed}. This class is the {@code AutoCloseable} analog to Guava's {@link Closer} class. See Guava's
 * <a href="https://github.com/google/guava/issues/3450" target="_blank">Issue 3450</a>,
 * <a href="https://github.com/google/guava/issues/3068" target="_blank">Issue 3068</a>, and
 * <a href="https://github.com/google/guava/issues/1020" target="_blank">Issue 1020</a> for further discussion.
 * <p>
 * {@code AutoCloseable} class has two main use cases:
 * <ul>
 * <li>When the number of {@code AutoCloseable} resources is not known until runtime (e.g. the resources are user
 * supplied).</li>
 * <li>When properly closing all {@code AutoCloseable} resources requires nested
 * <a href="http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html" target=
 * "_blank">try-with-resources</a> blocks which make the code too cumbersome.</li>
 * </ul>
 * <p>
 * This class is intended to be used in the following pattern:
 * <p>
 * Java 6 style:
 * 
 * <pre>
 * final AutoCloser closer = new AutoCloser();
 * try {
 *     final InputStream in = closer.register(...);
 *     final OutputStream out = closer.register(...);
 *     ...
 * } catch (final Throwable t) {
 *     throw closer.rethrow(t);
 * } finally {
 *     closer.close();
 * }
 * </pre>
 * <p>
 * 
 * Java 7+ try-with-resources style:
 * 
 * <pre>
 * try (final AutoCloser closer = new AutoCloser()) {
 *     final InputStream in = closer.register(...);
 *     final OutputStream out = closer.register(...);
 *     ...
 * }
 * </pre>
 *
 * @author Zhenya Leonov
 */
public final class AutoCloser implements AutoCloseable {

    private final Deque<AutoCloseable> stack = new ArrayDeque<>(4);

    private Throwable thrown;

    /**
     * Registers the given {@code AutoCloseable} resource to be closed when this {@code AutoCloser} is {@link #close
     * closed}.
     *
     * @param <C>      the type of {@code AutoCloseable}
     * @param resource the given {@code AutoCloseable} resource
     * @return the given {@code AutoCloseable} resource
     */
    public <C extends AutoCloseable> C register(final C resource) {
        if (resource != null)
            stack.push(resource);
        return resource;
    }

    /**
     * Stores the given throwable and rethrows it <i>as is</i> if it is an {@code Exception}, {@code RuntimeException} or
     * {@code Error}. In the unlikely possibility that it is a {@code Throwable} instance, it will be rethrown wrapped in a
     * {@code RuntimeException}.
     * <p>
     * This method always throws and as such should be called as {@code throw closer.rethrow(e)} to ensure the compiler
     * knows that it will throw.
     *
     * @param th the given throwable
     * @return this method always throws and as such should be called as {@code throw closer.rethrow(e)}
     * @throws Exception
     */
    public RuntimeException rethrow(final Throwable th) throws Exception {
        checkNotNull(th, "th == null");
        thrown = th;
        propagateIfPossible(th, Exception.class); // throws Exception or RuntimeException or Error
        throw new RuntimeException(th); // this should never happen unless we actually caught a Throwable instance
    }

    @Override
    public void close() throws Exception {
        Throwable th = thrown;

        while (!stack.isEmpty())
            try {
                stack.pop().close();
            } catch (final Throwable e) {
                if (th == null)
                    th = e;
                else
                    th.addSuppressed(e);
            }

        if (thrown == null && th != null)
            propagateIfPossible(th, Exception.class); // throws Exception or RuntimeException or Error
    }

}
