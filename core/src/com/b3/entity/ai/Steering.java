package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * The target of a steering behaviour
 *
 * @author dxw405
 */
interface SteeringTarget {
	/**
	 * @return A copy of the target's position
	 */
	Vector2 getPosition();
}

/**
 * A general steering behaviour that is owned by an entity
 *
 * @author dxw405
 */
abstract class Steering {
	protected PhysicsComponent entity;

	/**
	 * @param entity The entity that owns this {@link Steering}
	 */
	public Steering(PhysicsComponent entity) {
		this.entity = entity;
	}

	/**
	 * Called once per game update
	 *
	 * @param steeringOut The desired agent steering to apply
	 */
	public abstract void tick(Vector2 steeringOut);

	/**
	 * @return The entity that owns this {@link Steering}
	 */
	public PhysicsComponent getEntity() {
		return entity;
	}

	/**
	 * @param entity The new entity to own this {@link Steering}
	 */
	public void setEntity(PhysicsComponent entity) {
		this.entity = entity;
	}
}

/**
 * A steering behaviour that has a target
 *
 * @author dxw405
 */
abstract class SteeringWithTarget extends Steering {
	protected SteeringTarget target;

	public SteeringWithTarget(PhysicsComponent owner, SteeringTarget target) {
		super(owner);
		this.target = target;
	}

	/**
	 * @return The target
	 */
	public SteeringTarget getTarget() {
		return target;
	}

	/**
	 * @param target The new target
	 */
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
 *
 * @author dxw405
 */
class EntitySteeringTarget implements SteeringTarget {

	private PhysicsComponent targetEntity;

	/**
	 * @param entity The entity that is the target
	 */
	EntitySteeringTarget(PhysicsComponent entity) {
		this.targetEntity = entity;
	}

	/**
	 * @return A copy of the target's position
	 */
	@Override
	public Vector2 getPosition() {
		return new Vector2(targetEntity.getPosition());
	}
}

/**
 * A steering target that is a static tile in the world
 *
 * @author dxw405
 */
class TileSteeringTarget implements SteeringTarget {
	private Vector2 position;

	/**
	 * @param position The target tile
	 */
	TileSteeringTarget(Vector2 position) {
		this.position = position;
	}

	/**
	 * @return A copy of the target's position
	 */
	@Override
	public Vector2 getPosition() {
		return new Vector2(position);
	}

	/**
	 * @param position The new target position
	 */
	public void setPosition(Vector2 position) {
		this.position = position;
	}
}
