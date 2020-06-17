package software.leonov.common.util.concurrent;

import java.util.concurrent.ExecutorService;

/**
 * An {@code ExecutorService} which can be paused and resumed.
 * 
 * @author Zhenya Leonov
 */
public interface PausableExecutorService extends ExecutorService {

    /**
     * Attempts to halt the processing of waiting tasks.
     * <p>
     * This method does not have any control over actively executing tasks and returns immediately.
     * 
     * @return {@code true} if successful or {@code false} if a call to {@link ExecutorService#shutdownNow() shutdownNow()}
     *         has been previously made or this {@code ExecutorService} cannot be paused for a different reason
     */
    public boolean pause();

    /**
     * Resumes the processing of waiting tasks. Does nothing if the {@code ExecutorService} is not {@link #isPaused()
     * paused}.
     */
    public void resume();

    /**
     * Returns {@code true} if the {@code ExecutorService} is paused.
     * 
     * @return {@code true} if the {@code ExecutorService} is paused
     */
    public boolean isPaused();

}