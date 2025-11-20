# Instruções de Uso

## Compilação

### Usando Maven (recomendado)

```bash
mvn clean compile
mvn package
```

### Compilação Manual

1. Baixe a biblioteca iText 5.5.13.3 e coloque o JAR em `lib/itextpdf-5.5.13.3.jar`
2. Compile os arquivos Java:

```bash
javac -d target/classes -cp "lib/itextpdf-5.5.13.3.jar" src/main/java/br/edu/ufcg/grafos/*.java
```

## Execução

### Executar com arquivo de grafo

```bash
# Com Maven
mvn exec:java -Dexec.mainClass="br.edu.ufcg.grafos.Main" -Dexec.args="examples/graph1.txt 0 5"

# Manualmente
java -cp "target/classes:lib/itextpdf-5.5.13.3.jar" br.edu.ufcg.grafos.Main examples/graph1.txt 0 5
```

### Executar testes e gerar relatório

```bash
# Com Maven
mvn exec:java -Dexec.mainClass="br.edu.ufcg.grafos.TestMain"

# Manualmente
java -cp "target/classes:lib/itextpdf-5.5.13.3.jar" br.edu.ufcg.grafos.TestMain
```

O relatório PDF será gerado como `relatorio_caminhos_disjuntos.pdf` no diretório raiz.

## Formato do Arquivo de Grafo

O arquivo deve seguir o formato:

```
<número_de_vértices>
<origem1> <destino1>
<origem2> <destino2>
...
```

Exemplo (`examples/graph1.txt`):
```
6
0 1
0 2
1 3
2 3
3 4
3 5
4 5
```

## Testes Realizados

O programa testa dois tipos de grafos:

1. **Grafos Completos**: Tamanhos 5, 10, 15, 20 vértices
2. **Grafos em Camadas**: 3, 4, 5, 6 camadas com 5 vértices por camada

Para cada tipo, são testados pelo menos 4 tamanhos diferentes, conforme solicitado.

## Saída

O programa exibe:
- Quantidade de caminhos disjuntos encontrados
- Lista de cada caminho encontrado
- Tempo de execução

O relatório PDF contém:
- Tabelas com resultados de eficácia e eficiência
- Análise de performance
- Gráficos de distribuição de tempos
- Conclusões

