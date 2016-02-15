package com.b3.searching.optional;

import java.util.Collection;

public interface Takeable<E> extends Collection<E> {

	E take();

}
