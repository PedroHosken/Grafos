package tps.tp02;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe para ler instâncias da OR-Library no formato p-medianas.
 * 
 * Formato real da OR-Library:
 * - Linha 1: n m p (número de vértices, número de arestas, número de centros)
 * - Linhas seguintes: m arestas no formato u v w (vértice origem, vértice destino, peso)
 * 
 * Nota: Os vértices podem ser 1-indexed ou 0-indexed. Este leitor assume 1-indexed
 * e converte para 0-indexed internamente.
 */
public class ORLibraryReader {

    /**
     * Lê um arquivo da OR-Library e retorna um grafo completo.
     * 
     * Formato esperado (p-medianas):
     * - Linha 1: p (número de centros) e n (número de vértices)
     * - Linhas seguintes: matriz de distâncias n x n
     * 
     * @param filename Caminho do arquivo
     * @return Grafo completo com as distâncias
     * @throws FileNotFoundException Se o arquivo não for encontrado
     */
    public static CompleteGraph readFromFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        // Ler p e n (número de centros e vértices)
        // Nota: p não é usado diretamente, mas está no formato do arquivo
        @SuppressWarnings("unused")
        int p = scanner.nextInt();
        int n = scanner.nextInt();

        CompleteGraph graph = new CompleteGraph(n);

        // Ler a matriz de distâncias
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double distance = scanner.nextDouble();
                graph.setDistance(i, j, distance);
            }
        }

        scanner.close();
        return graph;
    }

    /**
     * Lê um arquivo da OR-Library no formato p-medianas com grafo esparso.
     * Calcula as distâncias entre todos os pares usando Floyd-Warshall.
     * 
     * Formato esperado:
     * - Linha 1: n m p (vértices, arestas, centros)
     * - Linhas seguintes: m arestas no formato u v w
     * 
     * @param filename Caminho do arquivo
     * @return Grafo completo com distâncias calculadas
     * @throws FileNotFoundException Se o arquivo não for encontrado
     */
    public static CompleteGraph readFromFileAuto(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        // Ler n, m, p
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        @SuppressWarnings("unused")
        int p = scanner.nextInt(); // p não é usado, mas está no formato

        // Criar estrutura para armazenar o grafo esparso
        // Usar lista de adjacência
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        // Ler as arestas
        // Assumir que os vértices são 1-indexed e converter para 0-indexed
        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt() - 1; // Converter para 0-indexed
            int v = scanner.nextInt() - 1; // Converter para 0-indexed
            double weight = scanner.nextDouble();
            
            // Adicionar aresta bidirecional (grafo não direcionado)
            adj.get(u).add(new Edge(v, weight));
            adj.get(v).add(new Edge(u, weight));
        }

        scanner.close();

        // Calcular distâncias entre todos os pares usando Floyd-Warshall
        CompleteGraph graph = new CompleteGraph(n);
        double[][] dist = new double[n][n];

        // Inicializar matriz de distâncias
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0.0;
                } else {
                    dist[i][j] = Double.MAX_VALUE;
                }
            }
        }

        // Preencher com arestas diretas
        for (int u = 0; u < n; u++) {
            for (Edge e : adj.get(u)) {
                int v = e.to;
                double w = e.weight;
                dist[u][v] = Math.min(dist[u][v], w);
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < Double.MAX_VALUE && dist[k][j] < Double.MAX_VALUE) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }

        // Copiar para o grafo completo
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                graph.setDistance(i, j, dist[i][j]);
            }
        }

        return graph;
    }

    /**
     * Classe auxiliar para representar uma aresta.
     */
    private static class Edge {
        int to;
        double weight;

        Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }
}

