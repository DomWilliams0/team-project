package com.b3.search.util.takeable;

import java.util.LinkedList;

/**
 * A {@link LinkedList} with a {@link #take()} method.
 *
 * @param <E> The type of elements held in this {@link java.util.Collection}.
 * @author bxd428
 */
public class LinkedListT<E> extends LinkedList<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.poll();
	}

}
