package com.b3.search;

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

public class SearchTicker {

	private Takeable<Node> frontier;
	private List<Node> lastFrontier = new ArrayList<>();

	private Set<Node> visited = new HashSet<>();
	private Map<Node, Node> cameFrom = new HashMap<>();
	private Map<Node, Float> costSoFar = new LinkedHashMap<>();

	private List<Node> path = new ArrayList<>();
	private boolean pathComplete;
	private boolean renderProgress;
	private Node start, end;

	private int stepsPerTick;
	private float timer;
	private boolean paused;

	private SearchAlgorithm algorithm;
	private Function<Node, Float> costSoFarFunction; // g in f(x) = g(x)+h(x)
	private Function2<Node, Node, Float> edgeCostFunction;

	public SearchTicker() {
		setAllCompleted(true);
		this.frontier = new StackT<>(); // placeholder
		paused = false;
	}

	public boolean isRenderProgress() {
		return renderProgress;
	}

	public void reset(SearchAlgorithm algorithm, Node start, Node end) {

		this.algorithm = algorithm;

		SearchParameters parameters = new SearchParameters(algorithm);
		Function2<Node, Node, Float> heuristic = parameters.getHeuristic();
		if (algorithm == SearchAlgorithm.A_STAR) {

			edgeCostFunction = Node::getEdgeCost;
			costSoFarFunction = node -> {
				Float value = costSoFar.get(node);
				return value == null ? Float.POSITIVE_INFINITY : value;
			};
		} else {
			edgeCostFunction = (n1, n2) -> 0f;
			costSoFarFunction = node -> 0f;
		}

		frontier = parameters.createFrontier(costSoFarFunction, heuristic, end);
		frontier.add(start);

		reset(false);

		this.start = start;
		this.end = end;

		costSoFar.put(start, 0f);
		renderProgress = true;
	}


	public void reset(boolean fromResetBtn) {
		setAllCompleted(fromResetBtn);

		/*if (start != null)
			actorLookup.get(start.getPoint()).setSelected(false);
		if (end != null)
			actorLookup.get(end.getPoint()).setSelected(false);*/

		lastFrontier.clear();
		visited.clear();
		path.clear();
		cameFrom.clear();
		costSoFar.clear();

		renderProgress = false;
	}

	private void setAllCompleted(boolean completed) {

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

	public void tick() {
		// already complete
		if (pathComplete)
			return;

		float timeBetweenTicks = Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS);
		timer += Utils.TRUE_DELTA_TIME;
		if (timer < timeBetweenTicks)
			return;

		timer -= timeBetweenTicks;

		// now complete
		if (frontier.isEmpty()) {
			pathComplete = true;
			return;
		}
		System.out.println(paused);
		//we have been told to pause
		if(paused) return;

		lastFrontier.clear();

		// Get steps per tick
		setStepsPerTick();

		for (int i = 0; i < stepsPerTick; i++) {
			// done
			if (frontier.isEmpty()) {
				setAllCompleted(true);
				return;
			}

			Node node = frontier.take();

			// already visited
			if (visited.contains(node))
				continue;

			visited.add(node);

			if (node.equals(end)) {
				setAllCompleted(true);
				path = constructPath(cameFrom, start, end);
				return;
			}

			if (algorithm == SearchAlgorithm.DEPTH_FIRST) {
				Set<Node> arr = node.getNeighbours();
				for (Node s : arr) {
					if (!visited.contains(s)) {
						boolean inPending = frontier.contains(s);

						if (!inPending) {
							cameFrom.put(s, node);
							frontier.add(s);
						}
					}
				}
			} else {
				node.getNeighbours()
						.stream()
						.filter(child -> !visited.contains(child))
						.forEach(child -> {
							float tentative_g = costSoFarFunction.apply(node) + edgeCostFunction.apply(node, child);
							if (!frontier.contains(child))
								frontier.add(child);
							if (tentative_g <= costSoFarFunction.apply(child)) {
								cameFrom.put(child, node);
								costSoFar.put(child, tentative_g);
							}
						});
			}
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

	public Set<Node> getVisited() {
		return visited;
	}

	public List<Node> getPath() {
		return path;
	}

	public boolean isPathComplete() {
		return pathComplete;
	}

	public void setStepsPerTick() {
		stepsPerTick = Config.getInt(ConfigKey.STEPS_PER_TICK);
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public boolean isPaused() {
		return paused;
	}

	public int getStepsPerTick() {
		return stepsPerTick;
	}

	public Node getStart() {
		return start;
	}

	public void setStart(Node start) {
		this.start = start;
	}

	public Node getEnd() {
		return end;
	}

	public void setEnd(Node end) {
		this.end = end;
	}

	public SearchAlgorithm getAlgorithm() {
		return algorithm;
	}

}
