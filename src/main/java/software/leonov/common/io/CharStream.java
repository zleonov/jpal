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
package software.leonov.common.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

/**
 * Static utility methods for working with character streams.
 * 
 * @author Zhenya Leonov
 * @see CharStreams
 */
final public class CharStream {

    // private final static int DEFAULT_BUFFER_SIZE = 8192;
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private CharStream() {
    }

    /**
     * Returns a new {@code BufferedReader} which reads from the given input stream in an efficient manner using the UTF-8
     * charset.
     * 
     * @param in the given input stream
     * @return a new {@code BufferedReader} which reads from the given input stream in an efficient manner using the UTF-8
     *         charset
     */
    public static BufferedReader newBufferedReader(final InputStream in) {
        return newBufferedReader(in, UTF_8);
    }

    /**
     * Returns a new {@code BufferedReader} which reads from the given input stream in an efficient manner using the
     * specified charset.
     * 
     * @param in      the given input stream
     * @param charset the character set to use when reading from the input stream
     * @return a new {@code BufferedReader} which reads from the given input stream in an efficient manner using the
     *         specified charset
     */
    public static BufferedReader newBufferedReader(final InputStream in, final Charset charset) {
        checkNotNull(in, "in == null");
        checkNotNull(charset, "charset == null");
        return new BufferedReader(new InputStreamReader(in, charset));
    }

    /**
     * Returning a new {@code BufferedWriter} that writes to the given output stream in an efficient manner using the UTF-8
     * charset.
     * 
     * @param out the given output stream
     * @return a new {@code BufferedWriter} that writes to the given output stream in an efficient manner using the UTF-8
     *         charset
     */
    public static BufferedWriter newBufferedWriter(final OutputStream out) {
        return newBufferedWriter(out, UTF_8);
    }

    /**
     * Returning a new {@code BufferedWriter} that writes to the given output stream in an efficient manner using the
     * specified charset.
     * 
     * @param out     the given output stream
     * @param charset the character set to use when writing to the output stream
     * @return a new {@code BufferedWriter} that writes to the given output stream in an efficient manner using the
     *         specified charset
     */
    public static BufferedWriter newBufferedWriter(final OutputStream out, final Charset charset) {
        checkNotNull(out, "out == null");
        checkNotNull(charset, "charset == null");
        return new BufferedWriter(new OutputStreamWriter(out, charset));
    }

    /**
     * Returns a string of all the characters read from the given input stream using the UTF-8 charset.
     * <p>
     * Does not close the input stream.
     * <p>
     * <b>Guava equivalent:</b> {@link CharStreams#toString(Readable)
     * CharStreams.toString(}{@link InputStreamReader#InputStreamReader(InputStream, Charset) new
     * InputStreamReader(InputStream, UTF_8))}
     * 
     * @param in the given input stream
     * @return a string of all the characters read from the given input stream using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     * @see CharStreams#toString(Readable)
     */
    public static String read(final InputStream in) throws IOException {
        return read(in, UTF_8);
    }

    /**
     * Returns a string of all the characters read from the given input stream using the specified charset.
     * <p>
     * Does not close the input stream.
     * <p>
     * <b>Guava equivalent:</b> {@link CharStreams#toString(Readable)
     * CharStreams.toString(}{@link InputStreamReader#InputStreamReader(InputStream, Charset) new
     * InputStreamReader(InputStream, Charset))}
     * 
     * @param in      the given input stream
     * @param charset the character set to use when reading the input stream
     * @return a string of all the characters read from the given input stream using the specified charset
     * @throws IOException if an I/O error occurs
     * @see CharStreams#toString(Readable)
     */
    public static String read(final InputStream in, final Charset charset) throws IOException {
        checkNotNull(in, "in == null");
        checkNotNull(charset, "charset == null");

        // Reading the contents of the stream into a byte array first is much faster than using StringBuilder
        return new String(ByteStream.toByteArray(in), charset);
    }

