package Implement.Im03;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Solução para a tarefa de encontrar o caminho mínimo com o menor número de
 * arestas
 * em um grafo direcionado e ponderado (pesos positivos).
 * * Implementação por: Cherry (para Pedro)
 */
public class ShortestPathTask {

    // --- Classes Internas para Estrutura do Grafo ---

    /**
     * Representa uma aresta direcionada e ponderada.
     */
    static class Edge {
        int to;
        int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Representa o grafo usando listas de adjacência.
     */
    static class Graph {
        private final int V; // Número de vértices
        private final List<List<Edge>> adj;

        public Graph(int V) {
            this.V = V;
            adj = new ArrayList<>(V);
            for (int i = 0; i < V; i++) {
                adj.add(new ArrayList<>());
            }
        }

        /**
         * Adiciona uma aresta direcionada de 'from' para 'to' com peso 'weight'.
         */
        public void addEdge(int from, int to, int weight) {
            // Validação para garantir que os vértices estão dentro do limite
            if (from >= 0 && from < V && to >= 0 && to < V && weight > 0) {
                adj.get(from).add(new Edge(to, weight));
            }
        }

        public int getV() {
            return V;
        }

        public List<Edge> getAdj(int v) {
            return adj.get(v);
        }
    }

    // --- Classes para o Algoritmo de Dijkstra Modificado ---

    /**
     * Representa o estado de um nó na fila de prioridade.
     * Implementa 'Comparable' para ordenar primeiro por 'weight' (menor)
     * e depois por 'edges' (menor) como critério de desempate.
     */
    static class Node implements Comparable<Node> {
        int vertex;
        long weight; // Usar 'long' para pesos evita overflow em caminhos longos
        int edges;

        public Node(int vertex, long weight, int edges) {
            this.vertex = vertex;
            this.weight = weight;
            this.edges = edges;
        }

        @Override
        public int compareTo(Node other) {
            // Critério 1: Peso total (menor primeiro)
            if (this.weight < other.weight)
                return -1;
            if (this.weight > other.weight)
                return 1;

            // Critério 2 (Desempate): Número de arestas (menor primeiro)
            if (this.edges < other.edges)
                return -1;
            if (this.edges > other.edges)
                return 1;

            return 0;
        }
    }

    /**
     * Encapsula o resultado final da busca.
     */
    static class PathResult {
        boolean found;
        long totalWeight;
        int totalEdges;
        List<Integer> path;

        // Construtor para caminho encontrado
        public PathResult(boolean found, long totalWeight, int totalEdges, List<Integer> path) {
            this.found = found;
            this.totalWeight = totalWeight;
            this.totalEdges = totalEdges;
            this.path = path;
        }

        // Construtor para caminho não encontrado
        public PathResult(boolean found) {
            this.found = found;
            this.totalWeight = -1;
            this.totalEdges = -1;
            this.path = Collections.emptyList();
        }
    }

    // --- Lógica Principal do Algoritmo ---

