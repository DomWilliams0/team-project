package com.b3.search;

import com.b3.world.World;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoaderBasic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Helpers to create {@link WorldGraph WorldGraphs}.
 * 
 * @author bxd428
 */
public class WorldGraphBasic {

	private static Field worldGraphField;
	private static Method processMapTileTypesMethod;
	
	/**
	 * Make the private fields in {@link World} accessible by reflection.
	 * And assign their reflective objects to the above fields.
	 */
	static {
		try {
			worldGraphField = World.class.getDeclaredField("worldGraph");
			worldGraphField.setAccessible(true);
			processMapTileTypesMethod = World.class.getDeclaredMethod("processMapTileTypes", TiledMap.class, boolean.class);
			processMapTileTypesMethod.setAccessible(true);
		} catch (NoSuchFieldException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Load a real {@code .tmx} file into a {@link WorldGraph}.
	 * @param mapName The name of the {@link .tmx} file to load.
	 * @return A newly generated {@link WorldGraph} with the map loaded in.
	 * @throws Exception If the map is unloadable or reflection fails or dolphins fly.
	 */
	public static WorldGraph getRealWorld(String mapName) throws Exception {
		World world = new World();
		TiledMap map = new TmxMapLoaderBasic().load("src-test/resources/test-worlds/" + mapName + ".tmx");
		WorldGraph graph = new WorldGraph(
				(int) map.getProperties().get("width"),
				(int) map.getProperties().get("height")
		);
		worldGraphField.set(world, graph);
		processMapTileTypesMethod.invoke(world, map, false);
		return graph;
	}
	
	/**
	 * Makes a very basic {@link WorldGraph} with some connections.
	 * All edges are 1 in weight.
	 * 
	 * @return A newly generated {@link WorldGraph}.
	 * @throws FileNotFoundException If someone deleted {@code graph.txt}.
	 */
	public static WorldGraph getBasicGraph() throws FileNotFoundException {
		return getTextGraph("graph");
	}
	
	/**
	 * Creates a new {@link WorldGraph} from a file.
	 * All edges are 1 in weight.
	 *
	 * @param graphName The name of the graph file.
	 * @return The newly created {@link WorldGraph}.
	 */
	private static WorldGraph getTextGraph(String graphName) throws FileNotFoundException {
		return fromFile("src-test/resources/test-worlds/" + graphName + ".txt");
	}

	/**
	 * Creates a new {@link WorldGraph} from a file.
	 * All edges are 1 in weight.
	 *
	 * @param filename The file name.
	 * @return The newly created {@link WorldGraph}.
	 */
	private static WorldGraph fromFile(String filename) throws FileNotFoundException {
		// Open file
		BufferedReader br = new BufferedReader(new FileReader(filename));

		// Create graph
		WorldGraph g = new WorldGraph(30, 30);
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
		return g;
	}

}
