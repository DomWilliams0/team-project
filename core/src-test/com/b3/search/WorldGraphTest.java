package com.b3.search;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class WorldGraphTest {

	private WorldGraph graph;

	@Before
	public void setUp() {
		try {
			graph = WorldGraphBasic.getBasicGraph();
		} catch (FileNotFoundException e) {
			fail("Graph file not found");
		}
		assertNotNull("Graph is null", graph);
	}

	/**
	 * Tests the {@link WorldGraph#getNodes()} method.
	 */
	@Test
	public void testGetNodes() {
		testGetNodes(new WorldGraph(0, 0), true);
		testGetNodes(graph, false);
	}

	/**
	 * Test helper for the {@link WorldGraph#getNodes()}
	 * method.
	 *
	 * @param worldGraph The {@link WorldGraph} to check.
	 * @param empty      Whether the {@code graph} actually is
	 *                   empty.
	 */
	public void testGetNodes(WorldGraph worldGraph, boolean empty) {
		boolean actualEmpty = worldGraph.getNodes().isEmpty();
		assertEquals("There were" + (actualEmpty ? "n't any" : "") + " nodes", actualEmpty, empty);
	}

	/**
	 * Tests the {@link WorldGraph#hasNode(Point)} method.
	 */
	@Test
	public void testHasNode() {
		testHasNode(0, 0, true);
		testGetNode(0, 1000, false);
		testGetNode(1000, 0, false);
		testGetNode(-1, 0, false);
		testGetNode(0, -1, false);
	}

	/**
	 * Test helper for the
	 * {@link WorldGraph#hasNode(Point)} method.
	 *
	 * @param x     The x coordinate of the {@link Node}.
	 * @param y     The y coordinate of the {@link Node}.
	 * @param found Whether the {@link Node} exists or not.
	 */
	public void testHasNode(int x, int y, boolean found) {
		boolean node = graph.hasNode(new Point(x, y));
		assertEquals("Node " + (node ? "" : "not ") + "found", node, found);
	}

	/**
	 * Tests the {@link WorldGraph#getNode(Point)} method.
	 */
	@Test
	public void testGetNode() {
		testGetNode(0, 0, true);
		testGetNode(0, 1000, false);
	}

	/**
	 * Tests the {@link WorldGraph#getNode(Point)} method.
	 *
	 * @param x     The x coordinate of the {@link Node}.
	 * @param y     The y coordinate of the {@link Node}.
	 * @param found Whether the {@link Node} exists or not.
	 */
	public void testGetNode(int x, int y, boolean found) {
		Node node = graph.getNode(new Point(x, y));
		assertNotEquals("Node " + (node == null ? "not " : "") + "got", found, node == null);
	}

	/**
	 * Tests the {@link WorldGraph#addNode(Point)} method.
	 */
	@Test
	public void testAddNode() {
		testAddNode(20, 1000);
	}

	/**
	 * Test helper for the
	 * {@link WorldGraph#addNode(Point)} method.
	 * First checks that the {@link Node} doesn't already
	 * exist. Then adds it and checks that it was added.
	 *
	 * @param x The x coordinate of the {@link Node} to
	 *          add.
	 * @param y The y coordinate of the {@link Node} to
	 *          add.
	 */
	public void testAddNode(int x, int y) {
		testGetNode(x, y, false);
		graph.addNode(new Point(x, y));
		testGetNode(x, y, true);
	}

	/**
	 * Tests the {@link WorldGraph#hasEdge(Point, Point)}
	 * method.
	 */
	@Test
	public void testHasEdge() {
		testHasEdge(0, 0, 0, 1, true);
		testHasEdge(0, 0, 0, 2, false);
	}

	/**
	 * Test helper for the the
	 * {@link WorldGraph#hasEdge(Point, Point)} method.
	 *
	 * @param x1    The x coordinate of the first {@link Node}
	 *              on the edge.
	 * @param y1    The y coordinate of the first {@link Node}
	 *              on the edge.
	 * @param x2    The x coordinate of the second
	 *              {@link Node} on the edge.
	 * @param y2    The y coordinate of the second
	 *              {@link Node} on the edge.
	 * @param found Whether the edge exists or not.
	 */
	public void testHasEdge(int x1, int y1, int x2, int y2, boolean found) {
		boolean edge = graph.hasEdge(new Point(x1, y1), new Point(x2, y2));
		assertEquals("Edge " + (edge ? "" : "not ") + "found", edge, found);
	}

	/**
	 * Tests the
	 * {@link WorldGraph#addEdge(Point, Point, float)}
	 * method.
	 * First checks that the edge doesn't already exist.
	 * Then adds it and checks that it was added.
	 */
	@Test
	public void testAddEdge() {
		testAddEdge(0, 0, 0, 7);
	}

	/**
	 * Test helper for the the
	 * {@link WorldGraph#addEdge(Point, Point, float)}
	 * method.
	 * First checks that the edge doesn't already
	 * exist. Then adds it and checks that it was added.
	 *
	 * @param x1 The x coordinate of the first {@link Node}
	 *           on the edge.
	 * @param y1 The y coordinate of the first {@link Node}
	 *           on the edge.
	 * @param x2 The x coordinate of the second
	 *           {@link Node} on the edge.
	 * @param y2 The y coordinate of the second
	 *           {@link Node} on the edge.
	 */
	public void testAddEdge(int x1, int y1, int x2, int y2) {
		testHasEdge(x1, y1, x2, y2, false);
		graph.addEdge(new Point(x1, y1), new Point(x2, y2), 1f);
		testHasEdge(x1, y1, x2, y2, true);
	}

	/**
	 * Tests the
	 * {@link WorldGraph#removeEdge(Point, Point)} method.
	 */
	@Test
	public void testRemoveEdge() {
		testRemoveEdge(1, 0, 1, 7);
	}

	/**
	 * Test helper for the the
	 * {@link WorldGraph#removeEdge(Point, Point)} method.
	 * First adds an edge (with checks) see
	 * {@link #testAddEdge()}. Then removes it and checks
	 * that it is deleted.
	 *
	 * @param x1 The x coordinate of the first {@link Node}
	 *           on the edge.
	 * @param y1 The y coordinate of the first {@link Node}
	 *           on the edge.
	 * @param x2 The x coordinate of the second
	 *           {@link Node} on the edge.
	 * @param y2 The y coordinate of the second
	 *           {@link Node} on the edge.
	 */
	public void testRemoveEdge(int x1, int y1, int x2, int y2) {
		testAddEdge(x1, y1, x2, y2);
		graph.removeEdge(new Point(x1, y1), new Point(x2, y2));
		testHasEdge(x1, y1, x2, y2, false);
	}

	@Test
	/**
	 * Tests the {@link WorldGraph#getRandomNode()} and
	 * {@link WorldGraph#getRandomNode(Node)} methods
	 */
	public void testRandomNode() {
		for (int i = 0; i < 10000; i++) {
			Node randomNode = graph.getRandomNode();
			assertNotNull(randomNode);
			assertTrue(graph.hasNode(randomNode.getPoint()));
		}

		Node notMe = graph.getRandomNode();
		for (int i = 0; i < 1000; i++) {
			Node randomNode = graph.getRandomNode(notMe);
			assertNotEquals(randomNode, notMe);
		}
	}

}
