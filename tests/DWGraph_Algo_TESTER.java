
import api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.*;

/**
 * A JUnit 5 testing class built to test all of the functions built in the DWGraph_Algo class.
 */
public class DWGraph_Algo_TESTER {

    int node;
    private double DefaultWeight = 10;
    private static Random random = new Random();
    private directed_weighted_graph TestGraph;
    private dw_graph_algorithms TestGraphAlgo;

    void initGraph(directed_weighted_graph g){
        TestGraph = g;
    }

    public directed_weighted_graph GraphCreator(int NodeSize, int EdgeSize) {
        int EdgeCounter = 0;
        int NodeA , NodeB;
        HashMap<Integer, ArrayList<Integer>> EdgeList = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>>  receivingEdges = new HashMap<>();

        for(int key = 0; key < NodeSize; key++) {
            node_data newNode = new NodeData(key);
            TestGraph.addNode(newNode);
            EdgeList.put(key, new ArrayList<>());
        }
        for (int edges = 0; edges < EdgeSize; edges++) {
            NodeA = random.nextInt(NodeSize);
            NodeB = random.nextInt(NodeSize);
            while (NodeA == NodeB || EdgeList.get(NodeA).contains(NodeB)) {
                NodeB = random.nextInt(NodeSize);
            }
            TestGraph.connect(NodeA, NodeB, DefaultWeight);
            receivingEdges.put(NodeB, new ArrayList<>());
            EdgeList.get(NodeA).add(NodeB);
            receivingEdges.get(NodeB).add(NodeA);
            EdgeCounter++;
        }
        return TestGraph;
    }

    @BeforeEach
    void init(){
        TestGraph = new DWGraph_DS();
        TestGraphAlgo = new DWGraph_Algo();
        TestGraphAlgo.init(TestGraph);
        initGraph(TestGraph);
    }
    @Test
    void Copy_Test(){
        int NodeSize= 100;
        for(int key = 0; key < NodeSize; key++) {
            node_data newNode = new NodeData(key);
            TestGraph.addNode(newNode);
        }
        for(node = 0; node < NodeSize - 1 ; node++) {
            TestGraph.connect(node,node+1, DefaultWeight);
        }
        directed_weighted_graph NewCopiedGraph = TestGraphAlgo.copy();
        assertEquals(NewCopiedGraph.nodeSize() , TestGraph.nodeSize());
        assertEquals(NewCopiedGraph.edgeSize(), TestGraph.edgeSize());
        TestGraph.removeNode(5);
        TestGraph.removeEdge(0,1);
        assertEquals(NewCopiedGraph.nodeSize() , TestGraph.nodeSize() + 1); //removed one node
        assertEquals(NewCopiedGraph.edgeSize(), TestGraph.edgeSize() + 3); //removed one edge and a node with 2 edges

    }

