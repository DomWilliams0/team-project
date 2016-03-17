package com.b3.world.building;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;


/**
 * Represents a building in the world, containing its dimensions, position and cache
 *
 * @author dxw405
 */
public class Building {

	private static final Vector3 tempCullingPosition = new Vector3();

	private final Vector2 tilePosition;
	private final Vector3 dimensions;

	private final Vector3 centre;
	private final Vector3 cullingDimensions;

	private boolean flattened;
	private final ModelInstance modelInstance;
	private final ModelInstance modelInstanceFlat;

	/**
	 * @param tilePosition  The tile to place this building at
	 * @param dimensions    The (width, length, height) of the building
	 * @param buildingCache The {@link BuildingModelCache} of course.
	 */
	public Building(Vector2 tilePosition, Vector3 dimensions, BuildingModelCache buildingCache) {
		this.tilePosition = tilePosition;
		this.dimensions = dimensions;
		this.modelInstance = buildingCache.createBuilding(tilePosition, dimensions, false);
		Vector3 flatDimensions = new Vector3(dimensions);
		flatDimensions.z = 0;
		this.modelInstanceFlat = buildingCache.createBuilding(tilePosition, flatDimensions, true);

		centre = new Vector3();
		cullingDimensions = new Vector3();
		BoundingBox boundingBox = new BoundingBox();
		modelInstance.calculateBoundingBox(boundingBox);
		boundingBox.getCenter(centre);
		boundingBox.getDimensions(cullingDimensions);
		this.flattened = false;
	}

	/**
	 * @return the tile position that this building is placed at (bottom left corner)
	 */
	public Vector2 getTilePosition() {
		return tilePosition;
	}

	/**
	 * @return the dimensions of the current building
	 */
	public Vector3 getDimensions() {
		return dimensions;
	}

	/**
	 * @param flatten iff true then the building will be flat, otherwise not
	 */
	public void setFlattened(boolean flatten) {
		flattened = flatten;
	}

	/**
	 * @return the instance of the building, if flattened then no textures - just a black box; if not then contains textures too
	 */
	public ModelInstance getModelInstance() {
		if (flattened) {
			return modelInstanceFlat;
		} else {
			return modelInstance;
		}
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

	/**
	 * checks if one building is equal to o
	 *
	 * @param o the building to be compared to
	 * @return true if equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Building building = (Building) o;

		return tilePosition.equals(building.tilePosition) && dimensions.equals(building.dimensions);
	}

	/**
	 * @return the hash code of the current building
	 */
	@Override
	public int hashCode() {
		int result = tilePosition.hashCode();
		result = 31 * result + dimensions.hashCode();
		return result;
	}

}
