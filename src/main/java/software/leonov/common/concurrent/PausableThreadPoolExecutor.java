package software.leonov.common.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.util.concurrent.Runnables;

import software.leonov.common.base.BiConsumers;
import software.leonov.common.base.Consumers;
import software.leonov.common.collect.Collect;

/**
 * An implementation of {@code PausableExecutorService} based on a <i>fixed</i> {@code ThreadPoolExecutor} with
 * additional convenience methods.
 * <p>
 * <b>Pause/resume functionality</b><br>
 * This executor builds on the <i>extension example</i> provided in the documentation of {@link ThreadPoolExecutor}. The
 * {@link #pause()} and {@link #resume()} methods allow users to halt or resume processing of pending tasks (pausing the
 * executor does not affect actively executing tasks).
 * <p>
 * The executor can be paused at any point unless it is {@link #isShutdown() shutting down} and the {@link #getQueue()
 * work queue} is empty. Note that calling {@link #shutdown()} does not automatically resume the executor. To do that
 * call {@link #shutdownFast()} or {@link #shutdownNow()}.
 * <p>
 * Be careful of race conditions if the pause/resume functionality is used to control program flow. The boolean value
 * returned from calling {@code pause()} and {@link #isPaused()} reflects a transient state which may already be invalid
 * when the call returns if other threads are modifying the state of the executor: for example if another thread calls
 * {@code shutdownNow()}.
 * <p>
 * <b>Callback functions</b><br>
 * The {@link #beforePause(Consumer)}, {@link #afterPause(Consumer)}, {@link #beforeExecute(Consumer)},
 * {@link #afterExecute(BiConsumer)}, and {@link #afterTerminated(Runnable)} methods allow users to register callback
 * functions which will be run when executor is paused/resumed, before/after task execution, and when the executor has
 * terminated, (in that order) respectively. Most commonly used for logging, statistics gathering, or to initialize
 * {@code ThreadLocal} variables.
 * <p>
 * This is a convenient alternative to the idiomatic pre Java 8 style of extending classes for the purpose of overriding
 * empty <i>hook</i> methods.
 * <p>
 * <b>CallerWaitsPolicy for rejected tasks</b><br>
 * The {@code ThreadPoolExecutor} implementation provides 4 <i>saturation policies</i> to handle rejected tasks when a
 * bounded {@link ThreadPoolExecutor#getQueue() work queue} fills up:
 * {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} (default),
 * {@link java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy CallerRunsPolicy},
 * {@link java.util.concurrent.ThreadPoolExecutor.DiscardPolicy DiscardPolicy}, and
 * {@link java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy DiscardOldestPolicy}. There is no predefined
 * saturation policy to make the {@code execute} method block when the work queue is full.
 * <p>
 * {@code PausableThreadPoolExecutor} provides a {@link PausableThreadPoolExecutor.CallerWaitsPolicy CallerWaitsPolicy}
 * which blocks the calling thread until there is an open slot in the {@link ThreadPoolExecutor#getQueue() work queue}
 * and submits the task again. Typically this will be used with a bounded {@link ArrayBlockingQueue}.
 * <p>
 * <b>Convenience methods</b><br>
 * The {@link #shutdownFast()} method is the middle ground between {@link #shutdown() shutdown()} and
 * {@link #shutdownNow()} and the {@link #awaitTermination()} method is a shorthand for
 * {@link #awaitTermination(long, TimeUnit) awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)}.
 * 
 * @author Zhenya Leonov
 */
public final class PausableThreadPoolExecutor extends ThreadPoolExecutor implements PausableExecutorService {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean paused = false;

