package fileio;

import model.Graph;
import model.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class GraphLoader {

    public Graph loadGraph(String filePath) {
        Graph graph = new Graph();
        Map<String, Node> nodeMap = new HashMap<>(); // ID ile Düğümleri eşleştirmek için geçici hafıza
        
        String line = "";
        String splitBy = ";";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // İlk satır başlık olduğu için okuyup atlıyoruz
            br.readLine(); 

            // 1. ADIM: Önce tüm Düğümleri (Nodes) oluştur
            // Dosyayı baştan sona oku ama henüz bağlantıları kurma
            while ((line = br.readLine()) != null) {
                String[] data = line.split(splitBy);
                
                String id = data[0];
                double aktiflik = Double.parseDouble(data[1]);
                int etkilesim = Integer.parseInt(data[2]);
                int baglantiSayisi = Integer.parseInt(data[3]); // CSV'deki hazır sayı (Görsel için)

                Node node = new Node(id, aktiflik, etkilesim, baglantiSayisi);
                graph.addNode(node);
                nodeMap.put(id, node);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. ADIM: Dosyayı TEKRAR oku ve bu sefer Bağlantıları (Edges) kur
        // Bu sefer ağırlık hesaplayacağız
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Başlığı atla

            while ((line = br.readLine()) != null) {
                String[] data = line.split(splitBy);
                String sourceId = data[0];
                
                // Eğer komşular hücresi boş değilse
                if (data.length > 4) {
                    String[] neighbors = data[4].split(",");
                    
                    Node sourceNode = nodeMap.get(sourceId);

                    for (String destId : neighbors) {
                        destId = destId.trim(); // Boşlukları temizle
                        Node destNode = nodeMap.get(destId);

                        if (destNode != null) {
                            // Çift yönlü kontrolü: Zaten eklenmişse tekrar ekleme
                            if (!graph.hasEdge(sourceNode, destNode)) {
                                
                                // --- HATA ÇÖZÜMÜ BURADA ---
                                // Ağırlığı formüle göre hesapla ve öyle ekle
                                double weight = calculateWeight(sourceNode, destNode);
                                graph.addEdge(sourceNode, destNode, weight);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return graph;
    }

    // Proje İsteri 4.3'teki Formül
    private double calculateWeight(Node n1, Node n2) {
        double diffAktiflik = Math.abs(n1.getAktiflik() - n2.getAktiflik());
        double diffEtkilesim = Math.abs(n1.getEtkilesim() - n2.getEtkilesim());
        double diffBaglanti = Math.abs(n1.getBaglantiSayisi() - n2.getBaglantiSayisi());
        
        // Formül: 1 / ( (1+DiffAktif) * (2+DiffEtkilesim) * (2+DiffBaglanti) )
        double payda = (1 + diffAktiflik) * (2 + diffEtkilesim) * (2 + diffBaglanti);
        
        return 1.0 / payda;
    }
}