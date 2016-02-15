package com.b3.search.util.takeable;

import java.util.Stack;

public class StackT<E> extends Stack<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.pop();
	}

}
