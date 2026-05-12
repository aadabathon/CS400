import java.util.List;
import java.io.IOException;

public class Frontend implements FrontendInterface {
  private final java.util.Scanner in;
  private final BackendInterface backend;

  private Integer lastLow = null;
  private Integer lastHigh = null;

  // Constructor to define our two private fields upon Instantiation.
  public Frontend(java.util.Scanner in, BackendInterface backend) {
    this.in = in;
    this.backend = backend;
  }
    /**
   * RunCommandLoop() is the foundation of our frontend, this permits the user to query our
   * iSongly program as long as they wish. We run a while loop until we have no more lines to
   * read or the user quits.
   */

  @Override
  public void runCommandLoop() {
    showCommandInstructions(); //Show user commands
    while (true) { //while we do not hit one of the break commands below.
      System.out.print("> ");
      if (!in.hasNextLine()) break;
      String line = in.nextLine().trim();
      if (line.equalsIgnoreCase("quit")) break;
      try {
        processSingleCommand(line);
      } catch (Exception e) { // Try catch block on the input line, throw generic exception e in case of exception.
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

   /**
   * showCommandInstructions is a simple self explanatory method that outputs our commands,
   * we run this method before the while loop within our commandLoop method.
   */



  @Override
  public void showCommandInstructions() { // Our Command display.
    System.out.println("Commands:");
    System.out.println("  load FILEPATH");
    System.out.println("  speed MAX");
    System.out.println("  speed MIN to MAX");
    System.out.println("  year MIN");
    System.out.println("  show MAX_COUNT");
    System.out.println("  show most energetic");
    System.out.println("  help");
    System.out.println("  quit");
  }

   /**
   * processSingleCommand(String) is the build of our frontend implementation, it parses the user inputs
   * and goes down the command list, executing each command if the given helper method returns 
   * true (ie equalsIgnoreCaseSafe or startsWithIgnorecase == true.)
   */
 
  @Override
  public void processSingleCommand(String command) {
    String c = command == null ? "" : command.trim(); // set c to empty string if command is null else set to command.trim();
    if (c.isEmpty()) { // if c is empty, output prompting message.
      System.out.println("Please enter a command.");
      return;
    }

    if (equalsIgnoreCaseSafe(c, "help")) { // if helper method returns true, c = "help" and we output Command Instruction to help user.
      showCommandInstructions();
      return;
    }

    if (startsWithIgnoreCase(c, "load ")) { // If c starts with load
      String path = c.substring(5).trim(); //set path to index beyond "load" and use .trim()
      if (path.isEmpty()) { // If path is empty throw error and return.
        System.out.println("Syntax error: load FILEPATH");
        return;
      }
      try {
        backend.readData(path); //call backend method to load path.
        System.out.println("Loaded: " + path);
      } catch (IOException ioe) { //Catch ioe and throw message.
        System.out.println("Failed to load file: " + ioe.getMessage());
      }
      return;
    }

    if (startsWithIgnoreCase(c, "speed")) { // Case speed
      String args = c.length() > 5 ? c.substring(5).trim() : ""; // args is what is beyond speed
      if (args.isEmpty()) { // cannot be empty
        System.out.println("Syntax error, try: speed MAX OR speed MIN to MAX");
        return;
      }

      int toIdx = indexOfIgnoreCase(args, " to ");
      if (toIdx >= 0) {
        String left = args.substring(0, toIdx).trim();
        String right = args.substring(toIdx + 4).trim();
        try {
          Integer min = Integer.valueOf(left);
          Integer max = Integer.valueOf(right);
          backend.getAndSetRange(min, max); // sets internal range
          lastLow = min;
          lastHigh = max;
          System.out.println("Speed range set: [" + min + ", " + max + "]");
        } catch (NumberFormatException nfe) {
          System.out.println("Syntax error: speed MIN to MAX (integers required)");
        }
        return;
      }

      try {
        Integer max = Integer.valueOf(args);
        backend.getAndSetRange(0, max);
        lastLow = 0;
        lastHigh = max;
        System.out.println("Speed range set: [0, " + max + "]");
      } catch (NumberFormatException nfe) {
        System.out.println("Syntax error: speed MAX  OR  speed MIN to MAX");
      }
      return;
    }


    if (startsWithIgnoreCase(c, "year")) { // Case year
      String args = c.length() > 4 ? c.substring(4).trim() : "";
      if (args.isEmpty()) { // if args empty output message with expected input.
        System.out.println("Syntax error: year MIN");
        return;
      }
      try {
        Integer minYear = Integer.valueOf(args);
        backend.applyAndSetFilter(minYear); // sets internal filter
        System.out.println("Year threshold set: " + minYear + "+");
      } catch (NumberFormatException nfe) { // Throw nfe for erroneous inputs
        System.out.println("Syntax error: MIN must be an integer year");
      }
      return;
    }

    if (startsWithIgnoreCase(c, "show")) { // Case show
      String args = c.length() > 4 ? c.substring(4).trim() : ""; //Args = what is beyond show
      if (args.isEmpty()) { //args cannot be empty
        System.out.println("Syntax error: show MAX_COUNT  OR  show most energetic");
        return;
      }

      if (equalsIgnoreCaseSafe(args, "most energetic")) { // Case show most energetic 
        List<String> top = backend.fiveMost();
        printList("Most Energetic", top, top == null ? 0 : top.size());
        return;
      }

      try {
        int maxCount = Integer.parseInt(args); // If not most energetic try maxCount.
        if (maxCount < 0) {
          System.out.println("MAX_COUNT must be non-negative");
          return;
        }

      if (lastLow == null || lastHigh == null) {
		System.out.println("Range not set, Make sure setRange is working properly");
		return;
	}

        List<String> titles = backend.getAndSetRange(lastLow, lastHigh); // Set titles to ran
        printList("Results", titles, maxCount);
      } catch (NumberFormatException nfe) {
        System.out.println("Syntax error: show MAX_COUNT  OR  show most energetic");
      }
      return;
    }

    System.out.println("Unknown command. Type 'help' to see valid commands."); //In case of unknown command, output nice message.
  }


//                                   |
// Helper methods implemented below  V

  private static boolean equalsIgnoreCaseSafe(String a, String b) {
    return a != null && a.equalsIgnoreCase(b); // return not null and a == b;
  }

  private static boolean startsWithIgnoreCase(String s, String prefix) {
    if (s == null || prefix == null) return false; // If either are null return false.
    if (s.length() < prefix.length()) return false; //If different lengths, return false.
    return s.regionMatches(true, 0, prefix, 0, prefix.length()); // else return s compared with prefix in a case insensitive manner.
  }

  private static int indexOfIgnoreCase(String haystack, String needle) {
    if (haystack == null || needle == null) return -1; // If either are null return -1, good practice i believe.
    if (needle.length() == 0) return 0;
    int hLen = haystack.length(), nLen = needle.length();
    if (nLen > hLen) return -1; //Haystack cannot contain needle if needle is greater than it in length.
    for (int i = 0; i <= hLen - nLen; i++) {
      if (haystack.regionMatches(true, i, needle, 0, nLen)) { // we return i IF haystack starting at i matches our needle string while ingoring case.
        return i; // Return the index of i in needle in a case insensitive manner.
      }
    }
    return -1; //If nothing above returns then we default fall back on a failure -1 return.
  }

  private void printList(String header, List<String> items, int limit) {
    System.out.println(header + ":");
    if (items == null || items.isEmpty()) { // empty list, return.
      System.out.println("(no results)");
      return;
    }
    int n = Math.min(limit, items.size()); // Int n is set to our limiting factor, the minimum of input limit and items.size().
    for (int i = 0; i < n; i++) {
      System.out.println((i + 1) + ". " + items.get(i)); // iterate through n print items index and value.
    }
  }
}
