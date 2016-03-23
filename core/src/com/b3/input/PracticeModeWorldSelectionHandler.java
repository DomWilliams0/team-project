package com.b3.input;

import com.b3.gui.PopupDescription;
import com.b3.gui.components.MessageBoxComponent;
import com.b3.gui.popup.Popup;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.takeable.Takeable;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.collision.Ray;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An enum to tell the world what stage {@link com.b3.mode.PracticeMode} is in.
 * IE if the user is adding to the frontier or explored
 *
 * @author oxe410
 */
enum Stage {
	CURRENT_NODE_SELECTION,
	ADD_TO_FRONTIER_SELECTION,
	ADD_TO_EXPLORED_SELECTION
}

/**
 * A selection handlers, that deals with input into the world
 * Takes the input and decides what class to call
 *
 * @author oxe410
 */
public class PracticeModeWorldSelectionHandler extends WorldSelectionHandler {

	private boolean firstLoadAlert1;
	private boolean firstLoadAlert2;

	private Stage currentStage;
	private MessageBoxComponent descriptionPopup;
	private boolean firstTime;

	/**
	 * Creates a new selection handler linked to a world
	 *
	 * @param world the {@link com.b3.mode.PracticeMode} to link this selection handler to
	 * @param popupStage the {@link com.badlogic.gdx.scenes.scene2d.Stage} to use
	 */
	public PracticeModeWorldSelectionHandler(World world, com.badlogic.gdx.scenes.scene2d.Stage popupStage) {
		super(world);
		this.currentStage = Stage.CURRENT_NODE_SELECTION;
		this.descriptionPopup = new MessageBoxComponent(popupStage, "", "OK");
		this.firstTime = true;
		this.firstLoadAlert1 = true;
		this.firstLoadAlert2 = true;
	}

	/**
	 * Decides what to do when the user presses on the world
	 * Either: adds to the frontier, adds to the visited, or shows an error
	 *
	 * @param screenX the x position of the mouse
	 * @param screenY the y position of the mouse
	 * @param pointer unused
	 * @param button  the button clicked (left, right or middle)
	 * @return
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for (int i = 0; i < world.getWorldGUI().getPopupManager().length(); i++) {
			Popup popup = world.getWorldGUI().getPopupManager().getPopup(i);
			if (popup.justOpen) {
				popup.shouldClose = true;
				return false;
			}
		}

		// if near bottom of screen hitting the intensive learning mode button so ignore
		if (screenX < 100 && screenY > Gdx.graphics.getHeight() - 100) {
			return false;
		}

		// selecting a tile
		WorldCamera worldCamera = world.getWorldCamera();
		Ray ray = worldCamera.getPickRay(screenX, screenY);
		ray.getEndPoint(tempRayCast, worldCamera.position.z);

		// convert to node
		WorldGraph worldGraph = world.getWorldGraph();
		Node node = worldGraph.getNode(new Point((int) tempRayCast.x, (int) tempRayCast.y));

		PopupDescription popupDescription = world.getWorldGUI().getPopupDescription();
		if (currentSelection.x == (int) tempRayCast.x && currentSelection.y == (int) tempRayCast.y) {
			// old node so change page number
			if (popupDescription.getPopupShowing())
				// if popup showing
				popupDescription.resetCounterAnimation();
			popupDescription.flipPageRight();
		} else {
			// new node so reset page number
			if (popupDescription.getPopupShowing())
				// if popup showing
				popupDescription.resetPage();
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
					System.out.println("Node that is expected to be clicked: " + actualNode);
					if (!actualNode.equals(node)) {
						if (firstLoadAlert1) {
							descriptionPopup.setText("Attention! This node is not the one to be selected for expansion.");
							descriptionPopup.show();
							firstLoadAlert1 = false;
						}
						worldGraph.getRenderer().highlightNodeRed(node.getPoint().getX(), node.getPoint().getY(), 50);
					} else {
						frontier.take();
						currentSearch.setMostRecentlyExpanded(node);
						currentStage = Stage.ADD_TO_FRONTIER_SELECTION;

						if (currentSearch.getEnd().equals(node)) {
							currentSearch.generatePath(node);
							currentSearch.setAllCompleted(true);
							currentStage = Stage.CURRENT_NODE_SELECTION;

							descriptionPopup.setText("Great! You reached the target");
							descriptionPopup.show();
						} else {
							if (firstTime) {
								descriptionPopup.setText("Good! Now please select the nodes to add to the frontier.");
								descriptionPopup.show();
							}

							// Backtrack check.
							List<Node> actualFrontier = node.getNeighbours()
									.stream()
									.filter(s -> !frontier.contains(s) && !visited.contains(s))
									.collect(Collectors.toList());
							if (actualFrontier.isEmpty()) {
								currentStage = Stage.CURRENT_NODE_SELECTION;
								currentSearch.addToVisited(node);
							}
						}

						currentSearch.setUpdated(true);
					}
					break;

				case ADD_TO_FRONTIER_SELECTION:
					List<Node> actualFrontier = currentSearch.getMostRecentlyExpanded().getNeighbours()
							.stream()
							.filter(s -> !frontier.contains(s) && !visited.contains(s))
							.collect(Collectors.toList());

					if (!actualFrontier.contains(node)) {
						if (firstLoadAlert2) {
							descriptionPopup.setText("Attention! This node can't be added to the frontier.");
							descriptionPopup.show();
							firstLoadAlert2 = false;
						}
						worldGraph.getRenderer().highlightNodeRed(node.getPoint().getX(), node.getPoint().getY(), 50);
					} else {
						currentSearch.addToFrontier(node);
						currentSearch.addToCameFrom(node, currentSearch.getMostRecentlyExpanded());

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

						currentSearch.setUpdated(true);
					}

					break;
			}
		}

		return true;
	}

}
