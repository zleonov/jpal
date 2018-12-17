/*
 * Copyright (C) 2012 Zhenya Leonov
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

package net.javatoday.common.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;

/**
 * An implementation of this interface is provided to the {@link Fs#walkFileTree(File, FileWalker) walkFileTree}
 * methods.
 * <p>
 * This interface is modeled after the
 * <a href= "http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileVisitor.html" target="_blank">FileVisitor</a>
 * interface available in Java 7.
 * <p>
 * <b>Warning:</b> The {@code java.io.File class} provides no platform-independent way to detect symbolic links, as such
 * there is no way to ensure a symbolic link to a directory is not followed when traversing a file tree. In the presence
 * of symbolic links, you may encounter files outside the starting directory, or even end up in an infinite loop.
 * <p>
 * <b>Note:</b> While this class is not deprecated, Java 7+ users are highly encouraged to switch to {@link FileVisitor}
 * in the <a href="https://www.oracle.com/technetwork/articles/javase/nio-139333.html">The Java NIO.2 File System
 * introduced in JDK 7</a>.
 * 
 * @author Zhenya Leonov
 * 
 * @param <R> the type of result returned by this {@code FileWalker}
 */
public interface FileWalker<R> {

    /**
     * The result type of a {@code FileWalker}.
     */
    public static enum VisitResult {
    /**
     * Continue traversal.
     */
    CONTINUE,

    /**
     * Continue without visiting the entries in this directory.
     */
    SKIP,

    /**
     * Continue without visiting the <i>siblings</i> of this file or directory.
     */
    SKIP_SIBLINGS,

    /**
     * Terminate traversal.
     */
    TERMINATE
    }

    /**
     * Returns the result of this iteration.
     * <p>
     * This is an optional operation. Implementations may choose to throw an {@code UnsupportedOperationException} or return
     * {@code null}.
     * <p>
     * For example this method may return a list of file(s) which match a certain criteria in a directory tree, simulating
     * the Unix/Linux {@code find} or Windows {@code search} commands.
     * 
     * @return the result of this iteration
     */
    public R getResult();

    /**
     * This method will be called for a directory before entries in the directory are visited.
     * 
     * @param dir the specified directory
     * @return {@link VisitResult#CONTINUE CONTINUE} to visit the entries in this directory<br>
     *         {@link VisitResult#SKIP SKIP} to skip this directory<br>
     *         {@link VisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} to skip this directory and it's <i>siblings</i><br>
     *         {@link VisitResult#TERMINATE TERMINATE} to terminate the traversal
     */
    public VisitResult preVisitDirectory(final File dir);

    /**
     * This method will be called for a directory after entries in the directory, and all of their descendants, are visited.
     * 
     * @param dir the specified directory
     * @return {@link VisitResult#CONTINUE CONTINUE} to continue with the next file or directory<br>
     *         {@link VisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} to skip the <i>siblings</i> of this directory<br>
     *         {@link VisitResult#TERMINATE TERMINATE} to terminate the traversal
     * @throws IOException if an I/O error occurs
     */
    public VisitResult postVisitDirectory(final File dir);

    /**
     * This method will be called for each file that is encountered in a directory.
     * 
     * @param file the specified file
     * @throws IOException if an I/O error occurs
     * @return {@link VisitResult#CONTINUE CONTINUE} to continue with the next file or directory<br>
     *         {@link VisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} to skip the <i>siblings</i> of this file<br>
     *         {@link VisitResult#TERMINATE TERMINATE} to terminate the traversal
     */
    public VisitResult visitFile(final File file) throws IOException;

    /**
     * This method will be called when a file is a directory that could not be opened, and other reasons.
     * 
     * @param file the specified file or directory
     * @param ex   the I/O exception that prevented the file from being visited
     * @return {@link VisitResult#CONTINUE CONTINUE} to continue with the next file or directory<br>
     *         {@link VisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} to skip the <i>siblings</i> of this file<br>
     *         {@link VisitResult#TERMINATE TERMINATE} to terminate the traversal
     * @throws IOException if an I/O error occurs
     */
    public VisitResult visitFileFailed(final File file, final IOException ex) throws IOException;

}
