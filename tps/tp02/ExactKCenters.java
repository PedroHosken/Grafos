package tps.tp02;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação exata do problema dos k-centros usando força bruta.
 * Testa todas as combinações possíveis de k vértices como centros.
 * 
 * Complexidade: O(C(n,k) * n * k) onde C(n,k) é o número de combinações
 * 
 * Este algoritmo é viável apenas para instâncias pequenas (n <= 20 aproximadamente).
 */
public class ExactKCenters {

    private CompleteGraph graph;
    private int k;
    private int[] bestCenters;
    private double bestRadius;

    /**
     * Constrói um resolvedor exato para o problema dos k-centros.
     * 
     * @param graph Grafo completo
     * @param k Número de centros a encontrar
     */
    public ExactKCenters(CompleteGraph graph, int k) {
        this.graph = graph;
        this.k = k;
        this.bestRadius = Double.MAX_VALUE;
        this.bestCenters = null;
    }

    /**
     * Resolve o problema dos k-centros de forma exata.
     * 
     * @return Array com os índices dos k centros ótimos
     */
    public int[] solve() {
        int n = graph.getNumVertices();
        
        if (k >= n) {
            // Se k >= n, todos os vértices são centros
            bestCenters = new int[n];
            for (int i = 0; i < n; i++) {
                bestCenters[i] = i;
            }
            bestRadius = 0.0;
            return bestCenters;
        }

        // Gerar todas as combinações de k vértices
        List<int[]> combinations = generateCombinations(n, k);
        
        // Testar cada combinação
        for (int[] centers : combinations) {
            double radius = graph.calculateRadius(centers);
            if (radius < bestRadius) {
                bestRadius = radius;
                bestCenters = centers.clone();
            }
        }

        return bestCenters;
    }

    /**
     * Gera todas as combinações de k elementos de um conjunto de n elementos.
     * 
     * @param n Tamanho do conjunto
     * @param k Tamanho da combinação
     * @return Lista de todas as combinações
     */
    private List<int[]> generateCombinations(int n, int k) {
        List<int[]> combinations = new ArrayList<>();
        int[] current = new int[k];
        generateCombinationsRecursive(combinations, current, 0, 0, n, k);
        return combinations;
    }

    /**
     * Método recursivo auxiliar para gerar combinações.
     */
    private void generateCombinationsRecursive(List<int[]> combinations, int[] current, 
                                               int start, int depth, int n, int k) {
        if (depth == k) {
            combinations.add(current.clone());
            return;
        }

        for (int i = start; i < n; i++) {
            current[depth] = i;
            generateCombinationsRecursive(combinations, current, i + 1, depth + 1, n, k);
        }
    }

    /**
     * Retorna o raio da melhor solução encontrada.
     */
    public double getBestRadius() {
        return bestRadius;
    }

    /**
     * Retorna os centros da melhor solução encontrada.
     */
    public int[] getBestCenters() {
        return bestCenters;
    }
}

