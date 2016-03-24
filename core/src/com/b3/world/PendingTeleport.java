package com.b3.world;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * A queued message to teleport an entity who is at the edge of the world to the other side
 *
 * @author dxw405
 */
class PendingTeleport {

	final Body body;
	final TeleportType teleportType;

	/**
	 * @param body         The {@link Body} to teleport
	 * @param teleportType Where to teleport this body to
	 */
	PendingTeleport(Body body, TeleportType teleportType) {
		this.body = body;
		this.teleportType = teleportType;
	}

	/**
	 * Where to teleport to.
	 */
	enum TeleportType {
		TO_LEFT,
		TO_TOP,
		TO_RIGHT,
		TO_BOTTOM
	}

}
