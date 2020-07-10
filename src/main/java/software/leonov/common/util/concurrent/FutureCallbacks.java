package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;

import com.google.common.util.concurrent.FutureCallback;

/**
 * Static utility methods pertaining to {@link FutureCallback}s.
 * 
 * @author Zhenya Leonov
 */
public final class FutureCallbacks {

    private FutureCallbacks() {
    }

    /**
     * Returns a {@code FutureCallback} which does nothing {@link FutureCallback#onSuccess(Object) on success} and invokes
     * the specified {@code consumer} if the future {@link FutureCallback#onFailure(Throwable) fails}.
     * <p>
     * Shorthand for creating a {@code FutureCallback} for {@code Runnable} tasks which return nothing.
     * 
     * @param consumer the specified consumer
     * @return a {@code FutureCallback} which does nothing {@link FutureCallback#onSuccess(Object) on success} and invokes
     *         the specified {@code consumer} if the future {@link FutureCallback#onFailure(Throwable) fails}
     */
    public static <V> FutureCallback<V> onFailure(final Consumer<Throwable> consumer) {
        checkNotNull(consumer, "consumer == null");
        return new FutureCallback<V>() {

            @Override
            public void onSuccess(final V result) {
            }

            @Override
            public void onFailure(final Throwable t) {
                consumer.accept(t);
            }
        };
    }

    public static <V> FutureCallback<V> create(final Consumer<V> succeeded, final Consumer<Throwable> failed) {
        checkNotNull(succeeded, "succeeded == null");
        checkNotNull(failed, "failed == null");
        return new FutureCallback<V>() {

            @Override
            public void onSuccess(final V result) {
                succeeded.accept(result);
            }

            @Override
            public void onFailure(final Throwable t) {
                failed.accept(t);
            }
        };
    }
}