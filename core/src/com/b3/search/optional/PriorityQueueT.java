package com.b3.search.optional;

import java.util.PriorityQueue;

public class PriorityQueueT<E> extends PriorityQueue<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.poll();
	}

}
