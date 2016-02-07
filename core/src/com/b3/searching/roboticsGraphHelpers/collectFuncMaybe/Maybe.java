package com.b3.searching.roboticsGraphHelpers.collectFuncMaybe;

public interface Maybe<A> {
	public boolean isNothing();

	public int size();

	public boolean has(A a);

	public Maybe<A> filter(Predicate<A> p);

	public <B> Maybe<B> map(Function<A, B> f);

	public <B> B fold(Function<A, B> f, B b);

	public boolean all(Predicate<A> p);

	public boolean some(Predicate<A> p);

	public void forEach(Action<A> a);

	public A fromMaybe();
}
