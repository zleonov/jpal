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
package net.javatoday.common.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.javatoday.common.io.FileWalker.VisitResult.CONTINUE;
import static net.javatoday.common.io.FileWalker.VisitResult.SKIP_SIBLINGS;
import static net.javatoday.common.io.FileWalker.VisitResult.TERMINATE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import net.javatoday.common.base.MessageDigests;
import net.javatoday.common.base.MoreStrings;
import net.javatoday.common.io.FileWalker.VisitResult;

/**
 * Static utility methods for working with {@link File}s.
 * <p>
 * <b>Warning:</b> The {@code java.io.File} facility suffers from race conditions and provides no platform-independent
 * way to detect symbolic links, as such there is no way to ensure a symbolic link to a directory is not followed when
 * traversing a file tree. In the presence of symbolic links, you may encounter files outside the starting directory, or
 * even end up in an infinite loop.
 * <p>
 * While this class is not deprecated, Java 7+ users are highly encouraged to use the
 * <a href="https://www.oracle.com/technetwork/articles/javase/nio-139333.html">The Java NIO.2 File System introduced in
 * JDK 7</a>. {@link java.nio.file.Path Path} users will find similar methods in the {@link MorePaths} class.
 * <p>
 * Below is table of common methods provided in this class and their Guava equivalents:
 * 
 * <pre>
 * <table border="1" cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th>Method</th><th>Guava</th>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(CharSequence, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charsets.UTF_8, APPEND)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(CharSequence, File, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset, APPEND)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(Iterable, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charsets.UTF_8, APPEND)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(Iterable, File, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset, APPEND)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedInputStream(File)}</td><td>{@link Files#asByteSource(File) Files.asByteSource(File)}{@link ByteSource#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedOutputStream(File, boolean) newBufferedOutputStream(File, false)}</td><td>{@link Files#asByteSink(File, FileWriteMode...) Files.asByteSink(File)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedOutputStream(File, boolean) newBufferedOutputStream(File, true)}</td><td>{@link Files#asByteSink(File, FileWriteMode...) Files.asByteSink(File, APPEND)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedWriter(File, boolean) newBufferedWriter(File, false)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charsets.UTF_8)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedWriter(File, boolean) newBufferedWriter(File, true)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charsets.UTF_8, APPEND)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedWriter(File, boolean, Charset) newBufferedWriter(File, false, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedWriter(File, boolean, Charset) newBufferedWriter(File, true, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset, APPEND)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newLineNumberReader(File)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newLineNumberReader(File, Charset)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newPrintStream(File, boolean)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newPrintStream(File, boolean, Charset)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newPrintWriter(File, boolean)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newPrintWriter(File, boolean, Charset)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#readLines(File)}</td><td>{@link Files#readLines(File, Charset) Files.readLines(File, Charsets.UTF_8)}</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link Files#readLines(File, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#toString(File)}</td><td>{@link Files#asCharSource(File, Charset) Files.asCharSource(File, Charsets.UTF_8)}{@link CharSource#read() .read()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#toString(File, Charset)}</td><td>{@link Files#asCharSource(File, Charset) Files.asCharSource(File, Charset)}{@link CharSource#read() .read()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(CharSequence, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charsets.UTF_8)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(CharSequence, File, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(Iterable, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charsets.UTF_8)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(Iterable, File, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#deleteDirectoryContents(File)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#deleteRecursively(File)}</td><td>N/A</td>
 *   </tr>
 * </table>
 * </pre>
 * 
 * @see MorePaths
 * @see com.google.common.io.Files
 * @see com.google.common.io.MoreFiles
 * @see java.nio.file.Files
 * 
 * @author Zhenya Leonov
 */
final public class Fs {

    private Fs() {
    }

