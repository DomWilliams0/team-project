package com.b3.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Essentially a wrapper around a {@link ModelInstance}.
 * But, does not actually contain the instance only the {@code transform}.
 */
public class ModelController {
	
	/**
	 * The {@code transform} of the {@link ModelInstance}.
	 */
	private Matrix4 transform;
	
	/**
	 * Our storage of the model's position.
	 * After updating this a call should be made to {@link #updateTransform()}.
	 */
	private Vector3 position;
	
	/**
	 * Our storage of the model's rotation.
	 * After updating this a call should be made to {@link #updateTransform()}.
	 */
	private float rotation = 0;
	
	/**
	 * Whether the model texture is flipped.
	 */
	private final boolean flipped;
	
	/**
	 * Whether the model should be rendered.
	 */
	private boolean visible = true;
	
	/**
	 * Creates a new ModelController.
	 * The {@link ModelInstance} is made by the {@code modelManager} and passed back to this controller.
	 * @param modelName    The name of the model, same as the filename.
	 * @param modelManager The {@link ModelManager} to create the {@link ModelInstance} and render it,
	 * @param flipped      Whether the model's texture is flipped.
	 */
	public ModelController(String modelName, ModelManager modelManager, boolean flipped) {
		this.flipped = flipped; // TODO - flipped should be fetched from a file with the model.
		setPositionAndRotation(0, 0, 0, 0);
		
		modelManager.requestModel(this, modelName, (transform) -> {
			this.transform = transform;
			updateTransform();
		});
	}
	
	/**
	 * Sets the render position of the model.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The same instance.
	 */
	public ModelController setPosition(float x, float y, float z) {
		position = new Vector3(x, y, z);
		updateTransform();
		return this;
	}
	
	/**
	 * Sets the render position of the model.
	 * {@code vector3} will be cloned.
	 * @param vector3 The new position.
	 * @return The same instance.
	 */
	public ModelController setPosition(Vector3 vector3) {
		position = new Vector3(vector3);
		updateTransform();
		return this;
	}
	
	/**
	 * Sets the render rotation of the model.
	 * @param degrees The new rotation.
	 * @return The same instance.
	 */
	public ModelController setRotation(float degrees) {
		rotation = degrees;
		updateTransform();
		return this;
	}
	
	/**
	 * Sets the render position and rotation of the model.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @param degrees The new rotation.
	 * @return The same instance.
	 */
	public ModelController setPositionAndRotation(float x, float y, float z, float degrees) {
		position = new Vector3(x, y, z);
		rotation = degrees;
		updateTransform();
		return this;
	}
	
	/**
	 * Sets the render position and rotation of the model.
	 * {@code vector3} will be cloned.
	 * @param vector3 The new position.
	 * @param degrees The new rotation.
	 * @return The same instance.
	 */
	public ModelController setPositionAndRotation(Vector3 vector3, float degrees) {
		position = new Vector3(vector3);
		rotation = degrees;
		updateTransform();
		return this;
	}
	
	/**
	 * Whether the model is visible/renders or not.
	 * @return <code>true</code> if the model is visible;
	 *         <code>false</code> otherwise.
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Sets whether the model is visible/renders or not.
	 * @param visible <code>true</code> will make the model visible/render.
	 * @return The same instance.
	 */
	public ModelController setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	/**
	 * Updates the {@code transform} of the {@link ModelInstance}.
	 */
	private void updateTransform() {
		if (transform == null)
			return;
		if (flipped) {
			transform.set(new float[] {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1})
					.translate(position.x, position.z, position.y)
					.rotate(Vector3.Y, rotation);
		} else {
			transform.set(new Matrix4())
					.translate(position)
					.rotate(Vector3.Z, -rotation);
		}
	}
	
}
