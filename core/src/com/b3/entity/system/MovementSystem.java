package com.b3.entity.system;

import com.b3.entity.component.PositionComponent;
import com.b3.entity.component.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class MovementSystem extends IteratingSystem {
	private ComponentMapper<PositionComponent> positions;
	private ComponentMapper<VelocityComponent> velocities;

	public MovementSystem() {
		super(Family.all(PositionComponent.class, VelocityComponent.class).get());
		positions = ComponentMapper.getFor(PositionComponent.class);
		velocities = ComponentMapper.getFor(VelocityComponent.class);
	}

	public void processEntity(Entity entity, float deltaTime) {
		PositionComponent position = positions.get(entity);
		VelocityComponent velocity = velocities.get(entity);

		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;
	}
}
