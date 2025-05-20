package software.leonov.common.base;

import java.util.function.Consumer;

/**
 * Static utility methods pertaining to {@link Consumer}s.
 * 
 * @authoer Zhenya Leonov
 */
public final class Consumers {

    private static final Consumer<?> DO_NOTHING = new Consumer<Object>() {

        @Override
        public void accept(Object t) {
        }
    };

    private Consumers() {
    }

    /**
     * Returns a {@code Consumer} that accepts everything and does nothing.
     * 
     * @param <T> the type of input to the operation
     * @return a {@code Consumer} that accepts everything and does nothing
     */
    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> doNothing() {
        return (Consumer<T>) DO_NOTHING;
    }

}
