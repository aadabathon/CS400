import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

//Necessary Imports above ^
//                        |
//Name: Adam Shebani
public class Backend implements BackendInterface {

  private final GraphADT<String, Double> graph; //Class fields for method implementation and such
  private final Set<String> nodes = new LinkedHashSet<>();

  public Backend(GraphADT<String, Double> graph) { //Default 
    this.graph = graph;
  }

    /**
   * Loads graph data from a dot file.  If a graph was previously loaded, this
   * method should first delete the contents (nodes and edges) of the existing
   * graph before loading a new one.
   * @param filename the path to a dot file to read graph data from
   * @throws IOException if there was any problem reading from this file
   */

  @Override
  public void loadGraphData(String filename) throws IOException {
    clearGraph();

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) { //Loop through lines in dot file to instantiate graph and nodes
        line = line.trim();
        if (line.isEmpty() || line.startsWith("//")) //Skip empty/comment lines
          continue;
        if (line.contains("{") || line.contains("}") || line.toLowerCase().startsWith("digraph"))
          continue; // Looking for dot notation

        if (line.contains("->")) { //Parse through our loaded dot file, and then we 
          String[] parts = line.split("->");
          if (parts.length < 2) continue;

          String from = clean(parts[0]); //from is the starting value
          String rest = parts[1];
          String to = clean(rest.split("\\[")[0]); // to is the destination value
          double weight = extractWeight(rest); // grab weight 
          if (weight <= 0){
		 weight = 1.0; //I'll just handle negative weights by setting it to 1 for now
		System.out.println("Negative weights set to 1.0 by design.");
	  }

          ensureNode(from);
          ensureNode(to);
          graph.insertEdge(from, to, weight);
        } 
        else if (line.endsWith(";")) { //For lines ending in semi colons we want the data so clean the line and ensurenode.
          String name = clean(line.replace(";", ""));
          ensureNode(name);
        }
      }
    }
  }

    /**
   * Returns a list of all locations (node data) available in the graph.
   * @return list of all location names
   */

  @Override
  public List<String> getListOfAllLocations() {
    List<String> list = new ArrayList<>(nodes); //Construct list from hashset nodes
    Collections.sort(list); 
    return list; //return sorted list of nodes
  }

    /**
   * Return the sequence of locations along the shortest path from
   * startLocation to endLocation, or an empty list if no such path exists.
   * @param startLocation the start location of the path
   * @param endLocation the end location of the path
   * @return a list with the nodes along the shortest path from startLocation
   *         to endLocation, or an empty list if no such path exists
   */

  @Override
  public List<String> findLocationsOnShortestPath(String start, String end) {
    try { // call dijkstras algorithm, make sure path is substantive, and that start and end are the first and last elements of path, in case of any error were just gonna return an empty list
      List<String> path = graph.shortestPathData(start, end);
      if (path == null || path.isEmpty()) return Collections.emptyList();
      if (!path.get(0).equals(start) || !path.get(path.size() - 1).equals(end)) return Collections.emptyList();
      return path;
    } catch (Exception e) {
      System.err.println("somethings wrong, check it out");
      return Collections.emptyList();
    }
  }

    /**
   * Return the walking times in seconds between each two nodes on the
   * shortest path from startLocation to endLocation, or an empty list of no
   * such path exists.
   * @param startLocation the start location of the path
   * @param endLocation the end location of the path
   * @return a list with the walking times in seconds between two nodes along
   *         the shortest path from startLocation to endLocation, or an empty
   *         list if no such path exists
   */

  @Override
  public List<Double> findTimesOnShortestPath(String start, String end) {
    List<String> path = findLocationsOnShortestPath(start, end);
    if (path.size() < 2) return Collections.emptyList();

    List<Double> times = new ArrayList<>();
    try {
      for (int i = 0; i < path.size() - 1; i++) {
        times.add(graph.getEdge(path.get(i), path.get(i + 1)).doubleValue());
      }
    } catch (Exception e) {
      System.err.println("somethings wrong, check it out.");
      return Collections.emptyList();
    }
    return times; // return the list of weights for i : (int) end after calling findLocationsOnShortestPath
  }

    /**
   * Returns the location can be reached from all of the specified start
   * locations in the shortest total time: minimizing the sum of the travel
   * times from each start locations.
   * @param startLocations the list of locations to minimize travel time from
   * @return the location that can be reached in the shortest total time from
   *         all of the specified start locations
   * @throws NoSuchElementException if there is no destination that can be
   *         reached from all of the start locations, or if any of the start
   *         locations does not exist within the graph
   */

  @Override
  public String getClosestDestinationFromAll(List<String> starts) throws NoSuchElementException {
    if (starts == null || starts.isEmpty()) throw new NoSuchElementException("No start locations");
    for (String s : starts){
      if (!nodes.contains(s))
        throw new NoSuchElementException("Unknown start: " + s);
    }
    
    String best = null;
    double bestSum = Double.POSITIVE_INFINITY;

    for (String dest : nodes) {
      double total = 0;
      boolean ok = true;

      for (String s : starts) {
        try {
          total += graph.shortestPathCost(s, dest); // Sum all of the paths in starts to EVERY destination in nodes
        } catch (Exception e) { // catch generic exception, this cascades into NSEE thrown later
          ok = false;
          break;
        }
      }

      if (ok && total < bestSum) { // Return the string assoicated with destination that MINIMIZED the TOTAL TRAVEL TIME FROM all the start locations
        best = dest;
        bestSum = total;
      }
    }

    if (best == null)
      throw new NoSuchElementException("No common reachable destination");
    return best;
  }

  /* 
   * HELPER METHOD BELOW TO AID IN OUR IMPLEMENTATIONS OF 
   * THE ABSTRACT METHODS FROM BackendInterface.java 
   * 
  */

  private void ensureNode(String name) {
    if (!nodes.contains(name)) { //Insert node of value String name into graph and into our hashset nodes when it's not already there
      graph.insertNode(name); 
      nodes.add(name);
    }
  }

  private void clearGraph() {
    for (String n : new ArrayList<>(nodes)) {
      graph.removeNode(n); //Remove all from graph and clear hashset
    }
    nodes.clear();
  }

  
  private static String clean(String s) { //This method is from trimming spaces and unwanted backslashes from dot files.
    s = s.trim();
    if (s.startsWith("\"") && s.endsWith("\""))
      s = s.substring(1, s.length() - 1);
    return s.trim();
  }

  private static double extractWeight(String s) {
    s = s.trim();
    int idx = s.indexOf("=");
    if (idx == -1) return 1.0;
    try {
      String num = s.substring(idx + 1).replaceAll("[^0-9.]", ""); //Replace all unwanted data from the [weight = x.x] so we just get x.x
      return Double.parseDouble(num);
    } catch (Exception e) { //An exception may be likely so just catch and return one.
      return 1.0;
    }
  }
}

