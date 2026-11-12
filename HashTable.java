public class HashTable {
    
    public class No {
        public String palavra;
        public int frequencia;
        public No proximo;

        public No(String palavra) {
            this.palavra = palavra;
            this.frequencia = 1;
            this.proximo = null;
        }
    }

    private No[] tabela;
    private int capacidade;
    private int totalColisoes;
    private int tipoHash; // 1 para DJB2 (Multiplicativo com shift), 2 para Polinomial

    public HashTable(int capacidade, int tipoHash) {
        this.capacidade = capacidade;
        this.tabela = new No[capacidade];
        this.totalColisoes = 0;
        this.tipoHash = tipoHash;
    }

    public void put(String palavra) {
        int indice = (tipoHash == 1) ? hashDJB2(palavra) : hashPolinomial(palavra);
        
        if (tabela[indice] == null) {
            tabela[indice] = new No(palavra);
        } else {
            No atual = tabela[indice];
            while (true) {
                if (atual.palavra.equals(palavra)) {
                    atual.frequencia++; // Palavra já existe, apenas incrementa
                    return;
                }
                if (atual.proximo == null) {
                    break;
                }
                atual = atual.proximo;
            }
            // Chegou ao fim da lista encadeada sem achar a palavra (ocorreu uma colisão)
            totalColisoes++;
            atual.proximo = new No(palavra);
        }
    }

    public int getFrequencia(String palavra) {
        int indice = (tipoHash == 1) ? hashDJB2(palavra) : hashPolinomial(palavra);
        No atual = tabela[indice];
        while (atual != null) {
            if (atual.palavra.equals(palavra)) {
                return atual.frequencia;
            }
            atual = atual.proximo;
        }
        return 0; // Não encontrou
    }

    // Função 1: Algoritmo DJB2 (Multiplicativo com shift de bits)
    private int hashDJB2(String palavra) {
        long hash = 5381;
        for (int i = 0; i < palavra.length(); i++) {
            hash = ((hash << 5) + hash) + palavra.charAt(i); // hash * 33 + c
        }
        return (int) (Math.abs(hash) % capacidade);
    }

    // Função 2: Polinomial clássica (base 31)
    private int hashPolinomial(String palavra) {
        long hash = 0;
        for (int i = 0; i < palavra.length(); i++) {
            hash = (31 * hash + palavra.charAt(i)) % capacidade;
        }
        return (int) Math.abs(hash);
    }

    public int getTotalColisoes() {
        return totalColisoes;
    }

    public No[] getTabela() {
        return tabela;
    }
    
    public int getCapacidade() {
        return capacidade;
    }
}