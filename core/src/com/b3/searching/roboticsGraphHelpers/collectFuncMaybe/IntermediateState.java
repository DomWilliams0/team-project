package com.b3.searching.roboticsGraphHelpers.collectFuncMaybe;

import com.b3.searching.roboticsGraphHelpers.Node;

import java.util.Collection;
import java.util.Set;

public class IntermediateState<A> {

	Node<A> currentNode;
	Set<Node<A>> explored;
	Collection<Node<A>> pending;
	
	public IntermediateState() {}
	
	public IntermediateState(Node<A> currentNode, Set<Node<A>> explored, Takeable<Node<A>> pending) {
		this.currentNode = currentNode;
		this.explored = explored;
		this.pending = pending;
	}
	
}