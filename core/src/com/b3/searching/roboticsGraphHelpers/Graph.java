package com.b3.searching.roboticsGraphHelpers;



import com.b3.searching.roboticsGraphHelpers.collectFuncMaybe.*;
import com.b3.searching.utils.ReadFile;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static com.b3.searching.roboticsGraphHelpers.SearchParameters.euclideanDistance;

/**
 * Represents a graph using the adjacency list approach
 * (undirected, directed or mixed)
 */
public class Graph<A> {
	
	private Map<A, Node<A>> nodes;
	
	/**
	 * Create a new graph
	 */
	public Graph() {
		nodes = new LinkedHashMap<>();
	}
	
	/**
	 * Initialise a frontier according to a specified search algorithm
	 * @param alg The search algorithm
	 * @return An empty collection
	 */
	private Takeable<Node<A>> initFrontier(SearchAlgorithm alg) {
		Takeable<Node<A>> frontier;
		
		switch (alg) {
			case A_STAR:
				frontier = new PriorityQueueT<>();
				break;
				
			case DEPTH_FIRST:
				frontier = new StackT<>();
				break;
			
			case BREADTH_FIRST:
			default:
				frontier = new LinkedListT<>();
				break;
		}
		
		return frontier;
	}
	
	/**
	 * Constructs a path from a tracking map, a start and a goal node
	 * @param map The tracking map (child - parent entries)
	 * @param start Start node
	 * @param end Goal node
	 * @return List of nodes that defines the path
	 */
	private ArrayList<Node<A>> constructPath(Map<Node<A>, Node<A>> map, Node<A> start, Node<A> end) {
		// Helper stack for inserting elements
		Stack<Node<A>> path = new Stack<>();
		// Array list that will contain the ordered path to return 
		ArrayList<Node<A>> retPath = new ArrayList<>();
		// Add end node to the stack
		path.add(end);
		
		// Insert nodes into stack
		Node<A> n;
		while (!(n = path.peek()).equals(start)) {
			path.add(map.get(n));
		}
		
		// Pop elements to get correct order (from start to end)
		while (!path.isEmpty()) {
			retPath.add(path.pop());
		}
		
		return retPath;
	}

	public ArrayList<Node<A>> getNodes() {
		return new ArrayList<>(nodes.values());
	}

	public Maybe<Node<A>> getNode(A a) {
		return hasNode(a) ? new Just<>(nodes.get(a)) : new Nothing<>();
	}
	/**
	 * Check whether there is a specified node in the table of nodes
	 * @param c The content of the node to search
	 * @return True is the node exists, false otherwise
	 */
	public boolean hasNode(A c) {
		return nodes.containsKey(c);
	}
	
	/**
	 * Adds a new node to the table of nodes
	 * @param c The node's data/content
	 * @return The new node. If it exists then simply return it.
	 */
	public Node<A> addNode(A c, int cost) {
		Node<A> node;
		
		if (!nodes.containsKey(c)) {
			node = new Node<>(c);
			nodes.put(c, node);
		}
		else
			node = nodes.get(c);

		node.setExtraCost(cost);

		return node;
	}
	
