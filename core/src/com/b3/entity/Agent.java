package com.b3.entity;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourWander;
import com.b3.entity.component.AIComponent;
import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.component.RenderComponent;
import com.b3.util.Config;
import com.b3.world.World;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class Agent extends Entity {

	private World world;
	private PhysicsComponent physics;
	private AIComponent ai;

	public Agent(World world, Vector2 tilePos) {
		// bounds check
		if (!world.isInBounds(tilePos))
			throw new IllegalArgumentException("Tile position is out of range: " + tilePos);

		this.world = world;

		float diameter = Config.getFloat("debug-entity-diameter");
		float radius = diameter / 2f;

		// render
		add(new RenderComponent(Color.BLUE, radius));

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