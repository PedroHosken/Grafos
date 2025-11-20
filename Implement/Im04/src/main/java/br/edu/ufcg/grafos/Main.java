package br.edu.ufcg.grafos;

import java.io.IOException;

/**
 * Classe principal para executar o programa.
 * Executa automaticamente para graph1.txt e graph2.txt
 */
public class Main {
    
    private static final String GRAPH1_PATH = "examples/graph1.txt";
    private static final String GRAPH2_PATH = "examples/graph2.txt";
    
    public static void main(String[] args) {
        // Executar para graph1.txt (6 vértices: 0 a 5)
        System.out.println("=== EXECUTANDO PARA GRAPH1.TXT ===\n");
        executeGraph(GRAPH1_PATH, 0, 5);
        
        System.out.println("\n\n");
        
        // Executar para graph2.txt (5 vértices: 0 a 4)
        System.out.println("=== EXECUTANDO PARA GRAPH2.TXT ===\n");
        executeGraph(GRAPH2_PATH, 0, 4);
    }
    
    private static void executeGraph(String graphFile, int source, int destination) {
        try {
            DirectedGraph graph = GraphReader.readGraph(graphFile);
            EdgeDisjointPaths.Result result = EdgeDisjointPaths.findEdgeDisjointPaths(graph, source, destination);
            EdgeDisjointPaths.displayResults(result, source, destination);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

