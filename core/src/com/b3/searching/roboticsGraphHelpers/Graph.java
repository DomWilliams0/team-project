package com.b3.searching.roboticsGraphHelpers;

import src_helpers.util.Point;
import src_helpers.util.ReadFile;
import src_helpers.util.collections.LinkedListT;
import src_helpers.util.collections.PriorityQueueT;
import src_helpers.util.collections.StackT;
import src_helpers.util.collections.Takeable;
import src_helpers.util.functions.Function2;
import src_helpers.util.maybe.Just;
import src_helpers.util.maybe.Maybe;
import src_helpers.util.maybe.Nothing;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

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
	public Node<A> addNode(A c) {
		Node<A> node;
		
		if (!nodes.containsKey(c)) {
			node = new Node<>(c);
			nodes.put(c, node);
		}
		else
			node = nodes.get(c);
		
		return node;
	}
	
	/**
	 * Removes a specified node
	 * @param c The node's content to be removed
	 * @return True if the node is in the table, false otherwise
	 */
	public boolean removeNode(A c) {
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
	public boolean hasEdge(A c1, A c2) {
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
	public void addEdge(A c1, A c2, boolean directed, float cost1, float cost2) {
		// Add nodes (if not existing) and get nodes from adjacency list
		Node<A> node1 = addNode(c1);
		Node<A> node2 = addNode(c2);

		node1.setExtraCost(cost1);
		node2.setExtraCost(cost2);
		
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
	 * Creates a new graph from a file
	 * @param filename The file name
	 * @return The newly created graph
	 */
	public static Graph<Point> fromFile(String filename) {
		// Open file and read lines
		ReadFile rf = new ReadFile(filename);

		System.out.println("POOOO");

		String[] lines;
		try {
			lines = rf.readLines();
		} catch (IOException e) {
			return null;
		}
		
		// Create graph
		Graph<Point> g = new Graph<>();
		
		// Loop through file lines
		for (String line : lines) {
			String[] parts = line.split(":");
			String nodeStr = parts[0];
			String neighboursStr = parts[1].substring(1); // Without initial space
			
			String[] coords = nodeStr.replaceAll("[()]", "").split(",");
			Point p = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

			// Add neighbours
			if (!neighboursStr.isEmpty()) {
				String[] neighbours = neighboursStr.split(" ");
				
				for (String neighbour : neighbours) {
					String[] coordsNeighbour = neighbour.replaceAll("[()]", "").split(",");
					Point pn = new Point(Integer.parseInt(coordsNeighbour[0]), Integer.parseInt(coordsNeighbour[1]));
					
					// Add edge between p and pn
					g.addEdge(p, pn, true, 0, 0);
				}
			}
			else
				g.addNode(p).setExtraCost(10);

		}

		return g;
	}
	
	/**
	 * Find a node from a starting node's content and ending node's content
	 * @param origin The content of the node at the start
	 * @param destination The content of the node at the end
	 * @param params Search parameters
	 * @return Maybe a node
	 */
	public Maybe<Node<A>> findNodeFrom(A origin, A destination, SearchParameters<A> params) {
		// Get path
		ArrayList<Node<A>> path = (ArrayList<Node<A>>)findPathFrom(origin, destination, params).fromMaybe();
		
		// Return last item if there is a path
		return path == null ?
				new Nothing<>() :
				new Just<>(path.get(path.size() - 1));
	}
	
	/**
	 * Find a path from the origin to the destination given search parameters
	 * @param origin The content of the starting node
	 * @param destination The content of the destination node
	 * @param params Search parameters (algorithm, heuristic and distance functions)
	 * @return Maybe a path
	 */
	public Maybe<List<Node<A>>> findPathFrom(A origin, A destination, SearchParameters<A> params) {
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
					float cost = D.get(n) + d.apply(n, s) - s.getExtraCost();
					//TODO implement extra cost into graph search
					//Can get extra cost from right here
					System.out.println("Overall Cost: " + cost + "; Current cost of this node: " + s.getExtraCost() + "; D.get(n): " + D.get(n) + "; d.apply(n,s): " + d.apply(n, s));
					boolean inPending = pending.contains(s); // Without computing it 2 times
					
					// If s is not in the frontier or found a better path
					if (!inPending || cost < D.get(s)) {
						// Update predecessor, actual and estimated cost
						pred.put(s, n);
						D.put(s, cost);
						s.setF(D.get(s) + h.apply(s, destinationNode));
						
						if (!inPending)
							pending.add(s);
					} else System.out.println("NOT GOING THERE");
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
	
	@Override
	public String toString() {
		String str = "";
		
		for (Entry<A, Node<A>> entry : nodes.entrySet()) {
			str += entry.getKey() + " -- " + entry.getValue().getSuccessors() + "\n";
		}
		
		return str;
	}
}
