import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.util.NoSuchElementException;

public class Backend_Placeholder implements BackendInterface {

    GraphADT<String, Double> graph;

    private List<String> locationsInGraph = new ArrayList<>(
            Arrays.asList("Union South",
                    "Computer Sciences and Statistics",
                    "Weeks Hall for Geological Sciences"));

    public Backend_Placeholder(GraphADT<String, Double> graph) {
        this.graph = graph;
    }

    @Override
    public void loadGraphData(String filename) throws IOException {
        graph.insertNode("Mosse Humanities Building");
        if (!locationsInGraph.contains("Mosse Humanities Building")) {
            locationsInGraph.add("Mosse Humanities Building");
        }
    }

    @Override
    public List<String> getListOfAllLocations() {
        return new ArrayList<>(locationsInGraph);
    }

    @Override
    public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
        return graph.shortestPathData(startLocation, endLocation);
    }

    @Override
    public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
        List<String> locations = graph.shortestPathData(startLocation, endLocation);
        List<Double> times = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            times.add(i + 1.0);
        }
        return times;
    }

    @Override
    public String getClosestDestinationFromAll(List<String> startLocations)
            throws NoSuchElementException {
        if (locationsInGraph.isEmpty()) {
            throw new NoSuchElementException("No locations available");
        }
        return locationsInGraph.get(locationsInGraph.size() - 1);
    }
}
