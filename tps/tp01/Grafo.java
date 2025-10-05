package tps.tp01;

import java.util.ArrayList;
import java.util.List;

public class Grafo {

    private final int V;
    private List<Integer>[] adj;

    @SuppressWarnings("unchecked")
    public Grafo(int V) {
        this.V = V;
        this.adj = (List<Integer>[]) new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    public void adicionarAresta(int v, int w) {
        adj[v].add(w);
        adj[w].add(v);
    }

    public void removerAresta(int v, int w) {
        adj[v].remove(Integer.valueOf(w));
        adj[w].remove(Integer.valueOf(v)); // Correção importante
    }

    public int getV() {
        return V;
    }

    public List<Integer>[] getAdj() {
        return adj;
    }
    
    public Grafo copiar() {
        Grafo copia = new Grafo(this.V);
        for (int v = 0; v < this.V; v++) {
            for (int vizinho : this.adj[v]) {
                if (v < vizinho) {
                    copia.adicionarAresta(v, vizinho);
                }
            }
        }
        return copia;
    }
}