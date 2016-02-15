package com.b3.search.util.takeable;

import java.util.LinkedList;

public class LinkedListT<E> extends LinkedList<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.poll();
	}

}