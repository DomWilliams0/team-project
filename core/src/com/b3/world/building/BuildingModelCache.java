package com.b3.world.building;

import com.b3.util.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a set of building Models for reuse
 *
 * @author dxw405 nbg481
 */
public class BuildingModelCache implements Disposable {
	private final Map<Vector3, Model> models;
	private final ModelBuilder builder;

	private Texture nightSide;
	private Texture topSide;
	private Texture nightSideFlipped;

	/**
	 * creates a new building model cache linked to a world
	 */
	public BuildingModelCache() {
		models = new HashMap<>();
		builder = new ModelBuilder();
		loadTextures();
	}

	/**
	 * load the textures that are required for buildings sides and roof
	 */
	private void loadTextures() {
		nightSide = new Texture("world/popups/night_side_scaled_new.jpg");
		nightSideFlipped = new Texture("world/popups/night_side_scaled_new_frontback.jpg");
		topSide = new Texture("world/popups/roof.jpg");
	}

	/**
	 * Creates a building with the given position and dimensions
	 * If a model already exists for the given dimensions, it is reused,
	 * otherwise a new model is created and cached for future use.
	 *
	 * @param pos        The tile position
	 * @param dimensions (width, length, height) of the building
	 * @return The newly created building model instance
	 */
	public ModelInstance createBuilding(Vector2 pos, Vector3 dimensions, boolean flat) {
		Model model = models.get(dimensions);
		if (model == null) {
			int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
			ModelBuilder modelBuilder = new ModelBuilder();

			float changer = (float) (0.5 * dimensions.z);

			// if not flat buildings render all the texture
			if (!flat) {
				modelBuilder.begin();
				// modelBuilder.part("front", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(brick)))
				//		.rect(-2f,-2f,-2f, -2f,2f,-2f,  2f,2f,-2, 2f,-2f,-2f, 0,0,-1);
				modelBuilder.part("back", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(topSide))) // ACTUAL TOP
						.rect(-2f, 2f, 2f, -2f, -2f, 2f, 2f, -2f, 2f, 2f, 2f, 2f, 0, 0, 1);
				modelBuilder.part("bottom", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(nightSideFlipped))) // ACTUAL FRONT
						.rect(-2f, -2f, 2f, -2f, -2f, -changer, 2f, -2f, -changer, 2f, -2f, 2f, 0, -1, 0);
				modelBuilder.part("top", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(nightSideFlipped))) // ACTUAL BACK
						.rect(-2f, 2f, -changer, -2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, -changer, 0, 1, 0);
				modelBuilder.part("left", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(nightSide))) // LEFT
						.rect(-2f, -2f, 2f, -2f, 2f, 2f, -2f, 2f, -changer, -2f, -2f, -changer, -1, 0, 0);
				modelBuilder.part("right", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(nightSide))) // RIGHT
						.rect(2f, -2f, -changer, 2f, 2f, -changer, 2f, 2f, 2f, 2f, -2f, 2f, 1, 0, 0);
				model = modelBuilder.end();
			} else {
				// otherwise just a plain box - good for efficiency as users won't actually see the buildings textures when they are flat
				model = builder.createBox(dimensions.x, dimensions.y, dimensions.z,
						new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
			}
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

	/**
	 * dispose of the model cache cleanly.
	 */
	@Override
	public void dispose() {
		models.values().forEach(Model::dispose);
	}

}
