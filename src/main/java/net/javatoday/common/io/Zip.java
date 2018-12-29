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
import static java.util.logging.Level.WARNING;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
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
import com.google.common.io.Closer;
import com.google.common.io.Files;

/**
 * Static utility methods useful for working with {@link ZipFile}s and {@link ZipOutputStream}s.
 * <p>
 * This class is provided as a convenience to developers who are making casual use of the {@code java.util.zip} package.
 * Consider using a more robust library like <a href="https://commons.apache.org/proper/commons-compress/
 * target="_blank">Apache Commons Compress</a>.
 * 
 * @author Zhenya Leonov
 */
final public class Zip {

    private static final Logger LOGGER = Logger.getLogger(Zip.class.getName());

    private Zip() {
    }

    private static Predicate<ZipEntry> IS_DIRECTORY = new Predicate<ZipEntry>() {

        @Override
        public boolean apply(ZipEntry e) {
            return e.isDirectory();
        }
    };

    private static Predicate<ZipEntry> IS_FILE = Predicates.not(IS_DIRECTORY);

    /**
     * Closes the specified {@code ZipFile}, with control over whether an {@code IOException} may be thrown. If the zip file
     * is {@code null} it is ignored. This is primarily useful in a finally block, where a thrown exception needs to be
     * logged but not propagated to the caller (otherwise the original exception will be lost).
     * <p>
     * If an {@code IOException} occurs and {@code hideIOException} is {@code true} it will be logged but never thrown.
     * Otherwise it will be propagated to the caller.
     * 
     * @deprecated Java 7+ users should switch to <a href=
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
                LOGGER.log(WARNING, "An IOException occurred while attempting to close " + zip.getName(), e);
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
     * @deprecated Java 7+ users should switch to <a href=
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
     * Returns a {@code ByteArrayInputStream} containing all the bytes read from the specified zip entry.
     * 
     * @param zip the zip file in which to find the zip entry
     * @param ze  the specified zip entry
     * @return a {@code ByteArrayInputStream} containing all the bytes read from the specified zip entry
     * @throws IOException if an I/O error occurs
     */
    public static ByteArrayInputStream newByteArrayInputStream(final ZipFile zip, final ZipEntry ze) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");
        return new ByteArrayInputStream(Zip.toByteArray(zip, ze));
    }

    /**
     * Returns a zip input stream to given file using the UTF-8 charset to decode zip entry names. The charset is ignored if
     * the <a href="package-summary.html#lang_encoding"> language encoding bit</a> of the zip entry's general purpose bit
     * flag is set.
     * 
     * @param file the given file
     * @return a zip input stream to given file using the UTF-8 charset to decode zip entry names
     * @throws IOException if an I/O error occurs
     */
    public static ZipInputStream newZipInputStream(final File file) throws IOException {
        checkNotNull(file, "file == null");

        return new ZipInputStream(new FileInputStream(file), Charsets.UTF_8);
    }

    /**
     * Returns a zip output stream to the given file using the UTF-8 charset to encode zip entry names and comments.
     * 
     * @param file the given file
     * @return a zip output stream to the given file using the UTF-8 charset to encode zip entry names and comments
     * @throws IOException if an I/O error occurs
     */
    public static ZipOutputStream newZipOutputStream(final File file) throws IOException {
        checkNotNull(file, "file == null");

        return new ZipOutputStream(new FileOutputStream(file), Charsets.UTF_8);
    }

    /**
     * Returns a zip input stream to given file using the specified charset to decode zip entry names. The charset is
     * ignored if the <a href="package-summary.html#lang_encoding"> language encoding bit</a> of the zip entry's general
     * purpose bit flag is set.
     * 
     * @param file    the given file
     * @param charset the specified charset
     * @return a zip input stream to given file using the specified charset to decode zip entry names
     * @throws IOException if an I/O error occurs
     */
    public static ZipInputStream newZipInputStream(final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(file, "charset == null");

        return new ZipInputStream(new FileInputStream(file), charset);
    }

    /**
     * Returns a zip output stream to the given file using the specified charset to encode zip entry names and comments.
     * 
     * @param file    the given file
     * @param charset the specified charset
     * @return a zip output stream to the given file using the specified charset to encode zip entry names and comments
     * @throws IOException if an I/O error occurs
     */
    public static ZipOutputStream newZipOutputStream(final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(file, "charset == null");

        return new ZipOutputStream(new FileOutputStream(file), charset);
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
        checkArgument(isFile().apply(ze), "ZipEntry is a directory");

        final Closer closer = Closer.create();

        try {
            return CharStream.readLines(closer.register(zip.getInputStream(ze)), charset);
        } catch (final Throwable t) {
            throw closer.rethrow(t);
        } finally {
            closer.close();
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
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");
        checkArgument(isFile().apply(ze), "ZipEntry is a directory");

        final Closer closer = Closer.create();

        try {
            return CharStream.readLines(closer.register(zip.getInputStream(ze)));
        } catch (final Throwable t) {
            throw closer.rethrow(t);
        } finally {
            closer.close();
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
    public static byte[] toByteArray(final ZipFile zip, final ZipEntry ze) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");

        final Closer closer = Closer.create();

        try {
            return ByteStreams.toByteArray(closer.register(zip.getInputStream(ze)));
        } catch (final Throwable t) {
            throw closer.rethrow(t);
        } finally {
            closer.close();
        }
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
    public static String toString(final ZipFile zip, final ZipEntry ze, final Charset charset) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");
        checkNotNull(charset, "charset == null");
        checkArgument(isFile().apply(ze), "ZipEntry is a directory");

        final Closer closer = Closer.create();

        try {
            return CharStream.toString(closer.register(zip.getInputStream(ze)), charset);
        } catch (final Throwable t) {
            throw closer.rethrow(t);
        } finally {
            closer.close();
        }
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
    public static String toString(final ZipFile zip, final ZipEntry ze) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(ze, "ze == null");
        checkArgument(!ze.isDirectory(), "ZipEntry is a directory");

        final Closer closer = Closer.create();

        try {
            return CharStream.toString(closer.register(zip.getInputStream(ze)));
        } catch (final Throwable t) {
            throw closer.rethrow(t);
        } finally {
            closer.close();
        }
    }

    /**
     * Extracts the give zip file to the destination directory using the UTF-8 charset to decode zip entry names. The
     * charset is ignored if the <a href="package-summary.html#lang_encoding"> language encoding bit</a> of the zip entry's
     * general purpose bit flag is set.
     * 
     * @param zip  the given zip file
     * @param dest the destination directory
     * @throws IOException if an I/O error occurs
     */
    public static void unzip(final File zip, final File dest) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(dest, "dest == null");
        checkArgument(zip.isFile(), "% is not a normal file or does not exist", zip.getPath());
        checkArgument(dest.isDirectory(), "% is not a directory or does not exist", dest.getPath());
        unzip(zip, dest, Charsets.UTF_8);
    }

    /**
     * Extracts the give zip file to the destination directory using the specified charset to decode zip entry names. The
     * charset is ignored if the <a href="package-summary.html#lang_encoding"> language encoding bit</a> of the zip entry's
     * general purpose bit flag is set.
     * 
     * @param zip     the given zip file
     * @param charset the specified charset
     * @param dest    the destination directory
     * @throws IOException if an I/O error occurs
     */
    public static void unzip(final File zip, final File dest, final Charset charset) throws IOException {
        checkNotNull(zip, "zip == null");
        checkNotNull(dest, "dest == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();

        try {
            final ZipInputStream zis = closer.register(newZipInputStream(zip, charset));

            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                final File fout = new File(dest, ze.getName());
                Files.createParentDirs(fout);
                if (ze.isDirectory())
                    fout.mkdir();
                else {
                    final OutputStream out = Fs.newBufferedOutputStream(fout, false);
                    ByteStreams.copy(zis, out);
                    out.close();
                }
            }
        } catch (final Throwable t) {
            throw closer.rethrow(t);
        } finally {
            closer.close();
        }
    }

}