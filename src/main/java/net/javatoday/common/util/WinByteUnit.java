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
package net.javatoday.common.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents the binary prefixes used by the Windows family of operating systems to report the <i>power of 2</i> byte
 * sizes of the filesystem and memory.
 * <p>
 * This {@code Enum} is a poor attempt to deal with the surprisingly
 * <a href="https://en.wikipedia.org/wiki/Wikipedia:Manual_of_Style/Dates_and_numbers#Quantities_of_bytes_and_bits"
 * target="_blank" >difficult issue</a> of representing the size of digital quantities.
 * <p>
 * For example:
 * 
 * <pre>
 * System.out.println(WinByteUnit.MEGABYTES.convert(2, WinByteUnit.KILOBYTES)); // prints 2048
 * </pre>
 * 
 * @author Zhenya Leonov
 */
public enum WinByteUnit {

    /**
     * The base unit of information, a bit can have only one of two values, 0 or 1.
     */
    BITS(1F / 8, "b"),

    /**
     * A single byte (B) consists of 8 bits.
     */
    BYTES(1L, "B"),

    /**
     * A kilobyte (KB) consists of 1024 bytes.
     */
    KILOBYTES(1024L, "KB"),

    /**
     * A megabyte (MB) consists of 1024 kilobytes.
     */
    MEGABYTES(1024L * 1024L, "MB"),

    /**
     * A gigabyte (GB) consists of 1024 megabytes.
     */
    GIGABYTES(1024L * 1024L * 1024L, "GB"),

    /**
     * A terabyte (TB) consists of 1024 gigabytes.
     */
    TERABYTES(1024L * 1024L * 1024L * 1024L, "TB"),

    /**
     * A petabyte (PT) consists of 1024 terabytes.
     */
    PETABYTES(1024L * 1024L * 1024L * 1024L * 1024L, "PT");

    private final float bytes;
    private final String symbol;

    WinByteUnit(final float bytes, final String symbol) {
        this.bytes = bytes;
        this.symbol = symbol;
    }

    /**
     * Converts the given value from this {@code WinByteUnit} to the requested {@code WinByteUnit}.
     * <p>
     * This method will attempt to maintain precision when converting from finer to coarser granularities. As a result the
     * returned {@code Number} may be an instance of {@code Float} or {@code Long}.
     * 
     * @param value the value to convert
     * @param unit  the request {@code WinByteUnit}
     * @return the given value converted from this {@code WinByteUnit} to the requested {@code WinByteUnit}
     */
    public Number convert(final long value, final WinByteUnit unit) {
        checkNotNull(unit, "unit == null");
        checkArgument(value >= 0, "value < 0");
        final float f = (value * bytes) / unit.bytes;
        if (f == (long) f)
            return (long) f;
        else
            return (float) f;
    }

    @Override
    public String toString() {
        return symbol;
    }

}