package tps.tp02;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Esta classe implementa uma solução APROXIMADA (heurística)
 * para o problema dos k-centros.
 * * Ela usa o algoritmo "Primeiro-Mais-Distante" (Farthest-First).
 */
public class SolucaoAproximada {

    // --- Variáveis da Classe ---
    private final Instancia instancia;
    private final int V;
    private final int k;
    private final int[][] distancias;

    /**
     * Classe interna para encapsular o resultado (igual à da SolucaoExata).
     */
    public static class Resultado {
        public final long raio;
        public final int[] centros;
        public final double tempoExecucaoMs;

        public Resultado(long raio, int[] centros, double tempoExecucaoMs) {
            this.raio = raio;
            this.centros = centros;
            this.tempoExecucaoMs = tempoExecucaoMs;
        }
    }

    // --- Construtor ---

    public SolucaoAproximada(Instancia instancia) {
        this.instancia = instancia;
        this.V = instancia.getV();
        this.k = instancia.getK();
        this.distancias = instancia.getDistancias();
    }

    // --- Método Público Principal ---

    /**
     * Executa a heurística Farthest-First.
     * 
     * @return Um objeto Resultado com o raio, os centros e o tempo.
     */
    public Resultado resolver() {
        System.out.println("Iniciando Solução Aproximada (Farthest-First)...");

        long inicio = System.nanoTime();

        // Conjunto para armazenar os índices dos centros escolhidos.
        // Usamos um Set para verificar rapidamente se um vértice já é centro.
        Set<Integer> centrosSet = new HashSet<>();

        // Array para armazenar os centros finais (para o resultado)
        int[] centrosArray = new int[k];
        int contagemCentros = 0;

        // 1. Escolha o primeiro centro
        // Vamos escolher o vértice 0 como padrão.
        int primeiroCentro = 0;
        centrosSet.add(primeiroCentro);
        centrosArray[contagemCentros] = primeiroCentro;
        contagemCentros++;

        // Array para guardar a menor distância de cada vértice até um centro
        long[] distMinimaParaCentro = new long[V];
        Arrays.fill(distMinimaParaCentro, Long.MAX_VALUE);

        // Atualiza a distância de todos os vértices para este primeiro centro
        for (int v = 0; v < V; v++) {
            distMinimaParaCentro[v] = distancias[v][primeiroCentro];
        }

        // 2. Escolha os k-1 centros restantes
        while (contagemCentros < k) {

            // Encontra o vértice "mais distante"
            long maxDist = -1;
            int proximoCentro = -1;

            for (int v = 0; v < V; v++) {
                // Procura apenas entre vértices que AINDA NÃO SÃO centros
                if (!centrosSet.contains(v)) {

                    // distMinimaParaCentro[v] contém a distância de 'v'
                    // para o centro mais próximo dele (escolhido até agora).
                    if (distMinimaParaCentro[v] > maxDist) {
                        maxDist = distMinimaParaCentro[v];
                        proximoCentro = v;
                    }
                }
            }

            // Adiciona o vértice encontrado como o novo centro
            centrosSet.add(proximoCentro);
            centrosArray[contagemCentros] = proximoCentro;
            contagemCentros++;

            // 3. Atualiza as distâncias mínimas
            // Agora que temos um novo centro, recalculamos a distMinimaParaCentro
            // de cada vértice.
            for (int v = 0; v < V; v++) {
                long distParaNovoCentro = distancias[v][proximoCentro];
                if (distParaNovoCentro < distMinimaParaCentro[v]) {
                    distMinimaParaCentro[v] = distParaNovoCentro;
                }
            }
        }

        // 4. Calcula o raio final da solução encontrada
        // O raio é o valor de 'maxDist' encontrado no ÚLTIMO passo,
        // mas para garantir, vamos recalcular o raio final.
        // (Opcional, mas mais seguro)
        long raioFinal = calcularRaio(centrosArray);

        long fim = System.nanoTime();
        double tempoMs = (fim - inicio) / 1_000_000.0;

        // Ordena o array de centros (opcional, apenas para exibição)
        Arrays.sort(centrosArray);

        return new Resultado(raioFinal, centrosArray, tempoMs);
    }

    /**
     * Calcula o "raio" para um dado conjunto de centros.
     * (Método auxiliar, idêntico ao da SolucaoExata)
     */
    private long calcularRaio(int[] centros) {
        long raioMaximoDaSolucao = 0;

        for (int v = 0; v < V; v++) {
            long distanciaMinimaCentro = Long.MAX_VALUE;
            for (int centro : centros) {
                long dist = distancias[v][centro];
                if (dist < distanciaMinimaCentro) {
                    distanciaMinimaCentro = dist;
                }
            }
            if (distanciaMinimaCentro > raioMaximoDaSolucao) {
                raioMaximoDaSolucao = distanciaMinimaCentro;
            }
        }
        return raioMaximoDaSolucao;
    }

}
