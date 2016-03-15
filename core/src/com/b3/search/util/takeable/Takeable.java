package com.b3.search.util.takeable;

import java.util.Collection;

/**
 * An extension of {@link Collection} with a {@link #peek()} and {@link #take()} method.
 *
 * @param <E> The type of elements held in this {@link java.util.Collection}.
 */
public interface Takeable<E> extends Collection<E> {

	/**
	 * Get the next logical element from the {@link Collection}.
	 * Where the next logical element may be the top element in a {@link java.util.Stack},
	 * the item that's been the longest in a {@link java.util.Queue}, etc.
	 *
	 * @return The next logical element from the {@link Collection}.
	 */
	E peek();

	/**
	 * Get and remove the next logical element from the {@link Collection}.
	 * Where the next logical element may be the top element in a {@link java.util.Stack},
	 * the item that's been the longest in a {@link java.util.Queue}, etc.
	 *
	 * @return The next logical element from the {@link Collection}.
	 */
	E take();

}
