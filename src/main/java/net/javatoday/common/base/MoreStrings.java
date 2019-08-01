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
package net.javatoday.common.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Static utility methods that operate on or return {@link String}s.
 * <p>
 * All methods in this class are expected to throw {@code NullPointerException}s when given {@code null} arguments
 * unless they are specifically documented as being {@code null} safe.
 * <p>
 * Throughout this class the exact definition of <i>whitespace</i> may change depending on which method is invoked. Each
 * method clearly documents what constitutes as a whitespace character. See a
 * <a href="https://spreadsheets.google.com/pub?key=pd8dAQyHbdewRsnE5x5GzKQ" substr="_blank"> comparison of several
 * definitions of whitespace</a>.
 *
 * @see Strings
 * @see CharMatcher
 * @see Joiner
 * @see Splitter
 * 
 * @author Zhenya Leonov
 */
final public class MoreStrings {

    private static final Pattern NEWLINE_WHITESPACE = Pattern.compile("[\n|\r]\\s*$");
    private static final Pattern SUPPLEMENTARY_CHARACTERS = Pattern.compile("[^\\u0000-\\uFFFF]");
    private static final Pattern WHITESPACE_NEWLINE = Pattern.compile("^\\s*[\n|\r]");

    private MoreStrings() {
    }

    /**
     * Returns all possible string combinations of length {@code k} from the set of characters in {@code alphabet}.
     * 
     * @param alphabet the set of characters to use
     * @param k        the length
     * @return all possible string combinations of length {@code k} from the set of characters in {@code alphabet}
     */
    public static List<String> combinations(final CharSequence alphabet, int k) {
        checkNotNull(alphabet, "alphabet == null");
        checkArgument(k > 0, "k < 0");

        final List<String> list = Lists.newArrayListWithCapacity((int) Math.pow(alphabet.length(), k));

        if (k == 1)
            return Lists.transform(Lists.charactersOf(alphabet), Functions.toStringFunction());
        else {
            final List<String> sublist = combinations(alphabet, k - 1);

            for (int i = 0; i < alphabet.length(); i++)
                for (int j = 0; j < sublist.size(); j++)
                    list.add(alphabet.charAt(i) + sublist.get(j));

            return list;
        }
    }

    /**
     * Returns {@code true} if {@code str} contains {@code substr}, ignoring case differences; else {@code false}.
     * <p>
     * Note: This method uses the rules of the default locale.
     *
     * @param str    the specified string
     * @param substr the string to search for
     * @return {@code true} if {@code str} contains the {@code substr}, ignoring case differences; {@code false} otherwise
     * @throws NullPointerException if either {@code str} or {@code substr} is {@code null}
     */
    public static boolean containsIgnoreCase(final String str, final String substr) {
        return indexOfIgnoreCase(str, substr) != -1;
    }

    /**
     * Returns the number of occurrences of {@code substr} in {@code str}.
     * 
     * @param str    the string to search in
     * @param substr the string to search for
     * @return the number of occurrences of {@code substr} in {@code str}
     */
    public static int count(final String str, final String substr) {
        checkNotNull(str, "str == null");
        checkNotNull(substr, "substr == null");

        if (str.length() == 0 || substr.length() == 0)
            return 0;

        int count = 0;
        int index = 0;

        while ((index = str.indexOf(substr, index)) != -1) {
            count++;
            index += substr.length();
        }

        return count;
    }

    /**
     * Returns {@code true} if {@code str} ends with {@code suffix}, ignoring case differences; else {@code false}.
     * 
     * @param str    the specified string
     * @param suffix the suffix
     * @return {@code true} if {@code str} ends with {@code suffix}, ignoring case differences; else {@code false}
     */
    public static boolean endsWithIgnoreCase(final String str, final String suffix) {
        checkNotNull(str, "str == null");
        checkNotNull(suffix, "suffix == null");
        return suffix.length() <= str.length() && indexOfIgnoreCase(str, suffix, str.length() - suffix.length()) != -1;
    }

    /**
     * Returns the specified string with line feed (LF): {@code \n} and carriage return (CR): {@code \r} characters escaped.
     * 
     * @param str the specified string
     * @return the specified string with line feed (LF): {@code \n} and carriage return (CR): {@code \r} characters escaped
     */
    public static String escapeEOLCharacters(final String str) {
        checkNotNull(str, "str == null");
        return replace(replace(str, "\r", "\\r"), "\n", "\\n");
    }

