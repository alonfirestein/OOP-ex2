
import api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A JUnit 5 testing class built to test all of the functions built in the DWGraph_DS class.
 */
public class DWGraph_DS_TEST {

    private int node;
    private double DefaultWeight = 10;
    private directed_weighted_graph TestGraph;
    private int SEED = 6;
    private Random rand = new Random(SEED);


    void initGraph(directed_weighted_graph g) {
        TestGraph = g;
    }

    @BeforeEach
    void init() {
        TestGraph = new DWGraph_DS();
        initGraph(TestGraph);
    }

    @Test
    @DisplayName("Number of Nodes Test")
    void Nodes_Test(){
        int NodeSize = 1000;
        for( node = 0 ; node < NodeSize; node++){
            node_data a = new NodeData(node);
            TestGraph.addNode(a);
        }
        assertEquals(NodeSize, TestGraph.nodeSize());
        int removeCounter = 0;
        for (node = 5; node < NodeSize; node+=5) {
            TestGraph.removeNode(node);
            removeCounter++;
            assertNull(TestGraph.getNode(node));
        }
        assertEquals(NodeSize - removeCounter, TestGraph.nodeSize());

    }

    @Test
    @DisplayName("Number of Edges Test")
    void Edges_Test() {
        int NodeSize = 1000;
        int EdgeCounter = 0;
        for(node = 0; node < NodeSize; node += 3) {
            node_data a = new NodeData(node);
            node_data b = new NodeData(node+1);
            TestGraph.addNode(a);
            TestGraph.addNode(b);
            TestGraph.connect(a.getKey(),b.getKey(), DefaultWeight);
            EdgeCounter++;
        }
        assertEquals(EdgeCounter, TestGraph.edgeSize());
        for (node = 0; node < NodeSize; node+= 3) {
            TestGraph.removeEdge(node, node+1);
            assertFalse(TestGraph.getE(node).contains(TestGraph.getNode(node+1)));
        }
        assertEquals(0, TestGraph.edgeSize());
    }

    @Test
    void Removing_Nodes_Test(){
        int NodeSize = 10000;
        for(node = 0; node < NodeSize; node++) {
            node_data a = new NodeData(node);
            TestGraph.addNode(a);
        }
        for(node = 0; node < NodeSize - 1 ; node++) {
            TestGraph.connect(node,node + 1, DefaultWeight);
        }
        int NumOfEdges = TestGraph.edgeSize();
        for(node = 2; node < NodeSize; node +=7) {
            TestGraph.removeNode(node);
            NodeSize--;
            NumOfEdges -= 2;
            assertNull(TestGraph.getNode(node));
        }
        //Checking number of edges and nodes in graph after removals.
        assertEquals(TestGraph.nodeSize(),NodeSize);
        assertEquals(TestGraph.edgeSize(),NumOfEdges);
    }



    @Test
    void Edges_Test_2(){
        int NodeSize = 50000;
        int EdgeCounter = 0;
        for( node = 0 ; node < NodeSize; node++) {
            node_data a = new NodeData(node);
            TestGraph.addNode(a);
        }
        assertEquals(TestGraph.nodeSize(), NodeSize);

        for(node = 0; node < 25000; node++){
            int FirstNode = rand.nextInt(TestGraph.nodeSize());
            int SecondNode = rand.nextInt(TestGraph.nodeSize());
            double RandomWeight = rand.nextDouble()*5;
            while (TestGraph.getE(FirstNode).contains(TestGraph.getNode(SecondNode)) || FirstNode == SecondNode) {
                SecondNode = rand.nextInt(NodeSize);
            }
            TestGraph.connect(FirstNode, SecondNode, RandomWeight);
            EdgeCounter++;
        }
        assertNotNull(TestGraph.edgeSize());
        assertEquals(TestGraph.edgeSize(), EdgeCounter);
    }

    @Test
    @DisplayName("MC Test")
    void MCCounter_Test() {
        int NodeSize = 1000;
        int MCcounterCheck = 0;
        for (node = 0; node < NodeSize; node++) {
            node_data a = new NodeData(node);
            TestGraph.addNode(a);
            MCcounterCheck++;
        }
        for(node = 0; node < 300; node++){
            int CurrentNode = rand.nextInt(NodeSize);
            if (node != CurrentNode && !TestGraph.getE(node).contains(CurrentNode)) {
                TestGraph.connect(node, CurrentNode, DefaultWeight);
                MCcounterCheck++;
            }
        }
        assertEquals(TestGraph.getMC(), MCcounterCheck);
    }

    @Test
    void General_Test() {
        int MCcheck = 0, EdgeCheck = 0, NodeCheck = 0;
        int NodeSize = 100;
        HashMap<Integer, ArrayList<Integer>> receivingEdges = new HashMap<>();
        for (node = 0; node < NodeSize; node++) {
            node_data a = new NodeData(node);
            TestGraph.addNode(a);
            receivingEdges.put(node, new ArrayList<>());
            MCcheck++;
            NodeCheck++;
        }
        for (node = 0; node < 100; node++){
            int FirstNode = rand.nextInt(NodeSize);
            int SecondNode = rand.nextInt(NodeSize);
            double RandomWeight = rand.nextDouble()*10;
            while (FirstNode == SecondNode || TestGraph.getE(FirstNode).contains(SecondNode)) {
                SecondNode = rand.nextInt(NodeSize);
            }
            TestGraph.connect(FirstNode, SecondNode, RandomWeight);
            receivingEdges.get(SecondNode).add(FirstNode);
            MCcheck++;
            EdgeCheck++;
        }
        for (node = 0; node < TestGraph.nodeSize(); node+=10) {
            if (!TestGraph.getE(node).isEmpty()) {
                EdgeCheck -= (TestGraph.getE(node).size() + receivingEdges.get(node).size());
                MCcheck += (TestGraph.getE(node).size() + receivingEdges.get(node).size());
            }
            TestGraph.removeNode(node);
            MCcheck++;
            NodeCheck--;
        }
        assertEquals(TestGraph.nodeSize(), NodeCheck);
        assertEquals(TestGraph.edgeSize(), EdgeCheck);
        assertEquals(TestGraph.getMC(), MCcheck);
        assertNotNull(TestGraph);
        assertNull(TestGraph.getNode(0));
    }



}
