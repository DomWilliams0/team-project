package com.b3.search;

import com.b3.search.util.Function2;
import com.b3.search.util.SearchAlgorithm;
import com.b3.search.util.SearchParameters;
import com.b3.search.util.takeable.LinkedListT;
import com.b3.search.util.takeable.StackT;
import com.b3.search.util.takeable.Takeable;

import java.util.*;

public class SearchTicker {

	private Takeable<Node> frontier;
	private List<Node> lastFrontier = new ArrayList<>();

	private Set<Node> visited = new HashSet<>();
	private Map<Node, Node> pred = new HashMap<>();
	private Map<Node, Float> D = new LinkedHashMap<>();
	private Function2<Node, Node, Float> d;
	private Function2<Node, Node, Float> h;

	private List<Node> path = new ArrayList<>();
	private boolean pathComplete;
	private boolean renderProgress;
	private Node start, end;

	private int stepsPerTick;
	private int frameDelay = 3; // todo: use time instead, for frame rate independence
	private int frameCounter = 0;
	private SearchAlgorithm algorithm;

	public SearchTicker(int stepsPerTick) {
		this.stepsPerTick = stepsPerTick;
		setAllCompleted(true);
		this.frontier = new StackT<>(); // placeholder
	}

	public boolean isRenderProgress() {
		return renderProgress;
	}

	public int getFrameDelay() {
		return this.frameDelay;
	}

	public void setFrameDelay(int fd) {
		this.frameDelay = fd;
	}

	public void reset(SearchAlgorithm algorithm, Node start, Node end) {

		this.algorithm = algorithm;

		switch (algorithm) {
			case DEPTH_FIRST:
				frontier = new StackT<>();
				d = h = SearchParameters.nothing();
				break;
			case BREADTH_FIRST:
				frontier = new LinkedListT<>();
				d = h = SearchParameters.nothing();
				break;
			case A_STAR:
				throw new UnsupportedOperationException("We need to somehow store f without putting it inside Nodes");
//				frontier = new PriorityQueueT<>();
//				d = h = SearchParameters.euclideanDistance();
//				break;
		}

		frontier.add(start);
		reset(false);
		this.start = start;
		this.end = end;

		D.put(start, 0f);
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
		pred.clear();
		D.clear();

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

		if (++frameCounter < frameDelay)
			return;

		frameCounter = 0;

		// now complete
		if (frontier.isEmpty()) {
			pathComplete = true;
			return;
		}

		lastFrontier.clear();

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
				path = constructPath(pred, start, end);
				return;
			}

			if (algorithm == SearchAlgorithm.DEPTH_FIRST) {
				Set<Node> arr = node.getNeighbours();
				for (Node s : arr) {
					if (!visited.contains(s)) {
						boolean inPending = frontier.contains(s);

						if (!inPending) {
							pred.put(s, node);
							frontier.add(s);
						}
					}
				}
			} else {
				for (Node child : node.getNeighbours()) {
					if (!visited.contains(child)) {
						float cost = D.get(node) + d.apply(node, child);
						boolean inPending = frontier.contains(child);

						if (!inPending || cost < D.get(child)) {
							pred.put(child, node);
							D.put(child, cost);
							// todo: uh oh
//							child.setF(D.get(child) + h.apply(child, end) - child.getExtraCost());

							if (!inPending) {
								lastFrontier.add(child);
								frontier.add(child);
							}
						}
					}
				}
			}
		}
	}

	public void render() {
		if (!renderProgress)
			return;

		// visited nodes
		/*for (Node visitedNode : visited)
			actorLookup.get(visitedNode.getPoint()).setNodeColour(Color.LIGHT_GRAY);

		// last frame's frontier
		for (Node frontierNode : lastFrontier) {
			actorLookup.get(frontierNode.getPoint()).setNodeColour(Color.FOREST);
		}

		// frontier
		for (Node frontierNode : frontier)
			actorLookup.get(frontierNode.getPoint()).setNodeColour(Color.LIME);

		// start and end are always red
		if (start != null)
			actorLookup.get(start.getPoint()).setNodeColour(Color.RED);
		if (end != null)
			actorLookup.get(end.getPoint()).setNodeColour(Color.RED);*/
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