    /**
     * Encontra o caminho mínimo (menor peso, depois menor número de arestas)
     * da origem 'source' ao destino 'dest'.
     *
     * @param graph  O grafo.
     * @param source Vértice de origem.
     * @param dest   Vértice de destino.
     * @return Um objeto PathResult contendo os detalhes do caminho.
     */
    public static PathResult findShortestPath(Graph graph, int source, int dest) {
        int V = graph.getV();

        long[] minWeight = new long[V]; // Armazena o menor peso até o vértice
        int[] minEdges = new int[V]; // Armazena o n° de arestas para o menor peso
        int[] parent = new int[V]; // Para reconstruir o caminho

        // Inicializa as distâncias
        Arrays.fill(minWeight, Long.MAX_VALUE);
        Arrays.fill(minEdges, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        // Ponto de partida
        minWeight[source] = 0;
        minEdges[source] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(source, 0, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.vertex;
            long w = current.weight;
            int e = current.edges;

            // Otimização: Se já encontramos um caminho melhor (ou igual com menos arestas)
            // para 'u', podemos ignorar esta entrada da fila (que é antiga).
            if (w > minWeight[u] || (w == minWeight[u] && e > minEdges[u])) {
                continue;
            }

            // Se o nó extraído é o destino, encontramos o caminho ótimo.
            // (Isso é verdade em Dijkstra. Assim que o destino é extraído,
            // seu caminho é final.)
            if (u == dest) {
                break; // Otimização: podemos parar aqui.
            }

            // Explora os vizinhos
            for (Edge edge : graph.getAdj(u)) {
                int v = edge.to;
                int edgeWeight = edge.weight;

                long newWeight = w + edgeWeight;
                int newEdges = e + 1;

                // Caso 1: Encontramos um caminho com peso estritamente menor.
                if (newWeight < minWeight[v]) {
                    minWeight[v] = newWeight;
                    minEdges[v] = newEdges;
                    parent[v] = u;
                    pq.add(new Node(v, newWeight, newEdges));

                    // Caso 2: Encontramos um caminho com o *mesmo* peso, mas *menos* arestas.
                } else if (newWeight == minWeight[v] && newEdges < minEdges[v]) {
                    minEdges[v] = newEdges;
                    parent[v] = u;
                    pq.add(new Node(v, newWeight, newEdges));
                }
            }
        }

        // --- Fim do Algoritmo ---

        // Se o peso do destino ainda é MAX_VALUE, não há caminho.
        if (minWeight[dest] == Long.MAX_VALUE) {
            return new PathResult(false);
        }

        // Reconstruir o caminho
        List<Integer> path = new ArrayList<>();
        int curr = dest;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path); // O caminho é construído do fim para o começo

        return new PathResult(true, minWeight[dest], minEdges[dest], path);
    }

    // --- Método Main para Execução ---

    /**
     * Método principal para ler o arquivo e executar o algoritmo.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java ShortestPathTask <arquivo_do_grafo>");
            System.out.println("\nFormato esperado do arquivo:");
            System.out.println("V E         (Ex: 5 7)");
            System.out.println("origem destino (Ex: 0 4)");
            System.out.println("u1 v1 w1    (Ex: 0 1 10)");
            System.out.println("u2 v2 w2    (Ex: 1 2 5)");
            System.out.println("...");
            return;
        }

        String filename = args[0];
        try (Scanner scanner = new Scanner(new File(filename))) {
            int V = scanner.nextInt();
            int E = scanner.nextInt();
            int source = scanner.nextInt();
            int dest = scanner.nextInt();

            if (V <= 0) {
                System.err.println("Erro: O número de vértices (V) deve ser positivo.");
                return;
            }
            if (source < 0 || source >= V || dest < 0 || dest >= V) {
                System.err.println("Erro: Vértices de origem ou destino fora do intervalo [0, V-1].");
                return;
            }

            Graph graph = new Graph(V);
            for (int i = 0; i < E; i++) {
                if (!scanner.hasNextInt())
                    break; // Evita erro se o arquivo terminar
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                int w = scanner.nextInt();
                graph.addEdge(u, v, w);
            }

            // --- Medir Eficiência ---
            long startTime = System.nanoTime();
            PathResult result = findShortestPath(graph, source, dest);
            long endTime = System.nanoTime();
            // Tempo em microssegundos (mais preciso para execuções rápidas)
            long durationMicroseconds = (endTime - startTime) / 1000;

            // --- Exibir Resultados ---
            System.out.println("=========================================");
            System.out.println("    Resultados da Busca de Caminho Mínimo");
            System.out.println("=========================================");
            System.out.println("Arquivo de Entrada: " + filename);
            System.out.println("Vértices (V): " + V + ", Arestas (E): " + E);
            System.out.println("Rota Solicitada: " + source + " -> " + dest);
            System.out.println("-----------------------------------------");

            if (result.found) {
                System.out.println("Status: Caminho Encontrado!");
                System.out.println("Comprimento (Peso Total): " + result.totalWeight);
                System.out.println("Número de Arestas: " + result.totalEdges);
                System.out.println("Caminho (Vértices): " + result.path);
            } else {
                System.out.println("Status: Nenhum caminho encontrado da origem ao destino.");
            }

            System.out.println("-----------------------------------------");
            System.out.println("Eficiência (Tempo de Execução): " + durationMicroseconds + " µs (microssegundos)");
            System.out.println("=========================================");

        } catch (FileNotFoundException e) {
            System.err.println("Erro: Arquivo não encontrado: " + filename);
        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}