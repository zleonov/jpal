/*
 * Copyright (C) 2019 Zhenya Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
