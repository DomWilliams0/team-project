package com.b3.search.util;

import java.util.HashMap;
import java.util.Set;

/**
 * Describes a search algorithm
 */
public enum SearchAlgorithm {

	DEPTH_FIRST("Depth First Search", "DFS: LIFO (stack)"),

	BREADTH_FIRST("Breadth First Search", "BFS: FIFO (queue)"),

	DIJKSTRA("Dijkstra's Algorithm", "Dijkstra's: Priority Queue"),

	A_STAR("A* Search", "A*: Priority Queue");

	private static final HashMap<String, SearchAlgorithm> NAMES = new HashMap<>();

	static {
		for (SearchAlgorithm algorithm : values()) {
			NAMES.put(algorithm.getName(), algorithm);
		}
	}

	private final String name;
	private final String frontierDescription;

	SearchAlgorithm(String name, String frontierDescription) {
		this.name = name;
		this.frontierDescription = frontierDescription;
	}

	public static SearchAlgorithm fromName(String name) {
		return NAMES.get(name);
	}

	public static Set<String> allNames() {
		return NAMES.keySet();
	}

	public String getName() {
		return name;
	}

	public String getFrontierDescription() {
		return frontierDescription;
	}

}
