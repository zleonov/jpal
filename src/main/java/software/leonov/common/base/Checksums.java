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

import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Common {@code Checksum}s provided in all Java platform implementations (two available as of Java 8).
 * <p>
 * <b>Note:</b> Java 9 introduced the CRC-32C checksum defined in <a href="https://tools.ietf.org/html/rfc3720">RFC
 * 3720</a>.
 * 
 * @deprecated Users not working with legacy APIs should prefer Guava's
 *             <a target="_blank" href="https://github.com/google/guava/wiki/HashingExplained">Hashing facility</a>.
 * 
 * @author Zhenya Leonov
 */
public final class Checksums {

    private Checksums() {
    }

    /**
     * A new checksum that can be used to compute the CRC-32 of a data stream.
     * 
     * @return a new checksum that can be used to compute the CRC-32 of a data stream
     */
    public static Checksum crc32() {
        return new CRC32();
    }

    /**
     * A new checksum that can be used to compute the Adler-32 checksum of a data stream.
     * <p>
     * An Adler-32 checksum is almost as reliable as a CRC-32 but can be computed much faster.
     * 
     * @return a new checksum that can be used to compute the Adler-32 checksum of a data stream
     */
    public static Checksum adler32() {
        return new Adler32();
    }

}