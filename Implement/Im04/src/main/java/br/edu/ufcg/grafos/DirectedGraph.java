package br.edu.ufcg.grafos;

import java.util.*;

/**
 * Representa um grafo direcionado com suporte para fluxo máximo.
 */
public class DirectedGraph {
    private int numVertices;
    private List<List<Edge>> adjacencyList;
    private Map<String, Edge> edgeMap;

    public DirectedGraph(int numVertices) {
        this.numVertices = numVertices;
        this.adjacencyList = new ArrayList<>();
        this.edgeMap = new HashMap<>();
        
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to) {
        addEdge(from, to, 1); // Capacidade padrão é 1 para caminhos disjuntos
    }

    public void addEdge(int from, int to, int capacity) {
        if (from < 0 || from >= numVertices || to < 0 || to >= numVertices) {
            throw new IllegalArgumentException("Vértices inválidos: " + from + " -> " + to);
        }

        String key = from + "->" + to;
        Edge edge = edgeMap.get(key);
        
        if (edge == null) {
            edge = new Edge(from, to, capacity);
            Edge reverse = new Edge(to, from, 0);
            edge.setReverse(reverse);
            reverse.setReverse(edge);
            
            adjacencyList.get(from).add(edge);
            adjacencyList.get(to).add(reverse);
            edgeMap.put(key, edge);
        } else {
            // Se a aresta já existe, aumenta a capacidade
            edge = new Edge(from, to, edge.getCapacity() + capacity);
            Edge reverse = new Edge(to, from, 0);
            edge.setReverse(reverse);
            reverse.setReverse(edge);
            
            adjacencyList.get(from).add(edge);
            adjacencyList.get(to).add(reverse);
        }
    }

    public List<Edge> getEdges(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public List<List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Encontra caminho aumentador usando BFS (Edmonds-Karp).
     */
    public List<Integer> findAugmentingPath(int source, int sink) {
        Queue<Integer> queue = new LinkedList<>();
        int[] parent = new int[numVertices];
        Edge[] parentEdge = new Edge[numVertices];
        Arrays.fill(parent, -1);
        
        queue.offer(source);
        parent[source] = source;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (Edge edge : adjacencyList.get(u)) {
                int v = edge.getTo();
                if (parent[v] == -1 && edge.getResidualCapacity() > 0) {
                    parent[v] = u;
                    parentEdge[v] = edge;
                    queue.offer(v);

                    if (v == sink) {
                        // Reconstruir caminho
                        List<Integer> path = new ArrayList<>();
                        int current = sink;
                        while (current != source) {
                            path.add(current);
                            current = parent[current];
                        }
                        path.add(source);
                        Collections.reverse(path);
                        return path;
                    }
                }
            }
        }

        return null; // Não há caminho aumentador
    }

    /**
     * Calcula o fluxo máximo usando o algoritmo de Edmonds-Karp.
     */
    public int maxFlow(int source, int sink) {
        // Resetar fluxos
        for (List<Edge> edges : adjacencyList) {
            for (Edge edge : edges) {
                edge.setFlow(0);
            }
        }

        int maxFlow = 0;
        
        while (true) {
            // Encontrar caminho aumentador usando BFS
            Queue<Integer> queue = new LinkedList<>();
            int[] parent = new int[numVertices];
            Edge[] parentEdge = new Edge[numVertices];
            Arrays.fill(parent, -1);
            
            queue.offer(source);
            parent[source] = source;

            while (!queue.isEmpty()) {
                int u = queue.poll();

                for (Edge edge : adjacencyList.get(u)) {
                    int v = edge.getTo();
                    if (parent[v] == -1 && edge.getResidualCapacity() > 0) {
                        parent[v] = u;
                        parentEdge[v] = edge;
                        queue.offer(v);

                        if (v == sink) {
                            // Encontrar capacidade mínima no caminho
                            int minCapacity = Integer.MAX_VALUE;
                            int current = sink;
                            
                            while (current != source) {
                                Edge e = parentEdge[current];
                                minCapacity = Math.min(minCapacity, e.getResidualCapacity());
                                current = parent[current];
                            }

                            // Aumentar fluxo ao longo do caminho
                            current = sink;
                            while (current != source) {
                                Edge e = parentEdge[current];
                                e.addFlow(minCapacity);
                                current = parent[current];
                            }

                            maxFlow += minCapacity;
                            break;
                        }
                    }
                }
                
                if (parent[sink] != -1) {
                    break;
                }
            }
            
            if (parent[sink] == -1) {
                break; // Não há mais caminhos aumentadores
            }
        }

        return maxFlow;
    }

    /**
     * Encontra todos os caminhos disjuntos em arestas entre source e sink.
     */
    public List<List<Integer>> findEdgeDisjointPaths(int source, int sink) {
        // Primeiro, calcular fluxo máximo
        maxFlow(source, sink);

        // Agora, extrair os caminhos do fluxo
        List<List<Integer>> paths = new ArrayList<>();
        DirectedGraph residualGraph = this; // Usar o grafo residual atual

        // Encontrar caminhos no grafo residual
        while (true) {
            List<Integer> path = findPathInResidualGraph(source, sink);
            if (path == null || path.isEmpty()) {
                break;
            }
            
            // Remover o fluxo deste caminho (marcar arestas como usadas)
            for (int i = 0; i < path.size() - 1; i++) {
                int u = path.get(i);
                int v = path.get(i + 1);
                
                for (Edge edge : adjacencyList.get(u)) {
                    if (edge.getTo() == v && edge.getFlow() > 0) {
                        edge.addFlow(-1); // Reduzir fluxo em 1
                        break;
                    }
                }
            }
            
            paths.add(path);
        }

        return paths;
    }

    /**
     * Encontra um caminho no grafo residual usando DFS.
     */
    private List<Integer> findPathInResidualGraph(int source, int sink) {
        boolean[] visited = new boolean[numVertices];
        List<Integer> path = new ArrayList<>();
        
        if (dfsPath(source, sink, visited, path)) {
            return path;
        }
        
        return null;
    }

    private boolean dfsPath(int u, int sink, boolean[] visited, List<Integer> path) {
        visited[u] = true;
        path.add(u);

        if (u == sink) {
            return true;
        }

        for (Edge edge : adjacencyList.get(u)) {
            int v = edge.getTo();
            if (!visited[v] && edge.getFlow() > 0) {
                if (dfsPath(v, sink, visited, path)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1);
        return false;
    }
}

