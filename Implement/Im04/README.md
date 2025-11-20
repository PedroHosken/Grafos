# Caminhos Disjuntos em Arestas

Implementação em Java de um algoritmo para encontrar caminhos disjuntos em arestas em grafos direcionados usando o algoritmo de fluxo máximo (Edmonds-Karp).

## Descrição

Este projeto implementa uma solução para o problema de encontrar todos os caminhos disjuntos em arestas entre dois vértices em um grafo direcionado. O algoritmo utiliza o conceito de fluxo máximo, onde cada aresta tem capacidade 1, e o número de caminhos disjuntos é igual ao valor do fluxo máximo.

## Estrutura do Projeto

```
src/main/java/br/edu/ufcg/grafos/
├── Edge.java                    # Representação de aresta
├── DirectedGraph.java           # Grafo direcionado com suporte a fluxo máximo
├── GraphReader.java             # Leitura/escrita de grafos em arquivo
├── EdgeDisjointPaths.java       # Algoritmo principal para caminhos disjuntos
├── Main.java                    # Classe principal para execução
├── GraphGenerator.java          # Geradores de grafos para testes
├── TestRunner.java              # Execução de testes
├── TestMain.java                # Classe principal para testes
└── ReportGenerator.java         # Geração de relatório PDF
```

## Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior

## Compilação

```bash
mvn clean compile
```

## Execução

### Executar com arquivo de grafo

```bash
mvn exec:java -Dexec.mainClass="br.edu.ufcg.grafos.Main" -Dexec.args="graph.txt 0 5"
```

Onde:
- `graph.txt` é o arquivo contendo a descrição do grafo
- `0` é o vértice origem
- `5` é o vértice destino

### Formato do arquivo de grafo

```
5
0 1
0 2
1 3
2 3
3 4
```

- Primeira linha: número de vértices
- Linhas seguintes: arestas no formato "origem destino"

### Executar testes e gerar relatório

```bash
mvn exec:java -Dexec.mainClass="br.edu.ufcg.grafos.TestMain"
```

Isso executará testes com diferentes tipos de grafos e gerará um relatório PDF (`relatorio_caminhos_disjuntos.pdf`).

## Tipos de Grafos Testados

1. **Grafos Completos**: Todos os vértices estão conectados a todos os outros
2. **Grafos em Camadas**: Grafos divididos em camadas, onde cada vértice de uma camada está conectado a todos os vértices da próxima camada

## Algoritmo

O algoritmo utiliza o método de **Edmonds-Karp** para calcular o fluxo máximo:

1. Cada aresta do grafo tem capacidade 1
2. O algoritmo encontra caminhos aumentadores usando BFS
3. O fluxo máximo encontrado corresponde ao número de caminhos disjuntos em arestas
4. Os caminhos são extraídos do grafo residual após o cálculo do fluxo

**Complexidade**: O(V * E²), onde V é o número de vértices e E é o número de arestas.

## Exemplo de Saída

```
========================================
Caminhos Disjuntos em Arestas
========================================
Origem: 0
Destino: 4
Quantidade de caminhos disjuntos: 2
Tempo de execução: 5 ms

Caminhos encontrados:
Caminho 1: 0 -> 1 -> 3 -> 4
Caminho 2: 0 -> 2 -> 3 -> 4
========================================
```

## Relatório PDF

O relatório PDF gerado contém:
- Tabelas com resultados de eficácia e eficiência
- Análise de performance para cada tipo de grafo
- Gráficos de distribuição de tempos
- Conclusões sobre o algoritmo

## Autor

Pedro Hosken

Implementação para a disciplina de Grafos
