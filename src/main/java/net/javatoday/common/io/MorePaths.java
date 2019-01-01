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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.MoreFiles;

import net.javatoday.common.base.MessageDigests;

/**
 * Static utility methods for working with {@link Path}s.
 * <p>
 * This class is the {@link java.nio.file.Path} counterpart to the {@link MorePathsTest} class.
 * <p>
 * Below is table of common methods provided in this class and their Guava and Java 7+ equivalents:
 * 
 * <pre>
 * <table border="1" cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th>Method</th><th>Guava</th><th>Java</th>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#append(CharSequence, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, StandardCharsets.UTF_8, CREATE, APPEND)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#append(CharSequence, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset, CREATE, APPEND)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#append(Iterable, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, StandardCharsets.UTF_8, CREATE, APPEND)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link Files#write(Path, Iterable, OpenOption...) Files.write(Path, Iterable, CREATE, APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#append(Iterable, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset, CREATE, APPEND)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link Files#write(Path, Iterable, Charset, OpenOption...) Files.write(Path, Iterable, Charset, CREATE, APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newBufferedInputStream(Path)}</td><td>{@link MoreFiles#asByteSource(Path, OpenOption...) MoreFiles.asByteSource(Path)}{@link ByteSource#openBufferedStream() .openBufferedStream()}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newBufferedOutputStream(Path, boolean) newBufferedOutputStream(Path, false)}</td><td>{@link MoreFiles#asByteSink(Path, OpenOption...) MoreFiles.asByteSink(Path)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newBufferedOutputStream(Path, boolean) newBufferedOutputStream(Path, true)}</td><td>{@link MoreFiles#asByteSink(Path, OpenOption...) MoreFiles.asByteSink(Path, CREATE, APPEND)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, StandardCharsets.UTF_8)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link Files#newBufferedWriter(Path, OpenOption...) Files.newBufferedWriter(Path)}</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, StandardCharsets.UTF_8, CREATE, APPEND)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link Files#newBufferedWriter(Path, OpenOption...) Files.newBufferedWriter(Path, CREATE, APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link Files#newBufferedWriter(Path, Charset, OpenOption...) Files.newBufferedWriter(Path, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset, CREATE, APPEND)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link Files#newBufferedWriter(Path, Charset, OpenOption...) Files.newBufferedWriter(Path, Charset, CREATE, APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newLineNumberReader(Path)}</td><td>N/A</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newLineNumberReader(Path, Charset)}</td><td>N/A</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newPrintStream(Path, boolean)}</td><td>N/A</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newPrintStream(Path, boolean, Charset)}</td><td>N/A</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newPrintWriter(Path, boolean)}</td><td>N/A</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#newPrintWriter(Path, boolean, Charset)}</td><td>N/A</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, StandardCharsets.UTF_8)}{@link CharSource#readLines() .readLines()}</td><td>{@link Files#readAllLines(Path)}</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, Charset)}{@link CharSource#readLines() .readLines()}</td><td>{@link Files#readAllLines(Path, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#toString(Path)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, StandardCharsets.UTF_8)}{@link CharSource#read() .read()}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#toString(Path, Charset)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, Charset)}{@link CharSource#read() .read()}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#write(CharSequence, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, StandardCharsets.UTF_8)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#write(CharSequence, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#write(Iterable, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, StandardCharsets.UTF_8)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link Files#write(Path, Iterable, OpenOption...) Files.write(Path, Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link MorePaths#write(Iterable, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link Files#write(Path, Iterable, OpenOption...) Files.write(Path, Iterable, Charset)}</td>
 *   </tr>
 * </table>
 * </pre>
 * 
 * @see java.nio.file.Paths
 * @see com.google.common.io.Files
 * @see com.google.common.io.MoreFiles
 * @see java.nio.file.Files
 * 
 * @author Zhenya Leonov
 */
final public class MorePaths {

    private MorePaths() {
    }

