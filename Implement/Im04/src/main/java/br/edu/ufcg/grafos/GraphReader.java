package br.edu.ufcg.grafos;

import java.io.*;
import java.util.*;

/**
 * Classe para ler grafos de arquivos.
 * Formato esperado:
 * - Primeira linha: número de vértices
 * - Linhas seguintes: arestas no formato "origem destino"
 */
public class GraphReader {
    
    public static DirectedGraph readGraph(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        String firstLine = reader.readLine();
        if (firstLine == null) {
            throw new IOException("Arquivo vazio");
        }
        
        int numVertices = Integer.parseInt(firstLine.trim());
        DirectedGraph graph = new DirectedGraph(numVertices);
        
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; // Ignorar linhas vazias e comentários
            }
            
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                graph.addEdge(from, to);
            }
        }
        
        reader.close();
        return graph;
    }
    
    public static void writeGraph(String filename, DirectedGraph graph) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        
        writer.println(graph.getNumVertices());
        
        for (int i = 0; i < graph.getNumVertices(); i++) {
            for (Edge edge : graph.getEdges(i)) {
                if (edge.getCapacity() > 0 && edge.getReverse() != null && 
                    edge.getReverse().getCapacity() == 0) {
                    writer.println(edge.getFrom() + " " + edge.getTo());
                }
            }
        }
        
        writer.close();
    }
}

