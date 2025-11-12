package tps.tp02;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Classe principal para testar as implementações do problema dos k-centros.
 * 
 * Para testar um arquivo, altere a variável FILENAME abaixo com o caminho do arquivo.
 * O valor de k (número de centros) será lido automaticamente do arquivo.
 */
public class Main {

    // ========================================================================
    // CONFIGURAÇÃO: Altere aqui o caminho do arquivo que deseja testar
    // ========================================================================
    private static final String FILENAME = "C:/Users/USER/Documents/GitHub/Grafos/tps/tp02/pmed1.txt";
    // Exemplos:
    // private static final String FILENAME = "pmed1.txt";
    // private static final String FILENAME = "pmed10.txt";
    // private static final String FILENAME = "pmed40.txt";
    // ========================================================================

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("TESTE - PROBLEMA DOS K-CENTROS");
        System.out.println("=================================================================");
        System.out.println("Arquivo: " + FILENAME);
        System.out.println();

        try {
            // Ler o valor de p (número de centros) do arquivo
            int p = readPFromFile(FILENAME);
            System.out.println("Número de centros (p): " + p);
            System.out.println();

            // Testar o arquivo
            testFile(FILENAME, p);

            System.out.println();
            System.out.println("✓ Teste concluído com sucesso!");

        } catch (FileNotFoundException e) {
            System.err.println("✗ Erro: Arquivo não encontrado: " + FILENAME);
            System.err.println("Verifique se o arquivo está no diretório correto.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("✗ Erro ao processar: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Lê o valor de p (número de centros) da primeira linha do arquivo.
     */
    private static int readPFromFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        
        // Ler n, m, p da primeira linha
        @SuppressWarnings("unused")
        int n = scanner.nextInt();
        @SuppressWarnings("unused")
        int m = scanner.nextInt();
        int p = scanner.nextInt();
        
        scanner.close();
        return p;
    }

    /**
     * Testa um arquivo específico.
     */
    private static void testFile(String filename, int k) throws FileNotFoundException {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Carregando grafo...");
        
        long startTime = System.currentTimeMillis();

        // Ler o grafo
        CompleteGraph graph = ORLibraryReader.readFromFileAuto(filename);
        int numVertices = graph.getNumVertices();
        
        long loadTime = System.currentTimeMillis() - startTime;
        System.out.println("✓ Grafo carregado!");
        System.out.println("  Vértices: " + numVertices);
        System.out.println("  Tempo de carregamento: " + loadTime + " ms");
        System.out.println();

        // Resolver com algoritmo aproximado (sempre executado)
        System.out.println("--- Algoritmo Aproximado (Guloso) ---");
        long approxStartTime = System.currentTimeMillis();
        ApproximateKCenters approxSolver = new ApproximateKCenters(graph, k);
        int[] approxCenters = approxSolver.solve();
        double approxRadius = approxSolver.getRadius();
        long approxTimeMs = System.currentTimeMillis() - approxStartTime;

        System.out.println("Centros encontrados: " + arrayToString(approxCenters));
        System.out.printf("Raio da solução: %.4f\n", approxRadius);
        System.out.println("Tempo de execução: " + approxTimeMs + " ms");
        System.out.println();

        // Tentar resolver com algoritmo exato
        long combinations = binomialCoefficient(numVertices, k);
        
        System.out.println("--- Algoritmo Exato ---");
        
        // Decidir qual algoritmo exato usar baseado no tamanho da instância
        boolean useBranchBound = (numVertices <= 40 && combinations <= 10_000_000);
        boolean useBruteForce = (numVertices <= 20 && combinations <= 1_000_000);
        
        if (!useBranchBound && !useBruteForce) {
            System.out.println("AVISO: Número de combinações muito grande (" + 
                (combinations > 1_000_000 ? ">1M" : combinations) + ")");
            System.out.println("Algoritmo exato não será executado (instância muito grande).");
            System.out.println("(Branch and Bound: n <= 40, Força Bruta: n <= 20 aproximadamente)");
        } else {
            long exactStartTime = System.currentTimeMillis();
            int[] exactCenters;
            double exactRadius;
            String algorithmName;
            
            if (useBranchBound) {
                // Usar Branch and Bound (mais eficiente para instâncias médias)
                algorithmName = "Branch and Bound";
                System.out.println("Método: Branch and Bound (com podas)");
                BranchBoundKCenters bbSolver = new BranchBoundKCenters(graph, k);
                exactCenters = bbSolver.solve();
                exactRadius = bbSolver.getBestRadius();
                System.out.println("Nós explorados: " + bbSolver.getNodesExplored());
                System.out.println("Nós podados: " + bbSolver.getNodesPruned());
            } else {
                // Usar Força Bruta (apenas para instâncias muito pequenas)
                algorithmName = "Força Bruta";
                System.out.println("Método: Força Bruta");
                ExactKCenters exactSolver = new ExactKCenters(graph, k);
                exactCenters = exactSolver.solve();
                exactRadius = exactSolver.getBestRadius();
            }
            
            long exactTimeMs = System.currentTimeMillis() - exactStartTime;

            System.out.println("Centros encontrados: " + arrayToString(exactCenters));
            System.out.printf("Raio da solução: %.4f\n", exactRadius);
            System.out.println("Tempo de execução: " + exactTimeMs + " ms");
            System.out.println();

            // Comparar resultados
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("COMPARAÇÃO");
            System.out.println("═══════════════════════════════════════════════════════════════");
            double ratio = approxRadius / exactRadius;
            System.out.printf("Razão (Aproximado/Exato): %.4f\n", ratio);
            System.out.printf("Fator de aproximação: %.2fx\n", ratio);
            
            if (ratio <= 2.0) {
                System.out.println("✓ Aproximação dentro do limite teórico (2x)");
            } else {
                System.out.println("⚠ Aproximação acima do limite teórico esperado");
            }
            
            System.out.printf("Diferença de tempo: %d ms (%s) vs %d ms (Aproximado)\n",
                    exactTimeMs, algorithmName, approxTimeMs);
            double speedup = (double) exactTimeMs / approxTimeMs;
            System.out.printf("Speedup: %.2fx mais rápido (Aproximado)\n", speedup);
        }
    }

    /**
     * Converte um array em string formatada.
     */
    private static String arrayToString(int[] arr) {
        if (arr == null || arr.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Calcula o coeficiente binomial C(n, k).
     */
    private static long binomialCoefficient(int n, int k) {
        if (k > n - k) {
            k = n - k;
        }
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }
}

