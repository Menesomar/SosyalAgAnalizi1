package model;

public class Edge {
    private Node source;      // Kaynak Düğüm
    private Node destination; // Hedef Düğüm
    private double weight;    // Ağırlık (Maliyet)

    // Yapıcı Metod (Constructor) - Artık 3 parametre alıyor
    public Edge(Node source, Node destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    // Getter Metodları
    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }
    
    // Setter (Gerekirse diye)
    public void setWeight(double weight) {
        this.weight = weight;
    }
}