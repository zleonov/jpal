package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import software.leonov.common.base.Consumers;

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
     * the specified {@code failed} consumer if the future {@link FutureCallback#onFailure(Throwable) fails}.
     * <p>
     * Shorthand for creating a {@code FutureCallback} for {@code Runnable} tasks which return nothing.
     * 
     * @param <V>    the type of value accepted by {@code FutureCallback}
     * @param failed the specified consumer
     * @return a {@code FutureCallback} which does nothing {@link FutureCallback#onSuccess(Object) on success} and invokes
     *         the specified {@code failed} consumer if the future {@link FutureCallback#onFailure(Throwable) fails}
     */
    public static <V> FutureCallback<V> onFailure(final Consumer<Throwable> failed) {
        checkNotNull(failed, "consumer == null");
        return newCallback(Consumers.doNothing(), failed);
    }

    /**
     * Returns a {@code FutureCallback} which invokes the {@code succeeded} and {@code failed} consumers if the future
     * {@link FutureCallback#onSuccess(Object) succeeds} or {@link FutureCallback#onFailure(Throwable) fails} respectively.
     * <p>
     * Shorthand for creating a {@code FutureCallback} using Java's lambda facility.
     * 
     * @param <V>       the type of value passed to the {@code succeeded} consumer
     * @param succeeded the consumer to invoke on success
     * @param failed    the consumer to invoke on failure
     * @return a {@code FutureCallback} which invokes the {@code succeeded} and {@code failed} consumers if the future
     *         {@link FutureCallback#onSuccess(Object) succeeds} or {@link FutureCallback#onFailure(Throwable) fails}
     *         respectively
     */
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
     * <b>Warning:</b> Using {@link MoreExecutors#directExecutor()} may be a dangerous choice if the callback is not fast
     * and lightweight. See {@link ListenableFuture#addListener ListenableFuture.addListener} documentation. Similar
     * overloaded methods were removed from Guava because of this danger.
     * 
     * @param <V>      the type of value produced by the {@code future}
     * @param future   the specified future
     * @param callback the callback to register
     * @return the specified future
     */
    public static <V> ListenableFuture<V> addCallback(final ListenableFuture<V> future, final FutureCallback<? super V> callback) {
        Futures.addCallback(future, callback, MoreExecutors.directExecutor());
        return future;
    }

}