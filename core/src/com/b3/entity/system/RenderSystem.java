package com.b3.entity.system;

import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.component.RenderComponent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Entity system to render entities as simple coloured shapes
 */
public class RenderSystem extends IteratingSystem {

	private static final int CIRCLE_SEGMENTS = 12;

	private ComponentMapper<PhysicsComponent> physics;
	private ComponentMapper<RenderComponent> models;

	private ShapeRenderer shapeRenderer;
	private PerspectiveCamera camera;

	private boolean modelRendering;

	/**
	 * Creates a new render system to place dots on screen (to represent people)
	 * @param camera the camera that the dots will be laid in front of
     */
	public RenderSystem(PerspectiveCamera camera) {
		super(Family.all(RenderComponent.class, PhysicsComponent.class).get());
		this.camera = camera;
		this.physics = ComponentMapper.getFor(PhysicsComponent.class);
		this.models = ComponentMapper.getFor(RenderComponent.class);
		this.shapeRenderer = new ShapeRenderer();
	}

	/**
	 * Begin the processing of the models
	 * Set up the shape rendering to use the camera in the world
	 */
	@Override
	public void beginProcessing() {
		modelRendering = Config.getBoolean(ConfigKey.RENDER_AGENT_MODELS);

		if (!modelRendering) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		}
	}

	/**
	 * Ends the shape rendering, no rendering can occur after this
	 */
	@Override
	public void endProcessing() {
		if (!modelRendering)
			shapeRenderer.end();
	}

	/**
	 * For each entity in the world, this will see where they are and make them wander around, taking into account nearby buildings and other entities
	 * @param entity    The current Entity being processed
	 * @param deltaTime The delta time between the last and current frame
     */
	public void processEntity(Entity entity, float deltaTime) {
		RenderComponent render = models.get(entity);

		PhysicsComponent phys = physics.get(entity);
		Vector2 pos = phys.getPosition();
		float degrees = angle(phys.lastPosition, pos);
		phys.lastPosition.set(pos);

		if (modelRendering)
			render.controller.setVisible(true).setPositionAndRotation(pos.x, pos.y, 0f, degrees);
		else {
			render.controller.setVisible(false);
			shapeRenderer.identity();
			shapeRenderer.translate(pos.x, pos.y, 0f);
			shapeRenderer.rotate(0, 0, 1, degrees + 45f);
			shapeRenderer.setColor(render.dotColour);
			shapeRenderer.circle(0, 0, render.radius, CIRCLE_SEGMENTS);
		}

	}

	/**
	 * The angle in degrees between the two input vector v1 and v2
	 * @param v1 the vector to be processed
	 * @param v2 the vector to be processed
     * @return the angle in degrees between the two inputs
     */
	private float angle(Vector2 v1, Vector2 v2) {
		double xdiff = v1.x - v2.x,
				ydiff = v2.y - v1.y;
		return (float) Math.toDegrees(Math.atan2(ydiff, xdiff));
	}

}
