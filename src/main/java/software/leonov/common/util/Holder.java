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

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * A mutable reference to any non-{@code null} value of type {@code T}. If no value is set the {@link #get()} method
 * will throw a {@code  NoSuchElementException}. After a value is set it can be changed at any time by calling the
 * {@link #set(Object) set(T)} method.
 * <p>
 * This class can be used to reference non-{@code final} variables in anonymous inner classes.
 * <p>
 * <b>Note:</b> While this class is not deprecated, unless mutability is absolutely required, users are encouraged to
 * use Guava's {@link com.google.common.base.Optional Optional} class or Java 8+ {@link java.util.Optional Optional}
 * class. {@link AtomicReference} is an alternative which can be used in a concurrent scenario.
 * 
 * @param <T> the type of value
 * @author Zhenya Leonov
 */
final public class Holder<T> {

    private T value = null;

    private Holder() {
    }

    /**
     * Returns a new {@code Holder} with no value.
     * 
     * @param <T> the type of value
     * @return a new {@code Holder} with no value
     */
    public static <T> Holder<T> empty() {
        return new Holder<T>();
    }

    /**
     * Returns a new {@code Holder} with the specified initial value.
     * 
     * @param <T>   the type of value
     * @param value the initial value
     * @return a new {@code Holder} with the specified initial value
     */
    public static <T> Holder<T> hold(final T value) {
        checkNotNull(value, "value == null");
        return new Holder<T>().set(value);
    }

    /**
     * Returns the value.
     * 
     * @return the value
     * @throws NoSuchElementException if no value has been set
     */
    public T get() {
        if (!isEmpty())
            throw new NoSuchElementException();
        return value;
    }

    /**
     * Returns {@code true} if this {@code Holder} contains a non-{@code null} value.
     * 
     * @return {@code true} if this {@code Holder} contains a non-{@code null} value
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Removes and returns the previously set value. After this call returns {@link #isEmpty()} will return {@code false}
     * and subsequent calls to {@link #get()} will result in a {@code NoSuchElementException}.
     * 
     * @return the value
     */
    public T remove() {
        final T value = this.value;
        this.value = null;
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value the value
     * @return this {@code Holder}
     */
    public Holder<T> set(final T value) {
        checkNotNull(value, "value == null");
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Holder))
            return false;

        final Holder<?> other = (Holder<?>) obj;

        return Objects.equal(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        if (isEmpty())
            return MoreObjects.toStringHelper(this).toString();
        else
            return MoreObjects.toStringHelper(this).add("get()", get().toString()).toString();
    }

}