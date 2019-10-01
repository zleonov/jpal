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
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.logging.Level.WARNING;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.MoreFiles;

/**
 * Static utility methods useful for working with {@link ZipFile}s and {@link ZipOutputStream}s.
 * <p>
 * This class is provided as a convenience to developers who are making casual use of the {@code java.util.zip} package.
 * Consider using a dedicated 3rd party library such as
 * <a target="_blank" href="https://commons.apache.org/proper/commons-compress">Apache Commons Compress</a> in
 * production projects.
 * 
 * @author Zhenya Leonov
 */
final public class Zip {

    private static final Logger logger = Logger.getLogger(Zip.class.getName());

    private static Predicate<ZipEntry> IS_DIRECTORY = new Predicate<ZipEntry>() {

        @Override
        public boolean apply(ZipEntry e) {
            return e.isDirectory();
        }
    };

    private static Predicate<ZipEntry> IS_FILE = Predicates.not(IS_DIRECTORY);

    private Zip() {
    }

    /**
     * Closes the specified {@code ZipFile}, with control over whether an {@code IOException} may be thrown. If the zip file
     * is {@code null} it is ignored. This is primarily useful in a finally block, where a thrown exception needs to be
     * logged but not propagated to the caller (otherwise the original exception will be lost).
     * <p>
     * If an {@code IOException} occurs and {@code hideIOException} is {@code true} it will be logged but never thrown.
     * Otherwise it will be propagated to the caller.
     * 
     * @deprecated Java 7+ users should switch to <a target="_blank" href=
     *             "https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">try-with-resources</a>
     *             or Guava's {@link Closeables#close(Closeable, boolean)} instead.
     * 
     * @param zip             the specified zip file
     * @param hideIOException whether or not propagate {@code IOException}s
     * @throws IOException if an I/O error occurs and {@code hideIOException} is {@code false}
     */
    public static void close(final ZipFile zip, final boolean hideIOException) throws IOException {
        try {
            if (zip != null)
                zip.close();
        } catch (final IOException e) {
            if (hideIOException)
                logger.log(WARNING, "An IOException occurred while attempting to close " + zip.getName(), e);
            else
                throw e;
        }
    }

    /**
     * Closes the specified {@code ZipFile}, logging and swallowing any {@code IOException}s. If the zip file is
     * {@code null} it is ignored.
     * <p>
     * <b>Warning:</b> this method is prone to misuse. In general {@code IOException}s should be handled and/or propagated
     * to the caller when closing a resource. Use this method only if you sure that no other action can be taken if an
     * {@code IOException} occurs.
     * 
     * @deprecated Java 7+ users should switch to <a target="_blank" href=
     *             "https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">try-with-resources</a>
     *             or Guava's {@link Closeables#close(Closeable, boolean)} instead.
     * 
     * @param zip the specified zip file
     */
    public static void closeQuietly(final ZipFile zip) {
        try {
            close(zip, true);
        } catch (final IOException e) {
            throw new AssertionError();
        }
    }

    /**
     * Returns all the entries in the specified zip file.
     * 
     * @deprecated Java 8+ users should call {@link ZipFile#stream()}.
     * 
     * @param zip the specified zip file
     * @return all the entries in the specified zip file
     */
    public static Iterator<? extends ZipEntry> getEntries(final ZipFile zip) {
        checkNotNull(zip, "zip == null");
        return Iterators.forEnumeration(zip.entries());
    }

    /**
     * Returns all the entries in the specified zip file that match the given predicate.
     * 
     * @deprecated Java 8+ users should call {@link ZipFile#stream()}{@link Stream#filter(Predicate) .filter(Predicate)}.
     * 
     * @param zip    the specified zip file
     * @param filter the given predicate
     * @return all the entries in the specified zip file that match the given predicate
     */
    public static Iterator<? extends ZipEntry> getEntries(final ZipFile zip, final Predicate<? super ZipEntry> filter) {
        checkNotNull(zip, "zip == null");
        checkNotNull(filter, "filter == null");
        return Iterators.filter(getEntries(zip), filter);
    }

    /**
     * Returns a predicate which tests if a {@code ZipEntry} is a directory.
     * 
     * @return a predicate which tests if a {@code ZipEntry} is a directory
     */
    public static Predicate<ZipEntry> isDirectory() {
        return IS_DIRECTORY;
    }

