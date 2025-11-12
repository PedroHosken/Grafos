package tps.tp02;

/**
 * Classe principal para resolver o problema dos k-centros e comparar
 * as abordagens exata e aproximada.
 */
public class KCentersSolver {

    private CompleteGraph graph;
    private int k;
    private SolutionResult exactResult;
    private SolutionResult approximateResult;

    /**
     * Constrói um resolvedor para o problema dos k-centros.
     * 
     * @param graph Grafo completo
     * @param k Número de centros a encontrar
     */
    public KCentersSolver(CompleteGraph graph, int k) {
        this.graph = graph;
        this.k = k;
    }

    /**
     * Resolve o problema usando o algoritmo exato.
     * 
     * @return Resultado da solução exata
     */
    public SolutionResult solveExact() {
        long startTime = System.nanoTime();
        
        ExactKCenters exactSolver = new ExactKCenters(graph, k);
        int[] centers = exactSolver.solve();
        double radius = exactSolver.getBestRadius();
        
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        
        exactResult = new SolutionResult(centers, radius, durationMs, true);
        return exactResult;
    }

    /**
     * Resolve o problema usando o algoritmo aproximado.
     * 
     * @return Resultado da solução aproximada
     */
    public SolutionResult solveApproximate() {
        long startTime = System.nanoTime();
        
        ApproximateKCenters approxSolver = new ApproximateKCenters(graph, k);
        int[] centers = approxSolver.solve();
        double radius = approxSolver.getRadius();
        
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        
        approximateResult = new SolutionResult(centers, radius, durationMs, false);
        return approximateResult;
    }

    /**
     * Resolve usando ambos os algoritmos e compara os resultados.
     */
    public void solveAndCompare() {
        System.out.println("=== Resolvendo problema dos k-centros ===");
        System.out.println("Número de vértices: " + graph.getNumVertices());
        System.out.println("Número de centros (k): " + k);
        System.out.println();

        // Resolver com algoritmo aproximado (sempre viável)
        System.out.println("--- Algoritmo Aproximado (Guloso) ---");
        solveApproximate();
        printResult(approximateResult);

        // Resolver com algoritmo exato (apenas se viável)
        int n = graph.getNumVertices();
        long combinations = binomialCoefficient(n, k);
        
        System.out.println();
        System.out.println("--- Algoritmo Exato (Força Bruta) ---");
        
        if (combinations > 1_000_000) {
            System.out.println("AVISO: Número de combinações muito grande (" + combinations + ")");
            System.out.println("Algoritmo exato pode ser muito lento. Pulando...");
            System.out.println("(Use apenas para instâncias pequenas: n <= 20 aproximadamente)");
        } else {
            solveExact();
            printResult(exactResult);
            
            // Comparar resultados
            System.out.println();
            System.out.println("=== Comparação ===");
            double ratio = approximateResult.getRadius() / exactResult.getRadius();
            System.out.printf("Razão (Aproximado/Exato): %.4f\n", ratio);
            System.out.printf("Fator de aproximação: %.2fx\n", ratio);
            
            if (ratio <= 2.0) {
                System.out.println("✓ Aproximação dentro do limite teórico (2x)");
            } else {
                System.out.println("⚠ Aproximação acima do limite teórico esperado");
            }
            
            System.out.printf("Diferença de tempo: %d ms (Exato) vs %d ms (Aproximado)\n",
                    exactResult.getTimeMs(), approximateResult.getTimeMs());
            double speedup = (double) exactResult.getTimeMs() / approximateResult.getTimeMs();
            System.out.printf("Speedup: %.2fx mais rápido (Aproximado)\n", speedup);
        }
    }

    /**
     * Imprime os resultados de uma solução.
     */
    private void printResult(SolutionResult result) {
        System.out.println("Centros encontrados: " + arrayToString(result.getCenters()));
        System.out.printf("Raio da solução: %.4f\n", result.getRadius());
        System.out.println("Tempo de execução: " + result.getTimeMs() + " ms");
    }

    /**
     * Converte um array em string formatada.
     */
    private String arrayToString(int[] arr) {
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
    private long binomialCoefficient(int n, int k) {
        if (k > n - k) {
            k = n - k; // Usar simetria
        }
        
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }

    /**
     * Classe interna para armazenar resultados de uma solução.
     */
    public static class SolutionResult {
        private int[] centers;
        private double radius;
        private long timeMs;
        private boolean isExact;

        public SolutionResult(int[] centers, double radius, long timeMs, boolean isExact) {
            this.centers = centers;
            this.radius = radius;
            this.timeMs = timeMs;
            this.isExact = isExact;
        }

        public int[] getCenters() {
            return centers;
        }

        public double getRadius() {
            return radius;
        }

        public long getTimeMs() {
            return timeMs;
        }

        public boolean isExact() {
            return isExact;
        }
    }
}

