package br.edu.ufcg.grafos;

/**
 * Representa uma aresta direcionada em um grafo com capacidade.
 */
public class Edge {
    private int from;
    private int to;
    private int capacity;
    private int flow;
    private Edge reverse;

    public Edge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public int getResidualCapacity() {
        return capacity - flow;
    }

    public Edge getReverse() {
        return reverse;
    }

    public void setReverse(Edge reverse) {
        this.reverse = reverse;
    }

    public void addFlow(int amount) {
        this.flow += amount;
        if (reverse != null) {
            reverse.flow -= amount;
        }
    }
}

