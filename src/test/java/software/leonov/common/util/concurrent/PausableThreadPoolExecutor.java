package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.Runnables;

/**
 * An implementation of {@code PausableExecutorService} based on a {@code ThreadPoolExecutor}.
 * 
 * @author Zhenya Leonov
 */
public final class PausableThreadPoolExecutor extends ThreadPoolExecutor implements PausableExecutorService {

    final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean paused = false;

    private Runnable pause = Runnables.doNothing();
    private Runnable resume = Runnables.doNothing();
    private Runnable terminate = Runnables.doNothing();

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters and default thread factory and
     * rejected execution handler.
     *
     * @param n the number of threads to keep in the pool
     * @throws IllegalArgumentException if {@code poolSize} < 1
     */
    public PausableThreadPoolExecutor(final int n) {
        super(n, n, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters and default thread factory and
     * rejected execution handler.
     *
     * @param n     the number of threads to keep in the pool
     * @param queue the queue where which will hold waiting tasks
     * @throws IllegalArgumentException if {@code poolSize} < 1
     */
    public PausableThreadPoolExecutor(final int n, final BlockingQueue<Runnable> queue) {
        super(n, n, 0L, TimeUnit.MILLISECONDS, queue, Executors.defaultThreadFactory());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters and default rejected execution
     * handler.
     *
     * @param poolSize the number of threads to keep in the pool
     * @param queue    the queue where which will hold waiting tasks
     * @param factory  the factory to use for creating new threads
     * @throws IllegalArgumentException if {@code poolSize} < 1
     */
    public PausableThreadPoolExecutor(final int poolSize, final BlockingQueue<Runnable> queue, final ThreadFactory factory) {
        super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue, factory);
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given initial parameters.
     *
     * @param n       the number of threads to keep in the pool
     * @param unit    the time {@code TimeUnit} for the {@code keepAliveTime} argument
     * @param queue   the queue where which will hold waiting tasks
     * @param factory the factory to use for creating new threads
     * @param handler the handler to use when execution is blocked because the thread bounds and queue capacities are
     *                reached
     * @throws IllegalArgumentException if {@code poolSize} < 1
     */
    public PausableThreadPoolExecutor(final int n, final BlockingQueue<Runnable> queue, final ThreadFactory factory, final RejectedExecutionHandler handler) {
        super(n, n, 0L, TimeUnit.MILLISECONDS, queue, factory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            if (paused) {
                pause.run();
                while (paused)
                    condition.await();
                resume.run();
            }
        } catch (final InterruptedException ie) {
            t.interrupt();
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        lock.lock();
        try {
            if (isShutdown() && getQueue().isEmpty())
                paused = false;
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

    @Override
    public boolean pause() {
        lock.lock();
        try {
            paused = !isShutdown() || !getQueue().isEmpty();
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
    protected void terminated() {
        super.terminated();
        terminate.run();
    }

    /**
     * Registers a callback function which will be invoked when this {@code PausableThreadPoolExecutor} has
     * {@link #terminated}.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor terminated(final Runnable callback) {
        checkNotNull(callback, "callback = null");
        this.terminate = callback;
        return this;

    }

    /**
     * Registers a callback function which will be invoked right before this {@code PausableThreadPoolExecutor} has
     * {@link #pause() paused}.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor beforePause(final Runnable callback) {
        checkNotNull(callback, "callback = null");
        this.pause = callback;
        return this;
    }

    /**
     * Registers a callback function which will be invoked right before this {@code PausableThreadPoolExecutor} has
     * {@link #resume() resumed}.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor beforeResume(final Runnable callback) {
        checkNotNull(callback, "callback = null");
        this.resume = callback;
        return this;
    }
}
