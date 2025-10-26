package Implement.Im3;

/**
 * Representa uma aresta direcionada e ponderada no grafo.
 * (Pesos são inteiros)
 */
public class Edge {
    int to;
    int weight; // MUDANÇA: de double para int

    /**
     * Construtor de uma aresta.
     * 
     * @param to     Vértice de destino.
     * @param weight Peso da aresta (inteiro).
     */
    public Edge(int to, int weight) { // MUDANÇA: weight agora é int
        this.to = to;
        this.weight = weight;
    }
}
