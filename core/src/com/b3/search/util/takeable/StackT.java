package com.b3.search.util.takeable;

import java.util.Stack;

/**
 * A {@link Stack} with a {@link #take()} method.
 * @param <E> The type of elements held in this {@link java.util.Collection}.
 */
public class StackT<E> extends Stack<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.pop();
	}

}
