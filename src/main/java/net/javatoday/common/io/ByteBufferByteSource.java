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
        return ByteBufferInputStream.create(buff);
    }

//    /**
//     * This method simply delegates to {@link #openStream()}.
//     * 
//     * @return a new {@code ByteBufferInputStream} which reads from the byte buffer
//     * @throws IOException if an I/O error occurs
//     */
    @Override
    public BufferedInputStream openBufferedStream() throws IOException {
        return (BufferedInputStream) super.openBufferedStream();
    }

}