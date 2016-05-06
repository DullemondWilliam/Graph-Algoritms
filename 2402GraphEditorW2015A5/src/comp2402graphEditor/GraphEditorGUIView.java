package comp2402graphEditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//DISCLAIMER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//==========
//This code is designed for classroom illustration
//It may have intentional omissions or defects that are
//for illustration or assignment purposes
//
//That being said: Please report any bugs to me so I can fix them
//...Lou Nel (ldnel@scs.carleton.ca)
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


// This class represents the view of the graph editor
// It contains the menus and dispatching of menu events

public class GraphEditorGUIView extends JFrame implements ActionListener, KeyListener {

	private GraphEditor		editor;
    private JScrollPane     scrollpane;
	private	JMenuBar		aMenuBar = new JMenuBar();
	private	JMenu			fileMenu = new JMenu("File");
    private JMenu           pathMenu = new JMenu("Pathes");
    private JMenu           searchMenu = new JMenu("Search");
    private	JMenu			editMenu = new JMenu("Edit");
    private	JMenu			displayMenu = new JMenu("Display");

    //FILE MENU ITEMS
	private JMenuItem		newItem = new JMenuItem("New");    
	private JMenuItem		openItem = new JMenuItem("Open Graph...");
	private JMenuItem		openImageItem = new JMenuItem("Open Image...");
	private JMenuItem		saveItem = new JMenuItem("Save");
	private JMenuItem		saveAsItem = new JMenuItem("SaveAs...");
	
	//PATH MENU ITEMS
    private JMenuItem       findDijkstraPathItem = new JMenuItem("Find Path: Dijstra shortest path algm");
    private JMenuItem       findAStarLowerBoundPathItem = new JMenuItem("Find Path: AStar lower bound heuristic");
    private JMenuItem       findAStarManhattanPathItem = new JMenuItem("Find Path: AStar manhattan heuristic");
    private JMenuItem       findAStarNoHeuristicPathItem = new JMenuItem("Find Path: AStar no heuristic: h(n)=0");
    private JMenuItem       findAStarBestFirstGreedyPathItem = new JMenuItem("Find Path: AStar Greedy: g(n)=0");
    private JMenuItem       findSimpleGreedyPathItem = new JMenuItem("Find Path: Simple Greedy Best Neighbour");
    private JMenuItem       hasPathItem = new JMenuItem("has s-t Path");
    private JMenuItem       findSpanningTreeItem = new JMenuItem("Min Cost Spanning Tree");
    
    //SEARCH MENU ITEMS
    private JMenuItem       depthFirstSearchItem = new JMenuItem("depthFirstSearch");
    private JMenuItem       breadthFirstSearchItem = new JMenuItem("breathFirstSearch");

    //EDIT MENU ITEMS
    private JMenuItem       clearSelectionsItem = new JMenuItem("Clear Selections");
    private JMenuItem       deleteSelectionsItem = new JMenuItem("Delete Selected Items");
    private JMenuItem       deleteSelectedNodesItem = new JMenuItem("Delete Selected Nodes");
    private JMenuItem       deleteSelectedEdgesItem = new JMenuItem("Delete Selected Edges Items");
    private JMenuItem       renumberNodesItem = new JMenuItem("Re-number Nodes");
    private JMenuItem       completeGraphItem = new JMenuItem("Complete Graph");
    private JMenuItem       randomNodeLocationsItem = new JMenuItem("Random Node Locations");

    //DISPLAY MENU ITEMS
    private JRadioButtonMenuItem    displayNodeLabelsItem = new JRadioButtonMenuItem("node labels");
    private JRadioButtonMenuItem    displayAnimationItem = new JRadioButtonMenuItem("show animation");
    private JRadioButtonMenuItem    displayEdgeWeightsItem = new JRadioButtonMenuItem("edge weights");
    private JRadioButtonMenuItem    displayBackgroundImageItem = new JRadioButtonMenuItem("show background image");
    private JRadioButtonMenuItem    smallNodeSizeItem = new JRadioButtonMenuItem("small nodes");
    private JRadioButtonMenuItem    mediumNodeSizeItem = new JRadioButtonMenuItem("mediumm nodes");
    private JRadioButtonMenuItem    largeNodeSizeItem = new JRadioButtonMenuItem("large nodes");


    public static boolean shiftPressed = false; //true when the shift key is being pressed
    public static boolean controlPressed = false; //true when the control key is being pressed
    public static boolean altPressed = false; //true when the alt key is being pressed
    //CONSTRUCTOR========================================================================
	
