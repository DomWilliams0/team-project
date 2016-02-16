package com.b3.util;

public enum ConfigKey {
	//todo remove debug prefix

	CAMERA_RESTRICT("camera-restrict"),
	CAMERA_ZOOM_SPEED("camera-zoom-speed"),
	CAMERA_MOVE_SPEED("camera-move-speed"),

	CAMERA_DISTANCE_DEFAULT("camera-distance-default"),
	CAMERA_DISTANCE_MINIMUM("camera-distance-min"),
	CAMERA_DISTANCE_MAXIMUM("camera-distance-max"),

	ENTITY_CULL_DISTANCE("entity-kill-distance"),
	ENTITY_DIAMETER("entity-diameter"),
	ENTITY_SPAWN_COUNT("entity-spawn-count"),

	BUILDING_COLLISIONS("building-collisions"),
	PHYSICS_RENDERING("physics-rendering"),

	SHOW_GRID("show-grid"),
	SHOW_PATHS("show-paths"),
	FLATTEN_BUILDINGS("flatten-buildings"),
	STEPS_PER_TICK("steps-per-tick"),

	TIME_BETWEEN_TICKS("time-between-ticks"),
	TIME_BETWEEN_TICKS_MIN("time-between-ticks-min"),
	TIME_BETWEEN_TICKS_MAX("time-between-ticks-max"),
	TIME_BETWEEN_TICKS_STEP("time-between-ticks-step");

	private final String key;

	ConfigKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
