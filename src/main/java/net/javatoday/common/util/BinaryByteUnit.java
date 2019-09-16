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
package net.javatoday.common.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.math.DoubleMath;

/**
 * Defines the commonly used binary prefixes specified by
 * <a href="https://en.wikipedia.org/wiki/JEDEC_memory_standards#JEDEC_Standard_100B.01" target="_blank">JEDEC Standard
 * 100B.01</a> to represent the <i>power of 2</i> byte sizes of the filesystem and memory.
 * <p>
 * This {@code Enum} is a poor attempt to deal with the surprisingly
 * <a href="https://en.wikipedia.org/wiki/Wikipedia:Manual_of_Style/Dates_and_numbers#Quantities_of_bytes_and_bits"
 * target="_blank" >difficult issue</a> of representing the size of digital quantities.
 * <p>
 * For example:
 * 
 * <pre>
 * System.out.println(BinaryByteUnit.MEGABYTES.convert(2.5, BinaryByteUnit.KILOBYTES)); // prints 2560
 * </pre>
 * 
 * @author Zhenya Leonov
 */
public enum BinaryByteUnit {

    /**
     * The base unit of information, a bit can have only one of two values, 0 or 1.
     */
    BITS(1F / 8, "b"),

    /**
     * A single byte consists of 8 bits.
     */
    BYTES(1, "B"),

    /**
     * A kilobyte (KB) consists of 1024 bytes.
     */
    KILOBYTES(1024, "KB"),

    /**
     * A megabyte (MB) consists of 1024 kilobytes.
     */
    MEGABYTES(KILOBYTES.base * 1024, "MB"),

    /**
     * A gigabyte (GB) consists of 1024 megabytes.
     */
    GIGABYTES(MEGABYTES.base * 1024, "GB"),

    /**
     * A terabyte (TB) consists of 1024 gigabytes.
     */
    TERABYTES(MEGABYTES.base * 1024, "TB"),

    /**
     * A petabyte (PT) consists of 1024 terabytes.
     */
    PETABYTES(TERABYTES.base * 1024, "PT");

    private static final ConcurrentMap<Locale, NumberFormat> formats = new ConcurrentHashMap<>();

    private final float base;
    private final String prefix;

    private BinaryByteUnit(final float base, final String prefix) {
        this.base = base;
        this.prefix = prefix;
    }

    /**
     * Converts the given value from this {@code BinaryByteUnit} to the specified {@code BinaryByteUnit}.
     * 
     * @param value the value to convert
     * @param unit  the specified {@code BinaryByteUnit}
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@code BinaryByteUnit}
     * @throws ArithmeticException if the result is not finite
     */
    public Number convert(final double value, final BinaryByteUnit unit) {
        checkNotNull(unit, "unit == null");
        checkArgument(Double.isFinite(value), value);
        checkArgument(Double.doubleToRawLongBits(value) >= 0, "value < 0");
        checkArgument(this != BinaryByteUnit.BITS || DoubleMath.isMathematicalInteger(value), "invalid value: %s bits", value);

        final double f = (value * base) / unit.base;

        if (Double.isInfinite(f))
            throw new ArithmeticException();

        final Double n = unit == BinaryByteUnit.BITS ? Math.ceil(f) : f;

        if (DoubleMath.isMathematicalInteger(n))
            return n.longValue();
        else
            return n;
    }

    /**
     * Shorthand for {@link #convert(double, BinaryByteUnit) convert(value, BinaryByteUnit.BITS)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@link BinaryByteUnit#BITS}
     */
    public Number toBits(final double value) {
        return convert(value, BinaryByteUnit.BITS);
    }

    /**
     * Shorthand for {@link #convert(double, BinaryByteUnit) convert(value, BinaryByteUnit.BYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@link BinaryByteUnit#BYTES}
     */
    public Number toBytes(final double value) {
        return convert(value, BinaryByteUnit.BYTES);
    }

    /**
     * Shorthand for {@link #convert(double, BinaryByteUnit) convert(value, BinaryByteUnit.KILOBYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@link BinaryByteUnit#KILOBYTES}
     */
    public Number toKillobytes(final double value) {
        return convert(value, BinaryByteUnit.KILOBYTES);
    }

