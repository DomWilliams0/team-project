package com.b3.entity.system;

import com.b3.entity.component.PositionComponent;
import com.b3.entity.component.RenderComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RenderSystem extends IteratingSystem {
	private ComponentMapper<PositionComponent> positions;
	private ComponentMapper<RenderComponent> renders;

	private ShapeRenderer shapeRenderer;
	private PerspectiveCamera camera;

	public RenderSystem(PerspectiveCamera camera) {
		super(Family.all(PositionComponent.class, RenderComponent.class).get());
		this.camera = camera;
		this.renders = ComponentMapper.getFor(RenderComponent.class);
		this.positions = ComponentMapper.getFor(PositionComponent.class);
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
		PositionComponent position = positions.get(entity);
		RenderComponent render = renders.get(entity);

		shapeRenderer.setColor(render.colour);
		shapeRenderer.circle(position.x, position.y, render.radius, 10);
	}
}
