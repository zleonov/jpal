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
 * Defines the commonly used <a href=https://en.wikipedia.org/wiki/International_System_of_Units target=_blank>SI
 * prefixes</a> to represent the byte sizes of the file system and memory.
 * <p>
 * This {@code Enum} is a poor attempt to deal with the surprisingly
 * <a href="https://en.wikipedia.org/wiki/Wikipedia:Manual_of_Style/Dates_and_numbers#Quantities_of_bytes_and_bits"
 * target="_blank" >difficult issue</a> of representing the size of digital quantities.
 * <p>
 * For example:
 * 
 * <pre>
 * System.out.println(DecimalByteUnit.MEGABYTES.convert(2.5, DecimalByteUnit.KILOBYTES)); // prints 2500
 * </pre>
 * 
 * @author Zhenya Leonov
 */
public enum DecimalByteUnit {

    /**
     * The base unit of information, a bit can have only one of two values, 0 or 1.
     */
    BITS(1F / 8, "b"),

    /**
     * A single byte consists of 8 bits.
     */
    BYTES(1, "B"),

    /**
     * A kilobyte (kB) consists of 1000 bytes.
     */
    KILOBYTES(1000, "kB"),

    /**
     * A megabyte (MB) consists of 1000 kilobytes.
     */
    MEGABYTES(KILOBYTES.base * 1000, "MB"),

    /**
     * A gigabyte (GB) consists of 1000 megabytes.
     */
    GIGABYTES(MEGABYTES.base * 1000, "GB"),

    /**
     * A terabyte (TB) consists of 1000 gigabytes.
     */
    TERABYTES(GIGABYTES.base * 1000, "TB"),

    /**
     * A petabyte (PT) consists of 1000 terabytes.
     */
    PETABYTES(TERABYTES.base * 1000, "PT");

    private static final ConcurrentMap<Locale, NumberFormat> formats = new ConcurrentHashMap<>();

    private final float base;
    private final String prefix;

    private DecimalByteUnit(final float base, final String prefix) {
        this.base = base;
        this.prefix = prefix;
    }

    /**
     * Converts the given value from this {@code DecimalByteUnit} to the specified {@code DecimalByteUnit}.
     * 
     * @param value the value to convert
     * @param unit  the specified {@code DecimalByteUnit}
     * @return the given value converted from this {@code DecimalByteUnit} to the specified {@code DecimalByteUnit}
     * @throws ArithmeticException if the result is not finite
     */
    public Number convert(final double value, final DecimalByteUnit unit) {
        checkNotNull(unit, "unit == null");
        checkArgument(Double.isFinite(value), value);
        checkArgument(Double.doubleToRawLongBits(value) >= 0, "value < 0");
        checkArgument(this != DecimalByteUnit.BITS || DoubleMath.isMathematicalInteger(value), "invalid value: %s bits", value);

        final double f = (value * base) / unit.base;

        if (Double.isInfinite(f))
            throw new ArithmeticException();

        final Double n = unit == DecimalByteUnit.BITS ? Math.ceil(f) : f;

        if (DoubleMath.isMathematicalInteger(n))
            return n.longValue();
        else
            return n;
    }

    /**
     * Shorthand for {@link #convert(double, DecimalByteUnit) convert(value, DecimalByteUnit.BITS)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code DecimalByteUnit} to the specified {@link DecimalByteUnit#BITS}
     */
    public Number toBits(final double value) {
        return convert(value, DecimalByteUnit.BITS);
    }

    /**
     * Shorthand for {@link #convert(double, DecimalByteUnit) convert(value, DecimalByteUnit.BYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code DecimalByteUnit} to the specified {@link DecimalByteUnit#BYTES}
     */
    public Number toBytes(final double value) {
        return convert(value, DecimalByteUnit.BYTES);
    }

    /**
     * Shorthand for {@link #convert(double, DecimalByteUnit) convert(value, DecimalByteUnit.KILOBYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code DecimalByteUnit} to the specified
     *         {@link DecimalByteUnit#KILOBYTES}
     */
    public Number toKillobytes(final double value) {
        return convert(value, DecimalByteUnit.KILOBYTES);
    }

    /**
     * Shorthand for {@link #convert(double, DecimalByteUnit) convert(value, DecimalByteUnit.MEGABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code DecimalByteUnit} to the specified
     *         {@link DecimalByteUnit#MEGABYTES}
     */
    public Number toMegabytes(final double value) {
        return convert(value, DecimalByteUnit.MEGABYTES);
    }

    /**
     * Shorthand for {@link #convert(double, DecimalByteUnit) convert(value, DecimalByteUnit.GIGABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code DecimalByteUnit} to the specified
     *         {@link DecimalByteUnit#GIGABYTES}
     */
    public Number toGigabytes(final double value) {
        return convert(value, DecimalByteUnit.GIGABYTES);
    }

    /**
     * Shorthand for {@link #convert(double, DecimalByteUnit) convert(value, DecimalByteUnit.TERABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code DecimalByteUnit} to the specified
     *         {@link DecimalByteUnit#TERABYTES}
     */
    public Number toTerabytes(final double value) {
        return convert(value, DecimalByteUnit.TERABYTES);
    }

    /**
     * Shorthand for {@link #convert(double, DecimalByteUnit) convert(value, DecimalByteUnit.PETABYTES)}
     * 
     * @param value the value to convert
     * @return the given value converted from this {@code DecimalByteUnit} to the specified
     *         {@link DecimalByteUnit#PETABYTES}
     */
    public Number toPetabytes(final double value) {
        return convert(value, DecimalByteUnit.PETABYTES);
    }

    /**
     * Returns the <a href=https://en.wikipedia.org/wiki/International_System_of_Units target=_blank>SI prefix</a> of this
     * {@code DecimalByteUnit}.
     * 
     * @return the prefix of this {@code DecimalByteUnit} as defined by the
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
    public static String format(double value, final DecimalByteUnit unit) {
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
    public static String format(double value, final DecimalByteUnit unit, final Locale locale) {
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
    public static String format(double value, final DecimalByteUnit unit, final NumberFormat format) {
        checkNotNull(unit, "unit == null");
        checkNotNull(format, "format == null");
        checkArgument(Double.isFinite(value), value);
        checkArgument(Double.doubleToRawLongBits(value) >= 0, "value < 0");
        checkArgument(unit != DecimalByteUnit.BITS || DoubleMath.isMathematicalInteger(value), "invalid value: %s bits", value);

        int index = unit.ordinal();
        final int length = values().length;
        final int base = value >= 1 ? unit == DecimalByteUnit.BITS ? 8 : 1000 : unit == DecimalByteUnit.BYTES ? 8 : 1000;

        if (value >= 1)
            while (value >= base && index++ < length - 1)
                value /= base;
        else
            while (value != 0 && value < 1 && index-- < length)
                value *= base;

        return format.format(value) + values()[index > length - 1 ? length - 1 : index];
    }

    public static void main(String[] args) {
        System.out.println(DecimalByteUnit.MEGABYTES.convert(2.5, DecimalByteUnit.BYTES));
        System.out.println(format(10000000, DecimalByteUnit.TERABYTES));
    }

}