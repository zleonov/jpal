package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Thread.NORM_PRIORITY;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

//    private static class DefaultThreadFactory implements ThreadFactory {
//        private final ThreadFactory defaultThreadFactory;
//        
//        private DefaultThreadFactory() {
//            defaultThreadFactory = Executors.defaultThreadFactory();
//        }        
//
//        @Override
//        public Thread newThread(final Runnable r) {
//            final Thread t = defaultThreadFactory.newThread(r); ThreadFactoryBuilder
//            return new Thread
//        }
//    }
//    
//    private static class ForwardingThread extends Thread {
//        
//    }

    /**
     * Returns a new {@code ThreadFactory} that creates {@link UninterruptibleThread Thread}s which cannot be
     * {@link Thread#interrupt() interrupted}.
     * <p>
     * Otherwise the returned factory behaves identically to {@link Executors#defaultThreadFactory()}.
     * 
     * @return a new {@code ThreadFactory} which creates {@code Thread}s that cannot be {@link Thread#interrupt()
     *         interrupted}
     * @see UninterruptibleThread
     */
    public static ThreadFactory uninterruptibleThreadFactory() {
        return new DefaultUninterruptibleThreadFactory();
    }

    private static class DefaultUninterruptibleThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolCount = new AtomicInteger(1);

        private final AtomicInteger threadCount = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String prefix;

        private DefaultUninterruptibleThreadFactory() {
            final SecurityManager manager = System.getSecurityManager();

            if (manager == null)
                group = Thread.currentThread().getThreadGroup();
            else
                group = manager.getThreadGroup();

            prefix = "uninterruptible-pool-" + poolCount.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new UninterruptibleThread(group, r, prefix + threadCount.getAndIncrement());

            if (t.isDaemon())
                t.setDaemon(false);

            if (t.getPriority() != NORM_PRIORITY)
                t.setPriority(NORM_PRIORITY);

            return t;
        }
    }

}