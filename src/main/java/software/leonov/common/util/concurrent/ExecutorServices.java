package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Static utility methods for {@link ExecutorService}s.
 * 
 * @author Zhenya Leonov
 */
public final class ExecutorServices {

    private ExecutorServices() {
    }

    /**
     * Waits for the specified {@code ExecutorService} to terminate after a {@link ExecutorService#shutdown() shutdown()} or
     * {@link ExecutorService#shutdownNow() shutdownNow()} request.
     * <p>
     * <b>Warning:</b> This method will block forever until the {@code ExecutorService} terminates. If active tasks are
     * deadlocked this thread must be interrupted.
     * 
     * @param exec the specified {@code ExecutorService}
     * @throws InterruptedException if interrupted while waiting
     */
    public static void awaitTermination(final ExecutorService exec) throws InterruptedException {
        checkNotNull(exec, "exec == null");
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    /**
     * Attempts to {@link ExecutorService#shutdown() shutdown} the specified {@code ExecutorService} and
     * {@link #awaitTermination(ExecutorService) waits} for it to terminate.
     * <p>
     * <b>Warning:</b> This method will block forever until the {@code ExecutorService} terminates. If active tasks are
     * deadlocked this thread must be interrupted.
     * 
     * @param exec the specified {@code ExecutorService}
     * @throws InterruptedException if interrupted while waiting
     */
    public static void ensureShutdown(final ExecutorService exec) throws InterruptedException {
        checkNotNull(exec, "exec == null");
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

}