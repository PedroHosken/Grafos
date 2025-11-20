package br.edu.ufcg.grafos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Gerador de relatório em PDF com tabelas e gráficos.
 */
public class ReportGenerator {
    
    private static Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font headingFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);
    
    /**
     * Gera relatório em PDF com os resultados dos testes.
     */
    public static void generatePDFReport(List<TestRunner.TestResult> results, String filename) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        
        document.open();
        
        // Título
        Paragraph title = new Paragraph("Relatório de Caminhos Disjuntos em Arestas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Agrupar resultados por tipo de grafo
        Map<String, List<TestRunner.TestResult>> grouped = new HashMap<>();
        for (TestRunner.TestResult result : results) {
            grouped.computeIfAbsent(result.getGraphType(), k -> new ArrayList<>()).add(result);
        }
        
        // Gerar seção para cada tipo de grafo
        for (Map.Entry<String, List<TestRunner.TestResult>> entry : grouped.entrySet()) {
            addGraphTypeSection(document, entry.getKey(), entry.getValue());
            document.add(new Paragraph("\n"));
        }
        
        // Conclusão
        addConclusion(document, results);
        
        document.close();
    }
    
    private static void addGraphTypeSection(Document document, String graphType, 
                                           List<TestRunner.TestResult> results) throws Exception {
        // Título da seção
        Paragraph heading = new Paragraph(graphType, headingFont);
        heading.setSpacingBefore(10);
        heading.setSpacingAfter(10);
        document.add(heading);
        
        // Tabela de resultados
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 2, 2, 2, 3});
        
        // Cabeçalho
        addTableHeader(table, "Tamanho");
        addTableHeader(table, "Vértices");
        addTableHeader(table, "Arestas");
        addTableHeader(table, "Caminhos");
        addTableHeader(table, "Tempo (ms)");
        
        // Dados
        for (TestRunner.TestResult result : results) {
            table.addCell(createCell(String.valueOf(result.getSize())));
            table.addCell(createCell(String.valueOf(result.getNumVertices())));
            table.addCell(createCell(String.valueOf(result.getNumEdges())));
            table.addCell(createCell(String.valueOf(result.getNumPaths())));
            table.addCell(createCell(String.valueOf(result.getExecutionTime())));
        }
        
        document.add(table);
        
        // Adicionar gráfico de tempo (texto descritivo, já que gráficos reais requerem mais complexidade)
        document.add(new Paragraph("\nAnálise de Performance:", headingFont));
        
        long maxTime = results.stream().mapToLong(TestRunner.TestResult::getExecutionTime).max().orElse(0);
        long minTime = results.stream().mapToLong(TestRunner.TestResult::getExecutionTime).min().orElse(0);
        double avgTime = results.stream().mapToLong(TestRunner.TestResult::getExecutionTime).average().orElse(0);
        
        document.add(new Paragraph(String.format(
            "Tempo mínimo: %d ms | Tempo máximo: %d ms | Tempo médio: %.2f ms",
            minTime, maxTime, avgTime), normalFont));
        
        // Gráfico simples em texto (barra ASCII)
        document.add(new Paragraph("\nDistribuição de Tempos (escala):", normalFont));
        for (TestRunner.TestResult result : results) {
            int barLength = maxTime > 0 ? (int) ((result.getExecutionTime() * 50.0) / maxTime) : 0;
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < barLength; i++) {
                bar.append("█");
            }
            document.add(new Paragraph(
                String.format("Tamanho %d: %s %d ms", result.getSize(), bar.toString(), result.getExecutionTime()),
                normalFont));
        }
    }
    
    private static void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headingFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private static PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, normalFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
    
    private static void addConclusion(Document document, List<TestRunner.TestResult> results) throws Exception {
        document.add(new Paragraph("\n\nCONCLUSÃO", headingFont));
        
        long totalTime = results.stream().mapToLong(TestRunner.TestResult::getExecutionTime).sum();
        int totalPaths = results.stream().mapToInt(TestRunner.TestResult::getNumPaths).sum();
        
        document.add(new Paragraph(
            String.format("Total de testes executados: %d", results.size()), normalFont));
        document.add(new Paragraph(
            String.format("Tempo total de execução: %d ms", totalTime), normalFont));
        document.add(new Paragraph(
            String.format("Total de caminhos encontrados: %d", totalPaths), normalFont));
        
        document.add(new Paragraph(
            "\nO algoritmo de fluxo máximo (Edmonds-Karp) foi utilizado para encontrar " +
            "caminhos disjuntos em arestas. A complexidade do algoritmo é O(V*E²), " +
            "onde V é o número de vértices e E é o número de arestas.", normalFont));
    }
}

