package net.javatoday.common.swing;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * Static utility methods for working with Java {@code Swing}.
 * 
 * @see SwingUtilities
 * @author Zhenya Leonov
 */
final public class Swing {

    private Swing() {
    }

    /**
     * Causes the specified {@code Runnable} be executed synchronously on the
     * <a href= "http://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html" ><i>AWT Event Dispatching
     * Thread</i></a>. If the current thread is the AWT Event Dispatching Thread the {@link Runnable#run()} method is
     * invoked immediately. This method blocks until the specified runnable is finished executing.
     * 
     * @param runnable the specified {@code Runnable}
     * @throws InvocationTargetException if we're interrupted while waiting for the event dispatching thread to finish
     *                                   executing
     * @throws InterruptedException      if an exception is thrown while executing
     * @see SwingUtilities#invokeLater(Runnable)
     */
    public static void edt(final Runnable runnable) throws InvocationTargetException, InterruptedException {
        checkNotNull(runnable, "runnable == null");
        if (!SwingUtilities.isEventDispatchThread())
            SwingUtilities.invokeAndWait(runnable);
        else
            runnable.run();
    }

}