    /**
     * Returns a predicate which tests if a {@code ZipEntry} is not a directory.
     * 
     * @return a predicate which tests if a {@code ZipEntry} is not a directory
     */
    public static Predicate<ZipEntry> isFile() {
        return IS_FILE;
    }

    /**
     * Returns a zip input stream to given file using the UTF-8 charset to decode zip entry names. The charset is ignored if
     * the <a target="_blank" href="package-summary.html#lang_encoding"> language encoding bit</a> of the zip entry's
     * general purpose bit flag is set.
     * 
     * @param path the given file
     * @return a zip input stream to given file using the UTF-8 charset to decode zip entry names
     * @throws IOException if an I/O error occurs
     */
    public static ZipInputStream newZipInputStream(final Path path) throws IOException {
        return newZipInputStream(path, UTF_8);
    }

    /**
     * Returns a zip input stream to given file using the specified charset to decode zip entry names. The charset is
     * ignored if the <a target="_blank" href="package-summary.html#lang_encoding"> language encoding bit</a> of the zip
     * entry's general purpose bit flag is set.
     * 
     * @param path    the given file
     * @param charset the specified charset
     * @return a zip input stream to given file using the specified charset to decode zip entry names
     * @throws IOException if an I/O error occurs
     */
    public static ZipInputStream newZipInputStream(final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "file == null");
        checkNotNull(path, "charset == null");
        return new ZipInputStream(Files.newInputStream(path), charset);
    }

    /**
     * Returns a zip output stream to the given file using the UTF-8 charset to encode zip entry names and comments.
     * 
     * @param path the given file
     * @return a zip output stream to the given file using the UTF-8 charset to encode zip entry names and comments
     * @throws IOException if an I/O error occurs
     */
    public static ZipOutputStream newZipOutputStream(final Path path) throws IOException {
        return newZipOutputStream(path, UTF_8);
    }

    /**
     * Returns a zip output stream to the given file using the specified charset to encode zip entry names and comments.
     * 
     * @param path    the given file
     * @param charset the specified charset
     * @return a zip output stream to the given file using the specified charset to encode zip entry names and comments
     * @throws IOException if an I/O error occurs
     */
    public static ZipOutputStream newZipOutputStream(final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(path, "charset == null");
        return new ZipOutputStream(Files.newOutputStream(path), charset);
    }

    /**
     * Writes a new zip file entry and positions the stream for writing the next entry.
     * 
     * @param zout  the {@code ZipOutputStream}
     * @param ze    the {@code ZipEntry}
     * @param bytes the contents of the {@code ZipEntry}
     * @return the {@code ZipOutputStream}
     * @throws IOException if an I/O error occurs
     */
    public static ZipOutputStream putNextEntry(final ZipOutputStream zout, final ZipEntry ze, final byte[] bytes) throws IOException {
        checkNotNull(zout, "zout == null");
        checkNotNull(ze, "ze ==  null");
        checkNotNull(bytes, "bytes == null");
        zout.putNextEntry(ze);
        zout.write(bytes);
        zout.closeEntry();
        return zout;
    }

    /**
     * Returns a string containing all the characters from the given zip entry in the UTF-8 charset.
     * 
     * @param zip the zip file in which to find the zip entry
     * @param ze  the given zip entry
     * @return a string containing all the characters from the given zip entry in the UTF-8 charset
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the specified entry is a directory
     * @see Charsets
     */
    public static String read(final ZipFile zip, final ZipEntry ze) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");
        // checkArgument(!ze.isDirectory(), "ZipEntry is a directory");

        return read(zip, ze, UTF_8);
    }

    /**
     * Returns a string containing all the characters from the given zip entry in the specified charset.
     * 
     * @param zip     the zip file in which to find the zip entry
     * @param ze      the given zip entry
     * @param charset the character set to use when reading the zip entry
     * @return a string containing all the characters from the given zip entry in the specified charset
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the specified entry is a directory
     * @see Charsets
     */
    public static String read(final ZipFile zip, final ZipEntry ze, final Charset charset) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");
        checkNotNull(charset, "charset == null");
        // checkArgument(isFile().apply(ze), "ZipEntry is a directory");

        try (final InputStream in = zip.getInputStream(ze)) {
            return CharStream.read(in, charset);
        }
    }

    /**
     * Returns a byte array containing all bytes from the specified zip entry.
     * 
     * @param zip the zip file in which to find the zip entry
     * @param ze  the specified zip entry
     * @return a byte array containing all bytes from the specified zip entry
     * @throws IOException if an I/O error occurs
     */
    public static byte[] readBytes(final ZipFile zip, final ZipEntry ze) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");

        try (final InputStream in = zip.getInputStream(ze)) {
            return ByteStreams.toByteArray(in);
        }
    }

    /**
     * Returns all of the lines read from the specified zip entry using the UTF-8 charset. The lines do not include
     * line-termination characters, but do include other leading and trailing whitespace.
     * 
     * @param zip the zip file in which to find the zip entry
     * @param ze  the specified zip entry
     * @return a mutable {@link List} containing all the lines read from a zip entry using the UTF-8 charset
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the specified entry is a directory
     */
    public static List<String> readLines(final ZipFile zip, final ZipEntry ze) throws IOException {
        return readLines(zip, ze, UTF_8);
    }

    /**
     * Returns all of the lines read from the specified zip entry using the specified charset. The lines do not include
     * line-termination characters, but do include other leading and trailing whitespace.
     * 
     * @param zip     the zip file in which to find the zip entry
     * @param ze      the specified zip entry
     * @param charset the charset used to decode the zip entry
     * @return a mutable {@link List} containing all the lines read from a zip entry using the specified charset
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the specified entry is a directory
     */
    public static List<String> readLines(final ZipFile zip, final ZipEntry ze, final Charset charset) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");
        checkNotNull(charset, "charset == null");
        // checkArgument(isFile().apply(ze), "ZipEntry is a directory");

        try (final InputStream in = zip.getInputStream(ze)) {
            return CharStream.readLines(in, charset);
        }
    }

    /**
     * Extracts the given zip file to the destination directory using the UTF-8 charset to decode zip entry names. The
     * charset is ignored if the <a target="_blank" href="package-summary.html#lang_encoding"> language encoding bit</a> of
     * the zip entry's general purpose bit flag is set.
     * <p>
     * If the destination directory does not exist or if the zip file contains entries outside the destination directory
     * this call will result in an {@code IllegalArgumentException}. If the destination directory is a symbolic link it will
     * be resolved to its final target. Zip entries representing symbolic links are not supported. Existing files will be
     * overwritten.
     * <p>
     * <b>Note:</b> If this operation fails because of an I/O error or other problems it may have succeeded in extracting
     * some (but not all) of the files and/or directories.
     * 
     * @param zip  the given zip file
     * @param dest the destination directory
     * @throws IOException if an I/O error occurs
     */
    public static void unzip(final Path zip, final Path dest) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(dest, "dest == null");
        unzip(zip, dest, UTF_8);
    }

    /**
     * Extracts the given zip file to the destination directory using the specified charset to decode zip entry names. The
     * charset is ignored if the <a target="_blank" href="package-summary.html#lang_encoding"> language encoding bit</a> of
     * the zip entry's general purpose bit flag is set.
     * <p>
     * If the destination directory does not exist or if the zip file contains entries outside the destination directory
     * this call will result in an {@code IllegalArgumentException}. If the destination directory is a symbolic link it will
     * be resolved to its final target. Zip entries representing symbolic links are not supported. Existing files will be
     * overwritten.
     * <p>
     * <b>Note:</b> If this operation fails because of an I/O error or other problems it may have succeeded in extracting
     * some (but not all) of the files and/or directories.
     * 
     * @param zip     the given zip file
     * @param dest    the destination directory
     * @param charset the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static void unzip(final Path zip, final Path dest, final Charset charset) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(dest, "dest == null");
        checkNotNull(charset, "charset == null");
        checkArgument(Files.isDirectory(dest), "%s is not a directory or does not exist", dest);

        final Path target = dest.toRealPath();

        try (final ZipInputStream zis = newZipInputStream(zip, charset)) {

            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                final Path fout = target.resolve(ze.getName());
                MoreFiles.createParentDirectories(fout);

                checkArgument(fout.startsWith(target), "%s is outside the destination directory", ze.getName());

                if (ze.isDirectory())
                    Files.createDirectory(fout);
                else
                    Files.copy(zis, fout, REPLACE_EXISTING);
            }
        }
    }

}