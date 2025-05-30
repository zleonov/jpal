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
package software.leonov.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Static utility methods pertaining to {@code BinaryFunction} instances.
 * <p>
 * The methods in this class return serializable {@code BinaryFunction}s as long as they are given serializable
 * parameters.
 * <p>
 * <b>Note:</b> The {@link BinaryFunction} interface is the legacy version of {@link BiFunction}. It has been refactored
 * to extend {@code BiFunction} for backwards compatibility. Java 8+ users should reference {@code BiFunction} directly.
 * <p>
 * For example: {@code BiFunction<Object, Object, T> bf = BinaryFunctions.constant(value);}
 * 
 * @author Zhenya Leonov
 */
@SuppressWarnings("deprecation")
public final class BinaryFunctions {

    private BinaryFunctions() {
    }

    private static final class ConstantBinaryFunction<E> implements BinaryFunction<Object, Object, E>, Serializable {
        private static final long serialVersionUID = -3035505608495294198L;

        private final E value;

        public ConstantBinaryFunction(final E value) {
            this.value = value;
        }

        @Override
        public E apply(final Object first, final Object second) {
            return value;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof ConstantBinaryFunction) {
                final ConstantBinaryFunction<?> that = (ConstantBinaryFunction<?>) obj;
                return Objects.equal(value, that.value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (value == null) ? 0 : value.hashCode();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("value", value).toString();
        }
    }

    /**
     * Returns a {@code BinaryFunction} that always returns the specified {@code value}.
     *
     * @param <E>   the type of return value
     * @param value the constant value for the {@code BinaryFunction} to return
     * @return a {@code BinaryFunction} that always returns the specified {@code value}
     */
    public static <E> BinaryFunction<Object, Object, E> constant(final E value) {
        return new ConstantBinaryFunction<E>(value);
    }

    private static final class SupplierBinaryFunction<E> implements BinaryFunction<Object, Object, E>, Serializable {
        private static final long serialVersionUID = 9041489237895725564L;

        private final Supplier<E> supplier;

        private SupplierBinaryFunction(final Supplier<E> supplier) {
            this.supplier = checkNotNull(supplier, "supplier == null");
        }

        @Override
        public E apply(final Object first, final Object second) {
            return supplier.get();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof SupplierBinaryFunction) {
                final SupplierBinaryFunction<?> that = (SupplierBinaryFunction<?>) obj;
                return this.supplier.equals(that.supplier);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return supplier.hashCode();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("supplier", supplier).toString();
        }
    }

    /**
     * Returns a {@code BinaryFunction} that always returns the result of invoking {@link Supplier#get()} on the specified
     * {@code supplier}, regardless of its inputs.
     *
     *@param <E> the type of value returned by the supplier
     * @param supplier the specified supplier
     * @return a {@code BinaryFunction} that always returns the result of invoking {@link Supplier#get()} on the specified
     *         {@code supplier}
     */
    public static <E> BinaryFunction<Object, Object, E> forSupplier(final Supplier<E> supplier) {
        return new SupplierBinaryFunction<E>(supplier);
    }

}
