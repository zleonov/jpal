package software.leonov.common.base;

import java.util.function.BiConsumer;

/**
 * Static utility methods pertaining to {@link BiConsumer}s.
 * 
 * @authoer Zhenya Leonov
 */
public final class BiConsumers {

    private static final BiConsumer<?, ?> DO_NOTHING = new BiConsumer<Object, Object>() {

        @Override
        public void accept(Object t, Object u) {
        }
    };

    private BiConsumers() {
    }

    /**
     * Returns a {@code BiConsumer} that accepts everything and does nothing.
     * 
     * @param <T> the type of the first argument to the operation
     * @param <U> the type of the second argument to the operation
     * @return a {@code BiConsumer} that accepts everything and does nothing
     */
    @SuppressWarnings("unchecked")
    public static <T, U> BiConsumer<T, U> doNothing() {
        return (BiConsumer<T, U>) DO_NOTHING;
    }

}