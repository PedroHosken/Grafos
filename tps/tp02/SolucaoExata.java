package tps.tp02;

import java.util.Arrays;

/**
 * Esta classe implementa a solução EXATA (força bruta)
 * para o problema dos k-centros.
 * * Ela funciona testando todas as combinações possíveis de k centros.
 */
public class SolucaoExata {

    // --- Variáveis da Classe ---
    private final Instancia instancia; // Nossos dados (V, k, distancias)
    private final int V;
    private final int k;
    private final int[][] distancias;

    private long menorRaioGlobal = Long.MAX_VALUE; // O melhor raio encontrado
    private int[] melhoresCentros = new int[0]; // O conjunto de centros que gerou o menorRaioGlobal

    /**
     * Classe interna para encapsular o resultado da execução.
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

    /**
     * Construtor da solução.
     * 
     * @param instancia O problema carregado do arquivo.
     */
    public SolucaoExata(Instancia instancia) {
        this.instancia = instancia;
        this.V = instancia.getV();
        this.k = instancia.getK();
        this.distancias = instancia.getDistancias();
    }

    // --- Método Público Principal ---

    /**
     * Executa o algoritmo de força bruta.
     * 
     * @return Um objeto Resultado com o raio, os centros e o tempo.
     */
    public Resultado resolver() {
        System.out.println("Iniciando Solução Exata (Força Bruta)...");
        System.out.println("Isso pode demorar bastante para V=" + V + ", k=" + k);

        // Para medir o tempo de execução
        long inicio = System.nanoTime();

        // Prepara o array que guardará uma combinação
        int[] combinacaoAtual = new int[k];

        // Inicia o processo recursivo de geração de combinações
        // Começa do vértice 0, com 0 centros escolhidos
        gerarCombinacoes(0, 0, combinacaoAtual);

        long fim = System.nanoTime();
        double tempoMs = (fim - inicio) / 1_000_000.0;

        // Retorna o melhor resultado encontrado
        return new Resultado(menorRaioGlobal, melhoresCentros, tempoMs);
    }

    // --- Lógica do Algoritmo ---

    /**
     * Função recursiva para gerar todas as combinações de k centros.
     * * @param inicio O índice do vértice a partir do qual tentaremos adicionar
     * 
     * @param contagem        Quantos centros já foram escolhidos
     * @param combinacaoAtual O array com os centros escolhidos até agora
     */
    private void gerarCombinacoes(int inicio, int contagem, int[] combinacaoAtual) {
        // --- Caso Base da Recursão ---
        // Se já escolhemos k centros, temos uma combinação completa.
        if (contagem == k) {
            // Agora, calculamos o "raio" desta combinação de centros
            long raioDestaCombinacao = calcularRaio(combinacaoAtual);

            // Se este raio for o menor que já vimos, salvamos ele
            if (raioDestaCombinacao < menorRaioGlobal) {
                menorRaioGlobal = raioDestaCombinacao;
                // Copiamos o array, pois combinacaoAtual será modificado
                melhoresCentros = Arrays.copyOf(combinacaoAtual, k);
            }
            return; // Encerra este ramo da recursão
        }

        // --- Passo Recursivo ---
        // Tenta adicionar cada vértice 'i' como o próximo centro
        for (int i = inicio; i < V; i++) {
            // O 'i' só pode ser escolhido se ainda houver "espaço"
            // no grafo para completar os k centros.
            // Esta verificação (i <= V - (k - contagem)) é uma otimização
            // para "podar" ramos da busca que não levarão a lugar nenhum.
            if (i <= V - (k - contagem)) {
                combinacaoAtual[contagem] = i; // Escolhe o vértice 'i'

                // Chama a recursão para escolher o próximo centro
                // Começa a busca a partir de 'i + 1' para evitar duplicatas
                // (ex: [1, 2] e [2, 1])
                gerarCombinacoes(i + 1, contagem + 1, combinacaoAtual);
            } else {
                // Se i for muito grande, não há como completar k centros
                break;
            }
        }
    }

    /**
     * Calcula o "raio" para um dado conjunto de centros.
     * O raio é a maior distância de um vértice qualquer até o seu centro
     * mais próximo.
     * * @param centros O conjunto de k centros a ser avaliado.
     * 
     * @return O raio (custo) desta solução.
     */
    private long calcularRaio(int[] centros) {
        long raioMaximoDaSolucao = 0;

        // Itera por CADA vértice 'v' do grafo (de 0 a V-1)
        for (int v = 0; v < V; v++) {

            // Encontra a distância de 'v' até o centro MAIS PRÓXIMO
            long distanciaMinimaCentro = Long.MAX_VALUE;

            for (int centro : centros) {
                long dist = distancias[v][centro];
                if (dist < distanciaMinimaCentro) {
                    distanciaMinimaCentro = dist;
                }
            }

            // O raio da solução é determinado pelo vértice "pior caso",
            // ou seja, o que está mais longe de seu centro mais próximo.
            if (distanciaMinimaCentro > raioMaximoDaSolucao) {
                raioMaximoDaSolucao = distanciaMinimaCentro;
            }
        }

        return raioMaximoDaSolucao;
    }

}