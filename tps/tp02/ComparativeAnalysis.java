package tps.tp02;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe para realizar análise comparativa entre os algoritmos exato e aproximado
 * em múltiplas instâncias da OR-Library.
 */
public class ComparativeAnalysis {

    /**
     * Executa análise comparativa em uma lista de arquivos.
     * 
     * @param filenames Lista de caminhos para arquivos de instâncias
     * @param k Número de centros a encontrar
     * @param outputFile Arquivo de saída para o relatório (opcional, null para imprimir no console)
     */
    public static void analyzeInstances(List<String> filenames, int k, String outputFile) {
        List<AnalysisResult> results = new ArrayList<>();

        System.out.println("=== Análise Comparativa - Problema dos k-centros ===");
        System.out.println("Número de instâncias: " + filenames.size());
        System.out.println("k (centros): " + k);
        System.out.println();

        for (String filename : filenames) {
            try {
                System.out.println("Processando: " + filename);
                AnalysisResult result = analyzeInstance(filename, k);
                results.add(result);
                System.out.println("✓ Concluído");
                System.out.println();
            } catch (Exception e) {
                System.err.println("✗ Erro ao processar " + filename + ": " + e.getMessage());
            }
        }

        // Gerar relatório
        generateReport(results, outputFile);
    }

    /**
     * Analisa uma única instância.
     */
    private static AnalysisResult analyzeInstance(String filename, int k) throws FileNotFoundException {
        CompleteGraph graph = ORLibraryReader.readFromFileAuto(filename);
        int n = graph.getNumVertices();

        AnalysisResult result = new AnalysisResult();
        result.filename = new File(filename).getName();
        result.numVertices = n;
        result.k = k;

        // Algoritmo aproximado (sempre executado)
        long startTime = System.nanoTime();
        ApproximateKCenters approxSolver = new ApproximateKCenters(graph, k);
        approxSolver.solve(); // Resolve e armazena internamente
        double approxRadius = approxSolver.getRadius();
        long endTime = System.nanoTime();
        result.approxTimeMs = (endTime - startTime) / 1_000_000;
        result.approxRadius = approxRadius;

        // Algoritmo exato (apenas se viável)
        long combinations = binomialCoefficient(n, k);
        result.numCombinations = combinations;

        if (combinations <= 1_000_000 && n <= 25) {
            startTime = System.nanoTime();
            ExactKCenters exactSolver = new ExactKCenters(graph, k);
            exactSolver.solve(); // Resolve e armazena internamente
            double exactRadius = exactSolver.getBestRadius();
            endTime = System.nanoTime();
            result.exactTimeMs = (endTime - startTime) / 1_000_000;
            result.exactRadius = exactRadius;
            result.approximationRatio = approxRadius / exactRadius;
            result.speedup = (double) result.exactTimeMs / result.approxTimeMs;
        } else {
            result.exactTimeMs = -1; // Não executado
            result.exactRadius = -1;
            result.approximationRatio = -1;
            result.speedup = -1;
        }

        return result;
    }

    /**
     * Gera relatório dos resultados.
     */
    private static void generateReport(List<AnalysisResult> results, String outputFile) {
        PrintWriter writer = null;
        try {
            if (outputFile != null) {
                writer = new PrintWriter(outputFile);
            }

            PrintWriter out = writer != null ? writer : new PrintWriter(System.out);

            out.println("=================================================================");
            out.println("RELATÓRIO DE ANÁLISE COMPARATIVA - PROBLEMA DOS K-CENTROS");
            out.println("=================================================================");
            out.println();

            // Cabeçalho da tabela
            out.printf("%-20s %8s %4s %15s %12s %12s %12s %10s %10s\n",
                    "Instância", "Vértices", "k", "Combinações", "Raio Aprox", "Raio Exato",
                    "Razão", "Tempo Aprox", "Tempo Exato");
            out.println("-".repeat(120));

            // Dados
            int exactCount = 0;
            double totalRatio = 0.0;
            double totalSpeedup = 0.0;

            for (AnalysisResult r : results) {
                String combStr = r.numCombinations > 1_000_000 ? ">1M" : String.valueOf(r.numCombinations);
                String exactRadiusStr = r.exactRadius >= 0 ? String.format("%.4f", r.exactRadius) : "N/A";
                String ratioStr = r.approximationRatio >= 0 ? String.format("%.4f", r.approximationRatio) : "N/A";
                String exactTimeStr = r.exactTimeMs >= 0 ? r.exactTimeMs + " ms" : "N/A";

                out.printf("%-20s %8d %4d %15s %12.4f %12s %12s %10d ms %10s\n",
                        r.filename, r.numVertices, r.k, combStr, r.approxRadius,
                        exactRadiusStr, ratioStr, r.approxTimeMs, exactTimeStr);

                if (r.exactRadius >= 0) {
                    exactCount++;
                    totalRatio += r.approximationRatio;
                    totalSpeedup += r.speedup;
                }
            }

            out.println("-".repeat(120));

            // Estatísticas
            out.println();
            out.println("=== ESTATÍSTICAS ===");
            out.println("Total de instâncias processadas: " + results.size());
            out.println("Instâncias resolvidas exatamente: " + exactCount);

            if (exactCount > 0) {
                double avgRatio = totalRatio / exactCount;
                double avgSpeedup = totalSpeedup / exactCount;
                out.printf("Razão média (Aprox/Exato): %.4f\n", avgRatio);
                out.printf("Speedup médio: %.2fx\n", avgSpeedup);

                if (avgRatio <= 2.0) {
                    out.println("✓ Aproximação dentro do limite teórico (2x)");
                } else {
                    out.println("⚠ Aproximação acima do limite teórico esperado");
                }
            }

            out.println();
            out.println("=================================================================");

            if (writer != null) {
                writer.close();
                System.out.println("Relatório salvo em: " + outputFile);
            }

        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
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

    /**
     * Classe para armazenar resultados de uma análise.
     */
    static class AnalysisResult {
        String filename;
        int numVertices;
        int k;
        long numCombinations;
        double approxRadius;
        long approxTimeMs;
        double exactRadius;
        long exactTimeMs;
        double approximationRatio;
        double speedup;
    }
}

