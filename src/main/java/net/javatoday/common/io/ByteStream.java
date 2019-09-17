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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import net.javatoday.common.base.MessageDigests;

/**
 * A utility class useful for working with byte streams.
 * 
 * @see ByteStreams
 * @see CharStreams
 * @author Zhenya Leonov
 */
final public class ByteStream {

    private final static int DEFAULT_BUFFER_SIZE = 8192;

    private ByteStream() {
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
     *             Guava's robust {@link Hashing} facility.
     *
     * @param in     the given input stream
     * @param digest the specified message digest object
     * @return the result of {@link MessageDigest#digest()} for all the bytes read from the given input stream
     * @throws IOException if an I/O error occurs
     */
    public static byte[] digest(final InputStream in, final MessageDigest digest) throws IOException {
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
     * <b>Note</b>: The underlying input stream must be non-blocking and finite.
     * 
     * @param in the specified input stream
     * @return a byte array containing all the bytes read from the input stream
     * @throws IOException if an I/O error occurs
     */
    public static byte[] readBytes(final InputStream in) throws IOException {
        checkNotNull(in, "in == null");
        return readBytes(in, DEFAULT_BUFFER_SIZE);
    }

    static byte[] readBytes(final InputStream in, long size) throws IOException {

        // size is a suggestion but it is not guaranteed to be accurate so we don't throw an OOME if it's too large
        byte[] bytes = new byte[size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size];
        int total = 0;
        int n;

        do {
            total += (n = in.read(bytes, total, (int) size - total));

            if ((n = in.read()) != -1) {
                bytes = Arrays.copyOf(bytes, (size *= 2) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                bytes[total++] = (byte) n;
            }
        } while (n != -1);

        if (size != total)
            bytes = Arrays.copyOf(bytes, total);

        return bytes;
    }

}
