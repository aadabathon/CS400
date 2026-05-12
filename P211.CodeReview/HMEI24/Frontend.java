import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This class models the Frontend of a webapp of finding shortest paths.
 */
public class Frontend implements FrontendInterface {

    // constant strings for path to html files
    private static final String SHORTEST_PATH_PROMPT_PATH = "shortestPathPrompt.html";
    private static final String CLOSEST_DESTINATIONS_PROMPT_PATH = "closestDestinationsPrompt.html";

    private BackendInterface backend; // backend reference

    /**
     * Construct a Frontend based on backend
     * 
     * @param backend
     */
    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    /**
     * Helper method that reads a html file and returns all of its content
     * 
     * @param filename path to html file
     * @return html of file's content, or an error message if IOException is thrown
     */
    private String readData(String filename) {
        String errorMsg = "Error: expected file %s but was not found".formatted(filename);
        try {
            File file = new File(filename);
            Scanner sc = new Scanner(file);
            String rst = "";

            // add all content of the file
            while (sc.hasNextLine()) {
                rst = rst.concat(sc.nextLine());
            }

            sc.close();
            return rst;
        } catch (IOException e) {
            System.out.println(errorMsg);
            return "<p>%s</p>".formatted(errorMsg);
        }
    }

    /**
     * Helper method that calculates the sum of a list of double
     * 
     * @param lst a list of Double
     * @return the sum of all elements in lst
     */
    private double sum(List<Double> lst) {
        return lst.stream().mapToDouble(time -> time.doubleValue()).sum();
    }

    /**
     * Removes everything in a string except alphabets, numbers, dot, hyphen, and spaces. Also
     * removes leading and trailing spaces.
     * 
     * @param string the string to be cleaned
     * @return cleaned string
     */
    private String cleanString(String string) {
        // remove every character that is not alphabets, numbers, '.', '-', ' ', and then strip()
        return string.replaceAll("[^A-Za-z0-9.\\- ]", "").strip();
    }

    @Override
    public String generateShortestPathPromptHTML() {
        // all content is in file
        return readData(SHORTEST_PATH_PROMPT_PATH);
    }

    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        // clean input strings
        if (start.isBlank() || end.isBlank()) {
            return "<p>Start location or end location cannot be empty!</p>";
        }
        start = cleanString(start);
        end = cleanString(end);
        
        // initialize lists for storing location and time from backend
        List<String> locations = new ArrayList<>();
        List<Double> times = new ArrayList<>();

        try {
            // get location and time of shortest path from backend
            locations = backend.findLocationsOnShortestPath(start, end);
            times = backend.findTimesOnShortestPath(start, end);
        } catch (Exception e) {
            
        }
        // either exception or something went wrong, return an error message.
        if (locations.isEmpty() || times.isEmpty()) {
            return "<p>Unable to find shortest path from %s to %s.</p>".formatted(start, end);
        }
        
        // construct result string.
        String p1 = "<p>Shortest path from %s to %s: </p>".formatted(start, end);
        // surround each location with <li>: <li>location</li>
        String ol = "<ol>" + locations.stream().map(location -> "<li>" + location + "</li>")
                .collect(Collectors.joining()) + "</ol>";
        String p2 = "<p>Total travel time: %.2f</p>".formatted(sum(times));

        return p1 + ol + p2;
    }

    @Override
    public String generateClosestDestinationsFromAllPromptHTML() {
        // all content is in file
        return readData(CLOSEST_DESTINATIONS_PROMPT_PATH);
    }

    @Override
    public String generateClosestDestinationsFromAllResponseHTML(String starts) {
        // split start locations by {any number of spaces},{any number of spaces}
        List<String> lst = List.of(starts.split(" *, *"));
        
        // initialize list to store clean locations
        List<String> locations = new ArrayList<>();
        // System.out.println(Arrays.toString(lst.toArray()));
        for (int i = lst.size() - 1; i >= 0; i--) {
            String location = lst.get(i);
            if (location.isBlank()) {
                // omit invalid location, rest is still added
                System.out.println("Warning: Omitted invalid location");
                continue;
            }
            // add cleaned location to locations list
            locations.add(0, cleanString(location));

        }
        
        // surround each location with <li>
        String ul = "<ul>" + locations.stream().map(location -> "<li>" + location + "</li>")
                .collect(Collectors.joining()) + "</ul>";
        
        // call backend to get closest destination
        String destination = "";
        try {
            destination = backend.getClosestDestinationFromAll(locations);
        } catch (NoSuchElementException e) {
            // destination would not be assigned, which will return the error message
        }

        if (destination.isBlank()) {
            return ul
                    + "<p>Unable to find closest destination. This can be due to: a specified start location is not found within the graph, no destination can be reached from all start locations</p>";
        }
        
        // construct result html string
        String p1 = "<p>Destination reached most quickly from all of start locations: %s</p>"
                .formatted(destination);
        String p2 = "<p>Time to reach %s from: <br>".formatted(destination);

        for (String location : locations) {
            try {
                // get time of shortest path from backend
                double time = sum(backend.findTimesOnShortestPath(location, destination));
                // format: location - time
                p2 = p2.concat("%s - %.2f<br>".formatted(location, time));
            } catch (Exception e) {
                // despite an error here, print all available information
                return ul + p1 + "<p>Error: Unable to calculate shortest path from %s to %s</p>"
                        .formatted(location, destination);
            }
        }
        // end <p> tag
        p2 = p2.concat("</p>");
        return ul + p1 + p2;
    }

//    public static void main(String[] args) {
//        System.out.println(new Frontend(backend).generateClosestDestinationsFromAllResponseHTML(
//                "   Union South   , Computer Sciences and Statistics"));
//    }

}
