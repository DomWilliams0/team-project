package com.b3.entity.system;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsSystem extends IteratingSystem {
	private ComponentMapper<PhysicsComponent> physics;
	private World world;

	public PhysicsSystem(World world) {
		super(Family.all(PhysicsComponent.class).get());
		this.world = world;
		this.physics = ComponentMapper.getFor(PhysicsComponent.class);
	}

	@Override
	public void beginProcessing() {
		world.step(Gdx.graphics.getRawDeltaTime(), 6, 4);
	}

	public void processEntity(Entity entity, float deltaTime) {
		Body body = physics.get(entity).getBody();

		Vector2 vel = body.getLinearVelocity();
		vel.scl(0.97f);

		body.setLinearVelocity(vel);
	}
}
