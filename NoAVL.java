import java.util.ArrayList;
import java.util.List;

public class NoAVL {
    private double chave;
    private List<Resultado> resultados;
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

    public double getChave() {
        return chave;
    }

    public List<Resultado> getResultados() {
        return resultados;
    }

    public void adicionarResultado(Resultado resultado) {
        this.resultados.add(resultado);
    }

    public NoAVL getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(NoAVL esquerda) {
        this.esquerda = esquerda;
    }

    public NoAVL getDireita() {
        return direita;
    }

    public void setDireita(NoAVL direita) {
        this.direita = direita;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }
}