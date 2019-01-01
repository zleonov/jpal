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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;

import net.javatoday.common.io.Fs;

/**
 * Utility methods for obtaining information about the state of the Java virtual machine.
 * 
 * @author Zhenya Leonov
 */
public final class JVM {

    private JVM() {
    }

    /**
     * Reassigns the "standard" output stream to the given file, using the UTF-8 charset.
     * 
     * @param file   the specified file
     * @param append whether to truncate or append to the given file
     * @throws IOException if an I/O error occurs
     */
    public static void setErr(final File file, final boolean append) throws IOException {
        setErr(file, Charsets.UTF_8, append);
    }

    /**
     * Reassigns the "standard" error stream to the given file, using the specified charset.
     * 
     * @param file    the specified file
     * @param charset the character set to use when writing the lines
     * @param append  whether to truncate or append to the given file
     * @throws IOException if an I/O error occurs
     */
    public static void setErr(final File file, final Charset charset, final boolean append) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");
        final PrintStream ps = Fs.newPrintStream(file, append, charset);
        System.setErr(ps);
    }

    /**
     * Reassigns the "standard" output stream to the given file, using the UTF-8 charset.
     * 
     * @param file   the specified file
     * @param append whether to truncate or append to the given file
     * @throws IOException if an I/O error occurs
     */
    public static void setOut(final File file, final boolean append) throws IOException {
        setOut(file, Charsets.UTF_8, append);
    }

    /**
     * Reassigns the "standard" output stream to the given file, using the specified charset.
     * 
     * @param file    the specified file
     * @param charset the character set to use when writing the lines
     * @param append  whether to truncate or append to the given file
     * @throws IOException if an I/O error occurs
     */
    public static void setOut(final File file, final Charset charset, final boolean append) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");
        final PrintStream ps = Fs.newPrintStream(file, append, charset);
        System.setOut(ps);
    }

    /**
     * Returns the amount of memory this Java virtual machine is currently using.
     * 
     * @return the amount of memory this Java virtual machine is currently using
     */
    public static long usedMemory() {
        final long totalMemory = Runtime.getRuntime().totalMemory();
        final long freeMemory = Runtime.getRuntime().freeMemory();
        return totalMemory - freeMemory;
    }

}
