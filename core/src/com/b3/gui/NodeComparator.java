package com.b3.gui;

import com.b3.search.Node;
import com.b3.search.Point;

import java.util.Comparator;

/**
 * A comparator to compare two {@link Node Nodes} by x
 * coordinate then y coordinate.
 */
public class NodeComparator implements Comparator<Node<Point>> {

	@Override
	public int compare(Node<Point> n1, Node<Point> n2) {
		Point p1 = n1.getContent(),
				p2 = n2.getContent();
		if (p1.getX() == p2.getX()) {
			return p1.getY() - p2.getY();
		}
		return p1.getX() - p2.getX();
	}

}
