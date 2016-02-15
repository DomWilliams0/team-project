package com.b3.world;

import com.b3.util.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a set of building Models for reuse
 */
public class BuildingModelCache implements Disposable {
	private Map<Vector3, Model> models;
	private ModelBuilder builder;

	public BuildingModelCache() {
		models = new HashMap<>();
		builder = new ModelBuilder();
	}

	/**
	 * Creates a building with the given position and dimensions
	 * If a model already exists for the given dimensions, it is reused,
	 *  otherwise a new model is created and cached for future use.
	 * @param pos The tile position
	 * @param dimensions (width, length, height) of the building
	 * @return The newly created building model instance
	 */
	public ModelInstance createBuilding(Vector2 pos, Vector3 dimensions) {
		Model model = models.get(dimensions);
		if (model == null) {
			model = builder.createBox(dimensions.x, dimensions.y, dimensions.z,
					new Material(ColorAttribute.createDiffuse(makeBuildingColour())),
					VertexAttributes.Usage.Normal | VertexAttributes.Usage.Position);

			models.put(dimensions, model);
		}

		return new ModelInstance(model, pos.x + dimensions.x / 2, pos.y + dimensions.y / 2, dimensions.z / 2);
	}

	/**
	 * @return A random building colour
	 */
	private Color makeBuildingColour() {
		float c = Utils.randomRange(40f / 255f, 100f / 255f);
		return new Color(c, c, c, 0.8f);
	}

	@Override
	public void dispose() {
		models.values().forEach(Model::dispose);
	}

}
