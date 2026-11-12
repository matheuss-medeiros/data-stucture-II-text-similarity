import java.util.Locale;

public class Resultado {
    private final String documento1;
    private final String documento2;
    private final double similaridade;

    public Resultado(String documento1, String documento2, double similaridade) {
        this.documento1 = documento1;
        this.documento2 = documento2;
        this.similaridade = similaridade;
    }

    public String getDocumento1() {
        return documento1;
    }

    public String getDocumento2() {
        return documento2;
    }

    public double getSimilaridade() {
        return similaridade;
    }

    public boolean contemDocumento(String nomeDoc) {
        return documento1.equalsIgnoreCase(nomeDoc) || documento2.equalsIgnoreCase(nomeDoc);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s e %s = %.2f", documento1, documento2, similaridade);
    }
}