package api;

import com.google.gson.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class was created in order to implement several algorithms of a weighted directed graph.
 * For some of these functions I created using several well known algorithms, more information about it
 * can be found in the Wiki area of the repository.
 *
 * @author Alon Firestein
 */
public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph graph_algo;
    private HashMap<node_data, Double> DistanceList = new HashMap<>();


    @Override
    public void init(directed_weighted_graph g) {
        graph_algo = g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return graph_algo;
    }

    /**
     * This function returns a deep copy of a directed weighted graph.
     *
     * @return
     */
    @Override
    public directed_weighted_graph copy() {
        if (this.graph_algo == null) {
            return null;
        }
        directed_weighted_graph NewGraphCopy = new DWGraph_DS();
        for (node_data OriginalNode : graph_algo.getV()) {
            node_data CopiedNode = new NodeData(OriginalNode);
            NewGraphCopy.addNode(CopiedNode);
        }
        for (node_data OriginalNode : graph_algo.getV()) {
            if (!graph_algo.getE(OriginalNode.getKey()).isEmpty()) {
                for (edge_data OriginalEdge : graph_algo.getE(OriginalNode.getKey())) {
                    int DestNode = OriginalEdge.getDest();
                    int SrcNode = OriginalNode.getKey();
                    double weight = OriginalEdge.getWeight();
                    NewGraphCopy.connect(SrcNode, DestNode, weight);
                }
            }
        }
        return NewGraphCopy;
    }

    /**
     * This DFS algorithm traverses through a graph in a recursive method to find out if the traversal was
     * completed and visited every single node in the graph.
     *
     * @param StartNode        - The node where the traversal of the function starts.
     * @param VisitedNodesList - A list of all the nodes that were visited during the traversal of the algorithm.
     * @return - Whether the traversal through the graph was successful or not.
     */
    private boolean DFS(int StartNode, HashSet<Integer> VisitedNodesList) {
        if (graph_algo.getE(StartNode).isEmpty()) return false;
        VisitedNodesList.add(StartNode);
        for (edge_data StartNodeEdges : graph_algo.getE(StartNode)) {
            if (!VisitedNodesList.contains(StartNodeEdges.getDest())) {
                DFS(StartNodeEdges.getDest(), VisitedNodesList);
            }
        }
        return true;
    }

    /**
     * This function was built in order to create a transposed graph and use it in the isConnected function.
     * In summary, this function switches the direction of each edge in the graph and returns the transposed graph.
     *
     * @return - The transposed graph.
     */
    public directed_weighted_graph getTransposedGraph() {
        directed_weighted_graph NewGraph = new DWGraph_DS();
        for (node_data node : graph_algo.getV()) {
            NewGraph.addNode(node);
        }
        for (node_data node : graph_algo.getV()) {
            for (edge_data edge : graph_algo.getE(node.getKey())) {
                NewGraph.connect(edge.getDest(), edge.getSrc(), edge.getWeight());
            }
        }
        return NewGraph;
    }

    /**
     * This function checks the connectivity of a graph and whether its strongly connected (given that its a directed graph).
     * Using help from the DFS algorithms and the getTransposedGraph function, this function checks the traversal
     * of the graph and if every node was visited, and after transposing the graph, if again every node is visited
     * in the traversal.
     *
     * @return - If the graph is strongly connected or not.
     */
    @Override
    public boolean isConnected() {
        if (graph_algo.getV().isEmpty() || (graph_algo.nodeSize() == 1)) {
            return true;
        }
        if (graph_algo.edgeSize() < graph_algo.nodeSize() - 1) {
            return false;
        }
        /* Option A: from here
//        for (node_data node : graph_algo.getV()) {
//            HashSet<Integer> VisitedNodesList = new HashSet<>();
//            DFS(node.getKey(), VisitedNodesList);
//            if (VisitedNodesList.size() < getGraph().nodeSize()) return false;
//        }
//        return true;
//    }
     */ //to here

        // Option B: from here - O(V+E)
        HashSet<Integer> VisitedNodesList = new HashSet<>();
        int FirstNode = 0;
        for (node_data node : graph_algo.getV()) {
            FirstNode = node.getKey();
            break;
        }
        if (!DFS(FirstNode, VisitedNodesList)) return false;
        if (VisitedNodesList.size() != graph_algo.nodeSize()) return false;

        VisitedNodesList = new HashSet<>();
        directed_weighted_graph TransposedGraph = getTransposedGraph();

        if (!DFS(FirstNode, VisitedNodesList)) return false;
        if (VisitedNodesList.size() != graph_algo.nodeSize()) {
            return false;
        }
        return true;
    }

    /**
     * Using Dijkstra's algorithm this function returns the total sum of the traversal of the edges of
     * the shortest path between two nodes in the graph. If no such path exists, the function returns -1.
     *
     * @param src  - start node of the path
     * @param dest - end (target) node of the path
     * @return - The total sum weight of all the edges in the path.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (src == dest) {
            return 0;
        }
        if (graph_algo == null || graph_algo.getNode(src) == null || graph_algo.getNode(dest) == null) {
            return -1;
        }
        HashMap<Integer, Integer> ParentsList = new HashMap<>();
        HashMap<Integer, Double> DistanceList = new HashMap<>();
        dijkstra_algo(src, ParentsList, DistanceList);
        if (DistanceList.get(dest) == Double.MAX_VALUE) {
            return -1;
        }
        return DistanceList.get(dest);
    }

    /**
     * Using Dijkstra's algorithm this function returns a list of the path that occurred during the traversal
     * of the nodes of the shortest path between two nodes in the graph. If no such path exists, the function returns null.
     *
     * @param src  - start node of the path
     * @param dest - end (target) node of the path
     * @return - a list of the nodes included in the path.
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        LinkedList<node_data> path = new LinkedList<>();
        if (src == dest) {
            return path;
        }
        if (graph_algo.getNode(src) == null || graph_algo.getNode(dest) == null) {
            return null;
        }
        HashMap<Integer, Integer> ParentsList = new HashMap<>();
        HashMap<Integer, Double> DistanceList = new HashMap<>();
        for (node_data node : graph_algo.getV()) {
            DistanceList.put(node.getKey(), Double.MAX_VALUE);
        }
        dijkstra_algo(src, ParentsList, DistanceList);
        int BackTrackPath = dest;
        while (DistanceList.get(BackTrackPath) != 0.0) {
            path.add(graph_algo.getNode(BackTrackPath));
            if (ParentsList.get(BackTrackPath) != null)
                BackTrackPath = ParentsList.get(BackTrackPath);
            else {
                return null;
            }
        }
        path.add(graph_algo.getNode(BackTrackPath));
        Collections.reverse(path);
        return path;

    }

    /**
     * This function saves a graph in a JSON format.
     *
     * @param file - the file name.
     * @return - If the graph was successfully saved or not.
     */
    @Override
    public boolean save(String file) {

        if (file == "" || file == null) {
            return false;
        }
        Gson gson = new GsonBuilder().create();
        String GraphInJSON = gson.toJson(graph_algo);

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(GraphInJSON);
            fw.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function was built to load a graph saved in JSON format.
     * note: This function only loads graphs that are written in the same format that was given to us.
     *
     * @param file - file name of JSON file
     * @return - If the graph was successfully loaded or not.
     */
    @Override
    public boolean load(String file) {
        if (file == "" || file == null) {
            return false;
        }
        try {
            String GraphData = new String(Files.readAllBytes(Paths.get(file)));
            JsonParser parser = new JsonParser();
            JsonObject JSONgraph = parser.parse(GraphData).getAsJsonObject();
            JsonArray NodesInGraph = JSONgraph.get("Nodes").getAsJsonArray();
            JsonArray EdgesInGraph = JSONgraph.get("Edges").getAsJsonArray();

            for (JsonElement node : NodesInGraph) {
                String GraphLoc = node.getAsJsonObject().get("pos").getAsString();
                String[] GraphLocArray = GraphLoc.split(",");
                int key = node.getAsJsonObject().get("id").getAsInt();
                node_data NewNode = new NodeData(key);
                geo_location GeoLoc = new GeoLocation(Double.parseDouble(GraphLocArray[0]),
                        Double.parseDouble(GraphLocArray[1]),
                        Double.parseDouble(GraphLocArray[2]));
                NewNode.setLocation(GeoLoc);
                getGraph().addNode(NewNode);
            }
            for (JsonElement edge : EdgesInGraph) {
                int src = edge.getAsJsonObject().get("src").getAsInt();
                int dest = edge.getAsJsonObject().get("dest").getAsInt();
                double weight = edge.getAsJsonObject().get("w").getAsDouble();
                getGraph().connect(src, dest, weight);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This is an implementation of Dijkstra's algorithm in order to traverse through the graph and find
     * the shortest path from the source node given in the input, to the rest of the nodes in the graph.
     *
     * @param src          - The source node of the traversal of the graph.
     * @param ParentsList  - A HashMap that contains each node and its direct parent in the graph.
     * @param DistanceList - A HashMap that contains each node and its distance from the source node.
     */
    public void dijkstra_algo(int src, HashMap<Integer, Integer> ParentsList, HashMap<Integer, Double> DistanceList) {
        PriorityQueue<Integer> NodeQueue = new PriorityQueue<>();
        HashSet<Integer> VisitedNodes = new HashSet<>();
        for (node_data node : graph_algo.getV()) {
            DistanceList.put(node.getKey(), Double.MAX_VALUE);
        }
        DistanceList.replace(src, 0.0);
        NodeQueue.add(src);
        VisitedNodes.add(src);
        while (!NodeQueue.isEmpty()) {
            int CurrentNode = NodeQueue.poll();

            for (edge_data edge : graph_algo.getE(CurrentNode)) {
                VisitedNodes.add(edge.getDest());
                double distance = DistanceList.get(CurrentNode) + edge.getWeight();
                if (distance < DistanceList.get(edge.getDest())) {
                    DistanceList.replace(edge.getDest(), distance);
                    ParentsList.put(edge.getDest(), CurrentNode);
                    NodeQueue.add(edge.getDest());
                    VisitedNodes.add(edge.getDest());
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_Algo that = (DWGraph_Algo) o;
        return graph_algo.equals(that.graph_algo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph_algo);
    }

    class NodesComparator implements Comparator<node_data> {
        @Override
        public int compare(node_data n1, node_data n2) {
            if (DistanceList.get(n1.getKey()) > DistanceList.get(n2.getKey())) {
                return -1;
            } else if (DistanceList.get(n1.getKey()) < DistanceList.get(n2.getKey())) {
                return 1;
            }
            return 0;
        }
    }
}

