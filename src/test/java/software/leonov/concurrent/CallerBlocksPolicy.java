package software.leonov.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.util.concurrent.Runnables;

/**
 * A {@code RejectedExecutionHandler} which prevents the executor from becoming saturated by waiting to submit the task
 * until there is space in the work queue {@link ThreadPoolExecutor#getQueue() work queue}.
 * <p>
 * This policy will have the same behavior as {@link ThreadPoolExecutor#AbortPolicy} if the queue is unbounded.
 * 
 * @author Zhenya Leonov
 */
public final class CallerBlocksPolicy implements RejectedExecutionHandler {

    private final Runnable before;
    private final Runnable after;

    /**
     * Creates a new {@code CallerBlocksPolicy}.
     */
    public CallerBlocksPolicy() {
        this(Runnables.doNothing(), Runnables.doNothing());
    }

    /**
     * Creates a new {@code CallerBlocksPolicy}.
     * 
     * @param before the runnable to run immediately before waiting for space to become available in the work queue (usually
     *               used for debugging and logging)
     * @param after  the runnable to run immediately after the task has been inserted into the work queue (usually used for
     *               debugging and logging)
     */
    public CallerBlocksPolicy(final Runnable before, final Runnable after) {
        checkNotNull(before, "before == null");
        checkNotNull(after, "after == null");
        this.before = before;
        this.after = after;
    }

    @Override
    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
        if (executor.isShutdown())
            throw new RejectedExecutionException();

        try {
            before.run();
            executor.getQueue().put(r);
            after.run();
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RejectedExecutionException(ie);
        }

    }

}
