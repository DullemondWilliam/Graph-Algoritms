package comp2402graphEditor;



import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import comp2402graphEditor.Graph.PathFindingMethod;

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

// This class represents a panel on which a graph is edited and displayed
// It handles and interprets mouse events for editing and loading and saving the graph to disk
// It interprets user GUI actions into appropriate actions on the graph


public class GraphEditor extends JPanel implements MouseListener, ActionListener, MouseMotionListener{

	// Keep the model (i.e. the graph)
    private Graph aGraph; //the graph being edited
    
    private File graphDataFile; //File the graph was loaded from
    private String imageDataFileName;
    private File imageDataFile; //File background image is loaded
    private BufferedImage  backgroundImage = null; //background image
    
    private static boolean displayGraph =true; //can be set false if graph is not to be displayed
    public static boolean displayBackgroundImage = false;
    private static boolean displayNodeLabels =false; 
    private static boolean displayEdgeWeights =false; 
    
    public static boolean useEdgeWeights =false; //use edge weights instead of graphical length when true
    
    
    final public static BasicStroke stroke = new BasicStroke(2.0f);
    
    //use this stroke for drawing wider selected edges
    final static BasicStroke wideStroke = new BasicStroke(4.0f);
    
    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,
                                                      BasicStroke.CAP_BUTT,
                                                      BasicStroke.JOIN_MITER,
                                                      10.0f, dash1, 0.0f);
    
    
    //timer and step size for animation.
    public static boolean displayAnimation =false; 
    private Timer aTimer = new Timer(100, this); //can used for animation 
    public static int AnimationSteps =20; //number of steps to animate a relocation of a node.

    
    Random rand = new Random(); //used when random numbers are required

	// Keep the frame for use with key events
    private GraphEditorGUIView	view;

    public static boolean getDisplayNodeLabels(){return  displayNodeLabels;} 
    public static boolean getDisplayEdgeWeights(){return  displayEdgeWeights;} 

	// Instance variables for node movement mouse actions
	private Point	   dragStartLocation; //the location a mouse drag started
	private Node	   dragNode; //the node a mouse drag started on
	private Rectangle  dragBox; //used for selecting node by dragging a box region around them
        
    private JPopupMenu nodePopUpMenu = new JPopupMenu(); 
    private JPopupMenu edgePopUpMenu = new JPopupMenu(); 

    //POPUP MENU ITEMS
    private JMenuItem   nodeSelectToggleItem = new JMenuItem("select (toggle)");    
    private JMenuItem   nodeMarkToggleItem = new JMenuItem("mark (toggle)");
    private JMenuItem   nodeEditLabelItem = new JMenuItem("label...");
    private JMenuItem   nodeEditDistanceItem = new JMenuItem("distance...");
    
    private JMenuItem   edgeSelectToggleItem = new JMenuItem("select (toggle)");    
    private JMenuItem   edgeMarkToggleItem = new JMenuItem("mark (toggle)");
    private JMenuItem   edgeEditWeightItem = new JMenuItem("weight...");

    private Node popUpNode = null;
    private Edge popUpEdge = null;
	
	public GraphEditor(GraphEditorGUIView aView) {
        view = aView;
        displayGraph = true; //to show the graph
		aGraph = new Graph();


		setSize(500, 500);
		setBackground(Color.white);
		addEventHandlers();
		dragStartLocation = null;
		dragNode = null;
        aTimer.stop();
        
        
		//POPUP MENUS
	    nodePopUpMenu.add(nodeSelectToggleItem); 
	    nodePopUpMenu.add(nodeMarkToggleItem); 
	    nodePopUpMenu.add(nodeEditLabelItem);
	    nodePopUpMenu.add(nodeEditDistanceItem);
	    nodeSelectToggleItem.addActionListener(this);
	    nodeMarkToggleItem.addActionListener(this);
	    nodeEditLabelItem.addActionListener(this);
	    nodeEditDistanceItem.addActionListener(this);
	    
	    edgePopUpMenu.add(edgeSelectToggleItem); 
	    edgePopUpMenu.add(edgeMarkToggleItem); 
	    edgePopUpMenu.add(edgeEditWeightItem); 
	    edgeSelectToggleItem.addActionListener(this);
	    edgeMarkToggleItem.addActionListener(this);
	    edgeEditWeightItem.addActionListener(this);

                
	}
    public Graph getGraph() {return aGraph;}

	public void addEventHandlers() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void removeEventHandlers() {
		removeMouseListener(this);
		removeMouseMotionListener(this);
	}
	
	public void forceUpdate(){
		if(displayAnimation ==true )
		   view.forceUpdate();
	}

	// Unused event handlers
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}
	public void mouseMoved(MouseEvent event) {}

	 // Mouse click event handler
	public void mouseClicked(MouseEvent event) {
	
	
	    // If this was a double-click, then add a node at the mouse location 
	    // or toggle the selection state of targeted node or edge
        if ((event.getClickCount() == 2)&& (!event.isPopupTrigger())) {
            Node aNode = aGraph.nodeAt(event.getPoint());
            if (aNode == null) {
				// We missed a node, now try for an edge midpoint
				Edge anEdge = aGraph.edgeAt(event.getPoint());
				
				if (anEdge == null)
					aGraph.addNode(new Node(event.getPoint())); //Create new Node
				else
					anEdge.toggleSelected();
			}
            else
                aNode.toggleSelected();

            // We have changed the model, so now we update
            update();
        }
	}

	// Mouse press event handler
	public void mousePressed(MouseEvent event) {
		// First check to see if we are about to drag a node
        dragStartLocation = event.getPoint();
        Node     aNode = aGraph.nodeAt(event.getPoint());
        if (aNode != null) {
	       dragNode = aNode; 
	    }
	}

	// Mouse drag event handler
    public void mouseDragged(MouseEvent event) {
        Graphics2D pen = (Graphics2D) getGraphics();
        if (dragNode != null) {
            if (dragNode.isSelected()) 
            {
                Point nodeLocation = dragNode.getLocation();
                Point mouseLocation = event.getPoint();
                int dx = (int) mouseLocation.getX() - (int) nodeLocation.getX();
                int dy = (int) mouseLocation.getY() - (int ) nodeLocation.getY();
                Iterator<Node> theNodes = aGraph.getNodes().iterator();
                Node currentNode;
                  while (theNodes.hasNext()) {
                     currentNode = theNodes.next();
                     if(currentNode.isSelected()) {
                          pen.setColor(getBackground());
                          Iterator<Edge>    edges = currentNode.incidentEdges().iterator();
                             while(edges.hasNext())
                                edges.next().drawWithPenColor(pen, displayEdgeWeights);

                          currentNode.drawWithPenColor(pen, displayNodeLabels);
                          currentNode.getLocation().translate(dx,dy);
                          edges = currentNode.incidentEdges().iterator();
                             while(edges.hasNext())
                                edges.next().draw(pen, displayEdgeWeights);
                          Iterator<Node>    nodes = currentNode.neighbours().iterator();
                             while(nodes.hasNext())
                                nodes.next().draw(pen, displayNodeLabels);


                          currentNode.draw(pen, displayNodeLabels);
                        }
                    }
                
               }
            else {
                 pen.setColor(getBackground());
                 pen.drawLine(dragNode.getLocation().x, dragNode.getLocation().y,
                          dragStartLocation.x, dragStartLocation.y);

                 dragStartLocation = event.getPoint();
                 pen.setColor(Color.black);
                 pen.drawLine(dragNode.getLocation().x, dragNode.getLocation().y,
                 dragStartLocation.x, dragStartLocation.y);
                 
            }
        }
        else {
        	if(dragBox != null){
               //erase old drag box
        	   pen.setColor(getBackground());
          	   pen.drawRect(dragBox.getLocation().x, dragBox.getLocation().y, (int) dragBox.getWidth(), (int) dragBox.getHeight());
        	}
        	int boxWidth = Math.abs(dragStartLocation.x - event.getPoint().x);
        	int boxHeight = Math.abs(dragStartLocation.y - event.getPoint().y);
        	int boxX = Math.min(dragStartLocation.x, event.getPoint().x);
        	int boxY = Math.min(dragStartLocation.y, event.getPoint().y);
        	
        	dragBox = new Rectangle(boxX, boxY, boxWidth, boxHeight);
        }
        
        // We have changed the model, so now update
        update();
    }

	// Mouse release event handler (i.e. stop dragging process)
    public void mouseReleased(MouseEvent event) {
    	
         if (event.isPopupTrigger()){
         	//we right clicked to run the right click message dialog
         	
        	dragNode = null;
        	
         	popUpNode = aGraph.nodeAt(event.getPoint() );
            popUpEdge = aGraph.edgeAt(event.getPoint());

            if(popUpNode != null){
            
                //We right clicked on a node
            	nodePopUpMenu.show(event.getComponent(), event.getX(), event.getY());

            }
            else if (popUpEdge != null){
            
                //we right clicked on an edge so get a new weight or probability from the user
               	edgePopUpMenu.show(event.getComponent(), event.getX(), event.getY());                             
                }
             else{
            	//right clicked in empty space so ignore this            
            }
         }

         else{
       
            // Check to see if we have let go on a node
           Node     aNode = aGraph.nodeAt(event.getPoint());
           Edge anEdge = aGraph.edgeAt(event.getPoint());
           
           if ((aNode != null) && (dragNode != null) && (aNode != dragNode)){
          
               if(!aNode.isConnectedTo(dragNode)) aGraph.addEdge(dragNode, aNode);
           }
           else if(aNode == null && anEdge == null && dragBox == null){
           
               //aGraph.clearSelections();
           }
           else if(dragBox != null){
           	  if(GraphEditorGUIView.shiftPressed)
           	     aGraph.addNodesInAreaToSelection(dragBox);
           	  else
                 aGraph.selectNodesInArea(dragBox);
           	
           }

           // Refresh the canvas either way
           dragNode = null;
        
        }
        
        dragBox = null;
        update();
	}

	// used by Key typed event handler
	public void deleteSelectedItems() {

            // First remove the selected edges
            Iterator<Edge> highlightedEdges = aGraph.selectedEdges().iterator();
            while (highlightedEdges.hasNext())
                aGraph.deleteEdge(highlightedEdges.next());

            // Now remove the selected nodes
            Iterator<Node> highlightedNodes = aGraph.selectedNodes().iterator();
            while (highlightedNodes.hasNext())
                aGraph.deleteNode(highlightedNodes.next());
            update();
        
    }

	public void deleteSelectedEdges() {

            // First remove the selected edges
            Iterator<Edge> highlightedEdges = aGraph.selectedEdges().iterator();
            while (highlightedEdges.hasNext())
                aGraph.deleteEdge(highlightedEdges.next());

            update();
        
    }
    

	
	public void deleteSelectedNodes() {


            //remove the selected nodes
            Iterator<Node> highlightedNodes = aGraph.selectedNodes().iterator();
            while (highlightedNodes.hasNext())
                aGraph.deleteNode(highlightedNodes.next());
            update();
        
    }
    

    public void displayNodeLabels(boolean displayIfTrue) {
        displayNodeLabels = displayIfTrue;
        update();             
     }
    public void displayBackgroundImage(boolean displayIfTrue) {
    	displayBackgroundImage = displayIfTrue;
        update();             
     }
    
    public void displayEdgeWeights(boolean displayIfTrue) {
       displayEdgeWeights = displayIfTrue;
       update();             
    }   
    public void displayAnimation(boolean displayIfTrue) {
       displayAnimation = displayIfTrue; //display forced updates
       if(displayAnimation) startAnimation();
       else stopAnimation();
       update();             
    }
    
    public void displayNodeSize(int nodeRadius) {
    	Node.RADIUS = nodeRadius;
        update();             
     }


	// The update method
	public void update() {
		removeEventHandlers();
		repaint();
		addEventHandlers();
		view.requestFocus();
 	}

	// This is the method that is responsible for displaying the graph
	
	
    public void paintComponent(Graphics aPen) {
		super.paintComponent(aPen);
		
		//switch to Graphics2D pen so we can control stroke widths better etc.
	    Graphics2D aPen2D = (Graphics2D) aPen;
        aPen2D.setStroke(stroke); 
        
        if(displayBackgroundImage){
        	//draw background image
            aPen.drawImage(backgroundImage, 0, 0, null);
            
        }
        
        if(displayGraph) {
        	  aGraph.draw(aPen2D);
		  if (dragNode != null)
		   if (!dragNode.isSelected()){
                     aPen.drawLine(dragNode.getLocation().x, dragNode.getLocation().y,
                          dragStartLocation.x, dragStartLocation.y);
                     
                     double lineLength = Graph.distanceBetween(
                  		   dragNode.getLocation(), dragStartLocation);
                     int ManhattanX = Math.abs(dragNode.getLocation().x - dragStartLocation.x);
                     int ManhattanY = Math.abs(dragNode.getLocation().y - dragStartLocation.y);
                     int ManhattanDistance = ManhattanX + ManhattanY;
                     aPen.drawString("Crow Flies: "+ (int) lineLength, 30, 90);
                     aPen.drawString("Manhattan: "+ ManhattanDistance, 30, 110);
                     aPen.drawString("Node + Crow Flies: "+ (dragNode.getDistance() + (int) lineLength), 30, 130);
                     aPen.drawString("Node + Manhattan: "+ (dragNode.getDistance() + ManhattanDistance), 30, 150);

		   }
        }
        
        if (dragBox != null){
          	 aPen.drawRect(dragBox.getLocation().x, dragBox.getLocation().y, (int) dragBox.getWidth(), (int) dragBox.getHeight());
        }



    }

    public void clearSelections() {
       aGraph.clearSelections();
       update();             
    }
    public void renumberNodes() {
       aGraph.numberNodesFrom(0);
       update();             
    }
    
    public void completeGraph() {
       aGraph.completeGraph();
       update();             
    }
    public void randomNodeLocation() {
       
       aGraph.randomNodeLocation(getWidth(),getHeight());
       update();             
    }
    public void depthFirstSearch() {
       aGraph.depthFirstSearch();
       update();             
    }
    public void breadthFirstSearch() {
       aGraph.breadthFirstSearch();
       update();             
    }


    public void findPath(Graph.PathFindingMethod theMethod) {
       aGraph.findPath(theMethod);
       update();             
    }
    public void findSpanningTree() {
       aGraph.findMinimumCostSpanningTree();
       update();             
    }
   
    public void hasSTPath() {
    
       if(aGraph.hasSTPath())
          System.out.println("Has ST path");
       else
          System.out.println("There is no ST path"); 
       update();             
    }



    public void displayGraphOnly() {
       aTimer.stop();
       displayGraph =true;
       update();             
    }
    
    public void startAnimation() {
       if(!displayAnimation) return;	
       aTimer.start();
    }
    public void stopAnimation() {
       aTimer.stop();
    } 


   // This is the Timer event handler 
   public void actionPerformed(ActionEvent e) {
    
       //used to do animations if desired
       if (e.getSource() == aTimer){
         aGraph.doAnimationStep();
       }
       else if (e.getSource() == nodeSelectToggleItem){
    	   popUpNode.toggleSelected();
       }
	   else if (e.getSource() == nodeMarkToggleItem){
		   popUpNode.toggleMarked();
	   }
	   else if (e.getSource() == nodeEditLabelItem){
	          String inputString = JOptionPane.showInputDialog(this, "Please enter Node label", popUpNode.getLabel()); 
	          popUpNode.setLabel(inputString);
	   }
	   else if (e.getSource() == nodeEditDistanceItem){
	          String inputString = JOptionPane.showInputDialog(this, "Please enter Node distance", popUpNode.getDistance()); 
	          popUpNode.setDistance(inputString);
	}
	   else if (e.getSource() == edgeSelectToggleItem){
		   popUpEdge.toggleSelected();
	   }
	   else if (e.getSource() == edgeMarkToggleItem){
		   popUpEdge.toggleMarked();
	   }
	   else if (e.getSource() == edgeEditWeightItem){
       	  String inputString = null;
          inputString = JOptionPane.showInputDialog(this, "Please enter int edge weight (greater than 0)", "" + popUpEdge.getWeight()); 
          if(inputString != null && inputString.trim().length() > 0){
        	 popUpEdge.parseInputWeightString(inputString.trim());

	      }		
	   }
       update(); 

       } 


	// create a new empty graph
	public void newGraph() {
	    aGraph = new Graph();
	    graphDataFile = null;
		update(); 
	}
	
	public void openGraph() {
		// This code loads a new graph from a file of the user's choosing
		
   	    graphDataFile = null;
   	    
   	    String currentDirectoryProperty = System.getProperty("user.dir");
   	    //System.out.println("ChartMaker::openFile: currentDirectoryProperty is: " + currentDirectoryProperty);
   	    
        JFileChooser chooser = new  JFileChooser();
        File currentDirectory = new File(currentDirectoryProperty); 
        
        
        chooser.setCurrentDirectory(currentDirectory);
        
        
        int returnVal = chooser.showOpenDialog(this);
         
        if (returnVal == JFileChooser.APPROVE_OPTION) { 
    
        	graphDataFile = chooser.getSelectedFile();
        	
       
        	System.out.println("Opening File: " + graphDataFile.getAbsolutePath());
			aGraph = GraphParser.parseFromFile(graphDataFile);
         
        }
		update(); 
	}
	
	public void openBackgroundImage() {
		// This code loads a new graph from a file of the user's choosing
		
   	    imageDataFile = null;
   	    
   	    String currentDirectoryProperty = System.getProperty("user.dir");
   	    //System.out.println("ChartMaker::openFile: currentDirectoryProperty is: " + currentDirectoryProperty);
   	    
        JFileChooser chooser = new  JFileChooser();
        File currentDirectory = new File(currentDirectoryProperty); 
        
        
        chooser.setCurrentDirectory(currentDirectory);
        
        
        int returnVal = chooser.showOpenDialog(this);
         
        if (returnVal == JFileChooser.APPROVE_OPTION) { 
    
        	imageDataFile = chooser.getSelectedFile();
        	imageDataFileName = imageDataFile.getName();
            try {
                System.out.println("Loading map: "+ imageDataFile);
                backgroundImage = ImageIO.read(imageDataFile);
                if(backgroundImage != null) displayBackgroundImage = true;
              } catch (IOException e) {
              	System.out.println("Map image file load ERROR");
              }
         
        }
		update(); 
	}

	public void saveGraph() {
		// This code saves the graph to a file
		//save the graph to the current graph data file
		if(graphDataFile != null){
			if(backgroundImage != null && displayBackgroundImage){
				int imageWidth = backgroundImage.getWidth(this);
				int imageHeight = backgroundImage.getHeight(this);
				
				GraphParser.writeToFile(aGraph, 
						                graphDataFile,
						                imageDataFileName,
						                imageWidth,
						                imageHeight);

			}
			else {
			  GraphParser.writeToFile(aGraph, 
					                  graphDataFile,
					                  null,
					                  -1,
					                  -1);
			}
		}
		else{
			saveAsGraph();
		}
	}
	
	public void saveAsGraph(){
		//choose a file to save the graph to
		
		graphDataFile = null; //clear current graph data file
		
   	    String currentDirectoryProperty = System.getProperty("user.dir");

	    JFileChooser chooser = new  JFileChooser();
        File currentDirectory = new File(currentDirectoryProperty); 
        
        chooser.setCurrentDirectory(currentDirectory);
        	    	     
        int returnVal = chooser.showSaveDialog(this);
         
        if (returnVal == JFileChooser.APPROVE_OPTION) { 
            
        	File selectedFile = chooser.getSelectedFile();
        	
        	if(selectedFile != null) graphDataFile = selectedFile;
        	
        	saveGraph();        	
        }	
	}
}

