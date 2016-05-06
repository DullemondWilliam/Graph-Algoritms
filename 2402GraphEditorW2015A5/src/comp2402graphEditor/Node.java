package comp2402graphEditor;

import java.util.*;
import java.awt.*;
import java.io.*;
//DISCLAIMER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//==========
//This code is designed for classroom illustration
//It may have intentional omissions or defects that are
//for illustration or assignment purposes
//
//That being said: Please report any bugs to me so I can fix them
//...Lou Nel (ldnel@scs.carleton.ca)
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class Node {

	//PARSING VARIALBLES=====================================
	//Tags used for XML like export of node objects
	final public static String startTag = "<node>";
	final public static String endTag = "</node>";
	final public static String labelTag = "<label>";
	final public static String labelEndTag = "</label>";
	final public static String locationTag = "<location>";
	final public static String locationEndTag = "</location>";
	final public static String weightTag = "<weight>";
	final public static String weightEndTag = "</weight>";

	final public static String selectedTag = "<selected/>"; //node is in selected state
	final public static String markedTag = "<marked/>"; //node is marked

	public static String [] propertyTagPairs = {
		Graph.XMLCommentTag, Graph.XMLCommentEndTag,
		labelTag,labelEndTag,
		locationTag, locationEndTag,
		weightTag, weightEndTag
	};
	public static String [] singletonPropertyTags = {
		selectedTag,
		markedTag
	};

	// Here is a class variable to keep the radius of all nodes for drawing
	public static final int smallNodeSize = 5;
	public static final int mediumNodeSize = 10;
	public static final int largeNodeSize = 15;

	public static int	RADIUS = mediumNodeSize;
	public static int counter = 1;

	final private static int labelPointSize = RADIUS * 2;
	final public static Font labelFont = new Font("Serif", Font.BOLD, labelPointSize);

	// These are the important data structure model variables
	//These attributes are saved when the graph is saved to disk
	private String    label; //name of the node
	private ArrayList<Edge>    incidentEdges; //the edges attached to this node
	private boolean   selected; //selection state for editing or display
	private boolean   marked; //marked state for display
	private int weight =0; //used when the graph should model nodes with different weights

	//Variables used for drawing and editing the graph with GUI
	private Point     location; //drawing location of the node
	private Point     alternateLocation; //used by graphics routines    
	private int deltaX = 0; //used when animating node moves
	private int deltaY = 0; //used when animating node moves
	private int numberOfSteps = 0; //number of steps to take in delta directions

	//Variables that can be used by algorithms
	//These variables can be used by algorithms that need this sort of information
	//They Can be re-initialized and reused by algorithms that need them
	//These attributes are NOT stored when the graph is saved to a data file
	private double distance; //used for shortest path algorithm
	private boolean visited; //used by algorithms to mark node as processed
	private Edge previousEdge; //used by shortest path algorithms to store previous node

	// CONSTRUCTORS ========================================================
	public Node() {
		initialize();
	}

	public Node(String aLabel) {
		initialize();
		label = aLabel;
	}

	public Node(Point aPoint) {
		initialize();
		location = aPoint;
	}

	public Node(String aLabel, Point aPoint) {
		initialize();
		label = aLabel;
		location = aPoint;
	}

	private void initialize() {
		label = "";
		location = new Point(0,0);
		alternateLocation = new Point(0,0);
		incidentEdges = new ArrayList<Edge>();
		selected = false;
		weight = 0;
		deltaX = 0;
		deltaY = 0;
		numberOfSteps = 0;
	}

	// The get & set methods ============================================================
	public String getLabel() { return label; }
	public Point getLocation() { return location; }
	public boolean isSelected() { return selected; }
	public boolean isMarked() { return marked; }
	public double getDistance()  {return distance;}
	public void setDistance(double d) {distance = d;}
	public void setDistance(String s) {
		if(s == null) return;
		if(s.length() == 0)return;
		try{
			distance = Double.parseDouble(s);
		}
		catch(NumberFormatException e){}
	}
	public boolean getVisited() {return visited;}
	public void setVisited(boolean state) {visited = state;}
	public int getWeight() {return weight;}
	public void setWeight(int anIntegerWeight) {weight = anIntegerWeight;}
	public Edge getPreviousEdge() {return previousEdge; }
	public void setPreviousEdge(Edge theEdge) {previousEdge = theEdge; }

	public ArrayList<Edge> incidentEdges() { return incidentEdges; }

	public void setLabel(String newLabel) { label = newLabel; }
	public void setLabel(int intLabel) { label = String.valueOf(intLabel); }
	public void setLocation(Point aPoint) { 
		alternateLocation = location; //store old location to allow reset
		location = aPoint; }

	public void resetLocation() {
		//reset location to the alternate point. 
		location = alternateLocation; 
	}

	public void setLocation(int x, int y) { setLocation(new Point(x, y)); }

	public void setDeltaForLocation(Point newLocation, int animationSteps) { 
		deltaX = (newLocation.x - location.x)/animationSteps;
		deltaY = (newLocation.y - location.y)/animationSteps;
		numberOfSteps = animationSteps;
	}

	public void moveNodeOneStep() {

		if(numberOfSteps > 0) {

			int x = location.x + deltaX;
			int y = location.y + deltaY;
			setLocation(x,y);
			numberOfSteps--;
		}
	}

	public void setSelected(boolean state) { selected = state; }
	public void toggleSelected() { selected = !selected; }
	public void setMarked(boolean state) { marked = state; }
	public void toggleMarked() { marked = !marked; }

	// Adding and deleting incident edges
	public void addIncidentEdge(Edge e) { incidentEdges.add(e); }
	public void deleteIncidentEdge(Edge e) { incidentEdges.remove(e); }

	public boolean isConnectedTo(Node aNode) {
		//answer whether there is an edge between this and aNode

		Iterator<Edge>    edges = incidentEdges.iterator();
		while(edges.hasNext())
			if ( edges.next().otherEndFrom(this) == aNode) return true;

		return false;
	}

	// Nodes look like this:  label(12,43)
	public String toString() {
		return(getLabel() + "(" + location.x + "," + location.y + ")");
	}

	// Return all the neighbouring nodes of this node
	public ArrayList<Node> neighbours() {
		ArrayList<Node> result = new ArrayList<Node>();

		Iterator<Edge>    edges = incidentEdges.iterator();
		while(edges.hasNext())
			result.add(edges.next().otherEndFrom(this));
		return result;
	}

	//helper methods to do calculations
	private double distanceBetween(Node n1, Node n2) {
		return Math.sqrt ((double) ((n2.getLocation().x - n1.getLocation().x) *                            (n2.getLocation().x - n1.getLocation().x) +
				(n2.getLocation().y - n1.getLocation().y) * 
				(n2.getLocation().y - n1.getLocation().y)));
	}

	private double distanceBetween(Point p1, Point p2) {
		return Math.sqrt ((double) ((p2.x - p1.x) *  (p2.x - p1.x) +
				(p2.y - p1.y) * 
				(p2.y - p1.y)));

	}

	public Node neighbourClosestTo(Point p) {
		//answer the neighbour closest to p
		double distance = Double.POSITIVE_INFINITY;
		Node closestNeighbour = null;

		Iterator<Node>    neighbours = neighbours().iterator();
		while(neighbours.hasNext()){
			Node n = neighbours.next();
			if (distanceBetween(p, n.getLocation() ) < distance) {
				closestNeighbour = n;
				distance = distanceBetween(p, n.getLocation() );
			}
		}
		return closestNeighbour;
	}

	public Node neighbourFurthestFrom(Point p) {
		//answer the neighbour furthest from p
		double distance = 0.0;
		Node furthestNeighbour = null;

		Iterator<Node>    neighbours = neighbours().iterator();
		while(neighbours.hasNext()){
			Node n = neighbours.next();
			if (distanceBetween(p, n.getLocation() ) > distance) {
				furthestNeighbour = n;
				distance = distanceBetween(p, n.getLocation() );
			}
		}
		return furthestNeighbour;
	}

	// Draw this node using the given Graphics object
	public void draw(Graphics2D aPen, boolean displayNodeLabels) {
		// Draw a blue or red-filled circle around the center of the node
		if (selected)
			aPen.setColor(Graph.SELECTED_COLOR);
		else if (marked)
			aPen.setColor(Graph.MARKED_COLOR);
		else
			aPen.setColor(Graph.NORMAL_COLOR);
		aPen.fillOval(location.x - Node.RADIUS, location.y - Node.RADIUS, 
				Node.RADIUS * 2, Node.RADIUS * 2);
		// Draw a black border around the circle
		aPen.setColor(Graph.NORMAL_EDGE_COLOR);
		aPen.drawOval(location.x - Node.RADIUS, location.y - Node.RADIUS, 
				Node.RADIUS * 2, Node.RADIUS * 2);

		if(displayNodeLabels) drawNodeLabel(aPen);
	}

	public void drawWithPenColor(Graphics2D aPen, boolean displayNodeLabels) {
		aPen.fillOval(location.x - Node.RADIUS, location.y - Node.RADIUS, 
				Node.RADIUS * 2, Node.RADIUS * 2);
		aPen.drawOval(location.x - Node.RADIUS, location.y - Node.RADIUS, 
				Node.RADIUS * 2, Node.RADIUS * 2);

		if(displayNodeLabels) drawNodeLabel(aPen);
	}

	public void drawNodeLabel(Graphics2D aPen) {

		// Draw the node label
		if(label == null) return; //don't draw null label
		if(label.length() == 0) return; //don't draw empty label

		Font oldFont = aPen.getFont(); //cache any font currently in use
		Color oldColor = aPen.getColor();

		aPen.setFont(labelFont);
		FontMetrics metrics = aPen.getFontMetrics();

		int labelWidth = metrics.stringWidth(label);
		int stringHeightOffset = labelPointSize/4; //rough estimate

		aPen.setColor(Color.black);

		String labelString = label + " " + ((int) distance);
		aPen.drawString(labelString, location.x - labelWidth/2 - Node.RADIUS, location.y - stringHeightOffset -Node.RADIUS);


		aPen.setColor(oldColor);
		aPen.setFont(oldFont);

	}

	//PARSING METHODS=====================================================================
	//Methods for parsing chart from data file
	public void setProperty(String propertyTagString){
		//set a singleton property described by propertyTagString

		if(propertyTagString.equals(selectedTag)) setSelected(true);
		if(propertyTagString.equals(markedTag)) setMarked(true);

	} 

	public void setProperty(String propertyTagString, String dataString) {

		//Assign data to the property identified by the propertyTagString

		if(dataString == null) return;

		//System.out.println("SETTING PROPERTY: " + propertyTagString + "  " + dataString);

		if(propertyTagString.equals(labelTag)) setLabel(dataString);
		if(propertyTagString.equals(weightTag)) setWeight(Integer.valueOf(dataString));
		if(propertyTagString.equals(locationTag)) {
			String[] locationData = dataString.split(",");
			String locationX = locationData[0];
			String locationY = locationData[1];

			setLocation(Integer.valueOf(locationX).intValue(), 
					Integer.valueOf(locationY).intValue());
		}

	}
	//Parsing Methods
	public boolean parseMultiLinePropertyString(String aString, BufferedReader inputFile){
		//If the string aString is a tag for a property that could be split over
		//many lines, BUT DOES NOT CONTAIN nested structures 
		//then parse it and set the property

		//Parse a string of the form

		// "<chartCommnents> 
		// "these are some chart comments"
		// "these are some more comments"
		// </chartCommnents>"

		//Note in the loop below the propertyTagPairs array is assumed to be of the form
		// <startTag>, <endTag>
		//that is why the /2 in the loop

		for(int i=0; i<propertyTagPairs.length/2; i++){
			String beginTag = propertyTagPairs[i*2];
			String endTag = propertyTagPairs[(i*2)+1]; 
			if(aString.equals(beginTag)){

				String inputLine; //current input line
				StringBuffer dataBuffer = new StringBuffer();
				try{
					while(!(inputLine = inputFile.readLine().trim()).startsWith(endTag)) {
						dataBuffer.append(inputLine).append("\n");
					}
				}
				catch (EOFException e) {
					System.out.println("VERSION PARSE Error: EOF encountered, file may be corrupted.");
					return false;
				} 
				catch (IOException e) {
					System.out.println("VERSION PARSE Error: Cannot read from file.");
					return false;
				}   
				//should have seen the end tag
				setProperty(beginTag, dataBuffer.toString());
				return true;
			}

		}
		return false;
	}

	public boolean parsePropertyString(String aString){
		//If the string aString is a tag-delimited property then set that property and
		//return true, otherwise return false
		//Parse a string of the form

		// "<chartTitle> April in the Spring </chartTitle>"

		//Note in the loop below the propertyTagPairs array is assumed to be of the form
		// <startTag>, <endTag>
		//that is why the /2 in the loop

		for(int i=0; i<propertyTagPairs.length/2; i++){
			String beginTag = propertyTagPairs[i*2];
			String endTag = propertyTagPairs[(i*2)+1]; 
			if((aString.startsWith(beginTag)) && (aString.endsWith(endTag))){

				setProperty(beginTag, aString.substring((beginTag.length()),(aString.length() - endTag.length())).trim());
				return true;
			}

		}
		return false;
	}

	public boolean parseSingletonPropertyString(String aString){
		//If the string aString is a singleton tag property then set that property and
		//return true, otherwise return false
		//Parse a string of the form

		// "<isRest/>"

		//Note in the loop below the propertyTagPairs array is assumed to be of the form
		// <startTag>, <endTag>, "comment",
		//that is why the /2 in the loop

		for(int i=0; i<singletonPropertyTags.length; i++){
			String singletonTag = singletonPropertyTags[i];
			if(aString.startsWith(singletonTag)){

				setProperty(singletonTag);
				return true;
			}

		}
		return false;
	}

	public void writeToFile(String baseIndent, PrintWriter outputFile){
		//Write the chart to a file in XML style tag-delimited data that
		//can be read parse in

		String tab = "   ";
		String indent = baseIndent + tab;

		//write class start tag
		outputFile.println(baseIndent + startTag);

		if(this.label != null && !this.label.isEmpty())
			outputFile.println(indent + labelTag  + getLabel()  + labelEndTag);

		outputFile.println(indent + locationTag  + location.x + "," + location.y + locationEndTag);

		if(this.isSelected()) outputFile.println(baseIndent + tab + selectedTag);

		if(this.isMarked()) outputFile.println(baseIndent + tab + markedTag);

		if(this.weight != 0)
			outputFile.println(indent + weightTag  + weight  + weightEndTag);


		//write class end tag
		outputFile.println(baseIndent + endTag);

	}

	public static Node parseFromFile(String openingTag, BufferedReader inputFile){
		//Parse in a node from the XML input file
		//The opening tag is the first tag that was stripped of by and outer level of
		//parsing

		// <graph>
		//     ...
		// </graph>

		Node parsedModel = new Node();

		String inputLine; //current input line
		String dataString = null;

		try {

			//System.out.println("Node Opening Tag:" + openingTag);

			//check that we have the right opening tag
			if(!openingTag.equalsIgnoreCase(startTag)){
				System.out.println("ERROR: Node Opening Tag:" + openingTag);
				return null; //no successful parse
			}

			//parse until we get to the closing tag
			while(!(inputLine = inputFile.readLine().trim()).startsWith(endTag)) 
			{ 
				dataString = inputLine;
				if(dataString.length() ==0){
					//do nothing, but allow for blank lines;
				}
				//see if the dataString is of the form "<tag> data </tag>"
				else if(parsedModel.parsePropertyString(dataString)) {}
				//see if the dataString is of the form "<tag/>
				else if(parsedModel.parseSingletonPropertyString(dataString)) {} 
				//see if it is a multi-line property string -but not nested         
				else if(parsedModel.parseMultiLinePropertyString(dataString,inputFile )) {}

			}
			//closing tag reached

			//catch file IO errors   
		} catch (EOFException e) {
			System.out.println("VERSION PARSE Error: EOF encountered, file may be corrupted.");
			return null;
		} catch (IOException e) {
			System.out.println("VERSION PARSE Error: Cannot read from file.");
			return null;
		}    	

		return parsedModel;

	} 


}