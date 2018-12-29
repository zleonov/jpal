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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

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

    private static final int BYTE_BUFF_SIZE = 4096;

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
     * <p>
     * <b>Note:</b> Users not working with legacy APIs should prefer {@link #hash(InputStream, HashFunction)} which uses
     * Guava's more robust {@link Hashing} facility.
     *
     * @param in     the given input stream
     * @param digest the specified message digest object
     * @return the result of {@link MessageDigest#digest()} for all the bytes read from the given input stream
     * @throws IOException if an I/O error occurs
     */
    public static byte[] digest(final InputStream in, final MessageDigest digest) throws IOException {
        checkNotNull(in, "in == null");
        checkNotNull(digest, "digest == null");

        final byte[] buff = new byte[BYTE_BUFF_SIZE];

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

        final byte[] buff = new byte[BYTE_BUFF_SIZE];
        final Hasher hasher = func.newHasher();

        for (int r = in.read(buff); r != -1; r = in.read(buff))
            hasher.putBytes(buff, 0, r);

        return hasher.hash();
    }

    /**
     * Returns a {@code ByteArrayInputStream} containing all the bytes read from the specified input stream.
     * <p>
     * Does not close the stream.
     * <p>
     * <b>Note</b>: The returned {@code ByteArrayInputStream} is <i>not</i> lazy. The underlying input stream must be non
     * blocking and finite.
     * 
     * @param in the specified input stream
     * @return a {@code ByteArrayInputStream} containing all the bytes read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static ByteArrayInputStream newByteArrayInputStream(final InputStream in) throws IOException {
        checkNotNull(in, "in == null");
        return in instanceof ByteArrayInputStream ? (ByteArrayInputStream) in : new ByteArrayInputStream(ByteStreams.toByteArray(in));
    }

}