    /**
     * Shorthand for {@link #convert(double, BinaryByteUnit) convert(value, BinaryByteUnit.MEGABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@link BinaryByteUnit#MEGABYTES}
     */
    public Number toMegabytes(final double value) {
        return convert(value, BinaryByteUnit.MEGABYTES);
    }

    /**
     * Shorthand for {@link #convert(double, BinaryByteUnit) convert(value, BinaryByteUnit.GIGABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@link BinaryByteUnit#GIGABYTES}
     */
    public Number toGigabytes(final double value) {
        return convert(value, BinaryByteUnit.GIGABYTES);
    }

    /**
     * Shorthand for {@link #convert(double, BinaryByteUnit) convert(value, BinaryByteUnit.TERABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@link BinaryByteUnit#TERABYTES}
     */
    public Number toTerabytes(final double value) {
        return convert(value, BinaryByteUnit.TERABYTES);
    }

    /**
     * Shorthand for {@link #convert(double, BinaryByteUnit) convert(value, BinaryByteUnit.PETABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code BinaryByteUnit} to the specified {@link BinaryByteUnit#PETABYTES}
     */
    public Number toPetabytes(final double value) {
        return convert(value, BinaryByteUnit.PETABYTES);
    }

    /**
     * Returns the prefix of this {@code BinaryByteUnit} as defined by the
     * <a href="https://en.wikipedia.org/wiki/JEDEC_memory_standards#JEDEC_Standard_100B.01" target="_blank">JEDEC Standard
     * 100B.01</a> specification.
     * 
     * @return the prefix of this {@code BinaryByteUnit} as defined by the
     *         <a href="https://en.wikipedia.org/wiki/JEDEC_memory_standards#JEDEC_Standard_100B.01" target="_blank">JEDEC
     *         Standard 100B.01</a> specification
     */
    @Override
    public String toString() {
        return prefix;
    }

    /**
     * Formats the specified {@code value} into a human-readable string.
     * 
     * @param value the specified value
     * @param unit  the unit of the value
     * @return a human-readable string representing the specified value in the given unit
     */
    public static String format(double value, final BinaryByteUnit unit) {
        return format(value, unit, Locale.getDefault());
    }

    /**
     * Formats the specified {@code value} into a human-readable string.
     * 
     * @param value  the specified value
     * @param unit   the unit of the value
     * @param locale the {@link Locale} to use when formatting the value
     * @return a human-readable string representing the specified value in the given unit
     */
    public static String format(double value, final BinaryByteUnit unit, final Locale locale) {
        return format(value, unit, formats.computeIfAbsent(locale, k -> {
            final NumberFormat format = NumberFormat.getNumberInstance(locale);
            format.setMaximumFractionDigits(2);
            return format;
        }));
    }

    /**
     * Formats the specified {@code value} into a human-readable string.
     * 
     * @param value  the specified value
     * @param unit   the unit of the value
     * @param format the {@link NumberFormat} to use when formatting the value
     * @return a human-readable string representing the specified value in the given unit
     */
    public static String format(double value, final BinaryByteUnit unit, final NumberFormat format) {
        checkNotNull(unit, "unit == null");
        checkNotNull(format, "format == null");
        checkArgument(Double.isFinite(value), value);
        checkArgument(Double.doubleToRawLongBits(value) >= 0, "value < 0");
        checkArgument(unit != BinaryByteUnit.BITS || DoubleMath.isMathematicalInteger(value), "invalid value: %s bits", value);

        int index = unit.ordinal();
        final int length = values().length;
        final int base = value >= 1 ? unit == BinaryByteUnit.BITS ? 8 : 1024 : unit == BinaryByteUnit.BYTES ? 8 : 1024;

        if (value >= 1)
            while (value >= base && index++ < length - 1)
                value /= base;
        else
            while (value != 0 && value < 1 && index-- < length)
                value *= base;

        return format.format(value) + values()[index > length - 1 ? length - 1 : index];
    }
    
}