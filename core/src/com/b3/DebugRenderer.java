package com.b3;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Debugs the renderer, used for rendering the world, spriteBatch and the Box2Drenderer
 */
public class DebugRenderer {
	private final SpriteBatch spriteBatch;
	private final Box2DDebugRenderer renderer;
	private final World world;

	/**
	 * Creates a new debug renderer using the current world
	 * @param world the world that this debug renderer is using
     */
	public DebugRenderer(World world) {
		this.world = world;
		spriteBatch = new SpriteBatch();
		// todo box2d scale
		renderer = new Box2DDebugRenderer();
	}

	/**
	 * Renders the world using the current camera
	 * @param camera the PerspectiveCamera used to render the world using the spriteBatch
     */
	public void render(PerspectiveCamera camera) {
		spriteBatch.begin();
		renderer.render(world, camera.combined);
		spriteBatch.end();
	}
}
