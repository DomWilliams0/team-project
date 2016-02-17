package com.b3.world;

import com.b3.event.EventType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class WorldQueryServiceTest {

    @Mock private World world;
    private WorldQueryService worldQueryService;

    private Building newBuilding(BuildingType buildingType) {

        Building building = mock(Building.class, RETURNS_DEEP_STUBS);

        when(building.getType()).thenCallRealMethod();
        when(building.getEvent()).thenCallRealMethod();

        doCallRealMethod().when(building).setType(any(BuildingType.class));
        doCallRealMethod().when(building).setEvent(any(EventType.class), eq(false));


        building.setType(buildingType);

        return building;

    }

    // TODO: Generalise it
    private ArrayList<Building> buildingsProvider() {

        ArrayList<Building> buildings = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            if (i <= 50)
                buildings.add(newBuilding(BuildingType.HOUSE));
            else if (i > 50 && i <= 60)
                buildings.add(newBuilding(BuildingType.FIRE_STATION));
            else if (i > 60 && i <= 80)
                buildings.add(newBuilding(BuildingType.POLICE_STATION));
            else
                buildings.add(newBuilding(BuildingType.RESTAURANT));
        }

        return buildings;

    }

    private List<Building> freeBuildings() {

        return world.getBuildings()
                .stream()
                .filter(building -> building.getEvent() == null && building.getType() == BuildingType.HOUSE)
                .collect(Collectors.toList());

    }

    @Before
    public void setUp() {

        world = mock(World.class);

        ArrayList<Building> buildings = buildingsProvider();
        for (Building building : buildings) {
            world.addBuilding(building);
        }

        when(world.getBuildings()).thenReturn(buildings);
        worldQueryService = new WorldQueryService(world);

    }

    @Test
    public void testGetAllBuildings() {

        ArrayList<Building> expectedBuildings = buildingsProvider();
        List<Building> actualBuildings = worldQueryService.getAllBuildings();

        assertEquals("Number of buildings", expectedBuildings.size(), actualBuildings.size());
        assertArrayEquals(
                "Gets all the buildings",
                expectedBuildings.stream().map(Building::getType).collect(Collectors.toList()).toArray(),
                actualBuildings.stream().map(Building::getType).collect(Collectors.toList()).toArray()
        );

    }

    @Test
    public void testGetFreeBuildings() {

        // Before setting events
        List<Building> expectedBuildings = freeBuildings();
        List<Building> actualBuildings = worldQueryService.getFreeBuildings();

        assertEquals("Number of free buildings", expectedBuildings.size(), actualBuildings.size());
        assertArrayEquals(
                "Gets free buildings",
                expectedBuildings.stream().map(Building::getType).collect(Collectors.toList()).toArray(),
                actualBuildings.stream().map(Building::getType).collect(Collectors.toList()).toArray()
        );

        // After setting events
        List<Building> buildings = worldQueryService.getAllBuildings();
        for (int i = 0; i < 20; i++) {
            buildings.get(i).setEvent(EventType.FIRE, false);
        }

        for (Building building : buildings) {
            world.addBuilding(building);
        }

        when(world.getBuildings()).thenReturn(buildings);

        assertEquals("Number of free buildings", 30, worldQueryService.getFreeBuildings().size());

    }

    @Test
    public void testGetBuildingsByType() {

        List<Building> actualHouses         = worldQueryService.getBuildingsByType(BuildingType.HOUSE);
        List<Building> actualFireStations   = worldQueryService.getBuildingsByType(BuildingType.FIRE_STATION);
        List<Building> actualPoliceStations = worldQueryService.getBuildingsByType(BuildingType.POLICE_STATION);
        List<Building> actualRestaurants    = worldQueryService.getBuildingsByType(BuildingType.RESTAURANT);

        assertEquals("Number of houses",            50, actualHouses.size());
        assertEquals("Number of fire stations",     10, actualFireStations.size());
        assertEquals("Number of police stations",   20, actualPoliceStations.size());
        assertEquals("Number of restaurants",       20, actualRestaurants.size());

    }

}
