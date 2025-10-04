package tps.tp01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    /**
     * Carrega um grafo a partir de um arquivo de texto.
     * O formato esperado é:
     * - A primeira linha contém o número de vértices (V) e o número de arestas (E).
     * - As linhas seguintes contêm pares de inteiros representando as arestas.
     *
     * @param caminhoArquivo O caminho para o arquivo do grafo.
     * @return Um objeto Grafo preenchido com os dados do arquivo.
     * @throws FileNotFoundException Se o arquivo não for encontrado.
     */
    public static Grafo carregarDeArquivo(String caminhoArquivo) throws FileNotFoundException {
        File arquivo = new File(caminhoArquivo);
        Scanner scanner = new Scanner(arquivo);

        // Lê a primeira linha para obter o número de vértices e arestas
        int numVertices = scanner.nextInt();
        int numArestas = scanner.nextInt();

        // Cria o objeto Grafo com o número de vértices lido
        Grafo grafo = new Grafo(numVertices);

        System.out.println("Lendo grafo com " + numVertices + " vértices e " + numArestas + " arestas.");

        // Itera sobre as arestas restantes no arquivo
        while (scanner.hasNextInt()) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();

            // Adiciona a aresta ao grafo, ajustando para o índice 0.
            // Vertice 1 no arquivo se torna vértice 0 no nosso grafo.
            grafo.adicionarAresta(u - 1, v - 1);
        }

        scanner.close();
        System.out.println("Grafo carregado com sucesso!");
        return grafo;

    }

    public static List<Integer> encontrarCaminhoEulerianoFleury(Grafo grafo, boolean usarTarjan) {
        // 1. Verificar as condições
        List<Integer> impares = encontrarVerticesGrauImpar(grafo);
        if (impares.size() != 0 && impares.size() != 2) {
            System.out.println(
                    "O grafo não é Euleriano nem Semi-Euleriano. Número de vértices ímpares: " + impares.size());
            return new ArrayList<>();
        }

        Grafo grafoCopia = grafo.copiar();

        // Contar o total de arestas para o nosso print de depuração
        int totalArestas = 0;
        for (int i = 0; i < grafo.getV(); i++) {
            totalArestas += grafo.getAdj()[i].size();
        }
        totalArestas /= 2; // Como o grafo é não-direcionado, cada aresta é contada duas vezes

        // 2. Determinar o vértice inicial
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

        // 3. Construir o caminho
        List<Integer> caminho = new ArrayList<>();
        caminho.add(u);

        int arestasProcessadas = 0;
        while (true) {
            // --- LINHA DE DEPURAÇÃO ADICIONADA ---
            // Imprime o progresso a cada 10 arestas processadas
            if (arestasProcessadas % 10 == 0) {
                System.out.println(
                        "Progresso: " + arestasProcessadas + " de " + totalArestas + " arestas encontradas...");
            }
            // ------------------------------------

            List<Integer> vizinhos = grafoCopia.getAdj()[u];
            if (vizinhos.isEmpty()) {
                break;
            }

            int v = -1;

            if (vizinhos.size() == 1) {
                v = vizinhos.get(0);
            } else {
                for (int vizinhoCandidato : vizinhos) {
                    if (!ehPonte(u, vizinhoCandidato, grafoCopia, usarTarjan)) {
                        v = vizinhoCandidato;
                        break;
                    }
                }
                if (v == -1) {
                    v = vizinhos.get(0);
                }
            }

            grafoCopia.removerAresta(u, v);
            caminho.add(v);
            u = v;
            arestasProcessadas++;
        }

        System.out.println("Progresso: " + arestasProcessadas + " de " + totalArestas + " arestas encontradas..."); // Print
                                                                                                                    // final
        return caminho;
    }

    /**
     * Verifica se uma aresta (u, v) é uma ponte no grafo atual.
     * Encapsula a chamada para o método de verificação de conectividade.
     * O parâmetro usarTarjan é mantido para consistência, mas a verificação
     * de conectividade é a mais eficiente aqui.
     */
    private static boolean ehPonte(int u, int v, Grafo grafo, boolean usarTarjan) {
        // A lógica é a mesma, independentemente do método original de encontrar todas
        // as pontes.
        // O teste de ponte para Fleury é sempre sobre o estado ATUAL do grafo.
        grafo.removerAresta(u, v);
        boolean desconectado = !ehConexo(grafo);
        grafo.adicionarAresta(u, v); // Adiciona a aresta de volta
        return desconectado;
    }

    // Adicione o método `encontrarVerticesGrauImpar` se ainda não o tiver
    public static List<Integer> encontrarVerticesGrauImpar(Grafo grafo) {
        List<Integer> impares = new ArrayList<>();
        for (int i = 0; i < grafo.getV(); i++) {
            if (grafo.getAdj()[i].size() % 2 != 0) {
                impares.add(i);
            }
        }
        return impares;
    }

    /**
     * Método auxiliar recursivo para a Busca em Profundidade (DFS).
     *
     * @param v         O vértice atual da busca.
     * @param visitados Um array de booleanos para marcar os vértices já visitados.
     * @param grafo     O grafo no qual a busca é executada.
     */
    private static void dfsUtil(int v, boolean[] visitados, Grafo grafo) {
        // Marca o vértice atual como visitado
        visitados[v] = true;

        // Percorre todos os vizinhos do vértice atual
        for (int vizinho : grafo.getAdj()[v]) {
            // Se o vizinho ainda não foi visitado, chama a recursão para ele
            if (!visitados[vizinho]) {
                dfsUtil(vizinho, visitados, grafo);
            }
        }
    }

    /**
     * Verifica se o grafo é conectado.
     * Utiliza uma DFS para contar quantos vértices são alcançáveis a partir de um
     * vértice inicial.
     *
     * @param grafo O grafo a ser verificado.
     * @return true se o grafo for conectado, false caso contrário.
     */
    private static boolean ehConexo(Grafo grafo) {
        // Array para marcar os vértices visitados na DFS
        boolean[] visitados = new boolean[grafo.getV()];

        // Encontra o primeiro vértice com algum vizinho para iniciar a busca.
        // Isso lida com o caso de vértices isolados.
        int verticeInicial = -1;
        for (int i = 0; i < grafo.getV(); i++) {
            if (!grafo.getAdj()[i].isEmpty()) {
                verticeInicial = i;
                break;
            }
        }

        // Se não houver arestas no grafo, consideramos conectado se tiver 0 ou 1
        // vértice.
        if (verticeInicial == -1) {
            return grafo.getV() <= 1;
        }

        // Inicia a DFS a partir do vértice inicial
        dfsUtil(verticeInicial, visitados, grafo);

        // Conta quantos vértices foram visitados
        int verticesVisitados = 0;
        for (int i = 0; i < grafo.getV(); i++) {
            // Só contamos os vértices que fazem parte do componente principal (têm arestas)
            if (visitados[i] && !grafo.getAdj()[i].isEmpty()) {
                verticesVisitados++;
            }
        }

        int totalVerticesComArestas = 0;
        for (int i = 0; i < grafo.getV(); i++) {
            if (!grafo.getAdj()[i].isEmpty()) {
                totalVerticesComArestas++;
            }
        }

        // Se o número de vértices visitados for igual ao total de vértices
        // que têm arestas, o grafo está conectado.
        return verticesVisitados == totalVerticesComArestas;
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
            String caminho = "C:/Users/USER/Documents/GitHub/Grafos/tps/tp01/grafo_100_euleriano.txt";
            Grafo meuGrafo = carregarDeArquivo(caminho);

            System.out.println("\n--- Fleury com Detecção Naive ---");
            long startTimeNaive = System.currentTimeMillis();
            List<Integer> caminhoNaive = encontrarCaminhoEulerianoFleury(meuGrafo, false);
            long endTimeNaive = System.currentTimeMillis();
            System.out.println("Busca finalizada em " + (endTimeNaive - startTimeNaive) + " ms.");
            // Imprime o caminho de forma invertida, pois a recursão o constrói ao contrário
            // Collections.reverse(caminhoNaive);
            // System.out.println("Caminho encontrado ("+caminhoNaive.size()+" arestas): " +
            // caminhoNaive);

            System.out.println("\n--- Fleury com Detecção por Tarjan ---");
            long startTimeTarjan = System.currentTimeMillis();
            List<Integer> caminhoTarjan = encontrarCaminhoEulerianoFleury(meuGrafo, true);
            long endTimeTarjan = System.currentTimeMillis();
            System.out.println("Busca finalizada em " + (endTimeTarjan - startTimeTarjan) + " ms.");
            // Collections.reverse(caminhoTarjan);
            // System.out.println("Caminho encontrado ("+caminhoTarjan.size()+" arestas): "
            // + caminhoTarjan);

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
