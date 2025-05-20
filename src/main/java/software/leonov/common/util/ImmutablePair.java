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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * An immutable {@code Pair} of non-{@code null} objects. Neither the first nor second object can be changed after
 * creation.
 * 
 * @author Zhenya Leonov
 * 
 * @param <T> The type of the first object
 * @param <U> The type of the second object
 */
final public class ImmutablePair<T, U> extends Pair<T, U> implements Serializable {

    private static final long serialVersionUID = 1L;

    private ImmutablePair(final T first, final U second) {
        super(first, second);
    }

    /**
     * Returns an immutable {@code Pair} containing the specified objects.
     * 
     * @param first  the first object
     * @param second the second object
     * @return an immutable {@code Pair} containing the specified objects
     */
    public static <T, U> ImmutablePair<T, U> of(final T first, final U second) {
        checkNotNull(first, "first == null");
        checkNotNull(second, "second == null");
        return new ImmutablePair<T, U>(first, second);
    }

    /**
     * Always throws {@code UnsupportedOperationException}.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public ImmutablePair<T, U> setFirst(final T first) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@code UnsupportedOperationException}.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public ImmutablePair<T, U> setSecond(final U second) {
        throw new UnsupportedOperationException();
    }

    private void writeObject(final ObjectOutputStream out) throws ClassNotFoundException, IOException {
        out.defaultWriteObject();
        out.writeObject(first);
        out.writeObject(second);
    }

    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();

        final T t = (T) in.readObject();
        final U u = (U) in.readObject();

        if (t == null || u == null)
            throw new InvalidObjectException("values cannot be null");

        first = t;
        second = u;
    }

}