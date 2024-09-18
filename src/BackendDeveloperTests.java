import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.List;
import javafx.application.Platform;

public class BackendDeveloperTests extends ApplicationTest{

  /**
   * Tests loading a file in and inserting the nodes from the file into a graph. There are two cases
   * that this test method checks.
   * 
   * 1) Test loading in a valid file. Then, call getListOfAllLocations() to make sure that the nodes
   * were inserted into the graph.
   * 2) Test loading in a file that cannot be found. Calling loadGraphData() should throw an
   * IOException
   */
  @Test
  public void testLoadGraph() {
    BackendInterface backend = new Backend();
    
    // Test 1: Shouldn't throw an error
    try {
      backend.loadGraphData("campus.dot");
    }catch(Exception e) {
      Assert.fail("An error was thrown when passing a valid file into loadGraphData()");
    }
    Assertions.assertEquals(true, backend.getListOfAllLocations().contains("Memorial Union"), "Memorial Union does not appear in the graph when it should.");
    Assertions.assertEquals(true, backend.getListOfAllLocations().contains("Campus Cars"), "Campus Cars does not appear in the graph when it should.");
    Assertions.assertEquals(true, backend.getListOfAllLocations().contains("Mosse Humanities Building"), "Mosse Humanities Building does not appear in the graph when it should.");
    
    // Test 2: Should throw an error
    try {
      backend.loadGraphData("DNE.dot");
    }catch(IOException e) {}
    catch(Exception e) {
      Assert.fail("An unexpected error was thrown when passing an invalid file into loadGraphData()");
    }
    Assert.fail("No error was thrown when one should have been thrown.");
  }
  
  /**
   * Tests finding the shortest path between a start node and an end node. Two cases should be 
   * considered.
   * 
   * 1) Load in a valid file. Call findShortestPath() on an instance where start and end have a path
   * between them. Check that the shortest path matches what is expected.
   * 2) Load in a valid file. Call findShortestPath() on an instance where start and end do NOT have
   * a path between them. The returned list should be empty.
   */
  @Test
  public void testShortestPath() {
    BackendInterface backend = new Backend();
    try {
      backend.loadGraphData("campus.dot");
    }catch(Exception e) {
      Assert.fail("An unexpected error was thrown when passing an invalid file into loadGraphData()");
    }
    
    // Test 1: Start = Jorns Hall, End = Adams Residence Hall
    List<String> returnList = backend.findShortestPath("Jorns Hall", "Adams Residence Hall");
    Assertions.assertEquals(2, returnList.size(), "The returnList size is incorrect");
    Assertions.assertEquals("Jorns Hall", returnList.get(0), "Incorrect shortest path between Jorns Hall and Adams Residence Hall");
    Assertions.assertEquals("Adams Residence Hall", returnList.get(1), "Incorrect shortest path between Jorns Hall and Adams Residence Hall");
    
    // Test 2: Start = Jorns Hall, End = DNE
    List<String> returnList2 = backend.findShortestPath("Jorns Hall", "DNE");
    Assertions.assertEquals(0, returnList2.size(), "The returnList size is incorrect");
    
  }
  
  /**
   * Tests finding the travel time along the shortest path in between two locations. Two cases 
   * should be considered. 
   * 
   * 1) Load in a valid file. Call getTravelTimesOnPath() on an instance where start and end have a 
   * path between them. Check that the travel time matches what is expected.
   * 2) Load in a valid file. Call getTravelTimesOnPath() on an instance where start and end do NOT
   * have a path between them. The returned list should be empty.
   */
  @Test
  public void testTravelTime() {
    BackendInterface backend = new Backend();
    try {
      backend.loadGraphData("campus.dot");
    }catch(Exception e) {
      Assert.fail("An unexpected error was thrown when passing an invalid file into loadGraphData()");
    }
    
    // Test 1: Start = Memorial Union, End = Science Hall
    List<Double> travelTime = backend.getTravelTimesOnPath("Memorial Union", "Science Hall");
    Assertions.assertEquals(1, travelTime.size(), "travelTime size is incorrect");
    Assertions.assertEquals(105.8, travelTime.get(0), "Incorrect travel time between Memorial Union and Science Hall");
    
    // Test 2: Start = Memorial Union, End = DNE
    List<Double> travelTime2 = backend.getTravelTimesOnPath("Memorial Union", "DNE");
    Assertions.assertEquals(0, travelTime2.size(), "travelTime size is incorrect");
  }
  
  /**
   * Tests finding the most distant location from the inputed start location. Two cases should be
   * considered. 
   * 
   * 1) Load in a valid file. Call getMostDistantLocation() on an instance where start has another 
   * connected to it. Check that the most distant location is what it should be.
   * 2) Load in a valid file. Call getMostDistantLocation() on an instance where start doesn't 
   * exist.
   */
  @Test
  public void testGetMostDistantLocation() {
    BackendInterface backend = new Backend();
    try {
      backend.loadGraphData("campus.dot");
    }catch(Exception e) {
      Assert.fail("An unexpected error was thrown when passing an invalid file into loadGraphData()");
    }
    
    // Test 1: Start = Memorial Union, End should be Jorns Hall
    Assertions.assertEquals("Jorns Hall", backend.getMostDistantLocation("Memorial Union"), 
        "The furthest node from Memorial Union should be Jorns Hall, but it wasn't.");
    
    // Test 2: Start = DNE
    Assertions.assertThrows(NoSuchElementException.class, () -> backend.getMostDistantLocation("DNE"));
  }
  
  public void testShortestPathIntegration() {
    Frontend frontend = new Frontend();
    Backend backend = new Backend();
    
    frontend.setBackend(backend);
    Pane pane = new Pane();
    frontend.createShortestPathControls(pane);
    
    Assertions.assertEquals(true, resultArea.getText().contains("Memorial Union -> Science Hall"), "The result area doesn't contain the correct text.");
  }
  
  public void testFurthestDestinationIntegration() {
    Frontend frontend = new Frontend();
    Backend backend = new Backend();
    
    frontend.setBackend(backend);
    
    try {
      backend.loadGraphData("campus.dot");
    }catch(Exception e) {
      Assert.fail("An unexpected error was thrown when passing an invalid file into loadGraphData()");
    }
    
    // Test the shortest path between Jorns Hall and Adams Residence Hall
    Platform.runLater(() -> {
      frontend.createAllControls(testPane);
    });
    Thread.sleep(1000); // wait for the updates to complete
    
    //Start = Memorial Union, End should be Jorns Hall
    String backendResult = backend.getMostDistantLocation("Memorial Union");
    Pane pane = new Pane();
    String frontendResult = frontend.createFurthestDestinationControls(pane);
    Assertions.assertEquals(true, backendResult == frontendResult, "The frontend and backend do not match up.");
  }
}
