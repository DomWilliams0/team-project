package com.b3.util;

public enum ConfigKey {
	//todo remove debug prefix

	CAMERA_RESTRICT("camera-restrict"),
	CAMERA_ZOOM_SPEED("camera-zoom-speed"),
	CAMERA_MOVE_SPEED("camera-move-speed"),

	CAMERA_DISTANCE_DEFAULT("camera-distance-default"),
	CAMERA_DISTANCE_MINIMUM("camera-distance-min"),
	CAMERA_DISTANCE_MAXIMUM("camera-distance-max"),

	ENTITY_CULL_DISTANCE("debug-entity-kill-distance"),
	ENTITY_DIAMETER("debug-entity-diameter"),
	ENTITY_SPAWN_COUNT("debug-entity-count"),

	BUILDING_COLLISIONS("building-collisions"),
	PHYSICS_RENDERING("debug-physics-rendering"),

	SHOW_GRID("show_grid"),
	SHOW_PATHS("show_paths"),
	FLATTEN_BUILDINGS("flatten_buildings"),
	STEPS_PER_TICK("steps_per_tick");

	private final String key;

	ConfigKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
