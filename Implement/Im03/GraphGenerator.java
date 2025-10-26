package Implement.Im03;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Gera arquivos de teste para o ShortestPathTask.
 */
public class GraphGenerator {

    /**
     * args: V E source dest maxWeight filename
     */
    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Uso: java GraphGenerator <V> <E> <source> <dest> <maxWeight> <filename>");
            System.out.println("  V: Número de vértices");
            System.out.println("  E: Número de arestas");
            System.out.println("  source: Vértice de origem (0 a V-1)");
            System.out.println("  dest: Vértice de destino (0 a V-1)");
            System.out.println("  maxWeight: Peso máximo da aresta (1 a maxWeight)");
            System.out.println("  filename: Nome do arquivo de saída");
            return;
        }

        try {
            int V = Integer.parseInt(args[0]);
            long E_long = Long.parseLong(args[1]); // Usar long para E
            int source = Integer.parseInt(args[2]);
            int dest = Integer.parseInt(args[3]);
            int maxWeight = Integer.parseInt(args[4]);
            String filename = args[5];

            // Validação de V, E, source, dest
            long maxPossibleEdges = (long) V * (V - 1); // Grafo direcionado sem auto-loops
            if (E_long > maxPossibleEdges) {
                System.out.println("Aviso: E (" + E_long + ") excede o máximo (" + maxPossibleEdges
                        + "). Ajustando para " + maxPossibleEdges);
                E_long = maxPossibleEdges;
            }
            int E = (int) E_long;

            Random rand = new Random();
            // Usar um Set garante que não criamos arestas duplicadas (u, v)
            Set<String> edges = new HashSet<>();

            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                // Linha 1: V E
                writer.println(V + " " + E);
                // Linha 2: Origem Destino
                writer.println(source + " " + dest);

                for (int i = 0; i < E; i++) {
                    int u, v;
                    String edgeKey;

                    do {
                        u = rand.nextInt(V);
                        v = rand.nextInt(V);
                        while (u == v) { // Evita auto-loops
                            v = rand.nextInt(V);
                        }
                        edgeKey = u + "-" + v;
                    } while (edges.contains(edgeKey)); // Garante arestas únicas

                    edges.add(edgeKey);
                    int w = rand.nextInt(maxWeight) + 1; // Peso de 1 até maxWeight

                    writer.println(u + " " + v + " " + w);
                }
            }

            System.out.println("Arquivo de grafo '" + filename + "' gerado com sucesso (" + V + "V, " + E + "E).");

        } catch (NumberFormatException e) {
            System.err.println("Erro: V, E, source, dest e maxWeight devem ser números inteiros.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
