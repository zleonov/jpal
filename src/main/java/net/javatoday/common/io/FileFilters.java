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
package net.javatoday.common.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import com.google.common.base.Predicate;
import com.google.common.io.PatternFilenameFilter;

/**
 * Static utility methods pertaining to {@link FileFilter}s.
 * <p>
 * <b>Warning:</b> The {@code java.io.File} facility should be considered deprecated. Java 7+ users are highly
 * encouraged to use {@code java.nio.file.Path} operations introduced in JDK 7 as part of the
 * <a target="_blank" href="https://www.oracle.com/technetwork/articles/javase/nio-139333.html">The Java NIO.2 File
 * System</a>. See {@link Fs} for more information.
 *
 * @author Zhenya Leonov
 * @see PatternFilenameFilter
 */
final public class FileFilters {

    private static final FileFilter ALWAYS_TRUE = new FileFilter() {

        @Override
        public boolean accept(final File path) {
            return true;
        }
    };

    private static final FileFilter ALWAYS_FALSE = new FileFilter() {

        @Override
        public boolean accept(final File path) {
            return false;
        }
    };

    private static final FileFilter IS_FILE = new FileFilter() {

        @Override
        public boolean accept(final File file) {
            checkNotNull(file, "file == null");
            return file.isFile();
        }
    };

    private static final FileFilter IS_DIRECTORY = new FileFilter() {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory();
        }
    };

    private FileFilters() {
    }

    /**
     * Returns a file filter that always evaluates to {@code false}.
     * 
     * @return a file filter that always evaluates to {@code false}
     */
    public static FileFilter alwaysFalse() {
        return ALWAYS_FALSE;
    }

    /**
     * Returns a file filter that always evaluates to {@code true}.
     * 
     * @return a file filter that always evaluates to {@code true}
     */
    public static FileFilter alwaysTrue() {
        return ALWAYS_TRUE;
    }

    /**
     * Returns a file filter that evaluates to {@code true} if all of the specified file filters evaluate to {@code true}.
     *
     * @param filters the specified file filters
     * @return a file filter that evaluates to {@code true} if all of the specified file filters evaluate to {@code true}
     * @throws IllegalArgumentException if no filters are given
     */
    public static FileFilter and(final FileFilter... filters) {
        checkNotNull(filters, "filters == null");
        checkArgument(filters.length > 0, "filters.length <= 0");
        if (filters.length == 1)
            return filters[0];

        return new FileFilter() {

            @Override
            public boolean accept(File file) {
                for (final FileFilter filter : filters) {
                    if (!filter.accept(file))
                        return false;
                }
                return true;
            }
        };
    }

    /**
     * Adapts a {@code FilenameFilter} to the {@code FileFilter} interface.
     *
     * @param filter the specified {@code FilenameFilter}
     * @return a new {@code FileFilter} which mimics the behavior of the specified {@code FilenameFilter}
     */
    public static FileFilter forFilenameFilter(final FilenameFilter filter) {
        checkNotNull(filter, "filter == null");

        return new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return filter.accept(file.getParentFile(), file.getName());
            }
        };
    }

    /**
     * Adapts a {@code Predicate} to the {@code FileFilter} interface.
     * 
     * @param predicate the specified {@code Predicate}
     * @return a new {@code FileFilter} which mimics the behavior of the specified {@code Predicate}
     */
    public static FileFilter forPredicate(final Predicate<File> predicate) {
        checkNotNull(predicate, "predicate == null");

        return new FileFilter() {

            @Override
            public boolean accept(final File file) {
                return predicate.apply(file);
            }
        };
    }

    /**
     * Returns a {@code FileFilter} which determines if the specified {@code File} is a directory.
     * 
     * @return a {@code FileFilter} which determines if the specified {@code File} is a directory
     */
    public static FileFilter isDirectory() {
        return IS_DIRECTORY;
    }

    /**
     * Returns a {@code FileFilter} which determines if the specified {@code File} is a normal file. A file is <i>normal</i>
     * if it is not a directory and, in addition, satisfies other system-dependent criteria.
     * 
     * @return a {@code FileFilter} which determines if the specified {@code File} is a normal file
     */
    public static FileFilter isFile() {
        return IS_FILE;
    }

    /**
     * Returns a file filter that evaluates to {@code true} if the given file filter evaluates to {@code false}.
     *
     * @param filter the given file filter
     * @return a file filter that evaluates to {@code true} if the given file filter evaluates to {@code false}
     */
    public static FileFilter not(final FileFilter filter) {
        checkNotNull(filter, "filter == null");
        return new FileFilter() {

            @Override
            public boolean accept(final File file) {
                return !filter.accept(file);
            }
        };
    }

    /**
     * Returns a file filter that evaluates to {@code true} if any of the specified file filters evaluate to {@code true}.
     *
     * @param filters the specified file filters
     * @return a file filter that evaluates to {@code true} if any of the specified file filters evaluate to {@code true}
     * @throws IllegalArgumentException if no filters are given
     */
    public static FileFilter or(final FileFilter... filters) {
        checkNotNull(filters, "filters == null");
        checkArgument(filters.length > 0, "filters.length <= 0");
        if (filters.length == 1)
            return filters[0];
        return new FileFilter() {

            @Override
            public boolean accept(final File file) {
                for (final FileFilter filter : filters) {
                    if (filter.accept(file))
                        return true;
                }
                return false;
            }
        };
    }
}
