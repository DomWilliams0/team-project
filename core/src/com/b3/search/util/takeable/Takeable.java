package com.b3.search.util.takeable;

import java.util.Collection;

public interface Takeable<E> extends Collection<E> {

	E take();

}
