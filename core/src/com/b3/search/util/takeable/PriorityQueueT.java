package com.b3.search.util.takeable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

/**
 * A {@link java.util.PriorityQueue} <b>like</b> structure with a {@link #peek()} and {@link #take()} method.
 * This is actually an {@link ArrayList} with prioritisation.
 * @param <E> The type of elements held in this {@link java.util.Collection}.
 */
public class PriorityQueueT<E> extends ArrayList<E> implements Takeable<E> {

	private final Function<E, Float> priorityFunction;

	/**
	 * Construct a new, empty, priority queue
	 * Utilising a given priority function
	 * @param priorityFunction The function to base take order on.
     */
	public PriorityQueueT(Function<E, Float> priorityFunction) {
		this.priorityFunction = priorityFunction;
	}

	private int peekIndex() {
		assert !isEmpty();
		int bestPos = 0;
		float lowestPriority = Float.POSITIVE_INFINITY;
		for (int i = 0; i < size(); i++) {
			Float toCheck = priorityFunction.apply(get(i));
			if (toCheck < lowestPriority) {
				bestPos = i;
				lowestPriority = toCheck;
			}
		}
		return bestPos;
	}

	@Override
	public E peek() {
		return get(peekIndex());
	}

	@Override
	public E take() {
		return remove(peekIndex());
	}
	
	/**
	 * @return A new {@link ArrayList} with all the elements, but in order.
	 */
	public ArrayList<E> sortedOrder() {
		Collections.sort(this, (e1, e2) -> Float.compare(priorityFunction.apply(e1), priorityFunction.apply(e2)));
		return new ArrayList<>(this);
	}

}
