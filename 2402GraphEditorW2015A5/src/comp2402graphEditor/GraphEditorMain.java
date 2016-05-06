package comp2402graphEditor;


import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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


// This class represents launch point for the application

public class GraphEditorMain {


	public static void main(String args[]) {
	//                 =====
		
	boolean RUN_WITH_GUI = true; //set to true to run with GUI
	
	if(RUN_WITH_GUI){
		//launch GUI graph editor

		GraphEditorGUIView frame =  new GraphEditorGUIView("Graph Editor");
                      
		// Add the usual window listener (for closing ability)
		frame.addWindowListener(
			new WindowAdapter() {
 				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);

                //show the frame
		frame.setVisible(true);
	}
	else{
	 //run without GUI
		//load a sample graph from data file in current directory,
	    //run an algorithm on the graph which marks some items in the graph as selected
		//write the resulting graph to the output file
		
		Graph aGraph = null;
		
		String graphInputDataFilename = "testGraph.xml";
		String graphOutputDataFilename = "testOutput.xml";
		
		//read the graph from an xml file
		File inputFile = new File(graphInputDataFilename);
		System.out.println("File "+ graphInputDataFilename + " exists: " + inputFile.exists());
		aGraph = GraphParser.parseFromFile(inputFile);
		System.out.println(aGraph);
		
		//use the graph data structures
		
		if(aGraph.getNodes().size() < 2) return; //DON'T  path finding the graph is too small
		  
			
		//run a path finding algorithm on the graph.
		  
		Node source;
		Node target;
		  
		  //If two nodes are already selected in the graph then use those as source and target
		  ArrayList<Node> selectedNodes = aGraph.selectedNodes();
		  if(selectedNodes.size()==2){
			  source = selectedNodes.get(0);
			  target = selectedNodes.get(1);
		  }
		  else {
			  //use the first and last node added to graph as arbitrary source and target for
			  //pathfinding
			  aGraph.clearSelections(); //clear any existing selections
			  ArrayList<Node> theNodes = aGraph.getNodes();
			  source = theNodes.get(0);
			  target = theNodes.get(theNodes.size()-1);
		  }
		
		//run a source-target path finding algorithm on the graph
		ArrayList<Edge> stPath;
		Graph.PathFindingMethod methodToTry = Graph.PathFindingMethod.AStarBestFirstGreedy;
		
		stPath = aGraph.findPathBetween(source, target, methodToTry);
		
		//The path finding algorithm will have marked nodes examined, selected the nodes and edges
		//on the path and returned the edge set forming the path.
		
		//Here you can investigate the various properties of the graph and path found
		
		
		//Print number of edges in path
		System.out.println("Path finding method: " + Graph.nameOfMethod(methodToTry));
		System.out.println("Number of edges in path: " + stPath.size());
		
		//Print length of found path
		int pathLength = 0;
		for(Edge e : stPath) pathLength += e.getWeight();		
		System.out.println("Path length: " + pathLength);
		
		//Print number of nodes marked (examined) by algorithm
		int numOfMarkedNodes = 0;
		for(Node n : aGraph.getNodes()) if(n.isMarked()) numOfMarkedNodes++;		
		System.out.println("Number Of Examined (marked) Nodes: " + numOfMarkedNodes);
		
		//Confirm whether path reached from source to target.
		Node currentNode = source;
		for(Edge e: stPath){
			currentNode = e.otherEndFrom(currentNode);
		}
		if(currentNode == target)
			System.out.println("CONFIRM: target node reached");
		else
			System.out.println("ERROR: Path does not reach target node");
		
		
		//write resulting graph to output file
		//this should have the path selected and the examined nodes marked
		File outputFile = new File(graphOutputDataFilename);
		GraphParser.writeToFile(aGraph, outputFile, null, -1, -1);
		
	
	}
	
	
	} //end main
}