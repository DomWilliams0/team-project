package com.b3.entity.system;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class PhysicsSystem extends IteratingSystem {
	private ComponentMapper<PhysicsComponent> physics;


	public PhysicsSystem() {
		super(Family.all(PhysicsComponent.class).get());
		physics = ComponentMapper.getFor(PhysicsComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PhysicsComponent phys = physics.get(entity);
		phys.update(deltaTime);
	}
}
