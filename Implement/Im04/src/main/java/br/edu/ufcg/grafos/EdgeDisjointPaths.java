package br.edu.ufcg.grafos;

import java.util.*;

/**
 * Classe principal para encontrar caminhos disjuntos em arestas.
 */
public class EdgeDisjointPaths {
    
    public static class Result {
        private int numPaths;
        private List<List<Integer>> paths;
        private long executionTime;
        
        public Result(int numPaths, List<List<Integer>> paths, long executionTime) {
            this.numPaths = numPaths;
            this.paths = paths;
            this.executionTime = executionTime;
        }
        
        public int getNumPaths() {
            return numPaths;
        }
        
        public List<List<Integer>> getPaths() {
            return paths;
        }
        
        public long getExecutionTime() {
            return executionTime;
        }
    }
    
    /**
     * Encontra todos os caminhos disjuntos em arestas entre source e sink.
     */
    public static Result findEdgeDisjointPaths(DirectedGraph graph, int source, int sink) {
        long startTime = System.nanoTime();
        
        // Criar cópia do grafo para não modificar o original
        DirectedGraph graphCopy = copyGraph(graph);
        
        // Calcular fluxo máximo
        int maxFlow = graphCopy.maxFlow(source, sink);
        
        // Extrair caminhos
        List<List<Integer>> paths = extractPaths(graphCopy, source, sink, maxFlow);
        
        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000; // Converter para milissegundos
        
        return new Result(paths.size(), paths, executionTime);
    }
    
    /**
     * Cria uma cópia do grafo.
     */
    private static DirectedGraph copyGraph(DirectedGraph original) {
        DirectedGraph copy = new DirectedGraph(original.getNumVertices());
        
        for (int i = 0; i < original.getNumVertices(); i++) {
            for (Edge edge : original.getEdges(i)) {
                if (edge.getCapacity() > 0 && edge.getReverse() != null && 
                    edge.getReverse().getCapacity() == 0) {
                    copy.addEdge(edge.getFrom(), edge.getTo(), edge.getCapacity());
                }
            }
        }
        
        return copy;
    }
    
    /**
     * Extrai os caminhos do grafo residual após calcular o fluxo máximo.
     */
    private static List<List<Integer>> extractPaths(DirectedGraph graph, int source, int sink, int maxFlow) {
        List<List<Integer>> paths = new ArrayList<>();
        
        // Encontrar caminhos usando DFS no grafo residual
        // O número máximo de caminhos é igual ao fluxo máximo
        for (int i = 0; i < maxFlow; i++) {
            List<Integer> path = findPathDFS(graph, source, sink);
            if (path == null || path.isEmpty()) {
                break;
            }
            
            // Remover fluxo deste caminho (marcar arestas como usadas)
            for (int j = 0; j < path.size() - 1; j++) {
                int u = path.get(j);
                int v = path.get(j + 1);
                
                // Encontrar a aresta forward e reduzir seu fluxo
                for (Edge edge : graph.getEdges(u)) {
                    if (edge.getTo() == v && edge.getFlow() > 0 && edge.getCapacity() > 0) {
                        edge.addFlow(-1);
                        break;
                    }
                }
            }
            
            paths.add(new ArrayList<>(path)); // Adicionar cópia do caminho
        }
        
        return paths;
    }
    
    /**
     * Encontra um caminho de source para sink usando DFS no grafo residual.
     */
    private static List<Integer> findPathDFS(DirectedGraph graph, int source, int sink) {
        boolean[] visited = new boolean[graph.getNumVertices()];
        List<Integer> path = new ArrayList<>();
        
        if (dfs(graph, source, sink, visited, path)) {
            return path;
        }
        
        return null;
    }
    
    private static boolean dfs(DirectedGraph graph, int u, int sink, boolean[] visited, List<Integer> path) {
        visited[u] = true;
        path.add(u);
        
        if (u == sink) {
            return true;
        }
        
        for (Edge edge : graph.getEdges(u)) {
            int v = edge.getTo();
            if (!visited[v] && edge.getFlow() > 0) {
                if (dfs(graph, v, sink, visited, path)) {
                    return true;
                }
            }
        }
        
        path.remove(path.size() - 1);
        visited[u] = false;
        return false;
    }
    
    /**
     * Exibe os resultados formatados.
     */
    public static void displayResults(Result result, int source, int sink) {
        System.out.println("========================================");
        System.out.println("Caminhos Disjuntos em Arestas");
        System.out.println("========================================");
        System.out.println("Origem: " + source);
        System.out.println("Destino: " + sink);
        System.out.println("Quantidade de caminhos disjuntos: " + result.getNumPaths());
        System.out.println("Tempo de execução: " + result.getExecutionTime() + " ms");
        System.out.println();
        
        if (result.getPaths().isEmpty()) {
            System.out.println("Nenhum caminho encontrado.");
        } else {
            System.out.println("Caminhos encontrados:");
            for (int i = 0; i < result.getPaths().size(); i++) {
                List<Integer> path = result.getPaths().get(i);
                System.out.print("Caminho " + (i + 1) + ": ");
                for (int j = 0; j < path.size(); j++) {
                    System.out.print(path.get(j));
                    if (j < path.size() - 1) {
                        System.out.print(" -> ");
                    }
                }
                System.out.println();
            }
        }
        System.out.println("========================================");
    }
}

