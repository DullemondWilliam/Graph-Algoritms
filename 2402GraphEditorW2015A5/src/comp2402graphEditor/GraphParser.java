package comp2402graphEditor;


import java.io.*;

public class GraphParser  {
//           ===========
/*This class is responsible for loading graphs from data files and
 *writing graphs to data files
 *The graph parser uses an XML-like encoding of graphs which should keep the 
 *graphs themselves independent of the actual code classes that implement the graph
 */
   //PARSING VARIALBLES=====================================
   //Tags used for XML like export
    
   final public static String XMLCommentTag = "<!--";
   final public static String XMLCommentEndTag = "-->";

   final public static String XMLDocumentTag = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
   final public static String XMLStartTag = "<graphXML>";
   final public static String XMLEndTag = "</graphXML>";
 

   //Array of property tag pairs like
   //<node>, </node>
    
   public static String [] propertyTagPairs = {
   	XMLCommentTag, XMLCommentEndTag,
   	XMLStartTag, XMLEndTag
   	};

   //Array of singleton property tags like
   // <selected/>
   public static String [] singletonPropertyTags = {
   };

   
   public static boolean isTag(String inputString) {
   //answer true if inputString is a legitimate tag for this class
     for (int i = 0; i< propertyTagPairs.length; i++)
       if(inputString.toLowerCase().startsWith(propertyTagPairs[i])) return true;
   
     for (int i = 0; i< singletonPropertyTags.length; i++)
       if(inputString.toLowerCase().startsWith(singletonPropertyTags[i])) return true;
   
     return false;
   }    
   //============End Parsing Variables==================================

    //=================STATIC VARIABLES================================
    public static String indentTab =  "   "; //to indent the tags written to file
    
    
    //=================STATIC METHODS==================================  
    
        
    
    public static Graph parseFromFile(File aFile){
    //Parse in a version from the input file
    //The file is expected to have the following version information
    // The file is expected to have the following outer XML structure    	
        // <graphXML>
    	//   <image>
    	//      ...optional background image file information
    	//   </image>
        //   <graph>
        //      ...
        //   </graph>
        // </graphXML>
    
      
	  Graph parsedModel = new Graph();
    
      String inputLine; //current input line
      String dataString = null;
      
      BufferedReader inputFile;
    
    try {
 
  	    inputFile = new BufferedReader(new FileReader(aFile));
       
    	//parse until we get to the closing tag
        while((inputLine = inputFile.readLine()) != null) 
          { 
        	 //System.out.println(inputLine);
             dataString = inputLine.trim();
             
             
             if(dataString.startsWith(XMLStartTag)|| dataString.startsWith(XMLEndTag)){
            	 //catch the XMLGraph start and end tag but don't use them
                 //they are currently ignored
            	 //in the future do some validation here
            	 
             }
             
             
             else if(dataString.startsWith(Graph.startTag)){
              	//parse a graph
              	parsedModel = Graph.parseFromFile(Graph.startTag, inputFile);
              }
             
             //see if the dataString is of the form "<tag> data </tag>"
             //else if(parsedModel.parsePropertyString(dataString)) {}
             //see if the dataString is of the form "<tag/>
             //else if(parsedModel.parseSingletonPropertyString(dataString)) {} 
          }
    	//closing tag reached
 	   inputFile.close();
        
        
     //catch file IO errors   
       } catch (EOFException e) {
            System.out.println("VERSION PARSE Error: EOF encountered, file may be corrupted.");
            return null;
       } catch (IOException e) {
            System.out.println("VERSION PARSE Error: Cannot read from file.");
            return null;
       }    	
	   
	   
	   if(parsedModel == null) {
	   	  System.out.println("ERROR: Graph: Parsed graph not created");
	   	  return null;
	   	
	   }
	   
	   
	   return parsedModel;

    } 
     

     
     public static void writeToFile(Graph graph, 
    		                        File aFile,
    		                        String aBackgroundImageFileName,
    		                        int backgroundImageWidth,
    		                        int backgroundImageHeight){
    //                 =====
    	//save the chartModel to disk
       PrintWriter outputFile = null;
    	
     try{
    	 
    	outputFile = new PrintWriter(new FileWriter(aFile));

     
        String indent = "";
        
        String warningString1 = "WARNING: This file was produced by its parent application:";
        String warningString3 = "and is not meant to be edited manually.  Doing so will likely ";
        String warningString4 = "make it unreadable. The file format is not an open standard and is";
        String warningString5 = "subject to change without notice";
    	
    	
    	outputFile.println(XMLDocumentTag);
    	

    	String propertyString = XMLCommentTag + " " + warningString1 + " " + XMLCommentEndTag;
    	outputFile.println(indent + propertyString);
        propertyString = XMLCommentTag + " " + warningString3 + " " + XMLCommentEndTag;
    	outputFile.println(indent + propertyString);
        propertyString = XMLCommentTag + " " + warningString4 + " " + XMLCommentEndTag;
    	outputFile.println(indent + propertyString);
        propertyString = XMLCommentTag + " " + warningString5 + " " + XMLCommentEndTag;
    	outputFile.println(indent + propertyString);
 
    	outputFile.println(XMLStartTag);

    	String tab = "   ";
    	if(aBackgroundImageFileName != null && 
    			backgroundImageWidth >0 &&
    			backgroundImageHeight > 0){
    		
    		graph.setBackgroundImageInfo(aBackgroundImageFileName,
    				backgroundImageWidth, backgroundImageHeight);
  		
    	}
    	
        graph.writeToFile(indent + tab, outputFile);

    	outputFile.println(XMLEndTag);

        outputFile.close();
        
        } catch (FileNotFoundException e) { 
            System.out.println("Error: Cannot open file" + outputFile + " for writing.");
            
        } catch (IOException e) { 
            System.out.println("Error: Cannot write to file: " + outputFile);
            
        }
        

    }       
     
    //=================INSTANCE VARIABLES==============================  
    


    //=================CONSTRUCTION====================================  
	
	public GraphParser(){
		

	}
	
	//==============================================================

} //end class GraphParser
