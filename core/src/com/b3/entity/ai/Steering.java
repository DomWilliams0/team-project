package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * The target of a steering behaviour
 */
interface SteeringTarget {
	/**
	 * @return A copy of the target's position
	 */
	Vector2 getPosition();
}

/**
 * A general steering behaviour that is owned by an entity
 */
abstract class Steering {
	protected PhysicsComponent entity;

	public Steering(PhysicsComponent entity) {
		this.entity = entity;
	}

	/**
	 * Called once per game update
	 *
	 * @param steeringOut The desired agent steering to apply
	 */
	public abstract void tick(Vector2 steeringOut);

	public PhysicsComponent getEntity() {
		return entity;
	}

	public void setEntity(PhysicsComponent entity) {
		this.entity = entity;
	}
}

/**
 * A steering behaviour that has a target
 */
abstract class SteeringWithTarget extends Steering {
	protected SteeringTarget target;

	public SteeringWithTarget(PhysicsComponent owner, SteeringTarget target) {
		super(owner);
		this.target = target;
	}

	public SteeringTarget getTarget() {
		return target;
	}

	public void setTarget(SteeringTarget target) {
		this.target = target;
	}

	/**
	 * @return The squared distance to the target's position
	 */
	public double getDistanceSqrd() {
		return entity.getPosition().sub(target.getPosition()).len2();
	}
}

/**
 * A steering target that is a moving entity in the world
 */
class EntitySteeringTarget implements SteeringTarget {

	private PhysicsComponent physicsComponent;

	EntitySteeringTarget(PhysicsComponent physicsComponent) {
		this.physicsComponent = physicsComponent;
	}

	@Override
	public Vector2 getPosition() {
		return new Vector2(physicsComponent.getPosition());
	}
}

/**
 * A steering target that is a static tile in the world
 */
class TileSteeringTarget implements SteeringTarget {
	private Vector2 position;

	TileSteeringTarget(Vector2 position) {
		this.position = position;
	}

	@Override
	public Vector2 getPosition() {
		return new Vector2(position);
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}
}
