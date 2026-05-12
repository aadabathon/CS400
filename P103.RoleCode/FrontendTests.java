import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class FrontendTests {
  private static class MockBackend implements BackendInterface {
    String lastLoaded; //Last file path loaded to readData().
    Integer lastLow, lastHigh; //Last speed range set via getAndSetRange().
    Integer lastYear; // Last threshold set via applyAndSetFilter().

    @Override
    public void readData(String filename) throws java.io.IOException { 
      this.lastLoaded = filename; // Simulate successful load by recording the filename.
    }

    @Override
    public java.util.List<String> getAndSetRange(Integer low, Integer high) { // Record the requested range so tests can assert it later.
      this.lastLow = low;
      this.lastHigh = high;
      java.util.ArrayList<String> list = new java.util.ArrayList<>(); // Create a new list with simple and testable values.
      list.add("Alpha");
      list.add("Bravo");
      list.add("Charlie");
      list.add("Delta");
      list.add("Echo");
      return list;
    }

    @Override
    public java.util.List<String> applyAndSetFilter(Integer threshold) {  // Record the threshold; return something consistent with getAndSetRange behavior.
      this.lastYear = threshold;
      return getAndSetRange(lastLow, lastHigh);
    }

    @Override
    public java.util.List<String> fiveMost() { // Return exactly 5 titles in a known order to check "show most energetic".
      java.util.ArrayList<String> top = new java.util.ArrayList<>();
      top.add("Most1");
      top.add("Most2");
      top.add("Most3");
      top.add("Most4");
      top.add("Most5");
      return top;
    }
  }

  private static Frontend makeFrontend(MockBackend be) {  //Factory for a Frontend that reads from System.in. We will call this after constructing TextUITester, so System.in is already redirected to the test script.
    return new Frontend(new java.util.Scanner(System.in), be);
  }

  @Test
  public void frontendTest1() { // Tests our outputs with TextUIHelper, upon startup we expect the commands to be displayed, typing help should print instructions, and typing an unknown command should throw our designated message.
    String script = ""
        + "help\n"     // repeats instructions
        + "unknown\n"  // should trigger unknown command warning
        + "quit\n";
    TextUITester t = new TextUITester(script);
    MockBackend be = new MockBackend();
    makeFrontend(be).runCommandLoop();
    String out = t.checkOutput();
    assertTrue(out.contains("Commands:"), "Should show command instructions at startup.");
    int first = out.indexOf("Commands:");
    int second = out.indexOf("Commands:", first + 1);
    assertTrue(second > first, "Typing 'help' should print instructions again.");
    assertTrue(out.toLowerCase().contains("unknown command"),
        "Should warn on unknown command.");
  }

  @Test
  public void frontendTest2() { // Validate parsing and confirmation of both "speed" syntaxes and verifying the backend receives the expected final range.
    String script = ""
        + "speed 1 to 3\n"
        + "speed 2\n"
        + "quit\n";
    TextUITester t = new TextUITester(script);

    MockBackend be = new MockBackend();
    makeFrontend(be).runCommandLoop();

    String out = t.checkOutput();

    assertTrue(out.contains("Speed range set: [1, 3]"), //We should establish an explicit min/max range.
        "Should confirm MIN..MAX range for 'speed 1 to 3'.");
    assertTrue(out.contains("Speed range set: [0, 2]"),
        "MAX-only should imply [0, MAX] for 'speed 2'.");

    assertEquals(Integer.valueOf(0), be.lastLow, "Backend should store low=0 after 'speed 2'."); // The mock BE should reflect the final state (from 'speed 2').
    assertEquals(Integer.valueOf(2), be.lastHigh, "Backend should store high=2 after 'speed 2'.");
  }

  @Test
  public void frontendTest3() { // Here we exercise the remaining commands and verify the printed outputs for: load FILEPATH, confirmation printed--
//--year MIN, confirmation printed--
//--show most energetic , prints the top-5 list from backend--
//--show MAX_COUNT , prints first N titles from the current range.

    String script = ""
        + "load data/songs.csv\n"
        + "year 2010\n"
        + "show most energetic\n"
        + "show 3\n"
        + "quit\n";
    TextUITester t = new TextUITester(script);

    MockBackend be = new MockBackend();
    makeFrontend(be).runCommandLoop();

    String out = t.checkOutput();

 
    assertTrue(out.contains("Loaded: data/songs.csv"), "Should confirm file loaded.");  // Load + Year confirmations as printed by Frontend.
    assertTrue(out.contains("Year threshold set: 2010+"), "Should confirm year threshold set.");

    assertEquals("data/songs.csv", be.lastLoaded, "Backend should record last loaded path."); // Backend side-effects recorded by mock for additional confidence.
    assertEquals(Integer.valueOf(2010), be.lastYear, "Backend should record year filter.");


    assertTrue(out.contains("Most Energetic:"), "Should print 'Most Energetic' header."); // show most energetic -> expect numbered list 1..5 with Most1..Most5.
    assertTrue(out.contains("1. Most1") && out.contains("5. Most5"),
        "Should list 5 energetic songs from backend.");

    assertTrue(out.contains("Results:"), "Should print 'Results' header for show MAX_COUNT.");  // show 3 -> first three from the canned titles (Alpha, Bravo, Charlie).
    assertTrue(out.contains("1. Alpha") && out.contains("2. Bravo") && out.contains("3. Charlie"),
        "Should print the first 3 titles for 'show 3'.");
  }
}
