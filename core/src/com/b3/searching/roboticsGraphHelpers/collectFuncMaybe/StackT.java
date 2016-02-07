package com.b3.searching.roboticsGraphHelpers.collectFuncMaybe;

import java.util.Stack;

public class StackT<E> extends Stack<E> implements Takeable<E> {

	@Override
	public E take() {
		return super.pop();
	}

}
