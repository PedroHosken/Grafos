package Implement.Im01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Representa um grafo direcionado usando a estrutura "Forward Star" e "Backward
 * Star".
 * Esta abordagem é extremamente eficiente em memória, utilizando arrays de
 * inteiros
 * para armazenar a topologia do grafo.
 * - Forward Star (pointerSucessores, arcoDestino) para sucessores.
 * - Backward Star (pointerPredecessores, arcoOrigem) para predecessores.
 */
public class Grafo {

    private int numVertices;
    private int numArestas;

    // --- Estrutura Forward Star (para sucessores) ---
    // Vetor de ponteiros: pointerSucessores[i] indica o início dos sucessores do
    // vértice i em arcoDestino.
    private int[] pointerSucessores;
    // Vetor de arcos de destino.
    private int[] arcoDestino;

    // --- Estrutura Backward Star (para predecessores) ---
    // Vetor de ponteiros: pointerPredecessores[i] indica o início dos predecessores
    // do vértice i em arcoOrigem.
    private int[] pointerPredecessores;
    // Vetor de arcos de origem (os predecessores).
    private int[] arcoOrigem;

    public Grafo() {
        // Construtor vazio, padrão
    }

    /**
     * Lê um arquivo de grafo e popula as estruturas de dados.
     * O método primeiro lê todas as arestas para a memória, depois constrói
     * as estruturas Forward e Backward Star em tempo linear (O(n+m)).
     * 
     * @param nomeArquivo O caminho para o arquivo do grafo.
     * @throws FileNotFoundException se o arquivo não for encontrado.
     */
    public void carregarDeArquivo(String nomeArquivo) throws FileNotFoundException {
        File arquivo = new File(nomeArquivo);
        Scanner scanner = new Scanner(arquivo);

        this.numVertices = scanner.nextInt();
        this.numArestas = scanner.nextInt();

        // 1. Ler todas as arestas para uma lista temporária para processamento.
        List<int[]> arestas = new ArrayList<>(this.numArestas);
        while (scanner.hasNextInt()) {
            arestas.add(new int[] { scanner.nextInt(), scanner.nextInt() });
        }
        scanner.close();

        // --- Construção da Estrutura Forward Star (Sucessores) ---
        this.pointerSucessores = new int[this.numVertices + 2];
        this.arcoDestino = new int[this.numArestas + 1];

        // Contagem do grau de saída de cada vértice
        // Usamos o futuro array de ponteiros para armazenar a contagem temporariamente
        for (int[] aresta : arestas) {
            // A posição i+1 armazena a contagem do vértice i
            this.pointerSucessores[aresta[0] + 1]++;
        }

        // Construção do vetor de ponteiros com base nos graus (soma cumulativa)
        this.pointerSucessores[1] = 1;
        for (int i = 2; i <= this.numVertices + 1; i++) {
            this.pointerSucessores[i] += this.pointerSucessores[i - 1];
        }

        // Preenchimento do vetor de destinos
        // É preciso uma cópia dos ponteiros para saber a próxima posição vaga de cada
        // vértice
        int[] ponteiroCopia = new int[this.numVertices + 2];
        System.arraycopy(this.pointerSucessores, 0, ponteiroCopia, 0, this.numVertices + 1);

        for (int[] aresta : arestas) {
            int u = aresta[0];
            int v = aresta[1];
            int pos = ponteiroCopia[u];
            this.arcoDestino[pos] = v;
            ponteiroCopia[u]++;
        }

        // --- Construção da Estrutura Backward Star (Predecessores) ---
        this.pointerPredecessores = new int[this.numVertices + 2];
        this.arcoOrigem = new int[this.numArestas + 1];

        // Contagem do grau de entrada de cada vértice
        for (int[] aresta : arestas) {
            this.pointerPredecessores[aresta[1] + 1]++;
        }

        // Construção do vetor de ponteiros para predecessores
        this.pointerPredecessores[1] = 1;
        for (int i = 2; i <= this.numVertices + 1; i++) {
            this.pointerPredecessores[i] += this.pointerPredecessores[i - 1];
        }

        // Preenchimento do vetor de origens (predecessores)
        System.arraycopy(this.pointerPredecessores, 0, ponteiroCopia, 0, this.numVertices + 1);

        for (int[] aresta : arestas) {
            int u = aresta[0];
            int v = aresta[1];
            int pos = ponteiroCopia[v];
            this.arcoOrigem[pos] = u;
            ponteiroCopia[v]++;
        }
    }