    private Consumer<Runnable> beforeExecute = Consumers.doNothing();
    private BiConsumer<Runnable, Throwable> afterExecute = BiConsumers.doNothing();
    private Consumer<Runnable> beforePause = Consumers.doNothing();
    private Consumer<Runnable> afterPause = Consumers.doNothing();
    private Runnable afterTerminated = Runnables.doNothing();

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, unbounded work queue,
     * default thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     */
    public PausableThreadPoolExecutor(final int corePoolSize) {
        this(corePoolSize, new LinkedBlockingQueue<>());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * default thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @param queue        the queue to use for holding tasks before they are executed
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final BlockingQueue<Runnable> queue) {
        this(corePoolSize, queue, Executors.defaultThreadFactory());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @param queue        the queue to use for holding tasks before they are executed
     * @param factory      the factory used to create new threads
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final BlockingQueue<Runnable> queue, final ThreadFactory factory) {
        this(corePoolSize, queue, factory, new AbortPolicy());
    }

    /**
     * Creates a new {@code PausableThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * thread factory, and {@code RejectedExecutionHandler}.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @param queue        the queue to use for holding tasks before they are executed
     * @param factory      the factory used to create new threads
     * @param handler      the handler to use when execution is blocked because the thread bounds and queue capacities are
     *                     reached
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final BlockingQueue<Runnable> queue, final ThreadFactory factory, final RejectedExecutionHandler handler) {
        super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, queue, factory, handler);
    }

    @Override
    public void execute(final Runnable command) {
        try {
            super.execute(command);
        } catch (final RejectedExecutionException e) {
            throw e;
        }
    }

    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            if (paused) {
                beforePause.accept(r);
                while (paused)
                    condition.await();
            }
        } catch (final InterruptedException e) {
            t.interrupt();
        } finally {
            afterPause.accept(r);
            lock.unlock();
        }
        beforeExecute.accept(r);
    }

    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        super.afterExecute(r, t);
        lock.lock();
        try {
            if (isShutdown() && getQueue().isEmpty())
                paused = false;
        } finally {
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
    public PausableThreadPoolExecutor beforeExecute(final Consumer<Runnable> callback) {
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
    public PausableThreadPoolExecutor afterExecute(final BiConsumer<Runnable, Throwable> callback) {
        checkNotNull(callback, "callback = null");
        this.afterExecute = callback;
        return this;
    }

    /**
     * Registers a callback function which will be invoked immediately before this {@code PausableThreadPoolExecutor} has
     * {@link #pause() paused}. This callback is invoked by the thread that will be executing the task.
     * 
     * @param callback the callback function
     * @return this {@code PausableThreadPoolExecutor} instance
     */
    public PausableThreadPoolExecutor beforePause(final Consumer<Runnable> callback) {
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
    public PausableThreadPoolExecutor afterPause(final Consumer<Runnable> callback) {
        checkNotNull(callback, "callback = null");
        this.afterPause = callback;
        return this;
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        super.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public void allowCoreThreadTimeOut(boolean value) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * {@inheritDoc}
//     * <p>
//     * The returned queue is a <i>read-only</i> unmodifiable view of the work queue.
//     * 
//     * @return a <i>read-only</i> unmodifiable view of the work queue
//     */
//    @Override
//    public BlockingQueue<Runnable> getQueue() {
//        return queue;
//    }
//
//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public void purge() {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public boolean remove(Runnable task) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public void setCorePoolSize(int corePoolSize) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public void setKeepAliveTime(long time, TimeUnit unit) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public void setMaximumPoolSize(int maximumPoolSize) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * This operation is not supported.
//     * 
//     * @throws UnsupportedOperationException always
//     */
//    @Override
//    public void setThreadFactory(ThreadFactory factory) {
//        throw new UnsupportedOperationException();
//    }

    /**
     * A handler for rejected tasks that blocks the calling thread until there is an open slot in the
     * {@link ThreadPoolExecutor#getQueue() work queue} and submits the task again.
     * <p>
     * This policy will have the same behavior as {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} if
     * the queue is unbounded.
     * 
     * @author Zhenya Leonov
     */
    public static class CallerWaitsPolicy implements RejectedExecutionHandler {

        private final Runnable before;
        private final Runnable after;

        /**
         * Creates a new {@code CallerBlocksPolicy}.
         */
        public CallerWaitsPolicy() {
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
        public CallerWaitsPolicy(final Runnable before, final Runnable after) {
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

}