	public GraphEditorGUIView (String title) {
		super(title);
		editor = new GraphEditor(this);
        scrollpane = new JScrollPane(editor);
		getContentPane().add(scrollpane, "Center");
		initialize();
	    editor.displayGraphOnly();
	}

	private void initialize() {
		setJMenuBar(aMenuBar);
		
		//FILE MENU
		aMenuBar.add(fileMenu);
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(openImageItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		newItem.addActionListener(this);
		openItem.addActionListener(this);
		openImageItem.addActionListener(this);
		saveItem.addActionListener(this);
		saveAsItem.addActionListener(this);

        
        //EDIT MENU
        editMenu.add(renumberNodesItem);
        editMenu.add(clearSelectionsItem);
        editMenu.add(new JSeparator());		
        editMenu.add(deleteSelectionsItem);
        editMenu.add(deleteSelectedEdgesItem);
        editMenu.add(deleteSelectedNodesItem);
        editMenu.add(new JSeparator());		
        editMenu.add(completeGraphItem);
        editMenu.add(randomNodeLocationsItem);
        
        completeGraphItem.addActionListener(this);
        deleteSelectionsItem.addActionListener(this);
        deleteSelectedEdgesItem.addActionListener(this);
        deleteSelectedNodesItem.addActionListener(this);
        clearSelectionsItem.addActionListener(this);
        renumberNodesItem.addActionListener(this);
        randomNodeLocationsItem.addActionListener(this);
       
        aMenuBar.add(editMenu);
        
        //DISPLAY MENU
        displayMenu.add(smallNodeSizeItem);
        smallNodeSizeItem.addActionListener(this);
        displayMenu.add(mediumNodeSizeItem);
        mediumNodeSizeItem.addActionListener(this);
        displayMenu.add(largeNodeSizeItem);
        largeNodeSizeItem.addActionListener(this);
        displayMenu.add(new JSeparator());		
        displayMenu.add(displayNodeLabelsItem);
		displayNodeLabelsItem.addActionListener(this);
        displayMenu.add(displayEdgeWeightsItem);
		displayEdgeWeightsItem.addActionListener(this);
        displayMenu.add(new JSeparator());		
        displayMenu.add(displayBackgroundImageItem);
		displayBackgroundImageItem.addActionListener(this);
        displayMenu.add(new JSeparator());		
        displayMenu.add(displayAnimationItem);
		displayAnimationItem.addActionListener(this);
        aMenuBar.add(displayMenu);
        
        //SEARCH MENU
        aMenuBar.add(searchMenu);
        searchMenu.add(depthFirstSearchItem);
        depthFirstSearchItem.addActionListener(this);
        searchMenu.add(breadthFirstSearchItem);
        breadthFirstSearchItem.addActionListener(this);
 
        //PATHES MENU
        aMenuBar.add(pathMenu);
        pathMenu.add(findDijkstraPathItem );
        pathMenu.add(findAStarLowerBoundPathItem  );
        pathMenu.add(findAStarManhattanPathItem  );
        pathMenu.add(findAStarNoHeuristicPathItem  );
        pathMenu.add(findAStarBestFirstGreedyPathItem  );
        pathMenu.add(findSimpleGreedyPathItem  );
        pathMenu.add(new JSeparator());		
        pathMenu.add(findSpanningTreeItem);
        pathMenu.add(new JSeparator());		
        pathMenu.add(hasPathItem);
		findDijkstraPathItem.addActionListener(this);
		findAStarLowerBoundPathItem.addActionListener(this);
		findAStarManhattanPathItem.addActionListener(this);
		findAStarNoHeuristicPathItem.addActionListener(this);
		findAStarBestFirstGreedyPathItem.addActionListener(this);
		findSimpleGreedyPathItem.addActionListener(this);
        findSpanningTreeItem.addActionListener(this);
        hasPathItem.addActionListener(this);      


 
        //needed for the scroll pane
		editor.setPreferredSize(new Dimension(editor.getWidth(), editor.getHeight() + 40 ));
                setSize(editor.getWidth(), editor.getHeight() + 40 );
                
        addKeyListener(this);

	}
	
	//KEYBOARD EVENT HANDLERS
	public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.VK_SHIFT)  shiftPressed = true;
        if (keyCode == KeyEvent.VK_CONTROL)controlPressed = true;
        if (keyCode == KeyEvent.VK_ALT)altPressed = true;		
		
	}
	public void keyReleased(KeyEvent event) {
    	int keyCode = event.getKeyCode();
    	 
        if (keyCode == KeyEvent.VK_SHIFT) shiftPressed = false;
        if (keyCode == KeyEvent.VK_CONTROL)controlPressed = false;
        if (keyCode == KeyEvent.VK_ALT) altPressed = false;
		
	}
    // Key typed event handler
	public void keyTyped(KeyEvent event) {
	//	System.out.println("Key typed");
		int keyChar = event.getKeyChar();
        if (keyChar == KeyEvent.VK_DELETE) {
        	editor.deleteSelectedItems();
        }
    }

	// MENU ITEM EVENT HANDLER
	public void actionPerformed(ActionEvent e) {
		//FILE MENU ITEMS
		if (e.getSource() == openItem)
			editor.openGraph();
		else if (e.getSource() == openImageItem){
			editor.openBackgroundImage();
			displayBackgroundImageItem.setSelected(GraphEditor.displayBackgroundImage);
			
		}
		else if (e.getSource() == newItem)
			editor.newGraph();
		else if (e.getSource() == saveItem)
			editor.saveGraph();
		else if (e.getSource() == saveAsItem)
			editor.saveAsGraph();
			
		//DISPLAY MENU ITEMS
 	    else if (e.getSource() == smallNodeSizeItem){
			editor.displayNodeSize(Node.smallNodeSize);
			mediumNodeSizeItem.setSelected(false);
			largeNodeSizeItem.setSelected(false);
 	    }
 	    else if (e.getSource() == mediumNodeSizeItem){
			editor.displayNodeSize(Node.mediumNodeSize);
			smallNodeSizeItem.setSelected(false);
			largeNodeSizeItem.setSelected(false);
 	    }
 	    else if (e.getSource() == largeNodeSizeItem){
			editor.displayNodeSize(Node.largeNodeSize);
			smallNodeSizeItem.setSelected(false);
			mediumNodeSizeItem.setSelected(false);
 	    }

 	    else if (e.getSource() == displayNodeLabelsItem)
			editor.displayNodeLabels(displayNodeLabelsItem.isSelected());
 	    else if (e.getSource() == displayBackgroundImageItem)
			editor.displayBackgroundImage(displayBackgroundImageItem.isSelected());
	    else if (e.getSource() == displayAnimationItem)
			editor.displayAnimation(displayAnimationItem.isSelected());
	    else if (e.getSource() == displayEdgeWeightsItem)
			editor.displayEdgeWeights(displayEdgeWeightsItem.isSelected());

		//SEARCH MENU ITEMS
	    else if (e.getSource() == depthFirstSearchItem)
		  editor.depthFirstSearch();			
	    else if (e.getSource() == breadthFirstSearchItem)
		  editor.breadthFirstSearch();
		  
		//PATH MENU ITEMS			
        else if (e.getSource() == findDijkstraPathItem )
            editor.findPath(Graph.PathFindingMethod.Dijkstra);
        else if (e.getSource() == findAStarLowerBoundPathItem )
            editor.findPath(Graph.PathFindingMethod.AStarLowerBound);
        else if (e.getSource() == findAStarManhattanPathItem )
            editor.findPath(Graph.PathFindingMethod.AStarManhattan);
        else if (e.getSource() == findAStarNoHeuristicPathItem )
            editor.findPath(Graph.PathFindingMethod.AStarZeroCostHeuristic);
        else if (e.getSource() == findAStarBestFirstGreedyPathItem )
            editor.findPath(Graph.PathFindingMethod.AStarBestFirstGreedy);
        else if (e.getSource() == findSimpleGreedyPathItem )
            editor.findPath(Graph.PathFindingMethod.GreedyBestNeighbour);
        else if (e.getSource() == findSpanningTreeItem)
            editor.findSpanningTree();
        else if (e.getSource() == hasPathItem)
            editor.hasSTPath();          
            
        //EDIT MENU ITEMS         
        else if (e.getSource() == renumberNodesItem)
            editor.renumberNodes();
        else if (e.getSource() == clearSelectionsItem)
            editor.clearSelections();
        else if (e.getSource() == deleteSelectionsItem)
            editor.deleteSelectedItems();
        else if (e.getSource() == deleteSelectedEdgesItem)
            editor.deleteSelectedEdges();
        else if (e.getSource() == deleteSelectedNodesItem)
            editor.deleteSelectedNodes();
        else if (e.getSource() == completeGraphItem)
            editor.completeGraph();
        else if (e.getSource() == randomNodeLocationsItem)
            editor.randomNodeLocation();

	}
	
	public void forceUpdate(){
		update(getGraphics()); //to force GUI update 
	}


}