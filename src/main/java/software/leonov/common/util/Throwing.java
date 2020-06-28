package software.leonov.common.util;

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
        thrown.forEach(first::addSuppressed);
        return first;
    }

}
