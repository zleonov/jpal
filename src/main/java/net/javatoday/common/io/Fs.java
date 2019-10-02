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
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static net.javatoday.common.io.FileWalker.VisitResult.CONTINUE;
import static net.javatoday.common.io.FileWalker.VisitResult.SKIP_SIBLINGS;
import static net.javatoday.common.io.FileWalker.VisitResult.TERMINATE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import net.javatoday.common.base.MessageDigests;
import net.javatoday.common.base.Str;
import net.javatoday.common.io.FileWalker.VisitResult;

/**
 * Static utility methods for working with {@link File}s and {@link Path}s.
 * <p>
 * The class is not intended to substitute the functionality in {@link java.nio.file.Files java.nio.file.Files},
 * {@link com.google.common.io.MoreFiles com.google.common.io.MoreFiles}, or {@link com.google.common.io.Files
 * com.google.common.io.Files}. The intent is to provide unified access between various Java and Guava I/O classes to
 * several of the most commonly used I/O operations.
 * <p>
 * <b>Warning:</b> The {@code java.io.File} facility does not scale with large file systems, suffers from race
 * conditions, and provides no platform-independent way to detect symbolic links. As such there is no way to ensure a
 * symbolic link to a directory is not followed when traversing a file tree. In the presence of symbolic links, you may
 * encounter files outside the starting directory, or even end up in an infinite loop.
 * <p>
 * While {@code java.io.File} operations are not deprecated, Java 7+ users are highly encouraged to use
 * {@code java.nio.file.Path} operations introduced in JDK 7 as part of the
 * <a target="_blank" href="https://www.oracle.com/technetwork/articles/javase/nio-139333.html">The Java NIO.2 File
 * System</a>.
 * <p>
 * The following tables describe common convenience methods provided by this class and their closest Guava and Java
 * equivalents:
 * 
 * <pre>
 * <table border="1" cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th colspan="2" align="center">File I/O</th>     
 *   </tr>
 *   <tr>
 *     <th align="center">Method</th><th align="center">Guava</th>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(byte[], File)}</td><td>{@link Files#asByteSink(File, FileWriteMode...) Files.asByteSink(File, }{@link FileWriteMode#APPEND APPEND)}{@link ByteSink#write(byte[]) .write(byte[])}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(CharSequence, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, }{@link Charsets#UTF_8 UTF_8, }{@link FileWriteMode#APPEND APPEND)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(CharSequence, File, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset, }{@link FileWriteMode#APPEND APPEND)}}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(Iterable, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, }{@link Charsets#UTF_8 UTF_8, }{@link FileWriteMode#APPEND APPEND)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(Iterable, File, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset, }{@link FileWriteMode#APPEND APPEND)}}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedInputStream(File)}</td><td>{@link Files#asByteSource(File) Files.asByteSource(File)}{@link ByteSource#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedOutputStream(File, boolean) newBufferedOutputStream(File, false)}</td><td>{@link Files#asByteSink(File, FileWriteMode...) Files.asByteSink(File)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedOutputStream(File, boolean) newBufferedOutputStream(File, true)}</td><td>{@link Files#asByteSink(File, FileWriteMode...) Files.asByteSink(File, }{@link FileWriteMode#APPEND APPEND)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newReader(File)}</td><td>{@link Files#asCharSource(File, Charset) Files.asCharSource(File, }{@link Charsets#UTF_8 UTF_8)}{@link CharSource#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newReader(File, Charset)}</td><td>{@link Files#asCharSource(File, Charset) Files.asCharSource(File, Charset)}{@link CharSource#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(File, boolean) newWriter(File, false)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, }{@link Charsets#UTF_8 UTF_8)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(File, boolean) newWriter(File, true)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, }{@link Charsets#UTF_8 UTF_8, }{@link FileWriteMode#APPEND APPEND)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(File, boolean, Charset) newWriter(File, false, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(File, boolean, Charset) newWriter(File, true, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset, }{@link FileWriteMode#APPEND APPEND)}}{@link CharSink#openBufferedStream() .openBufferedStream()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#read(File)}</td><td>{@link Files#asCharSource(File, Charset) Files.asCharSource(File, }{@link Charsets#UTF_8 UTF_8)}{@link CharSource#read() .read()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#read(File, Charset)}</td><td>{@link Files#asCharSource(File, Charset) Files.asCharSource(File, Charset)}{@link CharSource#read() .read()}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#readBytes(File)}</td><td>{@link Files#toByteArray(File)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#readLines(File)}</td><td>{@link Files#readLines(File, Charset) Files.readLines(File, }{@link Charsets#UTF_8 UTF_8)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#readLines(File, Charset)}</td><td>{@link Files#readLines(File, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(byte[], File)}</td><td>{@link Files#asByteSink(File, FileWriteMode...) Files.asByteSink(File)}{@link ByteSink#write(byte[]) .write(byte[])}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(CharSequence, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, }{@link Charsets#UTF_8 UTF_8)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(CharSequence, File, Charset)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, Charset)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(Iterable, File)}</td><td>{@link Files#asCharSink(File, Charset, FileWriteMode...) Files.asCharSink(File, }{@link Charsets#UTF_8 UTF_8)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td>
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
 * 
 * <table border="1" cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th colspan="3" align="center">Java NIO.2</th>
 *   </tr>
 *   <tr>
 *     <th align="center">Method</th><th align="center">Guava</th><th align="center">Java 8</th>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(byte[], Path)}</td><td>{@link MoreFiles#asByteSink(Path, OpenOption...) MoreFiles.asByteSink(Path, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link ByteSink#write(byte[]) .write(byte[])}</td><td>{@link java.nio.file.Files#write(Path, byte[], OpenOption...) Files.write(Path, byte[], }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(CharSequence, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, }{@link StandardCharsets#UTF_8 UTF_8, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>{@link java.nio.file.Files#write(Path, byte[], OpenOption...) Files.write(Path, }{@link String#getBytes(Charset) CharSequence.toString().getBytes(}{@link StandardCharsets#UTF_8 UTF_8), }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(CharSequence, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>{@link java.nio.file.Files#write(Path, byte[], OpenOption...) Files.write(Path, }{@link String#getBytes(Charset) CharSequence.toString().getBytes(Charset), }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(Iterable, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, }{@link StandardCharsets#UTF_8 UTF_8, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link java.nio.file.Files#write(Path, Iterable, OpenOption...) Files.write(Path, Iterable, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#append(Iterable, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link java.nio.file.Files#write(Path, Iterable, Charset, OpenOption...) Files.write(Path, Iterable, Charset, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedInputStream(Path)}</td><td>{@link MoreFiles#asByteSource(Path, OpenOption...) MoreFiles.asByteSource(Path)}{@link ByteSource#openBufferedStream() .openBufferedStream()}</td><td>{@link BufferedInputStream#BufferedInputStream(InputStream) new BufferedInputStream(}{@link java.nio.file.Files#newInputStream(Path, OpenOption...) Files.newInputStream(Path))}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedOutputStream(Path, boolean) newBufferedOutputStream(Path, false)}</td><td>{@link MoreFiles#asByteSink(Path, OpenOption...) MoreFiles.asByteSink(Path)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td><td>{@link BufferedOutputStream#BufferedOutputStream(OutputStream) new BufferedOutputStream(}{@link java.nio.file.Files#newOutputStream(Path, OpenOption...) Files.newOutputStream(Path))}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newBufferedOutputStream(Path, boolean) newBufferedOutputStream(Path, true)}</td><td>{@link MoreFiles#asByteSink(Path, OpenOption...) MoreFiles.asByteSink(Path, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link ByteSink#openBufferedStream() .openBufferedStream()}</td><td>{@link BufferedOutputStream#BufferedOutputStream(OutputStream) new BufferedOutputStream(}{@link java.nio.file.Files#newOutputStream(Path, OpenOption...) Files.newOutputStream(Path, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND))}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newReader(Path)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, }{@link StandardCharsets#UTF_8 UTF_8)}{@link CharSource#openBufferedStream() .openBufferedStream()}</td><td>{@link java.nio.file.Files#newBufferedReader(Path) Files.newBufferedReader(Path)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newReader(Path, Charset)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, Charset)}{@link CharSource#openBufferedStream() .openBufferedStream()}</td><td>{@link java.nio.file.Files#newBufferedReader(Path, Charset) Files.newBufferedReader(Path, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(Path, boolean) newWriter(Path, false)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, }{@link StandardCharsets#UTF_8 UTF_8)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link java.nio.file.Files#newBufferedWriter(Path, OpenOption...) Files.newBufferedWriter(Path)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(Path, boolean) newWriter(Path, true)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, }{@link StandardCharsets#UTF_8 UTF_8, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link java.nio.file.Files#newBufferedWriter(Path, OpenOption...) Files.newBufferedWriter(Path, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(Path, boolean, Charset) newWriter(Path, false, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link java.nio.file.Files#newBufferedWriter(Path, Charset, OpenOption...) Files.newBufferedWriter(Path, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#newWriter(Path, boolean, Charset) newWriter(Path, true, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}{@link CharSink#openBufferedStream() .openBufferedStream()}</td><td>{@link java.nio.file.Files#newBufferedWriter(Path, Charset, OpenOption...) Files.newBufferedWriter(Path, Charset, }{@link StandardOpenOption#CREATE CREATE, }{@link StandardOpenOption#APPEND APPEND)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#read(Path)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, }{@link StandardCharsets#UTF_8 UTF_8)}{@link CharSource#read() .read()}</td><td>{@link String#String(byte[], Charset) new String(}{@link java.nio.file.Files#readAllBytes(Path) Files.readAllBytes(Path), }{@link StandardCharsets#UTF_8 UTF_8)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#read(Path, Charset)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, Charset)}{@link CharSource#read() .read()}</td><td>{@link String#String(byte[], Charset) new String(}{@link java.nio.file.Files#readAllBytes(Path) Files.readAllBytes(Path), }{@link String#String(byte[], Charset) Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#readBytes(Path)}</td><td>{@link MoreFiles#asByteSource(Path, OpenOption...) MoreFiles.asByteSource(Path)}{@link ByteSource#read() .read()}</td><td>{@link java.nio.file.Files#readAllBytes(Path) Files.readAllBytes(Path)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#readLines(Path)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, }{@link StandardCharsets#UTF_8 UTF_8)}{@link CharSource#readLines() .readLines()}</td><td>{@link java.nio.file.Files#readAllLines(Path) Files.readAllLines(Path)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#readLines(Path, Charset)}</td><td>{@link MoreFiles#asCharSource(Path, Charset, OpenOption...) MoreFiles.asCharSource(Path, Charset)}{@link CharSource#readLines() .readLines()}</td><td>{@link java.nio.file.Files#readAllLines(Path, Charset) Files.readAllLines(Path, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(byte[], Path)}</td><td>{@link MoreFiles#asByteSink(Path, OpenOption...) MoreFiles.asByteSink(Path)}{@link ByteSink#write(byte[]) .write(byte[])}</td><td>{@link java.nio.file.Files#write(Path, byte[], OpenOption...) Files.write(Path, byte[])}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(CharSequence, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, }{@link StandardCharsets#UTF_8 UTF_8)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>{@link java.nio.file.Files#write(Path, byte[], OpenOption...) Files.write(Path, }{@link String#getBytes(Charset) CharSequence.toString().getBytes(}{@link StandardCharsets#UTF_8 UTF_8))}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(CharSequence, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset)}{@link CharSink#write(CharSequence) .write(CharSequence)}</td><td>{@link java.nio.file.Files#write(Path, byte[], OpenOption...) Files.write(Path, }{@link String#getBytes(Charset) CharSequence.toString().getBytes(Charset))}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(Iterable, Path)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, }{@link StandardCharsets#UTF_8 UTF_8)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link java.nio.file.Files#write(Path, Iterable, OpenOption...) Files.write(Path, Iterable)}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fs#write(Iterable, Path, Charset)}</td><td>{@link MoreFiles#asCharSink(Path, Charset, OpenOption...) MoreFiles.asCharSink(Path, Charset)}{@link CharSink#writeLines(Iterable) .writeLines(Iterable)}</td><td>{@link java.nio.file.Files#write(Path, Iterable, OpenOption...) Files.write(Path, Iterable, Charset)}</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#deleteDirectoryContents(Path, RecursiveDeleteOption...)}</td><td>N/A</td>
 *   </tr>
 *   <tr>
 *     <td>N/A</td><td>{@link MoreFiles#deleteRecursively(Path, RecursiveDeleteOption...)}</td><td>N/A</td>
 *   </tr>
 * </table>
 * </pre>
 * 
 * @see java.nio.file.Files java.nio.file.Files
 * @see com.google.common.io.Files com.google.common.io.Files
 * @see com.google.common.io.MoreFiles com.google.common.io.MoreFiles
 * 
 * @author Zhenya Leonov
 */