    /**
     * Returns the index within {@code str} of the first occurrence of {@code substr}, ignoring case differences.
     * <p>
     * Note: This method uses the rules of the default locale.
     *
     * @param str    the specified string
     * @param substr the string to search for
     * @return the index within {@code str} of the first occurrence of {@code substr}, ignoring case differences
     * @throws NullPointerException if either {@code str} or {@code substr} is {@code null}
     */
    public static int indexOfIgnoreCase(final String str, final String substr) {
        return indexOfIgnoreCase(str, substr, 0);
    }

    /**
     * Returns the index within {@code str} of the first occurrence of {@code substr}, starting at the specified index and
     * ignoring case differences.
     * <p>
     * Note: This method uses the rules of the default locale.
     *
     * @param str       the specified string
     * @param substr    the string to search for
     * @param fromIndex the index from which to start the search
     * @return the index within {@code str} of the first occurrence of {@code substr}, ignoring case differences
     * @throws IllegalArgumentException        if {@code fromIndex} < 0
     * @throws StringIndexOutOfBoundsException if {@code fromIndex} > the length of {@code str}
     * @throws NullPointerException            if either {@code str} or {@code substr} is {@code null}
     */
    public static int indexOfIgnoreCase(final String str, final String substr, final int fromIndex) {
        checkNotNull(str, "str == null");
        checkNotNull(substr, "substr == null");

        if (fromIndex < 0)
            throw new IllegalArgumentException("fromIndex < 0");
        if (fromIndex > str.length())
            throw new StringIndexOutOfBoundsException(fromIndex);

        if (substr.isEmpty())
            return fromIndex;

        final int limit = str.length() - substr.length() + 1;

        if (fromIndex <= limit)
            for (int i = fromIndex; i < limit; i++)
                if (str.regionMatches(true, i, substr, 0, substr.length()))
                    return i;

        return -1;

    }

    /**
     * Returns {@code true} if the specified character sequence is {@code null}, empty, or contains only white space
     * characters according to {@link CharMatcher#whitespace()}.
     * <p>
     * This method is {@code null} safe.
     *
     * @param chars the specified character sequence or {@code null}
     * @return {@code true} if the specified character sequence is {@code null}, empty, or contains only white space
     *         characters; {@code false} otherwise
     * @see CharMatcher#whitespace()
     * @see Strings#isNullOrEmpty(String)
     * @see Strings#nullToEmpty(String)
     * @see Objects#toString()
     */
    public static boolean isNullOrWhitespace(final CharSequence chars) {
        return chars == null || isWhitespace(chars);
    }

    /**
     * Returns {@code true} if the specified character sequence is empty or contains only white space characters according
     * to {@link CharMatcher#whitespace()}.
     *
     * @param chars the specified character sequence
     * @return {@code true} if the specified character sequence is empty or contains only white space characters;
     *         {@code false} otherwise
     * @see CharMatcher#whitespace()
     * @see Strings#isNullOrEmpty(String)
     * @see Strings#nullToEmpty(String)
     * @see Objects#toString()
     */
    public static boolean isWhitespace(final CharSequence chars) {
        checkNotNull(chars, "chars == null");
        return CharMatcher.whitespace().matchesAllOf(chars);
    }

    /**
     * Removes all instances of the specified substring from the given string.
     * 
     * @param str    the specified string
     * @param substr the substring to remove
     * @return the given string with all instances of the specified substring removed
     */
    public static String remove(final String str, final String substr) {
        checkNotNull(str, "str == null");
        checkNotNull(substr, "substr == null");
        return replace(str, substr, "", false);
    }

    /**
     * Removes all instances of the specified substring from the given string, ignoring case differences.
     * 
     * @param str    the specified string
     * @param substr the substring to remove
     * @return the given string with all instances of the specified substring removed, ignoring case differences
     */
    public static String removeIgnoreCase(final String str, final String substr) {
        checkNotNull(str, "str == null");
        checkNotNull(substr, "substr == null");
        return replace(str, substr, "", true);
    }

