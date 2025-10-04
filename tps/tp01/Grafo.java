package tps.tp01;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um grafo simples não-direcionado usando uma lista de adjacência.
 * A classe fornece funcionalidades básicas para manipular o grafo, como
 * adicionar e remover arestas, além de obter informações sobre seus vértices e
 * arestas.
 */
public class Grafo {

    // Número de vértices no grafo.
    private final int V;

    // Lista de adjacência. É um array de listas, onde cada lista armazena os
    // vizinhos de um vértice.
    private List<Integer>[] adj;

    /**
     * Construtor da classe Grafo.
     *
     * @param V O número total de vértices no grafo. Os vértices são rotulados de 0
     *          a V-1.
     */
    @SuppressWarnings("unchecked")
    public Grafo(int V) {
        if (V < 0) {
            throw new IllegalArgumentException("O número de vértices não pode ser negativo.");
        }
        this.V = V;
        // Inicializa a lista de adjacência para cada vértice.
        adj = (List<Integer>[]) new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    /**
     * Adiciona uma aresta não-direcionada entre os vértices v e w.
     * Como o grafo é não-direcionado, a conexão é mútua.
     *
     * @param v O primeiro vértice da aresta.
     * @param w O segundo vértice da aresta.
     */
    public void adicionarAresta(int v, int w) {
        // Adiciona w à lista de adjacência de v.
        adj[v].add(w);
        // Adiciona v à lista de adjacência de w.
        adj[w].add(v);
    }

    /**
     * Remove uma aresta não-direcionada entre os vértices v e w.
     * A remoção também é mútua.
     *
     * @param v O primeiro vértice da aresta.
     * @param w O segundo vértice da aresta.
     */
    public void removerAresta(int v, int w) {
        // Remove a primeira ocorrência de w da lista de v.
        adj[v].remove(Integer.valueOf(w));
        // Remove a primeira ocorrência de v da lista de w.
        adj[w].remove(Integer.valueOf(w));
    }

    /**
     * Retorna o número de vértices no grafo.
     *
     * @return O número de vértices (V).
     */
    public int getV() {
        return V;
    }

    /**
     * Retorna a lista de adjacência do grafo.
     *
     * @return Um array de listas representando as conexões de cada vértice.
     */
    public List<Integer>[] getAdj() {
        return adj;
    }

    /**
     * Cria uma cópia exata deste grafo.
     * Útil para algoritmos que modificam a estrutura do grafo, como o de Fleury.
     *
     * @return Um novo objeto Grafo que é uma cópia do atual.
     */
    public Grafo copiar() {
        Grafo copia = new Grafo(this.V);
        for (int v = 0; v < this.V; v++) {
            for (int vizinho : this.adj[v]) {
                // Adiciona a aresta apenas uma vez para evitar duplicação em grafos não
                // direcionados.
                if (v < vizinho) {
                    copia.adicionarAresta(v, vizinho);
                }
            }
        }
        return copia;
    }
}