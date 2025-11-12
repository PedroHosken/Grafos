package tps.tp02;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação exata do problema dos k-centros usando Branch and Bound.
 * 
 * Este algoritmo é mais eficiente que a força bruta pura, pois:
 * - Usa podas para eliminar ramos da árvore de busca que não podem levar a soluções melhores
 * - Calcula limites inferiores para determinar quando parar a busca em um ramo
 * - Mantém a melhor solução encontrada até o momento para comparação
 * 
 * Complexidade: O(C(n,k) * n * k) no pior caso, mas com podas pode ser muito mais rápido
 * 
 * Viável para instâncias maiores que a força bruta (n <= 30-40 aproximadamente, dependendo de k).
 */
public class BranchBoundKCenters {

    private CompleteGraph graph;
    private int k;
    private int[] bestCenters;
    private double bestRadius;
    private int nodesExplored;
    private int nodesPruned;

    /**
     * Constrói um resolvedor Branch and Bound para o problema dos k-centros.
     * 
     * @param graph Grafo completo
     * @param k Número de centros a encontrar
     */
    public BranchBoundKCenters(CompleteGraph graph, int k) {
        this.graph = graph;
        this.k = k;
        this.bestRadius = Double.MAX_VALUE;
        this.bestCenters = null;
        this.nodesExplored = 0;
        this.nodesPruned = 0;
    }

    /**
     * Resolve o problema dos k-centros usando Branch and Bound.
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

        // Inicializar com uma solução gulosa (para ter um limite superior inicial)
        ApproximateKCenters approxSolver = new ApproximateKCenters(graph, k);
        approxSolver.solve();
        bestRadius = approxSolver.getRadius();
        bestCenters = approxSolver.getCenters().clone();

        // Iniciar busca Branch and Bound
        List<Integer> currentCenters = new ArrayList<>();
        branchAndBound(0, 0, currentCenters, n);

        return bestCenters;
    }

    /**
     * Método recursivo de Branch and Bound.
     * 
     * @param start Índice do primeiro vértice a considerar
     * @param depth Profundidade atual na árvore (quantos centros já foram escolhidos)
     * @param currentCenters Lista de centros escolhidos até agora
     * @param n Número total de vértices
     */
    private void branchAndBound(int start, int depth, List<Integer> currentCenters, int n) {
        nodesExplored++;

        // Caso base: escolhemos k centros
        if (depth == k) {
            int[] centers = new int[k];
            for (int i = 0; i < k; i++) {
                centers[i] = currentCenters.get(i);
            }
            double radius = graph.calculateRadius(centers);
            
            if (radius < bestRadius) {
                bestRadius = radius;
                bestCenters = centers.clone();
            }
            return;
        }

        // Calcular quantos vértices ainda precisamos escolher
        int remaining = k - depth;
        
        // Para cada vértice candidato
        for (int i = start; i <= n - remaining; i++) {
            // Adicionar vértice i como centro
            currentCenters.add(i);
            
            // Calcular limite inferior para esta solução parcial
            double lowerBound = calculateLowerBound(currentCenters, n);
            
            // Podar se o limite inferior já é pior que a melhor solução atual
            if (lowerBound < bestRadius) {
                // Continuar a busca neste ramo
                branchAndBound(i + 1, depth + 1, currentCenters, n);
            } else {
                nodesPruned++;
            }
            
            // Remover vértice i (backtracking)
            currentCenters.remove(currentCenters.size() - 1);
        }
    }

    /**
     * Calcula um limite inferior para o raio de uma solução parcial.
     * 
     * O limite inferior é calculado como a maior distância mínima de qualquer vértice
     * aos centros já escolhidos, assumindo que os centros restantes serão escolhidos
     * de forma ótima (o que não é possível, mas dá um limite inferior).
     * 
     * @param currentCenters Centros já escolhidos
     * @param n Número total de vértices
     * @return Limite inferior para o raio
     */
    private double calculateLowerBound(List<Integer> currentCenters, int n) {
        if (currentCenters.isEmpty()) {
            return 0.0;
        }

        // Calcular a distância de cada vértice ao centro mais próximo já escolhido
        double maxDist = 0.0;
        for (int v = 0; v < n; v++) {
            double minDist = Double.MAX_VALUE;
            for (int center : currentCenters) {
                double dist = graph.getDistance(v, center);
                if (dist < minDist) {
                    minDist = dist;
                }
            }
            if (minDist > maxDist) {
                maxDist = minDist;
            }
        }

        // O limite inferior é a maior distância atual
        // (mesmo escolhendo os centros restantes de forma ótima, não podemos melhorar isso)
        return maxDist;
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

    /**
     * Retorna o número de nós explorados na árvore de busca.
     */
    public int getNodesExplored() {
        return nodesExplored;
    }

    /**
     * Retorna o número de nós podados (ramos cortados).
     */
    public int getNodesPruned() {
        return nodesPruned;
    }
}

