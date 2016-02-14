package com.b3.entity.system;

import com.b3.entity.component.AIComponent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

public class AISystem extends IteratingSystem {
	private static Vector2 steeringMovement = new Vector2();

	private ComponentMapper<PhysicsComponent> physicsComponents;
	private ComponentMapper<AIComponent> aiComponents;


	public AISystem() {
		super(Family.all(PhysicsComponent.class, AIComponent.class).get());
		physicsComponents = ComponentMapper.getFor(PhysicsComponent.class);
		aiComponents = ComponentMapper.getFor(AIComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PhysicsComponent phys = physicsComponents.get(entity);
		AIComponent ai = aiComponents.get(entity);

		ai.behaviour.tick(steeringMovement);
		phys.getBody().applyForceToCenter(steeringMovement, true);
	}
}
