package com.b3.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


/**
 * An agent's body, which interacts with buildings and other agents
 */
public class PhysicsComponent implements Component {

	private Body body;

	private float maxSpeed;
	private float maxAcceleration;

	public PhysicsComponent(World world, BodyDef bodyDef, Vector2 tilePos, float radius) {
		bodyDef.position.set(tilePos.x + 0.5f, tilePos.y + 0.5f);
		body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius);
		fixtureDef.shape = circleShape;
		body.createFixture(fixtureDef);
		circleShape.dispose();

		maxSpeed = 5f;
		maxAcceleration = 20; // todo what does this even do? probably not necessary
	}


	public Body getBody() {
		return body;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public float getMaxAcceleration() {
		return maxAcceleration;
	}


}