    /**
     * Appends a character sequence to the given file using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param chars the character sequence to append
     * @param to    the file to append to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File append(final CharSequence chars, final File to) throws IOException {
        return append(chars, to, Charsets.UTF_8);
    }

    /**
     * Appends a character sequence to the given file using the specified charset.
     * <p>
     * If the file does not exist it will be created.
     * 
     * @param chars   the character sequence to append
     * @param to      the file to append to
     * @param charset the character set to use when writing to the file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File append(final CharSequence chars, final File to, final Charset charset) throws IOException {
        checkNotNull(chars, "chars == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            CharStream.write(chars, closer.register(new FileOutputStream(to, true)), charset);
            return to;
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Appends lines of text to the given file (with each line, including the last, terminated with the operating system's
     * default line separator) using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param lines the lines of text to append
     * @param to    the file to append to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File append(final Iterable<? extends CharSequence> lines, final File to) throws IOException {
        return append(lines, to, Charsets.UTF_8);
    }

    /**
     * Appends lines of text to the given file (with each line, including the last, terminated with the operating system's
     * default line separator) using the specified charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param lines   the lines of text to append
     * @param to      the file to append to
     * @param charset the character set to use when writing to the file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File append(final Iterable<? extends CharSequence> lines, final File to, final Charset charset) throws IOException {
        checkNotNull(lines, "lines == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            CharStream.write(lines, closer.register(new FileOutputStream(to, true)), charset);
            return to;
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Deletes all the files within a directory. Does not delete the directory itself.
     * 
     * @param dir the directory to delete the contents of
     * @throws IllegalArgumentException if the argument is not a directory
     * @throws IOException              if an I/O error occurs
     */
    public static void deleteDirectoryContents(final File dir) throws IOException {
        checkNotNull(dir, "dir == null");
        checkArgument(dir.isDirectory(), "%s is not a directory or does not exist", dir.getPath());

        final File[] files = dir.listFiles();
        if (files == null)
            throw new IOException("unable to list files in " + dir.getPath());

        for (final File file : files)
            deleteRecursively(file);
    }

    /**
     * Deletes a file or directory and all contents recursively.
     *
     * @param file the file to delete
     * @throws IOException if an I/O error occurs
     */
    public static void deleteRecursively(final File file) throws IOException {
        checkNotNull(file, "file == null");

        if (file.isDirectory())
            deleteDirectoryContents(file);

        if (!file.delete())
            throw new IOException("failed to delete " + file.getPath());
    }

