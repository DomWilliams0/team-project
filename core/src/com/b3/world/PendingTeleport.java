package com.b3.world;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * A queued message to teleport an entity who is at the edge of the world to the other side
 *
 * @author dxw405
 */
public class PendingTeleport {

	public final Body body;
	public final TeleportType teleportType;

	public PendingTeleport(Body body, TeleportType teleportType) {
		this.body = body;
		this.teleportType = teleportType;
	}

	/**
	 * Where to teleport to.
	 */
	public enum TeleportType {
		TO_LEFT,
		TO_TOP,
		TO_RIGHT,
		TO_BOTTOM
	}

}
