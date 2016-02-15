package com.b3.search.optional;

import java.util.Collection;

public interface Takeable<E> extends Collection<E> {

	E take();

}
