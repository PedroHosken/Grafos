package tps.tp02;

/**
 * Implementação aproximada do problema dos k-centros usando algoritmo guloso.
 * 
 * Este algoritmo fornece uma 2-aproximação, ou seja, o raio encontrado é no máximo
 * 2 vezes o raio ótimo.
 * 
 * Algoritmo:
 * 1. Escolhe o primeiro centro arbitrariamente (ou o vértice 0)
 * 2. Para cada centro adicional (até k):
 *    - Escolhe o vértice que está mais longe de todos os centros já escolhidos
 * 
 * Complexidade: O(n * k)
 */
public class ApproximateKCenters {

    private CompleteGraph graph;
    private int k;
    private int[] centers;
    private double radius;

    /**
     * Constrói um resolvedor aproximado para o problema dos k-centros.
     * 
     * @param graph Grafo completo
     * @param k Número de centros a encontrar
     */
    public ApproximateKCenters(CompleteGraph graph, int k) {
        this.graph = graph;
        this.k = k;
    }

    /**
     * Resolve o problema dos k-centros de forma aproximada usando algoritmo guloso.
     * 
     * @return Array com os índices dos k centros encontrados
     */
    public int[] solve() {
        int n = graph.getNumVertices();
        
        if (k >= n) {
            // Se k >= n, todos os vértices são centros
            centers = new int[n];
            for (int i = 0; i < n; i++) {
                centers[i] = i;
            }
            radius = 0.0;
            return centers;
        }

        centers = new int[k];
        boolean[] isCenter = new boolean[n];
        
        // Passo 1: Escolher o primeiro centro arbitrariamente (vértice 0)
        centers[0] = 0;
        isCenter[0] = true;

        // Passo 2: Para cada centro adicional (1 até k-1)
        for (int i = 1; i < k; i++) {
            double maxMinDist = -1.0;
            int bestVertex = -1;

            // Para cada vértice que ainda não é centro
            for (int v = 0; v < n; v++) {
                if (isCenter[v]) continue;

                // Calcular a distância mínima deste vértice aos centros já escolhidos
                double minDistToCenters = Double.MAX_VALUE;
                for (int j = 0; j < i; j++) {
                    double dist = graph.getDistance(v, centers[j]);
                    if (dist < minDistToCenters) {
                        minDistToCenters = dist;
                    }
                }

                // Escolher o vértice com maior distância mínima aos centros
                if (minDistToCenters > maxMinDist) {
                    maxMinDist = minDistToCenters;
                    bestVertex = v;
                }
            }

            // Adicionar o vértice escolhido como novo centro
            centers[i] = bestVertex;
            isCenter[bestVertex] = true;
        }

        // Calcular o raio da solução encontrada
        radius = graph.calculateRadius(centers);

        return centers;
    }

    /**
     * Retorna o raio da solução encontrada.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Retorna os centros encontrados.
     */
    public int[] getCenters() {
        return centers;
    }
}

