package Implement.Im3;

/**
 * Representa o estado de um nó na fila de prioridade do Dijkstra.
 * Implementa Comparable para priorizar:
 * 1. Menor distância (peso total).
 * 2. (Desempate) Menor número de arestas.
 */
public class NodeState implements Comparable<NodeState> {
    int vertex;
    long distance; // MUDANÇA: de double para long
    int edges;

    public NodeState(int vertex, long distance, int edges) { // MUDANÇA: distance agora é long
        this.vertex = vertex;
        this.distance = distance;
        this.edges = edges;
    }

    @Override
    public int compareTo(NodeState other) {
        // 1. Prioriza pela menor distância
        if (this.distance < other.distance) {
            return -1;
        }
        if (this.distance > other.distance) {
            return 1;
        }

        // 2. Distâncias são iguais, usa o desempate: menor número de arestas
        if (this.edges < other.edges) {
            return -1;
        }
        if (this.edges > other.edges) {
            return 1;
        }

        // São idênticos em distância e arestas
        return 0;
    }
}