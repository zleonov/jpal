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

import java.util.function.BiPredicate;

/**
 * Determines a {@code true} or {@code false} value for two arguments.
 * 
 * @deprecated This interface is the legacy version of {@link BiPredicate}. It has been refactored to extend
 *             {@code BiPredicate} for backwards compatibility. Java 8+ users should use to {@code BiPredicate}
 *             directly.
 * 
 * @param <T> the type of the first argument
 * @param <U> the type of the second argument
 * @author Zhenya Leonov
 */
public interface BinaryPredicate<T, U> extends BiPredicate<T, U> {

    /**
     * Returns the result of applying this binary predicate to the specified arguments. This method is <i>generally
     * expected</i>, but not absolutely required to execute without any observable side effects.
     *
     * @param t the first argument
     * @param u the second argument
     * @return the result of applying this binary predicate to the specified arguments
     */
    public boolean apply(T t, U u);

    @Override
    default boolean test(T t, U u) {
        return apply(t, u);
    }

}
