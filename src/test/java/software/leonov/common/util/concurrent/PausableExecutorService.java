package software.leonov.common.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * An {@code ExecutorService} which can be paused and resumed.
 * 
 * @author Zhenya Leonov
 */
public interface PausableExecutorService extends ExecutorService {

    /**
     * Attempts to halt the processing of {@link ThreadPoolExecutor#getQueue() waiting} tasks.
     * <p>
     * This method is <i>weakly consistent</i>. After returning {@code true} it is possible that concurrent operations
     * immediately {@link #resume() resumed} the {@code ExecutorService}.
     * 
     * @return {@code true} if the successful or {@code false} if the {@code ExecutorService} is terminated or cannot be
     *         paused for a different reason
     */
    public boolean pause();

    /**
     * Resumes the processing of waiting tasks. Does nothing if the {@code ExecutorService} is not {@link #isPaused()
     * paused}.
     */
    public void resume();

    /**
     * Returns {@code true} if the {@code ExecutorService} is paused.
     * <p>
     * If a previous call to {@link #pause()} returned {@code true} and {@link #resume()} has not been called, that implies
     * but does not guarantee that this method will return {@code true}. See the <i>weakly consistent</i> behavior of
     * {@link #pause()} for more information.
     * 
     * @return {@code true} if the {@code ExecutorService} is paused
     */
    public boolean isPaused();

}