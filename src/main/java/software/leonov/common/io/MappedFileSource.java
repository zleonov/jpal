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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.google.common.io.ByteSource;

/**
 * A {@link ByteSource readable source of bytes} from one or more {@link MappedByteBuffer}s backed by a file. Files
 * whose size exceeds 2GB are supported.
 * 
 * @author Zhenya Leonov
 */
final public class MappedFileSource extends ByteSource {

    private static final long PAGE_SIZE = Integer.MAX_VALUE;

    private final ByteBuffer[] buffers;
    private final long capacity;

    /**
     * Creates a new {@code MappedFileSource} backed by one or more {@code MappedByteBuffer}s mapped to the specified
     * file.
     * 
     * @param path the specified file
     * @throws IOException if an I/O error occurs
     */
    public MappedFileSource(final Path path) throws IOException {
        checkNotNull(path, "path == null");
        capacity = Files.size(path);

        try (final FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {

            buffers = new ByteBuffer[(int) ((capacity + PAGE_SIZE - 1) / PAGE_SIZE)];

            for (int i = 0; i < buffers.length; i++)
                buffers[i] = channel.map(MapMode.READ_ONLY, i * PAGE_SIZE, Math.min(PAGE_SIZE, capacity - i * PAGE_SIZE));
        }
    }

    /**
     * Opens a new {@link MappedFileInputStream} which reads from this {@code MappedFileSource}.
     *
     * @throws IOException if an I/O error occurs
     * @return a new {@link MappedFileInputStream} which reads from this {@code MappedFileSource}
     */
    @Override
    public MappedFileInputStream openStream() throws IOException {
        return new MappedFileInputStream(buffers, capacity, (int) PAGE_SIZE);
    }

    /**
     * This method simply delegates to {@link #openStream()}.
     *
     * @return a new {@code MappedFileInputStream} which reads from the byte buffer
     * @throws IOException if an I/O error occurs
     */
    @Override
    public MappedFileInputStream openBufferedStream() throws IOException {
        // Do we benefit from buffering here? Or should we simply delegate to openStream()
        // return (BufferedInputStream) super.openBufferedStream();
        return openStream();
    }

}