package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Static utility methods pertaining to {@link FutureCallback}s.
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

    public static <V> FutureCallback<V> newCallback(final Consumer<? super V> succeeded, final Consumer<Throwable> failed) {
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

    /**
     * A convenience method that calls {@link Futures#addCallback(ListenableFuture, FutureCallback, Executor)
     * Futures.addCallback(future, callback, directExecutor())} and returns the specified future for method chaining.
     * <p>
     * <b>Warning</b>: Using {@link MoreExecutors#directExecutor()} may be a dangerous choice if the callback is not fast
     * and lightweight. See {@link ListenableFuture#addListener ListenableFuture.addListener} documentation. Similar
     * overloaded methods were removed from Guava because of this danger.
     * 
     * @param future   the specified future
     * @param callback the callback to register
     * @param executor the executor that will run the callback
     * @return the specified future
     */
    public static <V> ListenableFuture<V> addCallback(final ListenableFuture<V> future, final FutureCallback<? super V> callback) {
        return addCallback(future, callback, MoreExecutors.directExecutor());
    }

    /**
     * A convenience method that calls {@link Futures#addCallback(ListenableFuture, FutureCallback, Executor)} and returns
     * the specified future for method chaining.
     * 
     * @param future   the specified future
     * @param callback the callback to register
     * @param executor the executor that will run the callback
     * @return the specified future
     */
    public static <V> ListenableFuture<V> addCallback(final ListenableFuture<V> future, final FutureCallback<? super V> callback, final Executor executor) {
        checkNotNull(future, "future == null");
        checkNotNull(callback, "callback == null");
        checkNotNull(executor, "executor == null");
        Futures.addCallback(future, callback, executor);
        return future;
    }
}