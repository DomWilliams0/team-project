package com.b3.search.util;

import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.util.takeable.LinkedListT;
import com.b3.search.util.takeable.PriorityQueueT;
import com.b3.search.util.takeable.StackT;
import com.b3.search.util.takeable.Takeable;

import java.util.function.Function;

/**
 * Essentially a utils for {@link SearchAlgorithm}.
 * Contains all the functions that we did not want to put in the <code>enum</code>.
 * 
 * @author bxd428
 */
public class SearchParameters {

	private static final Function2<Node, Node, Float> NOTHING;
	private static final Function2<Node, Node, Float> EUCLIDEAN;

	static {
		EUCLIDEAN = SearchParameters::calculateEuclidean;
		NOTHING = (a, b) -> 0f;
	}

	/**
	 * Calculated the Euclidean distance between two {@link Node Nodes}.
	 * IE SQRT((x2-x1)^2 + (y2-y1)^2)
	 *
	 * @param n1 first node
	 * @param n2 second node
	 * @return The calculated Euclidean distance.
	 */
	public static float calculateEuclidean(Node n1, Node n2) {
		Point p1 = n1.getPoint();
		Point p2 = n2.getPoint();
		int x = p1.getX() - p2.getX();
		int y = p1.getY() - p2.getY();

		return (float) Math.sqrt(x * x + y * y);
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

	public Takeable<Node> createFrontier(Function<Node, Float> getGScore, Function2<Node, Node, Float> heuristic, Node end) {
		switch (algorithm) {
			case DEPTH_FIRST:
				return new StackT<>();
			case BREADTH_FIRST:
				return new LinkedListT<>();
			case DIJKSTRA:
				return new PriorityQueueT<>(getGScore::apply);
			case A_STAR:
				return new PriorityQueueT<>((n) -> getGScore.apply(n) + heuristic.apply(n, end));
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
