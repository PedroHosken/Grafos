# Trabalho Prático 02 - Problema dos k-centros

## Descrição

Este trabalho implementa duas abordagens para resolver o problema dos k-centros:

1. **Algoritmo Exato (Força Bruta)**: Testa todas as combinações possíveis de k vértices como centros para encontrar a solução ótima. Viável apenas para instâncias pequenas devido à complexidade exponencial.

2. **Algoritmo Aproximado (Farthest-First)**: Usa uma heurística gulosa que fornece uma 2-aproximação garantida (o raio encontrado é no máximo 2 vezes o raio ótimo). Complexidade O(n × k). Funciona para instâncias de qualquer tamanho.

## Estrutura do Projeto

### Classes Principais

- **`Instancia.java`**: Classe responsável por ler e processar arquivos de instância no formato OR-Library. Carrega o grafo, executa Floyd-Warshall para calcular distâncias de menor caminho entre todos os pares de vértices, e fornece acesso aos dados (V, k, matriz de distâncias).

- **`SolucaoExata.java`**: Implementa a solução exata usando força bruta. Gera recursivamente todas as combinações de k centros e avalia cada uma para encontrar a solução ótima.

- **`SolucaoAproximada.java`**: Implementa a heurística Farthest-First (Primeiro-Mais-Distante). Escolhe o primeiro centro arbitrariamente e, iterativamente, seleciona o vértice mais distante dos centros já escolhidos.

- **`Main.java`**: Classe principal que coordena a execução. Solicita o nome do arquivo ao usuário, carrega a instância, executa os algoritmos e exibe os resultados comparativos.

## Como Usar

### Compilação

Compile todos os arquivos Java do projeto:

```bash
javac tps/tp02/*.java
```

Ou compile individualmente:

```bash
javac tps/tp02/Instancia.java
javac tps/tp02/SolucaoExata.java
javac tps/tp02/SolucaoAproximada.java
javac tps/tp02/Main.java
```

### Execução

Execute o programa principal:

```bash
java tps.tp02.Main
```

O programa irá:
1. Solicitar o nome do arquivo (ex: `pmed1.txt`)
2. Buscar automaticamente o arquivo no diretório `tps/tp02/`
3. Carregar e processar a instância
4. Executar automaticamente o algoritmo aproximado
5. Perguntar se deseja executar o algoritmo exato (pode demorar muito para instâncias grandes)
6. Exibir os resultados e comparação (quando ambos são executados)

**Exemplo de uso:**

```
--- Testador de Instâncias k-Centros ---
Digite o nome do arquivo (ex: pmed1.txt): pmed1.txt

Processando arquivo: C:\Users\...\tps\tp02\pmed1.txt...
Instância carregada: V=100, k=5
----------------------------------------------

--- 1. Resultado (Aproximada / Farthest-First) ---
Tempo de Execução: 0.3300 ms
Raio Encontrado:   186
Centros: [1, 16, 47, 63, 77]
----------------------------------------------

--- 2. Resultado (Exata / Força Bruta) ---
AVISO: A solução exata (V=100, k=5) pode demorar MUITO.
Deseja executá-la? (s/n): n
```

### Localização de Arquivos

O programa busca automaticamente os arquivos nos seguintes locais (em ordem de prioridade):

1. Caminho absoluto fornecido (ex: `C:/Users/.../pmed1.txt`)
2. Caminho relativo fornecido (ex: `tps/tp02/pmed1.txt`)
3. Diretório padrão: `tps/tp02/nomeArquivo`
4. Diretório de trabalho atual

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

**Nota**: 
- Os vértices nos arquivos são **1-indexados** (começam em 1)
- Internamente, o programa converte para **0-indexados** (índices de 0 a V-1)
- O algoritmo Floyd-Warshall é executado automaticamente para calcular as distâncias de menor caminho entre todos os pares de vértices, construindo o grafo completo necessário para o problema dos k-centros

## Algoritmos

### Algoritmo Exato (Força Bruta)

- **Método**: Geração recursiva de todas as combinações C(V, k) de k vértices
- **Complexidade**: O(C(V, k) × V × k) no pior caso
- **Uso**: Apenas para instâncias muito pequenas (V ≤ 25 aproximadamente, dependendo de k)
- **Limitação**: O número de combinações cresce exponencialmente. Para V=100 e k=5, há aproximadamente 75 milhões de combinações.

**Como funciona:**
1. Gera recursivamente todas as combinações de k vértices
2. Para cada combinação, calcula o raio (maior distância de qualquer vértice ao seu centro mais próximo)
3. Mantém a melhor solução encontrada

### Algoritmo Aproximado (Farthest-First)

- **Método**: Heurística gulosa
- **Algoritmo**:
  1. Escolhe o primeiro centro arbitrariamente (vértice 0)
  2. Para cada um dos k-1 centros restantes:
     - Encontra o vértice mais distante de todos os centros já escolhidos
     - Adiciona esse vértice como novo centro
     - Atualiza as distâncias mínimas de todos os vértices aos centros
- **Complexidade**: O(V × k)
- **Garantia**: 2-aproximação (raio ≤ 2 × raio_ótimo)

**Vantagens:**
- Muito rápido, mesmo para instâncias grandes
- Sempre fornece uma solução viável
- Garantia teórica de qualidade (no máximo 2x pior que o ótimo)

## Resultados

O programa exibe:

- **Tempo de execução**: Em milissegundos (ms)
- **Raio encontrado**: Maior distância de um vértice ao seu centro mais próximo
- **Centros selecionados**: Lista dos vértices escolhidos como centros (exibidos em formato 1-indexado)
- **Comparação** (quando ambos algoritmos são executados):
  - Raio aproximado vs. raio exato
  - Gap percentual: diferença relativa entre as soluções
  - Tempos de execução comparados

## Notas Importantes

- ⚠️ **Algoritmo Exato**: Pode demorar muito tempo para instâncias grandes. O programa pergunta antes de executar.
- ✅ **Algoritmo Aproximado**: Sempre executado automaticamente, pois é rápido e eficiente.
- 📁 **Arquivos**: Coloque os arquivos de instância no diretório `tps/tp02/` ou forneça o caminho completo.
- 🔢 **Índices**: Os centros são exibidos em formato 1-indexado para facilitar a leitura, mas internamente são 0-indexados.

## Exemplo de Saída Completa

```
--- Testador de Instâncias k-Centros ---
Digite o nome do arquivo (ex: pmed1.txt): pmed1.txt

Processando arquivo: ...\tps\tp02\pmed1.txt...
Instância carregada: V=100, k=5
----------------------------------------------

--- 1. Resultado (Aproximada / Farthest-First) ---
Tempo de Execução: 0.3300 ms
Raio Encontrado:   186
Centros: [1, 16, 47, 63, 77]
----------------------------------------------

--- 2. Resultado (Exata / Força Bruta) ---
AVISO: A solução exata (V=100, k=5) pode demorar MUITO.
Deseja executá-la? (s/n): s
Iniciando Solução Exata (Força Bruta)...
Isso pode demorar bastante para V=100, k=5

Solução Exata Concluída.
Tempo de Execução: 12345.6789 ms
Raio Encontrado:   127
Centros: [12, 34, 56, 78, 90]

--- Comparação Final ---
Raio (Aproximado): 186 (0.3300 ms)
Raio (Exato):      127 (12345.6789 ms)
Gap (Aprox. vs Exato): 46.46%
----------------------------------------------
Teste concluído.
```
