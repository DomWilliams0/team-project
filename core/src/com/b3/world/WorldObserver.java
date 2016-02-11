package com.b3.world;

import com.b3.event.EventMessage;
import com.b3.event.EventType;
import com.b3.searching.WorldGraph;
import com.b3.searching.roboticsGraphHelpers.Graph;
import com.b3.searching.roboticsGraphHelpers.Node;
import com.b3.searching.roboticsGraphHelpers.Point;
import com.b3.searching.roboticsGraphHelpers.collectFuncMaybe.Maybe;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
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
        Graph<Point> g = wg.getGraphNicksStyle();
        Maybe<List<Node<Point>>> maybePath = g.findPathBFS(p1, p2);

        if (maybePath.isNothing()) {
            System.out.println("Nooooo");
            return;
        }

        List<Node<Point>> path = maybePath.fromMaybe();
        List<Vector2> points = path.stream().map(pointNode -> new Vector2(pointNode.getContent().getX(), pointNode.getContent().getY())).collect(Collectors.toList());
        Array<Vector2> vPoints = new Array<>();

        for (Vector2 v : points) {
            vPoints.add(v);
        }

        world.spawnAgentWithPath(true, vPoints);
    }
}
