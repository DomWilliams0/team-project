package com.b3.world;

import com.b3.event.EventType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Building {

	private static Vector3 tempCullingPosition = new Vector3();

	// id?
	// door locations?
	// collision box?
	private Vector2 tilePosition;
	private Vector3 dimensions;

	private Vector3 centre;
	private Vector3 cullingDimensions;

	private ModelInstance modelInstance;

	private BuildingType type;

	private EventType event;

	/**
	 * @param tilePosition  The tile to place this building at
	 * @param dimensions    The (width, length, height) of the building
	 * @param modelInstance The building's model
	 */
	public Building(Vector2 tilePosition, Vector3 dimensions, ModelInstance modelInstance) {
		this.tilePosition = tilePosition;
		this.dimensions = dimensions;
		this.modelInstance = modelInstance;

		centre = new Vector3();
		cullingDimensions = new Vector3();
		BoundingBox boundingBox = new BoundingBox();
		modelInstance.calculateBoundingBox(boundingBox);
		boundingBox.getCenter(centre);
		boundingBox.getDimensions(cullingDimensions);
	}

	/**
	 * Updates the building's model to correspond to its current event
	 */
	private void renderEvent() {
		switch (event) {
			case FIRE:
				modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
				break;

			case ROBBERY:
				modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLUE));
				break;

			case DELIVERY:
				modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.YELLOW));
				break;
		}
	}

	public Vector2 getTilePosition() {
		return tilePosition;
	}

	public Vector3 getDimensions() {
		return dimensions;
	}


	/**
	 * @return The position of the building's entry and exit point
	 */
	public Vector2 getEntryPoint() {
		return null;
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public BuildingType getType() {
		return type;
	}

	public void setType(BuildingType type) {
		this.type = type;
	}

	/**
	 * Checks if an object is visible and therefore should be rendered
	 *
	 * @param camera The camera
	 * @return True if the object is visible, otherwise false
	 */
	public boolean isVisible(PerspectiveCamera camera) {
		modelInstance.transform.getTranslation(tempCullingPosition);
		tempCullingPosition.add(centre);
		return camera.frustum.boundsInFrustum(tempCullingPosition, cullingDimensions);
	}

	public void removeEvent() {
		event = null;
	}

	public EventType getEvent() {
		return event;
	}

	/**
	 * Marks this building as a target of an event
	 *
	 * @param event The event happening
	 */
	public void setEvent(EventType event) {
		this.event = event;
		renderEvent();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Building building = (Building) o;

		return tilePosition.equals(building.tilePosition) && dimensions.equals(building.dimensions);

	}

	@Override
	public int hashCode() {
		int result = tilePosition.hashCode();
		result = 31 * result + dimensions.hashCode();
		return result;
	}
}
