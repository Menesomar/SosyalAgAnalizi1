package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.*;

public class Dijkstra {

    public List<Node> findShortestPath(Graph graph, Node start, Node end) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, Node> previousNodes = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> distances.getOrDefault(n.getId(), Double.MAX_VALUE)));
        Set<String> visited = new HashSet<>();

        // Başlangıç ayarları
        for (Node node : graph.getNodes()) {
            distances.put(node.getId(), Double.MAX_VALUE);
        }
        distances.put(start.getId(), 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            visited.add(current.getId());

            if (current.getId().equals(end.getId())) break; // Hedefe ulaştık

            for (Edge edge : graph.getEdges()) {
                Node neighbor = null;
                if (edge.getSource().getId().equals(current.getId())) neighbor = edge.getDestination();
                else if (edge.getDestination().getId().equals(current.getId())) neighbor = edge.getSource();

                if (neighbor != null && !visited.contains(neighbor.getId())) {
                    double newDist = distances.get(current.getId()) + edge.getWeight();
                    
                    if (newDist < distances.get(neighbor.getId())) {
                        distances.put(neighbor.getId(), newDist);
                        previousNodes.put(neighbor.getId(), current);
                        queue.remove(neighbor); // Güncellemek için çıkarıp ekliyoruz
                        queue.add(neighbor);
                    }
                }
            }
        }

        // Yolu geri sararak (Backtracking) listeyi oluştur
        List<Node> path = new ArrayList<>();
        Node current = end;
        while (current != null) {
            path.add(0, current); // Başa ekle
            current = previousNodes.get(current.getId());
        }

        // Eğer başlangıç düğümü yolda yoksa, yol bulunamamış demektir
        if (path.isEmpty() || !path.get(0).getId().equals(start.getId())) {
            return new ArrayList<>(); // Boş liste dön
        }

        return path;
    }
}
