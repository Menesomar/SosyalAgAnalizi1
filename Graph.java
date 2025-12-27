package model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }
    
    // EKSİK OLABİLECEK METOD BU:
    public void removeNode(Node node) {
        nodes.remove(node);
        // O düğüme bağlı tüm kenarları da sil
        edges.removeIf(edge -> edge.getSource() == node || edge.getDestination() == node);
    }

    public void addEdge(Node source, Node destination, double weight) {
        edges.add(new Edge(source, destination, weight));
    }
    
    // BU DA EKSİK OLABİLİR:
    public boolean hasEdge(Node n1, Node n2) {
        for (Edge e : edges) {
            if ((e.getSource() == n1 && e.getDestination() == n2) || 
                (e.getSource() == n2 && e.getDestination() == n1)) {
                return true;
            }
        }
        return false;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}