    /**
     * Replaces all instances of the specified substring in the given string with the replacement string.
     * <p>
     * <b>Note:</b> Users of Java 6, Java 7, and Java 8 should prefer this method to
     * {@link String#replace(CharSequence, CharSequence)} because it avoid the expensive cost of using the
     * {@code java.util.regex} facility.
     * <p>
     * Java 9+ users should prefer {@link String#replace(CharSequence, CharSequence)} which now has a faster implementation
     * and no longer uses {@code java.util.regex}. See
     * <a href="https://bugs.openjdk.java.net/browse/JDK-8058779">JDK-8058779 Issue</a> for more information.
     * 
     * @param str         the specified string
     * @param substr      the substring to replace
     * @param replacement the replacement string
     * @return the given string with all instances of the specified substring replaced with the replacement string
     */
    public static String replace(final String str, final String substr, final String replacement) {
        checkNotNull(str, "str == null");
        checkNotNull(substr, "substr == null");
        checkNotNull(replacement, "replacement == null");
        return replace(str, substr, replacement, false);
    }

    /**
     * Replaces all instances of the specified substring in the given string with the replacement string, ignoring case
     * differences.
     * <p>
     * <b>Note:</b> Users of Java 6, Java 7, and Java 8 should prefer this method to
     * {@link String#replace(CharSequence, CharSequence)} because it avoid the expensive cost of using the
     * {@code java.util.regex} facility.
     * <p>
     * Java 9+ users should prefer {@link String#replace(CharSequence, CharSequence)} which now has a faster implementation
     * and no longer uses {@code java.util.regex}. See
     * <a href="https://bugs.openjdk.java.net/browse/JDK-8058779">JDK-8058779 Issue</a> for more information.
     * 
     * @param str         the specified string
     * @param substr      the substring to replace
     * @param replacement the replacement string
     * @return the given string with all instances of the specified substring replaced with the replacement string, ignoring
     *         case differences
     */
    public static String replaceIgnoreCase(final String str, final String substr, final String replacement) {
        checkNotNull(str, "str == null");
        checkNotNull(substr, "substr == null");
        checkNotNull(replacement, "replacement == null");
        return replace(str, substr, replacement, true);
    }

    /**
     * Returns the given character sequence with the set of characters from U+0000 to U+FFFF (outside the <i>Basic
     * Multilingual Plane (BMP)</i> characters) replaced with the U+FFFD (� ) character.
     * <p>
     * One example where this method is useful is when you need to pass a UTF-8 encoded string to a consumer that only
     * supports one to three bytes per character.
     * 
     * @param chars the given character sequence
     * @return the given character sequence with the set of characters from U+0000 to U+FFFF (outside the <i>Basic
     *         Multilingual Plane (BMP)</i> characters) replaced with the U+FFFD (� ) character
     */
    public static String replaceSupplementaryCharacters(final CharSequence chars) {
        return replaceSupplementaryCharacters(chars, '\uFFFD');
    }

    /**
     * Returns the given character sequence with the set of characters from U+0000 to U+FFFF (outside the <i>Basic
     * Multilingual Plane (BMP)</i> characters) replaced with the specified character.
     * <p>
     * This method is useful when you need to pass a UTF-8 encoded string to a consumer that only supports one to three
     * bytes per character.
     * 
     * @param chars       the given character sequence
     * @param replacement the character to use when replacing characters outside the <i>Basic Multilingual Plane (BMP)</i>
     * @return the given character sequence with the set of characters from U+0000 to U+FFFF (outside the <i>Basic
     *         Multilingual Plane (BMP)</i> characters) replaced with the specified character
     */
    public static String replaceSupplementaryCharacters(final CharSequence chars, final char replacement) {
        checkNotNull(chars, "chars == null");
        return SUPPLEMENTARY_CHARACTERS.matcher(chars).replaceAll(String.valueOf(replacement));
    }

    /**
     * Returns {@code true} if {@code str} starts with {@code prefix}, ignoring case differences; else {@code false}.
     * 
     * @param str    the specified string
     * @param prefix the prefix
     * @return {@code true} if {@code str} starts with {@code prefix}, ignoring case differences; else {@code false}
     */
    public static boolean startsWithIgnoreCase(final String str, final String prefix) {
        return indexOfIgnoreCase(str, prefix) == 0;
    }

    /**
     * Returns a copy of the specified character sequence, with trailing and leading whitespace according to
     * {@link CharMatcher#whitespace()} omitted.
     * <p>
     * This method is {@code null} safe.
     *
     * @param chars the specified character sequence or {@code null}
     * @return a copy of the specified character sequence, with trailing and leading whitespace omitted
     */
    public static String trim(final CharSequence chars) {
        return chars == null ? null : CharMatcher.whitespace().trimFrom(chars);
    }

