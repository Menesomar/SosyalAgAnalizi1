package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.*;

public class DFS {
    // Stack (Yığın) kullanarak derinlemesine arama yapar
    public List<Node> run(Graph graph, Node startNode) {
        List<Node> visited = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        Set<String> visitedIds = new HashSet<>();

        stack.push(startNode);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (!visitedIds.contains(current.getId())) {
                visited.add(current);
                visitedIds.add(current.getId());

                // Komşuları bul
                for (Edge edge : graph.getEdges()) {
                    Node neighbor = null;
                    if (edge.getSource().getId().equals(current.getId())) {
                        neighbor = edge.getDestination();
                    } else if (edge.getDestination().getId().equals(current.getId())) {
                        neighbor = edge.getSource();
                    }

                    // Ziyaret edilmediyse yığına ekle
                    if (neighbor != null && !visitedIds.contains(neighbor.getId())) {
                        stack.push(neighbor);
                    }
                }
            }
        }
        return visited;
    }
}
