package com.b3.search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Helpers to create {@link Graph Graphs}.
 * 
 * @author bxd428
 */
public class GraphBasic {
	
	/**
	 * Makes a very basic {@link Graph} with some connections.
	 * All edges are 1 in weight.
	 * 
	 * @return A newly generated {@link Graph}.
	 * @throws FileNotFoundException If someone deleted {@code graph.txt}.
	 */
	public static Graph getBasicGraph() throws FileNotFoundException {
		return getTextGraph("graph");
	}
	
	/**
	 * Creates a new {@link Graph} from a file.
	 * All edges are 1 in weight.
	 *
	 * @param graphName The name of the graph file.
	 * @return The newly created {@link Graph}.
	 */
	private static Graph getTextGraph(String graphName) throws FileNotFoundException {
		Graph g = new Graph(30, 30);
		fromFile(g, "tests/resources/test-worlds/" + graphName + ".txt");
		return g;
	}

	/**
	 * Copies a graph from a file into a {@link Graph}.
	 * All edges are 1 in weight.
	 * 
	 * @param g The {@link Graph} to copy into.
	 * @param filename The file name.
	 * @throws AssertionError If the height and width are less than 30.   
	 */
	protected static void fromFile(Graph g, String filename) throws FileNotFoundException {
		assert g.getHeight() >= 30;
		assert g.getWidth() >= 30;
		// Open file
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		g.getNodes().clear();

		// Loop through file lines
		br.lines().forEach((line) -> {
			String[] parts = line.split(":");
			String nodeStr = parts[0];
			String neighboursStr = parts[1].substring(1); // Without initial space

			String[] coords = nodeStr.replaceAll("[()]", "").split(",");
			Point p = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

			// Add neighbours
			if (!neighboursStr.isEmpty()) {
				String[] neighbours = neighboursStr.split(" ");

				for (String neighbour : neighbours) {
					String[] coordsNeighbour = neighbour.replaceAll("[()]", "").split(",");
					Point pn = new Point(Integer.parseInt(coordsNeighbour[0]), Integer.parseInt(coordsNeighbour[1]));

					// Add edge between p and pn
					g.addEdge(p, pn, 1f);
				}
			} else {
				g.addNode(p);
			}

		});
	}

}
