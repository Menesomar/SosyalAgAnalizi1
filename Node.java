package model;

public class Node {
    private String id;
    private double aktiflik;
    private int etkilesim;
    private int baglantiSayisi;
    private int x, y; // Görselleştirme için koordinatlar

    public Node(String id, double aktiflik, int etkilesim, int baglantiSayisi) {
        this.id = id;
        this.aktiflik = aktiflik;
        this.etkilesim = etkilesim;
        this.baglantiSayisi = baglantiSayisi;
    }

    public String getId() { return id; }
    public double getAktiflik() { return aktiflik; }
    public int getEtkilesim() { return etkilesim; }
    public int getBaglantiSayisi() { return baglantiSayisi; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    @Override
    public String toString() { return id; }
}