@SuppressWarnings("deprecation")
final public class Fs {

    private Fs() {
    }

    /**
     * Appends bytes to the given file.
     * <p>
     * If the file does not exist it will be created.
     * 
     * @param bytes the bytes to append
     * @param to    the file write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File append(final byte[] bytes, final File to) throws IOException {
        checkNotNull(bytes, "bytes == null");
        checkNotNull(to, "to == null");
        final Closer closer = Closer.create();
        try {
            final OutputStream out = closer.register(new FileOutputStream(to, true));
            out.write(bytes);
            out.flush();
            return to;
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Appends bytes to the given file.
     * <p>
     * If the file does not exist it will be created.
     * 
     * @param bytes the bytes to append
     * @param to    the file write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path append(final byte[] bytes, final Path to) throws IOException {
        checkNotNull(bytes, "bytes == null");
        checkNotNull(to, "to == null");
        try (final OutputStream out = java.nio.file.Files.newOutputStream(to, CREATE, APPEND)) {
            out.write(bytes);
        }
        return to;
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
     * Appends a character sequence to the given file using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param chars the character sequence to append
     * @param to    the file to append to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path append(final CharSequence chars, final Path to) throws IOException {
        return append(chars, to, StandardCharsets.UTF_8);
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
    public static Path append(final CharSequence chars, final Path to, final Charset charset) throws IOException {
        checkNotNull(chars, "chars == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");
        try (final OutputStream out = java.nio.file.Files.newOutputStream(to, CREATE, APPEND)) {
            CharStream.write(chars, out, charset);
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
        try (final OutputStream out = java.nio.file.Files.newOutputStream(to, CREATE, APPEND)) {
            CharStream.write(lines, out, charset);
        }
        return to;
    }

    /**
     * Deletes all the files within a directory. Does not delete the directory itself.
     * 
     * @param dir the directory to delete the contents of
     * @throws IllegalArgumentException if the argument is not a directory
     * @throws IOException              if an I/O error occurs
     * @see MoreFiles#deleteDirectoryContents(Path, com.google.common.io.RecursiveDeleteOption...)
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
     * @see MoreFiles#deleteRecursively(Path, com.google.common.io.RecursiveDeleteOption...)
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
     * 
     * @deprecated Users not working with legacy APIs should prefer {@link #hash(File, HashFunction)} which uses Guava's
     *             <a target="_blank" href="https://github.com/google/guava/wiki/HashingExplained">Caching facility<a>.
     *
     * @param file   the given file
     * @param digest the specified message digest object
     * @return the result of {@link MessageDigest#digest()} for all the bytes in the given file
     * @throws IOException if an I/O error occurs
     */
    public static byte[] getDigest(final File file, final MessageDigest digest) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(digest, "digest == null");
        final Closer closer = Closer.create();
        try {
            return ByteStream.getDigest(closer.register(new FileInputStream(file)), digest);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Computes and returns the digest value of the given file using the specified message digest object.
     * <p>
     * Invoke {@link MessageDigests#toString(byte[])} to get a lower case hexadecimal string representation of the digest
     * value which conveniently matches the output of Unix-like commands such as {@code md5sum}.
     * <p>
     * The {@code MessageDigest} is reset when this method returns successfully.
     * 
     * @deprecated Users not working with legacy APIs should prefer {@link #hash(Path, HashFunction)} which uses Guava's
     *             <a target="_blank" href="https://github.com/google/guava/wiki/HashingExplained">Caching facility<a>.
     *
     * @param path   the given file
     * @param digest the specified message digest object
     * @return the result of {@link MessageDigest#digest()} for all the bytes in the given file
     * @throws IOException if an I/O error occurs
     */
    public static byte[] getDigest(final Path path, final MessageDigest digest) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(digest, "digest == null");
        final Closer closer = Closer.create();
        try {
            return ByteStream.getDigest(closer.register(java.nio.file.Files.newInputStream(path)), digest);
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
     * @see Files#getNameWithoutExtension(String)
     * @see Files#getFileExtension(String)
     * @see MoreFiles#getNameWithoutExtension(Path)
     * @see MoreFiles#getFileExtension(Path)
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
            return ByteStream.hash(closer.register(java.nio.file.Files.newInputStream(path)), func);
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
     * @see Files#move(File, File)
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
     * Returns a {@code BufferedInputStream} to the specified file.
     *
     * @param path the specified file
     * @return a {@code BufferedInputStream} to the specified file
     * @throws IOException if an I/O error occurs
     */
    public static BufferedInputStream newBufferedInputStream(final Path path) throws IOException {
        checkNotNull(path, "path == null");
        return new BufferedInputStream(java.nio.file.Files.newInputStream(path));
    }

    /**
     * Returns a {@code BufferedOutputStream} to the specified file.
     * <p>
     * If the file does not exist it will be created.
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
     * Returns a {@code BufferedOutputStream} to the specified file.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param path   the specified file
     * @param append whether to truncate or append to the specified file
     * @return a {@code BufferedOutputStream} to the specified file
     * @throws IOException if an I/O error occurs
     */
    public static BufferedOutputStream newBufferedOutputStream(final Path path, final boolean append) throws IOException {
        checkNotNull(path, "path == null");
        return new BufferedOutputStream(java.nio.file.Files.newOutputStream(path, append ? new StandardOpenOption[] { CREATE, APPEND } : new StandardOpenOption[] {}));
    }

    /**
     * Returns a {@code BufferedWriter} which writes to the given file using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created.
     * 
     * @param file    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code BufferedWriter} which writes to the given file using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedWriter newWriter(final File file, final boolean append) throws IOException {
        return newWriter(file, append, Charsets.UTF_8);
    }

    /**
     * Returns a {@code BufferedWriter} which writes to the given file using the specified charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param file    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code BufferedWriter} which writes to the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedWriter newWriter(final File file, final boolean append, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset));
    }

    /**
     * Returns a {@code BufferedWriter} which writes to the given file using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param path    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code BufferedWriter} which writes to the given file using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedWriter newWriter(final Path path, final boolean append) throws IOException {
        return newWriter(path, append, Charsets.UTF_8);
    }

    /**
     * Returns a {@code BufferedWriter} which writes to the given file using the specified charset.
     * <p>
     * If the file does not exist it will be created.
     *
     * @param file    the given file
     * @param append  whether to truncate or append to the specified file
     * @param charset the character set to use when writing to the file
     * @return a {@code BufferedWriter} which writes to the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedWriter newWriter(final Path path, final boolean append, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");
        return new BufferedWriter(new OutputStreamWriter(java.nio.file.Files.newOutputStream(path, append ? new OpenOption[] { CREATE, APPEND } : new OpenOption[] { CREATE, TRUNCATE_EXISTING, WRITE }), charset));
    }

//    /**
//     * Returns a {@code ByteArrayInputStream} containing all the bytes read from the specified file.
//     *
//     * @param file the specified file
//     * @return a {@code ByteArrayInputStream} containing all the bytes read from the specified file
//     * @throws IOException if an I/O error occurs
//     */
//    public static ByteArrayInputStream newByteArrayInputStream(final File file) throws IOException {
//        checkNotNull(file, "file == null");
//        return new ByteArrayInputStream(Files.asByteSource(file).read());
//    }
//
//    /**
//     * Returns a {@code ByteArrayInputStream} containing all the bytes read from the specified file.
//     *
//     * @param path the specified file
//     * @return a {@code ByteArrayInputStream} containing all the bytes read from the specified file
//     * @throws IOException if an I/O error occurs
//     */
//    public static ByteArrayInputStream newByteArrayInputStream(final Path path) throws IOException {
//        checkNotNull(path, "path == null");
//        return new ByteArrayInputStream(java.nio.file.Files.readAllBytes(path));
//    }

    /**
     * Returns a new {@code BufferedReader} which reads from the given file using the UTF-8 charset.
     * <p>
     * Does not close the reader.
     * 
     * @param file the given file
     * @return a new {@code BufferedReader} which reads from the given file using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedReader newReader(final File file) throws IOException {
        return newReader(file, Charsets.UTF_8);
    }

    /**
     * Returns a new {@code BufferedReader} which reads from the given file using the specified charset.
     * <p>
     * Does not close the reader.
     * 
     * @param file    the given file
     * @param charset the character set to use when reading from the input stream
     * @return a new {@code BufferedReader} which reads from the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedReader newReader(final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(file, "charset == null");
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    }

    /**
     * Returns a new {@code BufferedReader} which reads from the given file using the UTF-8 charset.
     * <p>
     * Does not close the reader.
     * 
     * @param path the given file
     * @return a new {@code BufferedReader} which reads from the given file using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedReader newReader(final Path path) throws IOException {
        return newReader(path, StandardCharsets.UTF_8);
    }

    /**
     * Returns a new {@code BufferedReader} which reads from the given file using the specified charset.
     * <p>
     * Does not close the reader.
     * 
     * @param path    the given file
     * @param charset the character set to use when reading from the input stream
     * @return a new {@code BufferedReader} which reads from the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static BufferedReader newReader(final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(path, "charset == null");
        return new BufferedReader(new InputStreamReader(java.nio.file.Files.newInputStream(path), charset));
    }

    /**
     * Returns a list of all lines read from the specified file in the UTF-8 charset. The lines do not include
     * line-termination characters.
     * <p>
     * <b>Note:</b> Use {@link Files#readLines(File, Charset)} to specify a different charset.
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
     * Returns a list of all lines read from the specified file in the UTF-8 charset. The lines do not include
     * line-termination characters.
     * <p>
     * <b>Note:</b> Use {@link Files#readLines(File, Charset)} to specify a different charset.
     * 
     * @param file    the specified file *
     * @param charset the character set to use when reading the file
     * @return a list of all lines read from the specified file in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readLines(final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            return CharStream.readLines(closer.register(new FileInputStream(file)), charset);
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
     * @see java.nio.file.Path#relativize(Path)
     */
    public static String relativize(final File base, final File child) throws IOException {
        return base.getCanonicalFile().toURI().relativize(child.getCanonicalFile().toURI()).getPath();
    }

    /**
     * Returns a list of all lines read from the specified file in the UTF-8 charset. The lines do not include
     * line-termination characters.
     * 
     * @param path the specified file
     * @return a list of all lines read from the specified file in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readLines(final Path path) throws IOException {
        checkNotNull(path, "path == null");

        try (final InputStream in = java.nio.file.Files.newInputStream(path)) {
            return CharStream.readLines(in, StandardCharsets.UTF_8);
        }
    }

    /**
     * Returns a list of all lines read from the specified file in the UTF-8 charset. The lines do not include
     * line-termination characters.
     * 
     * @param path    the specified file
     * @param charset the character set to use when reading the file
     * @return a list of all lines read from the specified file in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readLines(final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");

        try (final InputStream in = java.nio.file.Files.newInputStream(path)) {
            return CharStream.readLines(in, charset);
        }
    }

    /**
     * Returns a byte array containing all the bytes read from the specified file.
     * 
     * @param file the specified file
     * @return a byte array containing all the bytes read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static byte[] readBytes(final File file) throws IOException {
        checkNotNull(file, "file == null");

        final Closer closer = Closer.create();
        try {

            return ByteStream.readBytes(closer.register(new FileInputStream(file)), file.length());
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Returns a byte array containing all the bytes read from the specified file.
     * 
     * @param path the specified file
     * @return a byte array containing all the bytes read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static byte[] readBytes(final Path path) throws IOException {
        checkNotNull(path, "path == null");

        try (final InputStream in = java.nio.file.Files.newInputStream(path)) {
            return ByteStream.readBytes(in, java.nio.file.Files.size(path));
        }
    }

    /**
     * Returns the specified path string with all path-separator characters replaced with a forward slash ('/').
     * 
     * @param path the specified path
     * @return the specified path string with all path-separator characters replaced with a forward slash ('/')
     */
    public static String separatorsToUnix(final String path) {
        checkNotNull(path, "path == null");
        return Str.replace(path, "\\", "/");
    }

    /**
     * Returns the contents of the specified file as a string in the UTF-8 charset.
     * 
     * @param file the specified file
     * @return the contents of the specified file as a string in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static String read(final File file) throws IOException {
        return read(file, Charsets.UTF_8);
    }

    /**
     * Returns the contents of the given file as a string in the specified charset.
     * 
     * @param file    the specified file
     * @param charset the character set to use when reading the file
     * @return the contents of the given file as a string in the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static String read(final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            // Reading the contents of the stream into a byte array first is much faster than using StringBuilder
            return new String(ByteStream.readBytes(closer.register(new FileInputStream(file)), file.length()), charset);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Returns the contents of the specified file as a string in the UTF-8 charset.
     * 
     * @param file the specified file
     * @return the contents of the specified file as a string in the UTF-8 charset
     * @throws IOException if an I/O error occurs
     */
    public static String read(final Path path) throws IOException {
        return read(path, StandardCharsets.UTF_8);
    }

    /**
     * Returns the contents of the given file as a string in the specified charset.
     * 
     * @param file    the given file
     * @param charset the specified charset
     * @return the contents of the given file as a string in the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static String read(final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");

        // Reading the contents of the stream into a byte array first is much faster than using StringBuilder
        try (final InputStream in = java.nio.file.Files.newInputStream(path)) {
            return new String(ByteStream.readBytes(in, java.nio.file.Files.size(path)), charset);
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
    public static Path write(final CharSequence chars, final Path to) throws IOException {
        return write(chars, to, StandardCharsets.UTF_8);
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
    public static Path write(final CharSequence chars, final Path to, final Charset charset) throws IOException {
        checkNotNull(chars, "chars == null");
        checkNotNull(to, "to == null");
        checkNotNull(charset, "charset == null");
        try (final OutputStream out = java.nio.file.Files.newOutputStream(to, CREATE, TRUNCATE_EXISTING, WRITE)) {
            CharStream.write(chars, out, charset);
        }
        return to;
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

    /**
     * Writes lines of text to the given file (with each line, including the last, terminated with the operating system's
     * default line separator) using the UTF-8 charset.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before new lines are written.
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
     * If the file does not exist it will be created. If the file exists it will be truncated before new lines are written.
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
        try (final OutputStream out = java.nio.file.Files.newOutputStream(to)) {
            CharStream.write(lines, out, charset);
        }
        return to;
    }

    /**
     * Writes bytes to the given file.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before the bytes are written.
     * 
     * @param bytes the bytes to write
     * @param to    the file write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File write(final byte[] bytes, final File to) throws IOException {
        checkNotNull(bytes, "bytes == null");
        checkNotNull(to, "to == null");
        final Closer closer = Closer.create();
        try {
            final OutputStream out = closer.register(new FileOutputStream(to));
            out.write(bytes);
            out.flush();
            return to;
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Writes bytes to the given file.
     * <p>
     * If the file does not exist it will be created. If the file exists it will be truncated before the bytes are written.
     * 
     * @param bytes the bytes to write
     * @param to    the file write to
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path write(final byte[] bytes, final Path to) throws IOException {
        checkNotNull(bytes, "bytes == null");
        checkNotNull(to, "to == null");
        try (final OutputStream out = java.nio.file.Files.newOutputStream(to)) {
            out.write(bytes);
        }
        return to;
    }

    private static VisitResult walkFileTree(final File start, int depth, final int maxDepth, final FileWalker<?> walker) throws IOException {

        File[] files = null;

        try {
            if (start.isDirectory() && (files = start.listFiles()) == null)
                throw new IOException("cannot access " + start.getPath());
            if (depth >= maxDepth || start.isFile())
                return walker.visitFile(start);
        } catch (final SecurityException ex) {
            if (depth == 0)
                throw ex;
            return CONTINUE;
        } catch (final IOException ex) {
            return walker.visitFileFailed(start, ex);
        }

        VisitResult result = walker.preVisitDirectory(start);

        if (result != CONTINUE)
            return result;
        for (final File file : files) {
            if (!file.exists())
                return walker.visitFileFailed(file, new FileNotFoundException(file.getPath()));
            result = walkFileTree(file, depth + 1, maxDepth, walker);
            if (result == SKIP_SIBLINGS)
                break;
            if (result == TERMINATE)
                return TERMINATE;
        }
        return walker.postVisitDirectory(start);
    }

}