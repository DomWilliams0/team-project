package com.b3.searching;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class WorldGraphBasic {

	public static WorldGraph<Point> getBasicGraph() throws FileNotFoundException {
		return getGraph("graph");
	}

	private static WorldGraph<Point> getGraph(String graphName) throws FileNotFoundException {
		return fromFile("src-test/resources/" + graphName + ".txt");
	}

	/**
	 * Creates a new graph from a file
	 * @param filename The file name
	 * @return The newly created graph
	 */
	private static WorldGraph<Point> fromFile(String filename) throws FileNotFoundException {
		// Open file
		BufferedReader br = new BufferedReader(new FileReader(filename));

		// Create graph
		WorldGraph<Point> g = new WorldGraph<>(30, 30);

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
					g.addEdge(p, pn, true, 0, 0);
				}
			} else {
				g.addNode(p, 0);
			}

		});
		return g;
	}

}
