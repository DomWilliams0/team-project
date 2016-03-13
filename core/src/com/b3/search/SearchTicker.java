package com.b3.search;

import com.b3.gui.PseudocodeVisualiser;
import com.b3.input.SoundController;
import com.b3.mode.Mode;
import com.b3.mode.ModeType;
import com.b3.search.util.Function2;
import com.b3.search.util.SearchAlgorithm;
import com.b3.search.util.SearchParameters;
import com.b3.search.util.takeable.StackT;
import com.b3.search.util.takeable.Takeable;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchTicker extends Observable {

	private final WorldGraph worldGraph;
	private Takeable<Node> frontier;
	private List<Node> lastFrontier = new ArrayList<>();
	private Node mostRecentlyExpanded;						// Current node (expanded)
	private List<Node> currentNeighbours;					// Current neighbours to be expanded
	private Node currentNeighbour; 							// Current neighbour being analyzed
	private int neighboursSoFar;							// Neighbours visited so far

	private Set<Node> visited = new HashSet<>();
	private Map<Node, Node> cameFrom = new HashMap<>();

	private List<Node> path = new ArrayList<>();
	private boolean pathComplete;
	private boolean renderProgress;
	private Node start, end;

	private float timer;
	//array to identify who has told it to pause so conflicts don't occur
	//0: scrollpane
	//1: play/pause button
	//4: stepthrough active
	private boolean[] paused;
	private boolean updated;
	private boolean tickedOnce;

	private SearchAlgorithm algorithm;
	private Function<Node, Float> costSoFarFunction; // g in f(x) = g(x)+h(x)
	private Function2<Node, Node, Float> edgeCostFunction;

	// Pseudocode (inspect search)
	private Pseudocode pseudocode;
	private boolean inspectSearch;
	private ModeType mode;

	public SearchTicker(WorldGraph worldGraph, ModeType mode) {
		this.worldGraph = worldGraph;
		inspectSearch = false;
		tickedOnce = false;

		this.mode = mode;

		setAllCompleted(true);
		this.frontier = new StackT<>(); // placeholder
		//setup pause status and ensure it is unpaused.
		paused = new boolean[5];
		for(int i=0;i<paused.length;i++) paused[i]=false;
	}

	public boolean isRenderProgress() {
		return renderProgress;
	}

	public boolean isInspectingSearch() {
		return inspectSearch;
	}

	public void setInspectSearch(boolean inspectSearch) {
		this.inspectSearch = inspectSearch;
	}

	public void reset(SearchAlgorithm algorithm, Node start, Node end) {

		tickedOnce = false;

		this.algorithm = algorithm;

		pseudocode = new Pseudocode(algorithm);
		pseudocode.addObserver(PseudocodeVisualiser.getInstance());
		pseudocode.highlight(0);

		SearchParameters parameters = new SearchParameters(algorithm);
		Function2<Node, Node, Float> heuristic = parameters.getHeuristic();
		if (algorithm == SearchAlgorithm.A_STAR || algorithm == SearchAlgorithm.DIJKSTRA) {

			edgeCostFunction = Node::getEdgeCost;
			costSoFarFunction = (node) -> {
				if (!cameFrom.containsKey(node)) return Float.POSITIVE_INFINITY;
				float cost = 0;
				while (node != start) {
					Node n2 = cameFrom.get(node);
					cost += n2.getEdgeCost(node);
					node = n2;
				}
				return cost;
			};
		} else {
			edgeCostFunction = (n1, n2) -> 0f;
			costSoFarFunction = (node) -> 0f;
		}

		frontier = parameters.createFrontier(costSoFarFunction, heuristic, end);
		frontier.add(start);

		reset(false);

		this.start = start;
		this.end = end;
		renderProgress = true;
	}

	public void reset(boolean fromResetBtn) {

		tickedOnce = false;

		setAllCompleted(fromResetBtn);

		/*if (start != null)
			actorLookup.get(start.getPoint()).setSelected(false);
		if (end != null)
			actorLookup.get(end.getPoint()).setSelected(false);*/

		lastFrontier.clear();
		visited.clear();
		path.clear();
		cameFrom.clear();

		renderProgress = false;
	}

	public void setAllCompleted(boolean completed) {

		tickedOnce = false;

		pathComplete = completed;

		// reset all colours
		/*for (NodeActor nodeActor : actorLookup.values()) {
			nodeActor.setNodeColour(Color.BLACK);
		}

		// except start and end
		if (start != null)
			actorLookup.get(start.getPoint()).setSelected(true);
		if (end != null)
			actorLookup.get(end.getPoint()).setSelected(true);*/

		// clear states
		if (completed) {
			renderProgress = false;
			visited.clear();
			if (frontier != null)
				frontier.clear();
		}

	}

	/**
	 * tick one step in the current search
	 * abides by current pause status, and will only tick after a specified time since last tick.
	 */
	public void tick() {

		Float timeBetweenTicks;
		if (isPaused()) {
			timeBetweenTicks = Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS_MAX);
		} else {
			tickedOnce = true;
			timeBetweenTicks = Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS);
		}

		timer += Utils.TRUE_DELTA_TIME;

		if (timer < timeBetweenTicks)
			return;

		if (!isPaused())
				worldGraph.setColFlicker();

		if(timer > 2*timeBetweenTicks)
			//it has been a long time since last tick so reset it instead of decrementing it
			timer = 0;
		else
			// it hasn't been too long since last tick so decrement it
			timer -= timeBetweenTicks;

		//check if we're supposed to be paused
		if (isPaused()) {return;}

		tickFinal();
	}

	/**
	 * tick one step in the current search
	 * with option to override time settings and pause status
	 * @param override Whether to force a tick to occur regardless of time or pause status
     */
	public void tick(boolean override) {
		tickedOnce = true;

		worldGraph.setColFlicker();

		if(override)
			//override current status
			tickFinal();
		else
			// tick, but check pause status and timer.
			tick();
	}

	/**
	 * Performs a search tick for a specific line in the pseudocode
	 * @param line The line to execute
     */
	private void tickPseudocode(int line) {
		if (pathComplete)
			return;

		if (line == 0 && frontier.isEmpty()) {
			setAllCompleted(true);
			return;
		}

		lastFrontier.clear();

		if (line == 0) {
			pseudocode.highlight(line + 1);
			return;
		}

		//Node node;

		if (line == 1) {
			mostRecentlyExpanded = frontier.take();
			setUpdated(true);
			pseudocode.highlight(line + 1);
			return;
		}

		if (line == 2) {
			pseudocode.highlight(line + 1);
			return;
		}

		if (line == 3) {
			visited.add(mostRecentlyExpanded);
			setUpdated(true);
			pseudocode.highlight(line + 1);
			return;
		}

		if (line == 4) {
			path = constructPath(cameFrom, start, mostRecentlyExpanded);

			if (mostRecentlyExpanded.equals(end)) {
				pseudocode.highlight(5);
				setAllCompleted(true);
				return;
			}
		}

		// BFS or DFS
		// ----------
		if (algorithm == SearchAlgorithm.DEPTH_FIRST || algorithm == SearchAlgorithm.BREADTH_FIRST) {
			if (line == 4) {
				pseudocode.highlight(6);
				currentNeighbours = mostRecentlyExpanded.getNeighbours().stream().collect(Collectors.toList());
				return;
			}

			if (neighboursSoFar < currentNeighbours.size()) {
				// FOR
				currentNeighbour = currentNeighbours.get(neighboursSoFar);

				if (line == 6) {
					pseudocode.highlight(line + 1);
					return;
				}

				if (!visited.contains(currentNeighbour) && !frontier.contains(currentNeighbour) && pseudocode.getCurrentLine() == 7) {
					pseudocode.highlight(8);

					cameFrom.put(currentNeighbour, mostRecentlyExpanded);
					frontier.add(currentNeighbour);
					lastFrontier.add(currentNeighbour);
					setUpdated(true);
					return;
				}

				if (pseudocode.getCurrentLine() == 7 || pseudocode.getCurrentLine() == 8) {
					pseudocode.highlight(6);
					neighboursSoFar++;
					return;
				}
				// ENDFOR
			}

			neighboursSoFar = 0;
			currentNeighbours = null;
			currentNeighbour = null;
		}
		// A* and Dijkstra
		// ---------------
		else {
			if (line == 4) {
				pseudocode.highlight(6);
				currentNeighbours = mostRecentlyExpanded.getNeighbours().stream().collect(Collectors.toList());
				return;
			}

			if (neighboursSoFar < currentNeighbours.size()) {
				// FOR
				currentNeighbour = currentNeighbours.get(neighboursSoFar);

				if (line == 6) {
					pseudocode.highlight(line + 1);
					return;
				}

				if (line == 7) {
					pseudocode.highlight(line + 1);
					return;
				}

				if (line == 8) {
					float tentative_g = costSoFarFunction.apply(mostRecentlyExpanded) + edgeCostFunction.apply(mostRecentlyExpanded, currentNeighbour);
					if (tentative_g <= costSoFarFunction.apply(currentNeighbour)) {
						cameFrom.put(currentNeighbour, mostRecentlyExpanded);
						pseudocode.highlight(line + 1);
						return;
					}
				}

				if (line == 9) {
					pseudocode.highlight(line + 1);
					return;
				}

				if (line == 10) {
					if (!frontier.contains(currentNeighbour) && !visited.contains(currentNeighbour)) {
						pseudocode.highlight(line + 1);
						frontier.add(currentNeighbour);
						lastFrontier.add(currentNeighbour);
						setUpdated(true);

						return;
					}
				}

				// Next neighbour in the loop
				if (line == 8 || line == 10 || line == 11) {
					pseudocode.highlight(6);
					neighboursSoFar++;
					return;
				}
				// ENDFOR
			}

			neighboursSoFar = 0;
			currentNeighbours = null;
			currentNeighbour = null;
		}

		pseudocode.setCurrentLine(0);
		setUpdated(true);
	}

	/**
	 * Performs a tick where the only checks are whether it is complete
	 * This being called will progress the search one step regardless of status of timer or pause.
	 */
	private void tickFinal() {
		tickedOnce = true;

		if (inspectSearch) {
			tickPseudocode(pseudocode.getCurrentLine());
			return;
		}

		// already complete
		if (pathComplete)
			return;

		if (frontier.isEmpty()) {
			// Failed to find a path!
			// Or node was not added to the frontier on initialisation.
			setAllCompleted(true);
			path.clear(); // Clear the path, so no false hopes.
			return;
		}

		lastFrontier.clear();

		Node node = frontier.take();

		//record us expanding this node
		mostRecentlyExpanded = node;

		visited.add(node);

		path = constructPath(cameFrom, start, node);
		if (node.equals(end)) {
			setAllCompleted(true);
			return;
		}

		if (algorithm == SearchAlgorithm.DEPTH_FIRST || algorithm == SearchAlgorithm.BREADTH_FIRST) {
			node.getNeighbours()
					.stream()
					.filter(s -> !visited.contains(s))
					.forEach(s -> {
						boolean inPending = frontier.contains(s);

						if (!inPending) {
							cameFrom.put(s, node);
							frontier.add(s);
							lastFrontier.add(s);
						}
					});
		} else {
			node.getNeighbours()
					.stream()
					.forEach(child -> {
						float tentative_g = costSoFarFunction.apply(node) + edgeCostFunction.apply(node, child);
						if (tentative_g <= costSoFarFunction.apply(child)) {
							cameFrom.put(child, node);
							if (!frontier.contains(child) && !visited.contains(child)) {
								frontier.add(child);
								lastFrontier.add(child);
							}
						}
					});
		}

		setUpdated(true);

		if (Config.getBoolean(ConfigKey.SOUNDS_ON) && mode == ModeType.LEARNING) {
			//TODO this is the sound 'wave' pings that sounds cool. Needs to be refactored out of here though.
			if (worldGraph.getCurrentSearch().getStart() != null && worldGraph.getCurrentSearch().getEnd() != null && worldGraph.getCurrentSearch().getMostRecentlyExpanded() != null) {
				Point currentNode = worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint();
				Point startNode = worldGraph.getCurrentSearch().getStart().getPoint();
				Point end = worldGraph.getCurrentSearch().getEnd().getPoint();

				int changeInX = currentNode.getX() - end.getX();
				int changeInY = currentNode.getY() - end.getY();

				int changeInX2 = changeInX * changeInX;
				int changeInY2 = changeInY * changeInY;

				float finalEuclid = (float) Math.sqrt(changeInX2 + changeInY2);

				changeInX = startNode.getX() - end.getX();
				changeInY = startNode.getY() - end.getY();

				changeInX2 = changeInX * changeInX;
				changeInY2 = changeInY * changeInY;

				float maxValue = (float) Math.sqrt(changeInX2 + changeInY2);
//			float maxValue = 10;

				float convertedFinalEuclid = (float) ((finalEuclid / maxValue) * 2);

				SoundController.playSounds(3, convertedFinalEuclid);
			}
		} else {
			SoundController.stopSound(3);
		}

	}

	private List<Node> constructPath(Map<Node, Node> map, Node start, Node end) {

		// Helper stack for inserting elements
		Stack<Node> path = new Stack<>();
		// Array list that will contain the ordered path to return
		ArrayList<Node> retPath = new ArrayList<>();

		// Add end node to the stack
		path.add(end);

		// Insert nodes into stack
		Node n;
		while (!(n = path.peek()).equals(start)) {
			path.add(map.get(n));
		}

		// Pop elements to get correct order (from start to end)
		while (!path.isEmpty()) {
			retPath.add(path.pop());
		}

		return retPath;
	}

	public Takeable<Node> getFrontier() {
		return frontier;
	}

	public void addToFrontier(Node node) {
		frontier.add(node);
		setUpdated(true);
	}

	public Set<Node> getVisited() {
		return visited;
	}

	public void addToVisited(Node node) {
		visited.add(node);
		setUpdated(true);
	}

	public void addToCameFrom(Node node1, Node node2) {
		cameFrom.put(node1, node2);
	}

	public List<Node> getPath() {
		return path;
	}

	public void generatePath(Node end) {
		path = constructPath(cameFrom, start, end);
	}

	public boolean isPathComplete() {
		return pathComplete;
	}

	/**
	 * Tell the ticker to pause
	 * @param index your identifier, to know when you tell it to resume
     */
	public void pause(int index) {
		paused[index] = true;

		setChanged();
		notifyObservers();
	}

	/**
	 * Tell the ticker to resume
	 * @param index your identifier, to know if everyone has told it to resume.
     */
	public void resume(int index) {
		paused[index] = false;

		setChanged();
		notifyObservers();
	}

	public boolean isPaused() {
		for(boolean pause : paused) if(pause) return true;
		return false;
	}

	/**
	 * query an individual index of paused as to whether it is paused.
	 * @param index the index to query
	 * @return wither paused[index] is true
     */
	public boolean isPaused(int index) {
		return paused[index];
	}
	
	/**
	 * @return The {@link Node} that the search is starting from.
	 */
	public Node getStart() {
		return start;
	}
	
	/**
	 * Sets the {@link Node} that the search will start expanding from.
	 * @param start The new start {@link Node}.
	 */
	public void setStart(Node start) {
		this.start = start;
	}
	
	/**
	 * @return The {@link Node} that the search is trying to reach (the goal).
	 */
	public Node getEnd() {
		return end;
	}
	
	/**
	 * Sets the {@link Node} that the search is looking for.
	 * @param end The new goal {@link Node}.
	 */
	public void setEnd(Node end) {
		this.end = end;
	}
	
	/**
	 * @return The algorithm being used for this search.
	 */
	public SearchAlgorithm getAlgorithm() {
		return algorithm;
	}
	
	/**
	 * @return The {@link Node} that the search has just expanded.
	 *         May be <code>null</code>!
	 */
	public Node getMostRecentlyExpanded() {
		return mostRecentlyExpanded;
	}
	
	/**
	 * @return The neighbours of the most recently expanded {@link Node} of the search.
	 *         May be <code>null</code>!
	 */
	public List<Node> getCurrentNeighbours() {
		return currentNeighbours;
	}
	
	/**
	 * @return The neighbour of the most recently expanded {@link Node} that is currently being evaluated.
	 *         May be <code>null</code>!
	 */
	public Node getCurrentNeighbour() {
		return currentNeighbour;
	}

	public void setMostRecentlyExpanded(Node mostRecentlyExpanded) {
		this.mostRecentlyExpanded = mostRecentlyExpanded;
	}

	public List<Node> getLastFrontier() {
		return lastFrontier;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isUpdated() {
		return updated;
	}

	public float getG(Node node, Node child) {
		float g = costSoFarFunction.apply(node) + edgeCostFunction.apply(node, child);
		return g;
	}
	
	/**
	 * Gets the cost so far that is needed to reach a {@link Node}.
	 * Value may decrease after successive calls of {@link #tick()}.
	 * @param node The {@link Node} to get the current cost it takes to reach.
	 * @return A positive cost that the search has currently achieved to reach {@code node}.
	 */
	public float getG(Node node) {
		float g = costSoFarFunction.apply(node);
		return g;
	}

	public boolean isTickedOnce () {
		return tickedOnce;
	}

	public void clearPseudocodeInfo() {
		currentNeighbour = null;
		currentNeighbours = new ArrayList<>();
		neighboursSoFar = 0;
	}
}
