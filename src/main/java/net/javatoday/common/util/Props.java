package net.javatoday.common.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Properties;

import com.google.common.base.Charsets;
import com.google.common.io.Closer;

import net.javatoday.common.io.Fs;

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
     * @param file the given file
     * @return a new Java {@code Properties} object loaded from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Properties load(final File file) throws IOException {
        return load(null, file, Charsets.ISO_8859_1);
    }

    /**
     * Returns a new Java {@code Properties} object loaded from the specified file.
     * <p>
     * The file is assumed to be encoded in the ISO 8859-1 charset, where each byte is one Latin1 character, as outlined in
     * {@link Properties#load(Reader)}. Characters not in Latin1, and certain special characters, are represented in keys
     * and elements using Unicode escapes as defined in section 3.3 of <i>The Java Language Specification</i>.
     * 
     * @param defaults the default properties or {@code null} for no defaults
     * @param file     the given file
     * @return a new Java {@code Properties} object loaded from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Properties load(final Properties defaults, final File file) throws IOException {
        return load(defaults, file, Charsets.ISO_8859_1);
    }

    /**
     * Returns a new Java {@code Properties} object loaded from the given file using the specified charset.
     * <p>
     * Characters not in Latin1 may be represented (but not required) using Unicode escapes as defined in section 3.3 of
     * <i>The Java Language Specification</i>.
     * 
     * @param defaults the default properties or {@code null} for no defaults
     * @param file     the given file
     * @param charset  the charset to use
     * @return a new Java {@code Properties} object loaded from the given file using the specified charset
     * @throws IOException if an I/O error occurs
     */
    public static Properties load(final Properties defaults, final File file, final Charset charset) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");

        final Properties props = defaults == null ? new Properties() : new Properties(defaults);

        final Closer closer = Closer.create();
        try {
            props.load(closer.register(new InputStreamReader(new FileInputStream(file), charset)));
        } catch (final Throwable t) {
            closer.rethrow(t);
        } finally {
            closer.close();
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
     * @param file the specified XML file
     * @return a new Java {@code Properties} object loaded from the specified XML file
     * @throws IOException if an I/O error occurs
     */
    public static Properties loadFromXML(final File file) throws IOException {
        return loadFromXML(null, file);
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
     * @param file     the specified XML file
     * @return a new Java {@code Properties} object loaded from the specified XML file
     * @throws IOException if an I/O error occurs
     */
    public static Properties loadFromXML(final Properties defaults, final File file) throws IOException {
        checkNotNull(file, "file == null");

        final Properties props = defaults == null ? new Properties() : new Properties(defaults);

        final Closer closer = Closer.create();
        try {
            props.loadFromXML(closer.register(new FileInputStream(file)));
        } catch (final Throwable t) {
            closer.rethrow(t);
        } finally {
            closer.close();
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
     * @param file       the given file
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File save(final Properties properties, final String comments, final File file) throws IOException {
        return save(properties, comments, file, Charsets.ISO_8859_1);
    }

    /**
     * Saves the specified {@code Properties} to the given file. Internally this method delegates to
     * {@link Properties#store(Writer, String)}.
     * <p>
     * Default properties (if any) are <i>not</i> written out by this method.
     * <p>
     * If the charset is ISO 8859-1 this method behaves identically to {@link #save(Properties, String, File)}, otherwise
     * the {@code Properties} will be saved in the specified charset (without escaping Unicode characters).
     * 
     * @param properties the specified {@code Properties}
     * @param comments   a description of the property list or {@code null} if no comments are desired
     * @param file       the given file
     * @param charset    the charset to use
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File save(final Properties properties, final String comments, final File file, final Charset charset) throws IOException {
        checkNotNull(properties, "properties == null");
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            if (charset == Charsets.ISO_8859_1)
                properties.store(closer.register(new FileOutputStream(file)), comments);
            else
                properties.store(closer.register(Fs.newBufferedWriter(file, false, charset)), comments);
        } catch (final Throwable e) {
            closer.rethrow(e);
        } finally {
            closer.close();
        }
        return file;
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
     * @param file       the given file
     * @param charset    the charset to use
     * @return the given file
     * @throws IOException if an I/O error occurs
     */
    public static File saveAsXML(final Properties properties, final String comment, final File file, final Charset charset) throws IOException {
        checkNotNull(properties, "properties == null");
        checkNotNull(file, "file == null");
        checkNotNull(charset, "charset == null");

        final Closer closer = Closer.create();
        try {
            properties.storeToXML(closer.register(new FileOutputStream(file)), comment, charset.toString());
        } catch (final Throwable e) {
            closer.rethrow(e);
        } finally {
            closer.close();
        }
        return file;
    }

}
