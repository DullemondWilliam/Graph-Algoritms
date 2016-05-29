package comp2402graphEditor;

import java.io.*;

public class GraphParser {
	/* This class is responsible for loading graphs from data files and writing
	 * graphs to data files The graph parser uses an XML-like encoding of graphs
	 * which should keep the graphs themselves independent of the actual code
	 * classes that implement the graph
	 */
	final public static String XMLCommentTag 	= "<!--";
	final public static String XMLCommentEndTag = "-->";

	final public static String XMLDocumentTag 	= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	final public static String XMLStartTag 	= "<graphXML>";
	final public static String XMLEndTag 	= "</graphXML>";

	public static String[] propertyTagPairs = {
			XMLCommentTag, XMLCommentEndTag, XMLStartTag, XMLEndTag };

	public static String[] singletonPropertyTags = {};

	public static boolean isTag(String inputString) {
		// answer true if inputString is a legitimate tag for this class
		for (int i = 0; i < propertyTagPairs.length; i++){
			if (inputString.toLowerCase().startsWith(propertyTagPairs[i])){
				return true;
			}
		}
		for (int i = 0; i < singletonPropertyTags.length; i++){
			if (inputString.toLowerCase().startsWith(singletonPropertyTags[i])){
				return true;
			}
		}
		return false;
	}
	
	public static String indentTab = "   "; 
	public static Graph parseFromFile(File aFile) {
		Graph parsedModel = new Graph();

		String inputLine; 
		String dataString = null;

		BufferedReader inputFile;

		try {
			inputFile = new BufferedReader(new FileReader(aFile));

			while ((inputLine = inputFile.readLine()) != null) {
				dataString = inputLine.trim();

				if (dataString.startsWith(XMLStartTag) || dataString.startsWith(XMLEndTag)) {
					// they are currently ignored in the future do some validation here
				}else if (dataString.startsWith(Graph.startTag)) {
					parsedModel = Graph.parseFromFile(Graph.startTag, inputFile);
				}
			}
			inputFile.close();
		} catch (EOFException e) {
			System.out.println("VERSION PARSE Error: EOF encountered, file may be corrupted.");
			return null;
		} catch (IOException e) {
			System.out.println("VERSION PARSE Error: Cannot read from file.");
			return null;
		}

		if (parsedModel == null) {
			System.out.println("ERROR: Graph: Parsed graph not created");
		}
		return parsedModel;
	}

	public static void writeToFile(Graph graph, File aFile, String imageFileName,
			 int imageWidth, int imageHeight) {
		
		PrintWriter outputFile = null;
		try {
			outputFile = new PrintWriter(new FileWriter(aFile));

			String warningString1 = "WARNING: This file was produced by its parent application:";
			String warningString3 = "and is not meant to be edited manually.  Doing so will likely";
			String warningString4 = "make it unreadable. The file format is not an open standard";
			String warningString5 = "and is subject to change without notice";

			outputFile.println(XMLDocumentTag);

			String output = XMLCommentTag + " " + warningString1 + " " + XMLCommentEndTag;
			outputFile.println(output);
			output = XMLCommentTag + " " + warningString3 + " " + XMLCommentEndTag;
			outputFile.println(output);
			output = XMLCommentTag + " " + warningString4 + " " + XMLCommentEndTag;
			outputFile.println(output);
			output = XMLCommentTag + " " + warningString5 + " " + XMLCommentEndTag;
			outputFile.println(output);

			outputFile.println(XMLStartTag);

			if (imageFileName != null && imageWidth > 0
					&& imageHeight > 0)
				graph.setBackgroundImageInfo(imageFileName,
						imageWidth, imageHeight);
			
			graph.writeToFile(indentTab, outputFile);
			outputFile.println(XMLEndTag);
			outputFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: Cannot open file" + outputFile);
		} catch (IOException e) {
			System.out.println("Error: Cannot write to file: " + outputFile);
		}
	}
}