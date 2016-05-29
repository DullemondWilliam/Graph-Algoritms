package shortestpath;

import java.util.ArrayList;

import comp2402graphEditor.Edge;
import comp2402graphEditor.Graph;
import comp2402graphEditor.Node;

public class Dijkstras extends ShortestPath {

	@Override
	public ArrayList<Edge> findShortestPath(Node start, Node finish, ArrayList<Edge> graph) {
		ArrayList<Node> marked	= new ArrayList<Node>();

		start.setPreviousEdge(null); // there is not previous node to visit
		Node currentNode = start;

		while (currentNode != finish) {
			currentNode.setVisited(true);
			for (Node n : currentNode.neighbours()) {
				if (n.getVisited() == false) {
					n.setMarked(true);
					marked.add(n);

					Edge connectingEdge = Graph.edgeBetween(currentNode, n);// find
																		// corresponding
																		// edge
					int edgeLen = connectingEdge.getWeight();
					// set distance if it is greater then where we have come
					// reset
					if (n.getDistance() > currentNode.getDistance() + edgeLen) {
						n.setDistance(currentNode.getDistance() + edgeLen);
						n.setPreviousEdge(connectingEdge);
					}
				}
			}
			marked.remove(currentNode);
			currentNode = B (marked);
			if (currentNode == null) {
				return null;
			}
		}

		// Retrace the path using the previous edges recorded and return it
		ArrayList<Edge> thePath = new ArrayList<Edge>();
		Node someNode = currentNode;
		while (someNode != start) {
			Edge pathEdge = someNode.getPreviousEdge();
			pathEdge.setSelected(true);
			thePath.add(0, pathEdge); // prepend the edge to the path
			someNode = pathEdge.otherNode(someNode);
		}
		return thePath;
	}

	
}
