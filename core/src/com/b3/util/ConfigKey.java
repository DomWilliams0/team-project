package com.b3.util;

/**
 * All the different setting config keys.
 *
 * @author dxw405
 */
public enum ConfigKey {

	/**
	 * Whether the camera should be restricted.
	 */
	CAMERA_RESTRICT("camera-restrict"),
	/**
	 * The speed in which the camera will zoom in and out when the
	 * appropriate buttons are pressed.
	 */
	CAMERA_ZOOM_SPEED("camera-zoom-speed"),
	/**
	 * The speed in which the camera will zoom in and out when the
	 * appropriate buttons are pressed.
	 */
	CAMERA_MOVE_SPEED("camera-move-speed"),

	/**
	 * The maximum distance the camera is able to zoom in.
	 */
	CAMERA_DISTANCE_MINIMUM("camera-distance-min"),
	/**
	 * The maximum distance the camera is able to zoom out.
	 */
	CAMERA_DISTANCE_MAXIMUM("camera-distance-max"),

	/**
	 * The distance between the world's edge and the area in which entities should be teleported.
	 */
	ENTITY_CULL_DISTANCE("entity-kill-distance"),
	/**
	 * The diameter to render entities as.
	 */
	ENTITY_DIAMETER("entity-diameter"),
	/**
	 * The number of entities to spawn.
	 */
	ENTITY_SPAWN_COUNT("entity-spawn-count"),

	/**
	 * Whether the building should have a collision box.
	 */
	BUILDING_COLLISIONS("building-collisions"),
	/**
	 * Whether to show collision boxes on the entities.
	 */
	PHYSICS_RENDERING("physics-rendering"),

	/**
	 * Whether to show the landscape/terrain extension.
	 */
	LANDSCAPE_RENDERING("landscape-rendering"),

	/**
	 * Whether to show the grid lines on the world.
	 */
	SHOW_GRID("show-grid"),
	/**
	 * Whether to show the path the agents are taking.
	 */
	SHOW_PATHS("show-paths"),
	/**
	 * Whether to show the buildings or their flattened equivalent.
	 */
	FLATTEN_BUILDINGS("flatten-buildings"),
	/**
	 * Whether to show the agents with models or as circles.
	 */
	RENDER_AGENT_MODELS("render-agent-models"),
	/**
	 * Whether to show the static models in the world.
	 * E.g. streetlight and trees.
	 */
	RENDER_STATIC_MODELS("render-static-models"),
	/**
	 * Whether random flocking agents will be created.
	 */
	FLOCKING_ENABLED("flocking-enabled"),
	/**
	 * Whether the "add building" button has been pressed.
	 */
	ADD_BUILDING_MODE("add-building-mode"),
	/**
	 * Whether the "remove building" button has been pressed.
	 */
	REMOVE_BUILDING_MODE("remove-building-mode"),
	/**
	 * The movement speed of the agents in the game.
	 */
	GAME_SPEED("game-speed"),
	/**
	 * Whether sounds are enabled.
	 */
	SOUNDS_ON("sound-on"),

	/**
	 * The speed of the search.
	 */
	TIME_BETWEEN_TICKS("time-between-ticks"),
	/**
	 * The slowest the search can go.
	 * Used by the search speed slider.
	 */
	TIME_BETWEEN_TICKS_MIN("time-between-ticks-min"),
	/**
	 * The quickest the search can go.
	 * Used by the search speed slider.
	 */
	TIME_BETWEEN_TICKS_MAX("time-between-ticks-max"),
	/**
	 * The default search speed.
	 */
	TIME_BETWEEN_TICKS_STEP("time-between-ticks-step"),

	/**
	 * The texture to use for the sidebars and message boxes.
	 */
	TEXTURE_ATLAS("texture-atlas"),
	/**
	 * The font the application will use for displaying text.
	 */
	FONT_FILE("font-file");

	private final String key;

	ConfigKey(String key) {
		this.key = key;
	}

	/**
	 * @return The text of the key in the YAML file.
	 */
	public String getKey() {
		return key;
	}

}
