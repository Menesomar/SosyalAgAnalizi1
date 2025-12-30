package gui;

import model.Edge;
import model.Graph;
import model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GraphPanel extends JPanel {
    private Graph graph;
    private final int RADIUS = 25;
    
    // Renkler
    private final Color BG_COLOR = new Color(30, 30, 46);
    private final Color GRID_COLOR = new Color(255, 255, 255, 10);
    private final Color EDGE_COLOR = new Color(200, 200, 200, 80);

    private Map<Node, Color> specificNodeColors = new HashMap<>();
    
    // Etkileşim Değişkenleri
    private Node draggedNode = null;
    private Node connectionStartNode = null; // Bağlantı eklemek için seçilen ilk düğüm
    
    // Sağ Tık Menüsü
    private JPopupMenu popupMenu;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        this.setBackground(BG_COLOR);

        initPopupMenu(); // Menüyü oluştur

        // --- FARE OLAYLARI ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Sağ Tık Kontrolü
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                    return;
                }
                
                // Sol Tık: Sürükleme Başlat
                for (Node node : graph.getNodes()) {
                    if (isMouseOverNode(node, e.getX(), e.getY())) {
                        draggedNode = node;
                        // Eğer bağlantı modu aktifse (ikinci düğümü seçiyorsak)
                        if (connectionStartNode != null && connectionStartNode != node) {
                            finishConnection(node);
                        }
                        return;
                    }
                }
                
                // Boşluğa tıklanırsa bağlantı seçimini iptal et
                if (connectionStartNode != null) {
                    connectionStartNode = null;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
                setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                   checkClick(e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setX(e.getX());
                    draggedNode.setY(e.getY());
                    repaint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
        });
    }

    private void initPopupMenu() {
        popupMenu = new JPopupMenu();
    }

    private void showContextMenu(MouseEvent e) {
        popupMenu.removeAll(); // Menüyü temizle

        Node clickedNode = null;
        for (Node node : graph.getNodes()) {
            if (isMouseOverNode(node, e.getX(), e.getY())) {
                clickedNode = node;
                break;
            }
        }

        if (clickedNode != null) {
            // --- DÜĞÜME TIKLANDIYSA ---
            Node finalNode = clickedNode;
            
            JMenuItem itemDelete = new JMenuItem("❌ Düğümü Sil");
            itemDelete.addActionListener(ev -> {
                graph.removeNode(finalNode);
                repaint();
            });
            
            JMenuItem itemConnect = new JMenuItem("🔗 Bağlantı Ekle (Bunu Seç)");
            itemConnect.addActionListener(ev -> {
                connectionStartNode = finalNode;
                JOptionPane.showMessageDialog(this, "Şimdi bağlanacak ikinci düğüme sol tıklayın.");
                repaint();
            });

            popupMenu.add(new JLabel("  Seçilen: " + clickedNode.getId()));
            popupMenu.addSeparator();
            popupMenu.add(itemConnect);
            popupMenu.add(itemDelete);

        } else {
            // --- BOŞLUĞA TIKLANDIYSA ---
            int x = e.getX();
            int y = e.getY();

            JMenuItem itemAdd = new JMenuItem("➕ Yeni Kişi Ekle");
            itemAdd.addActionListener(ev -> {
                String id = JOptionPane.showInputDialog("Yeni Kişi Adı/ID:");
                if (id != null && !id.isEmpty()) {
                    // Rastgele özellikler ata
                    Random r = new Random();
                    Node newNode = new Node(id, r.nextDouble(), r.nextInt(20), 0);
                    newNode.setX(x);
                    newNode.setY(y);
                    graph.addNode(newNode);
                    repaint();
                }
            });
            popupMenu.add(itemAdd);
        }

        popupMenu.show(this, e.getX(), e.getY());
    }
    
    // İki düğüm arasına bağlantı kurma işlemi
    private void finishConnection(Node targetNode) {
        if (!graph.hasEdge(connectionStartNode, targetNode)) {
            // Dinamik ağırlık hesapla (Proje formülü)
            double weight = calculateWeight(connectionStartNode, targetNode);
            graph.addEdge(connectionStartNode, targetNode, weight);
            
            // Bağlantı sayısını güncelle (Basitçe +1 yapıyoruz görsel için)
            // Not: Gerçek analizde Centrality tekrar çalıştırılmalı.
        } else {
            JOptionPane.showMessageDialog(this, "Bu bağlantı zaten var!");
        }
        connectionStartNode = null; // Seçimi sıfırla
        repaint();
    }
    
    // Ağırlık Hesaplama Formülü (Proje İsteri 4.3)
    private double calculateWeight(Node n1, Node n2) {
        double diffAktiflik = Math.abs(n1.getAktiflik() - n2.getAktiflik());
        double diffEtkilesim = Math.abs(n1.getEtkilesim() - n2.getEtkilesim());
        double diffBaglanti = Math.abs(n1.getBaglantiSayisi() - n2.getBaglantiSayisi());
        
        return 1.0 / ( (1 + diffAktiflik) * (2 + diffEtkilesim) * (2 + diffBaglanti) );
    }

    private boolean isMouseOverNode(Node node, int x, int y) {
        return Math.sqrt(Math.pow(x - node.getX(), 2) + Math.pow(y - node.getY(), 2)) < RADIUS;
    }

    public void setNodeColors(Map<Node, Color> colors) {
        this.specificNodeColors = colors;
        repaint();
    }
    
    public void resetColors() {
        this.specificNodeColors.clear();
        repaint();
    }

    private void checkClick(int mouseX, int mouseY) {
        for (Node node : graph.getNodes()) {
            if (isMouseOverNode(node, mouseX, mouseY)) {
                String htmlInfo = "<html><body style='width: 200px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #4682B4;'>👤 " + node.getId() + "</h2>" +
                        "<p><b>Aktiflik:</b> " + node.getAktiflik() + "</p>" +
                        "<p><b>Etkileşim:</b> " + node.getEtkilesim() + "</p>" +
                        "</body></html>";
                JOptionPane.showMessageDialog(this, htmlInfo, "Bilgi", JOptionPane.PLAIN_MESSAGE);
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2d);

        if (graph == null) return;

        // Çizgiler
        g2d.setStroke(new BasicStroke(2));
        for (Edge edge : graph.getEdges()) {
            Node src = edge.getSource();
            Node dest = edge.getDestination();
            g2d.setColor(EDGE_COLOR);
            g2d.drawLine(src.getX(), src.getY(), dest.getX(), dest.getY());
            drawWeightLabel(g2d, edge);
        }
        
        // Eğer bağlantı ekleme modundaysak geçici çizgi çiz (Start -> Mouse)
        if (connectionStartNode != null) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
            Point p = getMousePosition();
            if (p != null) {
                g2d.drawLine(connectionStartNode.getX(), connectionStartNode.getY(), p.x, p.y);
            }
        }

        // Düğümler
        int diameter = RADIUS * 2;
        for (Node node : graph.getNodes()) {
            Color baseColor = specificNodeColors.getOrDefault(node, new Color(70, 130, 180));
            // Seçili bağlantı düğümü ise Sarı yap
            if (node == connectionStartNode) baseColor = Color.YELLOW;

            // Gölge
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillOval(node.getX() - RADIUS + 4, node.getY() - RADIUS + 4, diameter, diameter);

            // Dolgu
            g2d.setColor(baseColor);
            g2d.fillOval(node.getX() - RADIUS, node.getY() - RADIUS, diameter, diameter);

            // Çerçeve
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(node.getX() - RADIUS, node.getY() - RADIUS, diameter, diameter);

            // Yazı
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(node.getId(), node.getX() - fm.stringWidth(node.getId()) / 2, node.getY() + 5);
        }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(GRID_COLOR);
        int gridSize = 40;
        for (int x = 0; x < getWidth(); x += gridSize) g2d.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += gridSize) g2d.drawLine(0, y, getWidth(), y);
    }

    private void drawWeightLabel(Graphics2D g2d, Edge edge) {
        int midX = (edge.getSource().getX() + edge.getDestination().getX()) / 2;
        int midY = (edge.getSource().getY() + edge.getDestination().getY()) / 2;
        String text = String.format("%.3f", edge.getWeight());
        g2d.setColor(new Color(30, 30, 46, 200));
        g2d.fillRoundRect(midX - 18, midY - 10, 46, 18, 5, 5);
        g2d.setColor(new Color(100, 255, 218));
        g2d.setFont(new Font("Consolas", Font.PLAIN, 10));
        g2d.drawString(text, midX - 12, midY + 3);
    }
}