package Implement.Im3;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Implementa o algoritmo de Dijkstra modificado para encontrar o caminho
 * mínimo com o menor número de arestas como critério de desempate.
 */
public class ShortestPathFinder {

    private final Graph graph;
    private final long[] dist; // MUDANÇA: de double[] para long[]
    private final int[] numEdges;
    private final int[] parent;

    public ShortestPathFinder(Graph graph) {
        this.graph = graph;
        this.dist = new long[graph.V]; // MUDANÇA: array de long
        this.numEdges = new int[graph.V];
        this.parent = new int[graph.V];
    }

    /**
     * Encontra o caminho mínimo da origem ao destino.
     * 
     * @param source      O vértice de origem.
     * @param destination O vértice de destino.
     * @return Um objeto PathResult com os detalhes do caminho.
     */
    public PathResult findShortestPath(int source, int destination) {
        // 1. Inicialização
        Arrays.fill(dist, Long.MAX_VALUE); // MUDANÇA: Valor "infinito" de long
        Arrays.fill(numEdges, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        dist[source] = 0L; // MUDANÇA: 0 como long
        numEdges[source] = 0;

        PriorityQueue<NodeState> pq = new PriorityQueue<>();
        pq.add(new NodeState(source, 0L, 0)); // MUDANÇA: 0 como long

        // 2. Loop principal do Dijkstra
        while (!pq.isEmpty()) {
            NodeState current = pq.poll();
            int u = current.vertex;

            if (current.distance > dist[u] ||
                    (current.distance == dist[u] && current.edges > numEdges[u])) {
                continue;
            }

            // 3. Relaxamento dos vizinhos
            for (Edge edge : graph.adj[u]) {
                int v = edge.to;
                // 'weight' agora é int, 'dist[u]' é long.
                int weight = edge.weight;

                long newDist = dist[u] + weight; // MUDANÇA: newDist é long
                int newEdges = numEdges[u] + 1;

                // Caso 1: Encontramos um caminho com peso menor.
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    numEdges[v] = newEdges;
                    parent[v] = u;
                    pq.add(new NodeState(v, newDist, newEdges));
                }
                // Caso 2: Encontramos um caminho com o *mesmo* peso (empate).
                else if (newDist == dist[v] && newEdges < numEdges[v]) {
                    numEdges[v] = newEdges;
                    parent[v] = u;
                    pq.add(new NodeState(v, newDist, newEdges));
                }
            }
        }

        // 4. Montar o resultado
        // MUDANÇA: Verifica o "infinito" de long
        if (dist[destination] == Long.MAX_VALUE) {
            return new PathResult();
        } else {
            return new PathResult(dist[destination], numEdges[destination], parent, source, destination);
        }
    }
    
}
