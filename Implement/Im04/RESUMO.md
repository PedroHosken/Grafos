# Resumo da Implementação

## Objetivo
Implementar um método para determinar todos os caminhos disjuntos em arestas de um grafo direcionado utilizando algoritmo de fluxo máximo.

## Algoritmo Utilizado
**Edmonds-Karp** - Algoritmo de fluxo máximo que utiliza BFS para encontrar caminhos aumentadores.

- **Complexidade**: O(V * E²), onde V é o número de vértices e E é o número de arestas
- **Abordagem**: Cada aresta tem capacidade 1. O fluxo máximo encontrado corresponde ao número de caminhos disjuntos em arestas.

## Estrutura da Solução

### Classes Principais

1. **Edge.java**: Representa uma aresta direcionada com capacidade e fluxo
2. **DirectedGraph.java**: Grafo direcionado com implementação do algoritmo de fluxo máximo
3. **EdgeDisjointPaths.java**: Classe principal que encontra caminhos disjuntos
4. **GraphReader.java**: Leitura/escrita de grafos em arquivo
5. **Main.java**: Classe principal para execução com arquivo
6. **TestMain.java**: Classe principal para execução de testes
7. **GraphGenerator.java**: Geradores de diferentes tipos de grafos
8. **TestRunner.java**: Execução e coleta de resultados dos testes
9. **ReportGenerator.java**: Geração de relatório PDF

### Funcionalidades

- ✅ Leitura de grafo a partir de arquivo
- ✅ Cálculo de fluxo máximo (Edmonds-Karp)
- ✅ Extração de caminhos disjuntos em arestas
- ✅ Exibição de resultados formatados
- ✅ Geração de relatório PDF com tabelas e gráficos

## Testes Realizados

### Tipo 1: Grafos Completos
- 5 vértices
- 10 vértices
- 15 vértices
- 20 vértices

### Tipo 2: Grafos em Camadas
- 3 camadas x 5 vértices = 15 vértices
- 4 camadas x 5 vértices = 20 vértices
- 5 camadas x 5 vértices = 25 vértices
- 6 camadas x 5 vértices = 30 vértices

## Relatório PDF

O relatório gerado contém:
- Tabelas com resultados de eficácia (número de caminhos encontrados)
- Tabelas com resultados de eficiência (tempo de execução)
- Análise de performance (tempo mínimo, máximo e médio)
- Gráficos de distribuição de tempos
- Conclusões sobre o algoritmo

## Como Usar

1. **Compilar**: `mvn clean compile` ou usar o script `compile.bat`
2. **Executar com arquivo**: `mvn exec:java -Dexec.mainClass="br.edu.ufcg.grafos.Main" -Dexec.args="graph.txt 0 5"`
3. **Executar testes**: `mvn exec:java -Dexec.mainClass="br.edu.ufcg.grafos.TestMain"`

## Formato de Entrada

```
<número_de_vértices>
<origem1> <destino1>
<origem2> <destino2>
...
```

## Formato de Saída

```
========================================
Caminhos Disjuntos em Arestas
========================================
Origem: 0
Destino: 5
Quantidade de caminhos disjuntos: 2
Tempo de execução: 5 ms

Caminhos encontrados:
Caminho 1: 0 -> 1 -> 3 -> 5
Caminho 2: 0 -> 2 -> 4 -> 5
========================================
```

