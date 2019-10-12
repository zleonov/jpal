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
package net.javatoday.common.base;

import java.util.function.BiFunction;

/**
 * Determines an output value for two arguments.
 * 
 * @deprecated This interface is the legacy version of {@link BiFunction}. It has been refactored to extend
 *             {@code BiFunction} for backwards compatibility. Java 8+ users should use to {@code BiFunction} directly.
 *
 * @param <T> the type of the first argument
 * @param <U> the type of the second argument
 * @param <R> the type of result
 * @author Zhenya Leonov
 */
public interface BinaryFunction<T, U, R> extends BiFunction<T, U, R> {

    /**
     * Returns the result of applying this binary function to the specified arguments.
     *
     * @param t the first argument
     * @param u the second argument
     * @return the result of applying this binary function to the specified arguments
     */
    public R apply(T t, U u);

}
