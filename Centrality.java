package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.*;

public class Centrality {

    public List<Node> getTopNodes(Graph graph, int topN) {
        Map<Node, Integer> degreeMap = new HashMap<>();
        
        for (Node node : graph.getNodes()) {
            // SADECE "getDegree" metodunu çağırıyoruz, hesaplama orada düzeldi.
            degreeMap.put(node, getDegree(graph, node));
        }

        List<Node> sortedNodes = new ArrayList<>(degreeMap.keySet());
        sortedNodes.sort((n1, n2) -> degreeMap.get(n2) - degreeMap.get(n1));

        if (sortedNodes.size() > topN) {
            return sortedNodes.subList(0, topN);
        } else {
            return sortedNodes;
        }
    }
    
    // DÜZELTİLEN KISIM BURASI:
    public int getDegree(Graph graph, Node node) {
        int degree = 0;
        for (Edge edge : graph.getEdges()) {
            // Eskiden "|| edge.getDestination() == node" diyorduk, onu sildik.
            // Sadece Kaynak (Source) bu düğümse sayıyoruz.
            if (edge.getSource().getId().equals(node.getId())) {
                degree++;
            }
        }
        return degree;
    }
}