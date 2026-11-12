import java.util.ArrayList;
import java.util.List;

public class AVLTree {
    private static final double EPSILON = 1e-9;

    /**
     * Nó interno da Árvore AVL que armazena a similaridade como chave e uma lista
     * de pares de documentos (Resultado) associados àquela mesma similaridade.
     */
    public static class NoAVL {
        private final double chave;
        private final List<Resultado> resultados;
        private NoAVL esquerda;
        private NoAVL direita;
        private int altura;

        public NoAVL(Resultado resultado) {
            this.chave = resultado.getSimilaridade();
            this.resultados = new ArrayList<>();
            this.resultados.add(resultado);
            this.esquerda = null;
            this.direita = null;
            this.altura = 1;
        }

        public double getChave() { return chave; }
        public List<Resultado> getResultados() { return resultados; }
        public void adicionarResultado(Resultado r) { this.resultados.add(r); }
        public NoAVL getEsquerda() { return esquerda; }
        public void setEsquerda(NoAVL esquerda) { this.esquerda = esquerda; }
        public NoAVL getDireita() { return direita; }
        public void setDireita(NoAVL direita) { this.direita = direita; }
        public int getAltura() { return altura; }
        public void setAltura(int altura) { this.altura = altura; }
    }

    /**
     * Registro retornado pelo método de inserção que quantifica as rotações
     * (Simples Esquerda/Direita, Dupla Esquerda/Direita) realizadas na operação.
     */
    public static class EstatisticaRotacoes {
        private int simplesEsquerda;
        private int simplesDireita;
        private int duplaEsquerda;
        private int duplaDireita;

        public void somar(EstatisticaRotacoes outra) {
            this.simplesEsquerda += outra.simplesEsquerda;
            this.simplesDireita += outra.simplesDireita;
            this.duplaEsquerda += outra.duplaEsquerda;
            this.duplaDireita += outra.duplaDireita;
        }

        public int getSimplesEsquerda() { return simplesEsquerda; }
        public int getSimplesDireita() { return simplesDireita; }
        public int getDuplaEsquerda() { return duplaEsquerda; }
        public int getDuplaDireita() { return duplaDireita; }
        public int getTotal() {
            return simplesEsquerda + simplesDireita + duplaEsquerda + duplaDireita;
        }
    }

    private NoAVL raiz;
    private final EstatisticaRotacoes acumuladoRotacoes;
    private EstatisticaRotacoes rotacoesUltimaInsercao;

    public AVLTree() {
        this.raiz = null;
        this.acumuladoRotacoes = new EstatisticaRotacoes();
        this.rotacoesUltimaInsercao = new EstatisticaRotacoes();
    }

    /**
     * Insere um Resultado na Árvore AVL e retorna o registro de rotações da operação.
     */
    public EstatisticaRotacoes inserir(Resultado resultado) {
        this.rotacoesUltimaInsercao = new EstatisticaRotacoes();
        this.raiz = inserirRecursivo(this.raiz, resultado);
        this.acumuladoRotacoes.somar(this.rotacoesUltimaInsercao);
        return this.rotacoesUltimaInsercao;
    }

    private NoAVL inserirRecursivo(NoAVL no, Resultado resultado) {
        if (no == null) {
            return new NoAVL(resultado);
        }

        double chave = resultado.getSimilaridade();

        // Se a similaridade já existir na árvore, agrupa na lista do próprio nó
        if (Math.abs(chave - no.getChave()) < EPSILON) {
            no.adicionarResultado(resultado);
            return no;
        } else if (chave < no.getChave()) {
            no.setEsquerda(inserirRecursivo(no.getEsquerda(), resultado));
        } else {
            no.setDireita(inserirRecursivo(no.getDireita(), resultado));
        }

        atualizarAltura(no);
        return balancear(no);
    }

    private int altura(NoAVL no) {
        return (no == null) ? 0 : no.getAltura();
    }

    private void atualizarAltura(NoAVL no) {
        if (no != null) {
            no.setAltura(1 + Math.max(altura(no.getEsquerda()), altura(no.getDireita())));
        }
    }

