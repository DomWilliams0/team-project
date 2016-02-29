package com.b3.search.util;

import java.util.HashMap;
import java.util.Set;

/**
 * Describes a search algorithm
 */
public enum SearchAlgorithm {

	DEPTH_FIRST("Depth First Search"),

	BREADTH_FIRST("Breadth First Search"),

	DIJKSTRA("Dijkstra's Algorithm"),

	A_STAR("A* Search");

	private static final HashMap<String, SearchAlgorithm> NAMES = new HashMap<>();

	static {
		for (SearchAlgorithm algorithm : values()) {
			NAMES.put(algorithm.getName(), algorithm);
		}
	}

	private final String name;

	SearchAlgorithm(String name) {
		this.name = name;
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

}
