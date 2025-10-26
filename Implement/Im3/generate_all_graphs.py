import random
import sys
import os

def generate_sparse_grid(rows, cols, min_weight, max_weight):
    """
    Gera um grafo esparso em formato de grade.
    Cada nó (vértice) se conecta aos seus vizinhos (N, S, L, O)
    com pesos aleatórios.
    """
    V = rows * cols
    edges = []
    
    def get_index(r, c):
        return r * cols + c

    for r in range(rows):
        for c in range(cols):
            current_idx = get_index(r, c)
            
            # Aresta para a Direita (c + 1)
            if c + 1 < cols:
                neighbor_idx = get_index(r, c + 1)
                weight = random.randint(min_weight, max_weight)
                edges.append((current_idx, neighbor_idx, weight))
            
            # Aresta para Baixo (r + 1)
            if r + 1 < rows:
                neighbor_idx = get_index(r + 1, c)
                weight = random.randint(min_weight, max_weight)
                edges.append((current_idx, neighbor_idx, weight))
                
            # Adicionando arestas "para trás" para criar mais caminhos
            # Aresta para a Esquerda (c - 1)
            if c > 0:
                neighbor_idx = get_index(r, c - 1)
                weight = random.randint(min_weight, max_weight)
                edges.append((current_idx, neighbor_idx, weight))

            # Aresta para Cima (r - 1)
            if r > 0:
                neighbor_idx = get_index(r - 1, c)
                weight = random.randint(min_weight, max_weight)
                edges.append((current_idx, neighbor_idx, weight))

    return V, edges

def generate_dense(num_vertices, num_edges, min_weight, max_weight):
    """
    Gera um grafo denso aleatório.
    Garante que o número de arestas seja o solicitado,
    sem duplicatas.
    """
    V = num_vertices
    edges_set = set()
    edges = []
    
    if num_edges > V * (V - 1):
        print(f"Erro: Número de arestas {num_edges} excede o máximo {V * (V - 1)}.")
        sys.exit(1)

    while len(edges_set) < num_edges:
        u = random.randint(0, V - 1)
        v = random.randint(0, V - 1)
        
        if u != v and (u, v) not in edges_set:
            edges_set.add((u, v))
            weight = random.randint(min_weight, max_weight)
            edges.append((u, v, weight))
            
    return V, edges

def save_graph_to_file(filename, V, E, edges, source, destination):
    """
    Salva o grafo no formato de arquivo .txt esperado pelo Java.
    """
    try:
        with open(filename, 'w') as f:
            # Linha 1: V E
            f.write(f"{V} {E}\n")
            
            # Linhas de Arestas: u v w
            for u, v, w in edges:
                f.write(f"{u} {v} {w}\n")
                
            # Linha Final: source destination
            f.write(f"{source} {destination}\n")
        print(f"Sucesso: Arquivo '{filename}' (V={V}, E={E}) gerado.")
    except IOError as e:
        print(f"Erro ao salvar o arquivo {filename}: {e}")

def main():
    """
    Função principal para gerar todos os 8 grafos de teste.
    """
    print("Iniciando a geração dos 8 arquivos de teste...")
    
    # Pesos padrão
    MIN_W = 1
    MAX_W = 20

    # --- TIPO 1: GRAFOS ESPARSOS (Grid) ---
    print("\n--- Gerando Grafos Esparsos ---")
    
    # Esparso 1 (10x10)
    rows, cols = 10, 10
    V, edges = generate_sparse_grid(rows, cols, MIN_W, MAX_W)
    save_graph_to_file("esparso_1.txt", V, len(edges), edges, 0, V-1)
    
    # Esparso 2 (50x50)
    rows, cols = 50, 50
    V, edges = generate_sparse_grid(rows, cols, MIN_W, MAX_W)
    save_graph_to_file("esparso_2.txt", V, len(edges), edges, 0, V-1)
    
    # Esparso 3 (100x100)
    rows, cols = 100, 100
    V, edges = generate_sparse_grid(rows, cols, MIN_W, MAX_W)
    save_graph_to_file("esparso_3.txt", V, len(edges), edges, 0, V-1)
    
    # Esparso 4 (200x200)
    rows, cols = 200, 200
    V, edges = generate_sparse_grid(rows, cols, MIN_W, MAX_W)
    save_graph_to_file("esparso_4.txt", V, len(edges), edges, 0, V-1)

    # --- TIPO 2: GRAFOS DENSOS (Aleatório) ---
    print("\n--- Gerando Grafos Densos ---")
    
    # Denso 1 (V=100, E=5.000)
    v, e = 100, 5000
    V, edges = generate_dense(v, e, MIN_W, MAX_W)
    save_graph_to_file("denso_1.txt", V, len(edges), edges, 0, V-1)

    # Denso 2 (V=500, E=125.000)
    v, e = 500, 125000
    V, edges = generate_dense(v, e, MIN_W, MAX_W)
    save_graph_to_file("denso_2.txt", V, len(edges), edges, 0, V-1)

    # Denso 3 (V=1000, E=500.000)
    v, e = 1000, 500000
    V, edges = generate_dense(v, e, MIN_W, MAX_W)
    save_graph_to_file("denso_3.txt", V, len(edges), edges, 0, V-1)
    
    # Denso 4 (V=1500, E=1.125.000)
    v, e = 1500, 1125000
    V, edges = generate_dense(v, e, MIN_W, MAX_W)
    save_graph_to_file("denso_4.txt", V, len(edges), edges, 0, V-1)

    print("\nTodos os arquivos de teste foram gerados!")

if __name__ == "__main__":
    main()