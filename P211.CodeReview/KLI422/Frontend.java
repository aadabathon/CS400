import java.util.ArrayList;
import java.util.List;

/**
 * Frontend implementation that returns HTML fragments required by
 * FrontendInterface and delegates computations to BackendInterface.
 */
public class Frontend implements FrontendInterface {

  private final BackendInterface backend;

  // Required by the interface comment
  public Frontend(BackendInterface backend) {
    this.backend = backend;
  }

  @Override
  public String generateShortestPathPromptHTML() {
    StringBuilder sb = new StringBuilder();
    sb.append("<div class=\"shortest-path\">");
    sb.append("<label for=\"start\">Start Location:</label> ");
    sb.append("<input id=\"start\" type=\"text\" placeholder=\"e.g., Union South\" /> ");
    sb.append("<label for=\"end\">Destination:</label> ");
    sb.append("<input id=\"end\" type=\"text\" placeholder=\"e.g., Weeks Hall for Geological Sciences\" /> ");
    sb.append("<button id=\"find-shortest\">Find Shortest Path</button>");
    sb.append("</div>");
    return sb.toString();
  }

  @Override
  public String generateShortestPathResponseHTML(String start, String end) {
    try {
      List<String> path = backend.findLocationsOnShortestPath(start, end);
      if (path == null || path.isEmpty()) {
        return "<p>No path found from '" + esc(start) + "' to '" + esc(end) + "'.</p>";
      }

      List<Double> times = backend.findTimesOnShortestPath(start, end);
      double total = 0.0;
      if (times != null) {
        for (Double d : times) {
          if (d != null) total += d.doubleValue();
        }
      }

      StringBuilder sb = new StringBuilder();
      sb.append("<p>Shortest path from ").append(esc(start))
        .append(" to ").append(esc(end)).append(":</p>");

      sb.append("<ol>");
      for (String loc : path) {
        sb.append("<li>").append(esc(loc)).append("</li>");
      }
      sb.append("</ol>");

      sb.append("<p>Total time: ").append(formatSeconds(total)).append("</p>");
      return sb.toString();
    } catch (Exception e) {
      String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
      return "<p>Error computing path: " + esc(msg) + "</p>";
    }
  }

  @Override
  public String generateClosestDestinationsFromAllPromptHTML() {
    StringBuilder sb = new StringBuilder();
    sb.append("<div class=\"closest-from-all\">");
    sb.append("<label for=\"from\">Start Locations (comma separated):</label> ");
    sb.append("<input id=\"from\" type=\"text\" placeholder=\"e.g., Union South, Computer Sciences and Statistics\" /> ");
    sb.append("<button id=\"closest-all\">Closest From All</button>");
    sb.append("</div>");
    return sb.toString();
  }

  @Override
  public String generateClosestDestinationsFromAllResponseHTML(String starts) {
    try {
      List<String> startList = parseStarts(starts);
      if (startList.isEmpty()) {
        return "<p>No start locations provided.</p>";
      }

      String dest = backend.getClosestDestinationFromAll(startList);

      double total = 0.0;
      for (String s : startList) {
        List<Double> seg = backend.findTimesOnShortestPath(s, dest);
        if (seg != null) {
          for (Double d : seg) if (d != null) total += d.doubleValue();
        }
      }

      StringBuilder sb = new StringBuilder();
      sb.append("<ul>");
      for (String s : startList) {
        sb.append("<li>").append(esc(s)).append("</li>");
      }
      sb.append("</ul>");

      sb.append("<p>Closest common destination: ").append(esc(dest)).append("</p>");
      sb.append("<p>Total time from all starts (summed): ")
        .append(formatSeconds(total)).append("</p>");

      return sb.toString();
    } catch (Exception e) {
      String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
      return "<p>Error computing closest destination: " + esc(msg) + "</p>";
    }
  }

  // helpers

  private List<String> parseStarts(String raw) {
    List<String> list = new ArrayList<>();
    if (raw == null) return list;
    for (String s : raw.split(",")) {
      String t = s.trim();
      if (!t.isEmpty()) list.add(t);
    }
    return list;
  }

  private String esc(String s) {
    if (s == null) return "";
    return s.replace("&","&amp;")
            .replace("<","&lt;")
            .replace(">","&gt;")
            .replace("\"","&quot;")
            .replace("'","&#39;");
  }

  private String formatSeconds(double secs) {
    long s = Math.round(secs);
    return s + " seconds";
  }
}
