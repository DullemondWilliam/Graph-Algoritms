package shortestpath;

import java.util.ArrayList;

import comp2402graphEditor.Edge;
import comp2402graphEditor.Node;

public abstract class ShortestPath {
	
	public abstract ArrayList<Edge>	findShortestPath(Node start, Node end, ArrayList<Edge> graph);

	
}
