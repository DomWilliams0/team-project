package com.b3.util;

/**
 * Represents a pair of elements.
 *
 * @author oxe410
 * @param <T> The type of the first element.
 * @param <U> The type of the second element.
 *
 * @author oxe410
 */
public class Tuple<T, U> {

	private T first;
	private U second;

	/**
	 * @param first  The first element in the pair.
	 * @param second The second element in the pair.
	 */
	public Tuple(T first, U second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return The first element in the pair.
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Sets the first element in the pair.
	 *
	 * @param first The element to set it to,
	 */
	public void setFirst(T first) {
		this.first = first;
	}

	/**
	 * @return The second element in the pair.
	 */
	public U getSecond() {
		return second;
	}

	/**
	 * Sets the second element in the pair.
	 *
	 * @param second The element to set it to,
	 */
	public void setSecond(U second) {
		this.second = second;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple)) return false;
		Tuple oT = (Tuple) o;
		return getFirst().equals(oT.getFirst()) && getSecond().equals(oT.getSecond());
	}

	/**
	 * @return the hashCode of this Tuple
     */
	@Override
	public int hashCode() {
		return getFirst().hashCode();
	}

}