    /**
     * Returns a copy of the specified character sequence, with trailing whitespace according to
     * {@link CharMatcher#whitespace()} omitted.
     * <p>
     * This method is {@code null} safe.
     *
     * @param chars the specified character sequence or {@code null}
     * @return a copy of the specified character sequence, with trailing whitespace omitted
     */
    public static String trimEnd(final CharSequence chars) {
        return chars == null ? null : CharMatcher.whitespace().trimTrailingFrom(chars);
    }

    /**
     * Returns a copy of the specified character sequence, with leading and trailing <i>blank</i> lines omitted. A blank
     * line is defined as containing only whitespace characters according to the predefined character class {@code \s} which
     * matches [ \t\n\x0B\f\r] in the Java regex facility.
     * <p>
     * This method is {@code null} safe.
     *
     * @param chars the specified character sequence or {@code null}
     * @return a copy of the specified character sequence, with leading and trailing blank lines omitted
     */
    public static String trimLines(final CharSequence chars) {
        if (chars == null)
            return null;

        return NEWLINE_WHITESPACE.matcher(WHITESPACE_NEWLINE.matcher(chars).replaceAll("")).replaceAll("");
    }

    /**
     * Returns a copy of the specified character sequence, with leading whitespace according to
     * {@link CharMatcher#whitespace()} omitted.
     * <p>
     * This method is {@code null} safe.
     *
     * @param chars the specified character sequence or {@code null}
     * @return a copy of the specified character sequence, with leading whitespace omitted or {@code null}
     */
    public static String trimStart(final CharSequence chars) {
        return chars == null ? null : CharMatcher.whitespace().trimLeadingFrom(chars);
    }

//    /**
//     * Joins the string representation of each of {@code parts}.
//     * <p>
//     * For fine-grained control consider using {@link Joiner}.
//     *
//     * @param parts the objects to join
//     * @return a string comprised of the string representation of each {@code parts}
//     * @throws NullPointerException if any of {@code parts} is {@code null}
//     */
//    public static String join(final Iterator<?> parts) {
//        checkNotNull(parts, "parts == null");
//        return join(parts, null);
//    }
//
//    /**
//     * Joins the string representation of each of {@code parts}.
//     * <p>
//     * For fine-grained control consider using {@link Joiner}.
//     *
//     * @param parts the objects to join
//     * @return a string comprised of the string representation of each {@code parts}
//     * @throws NullPointerException if any of {@code parts} is {@code null}
//     */
//    public static String join(final Iterable<?> parts) {
//        checkNotNull(parts, "parts == null");
//        return join(parts.iterator(), null);
//    }
//
//    /**
//     * Joins the string representation of each of {@code parts} using the specified separator.
//     * <p>
//     * For fine-grained control consider using {@link Joiner}.
//     *
//     * @param parts     the objects to join
//     * @param separator the specified separator
//     * @return a string comprised of the string representation of each {@code parts} concatenated using the specified
//     *         separator
//     * @throws NullPointerException if any of {@code parts} is {@code null}
//     */
//    public static String join(final Iterable<?> parts, final CharSequence separator) {
//        checkNotNull(parts, "parts == null");
//        checkNotNull(separator, "separator == null");
//        return join(parts.iterator(), separator);
//    }
//
//    /**
//     * Joins the string representation of each of {@code parts} using the specified separator.
//     * <p>
//     * For fine-grained control consider using {@link Joiner}.
//     *
//     * @param parts     the objects to join
//     * @param separator the specified separator
//     * @return a string comprised of the string representation of each {@code parts} concatenated using the specified
//     *         separator
//     * @throws NullPointerException if any of {@code parts} is {@code null}
//     */
//    public static String join(final Iterator<?> parts, final CharSequence separator) {
//        checkNotNull(parts, "parts == null");
//        final StringBuilder sb = new StringBuilder();
//
//        if (parts.hasNext()) {
//            sb.append(parts.next());
//            while (parts.hasNext()) {
//                if (separator != null)
//                    sb.append(separator);
//                sb.append(parts.next());
//            }
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Splits the specified string into segments and returns them as an immutable list.
//     * <p>
//     * For fine-grained control consider using {@link Splitter}.
//     *
//     * @param str       the specified string
//     * @param separator the separator to use
//     * @return an immutable list of the segments split from {@code str}
//     */
//    public static List<String> split(final String str, final String separator) {
//        checkNotNull(str, "str == null");
//        checkNotNull(separator, "separator == null");
//        checkArgument(!separator.isEmpty(), "separator cannot be empty");
//        final ImmutableList.Builder<String> builder = ImmutableList.builder();
//
//        final int length = separator.length();
//
//        int start = 0;
//        int index = str.indexOf(separator);
//        while (index >= 0) {
//            builder.add(str.substring(start, index));
//            start = index + length;
//            index = str.indexOf(separator, start);
//        }
//        builder.add(str.substring(start));
//        return builder.build();
//    }
//
//    /**
//     * Returns a mutable list of the lines parsed from a string. The lines do not include line-termination characters, but
//     * do include other leading and trailing whitespace.
//     * <p>
//     * A line is considered to be terminated by either a line feed {@code \n}, a carriage return {@code \r}, or a carriage
//     * return followed immediately by a line feed {@code \r\n}.
//     * 
//     * @param string the specified string
//     * @return a mutable list of lines parsed from a string
//     */
//    public static List<String> readLines(final String string) {
//        checkNotNull(string, "string == null");
//        final StringReader reader = new StringReader(string);
//        try {
//            return CharStreams.readLines(reader);
//        } catch (final IOException e) {
//            // cannot happen
//            throw new AssertionError();
//        }
//    }

