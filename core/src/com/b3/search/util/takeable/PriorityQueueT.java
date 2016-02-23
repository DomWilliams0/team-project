package com.b3.search.util.takeable;

import java.util.ArrayList;
import java.util.function.Function;

public class PriorityQueueT<E> extends ArrayList<E> implements Takeable<E> {

	private final Function<E, Float> priorityFunction;

	public PriorityQueueT(Function<E, Float> priorityFunction) {
		this.priorityFunction = priorityFunction;
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

}
