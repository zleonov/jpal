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
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.zip.Checksum;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Ints;

import software.leonov.common.base.MessageDigests;

/**
 * A utility class useful for working with byte streams.
 * 
 * @see ByteStreams
 * @see CharStreams
 * @author Zhenya Leonov
 */
@SuppressWarnings("deprecation")
final public class ByteStream {

    private final static int DEFAULT_BUFFER_SIZE = 8192;

    private ByteStream() {
    }

    /**
     * Computes and returns the checksum value for all the bytes read from given input stream using the specified checksum
     * object.
     * <p>
     * Does not close the stream.
     * <p>
     * The {@code Checksum} is reset when this method returns successfully.
     * 
     * @deprecated Users not working with legacy APIs should prefer {@link #hash(InputStream, HashFunction)
     *             hash(InputStream, }{@link Hashing#crc32() Hashing.crc32())}{@link HashCode#padToLong() .padToLong()} or
     *             {@link #hash(InputStream, HashFunction) hash(InputStream, }{@link Hashing#adler32()
     *             Hashing.adler32())}{@link HashCode#padToLong() .padToLong()} which uses Guava's
     *             <a target="_blank" href="https://github.com/google/guava/wiki/HashingExplained">Hashing facility</a>.
     *
     * @param in       the given input stream
     * @param checksum the specified message digest object
     * @return the result of {@link Checksum#getValue()} for all the bytes read from the given input stream
     * @throws IOException if an I/O error occurs
     */
    public static long getChecksum(final InputStream in, final Checksum checksum) throws IOException {
        checkNotNull(in, "in == null");
        checkNotNull(checksum, "checksum == null");

        final byte[] buff = new byte[DEFAULT_BUFFER_SIZE];

        for (int r = in.read(buff); r != -1; r = in.read(buff))
            checksum.update(buff, 0, r);

        final long value = checksum.getValue();
        checksum.reset();
        return value;
    }

    /**
     * Computes and returns the digest value for all the bytes read from given input stream using the specified message
     * digest object.
     * <p>
     * Does not close the stream.
     * <p>
     * Invoke {@link MessageDigests#toString(byte[])} to get a lower case hexadecimal string representation of the digest
     * value which conveniently matches the output of Unix-like commands such as {@code md5sum}.
     * <p>
     * The {@code MessageDigest} is reset when this method returns successfully.
     * 
     * @deprecated Users not working with legacy APIs should prefer {@link #hash(InputStream, HashFunction)} which uses
     *             Guava's <a target="_blank" href="https://github.com/google/guava/wiki/HashingExplained">Hashing
     *             facility</a>.
     *
     * @param in     the given input stream
     * @param digest the specified message digest object
     * @return the result of {@link MessageDigest#digest()} for all the bytes read from the given input stream
     * @throws IOException if an I/O error occurs
     */
    public static byte[] getDigest(final InputStream in, final MessageDigest digest) throws IOException {
        checkNotNull(in, "in == null");
        checkNotNull(digest, "digest == null");

        final byte[] buff = new byte[DEFAULT_BUFFER_SIZE];

        for (int r = in.read(buff); r != -1; r = in.read(buff))
            digest.update(buff, 0, r);

        return digest.digest();
    }

    /**
     * Computes and returns the {@code HashCode} for all the bytes read from the given input stream using the specified hash
     * function.
     * <p>
     * Invoke {@link HashCode#asBytes()} to get the hash code value as a byte array or {@link HashCode#toString()} for a
     * lower case hexadecimal string representation which conveniently matches the output of Unix-like commands such as
     * {@code md5sum}.
     * 
     * @param in   the given input stream
     * @param func the specified hash function
     * @return the {@code HashCode} for all the bytes read from the given input stream using the specified hash function
     * @throws IOException if an I/O error occurs
     */
    public static HashCode hash(final InputStream in, final HashFunction func) throws IOException {
        checkNotNull(in, "in == null");
        checkNotNull(func, "func == null");

        final byte[] buff = new byte[DEFAULT_BUFFER_SIZE];
        final Hasher hasher = func.newHasher();

        for (int r = in.read(buff); r != -1; r = in.read(buff))
            hasher.putBytes(buff, 0, r);

        return hasher.hash();
    }

    /**
     * Returns a byte array containing all the bytes read from the specified input stream.
     * <p>
     * Does not close the stream.
     * <p>
     * <b>Note:</b> The underlying input stream must be non-blocking and finite.
     * 
     * @param in the specified input stream
     * @return a byte array containing all the bytes read from the input stream
     * @throws IOException if an I/O error occurs
     */
    public static byte[] toByteArray(final InputStream in) throws IOException {
        checkNotNull(in, "in == null");
        return toByteArray(in, DEFAULT_BUFFER_SIZE);
    }

    static byte[] toByteArray(final InputStream in, final long size) throws IOException {

        // size is a suggestion but it is not guaranteed to be accurate so we don't throw an OOME if it's too large
        int length = size < 1 ? DEFAULT_BUFFER_SIZE : Ints.saturatedCast(size); // do we need the saturated cast?
        byte[] bytes = new byte[length];
        int total = 0;
        int n;

        do {
            /*
             * a loop is required because we are not guaranteed that InputStream.read will return all the requested bytes in a
             * single call, even if they are available
             */
            while ((n = in.read(bytes, total, length - total)) > 0)
                total += n;

            if ((n = in.read()) != -1) {
                bytes = Arrays.copyOf(bytes, (length *= 2) > Integer.MAX_VALUE ? Integer.MAX_VALUE : length);
                bytes[total++] = (byte) n;
            }
        } while (n != -1);

        return bytes.length == total ? bytes : Arrays.copyOf(bytes, total);
    }

}

