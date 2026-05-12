import java.util.ArrayList;
import java.util.List;

/**
 * Frontend implementation that returns HTML fragments required by
 * FrontendInterface and delegates computations to BackendInterface.
 */
public class Frontend implements FrontendInterface {

    /**
     * Reference to the backend that performs all graph computations.
     */
    private final BackendInterface backend;

    /**
     * Creates a new Frontend that talks to the given backend.
     *
     * @param backend the backend that this frontend will delegate work to
     */
    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    /**
     * Builds the HTML form that lets a user request the shortest path between
     * two locations.
     *
     * @return HTML string with labels, text inputs, and button for shortest-path
     *         queries
     */
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

    /**
     * Uses the backend to compute the shortest path between the given start and
     * end locations and formats the result as HTML.
     *
     * @param start the starting location entered by the user
     * @param end   the destination location entered by the user
     * @return HTML describing the path and total time, or a message when no path
     *         can be found
     */
    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        try {
            // ask backend for list of locations on the shortest path
            List<String> path = backend.findLocationsOnShortestPath(start, end);
            if (path == null || path.isEmpty()) {
                return "<p>No path found from '" + esc(start) + "' to '" + esc(end) + "'.</p>";
            }

            // ask backend for segment times and sum them up
            List<Double> times = backend.findTimesOnShortestPath(start, end);
            double total = 0.0;
            if (times != null) {
                for (Double d : times) {
                    if (d != null) total += d.doubleValue();
                }
            }

            // build HTML listing each location along the path
            StringBuilder sb = new StringBuilder();
            sb.append("<p>Shortest path from ").append(esc(start))
                    .append(" to ").append(esc(end)).append(":</p>");

            sb.append("<ol>");
            for (String loc : path) {
                sb.append("<li>").append(esc(loc)).append("</li>");
            }
            sb.append("</ol>");

            // include total time at the end
            sb.append("<p>Total time: ").append(formatSeconds(total)).append("</p>");
            return sb.toString();
        } catch (RuntimeException e) {
            // log detailed error to server logs without exposing it in HTML
            System.err.println("Error computing shortest path from "
                    + start + " to " + end + ": " + e);
            // return a generic message to the user
            return "<p>Sorry, an error occurred while computing the path.</p>";
        }

    }

    /**
     * Builds the HTML form that lets a user enter multiple start locations for the
     * "closest destination from all" query.
     *
     * @return HTML string with a single text field for comma-separated starts
     */
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

    /**
     * Uses the backend to find the destination that is closest (in total time) from
     * all of the provided start locations, and formats the result as HTML.
     *
     * @param starts comma-separated list of starting locations
     * @return HTML listing all start locations, the chosen destination, and the
     *         total summed time
     */
    @Override
    public String generateClosestDestinationsFromAllResponseHTML(String starts) {
	List<String> startList = parseStarts(starts);
        try {
            // parse the raw comma-separated string into a cleaned list of starts
            if (startList.isEmpty()) {
                return "<p>No start locations provided.</p>";
            }

            // ask backend for destination that is closest from all starts
            String dest = backend.getClosestDestinationFromAll(startList);

            // if backend cannot find a common destination, report this case
            if (dest == null) {
                return "<p>No common destination could be found for the provided start locations.</p>";
            }

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
        } catch (RuntimeException e) {
            // log detailed error to server logs without exposing it in HTML
            System.err.println("Error computing closest destination from starts "
                    + startList + ": " + e);
            // return a generic, non-revealing message to the user
            return "<p>Sorry, an error occurred while computing the closest destination.</p>";
        }

    }

    // helpers

    /**
     * Parses a comma-separated string of start locations into a list of trimmed,
     * non-empty names.
     *
     * @param raw comma-separated string from the text input
     * @return list of cleaned start names (possibly empty, but never null)
     */
    private List<String> parseStarts(String raw) {
        List<String> list = new ArrayList<>();
        if (raw == null) return list;
        for (String s : raw.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) list.add(t);
        }
        return list;
    }

    /**
     * Escapes a string for safe inclusion in HTML.
     *
     * @param s the original string
     * @return escaped string that does not contain raw HTML-special characters
     */
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }

    /**
     * Formats a duration in seconds as a human-readable string.
     *
     * @param secs time in seconds
     * @return a string like "42 seconds"
     */
    private String formatSeconds(double secs) {
        long s = Math.round(secs);
        return s + " seconds";
    }
}
