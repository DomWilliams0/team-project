package com.b3.searching;

import com.b3.searching.optional.*;

import java.util.*;

public class SearchTicker {

	private Takeable<Node<Point>> frontier;
	private List<Node<Point>> lastFrontier = new ArrayList<>();

	private Set<Node<Point>> visited = new HashSet<>();
	private Map<Node<Point>, Node<Point>> pred = new HashMap<>();
	private Map<Node<Point>, Float> D = new LinkedHashMap<>();
	private Function2<Node<Point>, Node<Point>, Float> d;
	private Function2<Node<Point>, Node<Point>, Float> h;

	private List<Node<Point>> path = new ArrayList<>();
	private boolean pathComplete;
	private boolean renderProgress;
	private Node<Point> start, end;

	private int stepsPerTick;
	private int frameDelay = 30; // todo: use time instead, for frame rate independence
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

	public void reset(SearchAlgorithm algorithm, Node<Point> start, Node<Point> end) {

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
				frontier = new PriorityQueueT<>();
				d = h = SearchParameters.euclideanDistance();
				break;
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
			actorLookup.get(start.getContent()).setSelected(false);
		if (end != null)
			actorLookup.get(end.getContent()).setSelected(false);*/

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
			actorLookup.get(start.getContent()).setSelected(true);
		if (end != null)
			actorLookup.get(end.getContent()).setSelected(true);*/

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

			Node<Point> node = frontier.take();

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
				Object[] arr = node.getSuccessors().toArray();
				arr = findLowestCost(arr);
				for (int j=0; j<arr.length; j++) {
					Node<Point> s = (Node<Point>) arr[j];
					if (!visited.contains(s)) {
						boolean inPending = frontier.contains(s);

						if (!inPending) {
							pred.put(s, node);
							frontier.add(s);
						}
					}
				}
			} else {
				for (Node<Point> child : node.getSuccessors()) {
					if (!visited.contains(child)) {
						float cost = D.get(node) + d.apply(node, child);
						boolean inPending = frontier.contains(child);

						if (!inPending || cost < D.get(child)) {
							pred.put(child, node);
							D.put(child, cost);
							child.setF(D.get(child) + h.apply(child, end) - child.getExtraCost());

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

	private Object[] findLowestCost(Object[] arrTemp) {
		int n = arrTemp.length;

		boolean swapped = true;

		while (swapped) {
			swapped = false;
			for (int i = 1; i < n; i++) {
				Node<Point> a = (Node<Point>) arrTemp[i];
				Node<Point> b = (Node<Point>) arrTemp[i - 1];
				if (b.getExtraCost() < a.getExtraCost()) {
					arrTemp[i] = b;
					arrTemp[i-1] = a;
					swapped = true;
				}
			}
		}

		return arrTemp;
	}

	public void render() {
		if (!renderProgress)
			return;

		// visited nodes
		/*for (Node<Point> visitedNode : visited)
			actorLookup.get(visitedNode.getContent()).setNodeColour(Color.LIGHT_GRAY);

		// last frame's frontier
		for (Node<Point> frontierNode : lastFrontier) {
			actorLookup.get(frontierNode.getContent()).setNodeColour(Color.FOREST);
		}

		// frontier
		for (Node<Point> frontierNode : frontier)
			actorLookup.get(frontierNode.getContent()).setNodeColour(Color.LIME);

		// start and end are always red
		if (start != null)
			actorLookup.get(start.getContent()).setNodeColour(Color.RED);
		if (end != null)
			actorLookup.get(end.getContent()).setNodeColour(Color.RED);*/
	}


	private List<Node<Point>> constructPath(Map<Node<Point>, Node<Point>> map, Node<Point> start, Node<Point> end) {

		// Helper stack for inserting elements
		Stack<Node<Point>> path = new Stack<>();
		// Array list that will contain the ordered path to return
		ArrayList<Node<Point>> retPath = new ArrayList<>();

		// Add end node to the stack
		path.add(end);

		// Insert nodes into stack
		Node<Point> n;
		while (!(n = path.peek()).equals(start)) {
			path.add(map.get(n));
		}

		// Pop elements to get correct order (from start to end)
		while (!path.isEmpty()) {
			retPath.add(path.pop());
		}

		return retPath;
	}

	public Collection<Node<Point>> getFrontier() {
		return frontier;
	}

	public Set<Node<Point>> getVisited() {
		return visited;
	}

	public List<Node<Point>> getPath() {
		return path;
	}

	public boolean isPathComplete() {
		return pathComplete;
	}

	public int getStepsPerTick() {
		return stepsPerTick;
	}

	public Node<Point> getStart() {
		return start;
	}

	public void setStart(Node<Point> start) {
		this.start = start;
	}

	public Node<Point> getEnd() {
		return end;
	}

	public void setEnd(Node<Point> end) {
		this.end = end;
	}

}
