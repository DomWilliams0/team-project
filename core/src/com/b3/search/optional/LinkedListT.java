package com.b3.search.optional;

import java.util.LinkedList;

public class LinkedListT<E> extends LinkedList<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.poll();
	}

}
