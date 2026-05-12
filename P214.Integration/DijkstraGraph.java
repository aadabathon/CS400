import java.util.*;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referenced by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode pred;

        public SearchNode(Node startNode) {
            this.node = startNode;
            this.cost = 0;
            this.pred = null;
        }

        public SearchNode(SearchNode pred, Edge newEdge) {
            this.node = newEdge.succ;
            this.cost = pred.cost + newEdge.data.doubleValue();
            this.pred = pred;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new PlaceholderMap<>());
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the starting node for the path
     * @param end   the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    protected SearchNode computeShortestPath(Node start, Node end) { //Essentially; just implementing dijkstras
        if (start == null || end == null) {
        throw new NoSuchElementException();
    }
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        Set<Node> done = new HashSet<>();
        Map<Node, Double> best = new HashMap<>();

        pq.add(new SearchNode(start)); //Initial state before loop
        best.put(start, 0.0);

        while (!pq.isEmpty()) { //Main loop
                SearchNode curr = pq.poll(); //Grab the cheapest state so far.
                if (done.contains(curr.node)) continue;
                if (curr.node == end) return curr;
                done.add(curr.node); //Skip if in done, if curr is end, return curr, else curr is done

                for (Edge e : curr.node.edgesLeaving) { //Loop through edges leaving curr, find nxtcost, if its less than seen, update best and push curr
                        double w = e.data.doubleValue();
                        if (w < 0.0) continue;
                        double nxtCost = curr.cost + w;
                        Double seen = best.get(e.succ);
                        if (seen == null || nxtCost < seen) {
                                best.put(e.succ, nxtCost);
                                pq.add(new SearchNode(curr, e));
                                        }
                                }
                        }
                throw new NoSuchElementException("path not found"); // If we dont have a path between, throw error
}
    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shortest path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from nodes along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        if (start == null || end == null) throw new NoSuchElementException();
        Node St = nodes.get(start);
        Node Nd = nodes.get(end);
        SearchNode goal = computeShortestPath(St, Nd);

        LinkedList<NodeType> path = new LinkedList<>();

        SearchNode curr = goal;
        while (curr != null) {
                path.addFirst(curr.node.data);
                curr = curr.pred;
                } return path;
        }
    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path from the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @throws NoSuchElementException if either the start of the end node
                                      cannot be found, or there is no path
                                      from the start to the end node
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        if (start == null || end == null) throw new NoSuchElementException();
        Node St = nodes.get(start);
        Node Nd = nodes.get(end);
        return computeShortestPath(St, Nd).cost;
    }
}
