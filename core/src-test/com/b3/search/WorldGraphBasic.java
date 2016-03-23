package com.b3.search;

import com.b3.world.World;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoaderBasic;
import sun.reflect.ReflectionFactory;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
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
		// Create a World object without calling the constructor.
		ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
		Constructor objDef = Object.class.getDeclaredConstructor();
		Constructor intConstr = rf.newConstructorForSerialization(World.class, objDef);
		World world = World.class.cast(intConstr.newInstance());
		
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
	 * Creates a new {@link Graph} from a file.
	 * All edges are 1 in weight.
	 *
	 * @param graphName The name of the graph file.
	 * @return The newly created {@link WorldGraph}.
	 */
	private static WorldGraph getTextGraph(String graphName) throws FileNotFoundException {
		WorldGraph g = new WorldGraph(30, 30);
		GraphBasic.fromFile(g, "src-test/resources/test-worlds/" + graphName + ".txt");
		return g;
	}

}
