package com.b3.world;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ModelController {

	private Matrix4 transform;
	private Vector3 position;
	private float rotation = 0;
	private boolean flipped;
	private boolean visible = true;

	public ModelController(String modelName, ModelManager modelManager, boolean flipped) {
		this.flipped = flipped;
		setPositionAndRotation(0, 0, 0, 0);
		modelManager.requestModel(this, modelName, (transform) -> {
			this.transform = transform;
			updateTransform();
		});
	}

	public ModelController setPosition(float x, float y, float z) {
		position = new Vector3(x, y, z);
		updateTransform();
		return this;
	}

	public ModelController setPosition(Vector3 vector3) {
		position = new Vector3(vector3);
		updateTransform();
		return this;
	}

	public ModelController setRotation(float degrees) {
		rotation = degrees;
		updateTransform();
		return this;
	}
	
	public ModelController setPositionAndRotation(float x, float y, float z, float degrees) {
		position = new Vector3(x, y, z);
		rotation = degrees;
		updateTransform();
		return this;
	}
	
	public ModelController setPositionAndRotation(Vector3 vector3, float degrees) {
		position = new Vector3(vector3);
		rotation = degrees;
		updateTransform();
		return this;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public ModelController setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

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
