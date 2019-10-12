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

import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * Static utility methods for working with {@link Integer}s, {@link Long}s, {@link Float}s, and {@link Double}s.
 * 
 * @author Zhenya Leonov
 * @see <a target="_blank" href="https://github.com/google/guava/wiki/PrimitivesExplained">PrimitivesExplained</a>
 */
public final class Numbers {

    private Numbers() {
    }

    private static final Random R = new Random();

    /**
     * Returns a pseudorandom, uniformly distributed value between the given {@code least} value (inclusive) and
     * {@code bound} (exclusive) using a default {@link Random} instance.
     * <p>
     * <b>Note:</b> In most cases you should no longer use {@link Random}. {@link ThreadLocalRandom} is much quicker and
     * produces higher quality random numbers. {@link SplittableRandom} is available for use in {@link ForkJoinPool}s and
     * {@link Stream#parallel() parallel} streams. Only use {@link Random} if you plan to share it across threads (it is
     * thread-safe at the cost of extremely poor performance during high contention).
     * 
     * @deprecated Java 8+ users should use {@link ThreadLocalRandom#nextInt(int, int)} or
     *             {@link SplittableRandom#nextInt(int, int)}.
     * 
     * @param least the least value
     * @param bound the upper bound (exclusive)
     * @return a pseudorandom, uniformly distributed value between the given {@code least} value (inclusive) and
     *         {@code bound} (exclusive)
     * @throws IllegalArgumentException if {@code least} < 0 or {@code bound} < 0 or {@code least} >= {@code bound}
     */
    public static int nextRandomInt(final int least, final int bound) {
        return nextRandomInt(R, least, bound);
    }

    /**
     * Returns a pseudorandom, uniformly distributed value between the given {@code least} value (inclusive) and
     * {@code bound} (exclusive) using the specified {@link Random} instance.
     * <p>
     * <b>Note:</b> In most cases you should no longer use {@link Random}. {@link ThreadLocalRandom} is much quicker and
     * produces higher quality random numbers. {@link SplittableRandom} is available for use in {@link ForkJoinPool}s and
     * {@link Stream#parallel() parallel} streams. Only use {@link Random} if you plan to share it across threads (it is
     * thread-safe at the cost of extremely poor performance during high contention).
     * 
     * @deprecated Java 8+ users should use {@link ThreadLocalRandom#nextInt(int, int)} or
     *             {@link SplittableRandom#nextInt(int, int)}.
     * 
     * @param random the {@code Random} instance to use to generate random integers
     * @param least  the least value
     * @param bound  the upper bound (exclusive)
     * @return a pseudorandom, uniformly distributed value between the given {@code least} value (inclusive) and
     *         {@code bound} (exclusive)
     * @throws IllegalArgumentException if {@code least} < 0 or {@code bound} < 0 or {@code least} >= {@code bound}
     */
    public static int nextRandomInt(final Random random, final int least, final int bound) {
        checkNotNull(random, "random == null");
        checkArgument(least >= 0, "least < 0");
        checkArgument(bound >= 0, "bound < 0");
        checkArgument(least < bound, "least >= bound");
        return least + random.nextInt(bound - least);
    }

    /**
     * Parses the specified string as a signed decimal integer value. The ASCII character {@code '-'}
     * (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     * <p>
     * See Guava's {@link Ints#tryParse(String)} for more details.
     *
     * @param str          the string representation of an integer value
     * @param defaultValue the possibly {@code null} value to return if the parsing fails
     * @return the integer value represented by {@code str}, or {@code defaultValue} if {@code str} has a length of zero or
     *         cannot be parsed as an integer value
     */
    public static Integer tryParse(final String str, final Integer defaultValue) {
        checkNotNull(str, "str == null");
        final Integer result = Ints.tryParse(str);
        return result == null ? defaultValue : result;
    }

    /**
     * Parses the specified string as a signed decimal long value. The ASCII character {@code '-'}
     * (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     * <p>
     * See Guava's {@link Longs#tryParse(String)} for more details.
     *
     * @param str          the string representation of a long value
     * @param defaultValue the possibly {@code null} value to return if the parsing fails
     * @return the long value represented by {@code str}, or {@code defaultValue} if {@code str} has a length of zero or
     *         cannot be parsed as an long value
     */
    public static Long tryParse(final String str, final Long defaultValue) {
        checkNotNull(str, "str == null");
        final Long result = Longs.tryParse(str);
        return result == null ? defaultValue : result;
    }

    /**
     * Parses the specified string as a single-precision floating point value. The ASCII character {@code '-'}
     * (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     * <p>
     * See Guava's {@link Floats#tryParse(String)} for more details.
     *
     * @param str          the string representation of a float value
     * @param defaultValue the possibly {@code null} value to return if the parsing fails
     * @return the float value represented by {@code str}, or {@code defaultValue} if {@code str} has a length of zero or
     *         cannot be parsed as an float value
     */
    public static Float tryParse(final String str, final Float defaultValue) {
        checkNotNull(str, "str == null");
        final Float result = Floats.tryParse(str);
        return result == null ? defaultValue : result;
    }

    /**
     * Parses the specified string as a double-precision floating point value. The ASCII character {@code '-'}
     * (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     * <p>
     * See Guava's {@link Doubles#tryParse(String)} for more details.
     *
     * @param str          the string representation of a double value
     * @param defaultValue the possibly {@code null} value to return if the parsing fails
     * @return the double value represented by {@code str}, or {@code defaultValue} if {@code str} has a length of zero or
     *         cannot be parsed as an double value
     */
    public static Double tryParse(final String str, final Double defaultValue) {
        checkNotNull(str, "str == null");
        final Double result = Doubles.tryParse(str);
        return result == null ? defaultValue : result;
    }

}
