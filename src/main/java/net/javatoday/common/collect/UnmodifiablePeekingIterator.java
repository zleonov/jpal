package net.javatoday.common.collect;

import com.google.common.collect.PeekingIterator;
import com.google.common.collect.UnmodifiableIterator;

/**
 * A peeking iterator that does not support {@link PeekingIterator#remove()
 * remove()}.
 * 
 * @author Zhenya Leonov
 */
public abstract class UnmodifiablePeekingIterator<E> extends
		UnmodifiableIterator<E> implements PeekingIterator<E> {

	protected UnmodifiablePeekingIterator() {
	}

}