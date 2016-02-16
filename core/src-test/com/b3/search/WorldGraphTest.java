package com.b3.search;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class WorldGraphTest {

	private WorldGraph graph;

	@Before
	public void setUp() throws Exception {
		graph = WorldGraphBasic.getBasicGraph();
		if (graph == null)
			throw new Exception("Can't start tests. Graph is null.");
	}

	/**
	 * Tests the {@link WorldGraph#getNodes()} method.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testGetNodes() throws Exception {
		testGetNodes(new WorldGraph(0, 0), true);
		testGetNodes(graph, false);
	}

	/**
	 * Test helper for the {@link WorldGraph#getNodes()}
	 * method.
	 * @param worldGraph The {@link WorldGraph} to check.
	 * @param empty Whether the {@code graph} actually is
	 *              empty.
	 * @throws Exception If the {@code graph}
	 *                   {@link List#isEmpty()} when it
	 *                   shouldn't be or visa-versa.
	 */
	public void testGetNodes(WorldGraph worldGraph, boolean empty) throws Exception {
		boolean actualEmpty = worldGraph.getNodes().isEmpty();
		if (actualEmpty != empty)
			throw new Exception("There were" + (actualEmpty ? "n't any" : "") + " nodes. Expected otherwise.");
	}

	/**
	 * Tests the {@link WorldGraph#hasNode(Point)} method.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testHasNode() throws Exception {
		testHasNode(0, 0, true);
		testGetNode(0, 1000, false);
	}

	/**
	 * Test helper for the
	 * {@link WorldGraph#hasNode(Point)} method.
	 * @param x The x coordinate of the {@link Node}.
	 * @param y The y coordinate of the {@link Node}.
	 * @param found Whether the {@link Node} exists or not.
	 * @throws Exception If the {@link Node} exists when it
	 *                   shouldn't or visa-versa.
	 */
	public void testHasNode(int x, int y, boolean found) throws Exception {
		boolean node = graph.hasNode(new Point(x, y));
		if (node != found) {
			throw new Exception("Node " + (node ? "" : "not ") + "found. Expected otherwise.");
		}
	}

	/**
	 * Tests the {@link WorldGraph#getNode(Point)} method.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testGetNode() throws Exception {
		testGetNode(0, 0, true);
		testGetNode(0, 1000, false);
	}

	/**
	 * Tests the {@link WorldGraph#getNode(Point)} method.
	 * @param x The x coordinate of the {@link Node}.
	 * @param y The y coordinate of the {@link Node}.
	 * @param found Whether the {@link Node} exists or not.
	 * @throws Exception If the {@link Node} exists when it
	 *                   shouldn't or visa-versa.
	 */
	public void testGetNode(int x, int y, boolean found) throws Exception {
		Node node = graph.getNode(new Point(x, y));
		if (found == (node == null)) {
			throw new Exception("Node " + (node == null ? "not " : "") + "got. Expected otherwise.");
		}
	}

	/**
	 * Tests the {@link WorldGraph#addNode(Point)} method.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testAddNode() throws Exception {
		testAddNode(20, 1000);
	}

	/**
	 * Test helper for the
	 * {@link WorldGraph#addNode(Point)} method.
	 * First checks that the {@link Node} doesn't already
	 * exist. Then adds it and checks that it was added.
	 * @param x The x coordinate of the {@link Node} to
	 *          add.
	 * @param y The y coordinate of the {@link Node} to
	 *          add.
	 * @throws Exception If the {@link Node} already
	 *                   existed or if it wasn't added.
	 */
	public void testAddNode(int x, int y) throws Exception {
		testGetNode(x, y, false);
		graph.addNode(new Point(x, y));
		testGetNode(x, y, true);
	}

	/**
	 * Tests the {@link WorldGraph#hasEdge(Point, Point)}
	 * method.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testHasEdge() throws Exception {
		testHasEdge(0, 0, 0, 1, true);
		testHasEdge(0, 0, 0, 2, false);
	}

	/**
	 * Test helper for the the
	 * {@link WorldGraph#hasEdge(Point, Point)} method.
	 * @param x1 The x coordinate of the first {@link Node}
	 *           on the edge.
	 * @param y1 The y coordinate of the first {@link Node}
	 *           on the edge.
	 * @param x2 The x coordinate of the second
	 *           {@link Node} on the edge.
	 * @param y2 The y coordinate of the second
	 *           {@link Node} on the edge.
	 * @param found Whether the edge exists or not.
	 * @throws Exception If the edge exists when it
	 *                   shouldn't or visa-versa.
	 */
	public void testHasEdge(int x1, int y1, int x2, int y2, boolean found) throws Exception {
		boolean edge = graph.hasEdge(new Point(x1, y1), new Point(x2, y2));
		if (edge != found) {
			throw new Exception("Edge " + (edge ? "" : "not ") + "found. Expected otherwise.");
		}
	}

	/**
	 * Tests the
	 * {@link WorldGraph#addEdge(Point, Point, float)}
	 * method.
	 * First checks that the edge doesn't already exist.
	 * Then adds it and checks that it was added.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testAddEdge() throws Exception {
		testAddEdge(0, 0, 0, 7);
	}

	/**
	 * Test helper for the the
	 * {@link WorldGraph#addEdge(Point, Point, float)}
	 * method.
	 * First checks that the edge doesn't already
	 * exist. Then adds it and checks that it was added.
	 * @param x1 The x coordinate of the first {@link Node}
	 *           on the edge.
	 * @param y1 The y coordinate of the first {@link Node}
	 *           on the edge.
	 * @param x2 The x coordinate of the second
	 *           {@link Node} on the edge.
	 * @param y2 The y coordinate of the second
	 *           {@link Node} on the edge.
	 * @throws Exception If the edge already existed or if
	 *                   the edge didn't get added.
	 */
	public void testAddEdge(int x1, int y1, int x2, int y2) throws Exception {
		testHasEdge(x1, y1, x2, y2, false);
		graph.addEdge(new Point(x1, y1), new Point(x2, y2), 1f);
		testHasEdge(x1, y1, x2, y2, true);
	}

	/**
	 * Tests the
	 * {@link WorldGraph#removeEdge(Point, Point)} method.
	 * @throws Exception If the test fails.
	 */
	@Test
	public void testRemoveEdge() throws Exception {
		testRemoveEdge(1, 0, 1, 7);
	}

	/**
	 * Test helper for the the
	 * {@link WorldGraph#removeEdge(Point, Point)} method.
	 * First adds an edge (with checks) see
	 * {@link #testAddEdge()}. Then removes it and checks
	 * that it is deleted.
	 * @param x1 The x coordinate of the first {@link Node}
	 *           on the edge.
	 * @param y1 The y coordinate of the first {@link Node}
	 *           on the edge.
	 * @param x2 The x coordinate of the second
	 *           {@link Node} on the edge.
	 * @param y2 The y coordinate of the second
	 *           {@link Node} on the edge.
	 * @throws Exception If the edge didn't add properly
	 *                   or if the edge wan't removed.
	 */
	public void testRemoveEdge(int x1, int y1, int x2, int y2) throws Exception {
		testAddEdge(x1, y1, x2, y2);
		graph.removeEdge(new Point(x1, y1), new Point(x2, y2));
		testHasEdge(x1, y1, x2, y2, false);
	}

}
