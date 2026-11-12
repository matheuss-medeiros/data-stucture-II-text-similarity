import java.util.ArrayList;
import java.util.List;

public class AVLTree {
    private static final double EPSILON = 1e-9;
    private NoAVL raiz;
    private int totalRotacoes;
    private int rotacoesUltimaInsercao;

    public AVLTree() {
        this.raiz = null;
        this.totalRotacoes = 0;
        this.rotacoesUltimaInsercao = 0;
    }

    public int inserir(Resultado resultado) {
        this.rotacoesUltimaInsercao = 0;
        this.raiz = inserirRecursivo(this.raiz, resultado);
        return this.rotacoesUltimaInsercao;
    }

    private NoAVL inserirRecursivo(NoAVL no, Resultado resultado) {
        if (no == null) {
            return new NoAVL(resultado);
        }

        double chave = resultado.getSimilaridade();

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

    private int getFatorBalanceamento(NoAVL no) {
        return (no == null) ? 0 : altura(no.getEsquerda()) - altura(no.getDireita());
    }

    private NoAVL balancear(NoAVL no) {
        int fb = getFatorBalanceamento(no);

        if (fb > 1) {
            if (getFatorBalanceamento(no.getEsquerda()) < 0) {
                no.setEsquerda(rotacaoEsquerda(no.getEsquerda()));
            }
            return rotacaoDireita(no);
        }

        if (fb < -1) {
            if (getFatorBalanceamento(no.getDireita()) > 0) {
                no.setDireita(rotacaoDireita(no.getDireita()));
            }
            return rotacaoEsquerda(no);
        }

        return no;
    }

    private NoAVL rotacaoDireita(NoAVL y) {
        NoAVL x = y.getEsquerda();
        NoAVL T2 = x.getDireita();

        x.setDireita(y);
        y.setEsquerda(T2);

        atualizarAltura(y);
        atualizarAltura(x);

        this.totalRotacoes++;
        this.rotacoesUltimaInsercao++;

        return x;
    }

    private NoAVL rotacaoEsquerda(NoAVL x) {
        NoAVL y = x.getDireita();
        NoAVL T2 = y.getEsquerda();

        y.setEsquerda(x);
        x.setDireita(T2);

        atualizarAltura(x);
        atualizarAltura(y);

        this.totalRotacoes++;
        this.rotacoesUltimaInsercao++;

        return y;
    }

    public List<Resultado> obterAcimaDe(double limiar) {
        List<Resultado> lista = new ArrayList<>();
        percorrerDecrescente(this.raiz, limiar, -1, lista);
        return lista;
    }

    public List<Resultado> obterTopK(int k) {
        List<Resultado> lista = new ArrayList<>();
        percorrerDecrescente(this.raiz, 0.0, k, lista);
        return lista;
    }

    public List<Resultado> obterTodosDecrescente() {
        List<Resultado> lista = new ArrayList<>();
        percorrerDecrescente(this.raiz, 0.0, -1, lista);
        return lista;
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
        }

        if (no.getChave() + EPSILON >= limiar) {
            percorrerDecrescente(no.getEsquerda(), limiar, limiteK, acumulador);
        }
    }

    public int getTotalRotacoes() {
        return totalRotacoes;
    }

    public NoAVL getRaiz() {
        return raiz;
    }
}