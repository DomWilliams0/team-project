package com.b3.search;

import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.Utils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Tests for the {@link SearchTicker} class.
 */
public class SearchTickerTest {

	private WorldGraph graph;
	private SearchTicker searchTicker;

	@Before
	public void setUp() throws Exception {
		graph = WorldGraphBasic.getBasicGraph();
		if (graph == null)
			throw new Exception("Can't start tests. Graph is null.");

		// Trick the deltaTime.
		Utils.deltaTime = 1;

		// Setup the config.
		Config.loadConfig("assets/reference.yml");

		// Create the SearchTicker.
		searchTicker = new SearchTicker();
	}

	/**
	 * Tests the {@link SearchTicker SearchTicker's} path
	 * generation.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testTick() throws Exception {
		SearchAlgorithm BFS = SearchAlgorithm.BREADTH_FIRST;
		SearchAlgorithm DFS = SearchAlgorithm.DEPTH_FIRST;
		SearchAlgorithm AS  = SearchAlgorithm.A_STAR;

		Point[] path = new Point[] {new Point(0,0), new Point(0,1)};
		testTick(0, 0, 0, 1, BFS, path);
		testTick(0, 0, 0, 1, AS, path);

		path = new Point[] {new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)};
		testTick(0, 0, 0, 3, BFS, path);
		testTick(0, 0, 0, 3, AS, path);

		testTick(0, 0, 5, 0, BFS, null);
		testTick(0, 0, 5, 0, DFS, null);
		testTick(0, 0, 5, 0, AS, null);
	}

	/**
	 * Test helper for the
	 * {@link SearchTicker SearchTicker's} path generation.
	 * @param x1 The start x coordinate.
	 * @param y1 The start y coordinate.
	 * @param x2 The end x coordinate.
	 * @param y2 The end y coordinate.
	 * @param algorithm The {@link SearchAlgorithm} to
	 *                  test.
	 * @param correct The correct path that the search
	 *                should produce. <code>null</code>
	 *                should be used if the search should
	 *                not produce a path.
	 * @throws Exception If the correct path is different
	 *                   from the path produced.
	 */
	private void testTick(int x1, int y1, int x2, int y2, SearchAlgorithm algorithm, Point[] correct) throws Exception {
		assert graph != null;
		Node n1 = graph.getNode(new Point(x1, y1));
		if (n1 == null)
			throw new Exception("Point (" + x1 + ", " + y1 + ") not found in graph.");
		Node n2 = graph.getNode(new Point(x2, y2));
		if (n2 == null)
			throw new Exception("Point (" + x2 + ", " + y2 + ") not found in graph.");

		searchTicker.reset(algorithm, n1, n2);

		while (!searchTicker.isPathComplete())
			searchTicker.tick();

		List<Node> path = searchTicker.getPath();
		if (path.isEmpty() != (correct == null)) {
			if (!path.isEmpty())
				printPath(path);
			throw new Exception("Path is " + (path.isEmpty() ? "" : "not ") + "empty expected otherwise.");
		}
		if (correct == null)
			return;

		if (path.size() != correct.length) {
			printPath(path);
			throw new Exception("Wrong path size.");
		}
		for (int i = 0; i < correct.length; i++) {
			if (!path.get(i).getPoint().equals(correct[i])) {
				printPath(path);
				throw new Exception("Wrong path.");
			}
		}
	}

	/**
	 * Prints a path to {@link System#err}.
	 * @param nodes The path to print.
	 */
	private void printPath(List<Node> nodes) {
		System.err.print("PATH: ");
		nodes.stream().map(Node::getPoint).forEach((p) ->
				System.err.print("(" + p.getX() + ", " + p.getY() + ") ")
		);
		System.err.println();
	}

}
