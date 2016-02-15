package com.b3.world;

import com.b3.event.EventMessage;
import com.b3.event.EventType;
import com.b3.searching.Node;
import com.b3.searching.Point;
import com.b3.searching.WorldGraph;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorldObserver implements Observer {

    private World world;
    public WorldObserver(World world) {
        this.world = world;
    }

    private BuildingType getBuildingTypeFromEvent(EventType eventType) {
        switch (eventType) {
            case FIRE:      return BuildingType.FIRE_STATION;
            case ROBBERY:   return BuildingType.POLICE_STATION;
            case DELIVERY:  return BuildingType.RESTAURANT;
        }

        return BuildingType.HOUSE;
    }

    @Override
    public void update(Observable o, Object arg) {
        EventMessage evt = (EventMessage)arg;
        EventType eventType = evt.getEventType();

        // Get target and source buildings
        Building targetBuilding = (Building)evt.getMessage();
        targetBuilding.setEvent(eventType);
        Building sourceBuilding = world.getQueryService().getRandomBuildingByType(getBuildingTypeFromEvent(eventType));

        Vector2 src = sourceBuilding.getTilePosition();
        Point p1 = new Point((int)src.x - 1, (int)src.y);

        Vector2 trgt = targetBuilding.getTilePosition();
        Point p2 = new Point((int)trgt.x - 1, (int)trgt.y);

        System.out.println(p1);
        System.out.println(p2);

        // Perform search
        WorldGraph wg = world.getWorldGraph();
        Optional<List<Node<Point>>> optPath = wg.findPathBFS(p1, p2);

        if (!optPath.isPresent()) {
            System.out.println("Nooooo");
            return;
        }

        List<Node<Point>> path = optPath.get();
        List<Vector2> points = path.stream().map(pointNode -> new Vector2(pointNode.getContent().getX(), pointNode.getContent().getY())).collect(Collectors.toList());

        world.spawnAgentWithPath(points.get(0), points);
    }
}