    /**
     * Returns all the lines read from the given input stream using the UTF-8 charset. The lines do not include
     * line-termination characters, but do include other leading and trailing whitespace.
     * <p>
     * Does not close the input stream.
     * <p>
     * <b>Guava equivalent:</b> {@link CharStreams#readLines(Readable)
     * CharStreams.readLines(}{@link InputStreamReader#InputStreamReader(InputStream, Charset) new
     * InputStreamReader(InputStream, UTF_8))}
     * 
     * @param in the input stream to read from
     * @return a mutable {@code List} containing all the lines read from the given input stream using the UTF-8 charset
     * @throws IOException if an I/O error occurs
     * @see CharStreams#readLines(Readable)
     */
    public static List<String> readLines(final InputStream in) throws IOException {
        return readLines(in, UTF_8);
    }

    /**
     * Returns all the lines read from the given input stream using the specified charset. The lines do not include
     * line-termination characters, but do include other leading and trailing whitespace.
     * <p>
     * Does not close the input stream.
     * <p>
     * <b>Guava equivalent:</b> {@link CharStreams#readLines(Readable)
     * CharStreams.readLines(}{@link InputStreamReader#InputStreamReader(InputStream, Charset) new
     * InputStreamReader(InputStream, Charset))}
     * 
     * @param in      the input stream to read from
     * @param charset the character set to use
     * @return a mutable {@code List} containing all the lines read from the given input stream using the specified charset
     * @throws IOException if an I/O error occurs
     * @see CharStreams#readLines(Readable)
     */
    public static List<String> readLines(final InputStream in, final Charset charset) throws IOException {
        checkNotNull(in, "in == null");
        checkNotNull(charset, "charset == null");

        final BufferedReader reader = newBufferedReader(in, charset);

        final List<String> lines = Lists.newArrayList();

        String line = null;
        while ((line = reader.readLine()) != null)
            lines.add(line);

        return lines;
    }

    /**
     * Writes a character sequence to the given {@link Appendable} target.
     * <p>
     * Does not attempt close the target even if it is {@link Closeable}.
     * 
     * @param chars the character sequence to write
     * @param out   the given {@code OutputStream}
     * @return the given {@link Appendable} target
     * @throws IOException if an I/O error occurs
     */
    public static Appendable write(final CharSequence chars, final Appendable out) throws IOException {
        checkNotNull(chars, "chars == null");
        checkNotNull(out, "out == null");
        CharStreams.asWriter(out).append(chars).flush();
        return out;
    }

    /**
     * Writes a character sequence to the given output stream using the UTF-8 charset.
     * <p>
     * Does not close the stream.
     * 
     * @param chars the character sequence to write
     * @param out   the given {@code OutputStream}
     * @return the given {@code OutputStream}
     * @throws IOException if an I/O error occurs
     */
    public static OutputStream write(final CharSequence chars, final OutputStream out) throws IOException {
        return write(chars, out, UTF_8);
    }

    /**
     * Writes a character sequence to the given output stream using the specified charset.
     * <p>
     * Does not close the stream.
     * 
     * @param chars   the character sequence to write
     * @param out     the given {@code OutputStream}
     * @param charset the character set to use when writing the chars
     * @return the given {@code OutputStream}
     * @throws IOException if an I/O error occurs
     */
    public static OutputStream write(final CharSequence chars, final OutputStream out, final Charset charset) throws IOException {
        checkNotNull(out, "out == null");
        checkNotNull(charset, "charset == null");
        write(chars, new OutputStreamWriter(out, charset));
        return out;
    }

    /**
     * Writes lines of text to the given {@link Appendable} target (with each line, including the last, terminated with the
     * operating system's default line separator).
     * <p>
     * Does not attempt close the target even if it is {@link Closeable}.
     * 
     * @param lines the lines of text to be written to the output stream
     * @param out   the given {@link Appendable} target
     * @return the given {@link Appendable} target
     * @throws IOException if an I/O error occurs
     */
    public static Appendable write(final Iterable<? extends CharSequence> lines, final Appendable out) throws IOException {
        checkNotNull(lines, "lines == null");
        checkNotNull(out, "out == null");
        write(lines.iterator(), out);
        return out;
    }

