package br.edu.ufcg.grafos;

import java.io.IOException;
import java.util.Random;

/**
 * Gerador de grafos para testes.
 */
public class GraphGenerator {
    
    private static Random random = new Random();
    
    /**
     * Gera um grafo completo direcionado (todos os vértices conectados a todos).
     */
    public static DirectedGraph generateCompleteGraph(int numVertices) {
        DirectedGraph graph = new DirectedGraph(numVertices);
        
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (i != j) {
                    graph.addEdge(i, j);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Gera um grafo em camadas (layered graph).
     * O grafo é dividido em camadas, onde cada vértice de uma camada
     * está conectado a todos os vértices da próxima camada.
     */
    public static DirectedGraph generateLayeredGraph(int numLayers, int verticesPerLayer) {
        int numVertices = numLayers * verticesPerLayer;
        DirectedGraph graph = new DirectedGraph(numVertices);
        
        for (int layer = 0; layer < numLayers - 1; layer++) {
            for (int i = 0; i < verticesPerLayer; i++) {
                int from = layer * verticesPerLayer + i;
                for (int j = 0; j < verticesPerLayer; j++) {
                    int to = (layer + 1) * verticesPerLayer + j;
                    graph.addEdge(from, to);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Gera um grafo aleatório com densidade controlada.
     */
    public static DirectedGraph generateRandomGraph(int numVertices, double density) {
        DirectedGraph graph = new DirectedGraph(numVertices);
        int maxEdges = numVertices * (numVertices - 1);
        int numEdges = (int) (maxEdges * density);
        
        int edgesAdded = 0;
        while (edgesAdded < numEdges) {
            int from = random.nextInt(numVertices);
            int to = random.nextInt(numVertices);
            
            if (from != to) {
                // Verificar se a aresta já existe seria complexo, então apenas adicionamos
                graph.addEdge(from, to);
                edgesAdded++;
            }
        }
        
        return graph;
    }
    
    /**
     * Gera um grafo em grade (grid graph direcionado).
     */
    public static DirectedGraph generateGridGraph(int rows, int cols) {
        int numVertices = rows * cols;
        DirectedGraph graph = new DirectedGraph(numVertices);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int current = i * cols + j;
                
                // Aresta para direita
                if (j < cols - 1) {
                    graph.addEdge(current, i * cols + (j + 1));
                }
                
                // Aresta para baixo
                if (i < rows - 1) {
                    graph.addEdge(current, (i + 1) * cols + j);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Salva um grafo em arquivo.
     */
    public static void saveGraph(DirectedGraph graph, String filename) throws IOException {
        GraphReader.writeGraph(filename, graph);
    }
}

