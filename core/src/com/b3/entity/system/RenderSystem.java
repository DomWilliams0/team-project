package com.b3.entity.system;

import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.component.RenderComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class RenderSystem extends IteratingSystem {
	private ComponentMapper<PhysicsComponent> physics;
	private ComponentMapper<RenderComponent> renders;

	private ShapeRenderer shapeRenderer;
	private PerspectiveCamera camera;

	public RenderSystem(PerspectiveCamera camera) {
		super(Family.all(RenderComponent.class, PhysicsComponent.class).get());
		this.camera = camera;
		this.renders = ComponentMapper.getFor(RenderComponent.class);
		this.physics = ComponentMapper.getFor(PhysicsComponent.class);
		this.shapeRenderer = new ShapeRenderer();
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
		RenderComponent render = renders.get(entity);
		PhysicsComponent phys = physics.get(entity);
		Vector2 pos = phys.getPosition();

		shapeRenderer.setColor(render.colour);
		shapeRenderer.circle(pos.x, pos.y, render.radius, 20);
	}
}
