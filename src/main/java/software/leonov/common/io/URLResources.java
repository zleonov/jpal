package software.leonov.common.io;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Static utility methods for working with resources on the classpath.
 * <p>
 * <b>Note:</b> Despite accepting {@link URL} parameters methods in this class <b>are not</b> appropriate for
 * non-classpath resources.
 * 
 * @author Zhenya Leonov
 */
public final class URLResources {

    private URLResources() {
    }

    /**
     * Reads all characters from a URL into a {@link String} using the {@link StandardCharsets#UTF_8 UTF-8} charset.
     * <p>
     * <b>Guava equivalent:</b> {@link Resources#toString(URL, Charset) Resources.toString(URL, Charsets.UTF_8)}.
     *
     * @param resource the URL to read from
     * @return a string containing all the characters from the URL
     * @throws IOException if an I/O error occurs
     */
    public static String read(final URL resource) throws IOException {
        checkNotNull(resource, "resource == null");
        try (final InputStream in = resource.openStream()) {
            return CharStream.read(in, Charsets.UTF_8);
        }
    }

    /**
     * Reads all the lines from a URL into a {@link List List&lt;String&gt;} using the {@link StandardCharsets#UTF_8 UTF-8}
     * charset. The lines do not includeline-termination characters, but do include other leading and trailing whitespace.
     * <p>
     * <b>Guava equivalent:</b> {@link Resources#readLines(URL, Charset) Resources.readLines(URL, Charsets.UTF_8)}.
     *
     * @param resource the URL to read from
     * @return a mutable {@code List} containing all the lines read from the URL
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readLines(final URL resource) throws IOException {
        checkNotNull(resource, "resource == null");
        try (final InputStream in = resource.openStream()) {
            return CharStream.readLines(in, Charsets.UTF_8);
        }
    }

}
