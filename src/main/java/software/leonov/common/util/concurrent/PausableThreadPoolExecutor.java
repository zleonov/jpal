package software.leonov.common.util.concurrent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class PausableThreadPoolExecutor extends ThreadPoolExecutor implements PausableExecutorService {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private boolean paused = false;
    private boolean shutdownNow = false;
//    private Runnable callback = null;

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters and default thread factory and
     * rejected execution handler.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even if they are idle, unless
     *                        {@link ThreadPoolExecutor#allowCoreThreadTimeOut(boolean) allowCoreThreadTimeOut(true)} is set
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @param keepAliveTime   the maximum time that excess idle threads will wait for new tasks before terminating when the
     *                        number of threads is greater than the {@code corePoolSize}
     * @param unit            the time {@code TimeUnit} for the {@code keepAliveTime} argument
     * @param queue           the queue where which will hold waiting tasks
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> queue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, Executors.defaultThreadFactory());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters and default rejected execution
     * handler.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even if they are idle, unless
     *                        {@link ThreadPoolExecutor#allowCoreThreadTimeOut(boolean) allowCoreThreadTimeOut(true)} is set
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @param keepAliveTime   the maximum time that excess idle threads will wait for new tasks before terminating when the
     *                        number of threads is greater than the {@code corePoolSize}
     * @param unit            the time {@code TimeUnit} for the {@code keepAliveTime} argument
     * @param queue           the queue where which will hold waiting tasks
     * @param factory         the factory to use for creating new threads
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> queue, final ThreadFactory factory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, factory);
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters and default thread factory.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even if they are idle, unless
     *                        {@link ThreadPoolExecutor#allowCoreThreadTimeOut(boolean) allowCoreThreadTimeOut(true)} is set
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @param keepAliveTime   the maximum time that excess idle threads will wait for new tasks before terminating when the
     *                        number of threads is greater than the {@code corePoolSize}
     * @param unit            the time {@code TimeUnit} for the {@code keepAliveTime} argument
     * @param queue           the queue where which will hold waiting tasks
     * @param handler         the handler to use when execution is blocked because the thread bounds and queue capacities
     *                        are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> queue, final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, Executors.defaultThreadFactory(), handler);
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even if they are idle, unless
     *                        {@link ThreadPoolExecutor#allowCoreThreadTimeOut(boolean) allowCoreThreadTimeOut(true)} is set
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @param keepAliveTime   the maximum time that excess idle threads will wait for new tasks before terminating when the
     *                        number of threads is greater than the {@code corePoolSize}
     * @param unit            the time {@code TimeUnit} for the {@code keepAliveTime} argument
     * @param queue           the queue where which will hold waiting tasks
     * @param factory         the factory to use for creating new threads
     * @param handler         the handler to use when execution is blocked because the thread bounds and queue capacities
     *                        are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> queue, final ThreadFactory factory,
            final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, factory, handler);
    }

    /**
     * Returns a new {@code PausableThreadPoolExecutor} that reuses a fixed number of threads operating off a shared
     * unbounded queue.
     * <p>
     * At any point, at most {@code n} threads will be active processing tasks. If additional tasks are submitted when all
     * threads are active, they will wait in the queue until a thread is available. If any thread terminates due to a
     * failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks. The
     * threads in the pool will exist until it is explicitly {@link ExecutorService#shutdown shutdown}.
     *
     * @param n the number of threads in the pool
     * @return the newly created {@code PausableThreadPoolExecutor}
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public static PausableThreadPoolExecutor withFixedThreadPool(int n) {
        return new PausableThreadPoolExecutor(n, n, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

//    /**
//     * Returns a new {@code PausableThreadPoolExecutor} that creates new threads as needed, but will reuse previously
//     * constructed threads when they are available.
//     * <p>
//     * These pools will typically improve the performance of programs that execute many short-lived asynchronous tasks.
//     * Calls to {@code execute} will reuse previously constructed threads if available. If no existing thread is available,
//     * a new thread will be created and added to the pool. Threads that have not been used for sixty seconds are terminated
//     * and removed from the cache. Thus, a pool that remains idle for long enough will not consume any resources. Note that
//     * pools with similar properties but different details (for example, timeout parameters) may be created using the
//     * various constructors.
//     *
//     * @return the newly created new {@code PausableThreadPoolExecutor}
//     */
//    public static PausableThreadPoolExecutor withCachedThreadPool() {
//        return new PausableThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
//    }
//
//    /**
//     * Returns a new {@code PausableThreadPoolExecutor} that uses a single worker thread operating off an unbounded queue.
//     * <p>
//     * Note that if this single thread terminates due to a failure during execution prior to shutdown, a new one will take
//     * its place if needed to execute subsequent tasks.
//     * <p>
//     * Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time.
//     *
//     * @return the newly created new {@code PausableThreadPoolExecutor}
//     */
//    public static PausableThreadPoolExecutor withSingleThread() {
//        return new PausableThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            while (paused)
                condition.await();
        } catch (final InterruptedException ie) {
            t.interrupt();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean pause() {
        lock.lock();
        try {
            if (!isShutdownNow())
                paused = true;
            return paused;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void resume() {
        lock.lock();
        try {
            if (isPaused()) {
                paused = false;
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        lock.lock();
        try {
            shutdownNow = true;
            resume();
            return super.shutdownNow();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isPaused() {
        lock.lock();
        try {
            return paused;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns {@code true} if a call to {@link #shutdownNow()} was previously made. This is a more specific analog of
     * {@link #isShutdown()} which returns {@code true} if a call to either {@link #shutdown()} or {@code shutdownNow()} was
     * made.
     * 
     * @return if a call to {@link #shutdownNow} was previously made
     */
    public boolean isShutdownNow() {
        lock.lock();
        try {
            return shutdownNow;
        } finally {
            lock.unlock();
        }
    }

//    @Override
//    protected void terminated() {
//        super.terminated();
//        if (callback != null)
//            callback.run();
//    }
//
//    /**
//     * Registers a callback function which will be invoked when this {@code PausableThreadPoolExecutor} has terminated.
//     * 
//     * @param callback the callback function
//     * @return this {@code PausableThreadPoolExecutor} instance
//     */
//    public PausableThreadPoolExecutor whenTerminated(final Runnable callback) {
//        checkNotNull(callback);
//        lock.lock();
//        try {
//            this.callback = callback;
//            return this;
//        } finally {
//            lock.unlock();
//        }
//    }

}
