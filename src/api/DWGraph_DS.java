package api;

import java.util.*;

/**
 * This class was created in order to implement a weighted directed graph. This was done using the
 * implementation of the node_data and edge_data classes.
 *
 * @author Alon Firestein
 */
public class DWGraph_DS implements directed_weighted_graph {
    private int EdgeCounter, MCcounter;
    private HashMap<Integer, node_data> NodesInGraph;
    private HashMap<node_data, HashMap<node_data, edge_data>> EdgesInGraph;
    //Because this is a directed graph, we need to map all the nodes that act only as the dest node in the edge.
    private HashMap<node_data, HashSet<node_data>> NodesWithReceivingEdges;

    public DWGraph_DS() {
        EdgeCounter = MCcounter = 0;
        NodesInGraph = new HashMap<>();
        EdgesInGraph = new HashMap<>();
        NodesWithReceivingEdges = new HashMap<>();
    }


    @Override
    public node_data getNode(int key) {
        return NodesInGraph.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        return EdgesInGraph.get(getNode(src)).get(getNode(dest));
    }

    @Override
    public void addNode(node_data n) {
        if (!NodesInGraph.containsKey(n.getKey())) {
            NodesInGraph.put(n.getKey(), n);
            EdgesInGraph.put(n, new HashMap<>());
            NodesWithReceivingEdges.put(n, new HashSet<>());
            MCcounter++;
        }
    }

    /**
     * Connecting and creating and edge between two nodes, if one of the nodes does not exist then it shall
     * return an error and alert the user.
     * If an edge between the two nodes already exist, and we update it with the new input weight.
     * @param src - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {

        if (src == dest) {
            return;
        }
        if (getNode(src) == null || getNode(dest) == null) {
            System.err.println("ERROR: One of the Nodes does not exist - " + getNode(src) + " or " + getNode(dest));
            return;
        }
        edge_data NewEdge = new EdgeData(src, dest, w);
        if (getEdge(src, dest) != null && getEdge(src,dest).getWeight() != w) {
            EdgesInGraph.get(getNode(src)).replace(getNode(dest), NewEdge);
            EdgeCounter++;
            MCcounter++;
            return;
        }
        EdgesInGraph.get(getNode(src)).put(getNode(dest), NewEdge);
        NodesWithReceivingEdges.get(getNode(dest)).add(getNode(src));
        EdgeCounter++;
        MCcounter++;
    }

    /**
     * @return - A collection of all the nodes in the graph.
     */
    @Override
    public Collection<node_data> getV() {
        return NodesInGraph.values();
    }

    /**
     * @param node_id - the node of which we want to find the edges where node_id is the source node.
     * @return - A collection of all the nodes that are the dest of the edge connected to the node_id.
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        //if (getNode(node_id).equals(null)) { return null; }
        return EdgesInGraph.get(getNode(node_id)).values();
    }

    /**
     * When removing a node, this function also deletes all the edges that are connected to the node,
     * whether its the src node or the dest node.
     * @param key - the ID of the node we want to remove
     * @return - the data of the node we removed.
     */
    @Override
    public node_data removeNode(int key) {
        if (NodesInGraph.get(key) == null || !NodesInGraph.containsKey(key)) {
            return null;
        }
        ArrayList<node_data> NeighboursToRemove = new ArrayList<>();
        ArrayList<node_data> ToRemoveFromNeighbours = new ArrayList<>();
        node_data result = getNode(key);

        //If our node doesn't have neighbours
        if (EdgesInGraph.get(getNode(key)).isEmpty()) {
            NodesInGraph.remove(key);
            MCcounter++;
            return result;
        }

        NeighboursToRemove.addAll(EdgesInGraph.get(getNode(key)).keySet());
        //Removing all of the nodes edges
        for (node_data neighbour : NeighboursToRemove) {
            removeEdge(key, neighbour.getKey());
        }

        ToRemoveFromNeighbours.addAll(NodesWithReceivingEdges.get(getNode(key)));
        for (node_data neighbour : ToRemoveFromNeighbours) {
            removeEdge(neighbour.getKey(), key);
            //Because we activated the removeEdge function twice, but only removed one edge:
            //MCcounter--;
        }

        NodesInGraph.remove(key);
        MCcounter++;
        return result;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        if (getNode(src) == null || getNode(dest) == null) { return null; }
        NodesWithReceivingEdges.get(getNode(dest)).remove(getNode(src));
        EdgeCounter--;
        MCcounter++;
        return EdgesInGraph.get(getNode(src)).remove(getNode(dest));
    }

    @Override
    public int nodeSize() {
        return NodesInGraph.size();
    }

    @Override
    public int edgeSize() {
        return EdgeCounter;
    }

    @Override
    public int getMC() {
        return MCcounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_DS that = (DWGraph_DS) o;
        return EdgeCounter == that.EdgeCounter &&
                MCcounter == that.MCcounter &&
                NodesInGraph.equals(that.NodesInGraph) &&
                EdgesInGraph.equals(that.EdgesInGraph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EdgeCounter, MCcounter, NodesInGraph, EdgesInGraph);
    }

    @Override
    public String toString() {
        String result = "";
        for (node_data node : NodesInGraph.values()) {
            result += "Node: " + node + ", Neighbours: " + EdgesInGraph.get(node).keySet() + "\n";
        }
        return result;
    }
}
