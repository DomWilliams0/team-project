package com.b3.searching.roboticsGraphHelpers.collectFuncMaybe;

import java.util.PriorityQueue;

public class PriorityQueueT<E> extends PriorityQueue<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.poll();
	}

}
