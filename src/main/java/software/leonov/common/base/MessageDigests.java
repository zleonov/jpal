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
package software.leonov.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Common {@code MessageDigest}s which are supported by all Java platform implementations.
 * 
 * @deprecated Users not working with legacy APIs should prefer Guava's
 *             <a target="_blank" href="https://github.com/google/guava/wiki/HashingExplained">Hashing facility</a>.
 * 
 * @author Zhenya Leonov
 */
public final class MessageDigests {

    private static final String MD5 = "MD5";
    private static final String SHA_1 = "SHA-1";
    private static final String SHA_256 = "SHA-256";
    private static final String SHA_384 = "SHA-384";
    private static final String SHA_512 = "SHA-512";

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private MessageDigests() {
    }

    /**
     * Returns {@code MessageDigest} instance that implements Message-Digest Algorithm 5.
     * 
     * @return a {@code MessageDigest} instance that implements Message-Digest Algorithm 5
     */
    public static MessageDigest md5() {
        return getInstance(MD5);
    }

    /**
     * Returns {@code MessageDigest} instance that implements Secure Hash Algorithm 1.
     * 
     * @return a {@code MessageDigest} instance that implements Secure Hash Algorithm 1
     */
    public static MessageDigest sha1() {
        return getInstance(SHA_1);
    }

    /**
     * Returns {@code MessageDigest} instance that implements Secure Hash Algorithm 256.
     * 
     * @return a {@code MessageDigest} instance that implements Secure Hash Algorithm 256
     */
    public static MessageDigest sha256() {
        return getInstance(SHA_256);
    }

    /**
     * Returns {@code MessageDigest} instance that implements Secure Hash Algorithm 384.
     * 
     * @return a {@code MessageDigest} instance that implements Secure Hash Algorithm 384
     */
    public static MessageDigest sha384() {
        return getInstance(SHA_384);
    }

    /**
     * Returns {@code MessageDigest} instance that implements Secure Hash Algorithm 512.
     * 
     * @return a {@code MessageDigest} instance that implements Secure Hash Algorithm 512
     */
    public static MessageDigest sha512() {
        return getInstance(SHA_512);
    }

    /**
     * Returns a lowercase hexadecimal string representation of the specified byte array.
     * 
     * @param bytes the specified byte array
     * @return a lowercase hexadecimal string representation of the specified byte array
     */
    public static String toString(final byte[] bytes) {
        checkNotNull(bytes, "bytes == null");

        final StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (final byte b : bytes)
            sb.append(HEX[(b >> 4) & 0xF]).append(HEX[(b & 0xF)]);

        return sb.toString();
    }

    /**
     * Returns a lowercase hexadecimal string representation of {@link MessageDigest#digest()}.
     * <p>
     * The {@code MessageDigest} is reset after this method returns.
     * 
     * @param digest the specified {@code MessageDigest}
     * @return a lowercase hexadecimal string representation of {@link MessageDigest#digest()}
     */
    public static String toString(final MessageDigest digest) {
        checkNotNull(digest, "digest == null");

        return toString(digest.digest());
    }

    private static MessageDigest getInstance(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

}