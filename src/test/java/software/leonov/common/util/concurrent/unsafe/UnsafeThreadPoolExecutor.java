package software.leonov.common.util.concurrent.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Throwables;

public final class UnsafeThreadPoolExecutor extends ThreadPoolExecutor {

    private static final int SHUTDOWN;
    private static final Method CHECK_SHUTDOWN_ACCESS;
    private static final Method ADVANCE_RUN_STATE;
    private static final Method INTERRUPT_IDLE_WORKERS;
    private static final Method DRAIN_QUEUE;
    private static final Method ON_SHUTDOWN;
    private static final Method TRY_TERMINATE;

    private final ReentrantLock lock;

    static {
        try {
            final Class<?> clazz = ThreadPoolExecutor.class;
            final Field f = clazz.getDeclaredField("SHUTDOWN");
            f.setAccessible(true); // enables access to private variables
            SHUTDOWN = (int) f.get(null);
            CHECK_SHUTDOWN_ACCESS = clazz.getDeclaredMethod("checkShutdownAccess");
            CHECK_SHUTDOWN_ACCESS.setAccessible(true);
            ADVANCE_RUN_STATE = clazz.getDeclaredMethod("advanceRunState", int.class);
            ADVANCE_RUN_STATE.setAccessible(true);
            INTERRUPT_IDLE_WORKERS = clazz.getDeclaredMethod("interruptIdleWorkers");
            INTERRUPT_IDLE_WORKERS.setAccessible(true);
            DRAIN_QUEUE = clazz.getDeclaredMethod("drainQueue");
            DRAIN_QUEUE.setAccessible(true);
            ON_SHUTDOWN = clazz.getDeclaredMethod("onShutdown");
            ON_SHUTDOWN.setAccessible(true);
            TRY_TERMINATE = clazz.getDeclaredMethod("tryTerminate");
            TRY_TERMINATE.setAccessible(true);
        } catch (final Exception e) {
            throw new Error("failed to access internals of ThreadPoolExecutor.class using reflection", e);
        }
    }

    {
        try {
            final Field f = this.getClass().getSuperclass().getDeclaredField("mainLock");
            f.setAccessible(true);
            lock = (ReentrantLock) f.get(this);
        } catch (final Exception e) {
            throw new Error("failed to access internals of ThreadPoolExecutor.class using reflection", e);
        }
    }

    public UnsafeThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> queue) {
        super(corePoolSize, maxPoolSize, keepAliveTime, unit, queue);
    }

    public UnsafeThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> queue, ThreadFactory factory) {
        super(corePoolSize, maxPoolSize, keepAliveTime, unit, queue, factory);
    }

    public UnsafeThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> queue, RejectedExecutionHandler handler) {
        super(corePoolSize, maxPoolSize, keepAliveTime, unit, queue, handler);
    }

    public UnsafeThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> queue, ThreadFactory factory, RejectedExecutionHandler handler) {
        super(corePoolSize, maxPoolSize, keepAliveTime, unit, queue, factory, handler);
    }

    public static UnsafeThreadPoolExecutor newCachedThreadPool() {
        return new UnsafeThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    public static UnsafeThreadPoolExecutor newCachedThreadPool(final int maxPoolSize) {
        return new UnsafeThreadPoolExecutor(0, maxPoolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    public static UnsafeThreadPoolExecutor newFixedThreadPool(int nthreads) {
        return new UnsafeThreadPoolExecutor(nthreads, nthreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public static UnsafeThreadPoolExecutor newSingleThreadExecutor() {
        return new UnsafeThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Halts the processing of waiting tasks but does not attempt to stop actively executing tasks. All waiting tasks are
     * drained (removed) from the task queue and returned when this method completes.
     * <p>
     * This method does not wait for actively executing tasks to terminate. Use {@link #awaitTermination awaitTermination}
     * to do that.
     * <p>
     * This method is the middle ground between {@link #shutdown()} which will continue executing all previous submitted
     * tasks and {@link #shutdownNow()} which drains the task queue and attempts to interrupt all actively executing tasks
     * via {@link Thread#interrupt()}.
     */
    @SuppressWarnings("unchecked")
    public List<Runnable> shutdownFast() {
        final List<Runnable> tasks;

        try {
            lock.lock();

            try {
                CHECK_SHUTDOWN_ACCESS.invoke(this);
                ADVANCE_RUN_STATE.invoke(this, SHUTDOWN);
                INTERRUPT_IDLE_WORKERS.invoke(this);
                tasks = (List<Runnable>) DRAIN_QUEUE.invoke(this);
                ON_SHUTDOWN.invoke(this);
            } finally {
                lock.unlock();
            }

            TRY_TERMINATE.invoke(this);
        } catch (final InvocationTargetException e) {
            Throwables.throwIfUnchecked(e.getCause());
            throw new Error(e); // cannot happen
        } catch (final Exception other) {
            throw new Error("failed to invoke internals of ThreadPoolExecutor.class via reflection", other);
        }

        return tasks;
    }

    public static void main(String... args) {
        UnsafeThreadPoolExecutor exec = new UnsafeThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        exec.shutdownFast();
        exec.execute(() -> System.out.println("Hello"));
    }

}
