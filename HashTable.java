import java.util.ArrayList;
import java.util.List;

public class HashTable {

    public static class No {
        public String palavra;
        public int frequencia;
        public No proximo;

        public No(String palavra, int frequencia) {
            this.palavra = palavra;
            this.frequencia = frequencia;
            this.proximo = null;
        }
    }

    private final No[] tabela;
    private final int capacidade;
    private int totalColisoes;
    private final int tipoHash; // 1 = hashMultiplicativo, 2 = hashPolinomial

    public HashTable(int capacidade, int tipoHash) {
        this.capacidade = capacidade;
        this.tabela = new No[capacidade];
        this.totalColisoes = 0;
        this.tipoHash = tipoHash;
    }

    /**
     * Insere ou atualiza o par (chave, valor) na Tabela Hash.
     * Tratamento de colisões: Encadeamento Separado (Separate Chaining).
     * Quando duas chaves distintas mapeiam para o mesmo índice (bucket), a nova
     * chave é adicionada ao final da lista encadeada daquele índice e o contador
     * de colisões é incrementado.
     */
    public void put(String chave, int valor) {
        int indice = calcularHash(chave);

        if (tabela[indice] == null) {
            tabela[indice] = new No(chave, valor);
        } else {
            No atual = tabela[indice];
            while (true) {
                if (atual.palavra.equals(chave)) {
                    atual.frequencia = valor; // Atualiza valor de chave já existente
                    return;
                }
                if (atual.proximo == null) {
                    break;
                }
                atual = atual.proximo;
            }
            // Ocorreu colisão com outra(s) palavra(s) no mesmo bucket
            totalColisoes++;
            atual.proximo = new No(chave, valor);
        }
    }

    /**
     * Sobrecarga auxiliar que incrementa diretamente em 1 a frequência da chave.
     */
    public void put(String chave) {
        put(chave, get(chave) + 1);
    }

    /**
     * Retorna a frequência associada à chave na Tabela Hash, ou 0 caso não exista.
     */
    public int get(String chave) {
        int indice = calcularHash(chave);
        No atual = tabela[indice];
        while (atual != null) {
            if (atual.palavra.equals(chave)) {
                return atual.frequencia;
            }
            atual = atual.proximo;
        }
        return 0;
    }

    /**
     * Retorna todas as palavras únicas armazenadas na Tabela Hash.
     */
    public List<String> getPalavras() {
        List<String> palavras = new ArrayList<>();
        for (int i = 0; i < capacidade; i++) {
            No atual = tabela[i];
            while (atual != null) {
                palavras.add(atual.palavra);
                atual = atual.proximo;
            }
        }
        return palavras;
    }

    private int calcularHash(String chave) {
        return (tipoHash == 1) ? hashMultiplicativo(chave) : hashPolinomial(chave);
    }

    /**
     * Função de Dispersão 1: hashMultiplicativo (baseado em DJB2).
     * Parte da constante prima 5381 e multiplica o acumulador por 33 a cada caractere
     * usando deslocamento de bits ((hash << 5) + hash) somado ao código Unicode do caractere.
     */
    private int hashMultiplicativo(String palavra) {
        long hash = 5381;
        for (int i = 0; i < palavra.length(); i++) {
            hash = ((hash << 5) + hash) + palavra.charAt(i);
        }
        return (int) (Math.abs(hash) % capacidade);
    }

    /**
     * Função de Dispersão 2: hashPolinomial (base primo 31 via Regra de Horner).
     * Avalia o polinômio dos caracteres da string aplicando módulo a cada passo.
     */
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