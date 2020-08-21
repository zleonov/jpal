package software.leonov.concurrent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import com.google.common.math.IntMath;
import com.google.common.util.concurrent.Runnables;

import software.leonov.common.collect.Collect;

/**
 * An implementation of {@code PausableExecutorService} based on a <i>fixed</i> {@code ThreadPoolExecutor} with
 * additional convenience methods.
 * <p>
 * <b>Pause/resume functionality</b><br>
 * This executor builds on the <i>extension example</i> provided in the documentation of {@link ThreadPoolExecutor}. The
 * {@link #pause()} and {@link #resume()} methods allow users to halt or resume processing of pending tasks (note that
 * pausing the executor does not affect actively executing tasks).
 * <p>
 * The executor can be paused at any point unless it is {@link #isShutdown() shutting down} and the {@link #getQueue()
 * work queue} is empty. Note that calling {@link #shutdown()} does not automatically resume the executor. To do that
 * call {@link #shutdownFast()} or {@link #shutdownNow()}.
 * <p>
 * Be careful of race conditions if the pause/resume functionality is used to control program flow. The boolean value
 * returned from calling {@code pause()} and {@code isPaused()} reflects a transient state which may already be invalid
 * when the call returns, if other threads are modifying the state of the executor. For example if another thread calls
 * {@code shutdownNow()}.
 * <p>
 * <b>Maximum number of pending tasks</b><br>
 * To control the rate at which tasks are given to the executor, users can specify the maximum number of pending tasks
 * which can be held in the {@link #getQueue() work queue} via the {@link #PausableThreadPoolExecutor(int, int)} and
 * {@link #PausableThreadPoolExecutor(int, int, ThreadFactory)} constructors. The {@link #execute(Runnable)} method
 * (which is called by all of the {@code submit}/{@code invoke} methods) will block once {@code maxPendingTasks} is
 * reached, preventing the executor from becoming saturated.
 * <p>
 * In essence this functionality offers an alternative {@link RejectedExecutionHandler saturation policy} to the
 * {@link CallerRunsPolicy}. It would be appropriate to call it {@code BlockCallerPolicy} if it existed.
 * <p>
 * <b>Hard coded behavior</b><br>
 * This class was not designed to be extended and is thus marked {@code final}. The executor is created with the
 * following default settings: there is a fixed number of threads, threads do not time out, and if
 * {@code maxPendingTasks} is specified, tasks can only be rejected for one reason: if the executor is
 * {@link #isShutdown() shutting down}.
 * <p>
 * Modifying the behavior of this executor after it is created is not supported. The following functionality has been
 * disabled and will result in an {@code UnsupportedOperationException}:
 * {@link ThreadPoolExecutor#allowCoreThreadTimeOut(boolean) allowCoreThreadTimeOut},
 * {@link ThreadPoolExecutor#setThreadFactory(ThreadFactory) setThreadFactory},
 * {@link ThreadPoolExecutor#setRejectedExecutionHandler(RejectedExecutionHandler) setRejectedExecutionHandler},
 * {@link ThreadPoolExecutor#setMaximumPoolSize(int) setMaximumPoolSize},
 * {@link ThreadPoolExecutor#setKeepAliveTime(long, TimeUnit) setKeepAliveTime},
 * {@link ThreadPoolExecutor#setCorePoolSize(int) setCorePoolSize}, {@link ThreadPoolExecutor#remove(Runnable) remove},
 * {@link ThreadPoolExecutor#purge() purge}. The {@link #getQueue()} method returns a
 * {@link Collect#unmodifiable(BlockingQueue) read-only} view of the queue.
 * <p>
 * <b>Callback functions</b><br>
 * The {@link #beforePause(Runnable)}, {@link #afterPause(Runnable)}, {@link #beforeExecute(BiConsumer)},
 * {@link #afterExecute(BiConsumer)}, and {@link #afterTerminated(Runnable)} methods allow users to register callback
 * functions which will be run when executor is paused/resumed, before/after task execution, and when the executor has
 * terminated, (in that order) respectively. Most commonly used for logging, or to initialize {@code ThreadLocal}
 * variables.
 * <p>
 * <b>Convenience methods</b><br>
 * The {@link #shutdownFast()} method is the middle ground between {@link #shutdown() shutdown()} and
 * {@link #shutdownNow()} and the {@link #awaitTermination()} method is a shorthand for
 * {@link #awaitTermination(long, TimeUnit) awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS}).
 * 
 * @author Zhenya Leonov
 */
