package com.b3.search;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class WorldGraphTest {

	private WorldGraph<Point> graph;

	@Before
	public void setUp() throws Exception {
		graph = WorldGraphBasic.getBasicGraph();
		if (graph == null)
			throw new Exception("Can't start tests. Graph is null.");
	}

	// ----- HAS / GET / REMOVE -----

	@Test
	public void testGetNodes() throws Exception {
		graph.getNodes();
	}

	@Test
	public void testHasNode() throws Exception {
		testHasNode(0, 0, true);
		testGetNode(0, 1000, false);
	}

	public void testHasNode(int x, int y, boolean found) throws Exception {
		boolean node = graph.hasNode(new Point(x, y));
		if (node != found) {
			throw new Exception("Node " + (node ? "" : "not ") + "found. Expected otherwise.");
		}
	}

	@Test
	public void testGetNode() throws Exception {
		testGetNode(0, 0, true);
		testGetNode(0, 1000, false);
	}

	public void testGetNode(int x, int y, boolean found) throws Exception {
		Node<Point> node = graph.getNode(new Point(x, y));
		if (found == (node == null)) {
			throw new Exception("Node " + (node == null ? "not " : "") + "got. Expected otherwise.");
		}
	}

	@Test
	public void testAddNode() throws Exception {
		testAddNode(20, 1000);
	}

	public void testAddNode(int x, int y) throws Exception {
		testGetNode(x, y, false);
		graph.addNode(new Point(x, y), 1);
		testGetNode(x, y, true);
	}

	@Test
	public void testHasEdge() throws Exception {
		testHasEdge(0, 0, 0, 1, true);
		testHasEdge(0, 0, 0, 2, false);
	}

	public void testHasEdge(int x1, int y1, int x2, int y2, boolean found) throws Exception {
		boolean edge = graph.hasEdge(new Point(x1, y1), new Point(x2, y2));
		if (edge != found) {
			throw new Exception("Edge " + (edge ? "" : "not ") + "found. Expected otherwise.");
		}
	}

	@Test
	public void testAddEdge() throws Exception {
		testAddEdge(0, 0, 0, 7);
	}

	public void testAddEdge(int x1, int y1, int x2, int y2) throws Exception {
		testHasEdge(x1, y1, x2, y2, false);
		graph.addEdge(new Point(x1, y1), new Point(x2, y2), true, 1, 1);
		testHasEdge(x1, y1, x2, y2, true);
	}

	@Test
	public void testRemoveEdge() throws Exception {
		testRemoveEdge(1, 0, 1, 7);
	}

	public void testRemoveEdge(int x1, int y1, int x2, int y2) throws Exception {
		testAddEdge(x1, y1, x2, y2);
		graph.removeEdge(new Point(x1, y1), new Point(x2, y2));
		testHasEdge(x1, y1, x2, y2, false);
	}

	// ----- SEARCH -----

	@Test
	public void testFindPathFromASTAR() throws Exception {
		Point[] path = new Point[] {new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)};
		testFindPathFromASTAR(0, 0, 0, 3, path);
	}

	public void testFindPathFromASTAR(int x1, int y1, int x2, int y2, Point[] correct) throws Exception {
		testFindPath(graph.findPathBFS(new Point(x1, y1), new Point(x2, y2)), correct);
	}

	@Test
	public void testFindPathFromWithTrackingData() throws Exception {

	}

	@Test
	public void testFindPathBFS() throws Exception {
		Point[] path = new Point[] {new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)};
		testFindPathBFS(0, 0, 0, 3, path);
	}

	public void testFindPathBFS(int x1, int y1, int x2, int y2, Point[] correct) throws Exception {
		testFindPath(graph.findPathBFS(new Point(x1, y1), new Point(x2, y2)), correct);
	}

	@Test
	public void testFindPathDFSwithCosts() throws Exception {

	}

	public void testFindPath(Optional<List<Node<Point>>> oPath, Point[] correct) throws Exception {
		if (oPath.isPresent() == (correct == null)) {
			if (oPath.isPresent())
				printPath(oPath.get());
			throw new Exception("Path is " + (oPath.isPresent() ? "not " : "") + "empty expected otherwise.");
		}
		if (correct == null)
			return;

		List<Node<Point>> path = oPath.get();
		if (path.size() != correct.length) {
			printPath(path);
			throw new Exception("Wrong path size. (1)");
		}
		for (int i = 0; i < correct.length; i++) {
			if (!path.get(i).getContent().equals(correct[i])) {
				printPath(path);
				throw new Exception("Wrong path size. (2)");
			}
		}
	}

	/**
	 * Prints a path to {@link System#err}.
	 * @param nodes The path to print.
	 */
	private void printPath(List<Node<Point>> nodes) {
		System.err.print("PATH: ");
		nodes.stream().map(Node::getContent).forEach((p) ->
				System.err.print("(" + p.getX() + ", " + p.getY() + ") ")
		);
		System.err.println();
	}

}
