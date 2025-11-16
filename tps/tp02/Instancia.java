package tps.tp02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Esta classe representa uma instância do problema, lida a partir
 * de um arquivo de formato pmed.
 * * Ela armazena o número de vértices (V), o número de centros (k)
 * e a matriz de distâncias de menor caminho (distancias).
 */
public class Instancia {

    private int V; // Número de vértices
    private int k; // Número de centros
    private int[][] distancias; // Matriz de distâncias (grafo completo)

    // --- Construtor e Getters ---

    /**
     * Construtor que lê e processa o arquivo.
     * 
     * @param caminhoArquivo O nome do arquivo (ex: "pmed1.txt")
     */
    public Instancia(String caminhoArquivo) {
        try {
            lerEProcessarArquivo(caminhoArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + caminhoArquivo);
            e.printStackTrace();
        }
    }

    public int getV() {
        return V;
    }

    public int getK() {
        return k;
    }

    /**
     * Retorna a matriz de distâncias de menor caminho (pós-Floyd-Warshall).
     * 
     * @return uma matriz V x V
     */
    public int[][] getDistancias() {
        return distancias;
    }

    // --- Lógica Principal de Leitura e Processamento ---

    private void lerEProcessarArquivo(String caminhoArquivo) throws IOException {
        FileReader fileReader = new FileReader(caminhoArquivo);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // 1. Ler a primeira linha
        String linha = bufferedReader.readLine();
        String[] cabecalho = linha.trim().split("\\s+");

        this.V = Integer.parseInt(cabecalho[0]);
        int numArestas = Integer.parseInt(cabecalho[1]);
        this.k = Integer.parseInt(cabecalho[2]);

        // 2. Inicializar a matriz de distâncias
        // Usamos V (número de vértices). Os vértices nos arquivos são 1-indexados,
        // mas em Java usaremos 0-indexado (0 a V-1).
        this.distancias = new int[V][V];

        // Valor "infinito" para representar ausência de aresta.
        // Usamos (Integer.MAX_VALUE / 2) para evitar overflow durante a soma
        // no Floyd-Warshall.
        int infinito = Integer.MAX_VALUE / 2;

        for (int i = 0; i < V; i++) {
            Arrays.fill(distancias[i], infinito);
            distancias[i][i] = 0; // Distância de um nó para ele mesmo é 0
        }

        // 3. Ler as arestas do arquivo
        for (int i = 0; i < numArestas; i++) {
            linha = bufferedReader.readLine();
            String[] aresta = linha.trim().split("\\s+");

            // Os arquivos são 1-indexados (vértices de 1 a V).
            // Nosso array é 0-indexado (índices de 0 a V-1).
            // Por isso, subtraímos 1.
            int u = Integer.parseInt(aresta[0]) - 1;
            int v = Integer.parseInt(aresta[1]) - 1;
            int custo = Integer.parseInt(aresta[2]);

            // Grafo não-direcionado
            distancias[u][v] = custo;
            distancias[v][u] = custo;
        }

        bufferedReader.close();

        // 4. Executar o algoritmo de Floyd-Warshall
        // Isso transforma nossa matriz de "arestas" em uma matriz de "menor caminho"
        // (grafo completo)
        for (int k_fw = 0; k_fw < V; k_fw++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    // Se o caminho i -> k -> j for menor que o caminho direto i -> j
                    if (distancias[i][k_fw] + distancias[k_fw][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k_fw] + distancias[k_fw][j];
                    }
                }
            }
        }
    }

}