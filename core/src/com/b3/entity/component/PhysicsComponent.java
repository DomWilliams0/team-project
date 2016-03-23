package com.b3.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * An agent's body, which interacts with buildings and other agents through the power of love
 *
 * @author dxw405
 */
public class PhysicsComponent implements Component {

	/**
	 * The physique of the agent.
	 */
	public final Body body;

	/**
	 * The previous position of the agent.
	 */
	public final Vector2 lastPosition;

	/**
	 * The maximum speed the agent may have.
	 */
	public final float maxSpeed;

	/**
	 * The maximum acceleration of the agent.
	 */
	public final float maxAcceleration;

	/**
	 * @param world   The world the agent is in.
	 * @param bodyDef The description of the agent's physique.
	 * @param tilePos The tile to spawn the agent on.
	 * @param radius  The size of the agent's dot.
	 */
	public PhysicsComponent(World world, BodyDef bodyDef, Vector2 tilePos, float radius) {
		bodyDef.position.set(tilePos.x + 0.5f, tilePos.y + 0.5f);
		body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius);
		fixtureDef.shape = circleShape;
		body.createFixture(fixtureDef);
		circleShape.dispose();

		lastPosition = new Vector2();

		maxSpeed = 5f;
		maxAcceleration = 20;

	}

	/**
	 * @return The position the agent it at.
	 */
	public Vector2 getPosition() {
		return body.getPosition();
	}

}
