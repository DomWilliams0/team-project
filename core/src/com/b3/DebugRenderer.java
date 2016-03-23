package com.b3;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Debugs the renderer, used for rendering the {@link #world},
 * {@link #spriteBatch} and the {@link #renderer}.
 *
 * @author dxw405
 */
public class DebugRenderer {

	private final SpriteBatch spriteBatch;
	private final Box2DDebugRenderer renderer;
	private final World world;

	/**
	 * Creates a new debug renderer using the current {@link World}.
	 *
	 * @param world The {@link World} that this debug renderer is using.
	 */
	public DebugRenderer(World world) {
		this.world = world;
		spriteBatch = new SpriteBatch();
		renderer = new Box2DDebugRenderer();
	}

	/**
	 * Renders the {@link World} using the current {@link PerspectiveCamera}.
	 *
	 * @param camera The {@link PerspectiveCamera} used to render the
	 *               {@link #world} using the {@link #spriteBatch}.
	 */
	public void render(PerspectiveCamera camera) {
		spriteBatch.begin();
		renderer.render(world, camera.combined);
		spriteBatch.end();
	}
}
