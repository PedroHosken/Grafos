package tps.tp02;

/**
 * Representa um grafo completo com distâncias entre todos os pares de vértices.
 * Usado para o problema dos k-centros.
 */
public class CompleteGraph {
    private int numVertices;
    private double[][] distances; // Matriz de distâncias: distances[i][j] = distância de i para j

    /**
     * Constrói um grafo completo com n vértices.
     * 
     * @param n Número de vértices
     */
    public CompleteGraph(int n) {
        this.numVertices = n;
        this.distances = new double[n][n];
    }

    /**
     * Define a distância entre dois vértices.
     * 
     * @param i Vértice origem (0-indexed)
     * @param j Vértice destino (0-indexed)
     * @param distance Distância entre i e j
     */
    public void setDistance(int i, int j, double distance) {
        if (i >= 0 && i < numVertices && j >= 0 && j < numVertices) {
            distances[i][j] = distance;
        }
    }

    /**
     * Obtém a distância entre dois vértices.
     * 
     * @param i Vértice origem
     * @param j Vértice destino
     * @return Distância entre i e j
     */
    public double getDistance(int i, int j) {
        if (i >= 0 && i < numVertices && j >= 0 && j < numVertices) {
            return distances[i][j];
        }
        return Double.MAX_VALUE;
    }

    /**
     * Retorna o número de vértices do grafo.
     */
    public int getNumVertices() {
        return numVertices;
    }

    /**
     * Calcula a distância de um vértice ao conjunto de centros mais próximo.
     * 
     * @param vertex Vértice para o qual calcular a distância
     * @param centers Conjunto de centros (índices dos vértices)
     * @return Distância mínima do vértice a qualquer centro
     */
    public double distanceToCenters(int vertex, int[] centers) {
        double minDist = Double.MAX_VALUE;
        for (int center : centers) {
            double dist = getDistance(vertex, center);
            if (dist < minDist) {
                minDist = dist;
            }
        }
        return minDist;
    }

    /**
     * Calcula o raio de uma solução (maior distância de qualquer vértice ao centro mais próximo).
     * 
     * @param centers Conjunto de centros
     * @return Raio da solução
     */
    public double calculateRadius(int[] centers) {
        double maxRadius = 0.0;
        for (int v = 0; v < numVertices; v++) {
            double dist = distanceToCenters(v, centers);
            if (dist > maxRadius) {
                maxRadius = dist;
            }
        }
        return maxRadius;
    }
}

