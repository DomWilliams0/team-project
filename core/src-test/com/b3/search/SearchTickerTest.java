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

	private final SearchAlgorithm DFS = SearchAlgorithm.DEPTH_FIRST;
	private final SearchAlgorithm BFS = SearchAlgorithm.BREADTH_FIRST;
	private final SearchAlgorithm DIJ = SearchAlgorithm.DIJKSTRA;
	private final SearchAlgorithm AS  = SearchAlgorithm.A_STAR;

	@Before
	public void setUp() throws Exception {
		// Trick the delta time.
		Utils.DELTA_TIME = Utils.TRUE_DELTA_TIME = 10;

		// Setup the config.
		Config.loadConfig("assets/reference.yml");
	}

	private void setGraph(WorldGraph graph) throws Exception {
		if (graph == null)
			throw new Exception("Can't start tests. Graph is null.");
		this.graph = graph;
		this.searchTicker = new SearchTicker(graph);
	}

	/**
	 * Tests the {@link SearchTicker SearchTicker's} path
	 * generation.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testTick() throws Exception {
		/* Basic Graph */
		setGraph(WorldGraphBasic.getBasicGraph());
		Point[] path = new Point[] {new Point(0,0), new Point(0,1)};
		testTick(0, 0, 0, 1, path, BFS, DIJ, AS);

		path = new Point[] {new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)};
		testTick(0, 0, 0, 3, path, BFS, DIJ, AS);

		testTick(0, 0, 5, 0, null, DFS, BFS, DIJ, AS);

		/* aStar1 */
		setGraph(WorldGraphBasic.getRealWorld("aStar1"));
		path = new Point[] {new Point(0,11), new Point(0,10), new Point(0,9), new Point(0,8), new Point(0,7),
				new Point(0,6), new Point(0,5), new Point(0,4), new Point(0,3), new Point(0,2), new Point(1,2),
				new Point(2,2), new Point(3,2), new Point(4,2), new Point(5,2), new Point(6,2), new Point(7,2),
				new Point(8,2), new Point(8,1), new Point(8,0), new Point(7,0), new Point(6,0), new Point(5,0),
				new Point(4,0), new Point(3,0), new Point(2,0), new Point(1,0), new Point(0,0)};
		testTick(0, 11, 0, 0, path, DIJ, AS);
	}

	/**
	 * Test helper for the
	 * {@link SearchTicker SearchTicker's} path generation.
	 * @param x1 The start x coordinate.
	 * @param y1 The start y coordinate.
	 * @param x2 The end x coordinate.
	 * @param y2 The end y coordinate.
	 * @param correct The correct path that the search
	 *                should produce. <code>null</code>
	 *                should be used if the search should
	 *                not produce a path.
	 * @param algorithms The {@link SearchAlgorithm SearchAlgorithms}
	 *                   to test.
	 * @throws Exception If the correct path is different
	 *                   from the path produced.
	 */
	private void testTick(int x1, int y1, int x2, int y2, Point[] correct, SearchAlgorithm... algorithms) throws Exception {
		for (SearchAlgorithm algorithm : algorithms) {
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
