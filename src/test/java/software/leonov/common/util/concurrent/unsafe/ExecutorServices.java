package software.leonov.common.util.concurrent.unsafe;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Thread.NORM_PRIORITY;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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
     * Returns a new {@code ThreadFactory} that creates {@link UninterruptibleThread}s.
     * <p>
     * Otherwise the returned factory behaves identically to {@link Executors#defaultThreadFactory()}.
     * 
     * @return a new {@code ThreadFactory} which creates {@link UninterruptibleThread}s
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
        public Thread newThread(final Runnable runnable) {
            checkNotNull(runnable, "runnable == null");
            final Thread t = new UninterruptibleThread(group, runnable, prefix + threadCount.getAndIncrement());

            if (t.isDaemon())
                t.setDaemon(false);

            if (t.getPriority() != NORM_PRIORITY)
                t.setPriority(NORM_PRIORITY);

            return t;
        }
    }

}