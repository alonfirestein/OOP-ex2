package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyTest {

    public static void main(String[] args) throws IOException {

        node_data a = new NodeData(1);
        node_data b = new NodeData(2);
        node_data c = new NodeData(3);
        node_data d = new NodeData(4);
        node_data e = new NodeData(5);

        directed_weighted_graph g = new DWGraph_DS();
        dw_graph_algorithms g2 = new DWGraph_Algo();
        g2.init(g);


        System.out.println(g.nodeSize()); // 0
        g.addNode(a);
        g.addNode(b);
        g.addNode(c);
        g.addNode(d);
        g.addNode(e);
        System.out.println(g.nodeSize()); // 5
        System.out.println(g.edgeSize()); // 0
        g.connect(2,1,5);
        g.connect(1,4,8);
        g.connect(3,4,2);
        g.connect(4,5,12);
        g.connect(5,3,4);
        g.connect(4,2,1);

        directed_weighted_graph newGraph = new DWGraph_DS();
        dw_graph_algorithms g3 = new DWGraph_Algo();
        System.out.println("\n\n********New SAVE/LOAD Graph Test*******\n");
        g3.init(newGraph);
        System.out.println(g3.load("data/A0"));
        System.out.println(newGraph);


















    }
}
