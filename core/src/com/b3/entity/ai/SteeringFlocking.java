package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

public abstract class SteeringFlocking extends Steering {

	private static Vector2 tmpVector = new Vector2();
	private static final double DISTANCE_SQRD = Math.pow(10, 2);

	private ImmutableArray<Entity> entities;

	public SteeringFlocking(PhysicsComponent entity) {
		super(entity);
	}

	@Override
	public void tick(Vector2 steeringOut) {
		steeringOut.setZero();
		int count = 0;


		for (Entity e : entities) {
			PhysicsComponent phys = e.getComponent(PhysicsComponent.class);
			if (phys == null || phys == entity)
				continue;

			tmpVector.set(phys.getPosition());
			if (tmpVector.sub(entity.getPosition()).len2() < DISTANCE_SQRD) {
				processNeighbour(steeringOut, tmpVector, phys);
				count += 1;
			}


		}


		if (count != 0)
			finaliseOutput(steeringOut.scl(1f / count));
	}


	protected abstract void processNeighbour(Vector2 steeringOut, Vector2 neighbourPosition, PhysicsComponent neighbour);

	protected abstract void finaliseOutput(Vector2 steeringOut);

	public void setEntities(ImmutableArray<Entity> entities) {
		this.entities = entities;
	}
}
