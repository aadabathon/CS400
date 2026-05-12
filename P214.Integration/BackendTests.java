import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.beans.Transient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class BackendTests { //My test class for my backend Implementation

  /**
   * roleTest1:
   * We verify that the backend can load data from a simple DOT file
   * (nodes and a single edge) and then list all locations it knows about.
   * 
   * Methods exercised here: loadGraphData(), getListOfAllLocations()
   */

  @Test
  public void roleTest1() throws IOException {
    Graph_Placeholder graph = new Graph_Placeholder();
    Backend backend = new Backend(graph); 
    String dot = ""
        + "digraph G {\n"
        + "  \"A\";\n"
        + "  \"B\";\n"
        + "  \"A\" -> \"B\" [weight=\"2.5\"];\n"
        + "}\n";
    Path tmp = Files.createTempFile("tiny_graph", ".dot");
    Files.write(tmp, dot.getBytes(StandardCharsets.UTF_8)); //Files.write write bytes, not strings so we have to go back an forth to test.

    assertDoesNotThrow(() -> backend.loadGraphData(tmp.toString())); // Should load without throwing

    List<String> locations = backend.getListOfAllLocations(); 
    assertNotNull(locations);  // After load, make sure locations include A and B 
    assertTrue(locations.contains("A"));
    assertTrue(locations.contains("B"));
  }

  /**
   * roleTest2:
   * We verify shortest-path queries return a sensible path and
   * corresponding per-edge times using the placeholder's built-in path.
   * 
   * Methods exercised here: findLocationsOnShortestPath(), findTimesOnShortestPath()
   * 
   */
  @Test
  public void roleTest2() {
    Graph_Placeholder graph = new Graph_Placeholder();
    Backend backend = new Backend(graph);

    List<String> path = backend.findLocationsOnShortestPath( 
        "Union South", "Weeks Hall for Geological Sciences");
    assertNotNull(path); //Path cant be null
    assertEquals(3, path.size(), "Expected 3 nodes"); //Placeholder has three nodes
    assertEquals("Union South", path.get(0)); //Union South should be the first element
    assertEquals("Weeks Hall for Geological Sciences", path.get(path.size() - 1)); // Weeks hall should be the last

    List<Double> times = backend.findTimesOnShortestPath(
        "Union South", "Weeks Hall for Geological Sciences");
    assertNotNull(times); //Times cant be null
    assertEquals(path.size() - 1, times.size(), "Times should align with path edges"); //
    assertTrue(times.stream().allMatch(t -> t > 0.0)); // Lambdaaaa, makes sure for all t in streams is greater than 0
  }

  /**
   * roleTest3:
   * We verify the "closest destination" returns properly across multiple
   * start locations, and that invalid inputs throw NoSuchElementException.
   * 
   * Methods exercised here: getClosestDestinationFromAll()
   * 
   */

  @Test
  public void roleTest3() throws IOException { //Because the placeholder graph is pretty limited, I can't test getClosestDestinationFromAll() in a rigorous manner. I can however test error throwing and return correctness
    Graph_Placeholder graph = new Graph_Placeholder();
    Backend backend = new Backend(graph);

    String dot = ""
        + "digraph G {\n"
        + "  \"Union South\";\n"
        + "  \"Computer Sciences and Statistics\";\n"
        + "  \"Weeks Hall for Geological Sciences\";\n"
        + "}\n";

    Path tmp = Files.createTempFile("seed_placeholder_nodes", ".dot");
    Files.write(tmp, dot.getBytes(StandardCharsets.UTF_8));
    backend.loadGraphData(tmp.toString());

    List<String> starts = new ArrayList<>();
    starts.add("Union South");
    starts.add("Computer Sciences and Statistics");

    String destination = backend.getClosestDestinationFromAll(starts);
    assertNotNull(destination, "Should find some common destination");
    assertFalse(destination.trim().isEmpty()); //Destination should be findable and not empty

    List<String> badStarts = new ArrayList<>();
    badStarts.add("Nowhere");
    assertThrows(java.util.NoSuchElementException.class,
        () -> backend.getClosestDestinationFromAll(badStarts)); //Lamdbaaaaa, We expect getClosestDestinationFromAll to throw NSEE when badStarts is passed
    } 

//P214 INTEGRATION TESTS BELOW:

  private FrontendInterface makeIntegratedFrontend() throws IOException { //Build a dummy integrated stack upon which we will test
    GraphADT<String, Double> graph = new DijkstraGraph<>();
    BackendInterface backend = new Backend(graph);
    backend.loadGraphData("campus.dot");
    return new Frontend(backend);
}
   
@Test

/**
 * Test1: verifies that Frontend + Backend + DijkstraGraph can
 * compute a shortest path between two valid locations and embed that
 * result into the HTML response.
 */

public void testIntegrationShortestPathBasic() throws IOException {
    FrontendInterface frontend = makeIntegratedFrontend();
    String html = frontend.generateShortestPathResponseHTML("Union South", "Weeks Hall for Geological Sciences");


    assertTrue(html.contains("Shortest path from"), // Should contain the start and end
            "HTML should mention that it is showing a shortest path");
    assertTrue(html.contains("Union South"),
            "HTML should contain the starting location");
    assertTrue(html.contains("Weeks Hall for Geological Sciences"),
            "HTML should contain the destination location");

    assertFalse(html.contains("No path found"), //There should be no error message
            "Should not show the 'No path found' message for a valid pair");
      }

/**
 * Test2: verifies that an invalid or unknown start location is
 * handled in the proper manner
 */

@Test
public void testIntegrationShortestPathNoPath() throws IOException {
    FrontendInterface frontend = makeIntegratedFrontend();

    String html = frontend.generateShortestPathResponseHTML( //Use strings not in campus.dot
            "NotARealBuilding123",
            "Union South"
    );
    
    assertTrue(html.contains("No path found"), //Should contain this string arg.
            "HTML should report that no path was found for invalid start");
      }

/**
 * Test3: verifies that the closest-destination-from-all feature
 * works end-to-end.
 */

@Test
public void testIntegrationClosestDestinationBasic() throws IOException {
    FrontendInterface frontend = makeIntegratedFrontend();

    String starts = "Union South, Computer Sciences and Statistics";
    String html = frontend.generateClosestDestinationsFromAllResponseHTML(starts);

 
    assertTrue(html.contains("Union South"), // the response should contain each start location
            "HTML should list Union South as one of the starts");
    assertTrue(html.contains("Computer Sciences and Statistics"),
            "HTML should list Computer Sciences and Statistics as one of the starts");

    assertTrue(html.contains("Closest common destination:"), //Should also contain this string arg.
            "HTML should mention the closest common destination");
      }

/**
 * Test4: verifies that when all provided start locations are
 * invalid (do not exist in the graph), the error is handled well with a message instead of a crash.
 */

@Test
public void testIntegrationClosestDestinationInvalidStarts() throws IOException {
    FrontendInterface frontend = makeIntegratedFrontend();

    String starts = "FakeBuildingOne,FakeBuildingTwo"; //Invalid starts
    String html = frontend.generateClosestDestinationsFromAllResponseHTML(starts);

    assertTrue(html.contains("Sorry, an error occurred while computing the closest destination."), //Should contain the string arg.
            "HTML should show a generic error message for invalid start locations");
    }
}