    /**
     * Writes lines of text to the given output stream (with each line, including the last, terminated with the operating
     * system's default line separator) using the UTF-8 charset.
     * <p>
     * Does not close the stream.
     * 
     * @param lines the lines of text to be written to the output stream
     * @param out   the given {@code OutputStream}
     * @return the given {@code OutputStream}
     * @throws IOException if an I/O error occurs
     */
    public static OutputStream write(final Iterable<? extends CharSequence> lines, final OutputStream out) throws IOException {
        return write(lines, out, UTF_8);
    }

    /**
     * Writes lines of text to the given output stream (with each line, including the last, terminated with the operating
     * system's default line separator) using the specified charset.
     * <p>
     * Does not close the stream.
     * 
     * @param lines   the lines of text to be written to the output stream
     * @param out     the given {@code OutputStream}
     * @param charset the character set to use when writing the lines
     * @return the given {@code OutputStream}
     * @throws IOException if an I/O error occurs
     */
    public static OutputStream write(final Iterable<? extends CharSequence> lines, final OutputStream out, final Charset charset) throws IOException {
        checkNotNull(out, "out == null");
        checkNotNull(charset, "charset == null");
        write(lines, new OutputStreamWriter(out, charset));
        return out;
    }

    /**
     * Writes lines of text to the given {@link Appendable} target (with each line, including the last, terminated with the
     * operating system's default line separator).
     * <p>
     * Does not attempt close the target even if it is {@link Closeable}.
     * 
     * @param lines the lines of text to be written to the output stream
     * @param out   the given {@link Appendable} target
     * @return the given {@link Appendable} target
     * @throws IOException if an I/O error occurs
     */
    public static Appendable write(final Stream<? extends CharSequence> lines, final Appendable out) throws IOException {
        checkNotNull(lines, "lines == null");
        checkNotNull(out, "out == null");
        write(lines.iterator(), out);
        return out;
    }

    /**
     * Writes lines of text to the given output stream (with each line, including the last, terminated with the operating
     * system's default line separator) using the UTF-8 charset.
     * <p>
     * Does not close the stream.
     * 
     * @param lines the lines of text to be written to the output stream
     * @param out   the given {@code OutputStream}
     * @return the given {@code OutputStream}
     * @throws IOException if an I/O error occurs
     */
    public static OutputStream write(final Stream<? extends CharSequence> lines, final OutputStream out) throws IOException {
        return write(lines, out, UTF_8);
    }

    /**
     * Writes lines of text to the given output stream (with each line, including the last, terminated with the operating
     * system's default line separator) using the specified charset.
     * <p>
     * Does not close the stream.
     * 
     * @param lines   the lines of text to be written to the output stream
     * @param out     the given {@code OutputStream}
     * @param charset the character set to use when writing the lines
     * @return the given {@code OutputStream}
     * @throws IOException if an I/O error occurs
     */
    public static OutputStream write(final Stream<? extends CharSequence> lines, final OutputStream out, final Charset charset) throws IOException {
        checkNotNull(lines, "lines == null");
        checkNotNull(out, "out == null");
        checkNotNull(charset, "charset == null");
        write(lines.iterator(), new OutputStreamWriter(out, charset));
        return out;
    }

    private static void write(final Iterator<? extends CharSequence> lines, final Appendable out) throws IOException {
        @SuppressWarnings("resource")
        final Writer writer = out instanceof BufferedWriter ? (BufferedWriter) out : new BufferedWriter(CharStreams.asWriter(out));
        while (lines.hasNext())
            writer.append(lines.next()).append(LINE_SEPARATOR);
        writer.flush();
    }

}