package software.leonov.common.util;

import java.util.function.Consumer;

import com.google.common.io.Closeables;

/**
 * A utility class for working with {@code AutoCloseable} instances. This class the {@link AutoCloseable} analog to
 * Guava's {@link Closeables} class.
 *
 * @author Zhenya Leonov
 */
public final class AutoCloseables {

    private AutoCloseables() {
    };

    /**
     * Closes an {@link AutoCloseable} resource, with explicit control over how to handle any error that may occur.
     * <p>
     * This is primarily useful when manually cleaning up resources, where a thrown exception needs to be logged,
     * suppressed, or otherwise handled without being propagated.
     * <p>
     * <b>Discussion:</b> Common {@code closeQuietly} implementations generally fall into two categories:
     * </p>
     * <ul>
     * <li>They silently ignore any {@link Exception} that occurs during the closing process. While seemingly convenient,
     * this can mask critical errors, especially when writing to an I/O resource where data loss or corruption might occur
     * without any indication.</li>
     * <li>They log any closing exception using {@code java.util.logging}. This approach is preferable because it
     * acknowledges the error, but it lacks flexibility, as the user has no direct control over the logger configuration or
     * the handling of the logged exception.</li>
     * </ul>
     * <p>
     * In contrast, this method allows the user to provide a {@link Consumer} that will be invoked with any {@link Throwable
     * error} thrown by the {@code close()} method. This enables you to implement custom error logging, reporting, or
     * recovery mechanisms tailored to your specific application needs.
     * 
     * @param resource the {@code AutoCloseable} resource to close or {@code null} in which case this method is a no-op
     * @param consumer the exception handler to use
     * @see Closeables
     */
    public static void closeQuietly(final AutoCloseable resource, final Consumer<Throwable> consumer) {
        if (resource != null)
            try {
                resource.close();
            } catch (final Throwable t) {
                consumer.accept(t);
            }
    }

}
