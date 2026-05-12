import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeamTests {

    /**
     * Simple dummy backend stub that returns fixed data so we can test the
     * frontend behavior through its interface methods.
     */

    private static class BackendStub implements BackendInterface {
        @Override
        public void loadGraphData(String filename) throws IOException {
        }
	
	@Override
        public List<String> getListOfAllLocations(){
            return null;
        }
        @Override
        public List<String> findLocationsOnShortestPath(String start, String end) {
            List<String> path = new ArrayList<>();
            path.add(start);
            path.add("Midpoint");
            path.add(end);
            return path;
        }

        @Override
        public List<Double> findTimesOnShortestPath(String start, String end) {
            List<Double> times = new ArrayList<>();
            times.add(1.0);
            times.add(2.0);
            return times;
        }

        @Override
        public String getClosestDestinationFromAll(List<String> starts) {
            return "Central Hub";
        }
    }

    /**
     * Tests that the shortest path prompt HTML contains the required
     * input fields and button labels and is not null.
     */

    @Test
    public void testShortestPathPromptHasRequiredContents() {
        FrontendInterface frontend = new Frontend(new BackendStub());

        String html = frontend.generateShortestPathPromptHTML(); //Call generatePrompt and assert the String return is not null, contains id start, end, and Find Shortest Path as it should be a button label.

        assertNotNull(html); 
        assertTrue(html.contains("id=\"start\""), "Missing start input field");
        assertTrue(html.contains("id=\"end\""), "Missing end input field");
        assertTrue(html.contains("Find Shortest Path"),
                   "Missing 'Find Shortest Path' button label");
    }

    /**
     * Tests that the returned String from generateShortestPathResponseHTML(s, e) includes the start and end
     * locations and an ordered list (ol) that contains each location in the path.
     * And that html contains 3 lists containing start, midpoint and end after calling generateShortestPathResponseHTML(s,e);
     * And that html is not null.
     */

    @Test
    public void testShortestPathResponses() {
        FrontendInterface frontend = new Frontend(new BackendStub());

        String start = "StartPoint";
        String end = "EndPoint";
        String html = frontend.generateShortestPathResponseHTML(start, end); //Set start and end and call method

        assertNotNull(html); //Assert HTML is not null, contains starts and end, lists containg start, midpoint, and end, and an ordered list.
        assertTrue(html.contains(start), "Response should mention the start location");
        assertTrue(html.contains(end), "Response should mention the end location");
        assertTrue(html.contains("<ol"), "Response should contain an ordered list");
        assertTrue(html.contains("<li>" + start + "</li>"),
                   "Path should include start location as a list item");
        assertTrue(html.contains("<li>Midpoint</li>"),
                   "Path should include midpoint location as a list item");
        assertTrue(html.contains("<li>" + end + "</li>"),
                   "Path should include end location as a list item");
    }

    /**
     * Tests that the returned string from generateClosestDestinationsFromAllResponseHTML() includes
     * all of the provided start locations in an unordered list and the
     * common destination our dummy backend class method and is not null.
     */

    @Test
    public void testClosestDestinationsResponses() {
        FrontendInterface frontend = new Frontend(new BackendStub());

        String starts = "Alpha, Beta"; //Set a simple starts string
        String html = frontend.generateClosestDestinationsFromAllResponseHTML(starts); //Call method and make sure HTML is not null, and contains an unordered list, the contents of starts, and the only possible common destination.

        assertNotNull(html);
        assertTrue(html.contains("<ul"), "Response should contain an unordered list");
        assertTrue(html.contains("Alpha"), "Response should mention 'Alpha'");
        assertTrue(html.contains("Beta"), "Response should mention 'Beta'");
        assertTrue(html.contains("Central Hub"),
                   "Response should mention the closest common destination");
    }
}
