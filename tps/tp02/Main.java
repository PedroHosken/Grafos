package tps.tp02;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

/**
 * Classe principal para testar UMA instância (arquivo) de cada vez.
 * * Este programa pede ao usuário o nome do arquivo,
 * carrega-o, e executa as soluções Exata e Aproximada,
 * mostrando uma comparação dos resultados.
 */
public class Main {

    public static void main(String[] args) {
        // Define o Locale para usar ponto (.) como separador decimal no printf
        Locale.setDefault(Locale.US);

        // Objeto para ler a entrada do usuário (nome do arquivo)
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Testador de Instâncias k-Centros ---");
        System.out.print("Digite o nome do arquivo (ex: pmed1.txt): ");
        String nomeArquivo = scanner.nextLine().trim();

        try {
            // Construir o caminho completo do arquivo
            String caminhoArquivo = construirCaminhoArquivo(nomeArquivo);

            // --- 1. Carregar a Instância ---
            System.out.println("\nProcessando arquivo: " + caminhoArquivo + "...");
            Instancia instancia = new Instancia(caminhoArquivo);
            System.out.println("Instância carregada: V=" + instancia.getV() + ", k=" + instancia.getK());
            System.out.println("----------------------------------------------");

            // --- 2. Executar Solução Aproximada ---
            // (Sempre executamos esta, pois é rápida)
            SolucaoAproximada aprox = new SolucaoAproximada(instancia);
            SolucaoAproximada.Resultado resAprox = aprox.resolver();

            System.out.println("\n--- 1. Resultado (Aproximada / Farthest-First) ---");
            System.out.printf("Tempo de Execução: %.4f ms%n", resAprox.tempoExecucaoMs);
            System.out.println("Raio Encontrado:   " + resAprox.raio);
            System.out.println("Centros: " + formatarCentros(resAprox.centros));
            System.out.println("----------------------------------------------");

            // --- 3. Executar Solução Exata (com aviso) ---
            System.out.println("\n--- 2. Resultado (Exata / Força Bruta) ---");
            System.out.println("AVISO: A solução exata (V=" + instancia.getV() + ", k=" + instancia.getK()
                    + ") pode demorar MUITO.");
            System.out.print("Deseja executá-la? (s/n): ");

            String resposta = scanner.nextLine().trim().toLowerCase();

            if (resposta.equals("s")) {
                SolucaoExata exata = new SolucaoExata(instancia);
                SolucaoExata.Resultado resExato = exata.resolver();

                System.out.println("\nSolução Exata Concluída.");
                System.out.printf("Tempo de Execução: %.4f ms%n", resExato.tempoExecucaoMs);
                System.out.println("Raio Encontrado:   " + resExato.raio);
                System.out.println("Centros: " + formatarCentros(resExato.centros));

                // Comparação final
                System.out.println("\n--- Comparação Final ---");
                System.out.printf("Raio (Aproximado): %d (%.4f ms)%n", resAprox.raio, resAprox.tempoExecucaoMs);
                System.out.printf("Raio (Exato):      %d (%.4f ms)%n", resExato.raio, resExato.tempoExecucaoMs);

                if (resExato.raio > 0) {
                    double gap = 100.0 * (resAprox.raio - resExato.raio) / (double) resExato.raio;
                    System.out.printf("Gap (Aprox. vs Exato): %.2f%%%n", gap);
                }

            } else {
                System.out.println("Execução da solução exata pulada.");
            }

            System.out.println("----------------------------------------------");
            System.out.println("Teste concluído.");

        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Constrói o caminho completo do arquivo baseado no diretório do projeto.
     * Se o arquivo já tiver um caminho absoluto ou relativo válido, usa esse.
     * Caso contrário, procura no diretório tps/tp02/.
     * 
     * @param nomeArquivo Nome do arquivo fornecido pelo usuário
     * @return Caminho completo do arquivo
     */
    private static String construirCaminhoArquivo(String nomeArquivo) {
        // Se já for um caminho absoluto ou contém separadores de diretório, usa direto
        if (nomeArquivo.contains(File.separator) || nomeArquivo.contains("/") || nomeArquivo.contains("\\")) {
            // Se começa com drive letter (Windows) ou / (Unix), é absoluto
            if (nomeArquivo.length() > 1 && (nomeArquivo.charAt(1) == ':' || nomeArquivo.startsWith("/"))) {
                return nomeArquivo;
            }
            // Caminho relativo - tenta construir a partir do diretório atual
            File arquivo = new File(nomeArquivo);
            if (arquivo.exists()) {
                return arquivo.getAbsolutePath();
            }
        }

        // Tenta encontrar no diretório tps/tp02/ (diretório padrão)
        // Primeiro tenta a partir do diretório de trabalho atual
        String[] caminhosPossiveis = {
                "tps" + File.separator + "tp02" + File.separator + nomeArquivo,
                "tps/tp02/" + nomeArquivo,
                System.getProperty("user.dir") + File.separator + "tps" + File.separator + "tp02" + File.separator
                        + nomeArquivo
        };

        for (String caminho : caminhosPossiveis) {
            File arquivo = new File(caminho);
            if (arquivo.exists() && arquivo.isFile()) {
                return arquivo.getAbsolutePath();
            }
        }

        // Se não encontrou, tenta no diretório atual
        File arquivoAtual = new File(nomeArquivo);
        if (arquivoAtual.exists()) {
            return arquivoAtual.getAbsolutePath();
        }

        // Se ainda não encontrou, retorna o caminho relativo ao tps/tp02/
        // (o Instancia tentará ler e lançará exceção se não existir)
        return "tps" + File.separator + "tp02" + File.separator + nomeArquivo;
    }

    /**
     * Método auxiliar para formatar o array de centros para impressão.
     * (Converte de 0-indexado para 1-indexado).
     */
    private static String formatarCentros(int[] centros) {
        if (centros == null || centros.length == 0) {
            return "[]";
        }
        // Faz uma cópia para não alterar o array original
        int[] centrosCopia = Arrays.copyOf(centros, centros.length);

        // Adiciona 1 para exibição (usuário vê 1-indexado)
        for (int i = 0; i < centrosCopia.length; i++) {
            centrosCopia[i]++;
        }
        return Arrays.toString(centrosCopia);
    }
}