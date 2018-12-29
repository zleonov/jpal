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

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An {@link InputStream} which reads bytes from a {@link ByteBuffer}.
 * <p>
 * As long as the buffer is not modified externally this class is thread-safe. Multiple instances of this class can be
 * used to independently read from the same buffer.
 *
 * @author Zhenya Leonov
 */

// Buffers are inherently not thread-safe (even accessing a buffer using absolute positioning  from multiple threads is
// not guaranteed to return correct results) so we must use synchronization.
public final class ByteBufferInputStream extends InputStream {

    private final ByteBuffer buff;

    private int position = 0;
    private int mark = 0;
    private int capacity;

    private ByteBufferInputStream(final ByteBuffer buff) {
        this.buff = buff;
        capacity = buff.capacity();
    }

    /**
     * Returns a new {@code ByteBufferInputStream} which reads bytes from the specified {@code ByteBuffer}.
     * 
     * @param buff the specified {@code ByteBuffer}
     * @return a new {@code ByteBufferInputStream} which reads bytes from the specified {@code ByteBuffer}
     */
    public static ByteBufferInputStream create(final ByteBuffer buff) {
        checkNotNull(buff, "buff == null");
        return new ByteBufferInputStream(buff);
    }

    /**
     * Returns the number of remaining bytes that can be read (or skipped over) from this input stream.
     * 
     * @return the number of remaining bytes that can be read (or skipped over) from this input stream
     */
    @Override
    public synchronized int available() {
        return capacity - position;
    }

    /**
     * Reads the next byte of data as an {@code int} from {@code 0} to {@code 255}, or {@code -1} if the end of the stream
     * has been reached.
     *
     * @return the next byte of data as an {@code int} from {@code 0} to {@code 255}, or {@code -1} if the end of the stream
     *         has been reached
     */
    @Override
    public synchronized int read() {
        if (position < capacity)
            return buff.get(position++) & 0xff;
        else
            return -1;
    }

    /**
     * Reads up to {@code length} bytes of data from the input stream into an array of bytes. An attempt is made to read as
     * many as {@code length} bytes, but a smaller number may be read. The number of bytes actually read is returned as an
     * integer.
     * <p>
     * If {@code length} is zero, then no bytes are read and {@code 0} is returned. If no bytes are available then
     * {@code -1} is returned.
     *
     * @param bytes  the buffer into which the data is read
     * @param offset the start offset in array {@code bytes} at which the data is written
     * @param length the maximum number of bytes to read
     * @return the total number of bytes read into the array, or {@code -1} if there is no bytes are available
     * @throws IndexOutOfBoundsException if {@code offset} is negative, {@code length} is negative, or {@code length} is
     *                                   greater than {@code bytes.length - offset}
     */
    @Override
    public synchronized int read(final byte[] bytes, final int offset, int length) {
        checkNotNull(bytes, "bytes == null");

        if (offset < 0)
            throw new IndexOutOfBoundsException("offset < 0");

        if (length < 0)
            throw new IndexOutOfBoundsException("length < 0");

        if (length > bytes.length - offset)
            throw new IndexOutOfBoundsException("length > bytes.length - offset");

        if (length == 0)
            return 0;

        if (position >= capacity)
            return -1;

        if (position + length > capacity)
            length = capacity - position;

        buff.position(position);
        buff.get(bytes, offset, length);

        position += length;
        return length;
    }

    /**
     * Skips over and discards {@code n} bytes of data from this input stream. The skip method may, for a variety of
     * reasons, end up skipping over some smaller number of bytes, possibly 0. This may result from any of a number of
     * conditions; reaching end of file before {@code n} bytes have been skipped is only one possibility. The actual number
     * of bytes skipped is returned. If {@code n} is negative, no bytes are skipped.
     * 
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped
     */
    @Override
    public synchronized long skip(final long n) {
        if (n < 0)
            return 0;

        final int skip = position + n > capacity ? capacity - position : (int) n;

        position += skip;

        return skip;
    }

    /**
     * Tests if the {@code mark(int)} and {@code reset()} operations are supported (always returns {@code true}).
     * 
     * @return {@code true} always
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Marks the current position in the input stream. A subsequent call to the reset method repositions this stream at the
     * last marked position so that subsequent reads re-read the same bytes.
     *
     * @param limit always ignored
     */
    public synchronized void mark(final int limit) {
        mark = position;
    }

    /**
     * Resets this input stream to the previously marked position. The default marked position is zero.
     */
    public synchronized void reset() {
        position = mark;
    }

    /**
     * Closing a {@code ByteBufferInputStream} has no effect.
     */
    @Override
    public void close() {
    }

}