    @Test
    void Connected_Graph_Test_1() {
        int NodeSize = 100;
        for(int key = 0; key < NodeSize; key++) {
            node_data newNode = new NodeData(key);
            TestGraph.addNode(newNode);
        }
        for(node=0; node < NodeSize - 1; node++){
            TestGraph.connect(node,node+1, DefaultWeight);
            //TestGraph.connect(node, node+2, DefaultWeight);
        }
        TestGraph.connect(NodeSize-1, 0, DefaultWeight);
        assertTrue(TestGraphAlgo.isConnected());
    }
    @Test
    void Connected_Graph_Test_2() {
        GraphCreator(10000,9000);
        assertFalse(TestGraphAlgo.isConnected());
        //Any graph with (NodeSize-1) edges is not connected.
    }
    @Test
    void Shortest_Path_Dist_Test() {
        int NodeSize = 10000;
        for(int key = 0; key < NodeSize; key++) {
            node_data newNode = new NodeData(key);
            TestGraph.addNode(newNode);
        }
        for(node = 0; node < NodeSize - 1; node++) {
            TestGraph.connect(node,node + 1,DefaultWeight);
        }
        int NodeA = random.nextInt(NodeSize);
        int NodeB = random.nextInt(NodeSize);
        int PathDistance = Math.abs(NodeA-NodeB);
        if (NodeA<NodeB)
            assertEquals(TestGraphAlgo.shortestPathDist(NodeA, NodeB) , (PathDistance * DefaultWeight));
        else
            assertEquals(TestGraphAlgo.shortestPathDist(NodeA, NodeB) , -1);


    }
    @Test
    void Shortest_Path_Test() {
        int NodeSize = 10000;
        for(int key = 0; key < NodeSize; key++) {
            node_data newNode = new NodeData(key);
            TestGraph.addNode(newNode);
        }
        for(node = 0; node < NodeSize - 1; node++) {
            TestGraph.connect(node,node + 1,DefaultWeight);
        }
        int removedNode = (NodeSize / 2);
        TestGraph.removeNode(removedNode);
        assertEquals(-1 , TestGraphAlgo.shortestPathDist(removedNode - 1,removedNode + 1));
        assertEquals(0 , TestGraphAlgo.shortestPathDist(0,0));
        assertNull(TestGraphAlgo.shortestPath(removedNode - 1,removedNode + 1));
        assertNotNull(TestGraphAlgo.shortestPath(0,5));
    }
    @Test
    void Shortest_Path_Test_2() {
        int NodeSize = 15;
        for(int key = 0; key < NodeSize; key++) {
            node_data newNode = new NodeData(key);
            TestGraph.addNode(newNode);
        }
        TestGraph.connect(0,3,17);
        TestGraph.connect(10,7,9);
        TestGraph.connect(5,9,11);
        TestGraph.connect(4,6,14);
        TestGraph.connect(4,12,7);
        TestGraph.connect(1,8,20);
        TestGraph.connect(8,11,4);
        TestGraph.connect(2,13,15);
        TestGraph.connect(6,5,2);
        TestGraph.connect(12,1,3);
        TestGraph.connect(11,2,19);
        TestGraph.connect(13,7,8);
        TestGraph.connect(13,3,18);
        TestGraph.connect(14,2,6);
        TestGraph.connect(9,14,10);
        assertFalse(TestGraphAlgo.isConnected());
        assertNotNull(TestGraphAlgo.shortestPath(5,13));
        assertEquals(42.0, TestGraphAlgo.shortestPathDist(5,13));
        TestGraph.connect(9,14,20); // updating edge weight to check if it changes paths
        TestGraph.connect(9,11,1); // updating edge weight to check if it changes paths
        assertEquals(46.0, TestGraphAlgo.shortestPathDist(5,13));
        assertEquals(-1, TestGraphAlgo.shortestPathDist(9,0));
        TestGraph.connect(8,0,3); //Connecting edge to check path
        TestGraph.connect(3,0,5);
        assertEquals(58.0, TestGraphAlgo.shortestPathDist(9,0));

    }
    @Test
    void save_and_load() throws IOException {
        String file = "MyGraph.txt";
        String LoadFile = "data/A0";
        GraphCreator(10,15);
        assertTrue(TestGraphAlgo.save(file));
        TestGraph.removeNode(5);
        assertNull(TestGraph.getNode(5));
        TestGraph = new DWGraph_DS();
        TestGraphAlgo.init(TestGraph);
        assertTrue(TestGraphAlgo.load(LoadFile));
        assertEquals(11 ,TestGraph.nodeSize());
        assertEquals(22 ,TestGraph.edgeSize());
        // After loading the graph, node 5 still exists because it was removed after the graph was saved.
        assertNotNull(TestGraph.getNode(5));
    }


    @Test
    void General_Test() {
        GraphCreator(10000, 10000);
        TestGraph.removeNode(404);
        assertEquals(null, TestGraph.getNode(404)); //Should be null, 404 not found :)
        assertEquals(9999, TestGraph.nodeSize());
        assertFalse(TestGraphAlgo.isConnected());
    }


}