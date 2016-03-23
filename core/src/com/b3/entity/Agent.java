package com.b3.entity;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourNop;
import com.b3.entity.ai.BehaviourType;
import com.b3.entity.component.AIComponent;
import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.component.RenderComponent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.ModelController;
import com.b3.world.World;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * @author dxw405
 */
public class Agent extends Entity {

	public static final Color FLOCKING_COLOUR = Color.GRAY;
	public static final Color SEARCHING_COLOUR = Color.ORANGE;


	private World world;
	private RenderComponent render;
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
		render = new RenderComponent(new ModelController("agent", world.getModelManager(), true));
		add(render);

		// physics
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.linearDamping = 1f;
		physics = new PhysicsComponent(world.getPhysicsWorld(), bodyDef, tilePos, radius);
		physics.body.setUserData(this);
		add(physics);

		// ai
		ai = new AIComponent(new BehaviourNop(this));
		add(ai);

		world.getEngine().addEntity(this);
	}

	/**
	 * @return The entity's current {@link Behaviour}
	 * This is never null, but will be {@link BehaviourNop} when there is no current behaviour
	 */
	public Behaviour getBehaviour() {
		return ai.behaviour;
	}

	/**
	 * Sets the behaviour of this current agent
	 *
	 * @param behaviour the behaviour that this agent should follow
	 */
	public void setBehaviour(Behaviour behaviour) {
		ai.behaviour = behaviour;
		Float radius = Config.getFloat(ConfigKey.ENTITY_DIAMETER) / 2f;
		if (behaviour.getType() == BehaviourType.FOLLOW_PATH) {
			render.dotColour = SEARCHING_COLOUR;
			render.radius = radius;
		} else {
			render.dotColour = FLOCKING_COLOUR;
			render.radius = radius * 0.6f;
		}
	}

	/**
	 * Gets the world that this agent is in
	 *
	 * @return the world of type {@link World} that this agent it in
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Gets the {@link PhysicsComponent} that this agents is using
	 *
	 * @return the {@link PhysicsComponent} that this agent is following
	 */
	public PhysicsComponent getPhysicsComponent() {
		return physics;
	}

	public float getRadius() {
		return render.radius;
	}

	public void setRadius(float radius) {
		render.radius = radius;
	}
}
