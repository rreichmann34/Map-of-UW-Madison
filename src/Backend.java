import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Backend implements BackendInterface{
  
  /**
   * Stores a series of nodes and edges. Each node in the graph is represented by a string
   * and each edge has a weight, represented by a double. 
   */
  private GraphADT<String, Double> graph;
  
  /**
   * Stores the file that is entered when loading the graph's data. 
   */
  private File file;
  
  /**
   * Backend constructor that initializes the graph.
   */
  public Backend() {
    graph = new GraphPlaceholder();
  }
  
  /**
   * Takes in a file and loads in all of the nodes and edges stored within it.
   * Every line in the file should be in the following format:
   * "nodeName" -> "neighborNodeName" [seconds="double"];
   * 
   * @param filename the name of the file we are loading nodes and edges into
   * the graph from
   * @throws IOException if filename is not a file that can be found
   */
  public void loadGraphData(String filename) throws IOException{
    file = new File(filename);
    Scanner scnr = new Scanner(file);

    // Loop through every line in the provided file
    while(scnr.hasNextLine()) {
      String line = scnr.nextLine();

      if(line.contains("->")) {
        String[] temp = line.split("->");
        String start = temp[0].trim();
        start = start.replace("\"","");

        // Insert start into graph and allLocations if it's not already in there
        if(!allLocations.contains(start)){
          allLocations.add(start);
        }

        if(!graph.containsNode(start)) {
          graph.insertNode(start);
        }

        // Insert neighbor node into graph and allLocations if it's not already in there
        String[] temp2 = temp[1].split("\"");
        String end = temp2[1].trim();
        if(!graph.containsNode(end)) {
          graph.insertNode(end);
        }

        if(!allLocations.contains(end)){
          allLocations.add(end);
        }

        // Only the edge would need to be added to the graph because allLocations does not contain edges
        String[] temp3 = temp2[2].split("=");
        String edge = temp3[1].trim();
        edge = edge.replaceAll("];", "");
        Double edgeValue = Double.parseDouble(edge);
        if(!graph.containsEdge(start, end)) {
          graph.insertEdge(start,  end,  edgeValue);
        }
      }
    }

    scnr.close();

  }
  
  /**
   * Gets a list of all the nodes in the graph by scanning through the graph again. 
   * 
   * @return a list of all of the nodes in the graph
   */
  public List<String> getListOfAllLocations(){
    List<String> returnList = new LinkedList<>();
    Scanner scnr = null;
    
    try {
      scnr = new Scanner(file, "utf-8");
    }catch(Exception e) {
      System.out.println("File not found");
    }
    
    // Skip first row
    if(scnr.hasNext()) {
      scnr.nextLine();
    }
    
    while(scnr.hasNext()) {
      String toAdd = scnr.nextLine();
      int i = toAdd.indexOf("->");
      
      toAdd = toAdd.substring(0, i).trim();
      toAdd = toAdd.substring(1, toAdd.length() - 1);
      
      // Don't add toAdd to the returnList if it's already in the returnList
      if(!toAdd.equals(returnList.get(returnList.size() - 1))) {
        returnList.add(toAdd);
      }
    }
    
    scnr.close();
    return returnList;
    
  }
  
  /**
   * Finds the shortest path from the specified start and end locations. Relies on 
   * the graph's shortestPathData() method to work.
   * 
   * @param startLocation the place we are trying to find the shortest path from
   * @param endLocation the final destination we are trying to reach from startLocation
   * @return a list of strings representing the nodes we visit when taking the shortest
   * path between startLocation and endLocation. The return list includes the startLocation
   * and the endLocation in the list. Returns an empty list if no path exists.
   */
  public List<String> findShortestPath(String startLocation, String endLocation){
    return graph.shortestPathData(startLocation, endLocation);
  }
  
  /**
   * Gets a list of the edges in between the shortestPath. This method does so by 
   * relying on findShortestData().
   * 
   * @param startLocation the place we are trying to find the shortest path from
   * @param endLocation the final destination we are trying to reach from startLocation
   * @return a list of doubles representing the edges in between the nodes along the
   * shortest path. Returns an empty list if no path exists.
   */
  public List<Double> getTravelTimesOnPath(String startLocation, String endLocation){
    List<Double> returnList = new LinkedList<>();
    List<String> shortestPathList = findShortestPath(startLocation, endLocation);
    
    // Loop through all of the nodes in the list returned from findShortestPath()
    for(int i = 0; i < shortestPathList.size() - 1; i++) {
      Double toAdd = graph.getEdge(shortestPathList.get(i), shortestPathList.get(i + 1));
      returnList.add(toAdd);
    }
    
    return returnList;
  }
  
  /**
   * Calculates the total amount of time spent on the shortest path from startLocation to 
   * endLocation
   * 
   * @param startLocation the place we are trying to find the shortest path from
   * @param endLocation the final destination we are trying to reach from startLocation
   * @return a double representing the total time in seconds spent on the shortest path
   * between startLocation and endLocation
   */
  private Double calculateTime(String startLocation, String endLocation) {
    List<Double> times = getTravelTimesOnPath(startLocation, endLocation);
    
    Double d = 0.0;
    // Loop through every double in the list returned by getTravelTimesOnPath()
    for(Double time: times) {
      d += time;
    }
    
    return d;
  }
  
  /**
   * Gets the most distant location from a specified startLocation. This method does so by 
   * exploring the shortest path to every node from the specified startLocation.
   * 
   * @param startLocation the place we are trying to find the furthest node from
   * @return a string representing the furthest node from the start location in the graph. Returns 
   * an empty string if there are no edges away from the startLocation
   * @throws NoSuchElementException if the startLocation is not in the graph
   */
  public String getMostDistantLocation(String startLocation) throws NoSuchElementException{
    if(!graph.containsNode(startLocation)) {
      throw new NoSuchElementException(startLocation + " does not exist");
    }
    List<String> allLocations = getListOfAllLocations();
    Double max = 0.0;
    String maxPath = "";
    
    // Loop through all of the locations. If the current node we are at is not the start node, then
    // calculate the time it takes to get to the current node. If it is greater than the current
    // max path, set the current path to be the max path.
    for(int i = 0; i < allLocations.size(); i++) {
      if(!startLocation.equals(allLocations.get(i))) {
        Double currentTime = calculateTime(startLocation, allLocations.get(i));
        if(currentTime > max) {
          max = currentTime;
          maxPath = allLocations.get(i);
        }
      }
    }
    
    return maxPath;
  }
}
