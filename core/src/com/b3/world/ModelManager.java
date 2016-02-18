package com.b3.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

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

	public ModelManager(Environment environment) {
		this.environment = environment;

		// --- Place all the static models. ---
		// lights
		new ModelController("light", this, true).setPosition(37, 39, 0).setRotation(270);
		new ModelController("light", this, true).setPosition(35, 39, 0).setRotation(180);
		new ModelController("light", this, true).setPosition(35, 37, 0).setRotation(90);
		new ModelController("light", this, true).setPosition(37, 37, 0).setRotation(0);
		// TODO - Lighting.
	}

	/**
	 * Renders all the {@link ModelInstance ModelInstances}.
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