    /**
     * Appends a character sequence to the given file using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param content the character sequence to append
     * @param to      the file to append to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path append(final CharSequence content, final Path to) throws IOException {
        return append(content, to, StandardCharsets.UTF_8);
    }

    /**
     * Appends a character sequence to the given file using the specified charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param content the character sequence to append
     * @param to      the file to append to
     * @param charset the character set to use when writing to the file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path append(final CharSequence content, final Path to, final Charset charset) throws IOException {
        checkNotNull(content, "content == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");
        try (final OutputStream out = Files.newOutputStream(to, CREATE, APPEND)) {
            CharStream.write(content, out, charset);
        }
        return to;
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
    public static Path append(final Iterable<? extends CharSequence> lines, final Path to) throws IOException {
        append(lines, to, StandardCharsets.UTF_8);
        return to;
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
    public static Path append(final Iterable<? extends CharSequence> lines, final Path to, final Charset charset) throws IOException {
        checkNotNull(lines, "lines == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");
        try (final OutputStream out = Files.newOutputStream(to, CREATE, APPEND)) {
            CharStream.write(lines, out, charset);
        }
        return to;
    }

    /**
     * Computes and returns the digest value of the given file using the specified message digest object.
     * <p>
     * Invoke {@link MessageDigests#toString(byte[])} to get a lower case hexadecimal string representation of the digest
     * value which conveniently matches the output of Unix-like commands such as {@code md5sum}.
     * <p>
     * The {@code MessageDigest} is reset when this method returns successfully.
     * <p>
     * <b>Note:</b> Users not working with legacy APIs should prefer {@link #hash(Path, HashFunction)} which uses Guava's
     * robust {@link Hashing} facility.
     *
     * @param path   the given file
     * @param digest the specified message digest object
     * @return the result of {@link MessageDigest#digest()} for all the bytes in the given file
     * @throws IOException if an I/O error occurs
     */
    public static byte[] digest(final Path path, final MessageDigest digest) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(digest, "digest == null");
        final Closer closer = Closer.create();
        try {
            return ByteStream.digest(closer.register(Files.newInputStream(path)), digest);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Computes and returns the {@code HashCode} of the given file using the specified hash function.
     * <p>
     * Invoke {@link HashCode#asBytes()} to get the hash code value as a byte array or {@link HashCode#toString()} for a
     * lower case hexadecimal string representation which conveniently matches the output of Unix-like commands such as
     * {@code md5sum}.
     * 
     * @param path the specified file
     * @param func the given hash function
     * @return the {@code HashCode} of the given file using the specified hash function
     * @throws IOException if an I/O error occurs
     */
    public static HashCode hash(final Path path, HashFunction func) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(func, "func == null");
        final Closer closer = Closer.create();
        try {
            return ByteStream.hash(closer.register(Files.newInputStream(path)), func);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Returns a {@code BufferedInputStream} to the specified file.
     *
     * @param path the specified file
     * @return a {@code BufferedInputStream} to the specified file
     * @throws IOException if an I/O error occurs
     */
    public static BufferedInputStream newBufferedInputStream(final Path path) throws IOException {
        checkNotNull(path, "path == null");
        return new BufferedInputStream(Files.newInputStream(path));
    }

    /**
     * Returns a {@code BufferedOutputStream} to the specified file.
     *
     * @param path   the specified file
     * @param append whether to truncate or append to the specified file
     * @return a {@code BufferedOutputStream} to the specified file
     * @throws IOException if an I/O error occurs
     */
    public static BufferedOutputStream newBufferedOutputStream(final Path path, final boolean append) throws IOException {
        checkNotNull(path, "path == null");
        return new BufferedOutputStream(Files.newOutputStream(path, append ? new StandardOpenOption[] { CREATE, APPEND } : new StandardOpenOption[] {}));
    }

    /**
     * Returns a {@code ByteArrayInputStream} containing all the bytes read from the specified file.
     *
     * @param path the specified file
     * @return a {@code ByteArrayInputStream} containing all the bytes read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static ByteArrayInputStream newByteArrayInputStream(final Path path) throws IOException {
        checkNotNull(path, "path == null");
        return new ByteArrayInputStream(Files.readAllBytes(path));
    }

    /**
     * Returns a new {@code LineNumberReader} which reads from the given file using the UTF-8 charset.
     * <p>
     * Does not close the reader.
     * 
     * @param path the given file
     * @return a new {@code LineNumberReader} which reads from the given file using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static LineNumberReader newLineNumberReader(final Path path) throws IOException {
        return newLineNumberReader(path, StandardCharsets.UTF_8);
    }

    /**
     * Returns a new {@code LineNumberReader} which reads from the given file using the specified charset.
     * <p>
     * Does not close the reader.
     * 
     * @param path    the given file
     * @param charset the character set to use when reading from the input stream
     * @return a new {@code LineNumberReader} which reads from the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static LineNumberReader newLineNumberReader(final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(path, "charset == null");
        return new LineNumberReader(new InputStreamReader(Files.newInputStream(path), charset));
    }

    /**
     * Returns a new {@code PrintStream} which writes to the given file using the UTF-8 charset, without automatic line
     * flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintStream} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintStream#checkError()} method.
     *
     * @param path   the specified file
     * @param append whether to truncate or append to the specified file
     * @return a new {@code PrintStream} which writes to the given file using the UTF-8 charset, without automatic line
     *         flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintStream newPrintStream(final Path path, final boolean append) throws IOException {
        return newPrintStream(path, append, StandardCharsets.UTF_8);
    }

    /**
     * Returns a new {@code PrintStream} which writes to the given file using the UTF-8 charset, without automatic line
     * flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintStream} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintStream#checkError()} method.
     *
     * @param path    the specified file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the given file
     * @return a new {@code PrintStream} which writes to the given file using the UTF-8 charset, without automatic line
     *         flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintStream newPrintStream(final Path path, final boolean append, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");
        return CharStream.newPrintStream(newBufferedOutputStream(path, append), false, charset);
    }

    /**
     * Returns a {@code PrintWriter} to the given file using the UTF-8 charset, without automatic line flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintWriter} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintWriter#checkError()} method.
     *
     * @param path   the given file
     * @param append whether to truncate or append to the specified file
     * @return a {@code PrintWriter} to the given file using the UTF-8 charset, without automatic line flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintWriter newPrintWriter(final Path path, final boolean append) throws IOException {
        return newPrintWriter(path, append, StandardCharsets.UTF_8);
    }

    /**
     * Returns a {@code PrintWriter} to the given file using the specified charset, without automatic line flushing.
     * <p>
     * <b>Warning:</b> A {@code PrintWriter} never throws I/O exceptions. Users may check whether errors have occurred by
     * calling the {@link PrintWriter#checkError()} method.
     *
     * @param path    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code PrintWriter} to the given file using the specified charset, without automatic line flushing
     * @throws IOException if an I/O error occurs
     */
    public static PrintWriter newPrintWriter(final Path path, final boolean append, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");
        return new PrintWriter(Files.newBufferedWriter(path, append ? new StandardOpenOption[] { CREATE, APPEND } : new StandardOpenOption[] {}));
    }

    /**
     * Returns the contents of the specified file as a string in the UTF-8 charset.
     * 
     * @param file the specified file
     * @return the contents of the specified file as a string in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static String toString(final Path path) throws IOException {
        return toString(path, StandardCharsets.UTF_8);
    }

    /**
     * Returns the contents of the given file as a string in the specified charset.
     * 
     * @param file    the given file
     * @param charset the specified charset
     * @return the contents of the given file as a string in the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static String toString(final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");

        try (final InputStream in = Files.newInputStream(path)) {
            return CharStream.toString(in, charset);
        }
    }

    /**
     * Writes a character sequence to the given file using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new content is written.
     *
     * @param content the character sequence to append
     * @param to      the file to write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path write(final CharSequence content, final Path to) throws IOException {
        return write(content, to, StandardCharsets.UTF_8);
    }

    /**
     * Writes a character sequence to the given file using the specified charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new content is written.
     * 
     * @param content the character sequence to append
     * @param to      the file to write to
     * @param charset the character set to use when writing to the file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path write(final CharSequence content, final Path to, final Charset charset) throws IOException {
        checkNotNull(content, "content == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");
        try (final OutputStream out = Files.newOutputStream(to, CREATE, TRUNCATE_EXISTING, WRITE)) {
            CharStream.write(content, out, charset);
        }
        return to;
    }

    /**
     * Writes lines of text to the given file (with each line, including the last, terminated with the operating system's
     * default line separator) using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new content is written.
     *
     * @param lines the lines of text to write
     * @param to    the file write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path write(final Iterable<? extends CharSequence> lines, final Path to) throws IOException {
        return write(lines, to, StandardCharsets.UTF_8);
    }

    /**
     * Writes lines of text to the given file (with each line, including the last, terminated with the operating system's
     * default line separator) using the specified charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new content is written.
     * 
     * @param lines   the lines of text to write
     * @param to      the file write to
     * @param charset the character set to use when writing to the file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path write(final Iterable<? extends CharSequence> lines, final Path to, final Charset charset) throws IOException {
        checkNotNull(lines, "lines == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");
        try (final OutputStream out = Files.newOutputStream(to)) {
            CharStream.write(lines, out, charset);
        }
        return to;
    }

}