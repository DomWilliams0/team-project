package com.b3.search.util.takeable;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityQueueT<E> extends PriorityQueue<E> implements Takeable<E> {
	public PriorityQueueT(Comparator<? super E> comparator) {
		super(comparator);
	}

	@Override
	public E take() {
		return super.poll();
	}

}
