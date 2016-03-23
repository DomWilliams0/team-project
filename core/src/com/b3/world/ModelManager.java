package com.b3.world;

import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Tuple;
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
import com.badlogic.gdx.math.Matrix4;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Handles all the {@link Model Models}.
 * Includes, loading and rendering.
 *
 * @author bxd428
 */
public class ModelManager {

	/**
	 * The {@link ModelInstance ModelInstances} to render,
	 */
	private final ArrayList<Tuple<ModelController, ModelInstance>> models = new ArrayList<>();

	/**
	 * A {@link ArrayList list} of all the fixed position {@link Model Models}.
	 */
	private final ArrayList<ModelController> staticModels = new ArrayList<>();

	/**
	 * A map of {@link Model} names to {@code "passBacks"} that require a new
	 * {@link ModelInstance}.
	 * The model names in the map will be loaded at some point and
	 * the {@code "passBacks"} will get their
	 * {@link ModelInstance ModelInstances}.
	 */
	private final HashMap<String, ArrayList<Tuple<ModelController, Consumer<Matrix4>>>> unloadedPassBacks = new HashMap<>();

	private final Environment environment;

	private final AssetManager assetManager = new AssetManager();
	private final ModelBatch modelBatch = new ModelBatch();

	/**
	 * creates a new model manager
	 *
	 * @param environment the environment to use in managing the models
	 * @param map         the tiled map the buildings will be placed on
	 */
	public ModelManager(Environment environment, TiledMap map) {
		this.environment = environment;

		boolean renderStatics = Config.getBoolean(ConfigKey.RENDER_STATIC_MODELS);

		// Create static models from the models layer.
		MapLayer modelLayer = map.getLayers().get("models");
		if (modelLayer != null) {
			for (MapObject object : modelLayer.getObjects()) {
				// Get the properties of the model.
				MapProperties props = object.getProperties();
				String modelName = props.get("model", String.class);
				float x = (props.get("x", Float.class) / Utils.TILESET_RESOLUTION) + 0.5f;
				float y = (props.get("y", Float.class) / Utils.TILESET_RESOLUTION) + 0.5f;
				float rotation = Float.parseFloat(props.get("rotation", String.class));

				// Possibly not flipped, should really check.
				staticModels.add(
						new ModelController(modelName, this, true)
								.setPositionAndRotation(x, y, 0f, rotation)
								.setVisible(renderStatics)
				);
			}
		}

		if (map.getLayers().get("objects") != null) {
			TiledMapTileLayer objectLayer = (TiledMapTileLayer) map.getLayers().get("objects");
			for (int x = 0; x < objectLayer.getWidth(); x++) {
				for (int y = 0; y < objectLayer.getHeight(); y++) {
					// Check through each tile in the objects layer and see if it has a corresponding model.
					TiledMapTileLayer.Cell cell = objectLayer.getCell(x, y);
					if (cell == null)
						continue;
					TileType type = TileType.getFromCell(cell);
					if (type == TileType.UNKNOWN)
						continue;

					// Check if the TileType name corresponds to a model.
					File f = new File(getModelPath(type.name().toLowerCase()));
					if (!f.exists())
						continue;

					final int xF = x, yF = y;
					// Possibly not flipped, should really check.
					ModelController controller = new ModelController(type.name().toLowerCase(), this, true) {

						@Override
						public ModelController setVisible(boolean visible) {
							objectLayer.setCell(xF, yF, visible ? null : cell);
							return super.setVisible(visible);
						}

					}.setPosition(x + 0.5f, y + 0.5f, 0f).setVisible(renderStatics);
					staticModels.add(controller);
				}
			}
		}
	}

	/**
	 * Changes the visibility of all the statically loaded models.
	 *
	 * @param visible Whether they should be visible.
	 */
	public void setStaticsVisible(boolean visible) {
		for (ModelController controller : staticModels) {
			controller.setVisible(visible);
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
		models.stream()
				.filter(instance -> instance.getFirst().isVisible())
				.forEach(instance -> modelBatch.render(instance.getSecond(), environment));
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
		return "world/assets/" + modelName + "/" + modelName + ".g3db";
	}

	/**
	 * Loads and passes back any {@link ModelInstance ModelInstances}
	 * to any {@link ModelController ModelControllers} that requested
	 * them.
	 *
	 * @see #requestModel(ModelController, String, Consumer)
	 */
	private void tryLoadAssets() {
		// May as well queue up some asset loads.
		assetManager.update();

		// List used to prevent concurrent modification exceptions.
		ArrayList<String> toRemoveFromMap = new ArrayList<>();

		for (String modelName : unloadedPassBacks.keySet()) {
			if (assetManager.isLoaded(getModelPath(modelName), Model.class)) {
				// the model is now loaded, so lets pass back some instances to the ModelControllers.
				Model model = assetManager.get(getModelPath(modelName), Model.class);

				for (Tuple<ModelController, Consumer<Matrix4>> passBack : unloadedPassBacks.get(modelName)) {
					ModelInstance instance = new ModelInstance(model);
					// give it back to the ModelController.
					passBack.getSecond().accept(instance.transform);
					// add it to the list to render.
					models.add(new Tuple<>(passBack.getFirst(), instance));
				}
				toRemoveFromMap.add(modelName);
			} else {
				// if it is not loaded then try load it.
				assetManager.load(getModelPath(modelName), Model.class);
			}
		}
		toRemoveFromMap.forEach(unloadedPassBacks::remove);

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
	protected void requestModel(ModelController controller, String modelName, Consumer<Matrix4> passBack) {
		if (assetManager.isLoaded(getModelPath(modelName))) {
			// already loaded, so give it back straight away.
			ModelInstance instance = new ModelInstance(assetManager.get(modelName, Model.class));
			// give it back to the requester..
			passBack.accept(instance.transform);
			// add it to the list to render.
			models.add(new Tuple<>(controller, instance));
		} else {
			// isn't loaded, so add it to the queue to load.
			if (!unloadedPassBacks.containsKey(modelName)) {
				unloadedPassBacks.put(modelName, new ArrayList<>());
			}
			unloadedPassBacks.get(modelName).add(new Tuple<>(controller, passBack));
			// it will be given back at some point.
		}
	}

}
