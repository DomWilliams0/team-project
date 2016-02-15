package com.b3.util;

import com.b3.world.World;

public class Settings {
    public static boolean SHOW_GRID = true;
    public static boolean FLAT_BUILDINGS = false;

    private World world;

    public Settings(World world) {
        this.world = world;
    }

    public void toggleGrid() {
        SHOW_GRID = !SHOW_GRID;
    }

    public void toggleFlatBuildings() {
        FLAT_BUILDINGS = !FLAT_BUILDINGS;
        world.flattenBuildings(FLAT_BUILDINGS);
    }
}
