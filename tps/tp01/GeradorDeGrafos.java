package tps.tp01;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Classe responsável por gerar grafos aleatórios para os experimentos.
 * Gera 3 variações (não euleriano, semi-euleriano, euleriano) para
 * cada tamanho de grafo especificado.
 */
public class GeradorDeGrafos {

    /**
     * Gera um grafo aleatório, garantindo que ele seja conexo.
     * 
     * @param numVertices      O número de vértices do grafo.
     * @param numArestasExtras O número de arestas aleatórias a serem adicionadas
     *                         além do mínimo para conectividade.
     * @return Um objeto Grafo.
     */
    public static Grafo gerarGrafoAleatorioConectado(int numVertices, int numArestasExtras) {
        Grafo grafo = new Grafo(numVertices);
        Random random = new Random();
        Set<String> arestasExistentes = new HashSet<>();

        // 1. Garantir conectividade criando um caminho que passa por todos os vértices
        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            vertices.add(i);
        }
        Collections.shuffle(vertices); // Embaralha para não ser um caminho linear simples
        for (int i = 0; i < numVertices - 1; i++) {
            int u = vertices.get(i);
            int v = vertices.get(i + 1);
            grafo.adicionarAresta(u, v);
            arestasExistentes.add(Math.min(u, v) + "-" + Math.max(u, v));
        }

        // 2. Adicionar arestas aleatórias extras
        int arestasAdicionadas = 0;
        while (arestasAdicionadas < numArestasExtras) {
            int u = random.nextInt(numVertices);
            int v = random.nextInt(numVertices);
            String arestaKey = Math.min(u, v) + "-" + Math.max(u, v);

            // Evitar auto-loops e arestas duplicadas
            if (u != v && !arestasExistentes.contains(arestaKey)) {
                grafo.adicionarAresta(u, v);
                arestasExistentes.add(arestaKey);
                arestasAdicionadas++;
            }
        }
        return grafo;
    }

    // Copiamos estes métodos de AnalisadorDeGrafo para conveniência
    public static List<Integer> encontrarVerticesGrauImpar(Grafo grafo) {
        List<Integer> impares = new ArrayList<>();
        for (int i = 0; i < grafo.getV(); i++) {
            if (grafo.getAdj()[i].size() % 2 != 0) {
                impares.add(i);
            }
        }
        return impares;
    }

    public static void salvarGrafoParaArquivo(Grafo grafo, String caminhoArquivo) {
        try (PrintWriter writer = new PrintWriter(caminhoArquivo)) {
            int numArestas = 0;
            for (int i = 0; i < grafo.getV(); i++) {
                for (int vizinho : grafo.getAdj()[i]) {
                    if (i < vizinho)
                        numArestas++;
                }
            }
            writer.println(grafo.getV() + " " + numArestas);
            for (int i = 0; i < grafo.getV(); i++) {
                for (int vizinho : grafo.getAdj()[i]) {
                    if (i < vizinho) {
                        writer.println((i + 1) + " " + (vizinho + 1));
                    }
                }
            }
            System.out.println(" -> Grafo salvo com sucesso em: " + caminhoArquivo);
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar o arquivo do grafo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int[] tamanhos = { 100, 1000, 10000, 100000 };

        for (int V : tamanhos) {
            System.out.println("\n--- GERANDO GRAFOS COM " + V + " VÉRTICES ---");
            int E_extras = V * 3; // Densidade de arestas razoável

            // 1. Gerar e salvar o grafo Não Euleriano
            System.out.println("Gerando grafo Não Euleriano...");
            Grafo naoEuleriano = gerarGrafoAleatorioConectado(V, E_extras);
            salvarGrafoParaArquivo(naoEuleriano, "grafo_" + V + "_nao_euleriano.txt");

            // 2. Ajustar e salvar o grafo Semi-Euleriano
            System.out.println("Gerando grafo Semi-Euleriano...");
            Grafo semiEuleriano = naoEuleriano.copiar();
            List<Integer> imparesSemi = encontrarVerticesGrauImpar(semiEuleriano);
            for (int i = 0; i < imparesSemi.size() - 2; i += 2) { // Deixa os dois últimos
                semiEuleriano.adicionarAresta(imparesSemi.get(i), imparesSemi.get(i + 1));
            }
            salvarGrafoParaArquivo(semiEuleriano, "grafo_" + V + "_semi_euleriano.txt");

            // 3. Ajustar e salvar o grafo Euleriano
            System.out.println("Gerando grafo Euleriano...");
            Grafo euleriano = naoEuleriano.copiar();
            List<Integer> imparesEuler = encontrarVerticesGrauImpar(euleriano);
            for (int i = 0; i < imparesEuler.size(); i += 2) { // Conecta todos os pares
                euleriano.adicionarAresta(imparesEuler.get(i), imparesEuler.get(i + 1));
            }
            salvarGrafoParaArquivo(euleriano, "grafo_" + V + "_euleriano.txt");
        }
        System.out.println("\n--- Geração de todos os grafos concluída! ---");
    }
}