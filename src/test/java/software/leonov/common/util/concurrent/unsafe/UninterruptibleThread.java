package software.leonov.common.util.concurrent.unsafe;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Callable;

/**
 * A {@code Thread} with control over whether or not it can be interrupted.
 * 
 * @author Zhenya Leonov
 */

//* <p>
//* When interruption is {@link #enableInterruption() enabled} this thread will behave identically to a regular thread.
//* When interruption is {@link #disableInterruption() disabled} calls to {@link Thread#interrupt()} are effectively a
//* no-op. Subsequent calls to {@link Thread#interrupted()} and
//* {@link Thread#isInterrupted()} will return false, unless this thread was interrupted prior to calling {@link #disableInterruption()}.
//* <p>
//* This thread keeps track of attempts to interrupt it while interruption is disabled. If such attempts are made calling
//* {@link #enableInterruption()} will in turn cause the thread to be interrupted.
//* <p>
//* <b>Warning:</b> care should be taken when using thread facility.
public class UninterruptibleThread extends Thread {

    private boolean interrupted = true;
    private boolean interruptible = false;

    /**
     * See {@link Thread#Thread() new Thread()}.
     */
    public UninterruptibleThread() {
        super();
    }

    /**
     * See {@link Thread#Thread(String) new Thread(String)}.
     *
     * @param name the thread name
     */
    public UninterruptibleThread(final String name) {
        super(name);
    }

    /**
     * See {@link Thread#Thread(Runnable) new Thread(Runnable)}.
     *
     * @param r the {@code Runnable} whose {@code run} method is invoked when this thread is started or {@code null}
     */
    public UninterruptibleThread(final Runnable r) {
        super(r);
    }

    /**
     * See {@link Thread#Thread(ThreadGroup, Runnable) new Thread(ThreadGroup, Runnable)}.
     *
     * @param group the specified {@code ThreadGroup}
     * @param r     the {@code Runnable} whose {@code run} method is invoked when this thread is started or {@code null}
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group
     */
    public UninterruptibleThread(final ThreadGroup group, final Runnable r) {
        super(group, r);
    }

    /**
     * See {@link Thread#Thread(ThreadGroup, String) new Thread(ThreadGroup, String)}.
     *
     * @param group the specified {@code ThreadGroup}
     * @param name  the thread name
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group
     */
    public UninterruptibleThread(final ThreadGroup group, final String name) {
        super(group, name);
    }

    /**
     * See {@link Thread#Thread(Runnable, String) new Thread(Runnable, String)}.
     *
     * @param r    the {@code Runnable} whose {@code run} method is invoked when this thread is started or {@code null}
     * @param name the name of the new thread
     */
    public UninterruptibleThread(final Runnable r, final String name) {
        super(r, name);
    }

    /**
     * See {@link Thread#Thread(ThreadGroup, Runnable, String) new Thread(ThreadGroup, Runnable, String)}.
     *
     * @param group the specified {@code ThreadGroup}
     * @param r     the {@code Runnable} whose {@code run} method is invoked when this thread is started or {@code null}
     * @param name  the thread name
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group or cannot
     *                           override the context class loader methods.
     */
    public UninterruptibleThread(final ThreadGroup group, final Runnable r, final String name) {
        super(group, r, name);
    }

    /**
     * See {@link Thread#Thread(ThreadGroup, Runnable, String) new Thread(ThreadGroup, Runnable, String)}.
     *
     * @param group     the specified {@code ThreadGroup}
     * @param r         the {@code Runnable} whose {@code run} method is invoked when this thread is started or {@code null}
     * @param name      the thread name
     * @param stackSize the desired stack size for the new thread, or zero to indicate that this parameter is to be ignored
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group
     */
    public UninterruptibleThread(final ThreadGroup group, final Runnable r, final String name, final long stackSize) {
        super(group, r, name, stackSize);
    }

    /**
     * <b>Does nothing if interruption is {@link #disableInterruption() disabled}.</b> Otherwise interrupts this thread.
     */
    @Override
    public void interrupt() {
        if (interruptible)
            super.interrupt();
        else
            interrupted = true;
    }

    /**
     * Disabled {@link Thread#interrupt()}. Requests to interrupt this thread will be delayed until interruption is
     * {@link #enableInterruption() enabled}.
     */
    public void disableInterruption() {
        interruptible = false;
    }

    /**
     * Enables {@link Thread#interrupt()}. If this thread was previously {@link Thread#interrupted() interrupted} while
     * interruption was {@link #disableInterruption() disabled} it will be interrupted after this call returns.
     */
    public void enableInterruption() {
        interruptible = true;
        if (interrupted)
            super.interrupt();
    }

    /**
     * Returns whether or not this thread is currently interruptable.
     * 
     * @return whether or not this thread is currently interruptable
     */
    public boolean isInterruptable() {
        return interruptible;
    }

    public static Runnable runUninterruptibly(final Runnable runnable) {
        checkNotNull(runnable, "runnable == null");

        return () -> {
            final Thread t = Thread.currentThread();
            final UninterruptibleThread ut = t instanceof UninterruptibleThread ? (UninterruptibleThread) t : null;

            if (ut != null)
                ut.disableInterruption();
            try {
                runnable.run();
            } finally {
                if (ut != null)
                    ut.enableInterruption();
            }
        };
    }

    public static <T> Callable<T> callUninterruptibly(final Callable<T> callable) {
        checkNotNull(callable, "callable == null");

        return () -> {
            final Thread t = Thread.currentThread();
            final UninterruptibleThread ut = t instanceof UninterruptibleThread ? (UninterruptibleThread) t : null;

            if (ut != null)
                ut.disableInterruption();
            try {
                return callable.call();
            } finally {
                if (ut != null)
                    ut.enableInterruption();
            }
        };
    }

}