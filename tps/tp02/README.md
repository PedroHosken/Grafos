# Trabalho Prático 02 - Problema dos k-centros

## Descrição

Este trabalho implementa duas abordagens para resolver o problema dos k-centros:

1. **Algoritmo Exato**: Implementa duas estratégias exatas:
   - **Branch and Bound**: Usa podas inteligentes para eliminar ramos da árvore de busca que não podem levar a soluções melhores. Viável para instâncias médias (n ≤ 40 aproximadamente).
   - **Força Bruta**: Testa todas as combinações possíveis de k vértices como centros. Viável apenas para instâncias muito pequenas (n ≤ 20 aproximadamente).
   
   O programa escolhe automaticamente qual método usar baseado no tamanho da instância.

2. **Algoritmo Aproximado**: Usa um algoritmo guloso que fornece uma 2-aproximação (o raio encontrado é no máximo 2 vezes o raio ótimo). Complexidade O(n × k). Funciona para instâncias de qualquer tamanho.

## Estrutura do Projeto

- `CompleteGraph.java`: Representa um grafo completo com distâncias entre todos os pares de vértices.
- `ORLibraryReader.java`: Lê instâncias da OR-Library no formato p-medianas.
- `ExactKCenters.java`: Implementação exata usando força bruta.
- `BranchBoundKCenters.java`: Implementação exata usando Branch and Bound com podas.
- `ApproximateKCenters.java`: Implementação aproximada usando algoritmo guloso.
- `KCentersSolver.java`: Classe principal para resolver e comparar as abordagens.
- `Main.java`: Ponto de entrada do programa.
- `ComparativeAnalysis.java`: Análise comparativa em múltiplas instâncias.

## Como Usar

### Compilação

```bash
javac tps/tp02/*.java
```

### Execução

#### Testar uma única instância:

```bash
java tps.tp02.Main <arquivo> <k>
```

Exemplo:
```bash
java tps.tp02.Main pmed1.txt 5
```

#### Análise comparativa (via código):

Crie uma classe ou modifique `Main.java` para usar `ComparativeAnalysis.analyzeInstances()`.

## Formato dos Arquivos

Os arquivos devem estar no formato da OR-Library (p-medianas):
- **Primeira linha**: `n m p` (número de vértices, número de arestas, número de centros)
- **Linhas seguintes**: `m` arestas no formato `u v w` (vértice origem, vértice destino, peso)

**Exemplo:**
```
100 200 5
1 2 30
2 3 46
3 4 1
...
```

**Nota**: O leitor assume que os vértices são **1-indexed** no arquivo e os converte para **0-indexed** internamente. O algoritmo calcula automaticamente as distâncias entre todos os pares usando Floyd-Warshall para construir o grafo completo necessário para o problema dos k-centros.

## Algoritmos

### Algoritmo Exato

O programa implementa dois métodos exatos e escolhe automaticamente qual usar:

#### Branch and Bound
- **Método**: Árvore de busca com podas inteligentes
- **Complexidade**: O(C(n,k) × n × k) no pior caso, mas com podas pode ser muito mais rápido
- **Uso**: Instâncias médias (n ≤ 40 aproximadamente)
- **Vantagem**: Elimina ramos da busca que não podem melhorar a solução atual, reduzindo significativamente o espaço de busca

#### Força Bruta
- **Método**: Testa todas as combinações C(n,k)
- **Complexidade**: O(C(n,k) × n × k)
- **Uso**: Apenas para instâncias muito pequenas (n ≤ 20)

### Algoritmo Aproximado
- **Método**: Guloso
- **Algoritmo**:
  1. Escolhe o primeiro centro arbitrariamente
  2. Para cada centro adicional: escolhe o vértice mais distante de todos os centros já escolhidos
- **Complexidade**: O(n × k)
- **Garantia**: 2-aproximação (razo ≤ 2 × raio_ótimo)

## Resultados

O programa imprime:
- Os centros encontrados
- O raio da solução (maior distância de um vértice ao centro mais próximo)
- Tempo de execução
- Comparação entre as abordagens (quando ambas são executadas)

## Notas

- O algoritmo exato é automaticamente pulado se o número de combinações for muito grande (> 1.000.000)
- Para instâncias grandes, use apenas o algoritmo aproximado
- O algoritmo aproximado sempre fornece uma solução viável

