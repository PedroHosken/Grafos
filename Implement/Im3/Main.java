package Implement.Im3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Classe principal para ler o grafo de um arquivo, executar o
 * algoritmo de caminho mínimo e imprimir o resultado.
 * O nome do arquivo é definido diretamente na variável 'filename'.
 */
public class Main {

    public static void main(String[] args) {

        String filename = "C:/Users/USER/Documents/GitHub/Grafos/Implement/Im3/denso_4.txt";

        try {
            File file = new File(filename);
            Scanner sc = new Scanner(file);

            // 1. Ler V e E
            int V = sc.nextInt();
            int E = sc.nextInt();
            Graph graph = new Graph(V);

            // 2. Ler as E arestas
            for (int i = 0; i < E; i++) {
                int from = sc.nextInt();
                int to = sc.nextInt();
                int weight = sc.nextInt(); // MUDANÇA: de sc.nextDouble() para sc.nextInt()
                graph.addEdge(from, to, weight);
            }

            // 3. Ler origem e destino
            int source = sc.nextInt();
            int destination = sc.nextInt();

            System.out.println("Lendo grafo de: " + filename);
            System.out.println("Vértices: " + V + ", Arestas: " + E);
            System.out.println("Buscando caminho de " + source + " para " + destination + ".../n");

            // 4. Executar o algoritmo e medir o tempo
            ShortestPathFinder finder = new ShortestPathFinder(graph);

            long startTime = System.nanoTime();
            PathResult result = finder.findShortestPath(source, destination);
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;

            // 5. Imprimir resultados
            result.printResult();

            System.out.println("Tempo de execução: " + durationMs + " ms");

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Erro: Arquivo não encontrado: " + filename);
            System.out.println("Verifique se o nome do arquivo na variável 'filename' está correto.");
        } catch (Exception e) {
            System.out.println("Erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}