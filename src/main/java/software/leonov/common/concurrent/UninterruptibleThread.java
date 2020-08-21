package software.leonov.common.util.concurrent;

/**
 * A {@code Thread} which cannot be interrupted. The {@link Thread#interrupt()} is a no-op.
 * 
 * @author Zhenya Leonov
 */
public class UninterruptibleThread extends Thread {

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
     * <b>Does nothing.</b> This thread cannot be interrupted.
     */
    @Override
    public void interrupt() {
        if (interruptible)
            super.interrupt();
    }

    public static abstract class UninterruptableRunnable implements Runnable {

        @Override
        final public void run() {
            try {
                runUninterruptibly();
            } finally {
                final Thread t = Thread.currentThread();
                if (t instanceof UninterruptibleThread)
                    ((UninterruptibleThread) t).interruptible = true;
            }
        }

        public abstract void runUninterruptibly();
    }

}
