package com.b3.entity;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.WanderBehaviour;
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

	private Behaviour behaviour;

	public Agent(World world, Vector2 tilePos, Behaviour behaviour) {
		// bounds check
		if (!world.isInBounds(tilePos))
			throw new IllegalArgumentException("Tile position is out of range: " + tilePos);

		this.world = world;

		float diameter = Config.getFloat("debug-entity-diameter");
		float radius = diameter / 2f;

		add(new RenderComponent(Color.BLUE, radius));

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.linearDamping = 0.9f;
		physics = new PhysicsComponent(world.getPhysicsWorld(), bodyDef, tilePos, radius);
		physics.getBody().setUserData(this);
		add(physics);

		// debug stupid wander behaviour

		world.getEngine().addEntity(this);

		setBehaviour(behaviour);
	}

	public Agent(World world, Vector2 tilePos) {
		this(world, tilePos, new WanderBehaviour());
	}

	public Behaviour getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Behaviour behaviour) {
		// todo "disable" old behaviour necessary?

		this.behaviour = behaviour;
		behaviour.begin(this);
	}

	public PhysicsComponent getPhysicsComponent() {
		return physics;
	}
}