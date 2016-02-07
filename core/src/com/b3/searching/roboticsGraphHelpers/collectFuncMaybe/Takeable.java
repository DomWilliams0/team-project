package com.b3.searching.roboticsGraphHelpers.collectFuncMaybe;

import java.util.Collection;

public interface Takeable<E> extends Collection<E> {

	E take();

}
