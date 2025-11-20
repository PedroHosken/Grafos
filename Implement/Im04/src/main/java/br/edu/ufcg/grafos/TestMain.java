package br.edu.ufcg.grafos;

import java.util.List;

/**
 * Classe principal para executar testes e gerar relatório.
 */
public class TestMain {
    
    public static void main(String[] args) {
        System.out.println("Iniciando testes de caminhos disjuntos em arestas...");
        System.out.println("====================================================\n");
        
        // Executar todos os testes
        List<TestRunner.TestResult> results = TestRunner.runAllTests();
        
        // Exibir resultados no console
        TestRunner.displayResults(results);
        
        // Gerar relatório PDF
        try {
            String reportFile = "relatorio_caminhos_disjuntos.pdf";
            ReportGenerator.generatePDFReport(results, reportFile);
            System.out.println("\nRelatório PDF gerado: " + reportFile);
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório PDF: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nTestes concluídos!");
    }
}

