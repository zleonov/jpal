package net.javatoday.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.hash.Hashing;

/**
 * Common {@code MessageDigest}s which are supported by all Java platform implementations.
 * <p>
 * <b>Note:</b> Users not working with legacy APIs should prefer Guava's more robust {@link Hashing} facility.
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
     * <p>
     * The digest is reset after this call is made.
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
     * @param md the specified {@code MessageDigest}
     * @return a lowercase hexadecimal string representation of {@link MessageDigest#digest()}
     */
    public static String toString(final MessageDigest md) {
        checkNotNull(md, "md == null");

        return toString(md.digest());
    }

    private static MessageDigest getInstance(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

}