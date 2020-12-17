package api;

import java.util.Objects;

/**
 * This class was created in order to implement a node in a weighted directed graph.
 *
 * @author Alon Firestein
 */
public class NodeData implements node_data  {
    private int key, tag;
    private double weight;
    private geo_location location;
    private String NodeInfo;

    public NodeData(int key) {
        this.key = key;
        tag = 0;
        location = null;
        NodeInfo = "";
    }

    public NodeData(node_data otherNode) {
        key = otherNode.getKey();
        tag = otherNode.getTag();
        weight = otherNode.getWeight();
        location = otherNode.getLocation();
        NodeInfo = otherNode.getInfo();
    }

    @Override
    public int getKey() {return key; }

    @Override
    public geo_location getLocation() {
        return location;
    }

    @Override
    public void setLocation(geo_location p) {
        location = p;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double w) {
        if (w<0) { throw new IllegalArgumentException("ERR: Weight of a node cannot be negative!"); }
        weight = w;
    }

    @Override
    public String getInfo() {
        return NodeInfo;
    }

    @Override
    public void setInfo(String s) {
        NodeInfo = s;
    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public void setTag(int t) {
        tag = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeData nodeData = (NodeData) o;
        return key == nodeData.key &&
                tag == nodeData.tag &&
                Double.compare(nodeData.weight, weight) == 0 &&
                location.equals(nodeData.location) &&
                NodeInfo.equals(nodeData.NodeInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, tag, weight, location, NodeInfo);
    }

    @Override
    public String toString() {
        return "" + key;
    }
}