    /**
     * Returns the given character sequence truncated to the specified maximum length.
     * <p>
     * If the length of the given sequence is greater than {@code maxLength}, the returned string will be exactly
     * {@code maxLength} in length. Otherwise the sequence will be returned as a {@code String} with no changes.
     * <p>
     * This method is {@code null} safe.
     * 
     * @param chars     the given character sequence or {@code null}
     * @param maxLength the specified maximum length
     * @return the given character sequence truncated to the specified maximum length
     * @throws IllegalArgumentException if {@code maxLength} < 1
     */
    public static String truncate(final CharSequence chars, final int maxLength) {
        return truncate(chars, maxLength, null);
    }

    /**
     * Returns the given character sequence truncated to the specified maximum length.
     * <p>
     * If the length of the given sequence is greater than {@code maxLength}, the returned string will be exactly
     * {@code maxLength} in length and will end with {@code suffix} (if it is not {@code null}). Otherwise the sequence will
     * be returned as a {@code String} with no changes.
     * <p>
     * This method is {@code null} safe.
     * 
     * @param chars     the given character sequence or {@code null}
     * @param maxLength the specified maximum length
     * @param suffix    the string to append to the truncated sequence or {@code null} for no suffix
     * @return the given character sequence truncated to the specified maximum length
     * @throws IllegalArgumentException if {@code maxLength} < 1
     * @throws IllegalArgumentException if {@code suffix} is not {@code null} and its length is not less than
     *                                  {@code maxLength}
     */
    public static String truncate(final CharSequence chars, final int maxLength, final String suffix) {
        checkArgument(maxLength > 0, "maxLength < 1");

        if (chars == null)
            return null;

        if (chars.length() <= maxLength)
            return chars.toString();

        checkArgument(suffix == null || maxLength > suffix.length(), "maxLength <= suffix.length()");

        final StringBuilder sb = new StringBuilder(maxLength).append(chars, 0, suffix == null ? maxLength : maxLength - suffix.length());

        if (suffix != null)
            sb.append(suffix);

        return sb.toString();
    }

    /**
     * Returns {@code null} if the given character sequence is {@code null}, empty, or contains only whitespace characters
     * according to {@link CharMatcher#whitespace()}, otherwise the original character sequence is returned.
     * <p>
     * This method is {@code null} safe.
     *
     * @param chars the specified character sequence or {@code null}
     * @return {@code null} if the given character sequence is {@code null}, empty, or contains only whitespace characters
     *         according to {@link CharMatcher#whitespace()}
     */
    public static <T extends CharSequence> T whitespaceToNull(final T chars) {
        if (isNullOrWhitespace(chars))
            return null;
        return chars;
    }

    private static String replace(final String str, final String substr, final String replacement, final boolean ignoreCase) {
        int index = ignoreCase ? indexOfIgnoreCase(str, substr) : str.indexOf(substr);

        if (index < 0)
            return str;

        final int s_length = str.length();
        final int t_length = substr.length();

        final StringBuilder sb = new StringBuilder(s_length);

        int start = 0;
        while (index >= 0) {
            sb.append(str, start, index).append(replacement);
            start = index + t_length;
            index = ignoreCase ? indexOfIgnoreCase(str, substr, start) : str.indexOf(substr, start);
        }

        return sb.append(str, start, s_length).toString();
    }

}