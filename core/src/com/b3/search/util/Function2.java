package com.b3.search.util;

/**
 * {@link java.util.function.Function} but with two arguments.
 *
 * @param <A> The type of the first argument.
 * @param <B> The type of the second argument.
 * @param <C> The return type.
 */
public interface Function2<A, B, C> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param a The first function argument.
	 * @param b The second function argument.
	 * @return The function result.
	 */
	C apply(A a, B b);

}
