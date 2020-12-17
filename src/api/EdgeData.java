package api;

import java.util.Objects;

/**
 * This class was created in order to implement an edge in a weighted directed graph.
 *
 * @author Alon Firestein
 */
public class EdgeData implements edge_data {
    int edge_src, edge_dest, edge_tag;
    double edge_weight;
    String EdgeInfo;

    public EdgeData(int src, int dest, double weight) {
        this.edge_src = src;
        this.edge_dest = dest;
        this.edge_weight = weight;
    }
    public EdgeData(edge_data other) {
        edge_src = other.getSrc();
        edge_dest = other.getDest();
        edge_weight = other.getWeight();
        edge_tag = other.getTag();
        EdgeInfo = other.getInfo();
    }

    @Override
    public int getSrc() {
        return edge_src;
    }

    @Override
    public int getDest() {
        return edge_dest;
    }

    @Override
    public double getWeight() {
        return edge_weight;
    }

    @Override
    public String getInfo() {
        return EdgeInfo;
    }

    @Override
    public void setInfo(String s) {
        EdgeInfo = s;
    }

    @Override
    public int getTag() {
        return edge_tag;
    }

    @Override
    public void setTag(int t) {
        edge_tag = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeData edgeData = (EdgeData) o;
        return edge_src == edgeData.edge_src &&
                edge_dest == edgeData.edge_dest &&
                edge_tag == edgeData.edge_tag &&
                Double.compare(edgeData.edge_weight, edge_weight) == 0 &&
                EdgeInfo.equals(edgeData.EdgeInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge_src, edge_dest, edge_tag, edge_weight, EdgeInfo);
    }

    @Override
    public String toString() {
        return "{" + edge_src + "->" + edge_dest + ", Weight:" + edge_weight + "}";
    }
}
