package com.b3.search.util.takeable;

import java.util.ArrayList;
import java.util.function.Function;

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

	/**
	 * Construct a new Priority Queue
	 * As a copy of the given priority queue
	 * @param pq the priority queue to clone.
     */
	public PriorityQueueT(PriorityQueueT pq) {
		super(pq);
		this.priorityFunction = pq.getPriorityFunction();
	}

	@Override
	public E take() {
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
		return remove(bestPos);
	}

	public Function<E,Float> getPriorityFunction() {
		return priorityFunction;
	}

}
