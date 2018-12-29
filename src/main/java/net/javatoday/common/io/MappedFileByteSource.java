/*
<<<<<<< HEAD
 * Copyright (C) 2019 Zhenya Leonov
=======
 * Copyright (C) 2018 Zhenya Leonov
>>>>>>> branch 'master' of git@github.com:zleonov/jpal.git
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.google.common.io.ByteSource;
import com.google.common.io.Closer;

/**
 * A {@link ByteSource readable source of bytes} from one or more {@link MappedByteBuffer}s backed by a file. Files
 * whose size exceeds 2GB are supported.
 * 
 * @author Zhenya Leonov
 */
final public class MappedFileByteSource extends ByteSource {

    private static final long PAGE_SIZE = Integer.MAX_VALUE;

    private final ByteBuffer[] buffers;
    private final long capacity;

    private MappedFileByteSource(final File file) throws IOException {
        capacity = file.length();

        final Closer closer = Closer.create();

        try {
            final RandomAccessFile raf = closer.register(new RandomAccessFile(file, "r"));
            final FileChannel channel = closer.register(raf.getChannel());

            buffers = new ByteBuffer[(int) ((capacity + PAGE_SIZE - 1) / PAGE_SIZE)];

            for (int i = 0; i < buffers.length; i++)
                buffers[i] = channel.map(MapMode.READ_ONLY, i * PAGE_SIZE, Math.min(PAGE_SIZE, capacity - i * PAGE_SIZE));
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Creates a new {@code MappedFileByteSource} backed by one or more {@code MappedByteBuffer}s mapped to the specified
     * file.
     * 
     * @param file the specified file
     * @return a new {@code MappedFileByteSource} backed by one or more {@code MappedByteBuffer}s mapped to the specified
     *         file
     * @throws IOException if an I/O error occurs
     */
    public static MappedFileByteSource create(final File file) throws IOException {
        checkNotNull(file, "file == null");
        checkArgument(file.isFile(), "%s is not a normal file or does not exist", file.getPath());
        return new MappedFileByteSource(file);
    }

    /**
     * Opens a new {@link MappedFileInputStream} which reads from this {@code MappedFileByteSource}.
     *
     * @throws IOException if an I/O error occurs
     * @return a new {@link MappedFileInputStream} which reads from this {@code MappedFileByteSource}
     */
    @Override
    public MappedFileInputStream openStream() throws IOException {
        return new MappedFileInputStream(buffers, capacity, (int) PAGE_SIZE);
    }

//  /**
//  * This method simply delegates to {@link #openStream()}.
//  * 
//  * @return a new {@code ByteBufferInputStream} which reads from the byte buffer
//  * @throws IOException if an I/O error occurs
//  */
    @Override
    public BufferedInputStream openBufferedStream() throws IOException {
        return (BufferedInputStream) super.openBufferedStream();
    }

}