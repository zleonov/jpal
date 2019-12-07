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
package software.leonov.common.collect;

import java.util.Queue;

/**
 * A capacity restricted {@link Queue}. The size of this queue can vary, but
 * never exceed the maximum number of elements (the bound) specified at
 * creation.
 * <p>
 * Typical implementations will define a policy for removing <i>stale</i>
 * elements, or otherwise throw an {@code IllegalStateException} to prevent the
 * queue from exceeding its capacity restrictions.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 */
public interface BoundedQueue<E> extends Queue<E> {

	/**
	 * Returns the number of additional elements that this queue can hold.
	 * 
	 * @return the number of additional elements that this queue can hold
	 */
	public int remainingCapacity();

}