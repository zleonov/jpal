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
package net.javatoday.common.collect;

import java.util.Map;

/**
 * A capacity restricted {@link Map}. The size of this map can vary, but never exceed the maximum number of mappings
 * (the bound) specified at creation.
 * <p>
 * Typical implementations will define a policy for removing <i>stale</i> mappings, or otherwise throw an
 * {@code IllegalStateException} to prevent the map from exceeding its capacity restrictions.
 * 
 * @author Zhenya Leonov
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public interface BoundedMap<K, V> extends Map<K, V> {

    /**
     * Returns the number of additional mappings that this map can maintain.
     * 
     * @return the number of additional mappings that this map can maintain
     */
    public int remainingCapacity();

}