public final class PausableThreadPoolExecutor extends ThreadPoolExecutor implements PausableExecutorService {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean paused = false;

    private BiConsumer<Thread, Runnable> beforeExecute = (t, r) -> {
    };

    private BiConsumer<Runnable, Throwable> afterExecute = (r, t) -> {
    };

    private Runnable beforePause = Runnables.doNothing();
    private Runnable afterPause = Runnables.doNothing();
    private Runnable afterTerminated = Runnables.doNothing();

    private final Semaphore semaphore;

    private final BlockingQueue<Runnable> queue;

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, unbounded work queue,
     * default thread factory, and {@link AbortPolicy} handler which only throws {@link RejectedExecutionException}s if the
     * executor is {@link #isShutdown() shutting down}.
     *
     * @param nthreads the number of threads to keep in the pool
     * @throws IllegalArgumentException if {@code nthreads} < 1
     */
    public PausableThreadPoolExecutor(final int nthreads) {
        this(nthreads, new LinkedBlockingQueue<>());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * default thread factory, and {@link AbortPolicy} handler.
     *
     * @param nthreads the number of threads to keep in the pool
     * @param queue    the queue to use for holding tasks before they are executed
     * @throws IllegalArgumentException if {@code nthreads} < 1
     */
    public PausableThreadPoolExecutor(final int nthreads, final BlockingQueue<Runnable> queue) {
        this(nthreads, queue, Executors.defaultThreadFactory());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * thread factory, and {@link AbortPolicy} handler.
     *
     * @param nthreads the number of threads to keep in the pool
     * @param queue    the queue to use for holding tasks before they are executed
     * @param factory  the factory used to create new threads
     * @throws IllegalArgumentException if {@code nthreads} < 1
     */
    public PausableThreadPoolExecutor(final int nthreads, final BlockingQueue<Runnable> queue, final ThreadFactory factory) {
        this(nthreads, queue, factory, new AbortPolicy());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * thread factory, and {@code RejectedExecutionHandler}.
     *
     * @param nthreads the number of threads to keep in the pool
     * @param queue    the queue to use for holding tasks before they are executed
     * @param factory  the factory used to create new threads
     * @param handler  the handler to use when execution is blocked because the thread bounds and queue capacities are
     *                 reached
     * @throws IllegalArgumentException if {@code nthreads} < 1
     */
    public PausableThreadPoolExecutor(final int nthreads, final BlockingQueue<Runnable> queue, final ThreadFactory factory, final RejectedExecutionHandler handler) {
        this(nthreads, MaxSemaphore.getInstance(), queue, factory, handler);
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, a maximum number of tasks
     * that can be placed on the queue before blocking, default thread factory, and {@link AbortPolicy} handler which only
     * throws {@link RejectedExecutionException}s if the executor is {@link #isShutdown() shutting down}.
     *
     * @param nthreads        the number of threads to keep in the pool
     * @param maxPendingTasks the maximum size of the queue used to hold pending tasks
     * @throws IllegalArgumentException if {@code nthreads} < 1 or {@code maxPendingTasks < 1}
     */
    public PausableThreadPoolExecutor(final int nthreads, final int maxPendingTasks) {
        this(nthreads, maxPendingTasks, Executors.defaultThreadFactory());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, a maximum number of tasks
     * that can be placed on the queue before blocking, the specified thread factory, and {@link AbortPolicy} handler which
     * only throws {@link RejectedExecutionException}s if the executor is {@link #isShutdown() shutting down}.
     *
     * @param nthreads        the number of threads to keep in the pool
     * @param maxPendingTasks the maximum size of the queue used to hold pending tasks
     * @param factory         the factory used to create new threads
     * @throws IllegalArgumentException if {@code nthreads} < 1 or {@code maxPendingTasks < 1}
     */
    public PausableThreadPoolExecutor(final int nthreads, final int maxPendingTasks, final ThreadFactory factory) {
        this(nthreads, new Semaphore(IntMath.saturatedAdd(nthreads, maxPendingTasks), true), new LinkedBlockingQueue<>(), factory, new AbortPolicy());
        checkArgument(maxPendingTasks > 0, "maxPendingTasks < 1");
    }

    private PausableThreadPoolExecutor(final int nthreads, final Semaphore semaphore, final BlockingQueue<Runnable> queue, final ThreadFactory factory, final RejectedExecutionHandler handler) {
        super(nthreads, nthreads, 0L, TimeUnit.MILLISECONDS, queue, factory, handler);
        this.semaphore = semaphore;
        this.queue = semaphore instanceof MaxSemaphore ? queue : Collect.unmodifiable(queue);
    }

    @Override
    public void execute(final Runnable command) {
        semaphore.acquireUninterruptibly();
        try {
            super.execute(command);
        } catch (final RejectedExecutionException e) {
            semaphore.release();
            throw e;
        }
    }

    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            if (paused) {
                beforePause.run();
                while (paused)
                    condition.await();
            }
        } catch (final InterruptedException e) {
            t.interrupt();
        } finally {
            afterPause.run();
            lock.unlock();
        }
        beforeExecute.accept(t, r);
    }

    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        super.afterExecute(r, t);
        lock.lock();
        try {
            if (isShutdown() && getQueue().isEmpty())
                paused = false;
        } finally {
            semaphore.release();
            lock.unlock();
        }
        afterExecute.accept(r, t);
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
    public List<Runnable> shutdownFast() {
        lock.lock();
        try {
            super.shutdown();
            final List<Runnable> tasks = new ArrayList<>(getQueue().size());
            Collect.drainFully(getQueue(), tasks);
            resume();
            return tasks;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public List<Runnable> shutdownNow() {
        lock.lock();
        try {
            final List<Runnable> tasks = super.shutdownNow();
            resume();
            return tasks;
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void terminated() {
        super.terminated();
        afterTerminated.run();
    }

    /**
     * Registers a callback function which will be invoked when this {@code PausableThreadPoolExecutor} has
     * {@link #terminated}.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor afterTerminated(final Runnable callback) {
        checkNotNull(callback, "callback = null");
        this.afterTerminated = callback;
        return this;

    }

    /**
     * Registers a callback function which will be invoked prior to executing the task in the given {@code Thread}. This
     * callback is invoked by the thread that executed the task and may be used to reinitialize {@code ThreadLocal}s or to
     * perform logging.
     * <p>
     * Performs an equivalent function as overriding of {@link ThreadPoolExecutor#beforeExecute(Thread, Runnable)} in
     * extending classes.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor beforeExecute(final BiConsumer<Thread, Runnable> callback) {
        checkNotNull(callback, "callback = null");
        this.beforeExecute = callback;
        return this;
    }

    /**
     * Registers a callback function which will be invoked upon completion the task. This callback is invoked by the thread
     * that executed the task. If non-null, the {@code Throwable} is the uncaught {@code RuntimeException} or {@code Error}
     * that caused execution to terminate abruptly.
     * <p>
     * Performs an equivalent function as overriding of {@link ThreadPoolExecutor#afterExecute(Runnable, Throwable)} in
     * extending classes. See the {@code afterExecute} documentation for notes on differentiating {@code FutureTask}s from
     * generic {@code Runnable}s.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor afterExecute(final BiConsumer<Thread, Runnable> callback) {
        checkNotNull(callback, "callback = null");
        this.beforeExecute = callback;
        return this;
    }

    /**
     * Registers a callback function which will be invoked immediately before this {@code PausableThreadPoolExecutor} has
     * {@link #pause() paused}. This callback is invoked by the thread that will be executing the task.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor beforePause(final Runnable callback) {
        checkNotNull(callback, "callback = null");
        this.beforePause = callback;
        return this;
    }

    /**
     * Registers a callback function which will be invoked immediately after this {@code PausableThreadPoolExecutor} has
     * {@link #resume() resumed}. This callback is invoked by the thread that will be executing the task.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor afterPause(final Runnable callback) {
        checkNotNull(callback, "callback = null");
        this.afterPause = callback;
        return this;
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        super.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void allowCoreThreadTimeOut(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned queue is a <i>read-only</i> unmodifiable view of the work queue.
     * 
     * @return a <i>read-only</i> unmodifiable view of the work queue
     */
    @Override
    public BlockingQueue<Runnable> getQueue() {
        return queue;
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void purge() {
        throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean remove(Runnable task) {
        throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setCorePoolSize(int corePoolSize) {
        throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setKeepAliveTime(long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public void setThreadFactory(ThreadFactory factory) {
        throw new UnsupportedOperationException();
    }

}