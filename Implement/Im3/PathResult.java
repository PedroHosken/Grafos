package Implement.Im3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Armazena e exibe o resultado da busca pelo caminho mínimo.
 */
public class PathResult {
    public final long distance; // MUDANÇA: de double para long
    public final int edgeCount;
    public final List<Integer> path;
    public final boolean found;

    /**
     * Construtor para um caminho encontrado.
     * 
     * @param distance    Distância total (peso) do caminho.
     * @param edgeCount   Número de arestas no caminho.
     * @param parent      O array de predecessores para reconstruir o caminho.
     * @param source      Vértice de origem.
     * @param destination Vértice de destino.
     */
    public PathResult(long distance, int edgeCount, int[] parent, int source, int destination) { // MUDANÇA: distance é
                                                                                                 // long
        this.distance = distance;
        this.edgeCount = edgeCount;
        this.found = true;
        this.path = reconstructPath(parent, source, destination);
    }

    /**
     * Construtor para quando nenhum caminho é encontrado.
     */
    public PathResult() {
        this.distance = Long.MAX_VALUE; // MUDANÇA: Valor "infinito" para long
        this.edgeCount = 0;
        this.path = new ArrayList<>();
        this.found = false;
    }

    /**
     * Reconstrói a lista de vértices do caminho usando o array de pais.
     */
    private List<Integer> reconstructPath(int[] parent, int source, int destination) {
        List<Integer> pathList = new ArrayList<>();
        int current = destination;

        while (current != -1) {
            pathList.add(current);
            if (current == source) {
                break;
            }
            current = parent[current];
        }

        if (pathList.isEmpty() || pathList.get(pathList.size() - 1) != source) {
            return new ArrayList<>();
        }

        Collections.reverse(pathList);
        return pathList;
    }

    /**
     * Imprime o resultado formatado no console.
     */
    public void printResult() {
        if (!found || path.isEmpty()) {
            System.out.println("Nenhum caminho encontrado da origem ao destino.");
            return;
        }

        System.out.println("--- Caminho Mínimo Encontrado ---");
        // MUDANÇA: Trocado printf de %.2f (float) para %d (decimal/inteiro)
        System.out.printf("Comprimento (Peso Total): %d\n", distance);
        System.out.println("Quantidade de Arestas: " + edgeCount);

        System.out.print("Caminho (Vértices): ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println("\n----------------------------------");
    }
}