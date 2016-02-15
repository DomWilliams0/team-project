package com.b3.searching.optional;


public class Just<A> implements Maybe<A> {

	private final A something;

	public Just(A something) {
		assert (something != null);
		this.something = something;
	}

	public boolean isNothing() {
		return false;
	}

	public int size() {
		return 1;
	}

	public String toString() {
		return "Just(" + something.toString() + ")";
	}

	public boolean has(A a) {
		return something == a;
	}

	public Maybe<A> filter(Predicate<A> p) {
		return p.holds(something) ? new Just<A>(something) : new Nothing<A>();
	}

	public <B> Maybe<B> map(Function<A, B> f) {
		return new Just<B>(f.apply(something));
	}

	public <B> B fold(Function<A, B> f, B b) {
		return f.apply(something);
	}

	public boolean all(Predicate<A> p) {
		return p.holds(something);
	}

	public boolean some(Predicate<A> p) {
		return all(p);
	}

	public void forEach(Action<A> f) {
		f.apply(something);
	}

	public A fromMaybe() {
		return something;
	}
}
