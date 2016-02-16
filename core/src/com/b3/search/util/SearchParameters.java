package com.b3.search.util;

import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.util.takeable.LinkedListT;
import com.b3.search.util.takeable.PriorityQueueT;
import com.b3.search.util.takeable.StackT;
import com.b3.search.util.takeable.Takeable;

import java.util.Comparator;
import java.util.function.Function;

public class SearchParameters {

	private static final Function2<Node, Node, Float> NOTHING;
	private static final Function2<Node, Node, Float> EUCLIDEAN;

	static {
		EUCLIDEAN = (n1, n2) -> {
			Point p1 = n1.getPoint();
			Point p2 = n2.getPoint();
			int x = p1.getX() - p2.getX();
			int y = p1.getY() - p2.getY();

			return (float) Math.sqrt(x * x + y * y);
		};
		NOTHING = (a, b) -> 0f;
	}

	private SearchAlgorithm algorithm;
	private Function2<Node, Node, Float> h;

	public SearchParameters(SearchAlgorithm alg) {
		setAlgorithm(alg);
	}

	/**
	 * @return The search algorithm
	 */
	public SearchAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param alg Search algorithm to set
	 */
	public void setAlgorithm(SearchAlgorithm alg) {
		this.algorithm = alg;
		switch (alg) {
			case A_STAR:
				h = EUCLIDEAN;
				break;
			default:
				h = NOTHING;
				break;
		}
	}

	public Takeable<Node> createFrontier() {

		if (algorithm != SearchAlgorithm.BREADTH_FIRST && algorithm != SearchAlgorithm.DEPTH_FIRST)
			throw new IllegalArgumentException("No heuristic supplied for A* frontier");

		return createFrontier(null, null, null);

	}
	public Takeable<Node> createFrontier(Function<Node, Float> getGScore, Function2<Node, Node, Float> heuristic, Node end) {
		switch (algorithm) {
			case DEPTH_FIRST:
				return new StackT<>();
			case BREADTH_FIRST:
				return new LinkedListT<>();
			case A_STAR:
				return new PriorityQueueT<>((Comparator<Node>) (n1, n2) ->
						Float.compare(
								getGScore.apply(n1) + heuristic.apply(n1, end),
								getGScore.apply(n2) + heuristic.apply(n2, end)));
			default:
				throw new IllegalArgumentException("Invalid search algorithm: " + algorithm);
		}
	}



	/**
	 * @return The heuristic function
	 */
	public Function2<Node, Node, Float> getHeuristic() {

		return h;
	}
}
