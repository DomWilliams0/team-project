package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dxw405
 */
public abstract class SteeringFlocking extends Steering {

	private List<PhysicsComponent> neighbours;

	public SteeringFlocking(PhysicsComponent entity) {
		super(entity);
		this.neighbours = new ArrayList<>();
	}

	@Override
	public void tick(Vector2 steeringOut) {
		steeringOut.setZero();

		for (PhysicsComponent neighbour : neighbours)
			processNeighbour(steeringOut, neighbour.getPosition(), neighbour);

		if (!neighbours.isEmpty())
			finaliseOutput(steeringOut.scl(1f / neighbours.size()));
	}


	protected abstract void processNeighbour(Vector2 steeringOut, Vector2 neighbourPosition, PhysicsComponent neighbour);

	protected abstract void finaliseOutput(Vector2 steeringOut);

	public void setNeighbours(List<PhysicsComponent> neighbours) {
		this.neighbours = neighbours;
	}
}
