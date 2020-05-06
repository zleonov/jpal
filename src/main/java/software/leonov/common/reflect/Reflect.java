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
package software.leonov.common.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterators;

/**
 * Static reflection utilities.
 * 
 * @author Zhenya Leonov
 */
public final class Reflect {

    private Reflect() {
    }

    /**
     * Returns a string representation that "textually represents" the specified object by iterating through each declared
     * field via reflection.
     * <p>
     * The string returned by this method matches the format produced by {@link MoreObjects#toStringHelper(Object)}.
     * <p>
     * <b>Warning:</b> This method is intended to be used only be used for testing and debugging of classes which to not
     * have their own {@code toString()} implementation.
     * 
     * @param o the specified object
     * @return a string representation that "textually represents" the specified object by iterating through each declared
     *         field via reflection
     */
    public static String toString(final Object o) {

        if (o == null)
            return "null";

        final Class<?> clazz = o.getClass();
        final StringBuilder sb = new StringBuilder();
        sb.append(clazz.getSimpleName()).append('{');

        final Iterator<Field> itor = Iterators.forArray(clazz.getDeclaredFields());

        while (itor.hasNext()) {
            final Field field = itor.next();

            field.setAccessible(true);

            try {
                final Object value = field.get(o);

                sb.append(field.getName());
                sb.append('=');

                if (value != null && value.getClass().isArray())
                    if (value.getClass() == boolean[].class)
                        sb.append(Arrays.toString((boolean[]) value));
                    else if (value.getClass() == byte[].class)
                        sb.append(Arrays.toString((byte[]) value));
                    else if (value.getClass() == char[].class)
                        sb.append(Arrays.toString((char[]) value));
                    else if (value.getClass() == double[].class)
                        sb.append(Arrays.toString((double[]) value));
                    else if (value.getClass() == float[].class)
                        sb.append(Arrays.toString((float[]) value));
                    else if (value.getClass() == int[].class)
                        sb.append(Arrays.toString((int[]) value));
                    else if (value.getClass() == long[].class)
                        sb.append(Arrays.toString((long[]) value));
                    else if (value.getClass() == short[].class)
                        sb.append(Arrays.toString((short[]) value));
                    else
                        sb.append(Arrays.toString((Object[]) value));
                else
                    sb.append(value);

                if (itor.hasNext())
                    sb.append(", ");

            } catch (final IllegalAccessException iae) {
                throw new AssertionError(iae);
            }
        }

        return sb.append('}').toString();
    }

}
