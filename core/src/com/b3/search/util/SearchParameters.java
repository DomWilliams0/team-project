package com.b3.search.util;

import com.b3.search.Node;
import com.b3.search.Point;

public class SearchParameters<A> {
	
	private SearchAlgorithm alg;
	private Function2<Node<A>, Node<A>, Float> h;
	private Function2<Node<A>, Node<A>, Float> d;
	
	public SearchParameters() { }
	
	/**
	 * Creates the default search parameters
	 * @param alg The search algorithm
	 */
	public SearchParameters(SearchAlgorithm alg) {
		setAlgorithm(alg);
		
		// Set default functions
		setHeuristic(SearchParameters.nothing());
		setDistanceFn(SearchParameters.nothing());
	}
	
	/**
	 * Creates a new search parameters object
	 * @param alg The search algorithm
	 * @param h The heuristic function
	 * @param d The distance function
	 */
	public SearchParameters(SearchAlgorithm alg, Function2<Node<A>, Node<A>, Float> h, Function2<Node<A>, Node<A>, Float> d) {
		setAlgorithm(alg);
		setHeuristic(h);
		setDistanceFn(d);
	}

	/**
	 * @return The search algorithm
	 */
	public SearchAlgorithm getAlgorithm() {
		return alg;
	}

	/**
	 * @param alg Search algorithm to set
	 */
	public void setAlgorithm(SearchAlgorithm alg) {
		this.alg = alg;
	}

	/**
	 * @return The heuristic function
	 */
	public Function2<Node<A>, Node<A>, Float> getHeuristic() {
		return h;
	}

	/**
	 * @param h Heuristic function to set
	 */
	public void setHeuristic(Function2<Node<A>, Node<A>, Float> h) {
		this.h = h;
	}

	/**
	 * @return The distance function
	 */
	public Function2<Node<A>, Node<A>, Float> getDistanceFn() {
		return d;
	}

	/**
	 * @param d Distance function to set
	 */
	public void setDistanceFn(Function2<Node<A>, Node<A>, Float> d) {
		this.d = d;
	}
	
	/**
	 * Gets the euclidean distance function.
	 * Is a negative number to work with our comparator.
	 * @return The euclidean distance between 2 points
	 */
	public static Function2<Node<Point>, Node<Point>, Float> euclideanDistance() {
		// Return higher order function
		return (n1, n2) -> {
			Point p1 = n1.getContent();
			Point p2 = n2.getContent();
			int x = p1.getX() - p2.getX();
			int y = p1.getY() - p2.getY();

			return (float) -(x*x + y*y);
		};
	}
	
	/**
	 * Gets the manhattan distance function
	 * @return The manhattan distance between 2 points
	 */
	public static Function2<Node<Point>, Node<Point>, Float> manhattanDistance() {
		// Return higher order function
		return (n1, n2) -> {
			Point p1 = n1.getContent();
			Point p2 = n2.getContent();

			return (float) (Math.abs(p1.getX() - p1.getY()) + Math.abs(p2.getX() - p2.getY()));
		};
	}
	
	/**
	 * Gets the 'nothing' function
	 * @return The function that returns 0f for every input
	 */
	public static <A> Function2<Node<A>, Node<A>, Float> nothing() {
		return (a, b) -> 0f;
	}
	
	/**
	 * Factory method for BFS and DFS
	 * @param alg Search algorithm
	 * @return A new SearchParameters object suitable for BFS and DFS
	 */
	public static SearchParameters<Point> defaultParams(SearchAlgorithm alg) {
		return new SearchParameters<>(alg);
	}
	
	/**
	 * Factory method for A*
	 * @return A new SearchParameters object suitable for A* search
	 */
	public static SearchParameters<Point> AStarParams() {
		return new SearchParameters<>(SearchAlgorithm.A_STAR, euclideanDistance(), euclideanDistance());
	}
}
