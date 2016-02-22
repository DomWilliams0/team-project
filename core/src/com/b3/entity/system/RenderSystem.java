package com.b3.entity.system;

import com.b3.entity.component.ModelComponent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Entity system to render entities as simple coloured shapes
 */
public class RenderSystem extends IteratingSystem {

	private ComponentMapper<PhysicsComponent> physics;
	private ComponentMapper<ModelComponent> models;

	private ShapeRenderer shapeRenderer;
	private PerspectiveCamera camera;
	private Vector2 previousPosition;

	public RenderSystem(PerspectiveCamera camera) {
		super(Family.all(ModelComponent.class, PhysicsComponent.class).get());
		this.camera = camera;
		this.physics = ComponentMapper.getFor(PhysicsComponent.class);
		this.models = ComponentMapper.getFor(ModelComponent.class);
		this.shapeRenderer = new ShapeRenderer();
		this.previousPosition = new Vector2();
	}

	@Override
	public void beginProcessing() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	}

	@Override
	public void endProcessing() {
		shapeRenderer.end();
	}

	public void processEntity(Entity entity, float deltaTime) {
		ModelComponent model = models.get(entity);
		PhysicsComponent phys = physics.get(entity);
		Vector2 pos = phys.getPosition();

		model.render(pos.x, pos.y, (float) angle(previousPosition, pos));
		previousPosition = new Vector2(pos);
	}

	private double angle(Vector2 v1, Vector2 v2) {
		double xdiff = v1.x -v2.x,
				ydiff = v2.y - v1.y;
		return Math.toDegrees(Math.atan2(ydiff, xdiff));
	}

}
