package tps.tp01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * Classe principal para carregar um grafo de um arquivo e, futuramente,
 * executar os algoritmos de análise (Pontes, Caminho Euleriano).
 */
public class AnalisadorDeGrafo {

    // --- Variáveis de instância para o algoritmo de Tarjan ---
    private static int tempo;
    private static int[] low;
    private static int[] discovery;
    private static boolean[] visitados;
    private static List<int[]> pontes;

    public static Grafo carregarDeArquivo(String caminhoArquivo) throws FileNotFoundException {
        File arquivo = new File(caminhoArquivo);
        Scanner scanner = new Scanner(arquivo);
        int numVertices = scanner.nextInt();
        int numArestas = scanner.nextInt();
        Grafo grafo = new Grafo(numVertices);
        System.out.println("Lendo grafo com " + numVertices + " vértices e " + numArestas + " arestas.");
        while (scanner.hasNextInt()) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            grafo.adicionarAresta(u - 1, v - 1);
        }
        scanner.close();
        System.out.println("Grafo carregado com sucesso!");
        return grafo;
    }

    public static List<Integer> encontrarCaminhoEulerianoFleury(Grafo grafo, boolean usarTarjan) {
        List<Integer> impares = encontrarVerticesGrauImpar(grafo);
        if (impares.size() > 2) {
            System.out.println("O grafo não é Euleriano nem Semi-Euleriano.");
            return new ArrayList<>();
        }

        Grafo grafoCopia = grafo.copiar();
        int u = 0;
        if (!impares.isEmpty()) {
            u = impares.get(0);
        } else {
            for (int i = 0; i < grafoCopia.getV(); i++) {
                if (!grafoCopia.getAdj()[i].isEmpty()) {
                    u = i;
                    break;
                }
            }
        }

        List<Integer> caminho = new ArrayList<>();
        caminho.add(u);

        while (true) {
            List<Integer> vizinhos = grafoCopia.getAdj()[u];
            if (vizinhos.isEmpty())
                break;

            int v = -1;
            if (vizinhos.size() == 1) {
                v = vizinhos.get(0);
            } else {
                // CORREÇÃO: Itera sobre uma cópia da lista de vizinhos
                for (int vizinhoCandidato : new ArrayList<>(vizinhos)) {
                    if (!ehPonte(u, vizinhoCandidato, grafoCopia)) {
                        v = vizinhoCandidato;
                        break;
                    }
                }
                if (v == -1)
                    v = vizinhos.get(0);
            }
            grafoCopia.removerAresta(u, v);
            caminho.add(v);
            u = v;
        }
        return caminho;
    }

    // MÉTODO ehPonte CORRIGIDO E UNIFICADO
    private static boolean ehPonte(int u, int v, Grafo grafo) {
        grafo.removerAresta(u, v);
        boolean desconectado = !ehConexo(grafo);
        grafo.adicionarAresta(u, v);
        return desconectado;
    }

    public static List<Integer> encontrarVerticesGrauImpar(Grafo grafo) {
        List<Integer> impares = new ArrayList<>();
        for (int i = 0; i < grafo.getV(); i++) {
            if (grafo.getAdj()[i].size() % 2 != 0) {
                impares.add(i);
            }
        }
        return impares;
    }

    // Substitua o seu dfsUtil recursivo por este dfsUtil iterativo
    private static void dfsUtil(int v, boolean[] visitados, Grafo grafo) {
        Stack<Integer> pilha = new Stack<>();
        pilha.push(v);

        while (!pilha.isEmpty()) {
            int atual = pilha.pop();

            if (!visitados[atual]) {
                visitados[atual] = true;

                for (int vizinho : grafo.getAdj()[atual]) {
                    if (!visitados[vizinho]) {
                        pilha.push(vizinho);
                    }
                }
            }
        }
    }

    private static boolean ehConexo(Grafo grafo) {
        boolean[] visitados = new boolean[grafo.getV()];
        int verticeInicial = -1;
        for (int i = 0; i < grafo.getV(); i++) {
            if (!grafo.getAdj()[i].isEmpty()) {
                verticeInicial = i;
                break;
            }
        }
        if (verticeInicial == -1)
            return true;
        dfsUtil(verticeInicial, visitados, grafo);
        for (int i = 0; i < grafo.getV(); i++) {
            if (!grafo.getAdj()[i].isEmpty() && !visitados[i])
                return false;
        }
        return true;
    }

    /**
     * Encontra todas as pontes em um grafo usando o método naïve.
     * O método testa a remoção de cada aresta e verifica se o grafo se torna
     * desconexo.
     *
     * @param grafo O grafo a ser analisado.
     * @return Uma lista de pares de inteiros, onde cada par representa uma ponte.
     */
    public static List<int[]> encontrarPontesNaive(Grafo grafo) {
        List<int[]> pontes = new ArrayList<>();

        // 1. Itera por todos os vértices
        for (int u = 0; u < grafo.getV(); u++) {

            // --- CORREÇÃO AQUI ---
            // Criamos uma cópia da lista de vizinhos para evitar
            // ConcurrentModificationException.
            // Agora, iteramos sobre a cópia, enquanto modificamos a original.
            List<Integer> vizinhos = new ArrayList<>(grafo.getAdj()[u]);

            // 2. Itera pelos vizinhos (usando a cópia)
            for (int v : vizinhos) {
                // Para evitar analisar a mesma aresta duas vezes (u,v) e (v,u)
                if (u < v) {
                    // 3. Remove a aresta (u, v) temporariamente
                    grafo.removerAresta(u, v);

                    // 4. Verifica se o grafo ficou desconexo
                    if (!ehConexo(grafo)) {
                        // 5. Se ficou, a aresta é uma ponte
                        pontes.add(new int[] { u, v });
                    }

                    // 6. Adiciona a aresta de volta para o próximo teste
                    grafo.adicionarAresta(u, v);
                }
            }
        }
        return pontes;
    }

    /**
     * Encontra todas as pontes em um grafo usando o algoritmo de Tarjan.
     * Este método inicializa as estruturas de dados e inicia a busca DFS.
     *
     * @param grafo O grafo a ser analisado.
     * @return Uma lista de pares de inteiros, onde cada par representa uma ponte.
     */
    public static List<int[]> encontrarPontesTarjan(Grafo grafo) {
        int V = grafo.getV();

        // Inicializa as estruturas de dados
        tempo = 0;
        pontes = new ArrayList<>();
        low = new int[V];
        discovery = new int[V];
        visitados = new boolean[V];

        // Itera por todos os vértices para garantir que todos os componentes do grafo
        // sejam visitados
        // (importante se o grafo não for conexo)
        for (int i = 0; i < V; i++) {
            if (!visitados[i]) {
                dfsTarjan(i, -1, grafo); // O pai do vértice inicial é -1 (nenhum)
            }
        }
        return pontes;
    }

    /**
     * Função recursiva da DFS para o algoritmo de Tarjan.
     *
     * @param u     O vértice atual.
     * @param pai   O pai do vértice 'u' na árvore da DFS.
     * @param grafo O grafo.
     */
    private static void dfsTarjan(int u, int pai, Grafo grafo) {
        visitados[u] = true;
        discovery[u] = low[u] = ++tempo; // Define o tempo de descoberta e o low-link inicial

        // Percorre todos os vizinhos do vértice u
        for (int v : grafo.getAdj()[u]) {
            // Se o vizinho 'v' é o pai, ignora-o
            if (v == pai) {
                continue;
            }

            if (visitados[v]) {
                // Se o vizinho 'v' já foi visitado e não é o pai,
                // encontramos uma aresta de retorno (back edge).
                // Atualizamos o low-link de 'u' com o tempo de descoberta de 'v'.
                low[u] = Math.min(low[u], discovery[v]);
            } else {
                // Se o vizinho 'v' não foi visitado, ele é um descendente na árvore da DFS.
                // Chamamos a recursão para 'v'.
                dfsTarjan(v, u, grafo);

                // Após a chamada recursiva, atualizamos o low-link de 'u'.
                // 'u' pode alcançar tudo que 'v' alcança.
                low[u] = Math.min(low[u], low[v]);

                // --- A CONDIÇÃO DA PONTE ---
                // Se o menor tempo alcançável por 'v' for maior que o tempo de descoberta de
                // 'u',
                // então a aresta (u, v) é uma ponte.
                if (low[v] > discovery[u]) {
                    pontes.add(new int[] { u, v });
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Teste com um grafo que sabemos ser semi-euleriano (criado anteriormente)
            String caminho = "C:/Users/USER/Documents/GitHub/Grafos/tps/tp01/grafo_100000_semi_euleriano.txt";
            Grafo meuGrafo = carregarDeArquivo(caminho);

            // --- Estratégia 1: Fleury com Detecção Naive ---
            System.out.println("\n--- Fleury com Detecção Naive ---");

            // Classifica o grafo primeiro
            List<Integer> imparesNaive = encontrarVerticesGrauImpar(meuGrafo);
            String tipoGrafoNaive = "Não Euleriano";
            if (imparesNaive.size() == 0) {
                tipoGrafoNaive = "Euleriano";
            } else if (imparesNaive.size() == 2) {
                tipoGrafoNaive = "Semi-Euleriano";
            }
            System.out.println("Classificação do Grafo: " + tipoGrafoNaive);

            long startTimeNaive = System.currentTimeMillis();
            List<Integer> caminhoNaive = encontrarCaminhoEulerianoFleury(meuGrafo.copiar(), false);
            long endTimeNaive = System.currentTimeMillis();

            System.out.println("Busca finalizada em " + (endTimeNaive - startTimeNaive) + " ms.");
            if (caminhoNaive.isEmpty()) {
                System.out.println("Caminho euleriano não existe.");
            } else {
                System.out.println("Caminho encontrado (" + (caminhoNaive.size() - 1) + " arestas): " );
            }

            // --- Estratégia 2: Fleury com Detecção "Tarjan" ---
            System.out.println("\n--- Fleury com Detecção por Tarjan ---");

            // Classifica o grafo novamente para esta seção
            List<Integer> imparesTarjan = encontrarVerticesGrauImpar(meuGrafo);
            String tipoGrafoTarjan = "Não Euleriano";
            if (imparesTarjan.size() == 0) {
                tipoGrafoTarjan = "Euleriano";
            } else if (imparesTarjan.size() == 2) {
                tipoGrafoTarjan = "Semi-Euleriano";
            }
            System.out.println("Classificação do Grafo: " + tipoGrafoTarjan);

            long startTimeTarjan = System.currentTimeMillis();
            List<Integer> caminhoTarjan = encontrarCaminhoEulerianoFleury(meuGrafo.copiar(), true);
            long endTimeTarjan = System.currentTimeMillis();

            System.out.println("Busca finalizada em " + (endTimeTarjan - startTimeTarjan) + " ms.");
            if (caminhoTarjan.isEmpty()) {
                System.out.println("Caminho euleriano não existe.");
            } else {
                System.out.println("Caminho encontrado (" + (caminhoTarjan.size() - 1) + " arestas): ");
            }

        } catch (FileNotFoundException e) {
            System.err.println("Erro: O arquivo do grafo não foi encontrado.");
        }
    }
}

/*
 * TESTES:
 * try {
 * // Substitua "caminho/para/seu/arquivo.txt" pelo caminho real do arquivo
 * String caminho =
 * "C:/Users/USER/Documents/GitHub/Grafos/tps/tp01/graph-test-100-1.txt";
 * Grafo meuGrafo = carregarDeArquivo(caminho);
 * 
 * // Exemplo de verificação: imprimir o número de vizinhos do primeiro vértice
 * // (vértice 1 no arquivo)
 * System.out.println("O vértice 1 (índice 0) tem " +
 * meuGrafo.getAdj()[0].size() + " vizinhos.");
 * 
 * } catch (FileNotFoundException e) {
 * System.err.println("Erro: O arquivo do grafo não foi encontrado.");
 * e.printStackTrace();
 * }
 * 
 * 
 * 
 * // --- Teste com Método Naive ---
 * System.out.println("\n--- Método Naive ---");
 * long startTimeNaive = System.currentTimeMillis();
 * List<int[]> pontesNaive = encontrarPontesNaive(meuGrafo.copiar()); // Usamos
 * uma cópia, pois o método Naive
 * // modifica o grafo
 * long endTimeNaive = System.currentTimeMillis();
 * System.out.println("Busca finalizada em " + (endTimeNaive - startTimeNaive) +
 * " ms.");
 * System.out.println("Pontes encontradas: " + pontesNaive.size());
 * 
 * // --- Teste com Algoritmo de Tarjan ---
 * System.out.println("\n--- Algoritmo de Tarjan ---");
 * long startTimeTarjan = System.currentTimeMillis();
 * List<int[]> pontesTarjan = encontrarPontesTarjan(meuGrafo);
 * long endTimeTarjan = System.currentTimeMillis();
 * System.out.println("Busca finalizada em " + (endTimeTarjan - startTimeTarjan)
 * + " ms.");
 * System.out.println("Pontes encontradas: " + pontesTarjan.size());
 * 
 * // Opcional: imprimir as pontes encontradas por Tarjan
 * // for (int[] ponte : pontesTarjan) {
 * // System.out.println("Aresta: (" + (ponte[0] + 1) + ", " + (ponte[1] + 1) +
 * // ")");
 * // }
 */
