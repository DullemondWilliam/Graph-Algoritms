package comp2402graphEditor;

import java.util.*;
import java.awt.*;
import java.io.*;

public class Edge {
	private int 	weight 	= -1;	//Weight to be used by the algorithm 
	private String 	label	= ""; 	//user assignable label
	
	//head and tail node of the edge
	private Node    startNode	= null;
	private Node	endNode		= null;
	private boolean selected	= false; 
	private boolean marked		= false;
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Getters and Setters/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String	getLabel() 		{ return label; }
	public Node 	getStartNode()	{ return startNode; }
	public Node		getEndNode() 	{ return endNode; }
	public boolean 	isMarked() 		{ return selected; }
	public boolean 	isSelected() 	{ return selected; }
	public int 	getWeight() {
		if(weight <= 0)	{ return (int) graphicalLength();} 
		else	{ return weight;}
	} 
	
	public void setLabel(String newLabel)	{ label	= newLabel; }
	public void setStartNode(Node aNode)	{ startNode = aNode; }
	public void setEndNode(Node aNode) 		{ endNode 	= aNode; }
	public void setMarked(boolean state) 	{ marked 	= state; }
	public void setSelected(boolean state) 	{ selected 	= state; }
	public void toggleMarked() 				{ marked 	= !marked; }
	public void toggleSelected() 			{ selected 	= !selected; }
	public void setWeight(int newWeight) { 
		if(weight > 0) weight = newWeight; 
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Constructors////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Must Receive nodes or this makes no sense
	public Edge(Node start, Node end) {
		startNode	= start;
		endNode 	= end;
		label		= "";
		selected	= false;
	}

	public Edge(String aLabel, Node start, Node end) {
		startNode 	= start;
		endNode 	= end;
		label 		= aLabel;
		selected	= false;
	}

	private Edge(){} 
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Main Functions/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Edges formated to look like: sNode(12,43) --> eNode(67,34)
	public String toString() {
		return(startNode.toString() + " --> " + endNode.toString());
	}
		
	public double graphicalLength() {
		//Return the actual graphical display length of the edge
		Node n1 = startNode; 
		Node n2 = endNode; 
		return Math.sqrt ((double) ((n2.getLocation().x - n1.getLocation().x) *                            
				(n2.getLocation().x - n1.getLocation().x) +
				(n2.getLocation().y - n1.getLocation().y) * 
				(n2.getLocation().y - n1.getLocation().y)));
	}

	public boolean connects(Node a, Node b) {
		//answer whether this edge connects Nodes a and b
		if((startNode == a) && (endNode == b)) { return true;}
		if((startNode == b) && (endNode == a)) { return true;}
		return false;
	}

	public Node otherNode(Node aNode) {
		// Return the node of this edge that is opposite aNode
		if (startNode == aNode){
			return endNode;
		} else if(endNode == aNode){
			return startNode;
		} else{
			return null;
		}
	}

	public boolean containsPoint(Point p) {
		//Returns true if the given point is on the graphical line
		double xs = startNode.getLocation().getX();
		double xe = endNode.getLocation().getX();
		if (xs >= xe){
			if( (p.getX() >= xs) || (p.getX() <= xe)) 	{ return false;}
		} else if (xs < xe){
			if( (p.getX() >= xe) || (p.getX() <= xs))	{ return false;}
		}
		return true; 
	}

	public Point midPoint() {
		//Return the middle of the graphical line
		int	midPointX;
		int midPointY;
		midPointX = (getStartNode().getLocation().x + getEndNode().getLocation().x) / 2;
		midPointY = (getStartNode().getLocation().y + getEndNode().getLocation().y) / 2;
		return new Point(midPointX,midPointY);
	}

	public Point intersects(Edge e) {
		//Find the intersection of the given node and this node
		//Return null if they do not cross
		if (this == e) return null; //edge cannot intersect itself

		//if the edges have end points in common return null;
		if (startNode == e.endNode) 	{ return null;}
		if (startNode == e.startNode) 	{ return null;}
		if (endNode == e.endNode) 	{ return null;}
		if (endNode == e.startNode) { return null;}

		double x1 = startNode.getLocation().getX();
		double x2 = endNode.getLocation().getX();
		double y1 = startNode.getLocation().getY();
		double y2 = endNode.getLocation().getY();
		double x3 = e.startNode.getLocation().getX();
		double x4 = e.endNode.getLocation().getX();
		double y3 = e.startNode.getLocation().getY();
		double y4 = e.endNode.getLocation().getY();

		//CASE 1 Parallel
		if(((x2-x1) == 0.0 ) && ((x4-x3) == 0.0 )){
			return null; 
		} 

		//CASE 2 Not Parallel
		if(((x2-x1) != 0.0 ) && ((x4-x3) != 0.0 )){
			double m1 = (double) (y2-y1)/(x2-x1); //slope of this edge
			double b1 = (double) y1 - (m1*x1);    //y intercept of this edges
			double m2 = (double) (y4-y3)/(x4-x3); //slope of edge e
			double b2 = (double) y3 - (m2*x3);    //y intercept of edge e

			if( m1 == m2) {return null; } //edges are parallel

			double xintersect = (double) (b2-b1)/(m1-m2);
			double yintersect = (double) (m1*xintersect) + b1;
			Point intersect = new Point((int) xintersect, (int) yintersect);
			if (containsPoint(intersect) && e.containsPoint(intersect)){
				return intersect;
			} else	{ return null;}
		}
		//CASE 3 this EDGE IS VERTICAL
		if((x2-x1) == 0.0 ) {
			//this edge is vertical 
			double m2 = (double) (y4-y3)/(x4-x3); //slope of edge e
			double b2 = (double) y3 - (m2*x3);    //y intercept of edge e

			double xintersect = x2;
			double yintersect = (double) (m2*xintersect) + b2;
			Point intersect = new Point((int) xintersect, (int) yintersect);
			if (containsPoint(intersect) && e.containsPoint(intersect)){
				return intersect;
			} else { 
				return null;
			}
		} 

		//CASE 4 EDGE e IS VERTICAL
		if((x4-x3) == 0.0 ) {
			double m1 = (double) (y2-y1)/(x2-x1); //slope of this edge
			double b1 = (double) y1 - (m1*x1);    //y intercept of this edges

			double xintersect = x4;
			double yintersect = (double) (m1*xintersect) + b1;
			Point intersect = new Point((int) xintersect, (int) yintersect);
			if (containsPoint(intersect) && e.containsPoint(intersect)){
				return intersect;
			} else{
				return null;
			}
		} 
		return null;
	}

	public static Edge removeMinimumWeightEdge(ArrayList<Edge> edges){
		double minimumWeight = Double.POSITIVE_INFINITY;

		Edge minimumWeightEdge = null;
		for(int i=0; i<edges.size();i++){
			Edge anEdge = (Edge) edges.get(i);
			if(anEdge.getWeight() <= minimumWeight) {
				minimumWeight = anEdge.getWeight();
				minimumWeightEdge = anEdge;
			}

		}
		if(minimumWeightEdge != null) {
			edges.remove(minimumWeightEdge);
		}
		return minimumWeightEdge;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////
//Drawing Functions////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
	public void draw(Graphics2D aPen, boolean displayWeights) {
		// Draw the edge using the given Graphics object
		// Draw a black or red line from the center of the startNode to the center of the endNode
		Stroke oldStroke = aPen.getStroke(); //cache old pen stroke
		Color oldColor = aPen.getColor();
		
		if (selected){			
			//use wider stroke for drawing selected edges
			aPen.setStroke(GraphEditor.wideStroke);
			aPen.setColor(Graph.SELECTED_COLOR);
		}
		else if(marked){
			//use wider stroke for drawing marked edges
			aPen.setStroke(GraphEditor.wideStroke);
			aPen.setColor(Graph.MARKED_COLOR);
		}
		else {
			aPen.setColor(Graph.NORMAL_EDGE_COLOR);
		}
		aPen.drawLine(startNode.getLocation().x, startNode.getLocation().y,
				endNode.getLocation().x, endNode.getLocation().y);

		if(displayWeights){
			Font oldFont = aPen.getFont(); //cache any font currently in use
			aPen.setFont(Node.labelFont);
			aPen.setColor(Color.blue);
			Point midPoint = midPoint();
			aPen.drawString(String.valueOf(getWeight()), midPoint.x, midPoint.y - Node.RADIUS);
			aPen.setFont(oldFont);
		}
		//return pen to old stroke
		aPen.setStroke(oldStroke);
		aPen.setColor(oldColor);
	}

	public void drawWithPenColor(Graphics aPen, boolean displayWeights) {
		// Draw a line from the center of the startNode to the center of the endNode
		//using aPen's current color ---used for erasing
		aPen.drawLine(startNode.getLocation().x, startNode.getLocation().y,
				endNode.getLocation().x, endNode.getLocation().y);
		if(displayWeights){
			Point midPoint = midPoint();
			aPen.drawString(String.valueOf(getWeight()), midPoint.x, midPoint.y - Node.RADIUS);
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////
//XML Parsing and Writing Functions////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
	final public static String startTag	= "<edge>";
	final public static String endTag 	= "</edge>"; 
	final public static String labelTag 	= "<label>";
	final public static String labelEndTag 	= "</label>";
	final public static String startNodeLocationTag 	= "<startLocation>"; //location of head node
	final public static String startNodeLocationEndTag 	= "</startLocation>";
	final public static String endNodeLocationTag 		= "<endLocation>"; //location of tail node
	final public static String endNodeLocationEndTag 	= "</endLocation>";
	final public static String weightTag 	= "<weight>";
	final public static String weightEndTag = "</weight>";
	final public static String selectedTag 	= "<selected/>"; //node is in selected state
	final public static String markedTag 	= "<marked/>"; //node is marked

	public static String [] propertyTagPairs = {
		Graph.XMLCommentTag, Graph.XMLCommentEndTag,
		labelTag,labelEndTag,
		startNodeLocationTag, startNodeLocationEndTag,
		endNodeLocationTag, endNodeLocationEndTag,
		weightTag, weightEndTag
	};
	public static String [] singletonPropertyTags = {selectedTag, markedTag};

	//PARSING METHODS= for parsing graph from XML data file
	public void parseInputWeightString(String inputString){
		//set the weight of this edge by parsing input string
		/*a property string is expected and integer weight
		 *integer weights can also be a simple addition or subtraction
		 *such as 12+23 or 23-34
		 */
		if(inputString == null || inputString.trim().length() == 0) return;
		try{
			if(inputString.contains("+") && inputString.split("\\+").length == 2){
				String arguments[] = inputString.split("\\+");
				int a = Integer.parseInt(arguments[0]);
				int b = Integer.parseInt(arguments[1]);
				setWeight(a+b);
			}
			else if(inputString.contains("-") && inputString.split("\\-").length == 2){
				String arguments[] = inputString.split("\\-");
				int a = Integer.parseInt(arguments[0]);
				int b = Integer.parseInt(arguments[1]);
				setWeight(a-b);
			}
			else
				setWeight(Integer.parseInt(inputString));
			return;
		}
		catch(NumberFormatException e){}
	}
	public void setProperty(String propertyTagString){
		//set a singleton property described by propertyTagString
		if(propertyTagString.equals(selectedTag)) setSelected(true);
		if(propertyTagString.equals(markedTag)) setMarked(true);
	} 

	public void setProperty(String propertyTagString, String dataString) {
		//Assign data to the property identified by the propertyTagString
		if(dataString == null) return;
		if(propertyTagString.equals(labelTag)) setLabel(dataString); 
		if(propertyTagString.equals(weightTag)) setWeight(Integer.valueOf(dataString));
		if(propertyTagString.equals(startNodeLocationTag)) {
			String[] locationData = dataString.split(",");
			String locationX = locationData[0];
			String locationY = locationData[1];
			Point nodeLocation = new Point(Integer.valueOf(locationX).intValue(), 
					Integer.valueOf(locationY).intValue());
			Node startNodeModel = new Node(nodeLocation);

			setStartNode(startNodeModel);       	  
		}
		if(propertyTagString.equals(endNodeLocationTag)) {
			String[] locationData = dataString.split(",");
			String locationX = locationData[0];
			String locationY = locationData[1];
			Point nodeLocation = new Point(Integer.valueOf(locationX).intValue(), 
					Integer.valueOf(locationY).intValue());
			Node endNodeModel = new Node(nodeLocation);

			setEndNode(endNodeModel);       	  
		}
	}
	//Parsing Methods
	public boolean parseMultiLinePropertyString(String aString, BufferedReader inputFile){
		//If the string aString is a tag for a property that could be split over
		//many lines, BUT DOES NOT CONTAIN nested structures 
		//then parse it and set the property

		//Parse a string of the form

		// "<tag> 
		// "these are some comments"
		// "these are some comments"
		// </tag>"

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
			outputFile.println(indent + labelTag  + label +  labelEndTag);

		outputFile.println(indent + startNodeLocationTag  + startNode.getLocation().x + "," + startNode.getLocation().y + startNodeLocationEndTag);
		outputFile.println(indent + endNodeLocationTag + endNode.getLocation().x + "," + endNode.getLocation().y + endNodeLocationEndTag);

		if(this.isSelected()) outputFile.println(baseIndent + tab + selectedTag);
		if(this.isMarked()) outputFile.println(baseIndent + tab + markedTag);

		if(weight > 0)
			outputFile.println(indent + weightTag  + getWeight()  + weightEndTag);

		outputFile.println(baseIndent + endTag);
	}

	public static Edge parseFromFile(String openingTag, BufferedReader inputFile){
		//Parse in an edge from the XML input file
		//The opening tag is the first tag that was stripped of by and outer level of
		//parsing
		// <edge>
		//     ...
		// </edge>
		Edge parsedModel = new Edge();
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
			while(!(inputLine = inputFile.readLine().trim()).startsWith(endTag)) { 
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