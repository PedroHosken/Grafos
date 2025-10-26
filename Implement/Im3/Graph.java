package Implement.Im3;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um grafo direcionado e ponderado usando listas de adjacência.
 */
public class Graph {
    int V; // Número de vértices
    List<Edge>[] adj; // Lista de adjacência

    /**
     * Constrói um grafo com um número V de vértices.
     * 
     * @param V O número de vértices (numerados de 0 a V-1).
     */
    @SuppressWarnings("unchecked")
    public Graph(int V) {
        this.V = V;
        adj = (List<Edge>[]) new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<Edge>();
        }
    }

    /**
     * Adiciona uma aresta direcionada ao grafo.
     * 
     * @param from   Vértice de origem.
     * @param to     Vértice de destino.
     * @param weight Peso da aresta.
     */
    public void addEdge(int from, int to, int weight) {
        if (from >= 0 && from < V && to >= 0 && to < V) {
            adj[from].add(new Edge(to, weight));
        } else {
            System.err.println("Vértice inválido: " + from + " ou " + to);
        }
    }
}