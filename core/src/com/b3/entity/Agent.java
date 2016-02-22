package com.b3.entity;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourWander;
import com.b3.entity.component.AIComponent;
import com.b3.entity.component.ModelComponent;
import com.b3.entity.component.PhysicsComponent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.ModelController;
import com.b3.world.World;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class Agent extends Entity {

	private World world;
	private PhysicsComponent physics;
	private AIComponent ai;

	/**
	 * Spawns an agent into the given world at the given tile
	 *
	 * @param world   The world to spawn in
	 * @param tilePos The tile to spawn at
	 */
	public Agent(World world, Vector2 tilePos) {
		// bounds check
		if (!world.isInBounds(tilePos))
			throw new IllegalArgumentException("Tile position is out of range: " + tilePos);

		this.world = world;

		float diameter = Config.getFloat(ConfigKey.ENTITY_DIAMETER);
		float radius = diameter / 2f;

		// render
		add(new ModelComponent(new ModelController("agent", world.getModelManager(), true)));

		// physics
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.linearDamping = 1f;
		physics = new PhysicsComponent(world.getPhysicsWorld(), bodyDef, tilePos, radius);
		physics.getBody().setUserData(this);
		add(physics);

		// ai
		ai = new AIComponent(new BehaviourWander(this));
		add(ai);

		world.getEngine().addEntity(this);
	}

	/**
	 * @return The entity's current behaviour
	 * This is never null, but will be BehaviourNop when there is no current behaviour
	 */
	public Behaviour getBehaviour() {
		return ai.behaviour;
	}

	public void setBehaviour(Behaviour behaviour) {
		ai.behaviour = behaviour;
	}

	public World getWorld() {
		return world;
	}

	public PhysicsComponent getPhysicsComponent() {
		return physics;
	}
}