package com.b3.entity.system;

import com.b3.entity.ai.BehaviourType;
import com.b3.entity.ai.BehaviourWithPathFind;
import com.b3.entity.component.AIComponent;
import com.b3.entity.component.PhysicsComponent;
import com.b3.search.WorldGraph;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

/**
 * Entity system in charge of ticking entity behaviours, and applying their
 * desired steering to the physics component
 */
public class AISystem extends IteratingSystem {
	private static Vector2 steeringMovement = new Vector2();

	private ComponentMapper<PhysicsComponent> physicsComponents;
	private ComponentMapper<AIComponent> aiComponents;
	private WorldGraph worldGraph;


	public AISystem(WorldGraph worldGraph) {
		super(Family.all(PhysicsComponent.class, AIComponent.class).get());
		this.worldGraph = worldGraph;
		this.physicsComponents = ComponentMapper.getFor(PhysicsComponent.class);
		this.aiComponents = ComponentMapper.getFor(AIComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PhysicsComponent phys = physicsComponents.get(entity);
		AIComponent ai = aiComponents.get(entity);

		ai.behaviour.tick(steeringMovement.setZero());
		phys.getBody().applyForceToCenter(steeringMovement, true);


		if (worldGraph.hasSearchInProgress() &&
				worldGraph.getCurrentSearchAgent() == entity &&
				ai.behaviour.getType() == BehaviourType.FOLLOW_PATH) {
			BehaviourWithPathFind behaviour = (BehaviourWithPathFind) ai.behaviour;
			if (behaviour.hasArrivedForTheFirstTime())
				worldGraph.clearCurrentSearch();
		}
	}
}
