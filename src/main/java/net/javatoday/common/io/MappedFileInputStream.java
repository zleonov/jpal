/*
 * Copyright (C) 2018 Zhenya Leonov
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

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An {@code InputStream} backed by one or more {@code MappedByteBuffer}s. Files whose size exceeds 2GB are supported.
 * <p>
 * Instances of this class can be obtained from {@link MappedFileByteSource#openStream()}.
 * 
 * @author Zhenya Leonov
 */
// lots to optimize
final public class MappedFileInputStream extends InputStream {

    private final int pageSize;
    private final long capacity;

    private final ByteBuffer[] buffers;

    private long mark = 0;
    private long position = 0;

    MappedFileInputStream(final ByteBuffer[] buffers, final long capacity, final int pageSize) {
        checkNotNull(buffers, "buffers == null");
        checkArgument(capacity >= 0, "capacity < 0");
        checkNotNull(pageSize > 0, "pageSize <= 0");

        this.buffers = buffers;
        this.pageSize = pageSize;
        this.capacity = capacity;
    }

    private MappedFileInputStream(final ByteBuffer buff) {
        pageSize = Integer.MAX_VALUE;
        buffers = new ByteBuffer[] { buff };
        capacity = buff.capacity();
    }

    static MappedFileInputStream createForTesting(final ByteBuffer buff) {
        checkNotNull(buff, "buff == null");
        return new MappedFileInputStream(buff);
    }

    /**
     * Returns the number of remaining bytes that can be read (or skipped over) from this input stream.
     * <p>
     * <b>Warning:</b> the {@code InputStream} interface dictates that this method cannot return a value larger than
     * {@code Integer.MAX_VALUE}.
     * 
     * @deprecated Use {@link #remaining()} instead.
     *             <p>
     * @return the number of remaining bytes that can be read (or skipped over) from this input stream
     */
    @Deprecated
    @Override
    public synchronized int available() {
        final long available = capacity - position;
        return Integer.MAX_VALUE > available ? (int) available : Integer.MAX_VALUE;
    }

    /**
     * Returns this input stream's position.
     * 
     * @return this input stream's position
     */
    public synchronized long position() {
        return position;
    }

    /**
     * Sets this input stream's position. If the mark is defined and larger than the new position then it is discarded.
     * 
     * @param position the new position
     * @return this {@code MappedFileInputSream} instance
     */
    public synchronized MappedFileInputStream position(final long position) {
        checkArgument(position >= 0, "position < 0");
        checkArgument(position <= capacity, "position > capacity");

        this.position = position;
        if (mark > position)
            mark = 0;
        return this;
    }

    /**
     * Returns this input stream's capacity.
     *
     * @return the capacity of this input stream
     */
    public long capacity() {
        return capacity;
    }

    /**
     * Returns the number of remaining bytes that can be read (or skipped over) from this input stream.
     * 
     * @return the number of remaining bytes that can be read (or skipped over) from this input stream
     */
    public synchronized long remaining() {
        return capacity - position;
    }

    /**
     * Returns {@code true} if there is at least one more byte that can be read from this buffer, {@code false} otherwise.
     * 
     * @return {@code true} if there is at least one more byte that can be read from this buffer, {@code false} otherwise
     */
    public synchronized boolean hasRemaining() {
        return position < capacity;
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

        final long skip = position + n > capacity ? capacity - position : n;

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
     * Closing a {@code MappedFileInputStream} has no effect.
     */
    @Override
    public void close() {
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
        if (position < capacity) {
            final int i = (int) (position / pageSize);
            final int b = buffers[i].get((int) (position - i * pageSize)) & 0xff;
            position++;
            return b;
        } else
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

        length = (int) Math.min(remaining(), length);
        int count = 0;

        int i = (int) (position / pageSize);
        ByteBuffer buff = buffers[i];
        buff.position((int) (position - i * pageSize));

        while (count < length) {
            int remaining = buff.remaining();

            if (remaining == 0) {
                buff = buffers[++i];
                buff.position(0);
                remaining = buff.remaining();
            }

            buff.get(bytes, offset + count, Math.min(length - count, remaining));
            count += Math.min(length - count, remaining);
        }

        position += count;

        return count;
    }

}
