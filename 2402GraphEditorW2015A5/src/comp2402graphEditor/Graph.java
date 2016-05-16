package comp2402graphEditor;

import java.util.*;
import java.awt.*;
import java.io.*;

public class Graph {
	// PARSING VARIALBLES=====================================
	// Tags used for XML like export of graph objects
	final public static String startTag = "<graph>";
	final public static String endTag = "</graph>";

	final public static String XMLCommentTag = "<!--";
	final public static String XMLCommentEndTag = "-->";

	final public static String XMLBackgroundImageStartTag = "<image>";
	final public static String XMLBackgroundImageEndTag = "</image>";
	final public static String XMLFileNameStartTag = "<filename>";
	final public static String XMLFileNameEndTag = "</filename>";
	final public static String XMLDimensionsStartTag = "<dimensions>";
	final public static String XMLDimensionsEndTag = "</dimensions>";

	public static String[] propertyTagPairs = { XMLCommentTag, XMLCommentEndTag, XMLBackgroundImageStartTag,
			XMLBackgroundImageEndTag, XMLFileNameStartTag, XMLFileNameEndTag, XMLDimensionsStartTag,
			XMLDimensionsEndTag };
	public static String[] singletonPropertyTags = {};

	// Colors for selected and marked items
	public static Color NORMAL_COLOR = new Color(220, 220, 220); // light gray
	public static Color NORMAL_EDGE_COLOR = new Color(66, 66, 66); // gray

	public static Color SELECTED_COLOR = Color.red;
	public static Color MARKED_COLOR = new Color(100, 149, 237); // cornflower
																	// blue

	// This is the main data structure that represents an undirected graph
	// These are the instance variables

	private ArrayList<Node> nodes; // The nodes of the graph
	private ArrayList<Edge> edges; // The edges of the graph

	// info about background image that accompanies graph
	// (some graphs are models based on a background images, like a map)
	private String backgroundImageFileName = null;
	private int backgroundImageWidth = -1;
	private int backgroundImageHeight = -1;

	private GraphEditor owner = null; // editor which is currently editing this graph

	Random rand = new Random(); // used by algorithms that need a random number

	// Global Values to be used by sub-graph enumeration algorithms

	public static enum PathFindingMethod {
		Dijkstra, AStarLowerBound, AStarManhattan, AStarZeroCostHeuristic, AStarBestFirstGreedy, GreedyBestNeighbour
	}

	public static String nameOfMethod(PathFindingMethod aPathFindingMethod) {
		if (aPathFindingMethod == PathFindingMethod.Dijkstra)
			return "Dijkstra";
		else if (aPathFindingMethod == PathFindingMethod.AStarLowerBound)
			return "AStarLowerBound";
		else if (aPathFindingMethod == PathFindingMethod.AStarManhattan)
			return "AStarManhattan";
		else if (aPathFindingMethod == PathFindingMethod.AStarZeroCostHeuristic)
			return "AStarZeroCostHeuristic";
		else if (aPathFindingMethod == PathFindingMethod.AStarBestFirstGreedy)
			return "AStarBestFirstGreedy";
		else if (aPathFindingMethod == PathFindingMethod.GreedyBestNeighbour)
			return "GreedyBestNeighbour";
		return "UNKNOWN";

	}

