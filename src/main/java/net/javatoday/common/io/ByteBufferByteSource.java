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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.common.io.ByteSource;

/**
 * A {@link ByteSource readable source of bytes} from a {@link ByteBuffer}.
 * <p>
 * {@code InputStream}s return by this class are thread-safe and can be used to independently read from the same buffer.
 * 
 * @author Zhenya Leonov
 */
final public class ByteBufferByteSource extends ByteSource {

    private final ByteBuffer buff;

    private ByteBufferByteSource(final ByteBuffer buff) {
        this.buff = buff;
    }

    /**
     * Returns a new {@code ByteBufferByteSource} which opens streams to the specified byte buffer.
     * 
     * @param buff the specified byte buffer
     * @return a new {@code ByteBufferByteSource} which opens streams to the specified byte buffer
     */
    public static ByteBufferByteSource create(final ByteBuffer buff) {
        checkNotNull(buff, "buff == null");
        return new ByteBufferByteSource(buff);
    }

    /**
     * Opens a new {@link ByteBufferInputStream} which reads from the byte buffer.
     * 
     * @return a new {@code ByteBufferInputStream} which reads from the byte buffer
     * @throws IOException if an I/O error occurs
     */
    @Override
    public ByteBufferInputStream openStream() throws IOException {
        return new ByteBufferInputStream(buff);
    }

//  /**
//  * This method simply delegates to {@link #openStream()}.
//  * 
//  * @return a new {@code ByteBufferInputStream} which reads from the byte buffer
//  * @throws IOException if an I/O error occurs
//  */
// Do we benefit from buffering here? Or should we simply delegate to openStream()
    @Override
    public BufferedInputStream openBufferedStream() throws IOException {
        return (BufferedInputStream) super.openBufferedStream();
    }

}