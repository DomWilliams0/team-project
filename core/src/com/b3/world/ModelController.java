package com.b3.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ModelController {

	private ModelInstance instance;
	private Vector3 position;
	private float rotation = 0;
	private boolean flipped;
	private boolean visible = true;

	public ModelController(String modelName, ModelManager modelManager, boolean flipped) {
		this.flipped = flipped;
		setPosition(0, 0, 0);
		setRotation(0);
		modelManager.requestModel(modelName, (instance) -> {
			this.instance = instance;
			updateTransform();
		});
		updateTransform();
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

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			updateTransform();
		} else {
			instance.transform = new Matrix4().translate(-200f, -20f, 200f);
		}
	}

	private void updateTransform() {
		if (!visible)
			return;
		if (instance == null)
			return;
		if (flipped) {
			instance.transform
					= new Matrix4(new float[] {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1})
					.translate(position.x, position.z, position.y)
					.rotate(Vector3.Y, rotation);
		} else {
			instance.transform
					= new Matrix4()
					.translate(position)
					.rotate(Vector3.Z, -rotation);
		}
	}

}
