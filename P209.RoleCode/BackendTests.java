
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
