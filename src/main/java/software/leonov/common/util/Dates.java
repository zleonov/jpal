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
package software.leonov.common.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Static utility methods for working with dates.
 * <p>
 * <b>Note:</b> Java 8+ users should consider switching to the new
 * <a target="_blank" href="https://docs.oracle.com/javase/tutorial/datetime/index.html">Date and Time API</a>
 * introduced in Java 8.
 * 
 * @author Zhenya Leonov
 */
public final class Dates {

    private Dates() {
    }

    final private static DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);

    /**
     * Returns the current {@code Timestamp} formatted according to the specified pattern using {@link SimpleDateFormat}.
     * <p>
     * Exampe: {@code getFormattedTimestamp("YYYY-MM-DD")}
     * 
     * @param pattern the pattern to use when formatting the {@code Timestamp}
     * 
     * @return the current {@code Timestamp} as a string
     */
    public static String getFormattedTimestamp(final String pattern) {
        checkNotNull(pattern, "pattern == null");
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * Returns a date/time formatter which uses the {@link DateFormat#DEFAULT DEFAULT} date and time formatting styles in
     * the default locale.
     * 
     * @return a date/time formatter which uses the {@link DateFormat#DEFAULT DEFAULT} date and time formatting styles in
     *         the default locale
     */
    public static DateFormat getDefaultDateTimeInstance() {
        return DATE_FORMAT;
    }

    /**
     * Converts a {@code java.util.Date} object to {@code java.sql.Timestamp}.
     * 
     * @param date the date to convert
     * @return return a {@code java.sql.Timestamp} object converted from the given {@code java.util.Date} object
     */
    public static Timestamp getTimestamp(final Date date) {
        checkNotNull(date, "date == null");
        return new Timestamp(date.getTime());
    }

}
