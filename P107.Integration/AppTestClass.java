import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

/**
 * Integration tests for iSongly:
 * - Uses real Backend + RBTreeIterable (tree) and Frontend where noted.
 * - No placeholders are instantiated.
 * - Each test name includes "Integration".
 */

public class AppTestClass {
  private Backend newLoadedBackend() throws Exception {
    IterableSortedCollection<Song> tree = new RBTreeIterable<>();
    Backend backend = new Backend(tree);
    backend.readData("songs.csv"); // load real data for integration
    return backend;
  }

  /**
   * Integration test: Frontend + Backend end-to-end.
   * Simulates: load file, set speed range, show top 3 results, then quit.
   * Verifies key outputs with no syntax errors (parsing + backend link working).
   */

  @Test
  public void testFrontendLoadSpeedShowIntegration() throws Exception {
    String input = String.join("\n",
        "load songs.csv",
        "speed 0 to 1000",
        "show 3",
        "quit") + "\n";
    TextUITester tui = new TextUITester(input);

    Backend backend = newLoadedBackend();
    Frontend frontend = new Frontend(new Scanner(System.in), backend);

    frontend.runCommandLoop();
    String out = tui.checkOutput();

    assertTrue(out.contains("Loaded: songs.csv"), "Should confirm CSV was loaded");
    assertTrue(out.contains("Speed range set: [0, 1000]"), "Should confirm range was accepted");
    assertTrue(out.contains("Results:"), "Should print a Results: section");
    assertFalse(out.contains("Syntax error"), "No syntax errors expected");
  }

  /**
   * Integration test: Backend filter should reduce results to empty
   * when using a year threshold far in the future.
   */

  @Test
  public void testBackendYearFilterClearsResultsIntegration() throws Exception {
    
    Backend backend = newLoadedBackend(); 

    
    List<String> allBefore = backend.getAndSetRange(null, null); // Set no bounds on speed (null, null)
    assertNotNull(allBefore, "Initial unfiltered list should not be null");

    
    List<String> filtered = backend.applyAndSetFilter(3000); //apply impossible year threshold so nothing passes
    List<String> after = backend.getAndSetRange(null, null); 

    assertNotNull(filtered);
    assertNotNull(after);
    assertEquals(0, after.size(), "With year >= 3000 filter, no songs should remain");
  }

  /**
   * Integration test: fiveMost() should return at most 5 songs, and
   * with no year filter active it should return exactly min(5, total-in-range).
   */

  @Test
  public void testBackendFiveMostCountIntegration() throws Exception {
    Backend backend = newLoadedBackend();
    backend.applyAndSetFilter(null); // ensure no year filter

    List<String> all = backend.getAndSetRange(null, null); // Set a broad range and count all
    int total = all.size();

    List<String> top = backend.fiveMost();

    assertNotNull(top, "fiveMost() should never return null");
    assertTrue(top.size() <= 5, "fiveMost() must cap results at 5");
    assertEquals(Math.min(5, total), top.size(),
        "fiveMost() size should be min(5, total in current range/filter)");
  }

  /**
   * Integration test: Frontend "show most energetic" should reflect Backend filter.
   * After setting year 3000, "show most energetic" should print "(no results)".
   */
  @Test
  public void testFrontendMostEnergeticRespectsFilterIntegration() throws Exception {
    String input = String.join("\n",
        "load songs.csv",
        "year 3000",          
        "show most energetic",
        "quit") + "\n";
    TextUITester tui = new TextUITester(input);

    Backend backend = newLoadedBackend();
    Frontend frontend = new Frontend(new Scanner(System.in), backend);

    frontend.runCommandLoop();
    String out = tui.checkOutput();

    assertTrue(out.contains("Year threshold set: 3000+"), "Should confirm year filter");
    assertTrue(out.contains("Most Energetic:"), "Should print Most Energetic header");
    assertTrue(out.contains("(no results)"),
        "With year >= 3000, frontend should report (no results)");
  }
}

