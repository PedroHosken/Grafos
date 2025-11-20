package br.edu.ufcg.grafos;

import java.io.IOException;
import java.util.*;

/**
 * Classe para executar testes e coletar resultados.
 */
public class TestRunner {
    
    public static class TestResult {
        private String graphType;
        private int size;
        private int numVertices;
        private int numEdges;
        private int numPaths;
        private long executionTime;
        private String description;
        
        public TestResult(String graphType, int size, int numVertices, int numEdges, 
                         int numPaths, long executionTime, String description) {
            this.graphType = graphType;
            this.size = size;
            this.numVertices = numVertices;
            this.numEdges = numEdges;
            this.numPaths = numPaths;
            this.executionTime = executionTime;
            this.description = description;
        }
        
        public String getGraphType() { return graphType; }
        public int getSize() { return size; }
        public int getNumVertices() { return numVertices; }
        public int getNumEdges() { return numEdges; }
        public int getNumPaths() { return numPaths; }
        public long getExecutionTime() { return executionTime; }
        public String getDescription() { return description; }
    }
    
    /**
     * Executa testes para grafos completos.
     */
    public static List<TestResult> testCompleteGraphs() {
        List<TestResult> results = new ArrayList<>();
        int[] sizes = {5, 10, 15, 20};
        
        for (int size : sizes) {
            DirectedGraph graph = GraphGenerator.generateCompleteGraph(size);
            int numEdges = countEdges(graph);
            
            int source = 0;
            int destination = size - 1;
            
            EdgeDisjointPaths.Result result = EdgeDisjointPaths.findEdgeDisjointPaths(graph, source, destination);
            
            results.add(new TestResult(
                "Grafo Completo",
                size,
                size,
                numEdges,
                result.getNumPaths(),
                result.getExecutionTime(),
                size + " vértices"
            ));
        }
        
        return results;
    }
    
    /**
     * Executa testes para grafos em camadas.
     */
    public static List<TestResult> testLayeredGraphs() {
        List<TestResult> results = new ArrayList<>();
        int[] layers = {3, 4, 5, 6};
        int verticesPerLayer = 5;
        
        for (int numLayers : layers) {
            DirectedGraph graph = GraphGenerator.generateLayeredGraph(numLayers, verticesPerLayer);
            int numVertices = numLayers * verticesPerLayer;
            int numEdges = countEdges(graph);
            
            int source = 0;
            int destination = numVertices - 1;
            
            EdgeDisjointPaths.Result result = EdgeDisjointPaths.findEdgeDisjointPaths(graph, source, destination);
            
            results.add(new TestResult(
                "Grafo em Camadas",
                numLayers * verticesPerLayer,
                numVertices,
                numEdges,
                result.getNumPaths(),
                result.getExecutionTime(),
                numLayers + " camadas x " + verticesPerLayer + " vértices"
            ));
        }
        
        return results;
    }
    
    /**
     * Executa testes para grafos em grade.
     */
    public static List<TestResult> testGridGraphs() {
        List<TestResult> results = new ArrayList<>();
        int[][] gridSizes = {{3, 3}, {4, 4}, {5, 5}, {6, 6}};
        
        for (int[] grid : gridSizes) {
            int rows = grid[0];
            int cols = grid[1];
            DirectedGraph graph = GraphGenerator.generateGridGraph(rows, cols);
            int numVertices = rows * cols;
            int numEdges = countEdges(graph);
            
            int source = 0;
            int destination = numVertices - 1;
            
            EdgeDisjointPaths.Result result = EdgeDisjointPaths.findEdgeDisjointPaths(graph, source, destination);
            
            results.add(new TestResult(
                "Grafo em Grade",
                numVertices,
                numVertices,
                numEdges,
                result.getNumPaths(),
                result.getExecutionTime(),
                rows + "x" + cols + " grade"
            ));
        }
        
        return results;
    }
    
    /**
     * Conta o número de arestas no grafo.
     */
    private static int countEdges(DirectedGraph graph) {
        int count = 0;
        for (int i = 0; i < graph.getNumVertices(); i++) {
            for (Edge edge : graph.getEdges(i)) {
                if (edge.getCapacity() > 0 && edge.getReverse() != null && 
                    edge.getReverse().getCapacity() == 0) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Executa todos os testes.
     */
    public static List<TestResult> runAllTests() {
        List<TestResult> allResults = new ArrayList<>();
        
        System.out.println("Executando testes para Grafos Completos...");
        allResults.addAll(testCompleteGraphs());
        
        System.out.println("Executando testes para Grafos em Camadas...");
        allResults.addAll(testLayeredGraphs());
        
        return allResults;
    }
    
    /**
     * Exibe resultados em formato de tabela.
     */
    public static void displayResults(List<TestResult> results) {
        System.out.println("\n========================================");
        System.out.println("RESULTADOS DOS TESTES");
        System.out.println("========================================");
        
        Map<String, List<TestResult>> grouped = new HashMap<>();
        for (TestResult result : results) {
            grouped.computeIfAbsent(result.getGraphType(), k -> new ArrayList<>()).add(result);
        }
        
        for (Map.Entry<String, List<TestResult>> entry : grouped.entrySet()) {
            System.out.println("\n" + entry.getKey() + ":");
            System.out.println("Tamanho | Vértices | Arestas | Caminhos | Tempo (ms)");
            System.out.println("--------|----------|---------|----------|-----------");
            
            for (TestResult result : entry.getValue()) {
                System.out.printf("%7d | %8d | %7d | %8d | %10d%n",
                    result.getSize(),
                    result.getNumVertices(),
                    result.getNumEdges(),
                    result.getNumPaths(),
                    result.getExecutionTime());
            }
        }
    }
}

