package com.b3.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class PhysicsComponent implements Component, Steerable<Vector2> {

	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());
	private final World world;
	private final BodyDef bodyDef;

	public Body body;

	public float boundingRadius;
	public boolean tagged;
	public float zeroThreshold;

	public float maxLinearSpeed;
	public float maxLinearAcceleration;
	public float maxAngularSpeed;
	public float maxAngularAcceleration;

	public boolean independentFacing;
	public SteeringBehavior<Vector2> steeringBehavior;


	public PhysicsComponent(World world, BodyDef bodyDef, Vector2 tilePos, float radius) {
		bodyDef.position.set(tilePos);
		body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius);
		fixtureDef.shape = circleShape;
		body.createFixture(fixtureDef);

		maxLinearSpeed = 100;
		maxLinearAcceleration = 100;
		maxAngularSpeed = 100;
		maxAngularAcceleration = 100;
		boundingRadius = radius; // todo needs conversion?
		tagged = false;
		zeroThreshold = 0.001f;

		// for cloning
		this.world = world;
		this.bodyDef = bodyDef;
	}

	public boolean isIndependentFacing() {
		return independentFacing;
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return MathUtils.degRad * vector.angle();
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return new Vector2(1, 0).rotate(MathUtils.radDeg * angle);
	}

	public SteeringBehavior<Vector2> getSteeringBehavior() {
		return steeringBehavior;
	}

	public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
		this.steeringBehavior = steeringBehavior;
	}

	public void update(float deltaTime) {
		if (steeringBehavior != null) {
			// Calculate steering acceleration
			steeringBehavior.calculateSteering(steeringOutput);

			/*
			 * Here you might want to add a motor control layer filtering steering accelerations.
			 *
			 * For instance, a car in a driving game has physical constraints on its movement: it cannot turn while stationary; the
			 * faster it moves, the slower it can turn (without going into a skid); it can brake much more quickly than it can
			 * accelerate; and it only moves in the direction it is facing (ignoring power slides).
			 */

			// Apply steering acceleration
			applySteering(deltaTime);
		}
	}

	protected void applySteering(float deltaTime) {
		boolean anyAccelerations = false;

		// Update position and linear velocity.
		if (!steeringOutput.linear.isZero()) {
			Vector2 force = steeringOutput.linear.scl(deltaTime);
			body.applyForceToCenter(force, true);
			anyAccelerations = true;
		}

		// Update orientation and angular velocity
		if (isIndependentFacing()) {
			if (steeringOutput.angular != 0) {
				body.applyTorque(steeringOutput.angular * deltaTime, true);
				anyAccelerations = true;
			}
		} else {
			// If we haven't got any velocity, then we can do nothing.
			Vector2 linVel = getLinearVelocity();
			if (!linVel.isZero(0.001f)) {
				float newOrientation = vectorToAngle(linVel);
				body.setAngularVelocity((newOrientation - getAngularVelocity()) * deltaTime); // this is superfluous if independentFacing is always true
				body.setTransform(body.getPosition(), newOrientation);
			}
		}

		if (anyAccelerations) {
			// body.activate();

			// Cap the linear speed
			Vector2 velocity = body.getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			float maxLinearSpeed = getMaxLinearSpeed();
			if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
				body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
			}

			// Cap the angular speed
			float maxAngVelocity = getMaxAngularSpeed();
			if (body.getAngularVelocity() > maxAngVelocity) {
				body.setAngularVelocity(maxAngVelocity);
			}
		}
	}

	//
	// Limiter implementation
	//

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		return zeroThreshold;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		this.zeroThreshold = value;
	}

	@Override
	public void setOrientation(float orientation) {
		body.setTransform(body.getPosition(), orientation);
	}

	@Override
	public Location<Vector2> newLocation() {
		return new PhysicsComponent(world, bodyDef, new Vector2(0, 0), boundingRadius);
	}
}