	// helper method to determine distance between two points
	public static double distanceBetween(Point a, Point b){
		return Math.abs(Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow((a.getY() - b.getY()), 2)));
	}
	
	// helper method to determine distance between two nodes
	public static double distanceBetween(Node n1, Node n2) {
		return Math.sqrt((n2.getLocation().x - n1.getLocation().x) * (n2.getLocation().x 
				- n1.getLocation().x)+ (n2.getLocation().y - n1.getLocation().y) 
				* (n2.getLocation().y - n1.getLocation().y));
	}

	public Graph() {
		nodes = new ArrayList<Node>(); // nodes of the graph
		edges = new ArrayList<Edge>(); // edges of the graph
	}

	public static double infinity() {
		return Double.POSITIVE_INFINITY;
	}

	public void setBackgroundImageInfo(String anImageFileName, int width, int height) {
		backgroundImageFileName = anImageFileName;
		backgroundImageWidth = width;
		backgroundImageHeight = height;
	}

	public ArrayList<Node> getNodes() { return nodes;}

	// number the nodes with integers beginning with start
	public void numberNodesFrom(int start) {
		for (int i = 0; i < nodes.size(); i++){
			nodes.get(i).setLabel(i);
		}
	}


	// return a copy of the nodes List for use with destructive algorithms
	// Note: the copy contains references to the original nodes
	// (the nodes themselves are not copies)
	public ArrayList<Node> copyOfNodes() {return new ArrayList<Node>(nodes);}

	// return a copy of the edge list for use with destructive algorithms.
	// Note: the copy contains references to the original edges
	// (the edges themselves are not copies)
	public ArrayList<Edge> copyOfEdges() {
		return new ArrayList<Edge>(edges);
	}

	// return a random node of the graph
	public Node randomNode() {
		return nodes.get(rand.nextInt(nodes.size()));
	}

	// answer the edgelist of the graph
	public ArrayList<Edge> getEdges() {return edges;}
	
	// Graphs look like this: label(6 nodes, 15 edges)
	@Override
	public String toString() {
		return ("GRAPH: " + "(" + nodes.size() + " nodes, " + edges.size() + " edges)");
	}

	// Add a node to the graph
	public void addNode(Node aNode) {nodes.add(aNode);}

	// Add an edge to the graph between two given nodes
	public void addEdge(Node start, Node end) {
		Edge anEdge = new Edge(start, end);
		addEdge(anEdge);
	}

	// Add an Edge object to the graph if it does not contain it
	// Used by algorithms that want to remove an edge from the graph
	// and the replace it
	public void addEdge(Edge e) {
		// ASSUMPTION: edge e came from the graph and its end nodes are still
		// part of the graph
		if (!(e.getStartNode()).isConnectedTo(e.getEndNode())) {
			// Now tell the nodes about the edge
			e.getStartNode().addIncidentEdge(e);
			e.getEndNode().addIncidentEdge(e);
			edges.add(e);
		}
	}

	// Add an edge to the graph between two nodes with the given labels
	public void addEdge(String startLabel, String endLabel) {
		Node start, end;

		start = nodeNamed(startLabel);
		end = nodeNamed(endLabel);
		if ((start != null) && (end != null)){
			addEdge(start, end);
		}
	}

	// Remove an edge from the receiver
	public void deleteEdge(Edge anEdge) {
		// Just ask the nodes to remove it
		if (anEdge == null){
			return;
		}
		anEdge.getStartNode().deleteIncidentEdge(anEdge);
		anEdge.getEndNode().deleteIncidentEdge(anEdge);
		edges.remove(anEdge);
	}

	// Remove a node from the receiver
	public void deleteNode(Node aNode) {
		// Remove the opposite node's incident edges
		for (Edge anEdge : aNode.incidentEdges()) {
			anEdge.otherEndFrom(aNode).deleteIncidentEdge(anEdge);
			edges.remove(anEdge);
		}
		// Remove the node now
		nodes.remove(aNode);
	}

	// Return the node with the given label, null if there is none
	public Node nodeNamed(String aLabel) {
		for (int i = 0; i < nodes.size(); i++) {
			Node aNode = nodes.get(i);
			if (aNode.getLabel().equals(aLabel)){
				return aNode;
			}
		}
		// If we don't find one, return null
		return null;
	}

	// Return the node that contains point p if one exists
	public Node nodeAt(Point p) {
		for (int i = 0; i < nodes.size(); i++) {
			Node aNode = nodes.get(i);
			int distance = (p.x - aNode.getLocation().x) * (p.x - aNode.getLocation().x)
					+ (p.y - aNode.getLocation().y) * (p.y - aNode.getLocation().y);
			if (distance <= (Node.RADIUS * Node.RADIUS))
				return aNode;
		}
		return null;
	}

	// Return the node whose location is at the specified Point
	public Node nodeWithLocation(Point p) {
		for (int i = 0; i < nodes.size(); i++) {
			Node aNode = nodes.get(i);
			if (aNode.getLocation().x == p.x && aNode.getLocation().y == p.y){
				return aNode;
			}
		}
		return null;
	}

	// Answer an edge that is sufficiently close to a Point p
	// used to select edges with the mouse
	public Edge edgeAt(Point p) {
		/*
		 * This code basically gets the distance between the start node of the
		 * edge and the point on the screen selected as well as the distance
		 * between the end node of the edge and that point. After that, I add
		 * these two values together and I subtract the distance between the 2
		 * nodes. If the user clicks sufficiently close to the edge (in my code
		 * I made it a distance smaller than 1) then that edge is then selected.
		 * 
		 * Thanks to: Adrian Batos-Parac for this method
		 */
		for (int i = 0; i < edges.size(); i++) {
			Edge anEdge = edges.get(i);
			double distanceToStart = Graph.distanceBetween(p, anEdge.getStartNode().getLocation());
			double distanceToEnd = Graph.distanceBetween(p, anEdge.getEndNode().getLocation());
			double totalChange = (distanceToStart + distanceToEnd)
					- (Graph.distanceBetween(anEdge.getEndNode().getLocation(), anEdge.getStartNode().getLocation()));

			if (totalChange < 1)
				return anEdge;
		}
		return null;
	}

	// Get the edge that connects two nodes, null otherwise
	public Edge edgeBetween(Node n1, Node n2) {
		for (Edge e : n1.incidentEdges()) {// find corresponding edge
			if (e.connects(n1, n2)) {
				return e;
			}
		}
		return null;
	}

	// Get the edge go between two node partitions
	public ArrayList<Edge> edgesBetween(ArrayList<Node> nodes1, ArrayList<Node> nodes2) {
		// ASSUMPTION: nodes1 and nodes2 are disjoint

		ArrayList<Edge> partitionEdges = new ArrayList<Edge>();

		for (Edge anEdge : edges) {
			Node startNode = anEdge.getStartNode();
			Node endNode = anEdge.getEndNode();

			if ((nodes1.contains(startNode)) && (nodes2.contains(endNode))){
				partitionEdges.add(anEdge);
			}else if ((nodes1.contains(endNode)) && (nodes2.contains(startNode))){
				partitionEdges.add(anEdge);
			}
		}
		return partitionEdges;
	}

	// Gets all the nodes that are selected
	public ArrayList<Node> selectedNodes() {
		ArrayList<Node> selected = new ArrayList<Node>();

		for (Node aNode : nodes) {
			if (aNode.isSelected())
				selected.add(aNode);
		}
		return selected;
	}

	// Gets all the edges that are selected
	public ArrayList<Edge> selectedEdges() {
		ArrayList<Edge> selected = new ArrayList<Edge>();

		for (Edge anEdge : edges) {
			if (anEdge.isSelected())
				selected.add(anEdge);
		}
		return selected;
	}

	public void clearSelections() {
		for (Edge anEdge : edges) {
			anEdge.setSelected(false);
			anEdge.setMarked(false);
		}

		for (Node aNode : nodes) {
			aNode.setSelected(false);
			aNode.setMarked(false);
		}
	}

	public void clearEdgeSelections() {
		for (Edge anEdge : selectedEdges()) {
			anEdge.toggleSelected();
		}
	}

	public void selectNodesInArea(Rectangle boundingBox) {
		// Mark those nodes that fall within the boundingBox as selected
		// otherwise mark as unselected

		for (Node node : nodes) {
			node.setSelected(boundingBox.contains(node.getLocation()));
		}
	}

	public void addNodesInAreaToSelection(Rectangle boundingBox) {
		// Mark any nodes that fall within the boundingBox as selected
		for (Node node : nodes) {
			if (boundingBox.contains(node.getLocation())) {
				node.setSelected(true);
			}
		}
	}

	// Complete the graph to a Kn by adding any missing edges between nodes
	public void completeGraph() {
		for (Node aNode1 : nodes) {
			for (Node aNode2 : nodes) {
				if (edgeBetween(aNode1, aNode2) == null) {
					if (aNode1 != aNode2)
						addEdge(aNode1, aNode2);
				}
			}
		}
	}

	public void doAnimationStep() {
		for (Node aNode : nodes) {
			aNode.moveNodeOneStep();
		}

	}

	// Relocate nodes randomly
	public void randomNodeLocation(int width, int height) {
		for (Node aNode : nodes) {
			int randomX = rand.nextInt(width);
			int randomY = rand.nextInt(height);
			Point newLocation = new Point(randomX, randomY);
			if (GraphEditor.displayAnimation)
				aNode.setDeltaForLocation(newLocation, GraphEditor.AnimationSteps);
			else
				aNode.setLocation(newLocation);
		}

	}

	// ALGORITHMS
	public int numberOfEdgeCrossings() {
		// count the number of edges in the graph that currently cross each other
		int count = 0;

		for (Edge anEdge : edges)
			for (Edge anEdge2 : edges) {
				if (anEdge != anEdge2) {
					Point p = anEdge.intersects(anEdge2);
					if (p != null) {
						count++;
					}
				}
			}
		return count / 2;
	}
	// PATH FINDING
	// MINIMUM COST SPANNING TREE
	public void findMinimumCostSpanningTree() {
		// find, and select the edges that form a minimum cost spanning tree
		// Create node partitions for Prim-Jarnik Algorithm
		ArrayList<Node> processedNodes = new ArrayList<Node>();
		ArrayList<Node> remainingNodes = copyOfNodes();
		if (remainingNodes.size() <= 1)
			return; // no tree possible

		// partition the nodes into a partition of 1 node and the remaining
		// nodes
		Node startNode = remainingNodes.remove(0);
		startNode.setSelected(true);
		processedNodes.add(startNode);

		// clear all selected items in the graph
		clearSelections();

		findMinimumCostSpanningTree(processedNodes, remainingNodes);
	}

	public void findMinimumCostSpanningTree(ArrayList<Node> processedNodes, ArrayList<Node> remainingNodes) {
		// Recursive implementation of Prim-Jarnik Algorithm
		// Find a minimum cost spanning tree recursively by finding the lowest
		// cost edge
		// that connects the partial tree represened by the processed nodes to
		// one
		// of the remaining nodes

		// Basis Case
		if (remainingNodes.isEmpty())
			return;

		// Recursive Case
		ArrayList<Edge> crossEdges = edgesBetween(processedNodes, remainingNodes);

		if (crossEdges.size() < 1)
			return; // graph is disconnected

		Edge minimumCostEdge = Edge.removeMinimumWeightEdge(crossEdges); // good
																			// place
																			// for
																			// priority
																			// queue
		minimumCostEdge.setSelected(true);

		// Move the connected node to the processed partition
		Node startNode = minimumCostEdge.getStartNode();
		Node endNode = minimumCostEdge.getEndNode();
		if (remainingNodes.contains(startNode)) {
			remainingNodes.remove(startNode);
			processedNodes.add(startNode);

		} else if (remainingNodes.contains(endNode)) {
			remainingNodes.remove(endNode);
			processedNodes.add(endNode);
		}

		findMinimumCostSpanningTree(processedNodes, remainingNodes); // recurse
	}

	// CONNECTIVITY AND TRAVERSAL
	private boolean hasPathFrom(Node source, Node target) {
		// WARNING: don't call this method directly, call via the hasSTPath()
		// method
		// Answer true if there is a path from the source node to the target
		// node. Answer false if no path exists (source and target are
		// disconnected)

		// Basis case
		if (source == target)
			return true; // we have reached the target
		// Recursive case
		// mark the node as visited
		source.setVisited(true);
		source.setLabel(Node.counter++);

		// update its neighbours distances
		for (Node neighbour : source.neighbours()) {
			if (neighbour.getVisited() == false) {
				if (hasPathFrom(neighbour, target))
					return true;
			}
		}
		return false;
	}

	public boolean hasSTPath() {
		// answer whether the two selected nodes in the graph are connected
		ArrayList<Node> selectedNodes = selectedNodes();
		if (selectedNodes.size() == 2) {
			Node source = selectedNodes.get(0);
			Node target = selectedNodes.get(1);

			for (Node aNode : getNodes()) {
				aNode.setVisited(false);
				aNode.setLabel("");
			}
			Node.counter = 1;
			return hasPathFrom(source, target);
		}
		return false;
	}

	public void depthFirstSearch(Node source, int level_counter) {
		// Perform a depth first search from the source node
		// label the nodes in order the are seen
		// label with recursion level and sibling order
		// mark the node as visited

		source.setVisited(true);
		source.setLabel("" + (Node.counter++) + "[" + level_counter + "]");
		// update its neighbours distances
		for (Node neighbour : source.neighbours()) {
			if (neighbour.getVisited() == false) {
				depthFirstSearch(neighbour, level_counter + 1);
				edgeBetween(source, neighbour).setSelected(true); // select the
																	// forward
																	// edges
			} else
				edgeBetween(source, neighbour).setMarked(true); // mark the back
																// edges
		}
	}

	public void depthFirstSearch() {
		// search from a selected node
		ArrayList<Node> selectedNodes = selectedNodes();
		if (nodes.size() < 1)
			return;
		Node source = null;
		if (selectedNodes.size() >= 1)
			source = selectedNodes.get(0);
		else
			source = nodes.get(0);

		for (Node aNode : getNodes()) {
			aNode.setVisited(false);
			aNode.setLabel("");
		}
		clearEdgeSelections();

		Node.counter = 1;
		depthFirstSearch(source, 0);
	}

	public void breadthFirstSearch(ArrayList<Node> nodesToSearch, int level_counter) {
		// Perform a breadth first search from the source node
		// label the nodes in order the are seen
		// label with recursion level and sibiling order

		// Basis case
		if (nodesToSearch.size() < 1)
			return;

		// Recursion
		for (Node aNode : nodesToSearch) {
			aNode.setLabel("" + (Node.counter++) + "[" + level_counter + "]");
		}

		ArrayList<Node> newNodesToSearch = new ArrayList<Node>(); // get nodes
																	// for next
																	// level to
																	// search
		for (Node aNode : nodesToSearch) {
			for (Node neighbour : aNode.neighbours())
				if (neighbour.getVisited() == false) {
					newNodesToSearch.add(neighbour);
					neighbour.setVisited(true);
					edgeBetween(aNode, neighbour).setSelected(true);
				} else
					edgeBetween(aNode, neighbour).setMarked(true);
		}

		breadthFirstSearch(newNodesToSearch, level_counter + 1);
	}

	public void breadthFirstSearch() {
		// search from a selected node
		ArrayList<Node> selectedNodes = selectedNodes();
		if (nodes.size() < 1)
			return;
		Node source = null;
		if (selectedNodes.size() >= 1)
			source = selectedNodes.get(0);
		else
			source = nodes.get(0);

		// reset node labels
		for (Node aNode : getNodes()) {
			aNode.setVisited(false);
			aNode.setLabel("");
		}

		clearEdgeSelections();

		Node.counter = 1; // reset global counter
		ArrayList<Node> nodesToSearch = new ArrayList<Node>();
		source.setVisited(true);
		nodesToSearch.add(source);
		breadthFirstSearch(nodesToSearch, 0);
	}

	public void maxFlow() {
		// This method determines the maximum flow between the source node and
		// target node.
		// The edge weights are used as the maximum capacity that an edge can
		// carry.
		// The flow and direction variables in the edges are used to label the
		// amount of flow each
		// edge is carrying.

		// This method also displays the amount that on of the bottleneck edge's
		// capacity should be increased
		// to maximally increase the flow.
		System.out.println("Max Flow: To Be Developed");
	}

	// DRAW METHODS TO DISPLAY THE GRAPH==============================================
	// Draw the receiver with the given Graphics object
	public void draw(Graphics2D aPen) {
		// Draw the edges first
		for (int i = 0; i < edges.size(); i++)
			edges.get(i).draw(aPen, GraphEditor.getDisplayEdgeWeights());

		// Draw the nodes now
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).draw(aPen, GraphEditor.getDisplayNodeLabels());
			if (GraphEditor.getDisplayNodeLabels())
				nodes.get(i).drawNodeLabel(aPen);
		}

		aPen.drawString("Edge Crossings:" + numberOfEdgeCrossings(), 30, 30);
		// display the total weight of selected items.
		double weight = 0;

		for (Edge e : this.getEdges()) {
			if (e.isSelected())
				weight += e.getWeight();
		}

		int intWeight = (int) weight;
		if (intWeight > 0)
			aPen.drawString("Selected Edge(s) Weight:" + intWeight, 30, 50);

		for (Node n : this.getNodes()) {
			if (n.isSelected())
				weight += n.getDistance();
		}

		intWeight = (int) weight;
		if (intWeight > 0)
			aPen.drawString("Selected Node + Edges Weight:" + intWeight, 30, 70);
	}

	// PARSING METHODS
	// Methods for parsing graph from data file
	public void setProperty(String propertyTagString) {
		// set a singleton property described by propertyTagString
	}

	public void setProperty(String propertyTagString, String dataString) {
		// Assign data to the property identified by the propertyTagString
		if (dataString == null)
			return;
		// System.out.println("SETTING PROPERTY: " + propertyTagString + " " +
		// dataString);
	}
	// Parsing Methods
	// Parsing Methods
	public boolean parseMultiLinePropertyString(String aString, BufferedReader inputFile) {
		// If the string aString is a tag for a property that could be split
		// over
		// many lines, BUT DOES NOT CONTAIN nested structures
		// then parse it and set the property

		// Parse a string of the form

		// "<chartCommnents>
		// "these are some chart comments"
		// "these are some more comments"
		// </chartCommnents>"

		// Note in the loop below the propertyTagPairs array is assumed to be of
		// the form
		// <startTag>, <endTag>
		// that is why the /2 in the loop
		for (int i = 0; i < propertyTagPairs.length / 2; i++) {
			String beginTag = propertyTagPairs[i * 2];
			String endTag = propertyTagPairs[(i * 2) + 1];
			if (aString.equals(beginTag)) {

				String inputLine; // current input line
				StringBuffer dataBuffer = new StringBuffer();
				try {
					while (!(inputLine = inputFile.readLine().trim()).startsWith(endTag)) {
						dataBuffer.append(inputLine).append("\n");
					}
				} catch (EOFException e) {
					System.out.println("VERSION PARSE Error: EOF encountered, file may be corrupted.");
					return false;
				} catch (IOException e) {
					System.out.println("VERSION PARSE Error: Cannot read from file.");
					return false;
				}
				// should have seen the end tag
				setProperty(beginTag, dataBuffer.toString());
				return true;
			}
		}
		return false;
	}

	public boolean parsePropertyString(String aString) {
		// If the string aString is a tag-delimited property then set that
		// property and
		// return true, otherwise return false
		// Parse a string of the form

		// "<chartTitle> April in the Spring </chartTitle>"

		// Note in the loop below the propertyTagPairs array is assumed to be of
		// the form
		// <startTag>, <endTag>
		// that is why the /2 in the loop

		for (int i = 0; i < propertyTagPairs.length / 2; i++) {
			String beginTag = propertyTagPairs[i * 2];
			String endTag = propertyTagPairs[(i * 2) + 1];
			if ((aString.startsWith(beginTag)) && (aString.endsWith(endTag))) {
				setProperty(beginTag,
						aString.substring((beginTag.length()), (aString.length() - endTag.length() - 1)).trim());
				return true;
			}
		}
		return false;
	}

	public boolean parseSingletonPropertyString(String aString) {
		// If the string aString is a singleton tag property then set that
		// property and
		// return true, otherwise return false
		// Parse a string of the form

		// "<isRest/>"

		// Note in the loop below the propertyTagPairs array is assumed to be of
		// the form
		// <startTag>, <endTag>, "comment",
		// that is why the /2 in the loop

		for (int i = 0; i < singletonPropertyTags.length; i++) {
			String singletonTag = singletonPropertyTags[i];
			if (aString.startsWith(singletonTag)) {
				setProperty(singletonTag);
				return true;
			}
		}
		return false;
	}

	public void writeToFile(String baseIndent, PrintWriter outputFile) {
		// Write the chart to a file in XML style tag-delimited data
		String tab = "   ";
		String indent = baseIndent + tab;

		// write class start tag
		outputFile.println(baseIndent + startTag);

		if (backgroundImageFileName != null && !backgroundImageFileName.isEmpty()) {
			// write background image file information with graph
			outputFile.println(indent + XMLBackgroundImageStartTag);

			String propertyString;
			propertyString = XMLFileNameStartTag + backgroundImageFileName + XMLFileNameEndTag;
			outputFile.println(indent + tab + propertyString);
			propertyString = XMLDimensionsStartTag + backgroundImageWidth + "," + backgroundImageHeight
					+ XMLDimensionsEndTag;
			outputFile.println(indent + tab + propertyString);

			outputFile.println(indent + XMLBackgroundImageEndTag);
		}

		// Output the nodes
		for (int i = 0; i < nodes.size(); i++)
			nodes.get(i).writeToFile(indent, outputFile);

		// Output the edges
		for (int i = 0; i < edges.size(); i++)
			edges.get(i).writeToFile(indent, outputFile);

		// write class end tag
		outputFile.println(baseIndent + endTag);
	}

	public static Graph parseFromFile(String openingTag, BufferedReader inputFile) {
		// Parse in a graph from the XML input file
		// The opening tag is the first tag that was stripped of by and outer
		// level of
		// parsing

		// <graph>
		// ...
		// </graph>
		Graph parsedModel = new Graph();

		String inputLine; // current input line
		String dataString = null;

		try {
			// System.out.println("Graph Opening Tag:" + openingTag);
			// check that we have the right opening tag
			if (!openingTag.equalsIgnoreCase(startTag)) {
				System.out.println("ERROR: Graph Opening Tag:" + openingTag);
				return null; // no successful parse
			}

			// parse until we get to the closing tag
			while (!(inputLine = inputFile.readLine().trim()).startsWith(endTag)) {
				dataString = inputLine;
				if (dataString.length() == 0) {
					// do nothing, but allow for blank lines;
				} else if (dataString.startsWith(Graph.XMLBackgroundImageStartTag)) {
					// parse background image file data
					// DO NOTHING WITH THIS DATA FOR NOW
				} else if (dataString.startsWith(Node.startTag)) {
					// parse a staff
					Node node = Node.parseFromFile(Node.startTag, inputFile);
					if (node != null) {
						// System.out.println("Node");
						parsedModel.addNode(node);
					}
				} else if (dataString.startsWith(Edge.startTag)) {
					// parse a staff
					Edge edgeModel = Edge.parseFromFile(Edge.startTag, inputFile);
					if (edgeModel != null) {
						// System.out.println("Graph:: Edge parsed");
						// Replace the parsed temporary nodes with nodes from
						// the graph at the
						// same location.
						// This is because the nodes of the graph were not known
						// when the edge was
						// parsed in from the xml file, only the locations were
						// known

						Node graphStartNode = parsedModel.nodeWithLocation(edgeModel.getStartNode().getLocation());
						Node graphEndNode = parsedModel.nodeWithLocation(edgeModel.getEndNode().getLocation());
						edgeModel.setStartNode(graphStartNode);
						edgeModel.setEndNode(graphEndNode);
						parsedModel.addEdge(edgeModel);
					}
				}
				// see if the dataString is of the form "<tag> data </tag>"
				else if (parsedModel.parsePropertyString(dataString)) {
				}
				// see if the dataString is of the form "<tag/>
				else if (parsedModel.parseSingletonPropertyString(dataString)) {
				}
				// see if it is a multi-line property string -but not nested
				else if (parsedModel.parseMultiLinePropertyString(dataString, inputFile)) {
				}
			}
			// closing tag reached
			// catch file IO errors
		} catch (EOFException e) {
			System.out.println("VERSION PARSE Error: EOF encountered, file may be corrupted.");
			return null;
		} catch (IOException e) {
			System.out.println("VERSION PARSE Error: Cannot read from file.");
			return null;
		}
		return parsedModel;
	}

	public ArrayList<Edge> findPath(PathFindingMethod theMethod) {
		// find the path between two selected nodes and select its edges and
		// nodes
		// this method will only run if there are just two nodes selected in the
		// graph

		ArrayList<Node> selectedNodes = selectedNodes();
		if (selectedNodes.size() != 2)
			return null;

		Node source = selectedNodes.get(0);
		Node target = selectedNodes.get(1);

		return findPathBetween(source, target, theMethod);
	}

	public ArrayList<Edge> findPathBetween(Node source, Node target, PathFindingMethod theMethod) {
		// initialize the graph for path finding
		// Do any common initialization needed for all the path finding methods
		for (Node aNode : getNodes()) {
			aNode.setDistance(infinity()); // infinity
			aNode.setVisited(false);
			aNode.setPreviousEdge(null);
		}
		source.setDistance(0.0);
		clearEdgeSelections(); // clear any currently selected and marked edges
		// find the path using the appropriate method

		ArrayList<Edge> thePath = null;

		if (theMethod == PathFindingMethod.Dijkstra)
			thePath = findPathUsingDijstra(source, target);
		else if (theMethod == PathFindingMethod.AStarLowerBound)
			thePath = findPathUsingAStarLowerBound(source, target);
		else if (theMethod == PathFindingMethod.AStarManhattan)
			thePath = findPathUsingAStarManhattan(source, target);
		else if (theMethod == PathFindingMethod.AStarZeroCostHeuristic)
			thePath = findPathUsingAStarZeroCostHeuristic(source, target);
		else if (theMethod == PathFindingMethod.AStarBestFirstGreedy)
			thePath = findPathUsingAStarBestFirstGreedy(source, target);
		else if (theMethod == PathFindingMethod.GreedyBestNeighbour)
			thePath = findPathUsingGreedyHeuristic(source, target);

		// indicate the path by selecting the edges in the graph that form the
		// path

		int pathLength = 0;
		if (thePath != null) {
			for (Edge e : thePath) {
				e.setSelected(true);
				e.getStartNode().setSelected(true);
				e.getEndNode().setSelected(true);
				pathLength += e.getWeight();
			}
		}
		System.out.println("Path Length [" + Graph.nameOfMethod(theMethod) + "]: " + pathLength);
		return thePath;
	}

	public ArrayList<Edge> findPathUsingGreedyHeuristic(Node source, Node target) {
		// This
		// For illustration this method currently finds a path using an "As the
		// crow flies" greedy
		// Heuristic.
		// It simply chooses the next unvisited neighbour which is "as the crow
		// flies" closest to the target
		// Because of this simple greedy behaviour without backtracking, it can
		// dead end and get stuck
		// with nowhere to go

		// Run a greedy method to find the locally best node to visit next
		source.setPreviousEdge(null); // there is not previous node to visit

		Node currentNode = source;
		currentNode.setVisited(true);

		while (currentNode != target) {
			ArrayList<Node> neighbours = currentNode.neighbours();
			Node nextCandidate = null;
			double distance = infinity();
			for (Node n : neighbours) {
				// find neighbour which is unvisited and "as the crow flies"
				// closest to target
				n.setMarked(true); // mark node as having been examined by
									// algorithm
				if ((n.getVisited() == false) && Graph.distanceBetween(n, target) < distance) {
					distance = Graph.distanceBetween(n, target);
					nextCandidate = n;
				}
			}
			if (nextCandidate == null) {
				System.out.println("ERROR: Path Incomplete (we must have hit a dead end)");
				break; // break out of loop and report at least the partial path
			}
			Edge edgeToPrevious = edgeBetween(currentNode, nextCandidate);
			edgeToPrevious.setSelected(true);
			nextCandidate.setPreviousEdge(edgeToPrevious);
			nextCandidate.setVisited(true);
			currentNode = nextCandidate;
		}
		// Retrace the path using the previous edges recorded and return it
		ArrayList<Edge> thePath = new ArrayList<Edge>();
		Node someNode = currentNode;
		while (someNode != source) {
			Edge pathEdge = someNode.getPreviousEdge();
			thePath.add(0, pathEdge); // prepend the edge to the path
			someNode = pathEdge.otherEndFrom(someNode);
		}
		return thePath;
	}

	// Mine
	public Node smallestDistance(ArrayList<Node> a) {
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

	public ArrayList<Edge> findPathUsingDijstra(Node source, Node target) {
		ArrayList<Node> marked = new ArrayList<Node>();

		// initilize things
		System.out.println("Start Dijstra");
		source.setPreviousEdge(null); // there is not previous node to visit
		Node currentNode = source;

		while (currentNode != target) {
			currentNode.setVisited(true);
			for (Node n : currentNode.neighbours()) {
				if (n.getVisited() == false) {
					n.setMarked(true);
					marked.add(n);

					Edge connectingEdge = edgeBetween(currentNode, n);// find
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
			currentNode = smallestDistance(marked);
			if (currentNode == null) {
				return null;
			}
		}

		// Retrace the path using the previous edges recorded and return it
		ArrayList<Edge> thePath = new ArrayList<Edge>();
		Node someNode = currentNode;
		while (someNode != source) {
			Edge pathEdge = someNode.getPreviousEdge();
			pathEdge.setSelected(true);
			thePath.add(0, pathEdge); // prepend the edge to the path
			someNode = pathEdge.otherEndFrom(someNode);
		}
		return thePath;
	}

	public Node smallestDistanceAStar(ArrayList<Node> a, Node target) {
		if (a == null) {
			return null;
		}
		Node smallestNode = null;

		for (Node n : a) {
			if (smallestNode == null || n.getDistance() + distanceBetween(n, target) < smallestNode.getDistance()
					+ distanceBetween(smallestNode, target)) {
				smallestNode = n;
			}
		}

		return smallestNode;
	}

	public ArrayList<Edge> findPathUsingAStarLowerBound(Node source, Node target) {
		ArrayList<Node> marked = new ArrayList<Node>();
		// initilize things
		System.out.println("Start Crow Flies");
		source.setPreviousEdge(null); // there is not previous node to visit
		Node currentNode = source;

		while (currentNode != target) {
			currentNode.setVisited(true);
			for (Node n : currentNode.neighbours()) {
				if (n.getVisited() == false) {
					n.setMarked(true);
					marked.add(n);

					Edge connectingEdge = edgeBetween(currentNode, n);// find
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
			currentNode = smallestDistanceAStar(marked, target);
			if (currentNode == null) {
				return null;
			}
		}
		// Retrace the path using the previous edges recorded and return it
		ArrayList<Edge> thePath = new ArrayList<Edge>();
		Node someNode = currentNode;
		while (someNode != source) {
			Edge pathEdge = someNode.getPreviousEdge();
			pathEdge.setSelected(true);
			thePath.add(0, pathEdge); // prepend the edge to the path
			someNode = pathEdge.otherEndFrom(someNode);
		}
		return thePath;
	}

	public Node smallestDistanceManhattan(ArrayList<Node> a, Node target) {
		if (a == null) {
			return null;
		}
		Node smallestNode = null;

		int smallX = 0;
		int smallY = 0;

		for (Node n : a) {
			if (smallestNode == null || n.getDistance() + Math.abs(n.getLocation().x - target.getLocation().x)
					+ Math.abs(n.getLocation().y - target.getLocation().y) < smallestNode.getDistance() + smallX
							+ smallY) {

				smallestNode = n;
				smallX = Math.abs(smallestNode.getLocation().x - target.getLocation().x);
				smallY = Math.abs(smallestNode.getLocation().y - target.getLocation().y);
			}
		}
		return smallestNode;
	}

	public ArrayList<Edge> findPathUsingAStarManhattan(Node source, Node target) {
		ArrayList<Node> marked = new ArrayList<Node>();

		// initilize things
		System.out.println("Start Manhattan");
		source.setPreviousEdge(null); // there is not previous node to visit
		Node currentNode = source;

		while (currentNode != target) {
			currentNode.setVisited(true);
			for (Node n : currentNode.neighbours()) {
				if (n.getVisited() == false) {
					n.setMarked(true);
					marked.add(n);

					Edge connectingEdge = edgeBetween(currentNode, n);// find
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
			currentNode = smallestDistanceManhattan(marked, target);
			if (currentNode == null) {
				return null;
			}
		}
		// Retrace the path using the previous edges recorded and return it
		ArrayList<Edge> thePath = new ArrayList<Edge>();
		Node someNode = currentNode;
		while (someNode != source) {
			Edge pathEdge = someNode.getPreviousEdge();
			pathEdge.setSelected(true);
			thePath.add(0, pathEdge); // prepend the edge to the path
			someNode = pathEdge.otherEndFrom(someNode);
		}
		return thePath;
	}

	public ArrayList<Edge> findPathUsingAStarZeroCostHeuristic(Node source, Node target) {
		ArrayList<Node> marked = new ArrayList<Node>();

		// initilize things
		System.out.println("Start ZeroCostHeuristic");
		source.setPreviousEdge(null); // there is not previous node to visit
		Node currentNode = source;

		while (currentNode != target) {
			currentNode.setVisited(true);
			for (Node n : currentNode.neighbours()) {
				if (n.getVisited() == false) {
					n.setMarked(true);
					marked.add(n);

					Edge connectingEdge = edgeBetween(currentNode, n);// find
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
			currentNode = smallestDistance(marked);
			if (currentNode == null) {
				return null;
			}
		}
		// Retrace the path using the previous edges recorded and return it
		ArrayList<Edge> thePath = new ArrayList<Edge>();
		Node someNode = currentNode;
		while (someNode != source) {
			Edge pathEdge = someNode.getPreviousEdge();
			pathEdge.setSelected(true);
			thePath.add(0, pathEdge); // prepend the edge to the path
			someNode = pathEdge.otherEndFrom(someNode);
		}
		return thePath;
	}

	public ArrayList<Edge> findPathUsingAStarBestFirstGreedy(Node source, Node target) {
		ArrayList<Node> marked = new ArrayList<Node>();

		// initilize things
		System.out.println("Start greedy");
		source.setPreviousEdge(null); // there is not previous node to visit
		Node currentNode = source;

		while (currentNode != target) {
			currentNode.setVisited(true);
			for (Node n : currentNode.neighbours()) {
				if (n.getVisited() == false) {
					n.setMarked(true);
					marked.add(n);

					Edge connectingEdge = edgeBetween(currentNode, n);// find
																		// corresponding
																		// edge
					int edgeLen = connectingEdge.getWeight();
					// set distance if it is greater then where we have come
					// reset
					if (n.getDistance() > distanceBetween(n, target)) {
						n.setDistance(distanceBetween(n, target));
						n.setPreviousEdge(connectingEdge);
					}
				}
			}
			marked.remove(currentNode);
			currentNode = smallestDistance(marked);
			if (currentNode == null) {
				return null;
			}
		}
		// Retrace the path using the previous edges recorded and return it
		ArrayList<Edge> thePath = new ArrayList<Edge>();
		Node someNode = currentNode;
		while (someNode != source) {
			Edge pathEdge = someNode.getPreviousEdge();
			pathEdge.setSelected(true);
			thePath.add(0, pathEdge); // prepend the edge to the path
			someNode = pathEdge.otherEndFrom(someNode);
		}
		return thePath;
	}
}