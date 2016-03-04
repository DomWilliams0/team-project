package com.b3.input;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourMultiPathFind;
import com.b3.gui.components.MessageBoxComponent;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.takeable.Takeable;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


enum Stage {
    CURRENT_NODE_SELECTION,
    ADD_TO_FRONTIER_SELECTION,
    ADD_TO_EXPLORED_SELECTION
}

public class PracticeModeWorldSelectionHandler extends InputAdapter {
    private static final Vector3 tempRayCast = new Vector3();
    private World world;
    private Stage currentStage;
    private Point currentSelection;
    private MessageBoxComponent descriptionPopup;
    private boolean firstTime;

    public PracticeModeWorldSelectionHandler(World world, com.badlogic.gdx.scenes.scene2d.Stage popupStage) {
        this.world = world;
        this.currentStage = Stage.CURRENT_NODE_SELECTION;
        this.currentSelection = new Point(1,1);
        this.descriptionPopup = new MessageBoxComponent(popupStage, "", "OK");
        this.firstTime = true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // selecting an entity
        // todo

        //if near bottom of screen hitting the intensive learning mode button so ignore
        if (screenX < 100 && screenY > Gdx.graphics.getHeight()-100) {
            return false;
        }

        // selecting a tile
        WorldCamera worldCamera = world.getWorldCamera();
        Ray ray = worldCamera.getPickRay(screenX, screenY);
        ray.getEndPoint(tempRayCast, worldCamera.position.z);

        // convert to node
        WorldGraph worldGraph = world.getWorldGraph();
        Node node = worldGraph.getNode(new Point((int) tempRayCast.x, (int) tempRayCast.y));

        if (currentSelection.x == (int) tempRayCast.x && currentSelection.y == (int) tempRayCast.y) {
            //old node so change page number
            if (world.getrt().getPopupShowing())
                //if popup showing
                world.getrt().resetCounterAnimation();
            world.getrt().flipPageRight();
        } else {
            //new node so reset page number
            if (world.getrt().getPopupShowing())
                //if popup showing
                world.getrt().resetPage();
        }

        currentSelection = new Point((int) tempRayCast.x, (int) tempRayCast.y);

        if (node == null)
            return false;

        if (button == Input.Buttons.LEFT) {
            SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
            Takeable<Node> frontier = currentSearch.getFrontier();
            Set<Node> visited = currentSearch.getVisited();

            switch (currentStage) {
                case CURRENT_NODE_SELECTION:
                    Node actualNode = frontier.peek();
                    if (!actualNode.equals(node)) {
                        descriptionPopup.setText("Attention! This node is not the one to be selected for expansion.");
                        descriptionPopup.show();
                    }
                    else {
                        frontier.take();
                        currentSearch.setMostRecentlyExpanded(node);
                        currentStage = Stage.ADD_TO_FRONTIER_SELECTION;

                        if (currentSearch.getEnd().equals(node)) {
                            descriptionPopup.setText("Great! You reached the target");
                            descriptionPopup.show();
                        }
                        else if (firstTime) {
                            descriptionPopup.setText("Good! Now please select the nodes to add to the frontier.");
                            descriptionPopup.show();
                        }
                    }
                    break;

                case ADD_TO_FRONTIER_SELECTION:
                    List<Node> actualFrontier = currentSearch.getMostRecentlyExpanded().getNeighbours()
                            .stream()
                            .filter(s -> !frontier.contains(s) && !visited.contains(s))
                            .collect(Collectors.toList());

                    if (!actualFrontier.contains(node)) {
                        descriptionPopup.setText("Attention! This node can't be added to the frontier.");
                        descriptionPopup.show();
                    }
                    else {
                        currentSearch.addToFrontier(node);

                        // Recalculate frontier to see if it's empty
                        actualFrontier = currentSearch.getMostRecentlyExpanded().getNeighbours()
                                .stream()
                                .filter(s -> !frontier.contains(s) && !visited.contains(s))
                                .collect(Collectors.toList());

                        if (actualFrontier.isEmpty()) {
                            currentSearch.addToVisited(currentSearch.getMostRecentlyExpanded());
                            currentStage = Stage.CURRENT_NODE_SELECTION;

                            if (firstTime) {
                                descriptionPopup.setText("Great! Now follow the algorithm steps in order to reach the goal node.");
                                descriptionPopup.show();

                                firstTime = !firstTime;
                            }
                        }
                    }

                    break;
            }
        }

        // no search
        if (!worldGraph.hasSearchInProgress())
            return false;

        // add to goals
        Behaviour behaviour = worldGraph.getCurrentSearchAgent().getBehaviour();
        if (!(behaviour instanceof BehaviourMultiPathFind))
            return false;

        BehaviourMultiPathFind multiPathFind = (BehaviourMultiPathFind) behaviour;
        multiPathFind.addNextGoal(node.getPoint().toVector2());

        return true;
    }

}
