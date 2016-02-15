package com.b3.search.util;

import com.b3.search.Node;
import com.b3.search.Point;

public class SearchParameters {
	
	private SearchAlgorithm alg;
	private Function2<Node, Node, Float> h;

	public SearchParameters() { }
	
	/**
	 * Creates the default search parameters
	 * @param alg The search algorithm
	 */
	public SearchParameters(SearchAlgorithm alg) {
		setAlgorithm(alg);
		
		// Set default functions
		setHeuristic(SearchParameters.nothing());
	}
	
	/**
	 * Creates a new search parameters object
	 * @param alg The search algorithm
	 * @param h The heuristic function
	 * @param d The distance function
	 */
	public SearchParameters(SearchAlgorithm alg, Function2<Node, Node, Float> h, Function2<Node, Node, Float> d) {
		setAlgorithm(alg);
		setHeuristic(h);
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
	public Function2<Node, Node, Float> getHeuristic() {
		return h;
	}

	/**
	 * @param h Heuristic function to set
	 */
	public void setHeuristic(Function2<Node, Node, Float> h) {
		this.h = h;
	}

	/*
	/**
	 * Gets the euclidean distance function.
	 * Is a negative number to work with our comparator.
	 * @return The euclidean distance between 2 points
	 */
	public static Function2<Node, Node, Float> euclideanDistance() {
		// Return higher order function
		return (n1, n2) -> {
			Point p1 = n1.getPoint();
			Point p2 = n2.getPoint();
			int x = p1.getX() - p2.getX();
			int y = p1.getY() - p2.getY();

			return (float) -(x*x + y*y);
		};
	}

	/**
	 * Gets the 'nothing' function
	 * @return The function that returns 0f for every input
	 */
	public static  Function2<Node, Node, Float> nothing() {
		return (a, b) -> 0f;
	}
	
	/**
	 * Factory method for BFS and DFS
	 * @param alg Search algorithm
	 * @return A new SearchParameters object suitable for BFS and DFS
	 */
	public static SearchParameters defaultParams(SearchAlgorithm alg) {
		return new SearchParameters(alg);
	}
	
	/**
	 * Factory method for A*
	 * @return A new SearchParameters object suitable for A* search
	 */
	public static SearchParameters AStarParams() {
		return new SearchParameters(SearchAlgorithm.A_STAR, euclideanDistance(), euclideanDistance());
	}
}
