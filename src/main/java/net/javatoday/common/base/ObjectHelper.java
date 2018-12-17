package net.javatoday.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Objects;

import com.google.common.io.Closer;

import net.javatoday.common.io.Fs;

/**
 * Static utility methods that operate on or return {@link Object}s.
 * 
 * @author Zhenya Leonov
 */
public final class ObjectHelper {

    private ObjectHelper() {
    }

    /**
     * Returns a hash code value for the specified object, or 0 if {@code obj} is {@code null}.
     * 
     * @deprecated Java 7+ users should switch to {@link java.util.Objects#hashCode(Object)} instead.
     * 
     * @param obj the specified object
     * @return a hash code value for the specified object, or 0 if {@code obj} is {@code null}
     */
    public static int hashCode(final Object obj) {
        if (obj == null)
            return 0;
        else
            return obj.hashCode();
    }

    /**
     * Returns the result of calling {@code toString()} on the specified object or "null" if the object is {@code null}.
     * 
     * @deprecated Java 7+ users should switch to {@link java.util.Objects#toString(Object)} instead.
     * 
     * @param obj the specified object
     * @return the result of calling {@code toString()} on the specified object or "null" if the object is {@code null}
     */
    public static String toString(final Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    /**
     * Returns the result of calling {@code toString()} on the specified object or {@code defaultValue} if the object is
     * {@code null}.
     * 
     * @deprecated Java 7+ users should switch to {@link java.util.Objects#toString(Object, String)} instead.
     * 
     * @param obj          the specified object
     * @param defaultValue the {@code String} to return when the specified object is {@code null}
     * @return the result of calling {@code toString()} on the specified object or {@code defaultValue} if the object is
     *         {@code null}
     */
    public static String toString(final Object obj, final String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }

    /**
     * Returns an order independent hash code for the specified objects.
     * <p>
     * <b>Warning:</b> do not confuse {@code unorderedHashCode(elements)} with {@code elements.hashCode()} which will return
     * the hash code value of the {@code elements} object itself.
     * 
     * @param elements the specified objects
     * @return an order independent hash code for the specified objects
     */
    public static int unorderedHashCode(final Iterable<?> elements) {
        checkNotNull(elements, "elements == null");
        return unorderedHashCode(elements.iterator());
    }

    /**
     * Returns an order independent hash code for the specified objects.
     * <p>
     * <b>Warning:</b> do not confuse {@code unorderedHashCode(elements)} with {@code elements.hashCode()} which will return
     * the hash code value of the {@code elements} object itself.
     * 
     * @param elements an {@code Iterator} over the specified objects
     * @return an order independent hash code for the specified objects
     */
    public static int unorderedHashCode(final Iterator<?> elements) {
        checkNotNull(elements, "elements == null");
        int hashCode = 0;
        while (elements.hasNext())
            hashCode += Objects.hashCode(elements.next());
        return hashCode;
    }

    /**
     * Returns an order independent hash code for the specified objects.
     * 
     * @param elements the specified objects
     * @return an order independent hash code for the specified objects
     */
    public static int unorderedHashCode(final Object... elements) {
        checkNotNull(elements, "elements == null");
        int hashCode = 0;
        for (final Object obj : elements)
            hashCode += Objects.hashCode(obj);
        return hashCode;
    }

    /**
     * Returns the first non-{@code null} value from the specified values. If all the values are {@code null} or if
     * {@code first} and {@code rest} parameters are {@code null} then {@code null} will be returned.
     * 
     * @param first the first value
     * @param rest  additional values
     * @return the first non-{@code null} value from the specified values
     */
    @SafeVarargs
    public static <T> T coalesce(final T first, final T... rest) {
        if (first != null)
            return first;
        else if (rest != null)
            for (final T value : rest)
                if (value != null)
                    return value;

        return null;
    }

    /**
     * Writes the specified object to the given file.
     * <p>
     * See {@link ObjectOutputStream#writeObject(Object)} for more information.
     * <p>
     * Exceptions are thrown for classes that should not be serialized. All exceptions are fatal. Any underlying streams
     * will be closed before returning to the caller.
     * 
     * @param object the specified object
     * @param file   the given file
     * @throws InvalidClassException    if something is wrong with a class used by serialization
     * @throws NotSerializableException if any object to be serialized does not implement the {@link java.io.Serializable}
     *                                  interface
     * @throws IOException              if an I/O error occurs
     * @return the specified file
     */
    public static File writeObject(final Object object, final File file) throws IOException {
        checkNotNull(object, "object == null");
        checkNotNull(file, "file == null");

        final Closer closer = Closer.create();
        try {
            final ObjectOutputStream out = closer.register(new ObjectOutputStream(Fs.newBufferedOutputStream(file, false)));
            out.writeObject(object);
            return file;
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * Returns an object read from the specified file.
     * <p>
     * See {@link ObjectInputStream#readObject()} for more information.
     * <p>
     * Exceptions are thrown for classes that should not be deserialized. All exceptions are fatal. Any underlying streams
     * will be closed before returning to the caller.
     * 
     * @param file the specified file
     * @param type the class type of the object
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     * @throws InvalidClassException  if something is wrong with a class used by serialization
     * @throws ClassCastException     if the object is not assignable to the type {@code T}
     * @throws IOException            if an I/O error occurs
     * @return the object read from the specified file
     * 
     */
    public static <T> T readObject(final File file, final Class<? extends T> type) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(type, "type == null");

        final Closer closer = Closer.create();
        try {
            final ObjectInputStream in = closer.register(new ObjectInputStream(Fs.newBufferedInputStream(file)));
            final Object o = in.readObject();
            return type.cast(o);
        } catch (final Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

}