    /**
     * Verifica se um vértice está dentro do intervalo válido do grafo.
     */
    public boolean contemVertice(int vertice) {
        return vertice >= 1 && vertice <= this.numVertices;
    }

    /**
     * Retorna o grau de saída de um vértice.
     * Complexidade: O(1)
     */
    public int getGrauSaida(int vertice) {
        if (!contemVertice(vertice))
            return 0;
        return pointerSucessores[vertice + 1] - pointerSucessores[vertice];
    }

    /**
     * Retorna o grau de entrada de um vértice.
     * Complexidade: O(1)
     */
    public int getGrauEntrada(int vertice) {
        if (!contemVertice(vertice))
            return 0;
        return pointerPredecessores[vertice + 1] - pointerPredecessores[vertice];
    }

    /**
     * Retorna a lista de sucessores de um vértice.
     * Complexidade: O(grau de saída)
     */
    public List<Integer> getSucessores(int vertice) {
        if (!contemVertice(vertice))
            return Collections.emptyList();

        int start = pointerSucessores[vertice];
        int end = pointerSucessores[vertice + 1];
        List<Integer> sucessores = new ArrayList<>(end - start);

        for (int i = start; i < end; i++) {
            sucessores.add(arcoDestino[i]);
        }
        return sucessores;
    }

    /**
     * Retorna a lista de predecessores de um vértice.
     * Complexidade: O(grau de entrada)
     */
    public List<Integer> getPredecessores(int vertice) {
        if (!contemVertice(vertice))
            return Collections.emptyList();

        int start = pointerPredecessores[vertice];
        int end = pointerPredecessores[vertice + 1];
        List<Integer> predecessores = new ArrayList<>(end - start);

        for (int i = start; i < end; i++) {
            predecessores.add(arcoOrigem[i]);
        }
        return predecessores;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Solicitar informações do usuário
            // 1System.out.print("Digite o nome do arquivo do grafo (ex:
            // graph-test-100-1.txt): ");
            String nomeArquivo = "C:/Users/USER/Documents/GitHub/Grafos/Implement/Im01/graph-test-50000-1.txt";
            // "C:/Users/USER/Documents/GitHub/Grafos/Implement/Im01/graph-test-100-1.txt";
            // - certo

            System.out.print("Digite o número do vértice para análise: ");
            int verticeAnalisado = scanner.nextInt();

            // 2. Criar e carregar o grafo
            System.out.println("\nCarregando o grafo do arquivo... Por favor, aguarde.");
            Grafo grafo = new Grafo();
            grafo.carregarDeArquivo(nomeArquivo);
            System.out.println("Grafo carregado com sucesso!");

            // 3. Validar se o vértice existe
            if (!grafo.contemVertice(verticeAnalisado)) {
                System.out.println("\nErro: O vértice " + verticeAnalisado + " não existe no grafo.");
                scanner.close();
                return;
            }

            // 4. Obter e exibir as informações
            System.out.println("\n--- Análise para o Vértice " + verticeAnalisado + " ---");

            // Grau de Saída
            System.out.println("Grau de Saída: " + grafo.getGrauSaida(verticeAnalisado));

            // Grau de Entrada
            System.out.println("Grau de Entrada: " + grafo.getGrauEntrada(verticeAnalisado));

            // Conjunto de Sucessores
            // O String.join é uma forma elegante de formatar a lista para impressão
            String sucessores = String.join(", ",
                    grafo.getSucessores(verticeAnalisado).stream().map(String::valueOf).toArray(String[]::new));
            System.out.println("Conjunto de Sucessores: {" + sucessores + "}");

            // Conjunto de Predecessores
            String predecessores = String.join(", ",
                    grafo.getPredecessores(verticeAnalisado).stream().map(String::valueOf).toArray(String[]::new));
            System.out.println("Conjunto de Predecessores: {" + predecessores + "}");

        } catch (FileNotFoundException e) {
            System.err.println("\nErro: Arquivo não encontrado. Verifique o nome e o caminho do arquivo.");
        } catch (InputMismatchException e) {
            System.err.println("\nErro: O número do vértice deve ser um inteiro válido.");
        } catch (Exception e) {
            System.err.println("\nOcorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
