package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.google.common.util.concurrent.FutureCallback;

/**
 * Static utility methods pertaining to {@link Future}s.
 * 
 * @author Zhenya Leonov
 */
public final class MoreFutures {

    private MoreFutures() {
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
}