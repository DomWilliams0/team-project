package com.b3.search.util;

import java.util.HashMap;
import java.util.Set;

/**
 * Describes a search algorithm.
 *
 * @author bxd428
 */
public enum SearchAlgorithm {

	DEPTH_FIRST("Depth First Search", "DFS", "DFS: LIFO (stack)"),

	BREADTH_FIRST("Breadth First Search", "BFS", "BFS: FIFO (queue)"),

	DIJKSTRA("Dijkstra's Algorithm", "DIJS", "Dijkstra's: Priority Queue"),

	A_STAR("A* Search", "ASTR", "A*: Priority Queue");

	private static final HashMap<String, SearchAlgorithm> NAMES = new HashMap<>();
	private static final HashMap<String, SearchAlgorithm> SHORTS = new HashMap<>();

	static {
		for (SearchAlgorithm algorithm : values()) {
			NAMES.put(algorithm.getName(), algorithm);
			SHORTS.put(algorithm.getShorthand(), algorithm);
		}
	}

	private final String name;
	private final String shorthand;
	private final String frontierDescription;

	SearchAlgorithm(String name, String shorthand, String frontierDescription) {
		this.name = name;
		this.shorthand = shorthand.toUpperCase();
		this.frontierDescription = frontierDescription;
	}

	/**
	 * @param name the name of the algorithm chosen
	 * @return the algorithm chosen as a enum of {@link SearchAlgorithm}
	 */
	public static SearchAlgorithm fromName(String name) {
		return NAMES.get(name);
	}

	/**
	 * @return a {@link Set} of {@link String} containing all the algorithms as strings
	 */
	public static Set<String> allNames() {
		return NAMES.keySet();
	}

	/**
	 * @return the current name of the chosen algorithm as a String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param shorthand the shorthand name of the algorithm chosen
	 * @return the algorithm chosen as a enum of {@link SearchAlgorithm}
	 */
	public static SearchAlgorithm fromShorthand(String shorthand) {
		return SHORTS.get(shorthand.toUpperCase());
	}

	/**
	 * @return a {@link Set} of {@link String} containing all the algorithms' shorthand names as strings
	 */
	public static Set<String> allShorthands() {
		return SHORTS.keySet();
	}

	/**
	 * @return the shorthand abbreviation of the algorithm's name.
	 */
	public String getShorthand() {
		return shorthand;
	}

	/**
	 * @return the frontier's description
	 */
	public String getFrontierDescription() {
		return frontierDescription;
	}

}
