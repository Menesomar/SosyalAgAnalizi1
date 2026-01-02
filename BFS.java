package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.*;

public class BFS {
    
    // Verilen başlangıç düğümünden (startNode) gidilebilecek TÜM düğümleri bulur
    public List<Node> run(Graph graph, Node startNode) {
        List<Node> visited = new ArrayList<>();       // Ziyaret edilenlerin listesi
        Queue<Node> queue = new LinkedList<>();       // Sıradaki düğümler
        Set<String> visitedIds = new HashSet<>();     // Tekrarı önlemek için ID seti

        // Başlangıç düğümünü ekle
        queue.add(startNode);
        visited.add(startNode);
        visitedIds.add(startNode.getId());

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Mevcut düğümün (current) tüm komşularını bul
            for (Edge edge : graph.getEdges()) {
                Node neighbor = null;

                // Bağlantı yönsüz olduğu için iki uca da bakıyoruz
                if (edge.getSource().getId().equals(current.getId())) {
                    neighbor = edge.getDestination();
                } else if (edge.getDestination().getId().equals(current.getId())) {
                    neighbor = edge.getSource();
                }

                // Eğer komşu varsa ve daha önce gezilmediyse listeye ekle
                if (neighbor != null && !visitedIds.contains(neighbor.getId())) {
                    visited.add(neighbor);
                    visitedIds.add(neighbor.getId());
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }
}