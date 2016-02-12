package com.b3.searching.roboticsGraphHelpers;

import com.b3.searching.utils.ReadFile;

import java.io.IOException;

public class GraphBasic {

	public static Graph<Point> getBasicGraph() {
		return getGraph("graph");
	}

	private static Graph<Point> getGraph(String graphName) {
		return fromFile("src-test/resources/" + graphName + ".txt");
	}

	/**
	 * Creates a new graph from a file
	 * @param filename The file name
	 * @return The newly created graph
	 */
	private static Graph<Point> fromFile(String filename) {
		// Open file and read lines
		ReadFile rf = new ReadFile(filename);
		String[] lines;
		try {
			lines = rf.readLines();
		} catch (IOException e) {
			return null;
		}

		// Create graph
		Graph<Point> g = new Graph<>();

		// Loop through file lines
		for (String line : lines) {
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
					g.addEdge(p, pn, true, 0, 0);
				}
			} else {
				g.addNode(p, 0);
			}

		}
		return g;
	}

}
