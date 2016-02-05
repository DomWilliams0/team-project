package com.b3;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class DebugRenderer {
	private SpriteBatch spriteBatch;
	private Box2DDebugRenderer renderer;
	private World world;

	public DebugRenderer(World world) {
		this.world = world;
		spriteBatch = new SpriteBatch();
		// todo box2d scale
		renderer = new Box2DDebugRenderer();
	}

	public void render(PerspectiveCamera camera) {
		spriteBatch.begin();
		renderer.render(world, camera.combined);
		spriteBatch.end();
	}
}