	/**
	 * Removes a specified node
	 * @param c The node's content to be removed
	 * @return True if the node is in the table, false otherwise
	 */
	private boolean removeNode(A c) {
		if (hasNode(c)) {
			Node<A> removedNode = nodes.remove(c);
			
			// Remove node from related neighbours
			for (Entry<A, Node<A>> entry : nodes.entrySet()) {
				A key = entry.getKey();
				Node<A> value = entry.getValue();
				
				if (value.getSuccessors().contains(removedNode)) {
					value.removeSuccessor(removedNode);
					nodes.put(key, value);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Tells whether the graph has a specific edge
	 * @param c1 The first component
	 * @param c2 The second component
	 * @return True if the graph has a c1 -- c2 edge, false otherwise
	 */
	private boolean hasEdge(A c1, A c2) {
		Node<A> n1 = nodes.get(c1);
		Node<A> n2 = nodes.get(c2);
		
		return n1 != null && n2 != null && (n1.getSuccessors().contains(n2) || n2.getSuccessors().contains(n1));
	}
	
	/**
	 * Adds an edge/link between 2 nodes 
	 * @param c1 The content of the first node (source)
	 * @param c2 The content of the second node (destination)
	 * @param directed If true the edge is directed. If false it is undirected
	 */
	public void addEdge(A c1, A c2, boolean directed, int cost1, int cost2) {
		// Add nodes (if not existing) and get nodes from adjacency list
		Node<A> node1 = addNode(c1, cost1);
		Node<A> node2 = addNode(c2, cost2);

		// Create edge
		node1.addSuccessor(node2);
		if (!directed)
			node2.addSuccessor(node1);
		
		// Update adjacency list
		nodes.put(c1, node1);
		if (!directed)
			nodes.put(c2, node2);
	}
	
	/**
	 * Removes an edge (undirected or directed) between two nodes if it exists
	 * @param c1 The content of the first node
	 * @param c2 The content of the second node
	 * @return True if the edge exists and has been removed, false otherwise
	 */
	public boolean removeEdge(A c1, A c2) {
		// If there are the two nodes
		if (hasNode(c1) && hasNode(c2)) {
			Node<A> node1 = nodes.get(c1);
			Node<A> node2 = nodes.get(c2);
			
			// Attempt to remove the edge (if exists - either directed or undirected)
			boolean removed1 = node1.removeSuccessor(node2);
			boolean removed2 = node2.removeSuccessor(node1);
			
			if (removed1)
				nodes.put(c1, node1);
			else if (removed2)
				nodes.put(c2, node2);
			else return false;
			
			return true;
		}
		
		return false;
	}

	
	/**
	 * Find a path from the origin to the destination given search parameters
	 * @param origin The content of the starting node
	 * @param destination The content of the destination node
	 * @return Maybe a path
	 */
	public Maybe<List<Node<A>>> findPathFromASTAR(A origin, A destination) {

		SearchParameters<A> params = (SearchParameters<A>) new SearchParameters<>().AStarParams();

		// Get origin node from nodes map and create destination node with the specified content
		Node<A> startNode = nodes.get(origin);
		Node<A> destinationNode = new Node<>(destination);

		if (startNode == null)
			return new Nothing<>();

		// Unpack parameters: search algorithm, heuristic and distance functions
		Function2<Node<A>, Node<A>, Float> h = params.getHeuristic();
		Function2<Node<A>, Node<A>, Float> d = params.getDistanceFn(); // Edge weighting
		SearchAlgorithm alg = params.getAlgorithm();
		
		// Declare explored, pending, predecessors and cost tracking collections (cost from the origin along best known path)
		Set<Node<A>> explored = new HashSet<>();
		Takeable<Node<A>> pending = initFrontier(alg);
		Map<Node<A>, Node<A>> pred = new LinkedHashMap<>();
		Map<Node<A>, Float> D = new LinkedHashMap<>();
		
		// Initialise collections
		pending.add(startNode);
		D.put(startNode, 0f);
		startNode.setF(h.apply(startNode, destinationNode));
		
		// Start search
		while (!pending.isEmpty()) {
			// Get node according to the data structure
			Node<A> n = pending.take();
			
			// If it is the destination node then
			if (n.equals(destinationNode))
				// construct path from the map tracking and the start node
				return new Just<>(constructPath(pred, startNode, n));
			
			// Add n to the explored set
			explored.add(n);


			// Loop through all successors of n that haven't been visited yet
			for (Node<A> s : n.getSuccessors()) {
				// If successor has not been visited yet
				if (!explored.contains(s)) {
					float cost = D.get(n) + d.apply(n, s);
					boolean inPending = pending.contains(s); // Without computing it 2 times
					
					// If s is not in the frontier or found a better path
					if (!inPending || cost < D.get(s)) {
						// Update predecessor, actual and estimated cost
						pred.put(s, n);
						D.put(s, cost);
						if (s.getExtraCost() > 0) System.out.println("working");
						s.setF(D.get(s) + h.apply(s, destinationNode) - s.getExtraCost());
						
						if (!inPending)
							pending.add(s);
					}
				}
			}
		}
		
		// No path found
		return new Nothing<>();
	}
	
	public Maybe<Iterable<IntermediateState<A>>> findPathFromWithTrackingData(A origin, A destination, SearchParameters<A> params) {
		// Tracking data
		ArrayList<IntermediateState<A>> trackingData = new ArrayList<>();
		
		// Get origin node from nodes map and create destination node with the specified content
		Node<A> startNode = nodes.get(origin);
		Node<A> destinationNode = new Node<>(destination);

		if (startNode == null)
			return new Nothing<>();

		// Unpack parameters: search algorithm, heuristic and distance functions
		Function2<Node<A>, Node<A>, Float> h = params.getHeuristic();
		Function2<Node<A>, Node<A>, Float> d = params.getDistanceFn(); // Edge weighting
		SearchAlgorithm alg = params.getAlgorithm();
		
		// Declare explored, pending, predecessors and cost tracking collections (cost from the origin along best known path)
		Set<Node<A>> explored = new HashSet<>();
		Takeable<Node<A>> pending = initFrontier(alg);
		Map<Node<A>, Node<A>> pred = new LinkedHashMap<>();
		Map<Node<A>, Float> D = new LinkedHashMap<>();
		
		// Initialise collections
		pending.add(startNode);
		D.put(startNode, 0f);
		startNode.setF(h.apply(startNode, destinationNode));
		
		// Start search
		while (!pending.isEmpty()) {
			// Get node according to the data structure
			Node<A> n = pending.take();
			
			trackingData.add(new IntermediateState<>(n, explored, pending));
			
			// If it is the destination node then
			if (n.equals(destinationNode))
				// construct path from the map tracking and the start node
				return new Just<>(trackingData);
			
			// Add n to the explored set
			explored.add(n);
			
			// Loop through all successors of n that haven't been visited yet
			for (Node<A> s : n.getSuccessors()) {
				// If successor has not been visited yet
				if (!explored.contains(s)) {
					float cost = D.get(n) + d.apply(n, s);
					boolean inPending = pending.contains(s); // Without computing it 2 times
					
					// If s is not in the frontier or found a better path
					if (!inPending || cost < D.get(s)) {
						// Update predecessor, actual and estimated cost
						pred.put(s, n);
						D.put(s, cost);
						s.setF(D.get(s) + h.apply(s, destinationNode));
						
						if (!inPending)
							pending.add(s);
					}
				}
			}
		}
		
		// No path found
		return new Nothing<>();
	}

	/**
	 * Does BFS - note does not take into account costs as that would be dikstra's
	 * @param origin
	 * @param destination
     * @return
     */
	public Maybe<List<Node<A>>> findPathBFS (A origin, A destination) {
		Node<A> startNode = nodes.get(origin);
		Node<A> destinationNode = new Node<>(destination);

		if (startNode == null)
			return new Nothing<>();

		Set<Node<A>> explored = new HashSet<>();
		Takeable<Node<A>> pending = new LinkedListT<>();
		Map<Node<A>, Node<A>> pred = new LinkedHashMap<>();

		pending.add(startNode);

		while (!pending.isEmpty()) {
			Node<A> n = pending.take();

			if (n.equals(destinationNode))
				return new Just<>(constructPath(pred, startNode, n));

			explored.add(n);

			Object[] arr = n.getSuccessors().toArray();
			arr = findLowestCost(arr);
			for (int i=0; i<arr.length; i++) {
				Node<A> s = (Node<A>) arr[i];
				if (!explored.contains(s)) {
					boolean inPending = pending.contains(s);

					if (!inPending) {
						pred.put(s, n);
						pending.add(s);
					}
				}
			}
		}

		return new Nothing<>();
	}

	/**
	 * Does DFS with costs - IE smaller nodes are expanded first
	 * @param origin
	 * @param destination
	 * @return
	 */
	public Maybe<List<Node<A>>> findPathDFSwithCosts (A origin, A destination) {
		Node<A> startNode = nodes.get(origin);
		Node<A> destinationNode = new Node<>(destination);

		if (startNode == null)
			return new Nothing<>();

		Set<Node<A>> explored = new HashSet<>();
		Takeable<Node<A>> pending = new StackT<>();
		Map<Node<A>, Node<A>> pred = new LinkedHashMap<>();

		pending.add(startNode);

		while (!pending.isEmpty()) {
			Node<A> n = pending.take();

			if (n.equals(destinationNode))
				return new Just<>(constructPath(pred, startNode, n));

			explored.add(n);

			Object[] arr = n.getSuccessors().toArray();
			arr = findLowestCost(arr);
			for (int i=0; i<arr.length; i++) {
				Node<A> s = (Node<A>) arr[i];
				if (!explored.contains(s)) {
					boolean inPending = pending.contains(s);

					if (!inPending) {
						pred.put(s, n);
						pending.add(s);
					}
				}
			}
		}

		return new Nothing<>();
	}

	private Object[] findLowestCost(Object[] arrTemp) {
		int n = arrTemp.length;

		boolean swapped = true;

		while (swapped) {
			swapped = false;
			for (int i = 1; i < n; i++) {
				Node<A> a = (Node<A>) arrTemp[i];
				Node<A> b = (Node<A>) arrTemp[i - 1];
				if (b.getExtraCost() < a.getExtraCost()) {
					arrTemp[i] = b;
					arrTemp[i-1] = a;
					swapped = true;
				}
			}
		}

		return arrTemp;
	}

	@Override
	public String toString() {
		String str = "";
		
		for (Entry<A, Node<A>> entry : nodes.entrySet()) {
			str += entry.getKey() + " -- " + entry.getValue().getSuccessors() + "\n";
		}
		
		return str;
	}
}
