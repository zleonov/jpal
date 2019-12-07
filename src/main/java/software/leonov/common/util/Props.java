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
import static java.nio.charset.StandardCharsets.ISO_8859_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import software.leonov.common.io.Fs;

/**
 * Static utility methods for working with {@link Properties}.
 * 
 * @author Zhenya Leonov
 */
public final class Props {

    private Props() {
    }

    /**
     * Returns a new Java {@code Properties} object loaded from the specified file.
     * <p>
     * The file is assumed to be encoded in the ISO 8859-1 charset, where each byte is one Latin1 character, as outlined in
     * {@link Properties#load(Reader)}. Characters not in Latin1, and certain special characters, are represented in keys
     * and elements using Unicode escapes as defined in section 3.3 of <i>The Java Language Specification</i>.
     * 
     * @param path the given file
     * @return a new Java {@code Properties} object loaded from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Properties load(final Path path) throws IOException {
        return load(null, path, ISO_8859_1);
    }

    /**
     * Returns a new Java {@code Properties} object loaded from the specified file.
     * <p>
     * The file is assumed to be encoded in the ISO 8859-1 charset, where each byte is one Latin1 character, as outlined in
     * {@link Properties#load(Reader)}. Characters not in Latin1, and certain special characters, are represented in keys
     * and elements using Unicode escapes as defined in section 3.3 of <i>The Java Language Specification</i>.
     * 
     * @param defaults the default properties or {@code null} for no defaults
     * @param path     the given file
     * @return a new Java {@code Properties} object loaded from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Properties load(final Properties defaults, final Path path) throws IOException {
        return load(defaults, path, ISO_8859_1);
    }

    /**
     * Returns a new Java {@code Properties} object loaded from the given file using the specified charset.
     * <p>
     * Characters not in Latin1 may be represented (but not required) using Unicode escapes as defined in section 3.3 of
     * <i>The Java Language Specification</i>.
     * 
     * @param defaults the default properties or {@code null} for no defaults
     * @param path     the given file
     * @param charset  the charset to use
     * @return a new Java {@code Properties} object loaded from the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static Properties load(final Properties defaults, final Path path, final Charset charset) throws IOException {
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");

        final Properties props = defaults == null ? new Properties() : new Properties(defaults);

        try (final Reader in = new InputStreamReader(Files.newInputStream(path), charset)) {
            props.load(in);
        }

        return props;
    }

    /**
     * Returns a new Java {@code Properties} object loaded from the specified XML file.
     * <p>
     * The document encoding can be specified in the XML declaration:
     * 
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
     * </pre>
     * 
     * And the file must contains the following DOCTYPE declaration:
     * 
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     * 
     * Furthermore, the document must satisfy the properties DTD described above.
     * 
     * @param path the specified XML file
     * @return a new Java {@code Properties} object loaded from the specified XML file
     * @throws IOException if an I/O error occurs
     */
    public static Properties loadFromXML(final Path path) throws IOException {
        return loadFromXML(null, path);
    }

    /**
     * Returns a new Java {@code Properties} object loaded from the specified XML file.
     * <p>
     * The document encoding can be specified in the XML declaration:
     * 
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
     * </pre>
     * 
     * And the file must contains the following DOCTYPE declaration:
     * 
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     * 
     * Furthermore, the document must satisfy the properties DTD described above.
     * 
     * @param defaults the default properties or {@code null} for no defaults
     * @param path     the specified XML file
     * @return a new Java {@code Properties} object loaded from the specified XML file
     * @throws IOException if an I/O error occurs
     */
    public static Properties loadFromXML(final Properties defaults, final Path path) throws IOException {
        checkNotNull(path, "path == null");

        final Properties props = defaults == null ? new Properties() : new Properties(defaults);

        try (final InputStream in = Files.newInputStream(path)) {
            props.loadFromXML(in);
        }

        return props;
    }

    /**
     * Saves the specified {@code Properties} to the given file. Internally this method delegates to
     * {@link Properties#store(OutputStream, String)}.
     * <p>
     * Default properties (if any) are <i>not</i> written out by this method.
     * <p>
     * This method outputs the comments, properties keys, and values in the following format:
     * <ul>
     * <li>The file is written using the ISO 8859-1 character encoding.
     * <li>Characters not in Latin-1 in the comments are written as {@code \u005Cu}<i>xxxx</i> for their appropriate Unicode
     * hexadecimal value <i>xxxx</i>.
     * <li>Characters less than {@code \u005Cu0020} and characters greater than {@code \u005Cu007E} in property keys or
     * values are written as {@code \u005Cu}<i>xxxx</i> for the appropriate hexadecimal value <i>xxxx</i>.
     * </ul>
     * 
     * @param properties the specified {@code Properties}
     * @param comments   a description of the property list or {@code null} if no comments are desired
     * @param path       the given file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path save(final Properties properties, final String comments, final Path path) throws IOException {
        return save(properties, comments, path, ISO_8859_1);
    }

    /**
     * Saves the specified {@code Properties} to the given file. Internally this method delegates to
     * {@link Properties#store(Writer, String)}.
     * <p>
     * Default properties (if any) are <i>not</i> written out by this method.
     * <p>
     * If the charset is ISO 8859-1 this method behaves identically to {@link #save(Properties, String, Path)}, otherwise
     * the {@code Properties} will be saved in the specified charset (without escaping Unicode characters).
     * 
     * @param properties the specified {@code Properties}
     * @param comments   a description of the property list or {@code null} if no comments are desired
     * @param path       the given file
     * @param charset    the charset to use
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path save(final Properties properties, final String comments, final Path path, final Charset charset) throws IOException {
        checkNotNull(properties, "properties == null");
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");

        if (charset == ISO_8859_1)
            try (final OutputStream out = Files.newOutputStream(path)) {
                properties.store(out, comments);
            }
        else
            try (final Writer writer = Fs.newBufferedWriter(path, false, charset)) {
                properties.store(writer, comments);
            }

        return path;
    }

    /**
     * Writes an XML document representing the specified {@code Properties} to the given file using the specified charset.
     * Internally this methods delegates to {@link Properties#storeToXML(OutputStream, String, String)}.
     * <p>
     * Default properties (if any) are <i>not</i> written out by this method.
     * <p>
     * The XML document will have the following DOCTYPE declaration:
     * 
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     * 
     * @param properties the specified {@code Properties}
     * @param comment    a description of the property list or {@code null} if no comment is desired
     * @param path       the given file
     * @param charset    the charset to use
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static Path saveAsXML(final Properties properties, final String comment, final Path path, final Charset charset) throws IOException {
        checkNotNull(properties, "properties == null");
        checkNotNull(path, "path == null");
        checkNotNull(charset, "charset == null");

        try (final OutputStream out = Files.newOutputStream(path)) {
            properties.storeToXML(out, comment, charset.toString());
        }

        return path;
    }

}
