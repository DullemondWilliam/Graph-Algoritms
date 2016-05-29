package shortestpath;

import java.util.ArrayList;

import comp2402graphEditor.Node;

public class Algorithms {
	public static Node smallestDistance(ArrayList<Node> a) {
		if (a == null) {
			return null;
		}
		Node smallestNode = null;

		for (Node n : a) {
			if (smallestNode == null || n.getDistance() < smallestNode.getDistance()) {
				smallestNode = n;
			}
		}
		return smallestNode;
	}
}
