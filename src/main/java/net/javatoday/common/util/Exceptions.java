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
package net.javatoday.common.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;

/**
 * Static utility methods for working with {@link Throwable}s.
 * 
 * @author Zhenya Leonov
 */
public final class Exceptions {

    private Exceptions() {
    }

    /**
     * Propagates the specified {@code Throwable} as if it is always an instance of {@code RuntimeException} without
     * wrapping it in a {@code RuntimeException}.
     * <p>
     * The main advantage this method has over {@link Throwables#propagate(Throwable)} is not filling up the stack trace
     * with unnecessary bloat that comes from wrapping a checked exception in a {@code RuntimeException}.
     * <p>
     * For example:
     * 
     * <pre>
     * T doSomething() {
     *     try {
     *         return someMethodThatCouldThrowAnything();
     *     } catch (IKnowWhatToDoWithThisException e) {
     *         ...
     *     } catch (Throwable t) {
     *         throw Exceptions.throwUnchecked(t);
     *     }
     * }
     * </pre>
     * 
     * <b>Warning:</b> This method breaks Java's exception handling idiom and can lead to horrible errors when misused. See
     * <a target="_blank" href="https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html">Unchecked Exceptions — The
     * Controversy</a> for further discussion. If in doubt <b>do not use</b>.
     * 
     * @param t the specified throwable
     * @return the specified throwable
     */
    public static RuntimeException throwUnchecked(final Throwable t) {
       throw Exceptions.<RuntimeException>_throw_(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException _throw_(final Throwable t) throws T {
        checkNotNull(t, "t == null");

        // This is a no-op because of type erasure
        throw (T) t;
    }
    
//    public static void main(String[] args) {
//        throw throwUnchecked(new IOException());
//    }

}
