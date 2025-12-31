package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.awt.Color;
import java.util.*;

public class WelshPowell {

    // Her düğüme bir renk atayan metod
    public Map<Node, Color> colorGraph(Graph graph) {
        // 1. Düğümleri derecelerine (bağlantı sayılarına) göre BÜYÜKTEN KÜÇÜĞE sırala
        List<Node> sortedNodes = new ArrayList<>(graph.getNodes());
        sortedNodes.sort((n1, n2) -> Integer.compare(n2.getBaglantiSayisi(), n1.getBaglantiSayisi()));

        Map<Node, Color> nodeColors = new HashMap<>();
        
        // Kullanılabilecek Renk Paleti (İstersen daha fazla ekleyebilirsin)
        Color[] palette = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, 
            Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.PINK, Color.LIGHT_GRAY
        };
        
        int colorIndex = 0;

        // 2. Renklendirme Döngüsü
        for (Node node : sortedNodes) {
            // Eğer düğüm zaten boyanmışsa atla
            if (nodeColors.containsKey(node)) continue;

            // Yeni bir renk seç
            Color currentColor = (colorIndex < palette.length) ? palette[colorIndex] : getRandomColor();
            colorIndex++;

            // Önce bu düğümü boya
            nodeColors.put(node, currentColor);

            // Sonra bu düğümle komşu OLMAYAN diğer düğümleri de aynı renge boya
            for (Node otherNode : sortedNodes) {
                if (!nodeColors.containsKey(otherNode) && !isConnected(graph, node, otherNode)) {
                    // Ancak boyayacağımız bu "otherNode", şimdiki renge sahip başka bir düğümle komşu olmamalı
                    if (canBeColored(graph, otherNode, currentColor, nodeColors)) {
                        nodeColors.put(otherNode, currentColor);
                    }
                }
            }
        }
        return nodeColors;
    }

    // İki düğüm arasında bağlantı var mı?
    private boolean isConnected(Graph graph, Node n1, Node n2) {
        for (Edge edge : graph.getEdges()) {
            if ((edge.getSource() == n1 && edge.getDestination() == n2) ||
                (edge.getSource() == n2 && edge.getDestination() == n1)) {
                return true;
            }
        }
        return false;
    }

    // Bir düğüm belirtilen renge boyanabilir mi? (Komşularında o renk var mı?)
    private boolean canBeColored(Graph graph, Node node, Color color, Map<Node, Color> currentColors) {
        for (Edge edge : graph.getEdges()) {
            Node neighbor = null;
            if (edge.getSource() == node) neighbor = edge.getDestination();
            else if (edge.getDestination() == node) neighbor = edge.getSource();

            if (neighbor != null && currentColors.containsKey(neighbor)) {
                if (currentColors.get(neighbor).equals(color)) {
                    return false; // Komşusu bu renkte, boyayamazsın!
                }
            }
        }
        return true;
    }

    private Color getRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}