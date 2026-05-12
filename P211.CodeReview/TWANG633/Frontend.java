import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class Frontend implements FrontendInterface{

  private BackendInterface backend;


  /**
   * Implementing classes should support the constructor below.
   *
   * @param backend is used for shortest path computations
   */
  public Frontend(BackendInterface backend) {
    this.backend = backend;

    try {
      this.backend.loadGraphData("campus.dot");
    } catch (IOException e) {
      System.out.println("There is an error reading in the given file, please check your filepath.");
    }

  }



  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page.  This HTML
   * output should include:
   * - a text input field with the id="start", for the start location
   * - a text input field with the id="end", for the destination
   * - a button labelled "Find Shortest Path" to request this computation
   * Ensure that these text fields are clearly labelled, so that the user can understand how to use them.
   *
   * @return an HTML string that contains input controls that the user can make use of to request a
   * shortest path computation
   */
  @Override
  public String generateShortestPathPromptHTML() {


    return """
        <input id="start" type = "text" placeholder = "Enter start location here..."  />
        <input id="end" type = "text" placeholder = "Enter end location here..."  />
        <input type = "button" value = "Find Shortest Path" />
        """;
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page.
   * This HTML output should include:
   * - a paragraph (p) that describes the path's start and end locations
   * - an ordered list (ol) of locations along that shortest path
   * - a paragraph (p) that includes the total travel time along this path
   * Or if there is no such path, the HTML returned should instead
   * indicate the kind of problem encountered.
   *
   * @param start is the starting location to find a shortest path from
   * @param end   is the destination that this shortest path should end at
   * @return an HTML string that describes the shortest path between these two locations
   */
  @Override
  public String generateShortestPathResponseHTML(String start, String end) {

    List<String> locationsOnShortestPath = backend.findLocationsOnShortestPath(start, end);
    List<Double> totalTravelTime = backend.findTimesOnShortestPath(start, end);
    String htmlToReturn;

    if (!locationsOnShortestPath.isEmpty() || !totalTravelTime.isEmpty()) {
      // if the shortest path exists

      String pathParagraph = "<p>The path start location is " + start + " and end location is " + end + ".</p>\n";
      String locationsOrderedList = "<ol>" + fromListToHTML(locationsOnShortestPath) + "</ol>\n";
      String totalTravelTimeParagraph = "<p>The total travel time is " + fromListToHTML(totalTravelTime) + " seconds.</p>\n";

      htmlToReturn = pathParagraph + locationsOrderedList + totalTravelTimeParagraph;


    } else {
      // if there is no such path
      htmlToReturn = "<p>There is no shortest path found from " + start + " to " + end + ".</p>\n";
    }


    return htmlToReturn;
  }


  /**
   * A private helper method that helps to turn a List into HTML element:
   * - if the passed in List type is String, wrap each String with <li></li>
   * - if the passed in List type is Double, calculate the sum of each Double element and
   * return the number as a String
   *
   * @param list with locations along the path or with walking times along the path
   * @return a String of HTML list elements of locations or a String of total travel time in seconds
   * @param <T> can be String or Double
   */
  private <T> String fromListToHTML(List<T> list) {
    String HTMLToReturn = "";
    double totalTravelTime = 0.0;

    for (T element : list) {
      if (element instanceof String) {
        // wrap each location as HTML list item
        HTMLToReturn = HTMLToReturn + "<li>" + element + "</li>\n";

      } else if (element instanceof Double) {
        // calculate sum of travel times
        totalTravelTime = totalTravelTime + (Double) element;
        HTMLToReturn = String.valueOf(totalTravelTime);


      }
    }

    return HTMLToReturn;
  }



  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page.  This HTML
   * output should include:
   * - a text input field with the id="from", for the start locations
   * - a button labelled "Closest From All" to submit this request
   * Ensure that this text field is clearly labelled, so that the user can understand that they
   * should enter a comma separated list of as many locations as they would like into this field
   *
   * @return an HTML string that contains input controls that the user can make use of to request a
   * ten closest destinations calculation
   */
  @Override
  public String generateClosestDestinationsFromAllPromptHTML() {

    return """
        <input id="start" type = "text" placeholder = "Enter start locations here...(separated each location by comma)" />
        <input type = "button" value = "Closest From All" />
        """;
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page.  This HTML
   * output should include:
   * - an unordered list (ul) of the start Locations
   * - a paragraph (p) describing the destination that is reached most quickly from all of those
   * start locations (summing travel times)
   * - a paragraph that displays the total/summed travel time that it take to reach this destination
   * from all specified start locations
   * Or if no such destinations can be found, the HTML returned should instead indicate the kind of
   * problem encountered.
   *
   * @param starts is the comma separated list of starting locations to search from
   * @return an HTML string that describes the closest destinations from the specified start
   * location.
   */
  @Override
  public String generateClosestDestinationsFromAllResponseHTML(String starts) {
    String HTMLToReturn;

    // turn the start locations String into a List to pass into backend method
    List<String> startLocationsList = List.of(starts.split(","));
    String startLocations = "<ul>" + fromListToHTML(startLocationsList) + "</ul>\n";
    double totalTravelTime = 0.0;

    try {
      String destination = backend.getClosestDestinationFromAll(startLocationsList);

      String mostQuicklyDestinationParagraph = "<p>The destination that is reached most quickly " +
          "from all of the given start locations is " + destination + ".</p>\n";

      // iterate through each start location to get the shortest path to the destination, then
      // sum the travel time with a helper method
      for (String start : startLocationsList) {
        List<Double> travelTimeList = backend.findTimesOnShortestPath(start, destination);
        String travelTime = fromListToHTML(travelTimeList);
        totalTravelTime = totalTravelTime + Double.parseDouble(travelTime);

      }

      String totalTravelTimeParagraph = "<p>The total travel time to reach the destination " +
          destination + " from all specified start locations is " + totalTravelTime + " seconds.</p>\n";


      HTMLToReturn = startLocations + mostQuicklyDestinationParagraph + totalTravelTimeParagraph;

    } catch (NoSuchElementException e) {
      HTMLToReturn = "<p>There is no closest destinations can be found from the given list of start locations. " +
          "Please check if the entered start locations exist.</p>\n";
    }

    return HTMLToReturn;
  }
}
