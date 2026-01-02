import algorithms.BFS;
import algorithms.DFS;
import algorithms.Dijkstra;
import algorithms.WelshPowell;
import algorithms.Centrality;
import fileio.GraphLoader;
import fileio.GraphSaver; // YENİ: Kaydetme sınıfı
import gui.GraphPanel;
import gui.ModernButton;
import model.Graph;
import model.Node;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // İşletim sistemi temasına uyum sağla
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // --- 1. ADIM: DOSYA SEÇME EKRANI ---
        String dosyaYolu = "karmasik_ag.csv"; // Varsayılan dosya
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lütfen Veri Dosyasını (.csv) Seçiniz");
        fileChooser.setCurrentDirectory(new File(".")); // Proje klasörünü aç
        
        int secim = fileChooser.showOpenDialog(null);
        if (secim == JFileChooser.APPROVE_OPTION) {
            dosyaYolu = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            System.out.println("Dosya seçilmedi, varsayılan 'veriler.csv' aranıyor...");
        }

        // --- 2. ADIM: VERİ YÜKLEME ---
        GraphLoader loader = new GraphLoader();
        Graph graph = loader.loadGraph(dosyaYolu);

        // Eğer dosya boşsa veya bulunamadıysa
        if (graph.getNodes().isEmpty()) {
            JOptionPane.showMessageDialog(null, "HATA: Dosya bulunamadı veya içi boş! Lütfen 'veriler.csv' dosyasını kontrol edin.", "Hata", JOptionPane.ERROR_MESSAGE);
            // Programın çalışmaya devam etmesi için boş bir grafikle devam edebiliriz veya kapatabiliriz.
            // Biz boş grafikle devam edelim, belki kullanıcı sağ tıkla yeni ekleme yapar.
        }

        // Düğümlere Rastgele Konum Ata
        Random random = new Random();
        for (Node node : graph.getNodes()) {
            node.setX(100 + random.nextInt(800)); 
            node.setY(50 + random.nextInt(550));  
        }

        // --- 3. ADIM: ANA PENCERE TASARIMI ---
        JFrame frame = new JFrame("Sosyal Ağ Analizi Projesi (Final) | Rıdvan Elen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1250, 850);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(45, 45, 60)); // Koyu tema

        GraphPanel graphPanel = new GraphPanel(graph);
        frame.add(graphPanel, BorderLayout.CENTER);

        // --- 4. ADIM: KONTROL PANELİ ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        controlPanel.setBackground(new Color(45, 45, 60)); 
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        // Modern Butonlar
        ModernButton btnBFS = new ModernButton("BFS Erişim", new Color(65, 105, 225));
        ModernButton btnDFS = new ModernButton("DFS Derinlik", new Color(138, 43, 226));
        ModernButton btnDijkstra = new ModernButton("Dijkstra Yol", new Color(220, 20, 60));
        ModernButton btnColoring = new ModernButton("Renklendir", new Color(46, 139, 87));
        ModernButton btnAnalyze = new ModernButton("📊 Analiz Et", new Color(255, 140, 0));
        ModernButton btnSave = new ModernButton("💾 Kaydet (CSV)", new Color(47, 79, 79)); // YENİ BUTON
        ModernButton btnReset = new ModernButton("Temizle", new Color(119, 136, 153));

        Dimension btnSize = new Dimension(135, 45);
        btnBFS.setPreferredSize(btnSize);
        btnDFS.setPreferredSize(btnSize);
        btnDijkstra.setPreferredSize(btnSize);
        btnColoring.setPreferredSize(btnSize);
        btnAnalyze.setPreferredSize(btnSize);
        btnSave.setPreferredSize(btnSize);
        btnReset.setPreferredSize(btnSize);

        controlPanel.add(btnBFS);
        controlPanel.add(btnDFS);
        controlPanel.add(btnDijkstra);
        controlPanel.add(btnColoring);
        controlPanel.add(btnAnalyze);
        controlPanel.add(btnSave); // Panele ekledik
        controlPanel.add(btnReset);
        
        frame.add(controlPanel, BorderLayout.SOUTH);

        // --- 5. ADIM: BUTON AKSİYONLARI ---

        // BFS
        btnBFS.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("BFS Başlangıç ID:");
            Node start = findNode(graph, id);
            if (start != null) {
                BFS bfs = new BFS();
                List<Node> res = bfs.run(graph, start);
                highlightNodes(graphPanel, res, new Color(50, 255, 50)); 
                JOptionPane.showMessageDialog(frame, "BFS Tamamlandı.\nErişilen Kişi Sayısı: " + res.size());
            }
        });

        // DFS
        btnDFS.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("DFS Başlangıç ID:");
            Node start = findNode(graph, id);
            if (start != null) {
                DFS dfs = new DFS();
                List<Node> res = dfs.run(graph, start);
                highlightNodes(graphPanel, res, new Color(255, 0, 255));
            }
        });

        // Dijkstra
        btnDijkstra.addActionListener(e -> {
            String sId = JOptionPane.showInputDialog("Başlangıç ID:");
            String eId = JOptionPane.showInputDialog("Hedef ID:");
            Node s = findNode(graph, sId);
            Node end = findNode(graph, eId);
            
            if (s != null && end != null) {
                Dijkstra dij = new Dijkstra();
                List<Node> path = dij.findShortestPath(graph, s, end);
                if (!path.isEmpty()) {
                    highlightNodes(graphPanel, path, Color.RED);
                } else {
                    JOptionPane.showMessageDialog(frame, "Bu iki kişi arasında bağlantı yolu yok!");
                }
            }
        });

        // Renklendirme
        btnColoring.addActionListener(e -> {
            WelshPowell wp = new WelshPowell();
            Map<Node, Color> colors = wp.colorGraph(graph);
            graphPanel.setNodeColors(colors);
            
            long count = colors.values().stream().distinct().count();
            JOptionPane.showMessageDialog(frame, "Grafik Renklendirildi.\nKullanılan Toplam Renk Sayısı (Kromatik Sayı): " + count);
        });

        // Analiz
        btnAnalyze.addActionListener(e -> {
            Centrality centrality = new Centrality();
            List<Node> topNodes = centrality.getTopNodes(graph, 5); 
            
            String[] columns = {"Sıra", "Kullanıcı ID", "Bağlantı Sayısı", "Merkezilik Skoru"};
            Object[][] data = new Object[topNodes.size()][4];

            for (int i = 0; i < topNodes.size(); i++) {
                Node n = topNodes.get(i);
                int degree = centrality.getDegree(graph, n);
                double score = (graph.getNodes().size() > 1) ? (double)degree / (graph.getNodes().size() - 1) : 0;
                
                data[i][0] = (i + 1);
                data[i][1] = n.getId();
                data[i][2] = degree;
                data[i][3] = String.format("%.4f", score);
            }

            JTable table = new JTable(new DefaultTableModel(data, columns));
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.setRowHeight(30);
            
            JScrollPane scrollPane = new JScrollPane(table);
            JDialog dialog = new JDialog(frame, "🏆 En Popüler 5 Kullanıcı", true);
            dialog.setSize(600, 350);
            dialog.add(scrollPane);
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            
            highlightNodes(graphPanel, topNodes, new Color(255, 215, 0)); 
        });

        // YENİ: KAYDETME BUTONU AKSİYONU
        btnSave.addActionListener(e -> {
            JFileChooser fileChooserSave = new JFileChooser();
            fileChooserSave.setDialogTitle("Kaydedilecek Yeri Seç");
            fileChooserSave.setSelectedFile(new File("guncel_veriler.csv"));
            fileChooserSave.setCurrentDirectory(new File("."));
            
            int userSelection = fileChooserSave.showSaveDialog(frame);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooserSave.getSelectedFile();
                try {
                    GraphSaver saver = new GraphSaver();
                    saver.saveGraph(graph, fileToSave.getAbsolutePath());
                    JOptionPane.showMessageDialog(frame, "✅ Dosya Başarıyla Kaydedildi!\n" + fileToSave.getAbsolutePath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "❌ Kaydetme Hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Reset
        btnReset.addActionListener(e -> graphPanel.resetColors());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void highlightNodes(GraphPanel panel, List<Node> nodes, Color color) {
        Map<Node, Color> map = new HashMap<>();
        for (Node n : nodes) map.put(n, color);
        panel.setNodeColors(map);
    }

    private static Node findNode(Graph graph, String id) {
        if (id == null || id.trim().isEmpty()) return null;
        for (Node n : graph.getNodes()) {
            if (n.getId().equalsIgnoreCase(id)) return n;
        }
        JOptionPane.showMessageDialog(null, "HATA: '" + id + "' ID'li kullanıcı bulunamadı!");
        return null;
    }
}