    /**
     * Computes and returns the digest value of the given file using the specified message digest object.
     * <p>
     * Invoke {@link MessageDigests#toString(byte[])} to get a lower case hexadecimal string representation of the digest
     * value which conveniently matches the output of Unix-like commands such as {@code md5sum}.
     * <p>
     * The {@code MessageDigest} is reset when this method returns successfully.
     * <p>
     * <b>Note:</b> Users not working with legacy APIs should prefer {@link #hash(File, HashFunction)} which uses Guava's
     * robust {@link Hashing} facility.
     *
     * @param file   the given file
     * @param digest the specified message digest object
     * @return the result of {@link MessageDigest#digest()} for all the bytes in the given file
     * @throws IOException if an I/O error occurs
     */
    public static byte[] digest(final File file, final MessageDigest digest) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(digest, "digest == null");
        final Closer closer = Closer.create();
        try {
            return ByteStream.digest(closer.register(new FileInputStream(file)), digest);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Returns the last name in the path's name sequence.
     * <p>
     * This method handles both Windows and Unix path separator characters.
     * 
     * @param path the specified path
     * @return the last name in the path's name sequence
     */
    public static String getName(final String path) {
        checkNotNull(path, "path == null");
        final int lastIndexOf = CharMatcher.anyOf("\\/").lastIndexIn(path);
        return path.substring(lastIndexOf + 1);
    }

    /**
     * Computes and returns the {@code HashCode} of the given file using the specified hash function.
     * <p>
     * Invoke {@link HashCode#asBytes()} to get the hash code value as a byte array or {@link HashCode#toString()} for a
     * lower case hexadecimal string representation which conveniently matches the output of Unix-like commands such as
     * {@code md5sum}.
     * 
     * @param file the specified file
     * @param func the given hash function
     * @return the {@code HashCode} of the given file using the specified hash function
     * @throws IOException if an I/O error occurs
     */
    public static HashCode hash(final File file, final HashFunction func) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(func, "func == null");
        final Closer closer = Closer.create();
        try {
            return ByteStream.hash(closer.register(new FileInputStream(file)), func);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Moves the file from one path to another. This method can rename a file or move it to a different directory, like the
     * Unix {@code mv} command.
     *
     * @param from the source file
     * @param to   the destination file
     * @throws IOException if an I/O error occurs
     */
    public static void move(final File from, final File to) throws IOException {
        checkNotNull(to, "to == null");
        checkNotNull(from, "from == null");

        checkArgument(!from.equals(to), "source file %s and destination file %s must be different", from.getPath(), to.getPath());

        if (!from.renameTo(to)) {
            Files.copy(from, to);
            if (!from.delete())
                throw new IOException("unable to delete " + from.getPath());
        }
    }

    /**
     * Returns a {@code BufferedInputStream} to the specified file.
     *
     * @param file the specified file
     * @return a {@code BufferedInputStream} to the specified file
     * @throws IOException if an I/O error occurs
     */
    public static BufferedInputStream newBufferedInputStream(final File file) throws IOException {
        checkNotNull(file, "file == null");
        return new BufferedInputStream(new FileInputStream(file));
    }

    /**
     * Returns a {@code BufferedOutputStream} to the specified file.
     *
     * @param file   the specified file
     * @param append whether to truncate or append to the specified file
     * @return a {@code BufferedOutputStream} to the specified file
     * @throws IOException if an I/O error occurs
     */
    public static BufferedOutputStream newBufferedOutputStream(final File file, final boolean append) throws IOException {
        checkNotNull(file, "file == null");
        return new BufferedOutputStream(new FileOutputStream(file, append));
    }

    /**
     * Returns a {@code BufferedWriter} which writes to the given file using the UTF-8 charset.
     *
     * @param file    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code BufferedWriter} which writes to the given file using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedWriter newBufferedWriter(final File file, final boolean append) throws IOException {
        return newBufferedWriter(file, append, Charsets.UTF_8);
    }

    /**
     * Returns a {@code BufferedWriter} which writes to the given file using the specified charset.
     *
     * @param file    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code BufferedWriter} which writes to the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedWriter newBufferedWriter(final File file, final boolean append, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset));
    }

    /**
     * Returns a {@code ByteArrayInputStream} containing all the bytes read from the specified file.
     *
     * @param file the specified file
     * @return a {@code ByteArrayInputStream} containing all the bytes read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static ByteArrayInputStream newByteArrayInputStream(final File file) throws IOException {
        checkNotNull(file, "file == null");
        return new ByteArrayInputStream(Files.asByteSource(file).read());
    }

    /**
     * Returns a new {@code LineNumberReader} which reads from the given file using the UTF-8 charset.
     * <p>
     * Does not close the reader.
     * 
     * @param file the given file
     * @return a new {@code LineNumberReader} which reads from the given file using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static LineNumberReader newLineNumberReader(final File file) throws IOException {
        return newLineNumberReader(file, Charsets.UTF_8);
    }

    /**
     * Returns a new {@code LineNumberReader} which reads from the given file using the specified charset.
     * <p>
     * Does not close the reader.
     * 
     * @param file    the given file
     * @param charset the character set to use when reading from the input stream
     * @return a new {@code LineNumberReader} which reads from the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static LineNumberReader newLineNumberReader(final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(file, "charset == null");
        return new LineNumberReader(new InputStreamReader(new FileInputStream(file), charset));
    }

    /**
     * Returns a new {@code PrintStream} which writes to the given file using the UTF-8 charset, without automatic line
     * flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintStream} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintStream#checkError()} method.
     *
     * @param file   the specified file
     * @param append whether to truncate or append to the specified file
     * @return a new {@code PrintStream} which writes to the given file using the UTF-8 charset, without automatic line
     *         flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintStream newPrintStream(final File file, final boolean append) throws IOException {
        return newPrintStream(file, append, Charsets.UTF_8);
    }

    /**
     * Returns a new {@code PrintStream} which writes to the given file using the specified charset, without automatic line
     * flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintStream} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintStream#checkError()} method.
     * 
     * @param file    the specified file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the given file
     * @return a new {@code PrintStream} which writes to the given file using the specified charset, without automatic line
     *         flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintStream newPrintStream(final File file, final boolean append, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");
        return CharStream.newPrintStream(newBufferedOutputStream(file, append), false, charset);
    }

    /**
     * Returns a {@code PrintWriter} to the given file using the UTF-8 charset, without automatic line flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintWriter} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintWriter#checkError()} method.
     * 
     * @param file   the given file
     * @param append whether to truncate or append to the specified file
     * @return a {@code PrintWriter} to the given file using the UTF-8 charset, without automatic line flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintWriter newPrintWriter(final File file, final boolean append) throws IOException {
        return newPrintWriter(file, append, Charsets.UTF_8);
    }

    /**
     * Returns a {@code PrintWriter} to the given file using the specified charset, without automatic line flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintWriter} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintWriter#checkError()} method.
     *
     * @param file    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code PrintWriter} to the given file using the specified charset, without automatic line flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintWriter newPrintWriter(final File file, final boolean append, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset)));
    }

    /**
     * Returns a list of all lines read from the specified file in the UTF-8 charset. The lines do not include
     * line-termination characters.
     * <p>
     * Note: Use {@link Files#readLines(File, Charset)} to specify a different charset.
     * 
     * @param file the specified file
     * @return a list of all lines read from the specified file in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readLines(final File file) throws IOException {
        checkNotNull(file, "file == null");

        final Closer closer = Closer.create();
        try {
            return CharStream.readLines(closer.register(new FileInputStream(file)), Charsets.UTF_8);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Returns the relative path between {@code base} and {@code child}.
     * 
     * @param base  the base path
     * @param child the path to relativize against {@code base}
     * @return the relative path between {@code base} and {@code child}
     * @throws IOException if an I/O error occurs
     */
    public static String relativize(final File base, final File child) throws IOException {
        return base.getCanonicalFile().toURI().relativize(child.getCanonicalFile().toURI()).getPath();
    }

    /**
     * Returns the specified path string with all path-separator characters replaced with a forward slash ('/').
     * 
     * @param path the specified path
     * @return the specified path string with all path-separator characters replaced with a forward slash ('/')
     */
    public static String separatorsToUnix(final String path) {
        checkNotNull(path, "path == null");
        return MoreStrings.replace(path, "\\", "/");
    }

    /**
     * Returns the contents of the specified file as a string in the UTF-8 charset.
     * 
     * @param file the specified file
     * @return the contents of the specified file as a string in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static String toString(final File file) throws IOException {
        return toString(file, Charsets.UTF_8);
    }

    /**
     * Returns the contents of the given file as a string in the specified charset.
     * 
     * @param file    the specified file
     * @param charset the character set to use when reading the file
     * @return the contents of the given file as a string in the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static String toString(final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            return CharStream.toString(closer.register(new FileInputStream(file)), charset, (int) file.length());
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Walks a file tree.
     * 
     * This method walks a file tree rooted at a given starting file. The file tree traversal is depth-first with the given
     * {@link FileWalker} invoked for each file encountered. File tree traversal completes when all accessible files in the
     * tree have been visited, or a visit method returns {@link VisitResult#TERMINATE VisitResult.TERMINATE}.
     * <p>
     * If the file is not a directory then the {@link FileWalker#visitFile(File) visitFile(File)} is invoked. If the file is
     * a directory, and the directory could not be opened, then the {@link FileWalker#visitFileFailed(File, IOException)
     * visitFileFailed(File, IOException)} method is invoked with the I/O exception, after which, the file tree walk
     * continues, by default, at the next sibling of the directory.
     * <p>
     * If the directory is opened successfully, then the entries in the directory, and their descendants are visited. If an
     * entry is deleted before it is visited the {@link FileWalker#visitFileFailed(File, IOException) visitFileFailed(File,
     * IOException)} method is invoked with a {@code FileNotFoundException}. When all entries have been visited then the
     * {@link FileWalker#postVisitDirectory(File) postVisitDirectory(File)} method is invoked. The file tree walk then
     * continues, by default, at the next sibling of the directory.
     * <p>
     * When a security manager is installed and it denies access to a file (or directory), then it is ignored and the
     * {@code FileWalker} is not invoked for that file (or directory).
     * <p>
     * 
     * @param start  the starting file
     * @param walker the {@code FileWalker} instance to invoke for each file
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the starting file does not exist
     * @return the starting file
     */
    public static File walkFileTree(final File start, final FileWalker<?> walker) throws IOException {
        checkNotNull(start, "start == null");
        checkArgument(start.exists(), "%s does not exit", start.getPath());
        checkNotNull(walker, "walker == null");
        return walkFileTree(start, Integer.MAX_VALUE, walker);
    }

    /**
     * Walks a file tree.
     * 
     * This method walks a file tree rooted at a given starting file. The file tree traversal is depth-first with the given
     * {@link FileWalker} invoked for each file encountered. File tree traversal completes when all accessible files in the
     * tree have been visited, or a visit method returns {@link VisitResult#TERMINATE VisitResult.TERMINATE}.
     * <p>
     * If the file is not a directory, then the {@link FileWalker#visitFile(File) visitFile(File)} method is invoked. If the
     * file is a directory, and the directory could not be opened, then the
     * {@link FileWalker#visitFileFailed(File, IOException) visitFileFailed(File, IOException)} method is invoked with the
     * I/O exception, after which, the file tree walk continues.
     * <p>
     * If the directory is opened successfully, then the entries in the directory, and their descendants are visited. If an
     * entry is deleted before it is visited, then the {@link FileWalker#visitFileFailed(File, IOException)
     * visitFileFailed(File, IOException)} method is invoked with a {@code FileNotFoundException}. When all entries have
     * been visited then the {@link FileWalker#postVisitDirectory(File) postVisitDirectory(File)} method is invoked. The
     * file tree walk then continues.
     * <p>
     * The {@code maxDepth} parameter is the maximum number of levels of directories to visit. A value of 0 means that only
     * the starting file is visited, unless denied by the security manager. A value of {@link Integer#MAX_VALUE MAX_VALUE}
     * may be used to indicate that all levels should be visited.
     * <p>
     * When a security manager is installed and it denies access to a file (or directory), then it is ignored and the
     * {@code FileWalker} is not invoked for that file (or directory).
     * <p>
     * 
     * @param start    the starting file
     * @param maxDepth the maximum number of directory levels to visit
     * @param walker   the {@code FileWalker} instance to invoke for each file
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the starting file does not exist
     * @return the starting file
     */
    public static File walkFileTree(final File start, final int maxDepth, final FileWalker<?> walker) throws IOException {
        checkNotNull(start, "start == null");
        checkArgument(start.exists(), "%s does not exit", start.getPath());
        checkNotNull(walker, "walker == null");
        checkArgument(maxDepth >= 0, "maxDepth < 1");
        walkFileTree(start, 0, maxDepth, walker);
        return start;
    }

    /**
     * Writes a character sequence to the given file using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new characters are
     * written.
     *
     * @param chars the character sequence to append
     * @param to    the file to write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File write(final CharSequence chars, final File to) throws IOException {
        return write(chars, to, Charsets.UTF_8);
    }

    /**
     * Writes a character sequence to the given file using the specified charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new characters are
     * written.
     * 
     * @param chars   the character sequence to append
     * @param to      the file to write to
     * @param charset the character set to use when writing to the file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File write(final CharSequence chars, final File to, final Charset charset) throws IOException {
        checkNotNull(chars, "chars == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            CharStream.write(chars, closer.register(new FileOutputStream(to, false)), charset);
            return to;
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Writes lines of text to the given file (with each line, including the last, terminated with the operating system's
     * default line separator) using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new lines is written.
     *
     * @param lines the lines of text to write
     * @param to    the file write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File write(final Iterable<? extends CharSequence> lines, final File to) throws IOException {
        return write(lines, to, Charsets.UTF_8);
    }

    /**
     * Writes lines of text to the given file (with each line, including the last, terminated with the operating system's
     * default line separator) using the specified charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new lines is written.
     * 
     * @param lines   the lines of text to write
     * @param to      the file write to
     * @param charset the character set to use when writing to the file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File write(final Iterable<? extends CharSequence> lines, final File to, final Charset charset) throws IOException {
        checkNotNull(lines, "lines == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            CharStream.write(lines, closer.register(new FileOutputStream(to, false)), charset);
            return to;
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    private static VisitResult walkFileTree(final File start, int depth, final int maxDepth, final FileWalker<?> walker) throws IOException {

        File[] files = null;

        try {
            if (start.isDirectory() && (files = start.listFiles()) == null)
                throw new IOException("cannot access " + start.getPath());
            if (depth >= maxDepth || start.isFile())
                return walker.visitFile(start);
        } catch (SecurityException ex) {
            if (depth == 0)
                throw ex;
            return CONTINUE;
        } catch (IOException ex) {
            return walker.visitFileFailed(start, ex);
        }

        VisitResult result = walker.preVisitDirectory(start);

        if (result != CONTINUE)
            return result;
        for (final File file : files) {
            if (!file.exists())
                return walker.visitFileFailed(file, new FileNotFoundException(file.toString()));
            result = walkFileTree(file, depth + 1, maxDepth, walker);
            if (result == SKIP_SIBLINGS)
                break;
            if (result == TERMINATE)
                return TERMINATE;
        }
        return walker.postVisitDirectory(start);
    }

}