    /**
     * Calcula o Fator de Balanceamento: FB = Hdir - Hesq.
     * Valores positivos (FB > 1) indicam que o lado direito está mais pesado -> Rotação à Esquerda.
     * Valores negativos (FB < -1) indicam que o lado esquerdo está mais pesado -> Rotação à Direita.
     */
    private int getFatorBalanceamento(NoAVL no) {
        return (no == null) ? 0 : altura(no.getDireita()) - altura(no.getEsquerda());
    }

    /**
     * Verifica o Fator de Balanceamento do nó e executa as rotações simples ou duplas necessárias.
     */
    private NoAVL balancear(NoAVL no) {
        int fb = getFatorBalanceamento(no);

        // Desbalanceamento para a direita (FB > 1) -> Rotação para a Esquerda
        if (fb > 1) {
            if (getFatorBalanceamento(no.getDireita()) < 0) {
                // Caso Direita-Esquerda: Rotação Dupla à Esquerda
                no.setDireita(rotacaoDireita(no.getDireita()));
                this.rotacoesUltimaInsercao.duplaEsquerda++;
                return rotacaoEsquerda(no);
            } else {
                // Caso Direita-Direita: Rotação Simples à Esquerda
                this.rotacoesUltimaInsercao.simplesEsquerda++;
                return rotacaoEsquerda(no);
            }
        }

        // Desbalanceamento para a esquerda (FB < -1) -> Rotação para a Direita
        if (fb < -1) {
            if (getFatorBalanceamento(no.getEsquerda()) > 0) {
                // Caso Esquerda-Direita: Rotação Dupla à Direita
                no.setEsquerda(rotacaoEsquerda(no.getEsquerda()));
                this.rotacoesUltimaInsercao.duplaDireita++;
                return rotacaoDireita(no);
            } else {
                // Caso Esquerda-Esquerda: Rotação Simples à Direita
                this.rotacoesUltimaInsercao.simplesDireita++;
                return rotacaoDireita(no);
            }
        }

        return no;
    }

    private NoAVL rotacaoDireita(NoAVL y) {
        NoAVL x = y.getEsquerda();
        NoAVL t2 = x.getDireita();

        x.setDireita(y);
        y.setEsquerda(t2);

        atualizarAltura(y);
        atualizarAltura(x);

        return x;
    }

    private NoAVL rotacaoEsquerda(NoAVL x) {
        NoAVL y = x.getDireita();
        NoAVL t2 = y.getEsquerda();

        y.setEsquerda(x);
        x.setDireita(t2);

        atualizarAltura(x);
        atualizarAltura(y);

        return y;
    }

    /**
     * Lista todos os pares com similaridade >= limiar em ordem decrescente.
     */
    public List<Resultado> obterAcimaDe(double limiar) {
        List<Resultado> lista = new ArrayList<>();
        percorrerDecrescente(this.raiz, limiar, -1, lista);
        return lista;
    }

    /**
     * Lista os K pares mais semelhantes (respeitando o limiar informado) em ordem decrescente.
     */
    public List<Resultado> obterTopK(int k, double limiar) {
        List<Resultado> lista = new ArrayList<>();
        percorrerDecrescente(this.raiz, limiar, k, lista);
        return lista;
    }

    /**
     * Retorna os pares armazenados no nó mais à esquerda da árvore (menor similaridade).
     */
    public List<Resultado> obterMenores() {
        if (this.raiz == null) {
            return new ArrayList<>();
        }
        NoAVL atual = this.raiz;
        while (atual.getEsquerda() != null) {
            atual = atual.getEsquerda();
        }
        return atual.getResultados();
    }

    private void percorrerDecrescente(NoAVL no, double limiar, int limiteK, List<Resultado> acumulador) {
        if (no == null) {
            return;
        }
        if (limiteK > 0 && acumulador.size() >= limiteK) {
            return;
        }

        percorrerDecrescente(no.getDireita(), limiar, limiteK, acumulador);

        if (no.getChave() + EPSILON >= limiar) {
            for (Resultado r : no.getResultados()) {
                if (limiteK > 0 && acumulador.size() >= limiteK) {
                    return;
                }
                acumulador.add(r);
            }
            percorrerDecrescente(no.getEsquerda(), limiar, limiteK, acumulador);
        }
    }

    public EstatisticaRotacoes getAcumuladoRotacoes() {
        return acumuladoRotacoes;
    }

    public NoAVL getRaiz() {
        return raiz;
    }
}