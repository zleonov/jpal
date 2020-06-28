package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Duration;

public abstract class PausableExecutorNotifier implements Runnable {

    private final long timeout;
    private PausableThreadPoolExecutor exec;

    public PausableExecutorNotifier(final Duration duration) {
        checkNotNull(duration, "duration == null");
        timeout = duration.toMillis();
    }

    public void execute(final PausableThreadPoolExecutor exec) {
        checkNotNull(exec, "exec == null");
        this.exec = exec;
        exec.execute(this);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            exec.lock.lock();
            try {
                if (!exec.isShutdown() || exec.getQueue().size() > 0) {
                    if (cancelRequested()) {
                        exec.shutdownNow();
                        break;
                    } else if (pauseRequested())
                        exec.pause();
                    else if (exec.isPaused())
                        exec.resume();

                    Thread.sleep(timeout);
                }
            } catch (final InterruptedException e) {
                break;
            } finally {
                exec.lock.unlock();
            }
        }
    }

    protected abstract boolean cancelRequested();

    protected abstract boolean pauseRequested();

}