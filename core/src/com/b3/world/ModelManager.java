package com.b3.world;

import com.b3.util.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Handles all the {@link Model Models}.
 * Includes, loading and rendering.
 */
public class ModelManager {

	/**
	 * The {@link ModelInstance ModelInstances} to render,
	 */
	private Array<ModelInstance> models = new Array<>();

	/**
	 * A map of {@link Model} names to "passBacks" that require a new
	 * {@link ModelInstance}.
	 * The model names in the map will be loaded at some point and
	 * the "passBacks" will get their
	 * {@link ModelInstance ModelInstances}.
	 */
	private HashMap<String, ArrayList<Consumer<ModelInstance>>> unloadedPassBacks = new HashMap<>();

	private Environment environment;

	private AssetManager assetManager = new AssetManager();
	private ModelBatch modelBatch = new ModelBatch();

	public ModelManager(World world, Environment environment, TiledMap map) {
		this.environment = environment;

		MapLayer modelLayer = map.getLayers().get("models");
		if (modelLayer == null)
			return;

		for (MapObject object : modelLayer.getObjects()) {
			MapProperties props = object.getProperties();
			String modelName = props.get("model", String.class);
			float x = (props.get("x", Float.class) / Utils.TILESET_RESOLUTION) + 0.5f;
			float y = (props.get("y", Float.class) / Utils.TILESET_RESOLUTION) + 0.5f;
			float rotation = Float.parseFloat(props.get("rotation", String.class));
			// Possibly not flipped, should really check.
			ModelController controller = new ModelController(modelName, this, true).setPosition(x, y, 0f).setRotation(rotation);
			world.addFlattenListener((flat) -> controller.setVisible(!flat));
		}

		TiledMapTileLayer objectLayer = (TiledMapTileLayer) map.getLayers().get("objects");
		for (int x = 0; x < objectLayer.getWidth(); x++) {
			for (int y = 0; y < objectLayer.getHeight(); y++) {
				TiledMapTileLayer.Cell cell = objectLayer.getCell(x, y);
				if (cell == null)
					continue;
				TileType type = TileType.getFromCell(cell);
				if (type == TileType.UNKNOWN)
					continue;
				File f = new File(getModelPath(type.name().toLowerCase()));
				if (!f.exists())
					continue;

				// Possibly not flipped, should really check.
				final ModelController controller = new ModelController(type.name().toLowerCase(), this, true)
						.setPosition(x + 0.5f, y + 0.5f, 0f);
				final int xF = x, yF = y;
				world.addFlattenListener((flat) -> {
					controller.setVisible(!flat);
					objectLayer.setCell(xF, yF, flat ? cell : null);
				});
			}
		}
	}

	/**
	 * Renders all the {@link ModelInstance ModelInstances}.
	 *
	 * @param worldCamera The {@link WorldCamera} to render them for.
	 */
	public void render(WorldCamera worldCamera) {
		tryLoadAssets();
		modelBatch.begin(worldCamera);
		modelBatch.render(models, environment);
		modelBatch.end();
	}

	/**
	 * The path convention that {@link Model Models} follow.
	 * For easier loading, so we don't have to pass around paths.
	 *
	 * @param modelName The name of the {@link Model} to get the file
	 *                  path of.
	 * @return The path for the given {@link Model} name.
	 */
	private String getModelPath(String modelName) {
		return "core/assets/world/assets/" + modelName + "/" + modelName + ".g3db";
	}

	/**
	 * Loads and passes back any {@link ModelInstance ModelInstances}
	 * to any {@link ModelController ModelControllers} that requested
	 * them.
	 *
	 * @see #requestModel(String, Consumer)
	 */
	public void tryLoadAssets() {
		// May as well queue up some asset loads.
		assetManager.update();

		// List used to prevent concurrent modification exceptions.
		ArrayList<String> toRemoveFromMap = new ArrayList<>();

		for (String modelName : unloadedPassBacks.keySet()) {
			if (assetManager.isLoaded(getModelPath(modelName), Model.class)) {
				// the model is now loaded, so lets pass back some instances to the ModelControllers.
				Model model = assetManager.get(getModelPath(modelName), Model.class);

				for (Consumer<ModelInstance> passBack : unloadedPassBacks.get(modelName)) {
					ModelInstance instance = new ModelInstance(model);
					// give it back to the ModelController.
					passBack.accept(instance);
					// add it to the list to render.
					models.add(instance);
				}
				toRemoveFromMap.add(modelName);
			} else {
				// if it is not loaded then try load it.
				assetManager.load(getModelPath(modelName), Model.class);
			}
		}
		for (String modelName : toRemoveFromMap) {
			unloadedPassBacks.remove(modelName);
		}

		// Queue up the asset loads for the next batch.
		assetManager.update();
	}

	/**
	 * Requests a new {@link ModelInstance} for a particular
	 * {@link Model} name.
	 * The {@link ModelInstance} will be given back at some point by
	 * the {@code passBack} {@link Consumer}.
	 *
	 * @param modelName The name of the {@link Model} to get.
	 * @param passBack  The {@link Consumer} that will be given the
	 *                  {@link ModelInstance}.
	 */
	protected void requestModel(String modelName, Consumer<ModelInstance> passBack) {
		if (assetManager.isLoaded(getModelPath(modelName))) {
			// already loaded, so give it back straight away.
			ModelInstance instance = new ModelInstance(assetManager.get(modelName, Model.class));
			// give it back to the requester..
			passBack.accept(instance);
			// add it to the list to render.
			models.add(instance);
		} else {
			// isn't loaded, so add it to the queue to load.
			if (!unloadedPassBacks.containsKey(modelName)) {
				unloadedPassBacks.put(modelName, new ArrayList<>());
			}
			unloadedPassBacks.get(modelName).add(passBack);
			// it will be given back at some point.
		}
	}

}
