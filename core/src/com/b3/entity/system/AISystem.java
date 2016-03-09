package com.b3.entity.system;

import com.b3.entity.Agent;
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
	private static final float ENTITY_SPEED = 100.f; // tweaked to perfection

	private ComponentMapper<PhysicsComponent> physicsComponents;
	private ComponentMapper<AIComponent> aiComponents;
	private WorldGraph worldGraph;


	/**
	 * Creates a new AI system to process the steering of each entity.
	 */
	public AISystem(WorldGraph worldGraph) {
		super(Family.all(PhysicsComponent.class, AIComponent.class).get());
		this.worldGraph = worldGraph;
		this.physicsComponents = ComponentMapper.getFor(PhysicsComponent.class);
		this.aiComponents = ComponentMapper.getFor(AIComponent.class);
	}


	/**
	 * Process an entity, depending on its behaviour and the current progress of the search in the world,
	 * will update the linear acceleration and velocity according to their steering rate.
	 *
	 * @param entity    The current Entity being processed
	 * @param deltaTime The delta time between the last and current frame
	 */
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PhysicsComponent phys = physicsComponents.get(entity);
		AIComponent ai = aiComponents.get(entity);

		ai.behaviour.tick(steeringMovement.setZero());
		steeringMovement
				.nor()
				.scl(deltaTime * ENTITY_SPEED);

		if (ai.behaviour.getType() == BehaviourType.FOLLOW_PATH)
			phys.body.setLinearVelocity(steeringMovement);
		else
			phys.body.applyForceToCenter(steeringMovement, true);


		if (worldGraph.hasSearchInProgress() &&
				worldGraph.isAgentSearching(entity) &&
				ai.behaviour.getType() == BehaviourType.FOLLOW_PATH) {
			BehaviourWithPathFind behaviour = (BehaviourWithPathFind) ai.behaviour;
			if (behaviour.hasArrivedForTheFirstTime())
				worldGraph.clearSearch((Agent) entity);
		}
	}
}
