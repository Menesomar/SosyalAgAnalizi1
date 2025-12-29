package fileio;

import model.Edge;
import model.Graph;
import model.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphSaver {

    public void saveGraph(Graph graph, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        
        // Başlık
        writer.write("Id;Aktiflik;Etkilesim;BaglantiSayisi;Komsular\n");

        for (Node node : graph.getNodes()) {
            List<String> neighborIds = new ArrayList<>();
            int connectionCount = 0; 
            
            for (Edge edge : graph.getEdges()) {
                if (edge.getSource() == node) {
                    neighborIds.add(edge.getDestination().getId());
                    connectionCount++;
                } else if (edge.getDestination() == node) {
                    neighborIds.add(edge.getSource().getId());
                    connectionCount++;
                }
            }

            String komsular = String.join(",", neighborIds);
            
            // Veriyi formata uygun yaz
            String line = String.format("%s;%.1f;%d;%d;%s\n",
                    node.getId(),
                    node.getAktiflik(),
                    node.getEtkilesim(),
                    connectionCount,
                    komsular);
            
            // Virgül/Nokta format hatasını önle
            writer.write(line.replace(',', ',')); 
        }
        
        writer.close();
    }
}